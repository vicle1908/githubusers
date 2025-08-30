package com.example.githubusers.core.data.repository

import androidx.paging.*
import com.example.githubusers.core.data.api.GitHubApiService
import com.example.githubusers.core.data.local.datasource.UserLocalDataSource
import com.example.githubusers.core.data.local.datasource.UserDetailLocalDataSource
import com.example.githubusers.core.data.local.entity.UserEntity
import com.example.githubusers.core.data.mappers.toDomain
import com.example.githubusers.core.data.mappers.toEntity
import com.example.githubusers.core.domain.entity.User
import com.example.githubusers.core.domain.entity.UserDetail
import com.example.githubusers.core.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: GitHubApiService,
    private val local: UserLocalDataSource,
    private val userDetailLocal: UserDetailLocalDataSource
) : UserRepository {
    
    @OptIn(ExperimentalPagingApi::class)
    override fun getUsersPaged(query: String): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                prefetchDistance = 10,
                enablePlaceholders = false
            ),
            remoteMediator = UserRemoteMediator(
                api = api,
                local = local,
                query = query
            ),
            pagingSourceFactory = {
                local.getUsersPaging()
            }
        ).flow.map {
            it.map { userEntity -> userEntity.toDomain() }
        }
    }
    
    override fun getUserDetail(username: String): Flow<Result<UserDetail?>> {
        return kotlinx.coroutines.flow.flow {
            try {
                // First try to get from cache
                val cached = userDetailLocal.getUserDetailSync(username)
                if (cached != null) {
                    emit(Result.success(cached.toDomain()))
                }
                
                // Fetch from network
                val networkUser = api.getUserDetails(username)
                userDetailLocal.insertUserDetail(networkUser.toEntity())
                emit(Result.success(networkUser.toEntity().toDomain()))
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        }
    }
}

@OptIn(ExperimentalPagingApi::class)
class UserRemoteMediator(
    private val api: GitHubApiService,
    private val local: UserLocalDataSource,
    private val query: String
) : RemoteMediator<Int, UserEntity>() {
    
    override suspend fun load(loadType: LoadType, state: PagingState<Int, UserEntity>): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> local.getLastPageNumber() ?: 0
            }
            
            if (loadType == LoadType.REFRESH) {
                local.deleteAllUsers()
            }
            
            val users = api.getUsers(since = page * state.config.pageSize)
            local.insertUsers(users.map { it.toEntity(page) })
            
            MediatorResult.Success(endOfPaginationReached = users.isEmpty())
            
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
