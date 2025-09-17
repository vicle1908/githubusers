package com.example.githubusers.feature.users.detail.domain.repository

import androidx.paging.PagingData
import com.example.githubusers.feature.users.detail.domain.entity.Repository
import com.example.githubusers.feature.users.detail.domain.entity.UserDetail
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user detail operations.
 */
interface UserDetailRepository {
    /**
     * Get detailed user information.
     *
     * @param username The username to fetch details for
     * @return Flow of Result containing UserDetail or error
     */
    suspend fun getUserDetail(username: String): Result<UserDetail>

    /**
     * Get user's repositories with pagination.
     *
     * @param username The username whose repositories to fetch
     * @param sort Sort criteria (created, updated, pushed, full_name)
     * @param perPage Number of items per page
     * @return Flow of PagingData containing Repository items
     */
    fun getUserRepositories(
        username: String,
        sort: RepositorySort = RepositorySort.UPDATED,
        perPage: Int = 30
    ): Flow<PagingData<Repository>>

    /**
     * Clear cached user detail data.
     *
     * @param username The username whose data to clear
     */
    suspend fun clearUserDetailCache(username: String)

    /**
     * Check if user is followed by the authenticated user.
     *
     * @param username The username to check
     * @return True if following, false otherwise
     */
    suspend fun isFollowing(username: String): Boolean

    /**
     * Follow a user.
     *
     * @param username The username to follow
     * @return Result indicating success or failure
     */
    suspend fun followUser(username: String): Result<Unit>

    /**
     * Unfollow a user.
     *
     * @param username The username to unfollow
     * @return Result indicating success or failure
     */
    suspend fun unfollowUser(username: String): Result<Unit>
}

/**
 * Repository sort options.
 */
enum class RepositorySort(val value: String) {
    CREATED("created"),
    UPDATED("updated"),
    PUSHED("pushed"),
    FULL_NAME("full_name"),
    STARS("stars")
}
