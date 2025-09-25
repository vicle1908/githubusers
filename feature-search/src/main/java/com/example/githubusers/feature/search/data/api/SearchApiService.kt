package com.example.githubusers.feature.search.data.api

import com.example.githubusers.feature.search.data.model.GitHubRepositorySearchResponse
import com.example.githubusers.feature.search.data.model.GitHubSearchResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Search API service for making HTTP requests to GitHub's REST API
 * Owned by feature-search module per feature-based architecture
 */
@Singleton
class SearchApiService
@Inject
constructor(private val httpClient: HttpClient) {
    /**
     * Search for users on GitHub
     */
    suspend fun searchUsers(query: String, page: Int = 1, perPage: Int = 30): GitHubSearchResponse = httpClient
        .get("https://api.github.com/search/users") {
            // GitHub REST API requires a valid User-Agent and recommends explicit Accept header
            header("Accept", "application/vnd.github+json")
            header("User-Agent", "githubusers-android")
            parameter("q", query)
            parameter("page", page)
            parameter("per_page", perPage)
        }.body()

    suspend fun searchRepositories(
        query: String,
        sort: String? = null,
        order: String? = null,
        page: Int = 1,
        perPage: Int = 30
    ): GitHubRepositorySearchResponse = httpClient
        .get("https://api.github.com/search/repositories") {
            header("Accept", "application/vnd.github+json")
            header("User-Agent", "githubusers-android")
            parameter("q", query)
            parameter("page", page)
            parameter("per_page", perPage)
            sort?.let { parameter("sort", it) }
            order?.let { parameter("order", it) }
        }.body()
}
