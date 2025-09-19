package com.example.githubusers.feature.search.domain.usecase

import androidx.paging.PagingData
import com.example.githubusers.core.search.domain.SearchFilter
import com.example.githubusers.core.search.domain.SearchRepository
import com.example.githubusers.core.search.domain.SearchResult
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Enhanced use case for searching users with advanced filters.
 */
class EnhancedSearchUseCase
@Inject
constructor(private val repository: SearchRepository) {
    operator fun invoke(query: String, filter: SearchFilter = SearchFilter()): Flow<PagingData<SearchResult>> =
        repository.searchUsers(query, filter)
}

/**
 * Enhanced use case for managing search history without silent swallowing.
 */
class EnhancedManageSearchHistoryUseCase
@Inject
constructor(private val repository: SearchRepository) {
    suspend fun getRecentSearches(): List<String> = repository.getRecentSearches()

    suspend fun saveSearch(query: String) {
        if (query.isNotBlank()) {
            repository.saveSearchQuery(query)
        }
    }

    suspend fun clearHistory() {
        repository.clearSearchHistory()
    }
}

/**
 * Enhanced use case for getting trending users without silent swallowing.
 */
class EnhancedGetTrendingUsersUseCase
@Inject
constructor(private val repository: SearchRepository) {
    operator fun invoke(): Flow<PagingData<SearchResult>> = repository.getTrendingUsers()
}
