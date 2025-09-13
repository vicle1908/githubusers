package com.example.githubusers.feature.users.list.data.remote

import timber.log.Timber
import com.example.githubusers.core.ui.performance.PerformanceMonitor
import com.example.githubusers.feature.users.list.data.remote.dto.SearchResponseDto
import com.example.githubusers.feature.users.list.data.remote.dto.UserSummaryDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import javax.inject.Inject

/**
 * API service for fetching user list data from GitHub.
 */
class UserListApiService
    @Inject
    constructor(
        private val client: HttpClient,
        private val performanceMonitor: PerformanceMonitor,
    ) {
        companion object {
            private const val TAG = "UserListApiService"
            private const val BASE_URL = "https://api.github.com"
            private const val USERS_ENDPOINT = "$BASE_URL/users"
            private const val SEARCH_ENDPOINT = "$BASE_URL/search/users"
        }

        /**
         * Get all users with pagination.
         * @param since The ID of the user to start from (exclusive)
         * @param perPage Number of users per page
         * @return Result containing list of users or error
         */
        suspend fun getUsers(
            since: Int? = null,
            perPage: Int = 30,
        ): Result<List<UserSummaryDto>> =
            try {
                Timber.tag(TAG).d("Fetching users: since=$since, perPage=$perPage")
                Timber.tag(TAG).d("Making request to: $USERS_ENDPOINT")

                val startTime = System.nanoTime()
                val response =
                    client.get(USERS_ENDPOINT) {
                        since?.let { parameter("since", it) }
                        parameter("per_page", perPage)

                        // Add caching headers for better performance
                        header(HttpHeaders.CacheControl, "max-age=300") // Cache for 5 minutes
                        header(HttpHeaders.IfNoneMatch, "*") // Enable ETag support
                        header(HttpHeaders.Accept, "application/vnd.github.v3+json")
                        header(HttpHeaders.UserAgent, "GitHubUsers-Android/1.0")
                    }
                val duration = System.nanoTime() - startTime

                Timber.tag(TAG).d("Response status: ${response.status}")
                Timber.tag(TAG).d("Response headers: ${response.headers}")

                val success = response.status.isSuccess()
                val cacheHit =
                    response.headers["X-Cache"]?.contains("HIT") == true ||
                        response.headers["ETag"] != null

                performanceMonitor.trackNetworkRequest(
                    endpoint = "/users",
                    method = "GET",
                    duration = duration,
                    success = success,
                    cacheHit = cacheHit,
                )

                if (success) {
                    val users = response.body<List<UserSummaryDto>>()
                    Timber.tag(TAG).d("Fetched ${users.size} users successfully")
                    Timber.tag(TAG).d("First user: ${users.firstOrNull()}")
                    Result.success(users)
                } else {
                    Timber.tag(TAG).e("Error fetching users: HTTP ${response.status}")
                    Timber.tag(TAG).e("Response body: ${response.body<String>()}")
                    Result.failure(Exception("Failed to fetch users, status code: ${response.status}"))
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Exception fetching users")
                Timber.tag(TAG).e("Exception type: ${e::class.java.simpleName}")
                Timber.tag(TAG).e("Exception message: ${e.message}")
                Result.failure(e)
            }

        /**
         * Search users with query.
         * @param query Search query
         * @param page Page number (1-based)
         * @param perPage Number of results per page
         * @return Result containing search response or error
         */
        suspend fun searchUsers(
            query: String,
            page: Int = 1,
            perPage: Int = 30,
        ): Result<SearchResponseDto> =
            try {
                Timber.tag(TAG).d("Searching users: query=$query, page=$page, perPage=$perPage")

                val startTime = System.nanoTime()
                val response =
                    client.get(SEARCH_ENDPOINT) {
                        parameter("q", query)
                        parameter("page", page)
                        parameter("per_page", perPage)
                        parameter("sort", "repositories") // Sort by repository count for better results
                        parameter("order", "desc")

                        // Add performance headers
                        header(HttpHeaders.CacheControl, "max-age=120") // Cache searches for 2 minutes
                        header(HttpHeaders.Accept, "application/vnd.github.v3+json")
                        header(HttpHeaders.UserAgent, "GitHubUsers-Android/1.0")
                    }
                val duration = System.nanoTime() - startTime

                val success = response.status.isSuccess()
                val cacheHit =
                    response.headers["X-Cache"]?.contains("HIT") == true ||
                        response.headers["ETag"] != null

                performanceMonitor.trackNetworkRequest(
                    endpoint = "/search/users",
                    method = "GET",
                    duration = duration,
                    success = success,
                    cacheHit = cacheHit,
                )

                if (success) {
                    val searchResponse = response.body<SearchResponseDto>()
                    Timber.tag(TAG).d("Search returned ${searchResponse.items.size} users out of ${searchResponse.totalCount} total")
                    Result.success(searchResponse)
                } else {
                    Timber.tag(TAG).e("Error searching users: HTTP ${response.status}")
                    Result.failure(Exception("Failed to search users, status code: ${response.status}"))
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Exception searching users")
                Result.failure(e)
            }
    }
