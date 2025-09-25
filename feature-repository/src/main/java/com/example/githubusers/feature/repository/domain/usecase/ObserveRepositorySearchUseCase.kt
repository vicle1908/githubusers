package com.example.githubusers.feature.repository.domain.usecase

import androidx.paging.PagingData
import com.example.githubusers.core.search.SearchQueryNormalizer
import com.example.githubusers.feature.repository.domain.model.Repository
import com.example.githubusers.feature.repository.domain.repository.RepositoryRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveRepositorySearchUseCase @Inject constructor(private val repository: RepositoryRepository) {
    operator fun invoke(query: SearchQueryNormalizer.NormalizedSearchQuery): Flow<PagingData<Repository>> =
        repository.observeRepositories(query)
}
