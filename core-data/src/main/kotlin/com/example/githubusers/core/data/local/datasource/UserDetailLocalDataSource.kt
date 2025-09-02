package com.example.githubusers.core.data.local.datasource

import com.example.githubusers.core.data.local.dao.UserDetailDao
import com.example.githubusers.core.data.local.entity.UserDetailEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface UserDetailLocalDataSource {
    fun getUserDetail(login: String): Flow<UserDetailEntity?>

    suspend fun getUserDetailSync(login: String): UserDetailEntity?

    suspend fun insertUserDetail(userDetail: UserDetailEntity)

    suspend fun deleteUserDetail(login: String)

    suspend fun deleteUserDetailsOlderThan(timestamp: Long)
}

class UserDetailLocalDataSourceImpl
    @Inject
    constructor(
        private val userDetailDao: UserDetailDao,
    ) : UserDetailLocalDataSource {
        override fun getUserDetail(login: String): Flow<UserDetailEntity?> = userDetailDao.getUserDetailByLogin(login)

        override suspend fun getUserDetailSync(login: String): UserDetailEntity? = userDetailDao.getUserDetailByLoginSync(login)

        override suspend fun insertUserDetail(userDetail: UserDetailEntity) = userDetailDao.insertUserDetail(userDetail)

        override suspend fun deleteUserDetail(login: String) = userDetailDao.deleteUserDetailByLogin(login)

        override suspend fun deleteUserDetailsOlderThan(timestamp: Long) = userDetailDao.deleteUserDetailsOlderThan(timestamp)
    }
