package com.example.githubusers.feature.users.list.data.remote

import android.util.Log
import com.example.githubusers.feature.users.list.data.remote.dto.SearchResponseDto
import com.example.githubusers.feature.users.list.data.remote.dto.UserSummaryDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess
import javax.inject.Inject

/**
 * API service for fetching user list data from GitHub.
 */
class UserListApiService
    @Inject
    constructor(
        private val client: HttpClient,
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
                Log.d(TAG, "Fetching users: since=$since, perPage=$perPage")

                val response =
                    client.get(USERS_ENDPOINT) {
                        since?.let { parameter("since", it) }
                        parameter("per_page", perPage)
                    }

                if (response.status.isSuccess()) {
                    val users = response.body<List<UserSummaryDto>>()
                    Log.d(TAG, "Fetched ${users.size} users")
                    Result.success(users)
                } else {
                    Log.e(TAG, "Error fetching users: HTTP ${response.status}")
                    Result.failure(Exception("Failed to fetch users, status code: ${response.status}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception fetching users", e)
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
                Log.d(TAG, "Searching users: query=$query, page=$page, perPage=$perPage")

                val response =
                    client.get(SEARCH_ENDPOINT) {
                        parameter("q", query)
                        parameter("page", page)
                        parameter("per_page", perPage)
                    }

                if (response.status.isSuccess()) {
                    val searchResponse = response.body<SearchResponseDto>()
                    Log.d(TAG, "Search returned ${searchResponse.items.size} users out of ${searchResponse.totalCount} total")
                    Result.success(searchResponse)
                } else {
                    Log.e(TAG, "Error searching users: HTTP ${response.status}")
                    Result.failure(Exception("Failed to search users, status code: ${response.status}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception searching users", e)
                Result.failure(e)
            }
    }
