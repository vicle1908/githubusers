package com.example.githubusers.feature.users.domain.list.usecase

import androidx.paging.PagingData
import com.example.githubusers.core.search.SearchQueryNormalizer
import com.example.githubusers.core.users.domain.UserSummary
import com.example.githubusers.feature.users.domain.repository.UserRepository
import dagger.Reusable
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing the paginated user list.
 */
@Reusable
class ObserveUserListUseCase @Inject constructor(private val repository: UserRepository) {
    operator fun invoke(query: SearchQueryNormalizer.NormalizedSearchQuery): Flow<PagingData<UserSummary>> =
        repository.observeUsers(query)
}
