package com.example.githubusers.feature.search.domain.entity

/**
 * Represents a search result item
 */
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

enum class SearchResultType {
    USER,
    ORGANIZATION
}

/**
 * Search filter options
 */
data class SearchFilter(
    val type: SearchResultType? = null,
    val location: String? = null,
    val language: String? = null,
    val minRepos: Int? = null,
    val minFollowers: Int? = null,
    val sortBy: SearchSortOption = SearchSortOption.BEST_MATCH
)

enum class SearchSortOption {
    BEST_MATCH,
    FOLLOWERS,
    REPOSITORIES,
    JOINED
}
