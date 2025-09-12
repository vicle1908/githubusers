package com.example.githubusers.feature.search.data.mapper

import com.example.githubusers.feature.search.data.model.GitHubUser
import com.example.githubusers.feature.search.data.model.GitHubUserDetail
import com.example.githubusers.feature.search.domain.entity.SearchResult
import com.example.githubusers.feature.search.domain.entity.SearchResultType

/**
 * Extension function to map GitHubUser to SearchResult
 */
fun GitHubUser.toSearchResult(): SearchResult = SearchResult(
    id = this.id,
    login = this.login,
    name = null, // Basic user doesn't have name
    avatarUrl = this.avatar_url,
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
    avatarUrl = this.avatar_url,
    type = SearchResultType.USER, // Detailed users are always USER type
    score = 0f, // Default score
    bio = this.bio,
    location = this.location,
    company = this.company,
    publicRepos = this.public_repos,
    followers = this.followers
)

/**
 * Extension function to map search API result with score
 */
fun GitHubUser.toSearchResult(score: Float): SearchResult = this.toSearchResult().copy(score = score)
