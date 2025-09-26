package com.example.githubusers.feature.users.domain.detail.usecase

import com.example.githubusers.feature.users.domain.model.UserDetail
import com.example.githubusers.feature.users.domain.repository.UserRepository
import dagger.Reusable
import javax.inject.Inject

/**
 * Use case for fetching detailed user information.
 */
@Reusable
class GetUserDetailUseCase @Inject constructor(private val repository: UserRepository) {
    /**
     * Execute the use case to get user details.
     *
     * @param username The username to fetch details for
     * @return Result containing UserDetail or error
     */
    suspend operator fun invoke(username: String): Result<UserDetail> = repository.fetchUserDetail(username)
}
