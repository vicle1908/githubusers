package com.example.githubusers.feature.users.detail.domain.usecase

import com.example.githubusers.feature.users.detail.domain.entity.UserDetail
import com.example.githubusers.feature.users.detail.domain.repository.UserDetailRepository
import javax.inject.Inject

/**
 * Use case for fetching detailed user information.
 */
class GetUserDetailUseCase
@Inject
constructor(private val repository: UserDetailRepository) {
    /**
     * Execute the use case to get user details.
     *
     * @param username The username to fetch details for
     * @return Result containing UserDetail or error
     */
    suspend operator fun invoke(username: String): Result<UserDetail> = repository.getUserDetail(username)
}
