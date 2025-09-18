package com.example.githubusers.feature.users.list.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.githubusers.core.ui.performance.PerformanceMonitor
import com.example.githubusers.core.users.domain.UserSummary
import com.example.githubusers.feature.users.list.data.local.UserListDatabase
import com.example.githubusers.feature.users.list.data.mapper.toDomainModel
import com.example.githubusers.feature.users.list.data.paging.UserListRemoteMediator
import com.example.githubusers.feature.users.list.data.remote.UserListApiService
import com.example.githubusers.feature.users.list.domain.repository.UserListRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of UserListRepository.
 * Handles browsing data via Room cache + RemoteMediator.
 */
class UserListRepositoryImpl @Inject constructor(
    private val apiService: UserListApiService,
    private val database: UserListDatabase,
    private val performanceMonitor: PerformanceMonitor
) : UserListRepository {
    companion object {
        private const val PAGE_SIZE = 20
        private const val PREFETCH_DISTANCE = 5
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getUsersPaged(): Flow<PagingData<UserSummary>> = Pager(
        config =
        PagingConfig(
            pageSize = PAGE_SIZE,
            prefetchDistance = PREFETCH_DISTANCE,
            initialLoadSize = PAGE_SIZE,
            enablePlaceholders = false
        ),
        remoteMediator = UserListRemoteMediator(apiService, database, performanceMonitor),
        pagingSourceFactory = { database.userSummaryDao().getUsersPaged() }
    ).flow.map { pagingData ->
        pagingData.map { entity ->
            entity.toDomainModel()
        }
    }

    override suspend fun clearCache() {
        database.userSummaryDao().clearAll()
        database.remoteKeyDao().clearAll()
    }
}
