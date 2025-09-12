package com.example.githubusers.feature.users.list.data.paging

import android.util.Log
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

/**
 * RemoteMediator for handling pagination with local caching.
 * This is used when browsing all users (not searching).
 */
@OptIn(ExperimentalPagingApi::class)
class UserListRemoteMediator(
    private val apiService: UserListApiService,
    private val database: UserListDatabase,
    private val performanceMonitor: PerformanceMonitor,
) : RemoteMediator<Int, UserSummaryEntity>() {
    companion object {
        private const val TAG = "UserListRemoteMediator"
        private const val REMOTE_KEY_ID = "user_list_remote_key"
    }

    override suspend fun initialize(): InitializeAction {
        // Skip initial refresh if data exists
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UserSummaryEntity>,
    ): MediatorResult {
        Log.d(TAG, "Loading data: loadType=$loadType, pageSize=${state.config.pageSize}")
        return try {
            val loadKey =
                when (loadType) {
                    LoadType.REFRESH -> {
                        Log.d(TAG, "REFRESH: loading from beginning")
                        null
                    }
                    LoadType.PREPEND -> {
                        Log.d(TAG, "PREPEND: not supported, returning end of pagination")
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    LoadType.APPEND -> {
                        val remoteKey = database.remoteKeyDao().getRemoteKey(REMOTE_KEY_ID)
                        Log.d(TAG, "APPEND: remoteKey=$remoteKey, nextKey=${remoteKey?.nextKey}")
                        remoteKey?.nextKey
                            ?: return MediatorResult.Success(endOfPaginationReached = true)
                    }
                }

            Log.d(TAG, "Calling API with since=$loadKey, perPage=${state.config.pageSize}")
            val response =
                apiService.getUsers(
                    since = loadKey,
                    perPage = state.config.pageSize,
                )

            response.fold(
                onSuccess = { users ->
                    Log.d(TAG, "API call successful: received ${users.size} users")
                    database.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            Log.d(TAG, "REFRESH: clearing existing data")
                            database.userSummaryDao().clearAll()
                            database.remoteKeyDao().clearAll()
                        }

                        val entities =
                            performanceMonitor.withMemoryTracking("DatabaseTransaction-${loadType.name}") {
                                users.map { it.toEntity() }
                            }
                        Log.d(TAG, "Inserting ${entities.size} entities into database")
                        database.userSummaryDao().insertAll(entities)

                        // Store the next key (the last user's ID)
                        val nextKey = users.lastOrNull()?.id
                        val remoteKey =
                            RemoteKeyEntity(
                                id = REMOTE_KEY_ID,
                                prevKey = loadKey,
                                nextKey = nextKey,
                            )
                        Log.d(TAG, "Storing remote key: $remoteKey")
                        database.remoteKeyDao().insertOrReplace(remoteKey)
                    }

                    val result = MediatorResult.Success(endOfPaginationReached = users.isEmpty())
                    Log.d(TAG, "Returning success: endOfPaginationReached=${users.isEmpty()}")
                    result
                },
                onFailure = { exception ->
                    Log.e(TAG, "API call failed", exception)
                    MediatorResult.Error(exception)
                },
            )
        } catch (e: IOException) {
            Log.e(TAG, "IOException in RemoteMediator", e)
            MediatorResult.Error(e)
        } catch (e: ClientRequestException) {
            // 4xx errors
            Log.e(TAG, "ClientRequestException in RemoteMediator", e)
            MediatorResult.Error(e)
        } catch (e: ServerResponseException) {
            // 5xx errors
            Log.e(TAG, "ServerResponseException in RemoteMediator", e)
            MediatorResult.Error(e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected exception in RemoteMediator", e)
            MediatorResult.Error(e)
        }
    }
}
