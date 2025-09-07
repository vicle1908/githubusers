package com.example.githubusers.feature.users.list.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.githubusers.feature.users.list.data.local.entity.UserSummaryEntity

/**
 * Data Access Object for user summaries.
 */
@Dao
interface UserSummaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserSummaryEntity>)

    @Query("SELECT * FROM user_summaries ORDER BY id ASC")
    fun getUsersPaged(): PagingSource<Int, UserSummaryEntity>

    @Query("DELETE FROM user_summaries")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM user_summaries")
    suspend fun count(): Int
}
