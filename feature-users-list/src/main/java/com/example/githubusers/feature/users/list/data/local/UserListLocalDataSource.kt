package com.example.githubusers.feature.users.list.data.local

import androidx.paging.PagingSource
import com.example.githubusers.feature.users.list.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Local data source interface for user list operations.
 */
interface UserListLocalDataSource {
    /**
     * Get paginated users from local database.
     */
    fun getUsersPagingSource(): PagingSource<Int, UserEntity>

    /**
     * Search users in local database.
     */
    fun searchUsersPagingSource(query: String): PagingSource<Int, UserEntity>

    /**
     * Insert or update users in database.
     */
    suspend fun insertUsers(users: List<UserEntity>)

    /**
     * Clear all users from database.
     */
    suspend fun clearAllUsers()

    /**
     * Get the last user ID for pagination.
     */
    suspend fun getLastUserId(): Long?

    /**
     * Check if we have cached users.
     */
    suspend fun hasUsers(): Boolean

    /**
     * Observe user count.
     */
    fun observeUserCount(): Flow<Int>
}
