package com.example.githubusers.core.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.githubusers.core.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for User operations
 */
@Dao
interface UserDao {
    
    @Query("SELECT * FROM users ORDER BY page, id")
    fun getAllUsersPaging(): PagingSource<Int, UserEntity>
    
    @Query("SELECT * FROM users ORDER BY page, id")
    fun getAllUsers(): Flow<List<UserEntity>>
    
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Long): UserEntity?
    
    @Query("SELECT * FROM users WHERE login = :login")
    suspend fun getUserByLogin(login: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE page = :page ORDER BY id")
    suspend fun getUsersByPage(page: Int): List<UserEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)
    
    @Update
    suspend fun updateUser(user: UserEntity)
    
    @Delete
    suspend fun deleteUser(user: UserEntity)
    
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
    
    @Query("DELETE FROM users WHERE cached_at < :timestamp")
    suspend fun deleteUsersOlderThan(timestamp: Long)
    
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
    
    @Query("SELECT MAX(page) FROM users")
    suspend fun getLastPageNumber(): Int?
    
    @Transaction
    suspend fun refreshUsers(users: List<UserEntity>, page: Int) {
        // Delete old data for this page
        deleteUsersByPage(page)
        // Insert new data
        insertUsers(users)
    }
    
    @Query("DELETE FROM users WHERE page = :page")
    suspend fun deleteUsersByPage(page: Int)
}
