package com.example.githubusers.feature.users.data.detail.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.githubusers.feature.users.data.local.dao.RepositoryDao
import com.example.githubusers.feature.users.data.local.dao.UserDetailDao
import com.example.githubusers.feature.users.data.remote.api.UserDetailRemoteDataSource
import com.example.githubusers.feature.users.data.remote.paging.RepositoryPagingSource
import com.example.githubusers.feature.users.data.detail.mapper.UserDetailMapper.toDomain
import com.example.githubusers.feature.users.data.detail.mapper.UserDetailMapper.toEntity
import com.example.githubusers.feature.users.domain.detail.repository.RepositorySort
import com.example.githubusers.feature.users.domain.detail.repository.UserDetailRepository
import com.example.githubusers.feature.users.domain.model.Repository
import com.example.githubusers.feature.users.domain.model.UserDetail
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of UserDetailRepository.
 */
class UserDetailRepositoryImpl
@Inject
constructor(
    private val remoteDataSource: UserDetailRemoteDataSource,
    private val userDetailDao: UserDetailDao,
    private val repositoryDao: RepositoryDao
) : UserDetailRepository {
    override suspend fun getUserDetail(username: String): Result<UserDetail> = try {
        // Try to get from cache first
        val cachedUser = userDetailDao.getUserDetail(username)
        if (cachedUser != null) {
            // Return cached data but also refresh in background
            refreshUserDetail(username)
            Result.success(cachedUser.toDomain())
        } else {
            // Fetch from network
            val userDetail = remoteDataSource.getUserDetail(username)
            // Cache the result
            userDetailDao.insertUserDetail(userDetail.toEntity())
            Result.success(userDetail.toDomain())
        }
    } catch (e: Exception) {
        // If network fails, try to return cached data
        val cachedUser = userDetailDao.getUserDetail(username)
        if (cachedUser != null) {
            Result.success(cachedUser.toDomain())
        } else {
            Result.failure(e)
        }
    }

    private suspend fun refreshUserDetail(username: String) {
        try {
            val userDetail = remoteDataSource.getUserDetail(username)
            userDetailDao.insertUserDetail(userDetail.toEntity())
        } catch (e: Exception) {
            // Ignore refresh errors
        }
    }

    override fun getUserRepositories(
        username: String,
        sort: RepositorySort,
        perPage: Int
    ): Flow<PagingData<Repository>> = Pager(
        config =
        PagingConfig(
            pageSize = perPage,
            enablePlaceholders = false,
            prefetchDistance = 5
        ),
        pagingSourceFactory = {
            RepositoryPagingSource(
                remoteDataSource = remoteDataSource,
                username = username,
                sort = sort
            )
        }
    ).flow

    override suspend fun clearUserDetailCache(username: String) {
        userDetailDao.deleteUserDetail(username)
        repositoryDao.deleteRepositoriesByUser(username)
    }

    override suspend fun isFollowing(username: String): Boolean = try {
        remoteDataSource.isFollowing(username)
    } catch (e: Exception) {
        false
    }

    override suspend fun followUser(username: String): Result<Unit> = try {
        remoteDataSource.followUser(username)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun unfollowUser(username: String): Result<Unit> = try {
        remoteDataSource.unfollowUser(username)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
