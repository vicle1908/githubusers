package com.example.githubusers.feature.users.domain.list.repository

import androidx.paging.PagingData
import com.example.githubusers.core.search.SearchQueryNormalizer
import com.example.githubusers.core.users.domain.UserSummary
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user list operations.
 */
interface UserListRepository {
    /**
     * Observe the paginated list of users for the supplied search query.
     */
    fun getUsersPaged(query: SearchQueryNormalizer.NormalizedSearchQuery): Flow<PagingData<UserSummary>>

    /**
     * Convenience overload for the default browse experience.
     */
    fun getUsersPaged(): Flow<PagingData<UserSummary>> = getUsersPaged(SearchQueryNormalizer.normalize(""))

    /**
     * Clear cached users data.
     */
    suspend fun clearCache()
}
