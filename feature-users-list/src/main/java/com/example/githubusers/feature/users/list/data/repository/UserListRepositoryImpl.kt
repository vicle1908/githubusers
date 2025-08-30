package com.example.githubusers.feature.users.list.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.githubusers.feature.users.list.data.local.UserListDatabase
import com.example.githubusers.feature.users.list.data.mapper.toDomainModel
import com.example.githubusers.feature.users.list.data.paging.UserListPagingSource
import com.example.githubusers.feature.users.list.data.paging.UserListRemoteMediator
import com.example.githubusers.feature.users.list.data.remote.UserListApiService
import com.example.githubusers.feature.users.list.domain.entity.UserSummary
import com.example.githubusers.feature.users.list.domain.repository.UserListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of UserListRepository.
 * Handles data fetching from GitHub API and local caching using Room.
 */
class UserListRepositoryImpl
    @Inject
    constructor(
        private val apiService: UserListApiService,
        private val database: UserListDatabase,
    ) : UserListRepository {
        companion object {
            private const val PAGE_SIZE = 20
            private const val PREFETCH_DISTANCE = 5
        }

        @OptIn(ExperimentalPagingApi::class)
        override fun getUsersPaged(query: String): Flow<PagingData<UserSummary>> =
            if (query.isBlank()) {
                // Browse all users with RemoteMediator for caching
                Pager(
                    config =
                        PagingConfig(
                            pageSize = PAGE_SIZE,
                            prefetchDistance = PREFETCH_DISTANCE,
                            initialLoadSize = PAGE_SIZE,
                            enablePlaceholders = false,
                        ),
                    remoteMediator = UserListRemoteMediator(apiService, database),
                    pagingSourceFactory = { database.userSummaryDao().getUsersPaged() },
                ).flow.map { pagingData ->
                    pagingData.map { entity ->
                        entity.toDomainModel()
                    }
                }
            } else {
                // Search users directly from API (no caching for search)
                Pager(
                    config =
                        PagingConfig(
                            pageSize = 30,
                            prefetchDistance = PREFETCH_DISTANCE,
                            initialLoadSize = 30,
                            enablePlaceholders = false,
                        ),
                    pagingSourceFactory = { UserListPagingSource(apiService, query) },
                ).flow
            }

        @OptIn(ExperimentalPagingApi::class)
        override fun getUsers(since: Int): Flow<PagingData<UserSummary>> {
            // Browse all users with RemoteMediator for caching
            return Pager(
                config =
                    PagingConfig(
                        pageSize = PAGE_SIZE,
                        prefetchDistance = PREFETCH_DISTANCE,
                        initialLoadSize = PAGE_SIZE,
                        enablePlaceholders = false,
                    ),
                remoteMediator = UserListRemoteMediator(apiService, database),
                pagingSourceFactory = { database.userSummaryDao().getUsersPaged() },
            ).flow.map { pagingData ->
                pagingData.map { entity ->
                    entity.toDomainModel()
                }
            }
        }

        override fun searchUsers(query: String): Flow<PagingData<UserSummary>> {
            // Search users directly from API (no caching for search)
            return Pager(
                config =
                    PagingConfig(
                        pageSize = 30,
                        prefetchDistance = PREFETCH_DISTANCE,
                        initialLoadSize = 30,
                        enablePlaceholders = false,
                    ),
                pagingSourceFactory = { UserListPagingSource(apiService, query) },
            ).flow
        }

        override suspend fun clearCache() {
            database.userSummaryDao().clearAll()
            database.remoteKeyDao().clearAll()
        }
    }
