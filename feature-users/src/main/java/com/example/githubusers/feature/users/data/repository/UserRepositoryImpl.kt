package com.example.githubusers.feature.users.data.repository

import androidx.paging.PagingData
import androidx.paging.map
import com.example.githubusers.feature.users.detail.domain.repository.UserDetailRepository
import com.example.githubusers.feature.users.domain.entity.User
import com.example.githubusers.feature.users.domain.entity.UserDetail
import com.example.githubusers.feature.users.domain.repository.UserRepository
import com.example.githubusers.feature.users.list.domain.repository.UserListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of UserRepository that delegates to existing repositories
 * Owned by feature-users module per feature-based architecture
 */
@Singleton
class UserRepositoryImpl
    @Inject
    constructor(
        private val userListRepository: UserListRepository,
        private val userDetailRepository: UserDetailRepository,
    ) : UserRepository {
        override fun getUsersPaged(query: String): Flow<PagingData<User>> {
            // Delegate to user list repository and convert UserSummary to User
            return userListRepository.getUsersPaged(query).map { pagingData ->
                pagingData.map { userSummary ->
                    User(
                        id = userSummary.id.toLong(),
                        login = userSummary.login,
                        avatarUrl = userSummary.avatarUrl,
                        htmlUrl = userSummary.htmlUrl,
                        type = userSummary.type,
                    )
                }
            }
        }

        override suspend fun getUserDetail(username: String): Result<UserDetail?> {
            // Delegate to user detail repository and convert to domain entity
            return userDetailRepository.getUserDetail(username).map { detailEntity ->
                UserDetail(
                    id = detailEntity.id,
                    login = detailEntity.login,
                    name = detailEntity.name,
                    avatarUrl = detailEntity.avatarUrl,
                    htmlUrl = detailEntity.htmlUrl,
                    bio = detailEntity.bio,
                    location = detailEntity.location,
                    company = detailEntity.company,
                    blog = detailEntity.blog,
                    publicRepos = detailEntity.publicRepos,
                    followers = detailEntity.followers,
                    following = detailEntity.following,
                    createdAt = detailEntity.createdAt.toString(),
                    updatedAt = detailEntity.updatedAt.toString(),
                )
            }
        }
    }
