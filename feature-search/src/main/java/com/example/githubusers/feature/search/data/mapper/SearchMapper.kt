package com.example.githubusers.feature.search.data.mapper

import com.example.githubusers.core.search.domain.SearchResult
import com.example.githubusers.core.search.domain.SearchResultType
import com.example.githubusers.core.users.domain.UserSummary
import com.example.githubusers.feature.search.data.model.GitHubUser
import com.example.githubusers.feature.search.data.model.GitHubUserDetail

/**
 * Extension function to map GitHubUser to SearchResult
 */
fun GitHubUser.toSearchResult(): SearchResult = SearchResult(
    id = this.id,
    login = this.login,
    name = null, // Basic user doesn't have name
    avatarUrl = this.avatarUrl,
    type =
    when (this.type.lowercase()) {
        "user" -> SearchResultType.USER
        "organization" -> SearchResultType.ORGANIZATION
        else -> SearchResultType.USER
    },
    score = 0f, // Default score for non-search results
    bio = null,
    location = null,
    company = null,
    publicRepos = null,
    followers = null
)

/**
 * Extension function to map GitHubUserDetail to SearchResult
 */
fun GitHubUserDetail.toSearchResult(): SearchResult = SearchResult(
    id = this.id,
    login = this.login,
    name = this.name,
    avatarUrl = this.avatarUrl,
    type = SearchResultType.USER, // Detailed users are always USER type
    score = 0f, // Default score
    bio = this.bio,
    location = this.location,
    company = this.company,
    publicRepos = this.publicRepos,
    followers = this.followers
)

/**
 * Extension function to map search API result with score
 */
fun GitHubUser.toSearchResult(score: Float): SearchResult = this.toSearchResult().copy(score = score)

/**
 * Map search result to the shared user summary used by feature-users UI.
 */
fun SearchResult.toUserSummary(): UserSummary = UserSummary(
    id = id,
    login = login,
    avatarUrl = avatarUrl,
    htmlUrl = "https://github.com/$login",
    type =
    when (type) {
        SearchResultType.USER -> "User"
        SearchResultType.ORGANIZATION -> "Organization"
    }
)
