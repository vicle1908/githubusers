package com.example.githubusers.feature.users.data.list.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.githubusers.core.paging.PagingProfiles
import com.example.githubusers.core.search.SearchQueryNormalizer
import com.example.githubusers.core.ui.performance.PerformanceMonitor
import com.example.githubusers.core.users.domain.UserSummary
import com.example.githubusers.feature.users.data.UserQueryParser
import com.example.githubusers.feature.users.data.list.mapper.toDomainModel
import com.example.githubusers.feature.users.data.list.paging.UserListRemoteMediator
import com.example.githubusers.feature.users.data.local.UsersDatabase
import com.example.githubusers.feature.users.data.remote.UserListApiService
import com.example.githubusers.feature.users.domain.list.repository.UserListRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of UserListRepository.
 */
class UserListRepositoryImpl @Inject constructor(
    private val apiService: UserListApiService,
    private val database: UsersDatabase,
    private val performanceMonitor: PerformanceMonitor
) : UserListRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getUsersPaged(query: SearchQueryNormalizer.NormalizedSearchQuery): Flow<PagingData<UserSummary>> {
        val dao = database.userSummaryDao()
        val parsedQuery = UserQueryParser.parse(
            normalized = query,
            fallbackQuery = ""
        )
        val pagingConfig: PagingConfig = PagingProfiles.compactList()

        return Pager(
            config = pagingConfig,
            remoteMediator = UserListRemoteMediator(apiService, database, performanceMonitor),
            pagingSourceFactory = {
                val normalizedFilter = parsedQuery.plainQuery?.lowercase()
                val typeQualifier = parsedQuery.type?.lowercase()
                if (normalizedFilter == null && typeQualifier == null) {
                    dao.getUsersPaged()
                } else {
                    dao.searchUsersPaged(normalizedFilter, typeQualifier)
                }
            }
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomainModel() }
        }
    }

    override suspend fun clearCache() {
        database.userSummaryDao().clearAll()
        database.remoteKeyDao().clearAll()
    }
}
