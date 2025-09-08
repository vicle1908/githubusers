package com.example.githubusers.feature.users.domain.repository

import androidx.paging.PagingData
import com.example.githubusers.feature.users.domain.entity.User
import com.example.githubusers.feature.users.domain.entity.UserDetail
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user-related operations
 * Owned by feature-users module per feature-based architecture
 */
interface UserRepository {
    /**
     * Get users with paging support
     */
    fun getUsersPaged(query: String): Flow<PagingData<User>>

    /**
     * Get user details by username
     */
    suspend fun getUserDetail(username: String): Result<UserDetail?>

    /**
     * Get user by ID
     */
    suspend fun getUserById(id: Long): Result<User>

    /**
     * Get user repositories
     */
    suspend fun getUserRepositories(username: String): Result<List<com.example.githubusers.feature.users.domain.entity.Repository>>

    /**
     * Search users
     */
    suspend fun searchUsers(query: String): Result<List<User>>

    /**
     * Get cached user details
     */
    fun getCachedUserDetail(username: String): Flow<UserDetail?>

    /**
     * Clear user cache
     */
    suspend fun clearUserCache()
}
