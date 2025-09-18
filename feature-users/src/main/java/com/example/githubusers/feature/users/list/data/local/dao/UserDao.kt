package com.example.githubusers.feature.users.list.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.githubusers.feature.users.list.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for user operations.
 */
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Query("SELECT * FROM users ORDER BY id ASC")
    fun getUsersPagingSource(): PagingSource<Int, UserEntity>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE login = :login")
    suspend fun getUserByLogin(login: String): UserEntity?

    @Query("DELETE FROM users")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    @Query("SELECT COUNT(*) FROM users")
    fun observeUserCount(): Flow<Int>

    @Query("SELECT MAX(id) FROM users")
    suspend fun getLastUserId(): Long?
}
