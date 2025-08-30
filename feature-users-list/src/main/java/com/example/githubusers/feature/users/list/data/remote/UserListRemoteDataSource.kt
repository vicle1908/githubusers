package com.example.githubusers.feature.users.list.data.remote

import com.example.githubusers.feature.users.list.data.remote.dto.SearchResponse
import com.example.githubusers.feature.users.list.data.remote.dto.UserDto

/**
 * Remote data source interface for user list operations.
 */
interface UserListRemoteDataSource {
    /**
     * Fetch users from GitHub API.
     *
     * @param since The ID of the last user seen (for pagination)
     * @param perPage Number of users per page
     * @return List of UserDto from the API
     */
    suspend fun getUsers(
        since: Int,
        perPage: Int,
    ): List<UserDto>

    /**
     * Search users from GitHub API.
     *
     * @param query Search query
     * @param page Page number
     * @param perPage Number of results per page
     * @return SearchResponse containing users and metadata
     */
    suspend fun searchUsers(
        query: String,
        page: Int,
        perPage: Int,
    ): SearchResponse
}
