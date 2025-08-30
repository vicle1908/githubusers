package com.example.githubusers.feature.users.detail.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.githubusers.feature.users.detail.data.local.entity.UserDetailEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for user detail operations.
 */
@Dao
interface UserDetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserDetail(user: UserDetailEntity)

    @Query("SELECT * FROM user_details WHERE login = :username")
    suspend fun getUserDetail(username: String): UserDetailEntity?

    @Query("SELECT * FROM user_details WHERE login = :username")
    fun getUserDetailFlow(username: String): Flow<UserDetailEntity?>

    @Query("DELETE FROM user_details WHERE login = :username")
    suspend fun deleteUserDetail(username: String)

    @Query("DELETE FROM user_details")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM user_details")
    suspend fun getUserCount(): Int
}
