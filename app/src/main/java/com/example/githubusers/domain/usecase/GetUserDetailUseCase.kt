package com.example.githubusers.domain.usecase

import com.example.githubusers.domain.entity.UserDetail
import com.example.githubusers.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case for fetching user details.
 */
class GetUserDetailUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    /**
     * Executes the use case to fetch a user's detail.
     * @param username The GitHub username for which the details are fetched.
     * @return Flow emitting Result<UserDetail?>, wrapping either success or failure.
     */
    operator fun invoke(username: String): Flow<Result<UserDetail?>> {
        return userRepository.getUserDetail(username)
    }
}
