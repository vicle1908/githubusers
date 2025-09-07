package com.example.githubusers.core.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * GitHub API service for making HTTP requests to GitHub's REST API
 */
@Singleton
class GitHubApiService @Inject constructor(
    private val httpClient: HttpClient
) {
    /**
     * Search for users on GitHub
     */
    suspend fun searchUsers(
        query: String,
        page: Int = 1,
        perPage: Int = 30
    ): String {
        return httpClient.get("https://api.github.com/search/users") {
            parameter("q", query)
            parameter("page", page)
            parameter("per_page", perPage)
        }.body()
    }

    /**
     * Get user details by username
     */
    suspend fun getUser(username: String): String {
        return httpClient.get("https://api.github.com/users/$username").body()
    }

    /**
     * Get user repositories
     */
    suspend fun getUserRepositories(
        username: String,
        page: Int = 1,
        perPage: Int = 30
    ): String {
        return httpClient.get("https://api.github.com/users/$username/repos") {
            parameter("page", page)
            parameter("per_page", perPage)
        }.body()
    }
}
