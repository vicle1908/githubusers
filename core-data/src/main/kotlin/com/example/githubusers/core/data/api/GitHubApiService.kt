package com.example.githubusers.core.data.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * GitHub API service using pure Ktor client.
 * No Retrofit needed - everything is done with Ktor and Kotlin.
 */
@Singleton
class GitHubApiService @Inject constructor(
    private val client: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://api.github.com"
    }
    
    /**
     * Get list of GitHub users
     */
    suspend fun getUsers(since: Int = 0, perPage: Int = 30): List<GitHubUser> {
        return client.get("$BASE_URL/users") {
            parameter("since", since)
            parameter("per_page", perPage)
        }.body()
    }
    
    /**
     * Get user details by username
     */
    suspend fun getUserDetails(username: String): GitHubUserDetail {
        return client.get("$BASE_URL/users/$username").body()
    }
    
    /**
     * Search users by query
     */
    suspend fun searchUsers(query: String, page: Int = 1, perPage: Int = 30): SearchResult {
        return client.get("$BASE_URL/search/users") {
            parameter("q", query)
            parameter("page", page)
            parameter("per_page", perPage)
        }.body()
    }
    
    /**
     * Get user's repositories
     */
    suspend fun getUserRepos(username: String, page: Int = 1, perPage: Int = 30): List<Repository> {
        return client.get("$BASE_URL/users/$username/repos") {
            parameter("page", page)
            parameter("per_page", perPage)
            parameter("sort", "updated")
        }.body()
    }
    
    /**
     * Get user's followers
     */
    suspend fun getUserFollowers(username: String, page: Int = 1, perPage: Int = 30): List<GitHubUser> {
        return client.get("$BASE_URL/users/$username/followers") {
            parameter("page", page)
            parameter("per_page", perPage)
        }.body()
    }
    
    /**
     * Get user's following
     */
    suspend fun getUserFollowing(username: String, page: Int = 1, perPage: Int = 30): List<GitHubUser> {
        return client.get("$BASE_URL/users/$username/following") {
            parameter("page", page)
            parameter("per_page", perPage)
        }.body()
    }
}

// Data models using Kotlin Serialization
@Serializable
data class GitHubUser(
    val id: Long,
    val login: String,
    val avatar_url: String,
    val html_url: String,
    val type: String
)

@Serializable
data class GitHubUserDetail(
    val id: Long,
    val login: String,
    val avatar_url: String,
    val html_url: String,
    val name: String? = null,
    val company: String? = null,
    val blog: String? = null,
    val location: String? = null,
    val email: String? = null,
    val bio: String? = null,
    val public_repos: Int = 0,
    val public_gists: Int = 0,
    val followers: Int = 0,
    val following: Int = 0,
    val created_at: String,
    val updated_at: String
)

@Serializable
data class SearchResult(
    val total_count: Int,
    val incomplete_results: Boolean,
    val items: List<GitHubUser>
)

@Serializable
data class Repository(
    val id: Long,
    val name: String,
    val full_name: String,
    val owner: GitHubUser,
    val private: Boolean,
    val html_url: String,
    val description: String? = null,
    val fork: Boolean,
    val created_at: String,
    val updated_at: String,
    val pushed_at: String? = null,
    val homepage: String? = null,
    val size: Int,
    val stargazers_count: Int,
    val watchers_count: Int,
    val language: String? = null,
    val forks_count: Int,
    val open_issues_count: Int,
    val default_branch: String
)
