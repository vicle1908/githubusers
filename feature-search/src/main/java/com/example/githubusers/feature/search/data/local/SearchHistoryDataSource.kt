package com.example.githubusers.feature.search.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * DataStore extension for search history
 */
private val Context.searchDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "search_history"
)

/**
 * Local data source for managing search history (v2 JSON with migration from v1 set)
 */
@Singleton
class SearchHistoryDataSource
@Inject
constructor(@param:ApplicationContext private val context: Context) {
    companion object {
        // v1 legacy (unordered stringSet)
        private val SEARCH_HISTORY_V1_SET = stringSetPreferencesKey("search_history")

        // v2 versioned JSON wrapper
        private val SEARCH_HISTORY_V2_JSON = stringPreferencesKey("search_history_json_v2")
        private val MIGRATION_DONE_V2 = booleanPreferencesKey("search_history_migration_done_v2")

        private const val MAX_HISTORY_SIZE = 10
        private const val V2_VERSION = 2
    }

    private val dataStore = context.searchDataStore

    private val json by lazy {
        Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
        }
    }

    @Serializable
    private data class RecentSearchEntry(val query: String, val updatedAt: Long)

    @Serializable
    private data class RecentSearchesV2(
        val version: Int = V2_VERSION,
        @SerialName("entries") val entries: List<RecentSearchEntry> = emptyList()
    )

    /**
     * Get recent search queries (most-recent first), migrating from v1 if needed.
     */
    suspend fun getRecentSearches(): List<String> = withContext(Dispatchers.IO) {
        val prefs = dataStore.data.first()

        // Try v2
        prefs[SEARCH_HISTORY_V2_JSON]?.let { raw ->
            parseV2(raw)?.let { return@withContext it.entries.map { e -> e.query } }
        }

        // No v2 yet → attempt migration from v1 set
        val v1Set: Set<String>? = prefs[SEARCH_HISTORY_V1_SET]
        if (!v1Set.isNullOrEmpty()) {
            migrateFromV1(v1Set)
            val after = dataStore.data.first()[SEARCH_HISTORY_V2_JSON]
            val parsed = after?.let { parseV2(it) }
            return@withContext parsed?.entries?.map { e -> e.query } ?: emptyList()
        }

        // Nothing present
        emptyList()
    }

    /**
     * Save a search query using v2 JSON (move-to-front, de-dup, cap MAX_HISTORY_SIZE).
     */
    suspend fun saveSearchQuery(query: String) = withContext(Dispatchers.IO) {
        val normalized = query.trim()
        if (normalized.isEmpty()) return@withContext

        dataStore.edit { preferences ->
            val now = System.currentTimeMillis()
            val current = preferences[SEARCH_HISTORY_V2_JSON]
            val currentV2 = current?.let { parseV2(it) } ?: RecentSearchesV2()

            val entries = currentV2.entries.toMutableList()

            // Remove existing (case-insensitive) while preserving original casing of new insert
            val existingIndex = entries.indexOfFirst { it.query.equals(normalized, ignoreCase = true) }
            if (existingIndex >= 0) entries.removeAt(existingIndex)

            entries.add(0, RecentSearchEntry(query = normalized, updatedAt = now))

            val capped = if (entries.size > MAX_HISTORY_SIZE) entries.take(MAX_HISTORY_SIZE) else entries
            val toWrite = RecentSearchesV2(version = V2_VERSION, entries = capped)
            preferences[SEARCH_HISTORY_V2_JSON] = json.encodeToString(toWrite)
            // We keep v1 key around for one release; we do not write it anymore.
        }
    }

    /**
     * Clear all search history (v2 and legacy v1).
     */
    suspend fun clearSearchHistory() = withContext(Dispatchers.IO) {
        dataStore.edit { preferences ->
            preferences.remove(SEARCH_HISTORY_V2_JSON)
            preferences.remove(SEARCH_HISTORY_V1_SET)
            preferences.remove(MIGRATION_DONE_V2)
        }
    }

    private fun parseV2(raw: String): RecentSearchesV2? = runCatching {
        json.decodeFromString(RecentSearchesV2.serializer(), raw)
    }.getOrNull()

    private suspend fun migrateFromV1(v1Set: Set<String>) {
        dataStore.edit { preferences ->
            // If migration already done and v2 exists, skip
            val already = preferences[MIGRATION_DONE_V2] == true && preferences[SEARCH_HISTORY_V2_JSON] != null
            if (already) return@edit

            val now = System.currentTimeMillis()
            val deterministic = v1Set.map {
                it.trim()
            }.filter { it.isNotEmpty() }.distinctBy { it.lowercase() }.sortedBy { it.lowercase() }
            val capped = if (deterministic.size >
                MAX_HISTORY_SIZE
            ) {
                deterministic.take(MAX_HISTORY_SIZE)
            } else {
                deterministic
            }
            val migrated = RecentSearchesV2(
                version = V2_VERSION,
                entries = capped.map { RecentSearchEntry(query = it, updatedAt = now) }
            )
            preferences[SEARCH_HISTORY_V2_JSON] = json.encodeToString(migrated)
            preferences[MIGRATION_DONE_V2] = true
        }
    }
}
