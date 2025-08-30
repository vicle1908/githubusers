package com.example.githubusers.feature.users.list.domain.repository

import androidx.paging.PagingData
import com.example.githubusers.feature.users.list.domain.entity.UserSummary
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user list operations.
 * Defines the contract for data access in the user list feature.
 */
interface UserListRepository {
    /**
     * Get paginated users based on the query.
     * @param query Search query. Empty string returns all users.
     * @return Flow of paginated user summaries
     */
    fun getUsersPaged(query: String = ""): Flow<PagingData<UserSummary>>

    /**
     * Get paginated list of all users.
     * @param since The ID of the last user seen (for pagination)
     * @return Flow of paginated user summaries
     */
    fun getUsers(since: Int = 0): Flow<PagingData<UserSummary>>

    /**
     * Search users by query.
     * @param query The search query
     * @return Flow of paginated user summaries matching the query
     */
    fun searchUsers(query: String): Flow<PagingData<UserSummary>>

    /**
     * Clear cached users data.
     */
    suspend fun clearCache()
}
