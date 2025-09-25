package com.example.githubusers.feature.users.list.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.githubusers.core.paging.KeysetRemoteMediator
import com.example.githubusers.core.paging.KeysetRemoteMediatorCallbacks
import com.example.githubusers.core.ui.performance.PerformanceMonitor
import com.example.githubusers.core.ui.performance.withMemoryTracking
import com.example.githubusers.feature.users.data.local.UsersDatabase
import com.example.githubusers.feature.users.data.local.entity.RemoteKeyEntity
import com.example.githubusers.feature.users.data.local.entity.UserSummaryEntity
import com.example.githubusers.feature.users.data.remote.UserListApiService
import com.example.githubusers.feature.users.list.data.mapper.toEntity
import timber.log.Timber

/**
 * Thin wrapper around [KeysetRemoteMediator] for GitHub's user "since" pagination.
 */
@OptIn(ExperimentalPagingApi::class)
class UserListRemoteMediator(
    apiService: UserListApiService,
    private val database: UsersDatabase,
    private val performanceMonitor: PerformanceMonitor
) : RemoteMediator<Int, UserSummaryEntity>() {

    companion object {
        private const val TAG = "UserListRemoteMediator"
        private const val REMOTE_KEY_ID = "user_list_remote_key"
    }

    private val delegate = KeysetRemoteMediator(
        callbacks = KeysetRemoteMediatorCallbacks(
            loadCurrentKey = {
                database.remoteKeyDao().getRemoteKey(REMOTE_KEY_ID)?.nextKey
            },
            fetch = { loadType, key, pageSize ->
                Timber.tag(TAG).d("Fetching loadType=%s key=%s pageSize=%d", loadType, key, pageSize)
                val result = apiService.getUsers(since = key, perPage = pageSize)
                result.onFailure { throw it }
                val users = result.getOrThrow()
                performanceMonitor.withMemoryTracking("MapDtoToEntity-${loadType.name}") {
                    users.map { it.toEntity() }
                }
            },
            store = { loadType, items, nextKey ->
                database.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        Timber.tag(TAG).d("Clearing cache for refresh")
                        database.userSummaryDao().clearAll()
                        database.remoteKeyDao().clearAll()
                    }
                    Timber.tag(TAG).d("Persisting %d users; nextKey=%s", items.size, nextKey)
                    database.userSummaryDao().insertAll(items)
                    database.remoteKeyDao().insertOrReplace(
                        RemoteKeyEntity(
                            id = REMOTE_KEY_ID,
                            prevKey = null,
                            nextKey = nextKey
                        )
                    )
                }
            },
            clear = {
                database.withTransaction {
                    database.userSummaryDao().clearAll()
                    database.remoteKeyDao().clearAll()
                }
            },
            resolveNextKey = { items, _ ->
                items.lastOrNull()?.id
            }
        )
    )

    override suspend fun initialize(): InitializeAction = delegate.initialize()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, UserSummaryEntity>): MediatorResult =
        delegate.load(loadType, state)
}
