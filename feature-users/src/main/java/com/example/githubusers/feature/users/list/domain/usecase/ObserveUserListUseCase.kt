package com.example.githubusers.feature.users.list.domain.usecase

import androidx.paging.PagingData
import com.example.githubusers.core.users.domain.UserSummary
import com.example.githubusers.feature.users.list.domain.repository.UserListRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing the paginated user list.
 */
class ObserveUserListUseCase @Inject constructor(private val repository: UserListRepository) {
    operator fun invoke(): Flow<PagingData<UserSummary>> = repository.getUsersPaged()
}
