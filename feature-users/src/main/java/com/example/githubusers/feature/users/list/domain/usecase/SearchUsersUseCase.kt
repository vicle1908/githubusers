package com.example.githubusers.feature.users.list.domain.usecase

import androidx.paging.PagingData
import com.example.githubusers.feature.users.list.domain.entity.UserSummary
import com.example.githubusers.feature.users.list.domain.repository.UserListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for searching users by query.
 */
class SearchUsersUseCase
    @Inject
    constructor(
        private val repository: UserListRepository,
    ) {
        /**
         * Execute the use case to search users.
         *
         * @param query The search query string
         * @return Flow of PagingData containing UserSummary items matching the query
         */
        operator fun invoke(query: String): Flow<PagingData<UserSummary>> = repository.searchUsers(query)
    }
