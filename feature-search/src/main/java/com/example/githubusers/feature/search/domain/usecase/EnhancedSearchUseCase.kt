package com.example.githubusers.feature.search.domain.usecase

import androidx.paging.PagingData
import com.example.githubusers.feature.search.domain.entity.SearchFilter
import com.example.githubusers.feature.search.domain.entity.SearchResult
import com.example.githubusers.feature.search.domain.repository.SearchRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Enhanced use case for searching users with advanced filters and improved error handling
 */
class EnhancedSearchUseCase
@Inject
constructor(private val repository: SearchRepository) {
    operator fun invoke(query: String, filter: SearchFilter = SearchFilter()): Flow<PagingData<SearchResult>> =
        repository.searchUsers(query, filter)
}

/**
 * Enhanced use case for managing search history with better error handling
 */
class EnhancedManageSearchHistoryUseCase
@Inject
constructor(private val repository: SearchRepository) {
    suspend fun getRecentSearches(): List<String> = try {
        repository.getRecentSearches()
    } catch (e: Exception) {
        // Return empty list on error to prevent app crashes
        emptyList()
    }

    suspend fun saveSearch(query: String) {
        try {
            // Only save non-empty queries
            if (query.isNotBlank()) {
                repository.saveSearchQuery(query)
            }
        } catch (e: Exception) {
            // Silently fail to prevent app crashes
            // In a production app, you might want to log this
        }
    }

    suspend fun clearHistory() {
        try {
            repository.clearSearchHistory()
        } catch (e: Exception) {
            // Silently fail to prevent app crashes
            // In a production app, you might want to log this
        }
    }
}

/**
 * Enhanced use case for getting trending users with improved error handling
 */
class EnhancedGetTrendingUsersUseCase
@Inject
constructor(private val repository: SearchRepository) {
    operator fun invoke(): Flow<PagingData<SearchResult>> = try {
        repository.getTrendingUsers()
    } catch (e: Exception) {
        // Return empty flow on error
        kotlinx.coroutines.flow.flowOf(androidx.paging.PagingData.empty())
    }
}
