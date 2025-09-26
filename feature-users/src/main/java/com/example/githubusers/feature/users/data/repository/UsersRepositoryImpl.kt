package com.example.githubusers.feature.users.data.repository

import androidx.paging.PagingData
import com.example.githubusers.core.search.SearchQueryNormalizer
import com.example.githubusers.core.users.domain.UserSummary
import com.example.githubusers.feature.users.domain.detail.repository.UserDetailRepository
import com.example.githubusers.feature.users.domain.model.UserDetail
import com.example.githubusers.feature.users.domain.repository.UserRepository
import com.example.githubusers.feature.users.domain.list.repository.UserListRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Aggregates list and detail repositories behind a single user-facing contract.
 */
class UsersRepositoryImpl @Inject constructor(
    private val userListRepository: UserListRepository,
    private val userDetailRepository: UserDetailRepository
) : UserRepository {

    override fun observeUsers(query: SearchQueryNormalizer.NormalizedSearchQuery): Flow<PagingData<UserSummary>> =
        userListRepository.getUsersPaged(query)

    override suspend fun fetchUserDetail(username: String): Result<UserDetail> =
        userDetailRepository.getUserDetail(username)
}
