package com.example.githubusers.feature.repository.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.room.withTransaction
import com.example.githubusers.core.paging.BaseRemoteMediator
import com.example.githubusers.feature.repository.data.local.RepositoryDatabase
import com.example.githubusers.feature.repository.data.local.RepositoryEntity
import com.example.githubusers.feature.repository.data.mapper.toEntity
import com.example.githubusers.feature.repository.data.remote.RepositoryApiService
import timber.log.Timber

@OptIn(ExperimentalPagingApi::class)
class RepositoryRemoteMediator(
    private val apiService: RepositoryApiService,
    private val database: RepositoryDatabase,
    private val query: String
) : BaseRemoteMediator<RepositoryEntity>() {

    private val dao = database.repositoryDao()

    override suspend fun fetch(page: Int, pageSize: Int): List<RepositoryEntity> {
        Timber.tag(TAG).d("Fetching repositories: page=%d, perPage=%d, query='%s'", page, pageSize, query)
        val response = apiService.searchRepositories(
            query = query,
            page = page,
            perPage = pageSize
        )

        return response.fold(
            onSuccess = { payload ->
                Timber.tag(TAG).d("Fetched %d repositories", payload.items.size)
                payload.items.map { it.toEntity() }
            },
            onFailure = { exception ->
                Timber.tag(TAG).e(exception, "Failed to fetch repositories: page=%d, query='%s'", page, query)
                throw exception
            }
        )
    }

    override suspend fun store(items: List<RepositoryEntity>, loadType: LoadType) {
        if (items.isEmpty()) return

        Timber.tag(TAG).d(
            "Persisting %d repositories for loadType=%s",
            items.size,
            loadType
        )

        database.withTransaction {
            // Clear existing data when it's a refresh load
            if (loadType == LoadType.REFRESH) {
                dao.clearAll()
            }
            dao.insertAll(items)
        }
    }

    override suspend fun clear() {
        Timber.tag(TAG).d("Clearing cached repositories for query='%s'", query)
        database.withTransaction {
            dao.clearAll()
        }
    }

    companion object {
        private const val TAG = "RepositoryRemoteMediator"
    }
}
