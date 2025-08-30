package com.example.githubusers.navigation.impl

import android.content.Context
import android.content.SharedPreferences

/**
 * Minimal persistence abstraction for Navigation 3 back stack.
 * Stores a compact, versioned representation of deep links only.
 */
interface BackStackStore {
    fun save(payload: PersistedBackStack)

    fun load(): PersistedBackStack?

    fun clear()
}

/**
 * Versioned back stack payload. Only deep links are stored; arguments are implicit.
 */
data class PersistedBackStack(
    val schemaVersion: Int,
    val timestamp: Long,
    val appVersion: String?,
    val navGraphVersion: Int,
    val entries: List<String>,
)

/**
 * SharedPreferences-backed implementation.
 * Note: Use application context. Payload size is bounded by config in controller.
 */
class SharedPrefsBackStackStore(
    context: Context,
    name: String = DEFAULT_PREF_NAME,
) : BackStackStore {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(name, Context.MODE_PRIVATE)

    override fun save(payload: PersistedBackStack) {
        val joined = payload.entries.joinToString(SEPARATOR)
        prefs
            .edit()
            .putInt(KEY_SCHEMA, payload.schemaVersion)
            .putLong(KEY_TIMESTAMP, payload.timestamp)
            .putString(KEY_APP_VERSION, payload.appVersion)
            .putInt(KEY_NAV_VERSION, payload.navGraphVersion)
            .putString(KEY_ENTRIES, joined)
            .apply()
    }

    override fun load(): PersistedBackStack? {
        val schema = prefs.getInt(KEY_SCHEMA, -1)
        if (schema < 0) return null
        val ts = prefs.getLong(KEY_TIMESTAMP, -1L)
        if (ts <= 0L) return null
        val app = prefs.getString(KEY_APP_VERSION, null)
        val nav = prefs.getInt(KEY_NAV_VERSION, -1)
        if (nav < 0) return null
        val joined = prefs.getString(KEY_ENTRIES, null) ?: return null
        val entries = if (joined.isEmpty()) emptyList() else joined.split(SEPARATOR)
        return PersistedBackStack(
            schemaVersion = schema,
            timestamp = ts,
            appVersion = app,
            navGraphVersion = nav,
            entries = entries,
        )
    }

    override fun clear() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val DEFAULT_PREF_NAME = "nav3_back_stack"
        const val KEY_SCHEMA = "schema"
        const val KEY_TIMESTAMP = "ts"
        const val KEY_APP_VERSION = "app"
        const val KEY_NAV_VERSION = "nav"
        const val KEY_ENTRIES = "entries"
        const val SEPARATOR = "\u0001" // unlikely in URIs
    }
}

/**
 * Persistence configuration with guardrails.
 */
data class PersistenceConfig(
    val enabled: Boolean = false,
    val maxEntries: Int = 5,
    val maxTotalEntriesLength: Int = 20000, // ~20KB budget
    val ttlMillis: Long = 7L * 24L * 60L * 60L * 1000L, // 7 days
    val schemaVersion: Int = 1,
    val navGraphVersion: Int = 1,
    val appVersionProvider: () -> String? = { null },
)
