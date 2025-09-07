package com.example.githubusers.feature.users.list.domain.usecase

import androidx.paging.PagingData
import com.example.githubusers.feature.users.list.domain.entity.UserSummary
import com.example.githubusers.feature.users.list.domain.repository.UserListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for fetching paginated list of users.
 */
class GetUsersUseCase
    @Inject
    constructor(
        private val repository: UserListRepository,
    ) {
        /**
         * Execute the use case to get paginated users.
         *
         * @param since The ID of the user to start from (for pagination)
         * @return Flow of PagingData containing UserSummary items
         */
        operator fun invoke(since: Int = 0): Flow<PagingData<UserSummary>> = repository.getUsers(since)
    }
