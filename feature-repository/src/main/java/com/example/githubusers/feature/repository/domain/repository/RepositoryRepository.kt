package com.example.githubusers.feature.repository.domain.repository

import androidx.paging.PagingData
import com.example.githubusers.core.search.SearchQueryNormalizer
import com.example.githubusers.feature.repository.domain.model.Repository
import com.example.githubusers.feature.repository.domain.model.RepositoryDetail
import kotlinx.coroutines.flow.Flow

/**
 * Domain-facing repository exposing repository search results.
 */
interface RepositoryRepository {
    fun observeRepositories(query: SearchQueryNormalizer.NormalizedSearchQuery): Flow<PagingData<Repository>>

    suspend fun getRepository(owner: String, name: String): Result<RepositoryDetail?>
}
