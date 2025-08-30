package com.example.githubusers.core.data.local.dao

import androidx.room.*
import com.example.githubusers.core.data.local.entity.UserDetailEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for UserDetail operations
 */
@Dao
interface UserDetailDao {
    
    @Query("SELECT * FROM user_details WHERE login = :login")
    fun getUserDetailByLogin(login: String): Flow<UserDetailEntity?>
    
    @Query("SELECT * FROM user_details WHERE login = :login")
    suspend fun getUserDetailByLoginSync(login: String): UserDetailEntity?
    
    @Query("SELECT * FROM user_details WHERE id = :userId")
    suspend fun getUserDetailById(userId: Long): UserDetailEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserDetail(userDetail: UserDetailEntity)
    
    @Update
    suspend fun updateUserDetail(userDetail: UserDetailEntity)
    
    @Delete
    suspend fun deleteUserDetail(userDetail: UserDetailEntity)
    
    @Query("DELETE FROM user_details WHERE login = :login")
    suspend fun deleteUserDetailByLogin(login: String)
    
    @Query("DELETE FROM user_details")
    suspend fun deleteAllUserDetails()
    
    @Query("DELETE FROM user_details WHERE cached_at < :timestamp")
    suspend fun deleteUserDetailsOlderThan(timestamp: Long)
    
    @Query("SELECT COUNT(*) FROM user_details")
    suspend fun getUserDetailCount(): Int
    
    @Query("""
        SELECT * FROM user_details 
        WHERE cached_at > :timestamp 
        ORDER BY cached_at DESC
    """)
    suspend fun getRecentUserDetails(timestamp: Long): List<UserDetailEntity>
}
