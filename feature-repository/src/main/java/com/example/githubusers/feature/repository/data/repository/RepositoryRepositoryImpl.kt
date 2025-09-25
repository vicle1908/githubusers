package com.example.githubusers.feature.repository.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.githubusers.core.paging.PagingProfiles
import com.example.githubusers.core.search.SearchQueryNormalizer
import com.example.githubusers.feature.repository.data.RepositoryQueryParser
import com.example.githubusers.feature.repository.data.local.RepositoryDatabase
import com.example.githubusers.feature.repository.data.mapper.toDomain
import com.example.githubusers.feature.repository.data.paging.RepositoryRemoteMediator
import com.example.githubusers.feature.repository.data.remote.RepositoryApiService
import com.example.githubusers.feature.repository.domain.RepositorySearchDefaults
import com.example.githubusers.feature.repository.domain.model.Repository
import com.example.githubusers.feature.repository.domain.model.RepositoryDetail
import com.example.githubusers.feature.repository.domain.repository.RepositoryRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalPagingApi::class)
@Singleton
class RepositoryRepositoryImpl @Inject constructor(
    private val database: RepositoryDatabase,
    private val apiService: RepositoryApiService
) : RepositoryRepository {

    override fun observeRepositories(query: SearchQueryNormalizer.NormalizedSearchQuery): Flow<PagingData<Repository>> {
        val dao = database.repositoryDao()
        val parsedQuery = RepositoryQueryParser.parse(
            normalized = query,
            fallbackQuery = RepositorySearchDefaults.DEFAULT_QUERY
        )
        val pagingConfig: PagingConfig = PagingProfiles.defaultList()

        return Pager(
            config = pagingConfig,
            remoteMediator = RepositoryRemoteMediator(
                apiService = apiService,
                database = database,
                query = parsedQuery.networkQuery
            ),
            pagingSourceFactory = {
                dao.searchPagingSource(
                    language = parsedQuery.language,
                    plainQuery = parsedQuery.plainQuery
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomain() }
        }
    }

    override suspend fun getRepository(owner: String, name: String): Result<RepositoryDetail?> =
        apiService.getRepository(owner, name)
            .map { it?.toDomain() }
}
