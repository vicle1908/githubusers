package com.example.githubusers.core.data.local.datasource

import androidx.paging.PagingSource
import com.example.githubusers.core.data.local.dao.UserDao
import com.example.githubusers.core.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface UserLocalDataSource {
    fun getUsersPaging(): PagingSource<Int, UserEntity>
    fun getUsers(): Flow<List<UserEntity>>
    suspend fun getUserById(userId: Long): UserEntity?
    suspend fun getUserByLogin(login: String): UserEntity?
    suspend fun getUsersByPage(page: Int): List<UserEntity>
    suspend fun insertUser(user: UserEntity)
    suspend fun insertUsers(users: List<UserEntity>)
    suspend fun updateUser(user: UserEntity)
    suspend fun deleteUser(user: UserEntity)
    suspend fun deleteAllUsers()
    suspend fun deleteUsersOlderThan(timestamp: Long)
    suspend fun getUserCount(): Int
    suspend fun getLastPageNumber(): Int?
    suspend fun refreshUsers(users: List<UserEntity>, page: Int)
}

class UserLocalDataSourceImpl @Inject constructor(
    private val userDao: UserDao
) : UserLocalDataSource {
    
    override fun getUsersPaging(): PagingSource<Int, UserEntity> = userDao.getAllUsersPaging()
    
    override fun getUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()
    
    override suspend fun getUserById(userId: Long): UserEntity? = userDao.getUserById(userId)
    
    override suspend fun getUserByLogin(login: String): UserEntity? = userDao.getUserByLogin(login)
    
    override suspend fun getUsersByPage(page: Int): List<UserEntity> = userDao.getUsersByPage(page)
    
    override suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)
    
    override suspend fun insertUsers(users: List<UserEntity>) = userDao.insertUsers(users)
    
    override suspend fun updateUser(user: UserEntity) = userDao.updateUser(user)
    
    override suspend fun deleteUser(user: UserEntity) = userDao.deleteUser(user)
    
    override suspend fun deleteAllUsers() = userDao.deleteAllUsers()
    
    override suspend fun deleteUsersOlderThan(timestamp: Long) = userDao.deleteUsersOlderThan(timestamp)
    
    override suspend fun getUserCount(): Int = userDao.getUserCount()
    
    override suspend fun getLastPageNumber(): Int? = userDao.getLastPageNumber()
    
    override suspend fun refreshUsers(users: List<UserEntity>, page: Int) = userDao.refreshUsers(users, page)
}
