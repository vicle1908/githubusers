package com.example.githubusers.domain.usecase

import androidx.paging.PagingData
import com.example.githubusers.domain.entity.User
import com.example.githubusers.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsersPagedUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<PagingData<User>> {
        return userRepository.getUsersPaged()
    }
}
