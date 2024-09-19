package com.example.githubusers.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.githubusers.data.local.RemoteKeyDao
import com.example.githubusers.data.local.UserDao
import com.example.githubusers.data.local.UserDatabase
import com.example.githubusers.data.remote.UserApiService
import com.example.githubusers.data.remote.UserRemoteMediator
import com.example.githubusers.domain.entity.User
import com.example.githubusers.domain.entity.UserDetail
import com.example.githubusers.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Implementation of the [UserRepository] interface.
 * Manages data fetching from Room (local database) and the GitHub API (remote) using RemoteMediator.
 * Maps data from DTOs to domain entities before exposing it to the app's presentation layer.
 *
 * @property apiService The service used to interact with the GitHub API.
 * @property userDao The DAO used to interact with the local Room database.
 * @property userDatabase The Room database instance.
 */
class UserRepositoryImpl
@Inject
constructor(
    private val apiService: UserApiService,
    private val userDao: UserDao,
    private val remoteKeyDao: RemoteKeyDao,
    private val userDatabase: UserDatabase,
) : UserRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getUsersPaged(): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false,
            ),
            remoteMediator = UserRemoteMediator(apiService, userDao, remoteKeyDao, userDatabase),
            pagingSourceFactory = {
                userDao.getUsersPaged()
            },
        ).flow
    }

    /**
     * Fetches detailed information about a specific GitHub user.
     * First, retrieves cached data from Room, then fetches fresh data from the GitHub API.
     * If the API data is different from the cached data, Room is updated with the new data.
     * The result is wrapped in Kotlin's [Result] class to handle success and error cases.
     *
     * @param username The GitHub username for which the details are fetched.
     * @return A [Flow] that emits [Result] of the [UserDetail] of the GitHub user.
     */
    override fun getUserDetail(username: String): Flow<Result<UserDetail?>> = flow {
        // Step 1: Emit the cached data from Room
        userDao.getUserDetail(username).collect { cachedUserDetail ->
            // Emit cached data, which may be null if not yet available
            emit(Result.success(cachedUserDetail))

            // Step 2: Fetch fresh data from the API
            val apiResult = apiService.getUserDetail(username)

            // Step 3: Handle the API response
            apiResult.onSuccess { fetchedUserDetail ->
                // If API data is different, update Room
                if (fetchedUserDetail != cachedUserDetail) {
                    userDao.insertUserDetail(fetchedUserDetail)
                    // Room will trigger an emission of the updated data, no need to emit here
                }
            }.onFailure { exception ->
                // Step 4: If API call fails, emit a failure result but retain cached data
                // Notify that the API call failed but don't interfere with the cached data
                emit(Result.failure<UserDetail>(exception))
            }
        }
    }.catch { e ->
        // Step 5: Handle any unexpected exceptions that occur in the flow
        emit(Result.failure(e))
    }
}
