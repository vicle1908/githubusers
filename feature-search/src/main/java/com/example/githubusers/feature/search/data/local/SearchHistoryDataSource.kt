package com.example.githubusers.feature.search.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataStore extension for search history
 */
private val Context.searchDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "search_history",
)

/**
 * Local data source for managing search history
 */
@Singleton
class SearchHistoryDataSource
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        companion object {
            private val SEARCH_HISTORY_KEY = stringSetPreferencesKey("search_history")
            private const val MAX_HISTORY_SIZE = 10
        }

        private val dataStore = context.searchDataStore

        /**
         * Get recent search queries
         */
        suspend fun getRecentSearches(): List<String> =
            dataStore.data
                .map { preferences ->
                    preferences[SEARCH_HISTORY_KEY]?.toList() ?: emptyList()
                }.first()

        /**
         * Save a search query to history
         */
        suspend fun saveSearchQuery(query: String) {
            dataStore.edit { preferences ->
                val currentHistory = preferences[SEARCH_HISTORY_KEY]?.toMutableSet() ?: mutableSetOf()

                // Remove if exists (to move to front)
                currentHistory.remove(query)

                // Convert to list to maintain order
                val historyList = currentHistory.toMutableList()

                // Add at the beginning
                historyList.add(0, query)

                // Limit size
                val limitedHistory = historyList.take(MAX_HISTORY_SIZE).toSet()

                preferences[SEARCH_HISTORY_KEY] = limitedHistory
            }
        }

        /**
         * Clear all search history
         */
        suspend fun clearSearchHistory() {
            dataStore.edit { preferences ->
                preferences.remove(SEARCH_HISTORY_KEY)
            }
        }
    }
