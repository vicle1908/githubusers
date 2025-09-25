package com.example.githubusers.feature.search.data.mapper

import com.example.githubusers.core.search.domain.SearchResult
import com.example.githubusers.core.search.domain.SearchResultType
import com.example.githubusers.core.users.domain.UserSummary
import com.example.githubusers.feature.repository.domain.model.Repository
import com.example.githubusers.feature.search.data.model.GitHubRepository
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

fun GitHubRepository.toSearchResult(): SearchResult = SearchResult(
    id = this.id,
    login = this.owner.login,
    name = this.name,
    avatarUrl = this.owner.avatarUrl,
    type = SearchResultType.REPOSITORY,
    score = this.score,
    bio = null,
    location = null,
    company = null,
    publicRepos = null,
    followers = null,
    repositoryFullName = this.fullName,
    repositoryOwnerLogin = this.owner.login,
    repositoryDescription = this.description,
    repositoryHtmlUrl = this.htmlUrl,
    stargazersCount = this.stargazersCount,
    primaryLanguage = this.primaryLanguage,
    forksCount = this.forksCount,
    openIssuesCount = this.openIssuesCount
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
    avatarUrl = avatarUrl ?: "",
    htmlUrl = "https://github.com/$login",
    type =
    when (type) {
        SearchResultType.USER -> "User"
        SearchResultType.ORGANIZATION -> "Organization"
        SearchResultType.REPOSITORY -> "Repository"
    }
)

fun SearchResult.toRepositoryModel(): Repository = Repository(
    id = id,
    name = name ?: repositoryFullName?.substringAfter('/') ?: login,
    fullName = repositoryFullName ?: name ?: login,
    ownerLogin = repositoryOwnerLogin ?: login,
    description = repositoryDescription,
    htmlUrl = repositoryHtmlUrl ?: "https://github.com/${repositoryFullName ?: login}",
    stargazersCount = stargazersCount ?: 0,
    watchersCount = stargazersCount ?: 0,
    language = primaryLanguage,
    forksCount = forksCount ?: 0,
    openIssuesCount = openIssuesCount ?: 0,
    licenseName = null
)
