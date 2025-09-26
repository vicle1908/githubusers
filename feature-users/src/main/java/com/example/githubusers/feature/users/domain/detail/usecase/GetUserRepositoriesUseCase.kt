package com.example.githubusers.feature.users.domain.detail.usecase

import androidx.paging.PagingData
import com.example.githubusers.feature.users.domain.detail.repository.RepositorySort
import com.example.githubusers.feature.users.domain.detail.repository.UserDetailRepository
import com.example.githubusers.feature.users.domain.model.Repository
import dagger.Reusable
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Use case for fetching user's repositories with pagination.
 */
@Reusable
class GetUserRepositoriesUseCase @Inject constructor(private val repository: UserDetailRepository) {
    /**
     * Execute the use case to get user's repositories.
     *
     * @param username The username whose repositories to fetch
     * @param sort Sort criteria for repositories
     * @return Flow of PagingData containing Repository items
     */
    operator fun invoke(username: String, sort: RepositorySort = RepositorySort.UPDATED): Flow<PagingData<Repository>> =
        repository.getUserRepositories(username, sort)
}
