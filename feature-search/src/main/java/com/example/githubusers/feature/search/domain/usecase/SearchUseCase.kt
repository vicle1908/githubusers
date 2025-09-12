package com.example.githubusers.feature.search.domain.usecase

import androidx.paging.PagingData
import com.example.githubusers.feature.search.domain.entity.SearchFilter
import com.example.githubusers.feature.search.domain.entity.SearchResult
import com.example.githubusers.feature.search.domain.repository.SearchRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Use case for searching users with advanced filters
 */
class SearchUseCase
@Inject
constructor(private val repository: SearchRepository) {
    operator fun invoke(query: String, filter: SearchFilter = SearchFilter()): Flow<PagingData<SearchResult>> =
        repository.searchUsers(query, filter)
}

/**
 * Use case for managing search history
 */
class ManageSearchHistoryUseCase
@Inject
constructor(private val repository: SearchRepository) {
    suspend fun getRecentSearches(): List<String> = repository.getRecentSearches()

    suspend fun saveSearch(query: String) {
        repository.saveSearchQuery(query)
    }

    suspend fun clearHistory() {
        repository.clearSearchHistory()
    }
}

/**
 * Use case for getting trending users
 */
class GetTrendingUsersUseCase
@Inject
constructor(private val repository: SearchRepository) {
    operator fun invoke(): Flow<PagingData<SearchResult>> = repository.getTrendingUsers()
}
