package com.example.githubusers.feature.users.domain.detail.usecase

import com.example.githubusers.feature.users.domain.detail.repository.UserDetailRepository
import dagger.Reusable
import javax.inject.Inject

/**
 * Use case for following/unfollowing a user.
 */
@Reusable
class FollowUserUseCase @Inject constructor(private val repository: UserDetailRepository) {
    /**
     * Toggle follow status for a user.
     *
     * @param username The username to follow/unfollow
     * @param isFollowing Current following status
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(username: String, isFollowing: Boolean): Result<Unit> = if (isFollowing) {
        repository.unfollowUser(username)
    } else {
        repository.followUser(username)
    }

    /**
     * Check if user is being followed.
     *
     * @param username The username to check
     * @return True if following, false otherwise
     */
    suspend fun isFollowing(username: String): Boolean = repository.isFollowing(username)
}
