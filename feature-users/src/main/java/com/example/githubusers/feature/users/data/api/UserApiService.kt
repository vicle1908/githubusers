package com.example.githubusers.feature.users.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * User API service for making HTTP requests to GitHub's REST API
 * Owned by feature-users module per feature-based architecture
 */
@Singleton
class UserApiService
@Inject
constructor(private val httpClient: HttpClient) {
    /**
     * Get user details by username
     */
    suspend fun getUser(username: String): String = httpClient.get("https://api.github.com/users/$username").body()

    /**
     * Get user repositories
     */
    suspend fun getUserRepositories(username: String, page: Int = 1, perPage: Int = 30): String = httpClient
        .get("https://api.github.com/users/$username/repos") {
            parameter("page", page)
            parameter("per_page", perPage)
        }.body()
}
