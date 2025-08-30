package com.example.githubusers.feature.users.list.data.local

import androidx.paging.PagingSource
import com.example.githubusers.feature.users.list.data.local.dao.UserDao
import com.example.githubusers.feature.users.list.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of UserListLocalDataSource using Room.
 */
class UserListLocalDataSourceImpl
    @Inject
    constructor(
        private val userDao: UserDao,
    ) : UserListLocalDataSource {
        override fun getUsersPagingSource(): PagingSource<Int, UserEntity> = userDao.getUsersPagingSource()

        override fun searchUsersPagingSource(query: String): PagingSource<Int, UserEntity> = userDao.searchUsersPagingSource("%$query%")

        override suspend fun insertUsers(users: List<UserEntity>) {
            userDao.insertUsers(users)
        }

        override suspend fun clearAllUsers() {
            userDao.clearAll()
        }

        override suspend fun getLastUserId(): Long? = userDao.getLastUserId()

        override suspend fun hasUsers(): Boolean = userDao.getUserCount() > 0

        override fun observeUserCount(): Flow<Int> = userDao.observeUserCount()
    }
