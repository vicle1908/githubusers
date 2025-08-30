package com.example.githubusers.feature.search.domain.repository

import androidx.paging.PagingData
import com.example.githubusers.feature.search.domain.entity.SearchFilter
import com.example.githubusers.feature.search.domain.entity.SearchResult
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for search operations
 */
interface SearchRepository {
    /**
     * Search for users with advanced filters
     */
    fun searchUsers(
        query: String,
        filter: SearchFilter = SearchFilter(),
    ): Flow<PagingData<SearchResult>>

    /**
     * Get recent search queries
     */
    suspend fun getRecentSearches(): List<String>

    /**
     * Save a search query to history
     */
    suspend fun saveSearchQuery(query: String)

    /**
     * Clear search history
     */
    suspend fun clearSearchHistory()

    /**
     * Get trending users
     */
    fun getTrendingUsers(): Flow<PagingData<SearchResult>>
}
