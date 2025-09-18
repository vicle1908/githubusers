package com.example.githubusers.core.search.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

/**
 * Canonical domain models and contracts for search functionality.
 */

@Serializable
data class SearchResult(
    val id: Long,
    val login: String,
    val name: String?,
    val avatarUrl: String,
    val type: SearchResultType,
    val score: Float,
    val bio: String? = null,
    val location: String? = null,
    val company: String? = null,
    val publicRepos: Int? = null,
    val followers: Int? = null
)

@Serializable
enum class SearchResultType {
    USER,
    ORGANIZATION
}

@Serializable
data class SearchFilter(
    val type: SearchResultType? = null,
    val location: String? = null,
    val language: String? = null,
    val minRepos: Int? = null,
    val minFollowers: Int? = null,
    val sortBy: SearchSortOption = SearchSortOption.BEST_MATCH
)

@Serializable
enum class SearchSortOption {
    BEST_MATCH,
    FOLLOWERS,
    REPOSITORIES,
    JOINED
}

interface SearchRepository {
    fun searchUsers(query: String, filter: SearchFilter = SearchFilter()): Flow<PagingData<SearchResult>>

    suspend fun getRecentSearches(): List<String>

    suspend fun saveSearchQuery(query: String)

    suspend fun clearSearchHistory()

    fun getTrendingUsers(): Flow<PagingData<SearchResult>>
}
