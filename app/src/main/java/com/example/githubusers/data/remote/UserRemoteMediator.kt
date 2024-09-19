package com.example.githubusers.data.remote

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.githubusers.data.local.RemoteKeyDao
import com.example.githubusers.data.local.UserDao
import com.example.githubusers.data.local.UserDatabase
import com.example.githubusers.domain.entity.RemoteKey
import com.example.githubusers.domain.entity.User
import java.io.IOException

/**
 * RemoteMediator to fetch data from API and cache it in Room.
 */
@OptIn(ExperimentalPagingApi::class)
class UserRemoteMediator(
    private val apiService: UserApiService,
    private val userDao: UserDao,
    private val remoteKeyDao: RemoteKeyDao,
    private val database: UserDatabase,
) : RemoteMediator<Int, User>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, User>,
    ): MediatorResult {
        return try {
            val loadKey =
                when (loadType) {
                    LoadType.REFRESH -> {
                        Log.d("UserRemoteMediator", "LoadType.REFRESH")
                        null // Start from the first page
                    }
                    LoadType.APPEND -> {
                        Log.d("UserRemoteMediator", "LoadType.APPEND: Fetching next page")
                        val remoteKey = getLastRemoteKey(state)
                        remoteKey?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    LoadType.PREPEND -> {
                        // GitHub API does not support backward pagination
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                }

            // Fetch data from the API
            val response: Result<List<User>> = apiService.getUsers(perPage = state.config.pageSize, since = loadKey ?: 0)

            // Check API response
            response.fold(
                onSuccess = { users ->
                    // Handle case where the API returns no users (end of pagination)
                    val endOfPaginationReached = users.isEmpty()
                    Log.d("UserRemoteMediator", "Fetched ${users.size} users, endOfPaginationReached=$endOfPaginationReached")

                    // Perform database transaction to insert data and update remote keys
                    database.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            Log.d("UserRemoteMediator", "Clearing all users and remote keys from database")
                            userDao.clearAll()
                            remoteKeyDao.clearAll()
                        }

                        // Insert new data only if users are present
                        if (users.isNotEmpty()) {
                            val nextKey = users.lastOrNull()?.id?.plus(1)

                            // Insert RemoteKeys for the users
                            val keys =
                                users.map { user ->
                                    RemoteKey(userId = user.id, nextKey = nextKey)
                                }

                            Log.d("UserRemoteMediator", "Inserting keys: $keys")
                            remoteKeyDao.insertAll(keys)
                            userDao.insertAll(users)
                        }
                    }

                    // Return success with pagination status
                    MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
                },
                onFailure = { exception ->
                    Log.e("UserRemoteMediator", "Error fetching users: ${exception.localizedMessage}")
                    MediatorResult.Error(exception)
                },
            )
        } catch (e: IOException) {
            Log.e("UserRemoteMediator", "IOException: ${e.localizedMessage}")
            MediatorResult.Error(e)
        }
    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, User>): RemoteKey? {
        // Retrieve the last page that contains data
        val lastPage = state.pages.lastOrNull { it.data.isNotEmpty() }

        // If lastPage is null (on first run or no data), return a default RemoteKey
        if (lastPage == null) {
            Log.d("UserRemoteMediator", "No last page available in state, returning default RemoteKey with nextKey=0")
            return RemoteKey(userId = 0, nextKey = 0) // Default RemoteKey to start from since=0
        }

        // Retrieve the last user from the last page
        val lastUser = lastPage.data.lastOrNull()
        if (lastUser == null) {
            Log.d("UserRemoteMediator", "No last user in the last page")
            return null
        }

        // Retrieve the RemoteKey for the last user
        return remoteKeyDao.remoteKeyByUser(lastUser.id)
    }

    override suspend fun initialize(): InitializeAction {
        // Implement initialization logic if needed
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }
}
