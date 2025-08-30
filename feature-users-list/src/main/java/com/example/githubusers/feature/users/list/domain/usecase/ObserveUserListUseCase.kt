package com.example.githubusers.feature.users.list.domain.usecase

import androidx.paging.PagingData
import com.example.githubusers.feature.users.list.domain.entity.UserSummary
import com.example.githubusers.feature.users.list.domain.repository.UserListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing paginated user list.
 * Encapsulates the business logic for fetching and observing users.
 */
class ObserveUserListUseCase
    @Inject
    constructor(
        private val repository: UserListRepository,
    ) {
        /**
         * Execute the use case to get paginated users.
         * @param query Search query (empty for all users)
         * @return Flow of paginated user summaries
         */
        operator fun invoke(query: String = ""): Flow<PagingData<UserSummary>> = repository.getUsersPaged(query)
    }
