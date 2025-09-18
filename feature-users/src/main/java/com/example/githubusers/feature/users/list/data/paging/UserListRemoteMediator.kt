package com.example.githubusers.feature.users.list.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.githubusers.core.ui.performance.PerformanceMonitor
import com.example.githubusers.core.ui.performance.withMemoryTracking
import com.example.githubusers.feature.users.list.data.local.UserListDatabase
import com.example.githubusers.feature.users.list.data.local.entity.RemoteKeyEntity
import com.example.githubusers.feature.users.list.data.local.entity.UserSummaryEntity
import com.example.githubusers.feature.users.list.data.mapper.toEntity
import com.example.githubusers.feature.users.list.data.remote.UserListApiService
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import java.io.IOException
import timber.log.Timber

/**
 * RemoteMediator for handling pagination with local caching.
 * This is used when browsing all users (not searching).
 */
@OptIn(ExperimentalPagingApi::class)
class UserListRemoteMediator(
    private val apiService: UserListApiService,
    private val database: UserListDatabase,
    private val performanceMonitor: PerformanceMonitor
) : RemoteMediator<Int, UserSummaryEntity>() {
    companion object {
        private const val TAG = "UserListRemoteMediator"
        private const val REMOTE_KEY_ID = "user_list_remote_key"
    }

    override suspend fun initialize(): InitializeAction {
        // Skip initial refresh if data exists
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, UserSummaryEntity>): MediatorResult {
        Timber.tag(TAG).d("Loading data: loadType=$loadType, pageSize=${state.config.pageSize}")
        return try {
            val loadKey =
                when (loadType) {
                    LoadType.REFRESH -> {
                        Timber.tag(TAG).d("REFRESH: loading from beginning")
                        null
                    }
                    LoadType.PREPEND -> {
                        Timber.tag(TAG).d("PREPEND: not supported, returning end of pagination")
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    LoadType.APPEND -> {
                        val remoteKey = database.remoteKeyDao().getRemoteKey(REMOTE_KEY_ID)
                        Timber.tag(TAG).d("APPEND: remoteKey=$remoteKey, nextKey=${remoteKey?.nextKey}")
                        remoteKey?.nextKey
                            ?: return MediatorResult.Success(endOfPaginationReached = true)
                    }
                }

            Timber.tag(TAG).d("Calling API with since=$loadKey, perPage=${state.config.pageSize}")
            val response =
                apiService.getUsers(
                    since = loadKey,
                    perPage = state.config.pageSize
                )

            response.fold(
                onSuccess = { users ->
                    Timber.tag(TAG).d("API call successful: received ${users.size} users")
                    database.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            Timber.tag(TAG).d("REFRESH: clearing existing data")
                            database.userSummaryDao().clearAll()
                            database.remoteKeyDao().clearAll()
                        }

                        val entities =
                            performanceMonitor.withMemoryTracking("DatabaseTransaction-${loadType.name}") {
                                users.map { it.toEntity() }
                            }
                        Timber.tag(TAG).d("Inserting ${entities.size} entities into database")
                        database.userSummaryDao().insertAll(entities)

                        // Store the next key (the last user's ID)
                        val nextKey = users.lastOrNull()?.id
                        val remoteKey =
                            RemoteKeyEntity(
                                id = REMOTE_KEY_ID,
                                prevKey = loadKey,
                                nextKey = nextKey
                            )
                        Timber.tag(TAG).d("Storing remote key: $remoteKey")
                        database.remoteKeyDao().insertOrReplace(remoteKey)
                    }

                    val result = MediatorResult.Success(endOfPaginationReached = users.isEmpty())
                    Timber.tag(TAG).d("Returning success: endOfPaginationReached=${users.isEmpty()}")
                    result
                },
                onFailure = { exception ->
                    Timber.tag(TAG).e(exception, "API call failed")
                    MediatorResult.Error(exception)
                }
            )
        } catch (e: IOException) {
            Timber.tag(TAG).e(e, "IOException in RemoteMediator")
            MediatorResult.Error(e)
        } catch (e: ClientRequestException) {
            // 4xx errors
            Timber.tag(TAG).e(e, "ClientRequestException in RemoteMediator")
            MediatorResult.Error(e)
        } catch (e: ServerResponseException) {
            // 5xx errors
            Timber.tag(TAG).e(e, "ServerResponseException in RemoteMediator")
            MediatorResult.Error(e)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Unexpected exception in RemoteMediator")
            MediatorResult.Error(e)
        }
    }
}
