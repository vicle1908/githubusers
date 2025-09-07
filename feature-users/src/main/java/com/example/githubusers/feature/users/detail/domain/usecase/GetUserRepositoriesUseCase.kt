package com.example.githubusers.feature.users.detail.domain.usecase

import androidx.paging.PagingData
import com.example.githubusers.feature.users.detail.domain.entity.Repository
import com.example.githubusers.feature.users.detail.domain.repository.RepositorySort
import com.example.githubusers.feature.users.detail.domain.repository.UserDetailRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for fetching user's repositories with pagination.
 */
class GetUserRepositoriesUseCase
    @Inject
    constructor(
        private val repository: UserDetailRepository,
    ) {
        /**
         * Execute the use case to get user's repositories.
         *
         * @param username The username whose repositories to fetch
         * @param sort Sort criteria for repositories
         * @return Flow of PagingData containing Repository items
         */
        operator fun invoke(
            username: String,
            sort: RepositorySort = RepositorySort.UPDATED,
        ): Flow<PagingData<Repository>> = repository.getUserRepositories(username, sort)
    }
