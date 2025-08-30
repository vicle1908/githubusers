package com.example.githubusers.feature.users.list.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
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
) : RemoteMediator<Int, UserSummaryEntity>() {
    companion object {
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
        return try {
            val loadKey =
                when (loadType) {
                    LoadType.REFRESH -> null
                    LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                    LoadType.APPEND -> {
                        val remoteKey = database.remoteKeyDao().getRemoteKey(REMOTE_KEY_ID)
                        remoteKey?.nextKey
                            ?: return MediatorResult.Success(endOfPaginationReached = true)
                    }
                }

            val response =
                apiService.getUsers(
                    since = loadKey,
                    perPage = state.config.pageSize,
                )

            response.fold(
                onSuccess = { users ->
                    database.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            database.userSummaryDao().clearAll()
                            database.remoteKeyDao().clearAll()
                        }

                        val entities = users.map { it.toEntity() }
                        database.userSummaryDao().insertAll(entities)

                        // Store the next key (the last user's ID)
                        val nextKey = users.lastOrNull()?.id
                        val remoteKey =
                            RemoteKeyEntity(
                                id = REMOTE_KEY_ID,
                                prevKey = loadKey,
                                nextKey = nextKey,
                            )
                        database.remoteKeyDao().insertOrReplace(remoteKey)
                    }

                    MediatorResult.Success(endOfPaginationReached = users.isEmpty())
                },
                onFailure = { exception ->
                    MediatorResult.Error(exception)
                },
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: ClientRequestException) {
            // 4xx errors
            MediatorResult.Error(e)
        } catch (e: ServerResponseException) {
            // 5xx errors
            MediatorResult.Error(e)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
