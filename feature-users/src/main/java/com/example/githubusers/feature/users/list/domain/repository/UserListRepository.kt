package com.example.githubusers.feature.users.list.domain.repository

import androidx.paging.PagingData
import com.example.githubusers.core.users.domain.UserSummary
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user list operations.
 * Defines the contract for data access in the user list feature.
 */
interface UserListRepository {
    /**
     * Get the paginated list of users for browsing.
     */
    fun getUsersPaged(): Flow<PagingData<UserSummary>>

    /**
     * Clear cached users data.
     */
    suspend fun clearCache()
}
