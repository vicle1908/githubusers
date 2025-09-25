package com.example.githubusers.feature.users.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.githubusers.feature.users.data.local.entity.UserSummaryEntity

/**
 * Data Access Object for user summaries.
 */
@Dao
interface UserSummaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserSummaryEntity>)

    @androidx.room.Transaction
    suspend fun replaceAll(users: List<UserSummaryEntity>) {
        clearAll()
        insertAll(users)
    }

    @Query("SELECT * FROM user_summaries ORDER BY id ASC")
    fun getUsersPaged(): PagingSource<Int, UserSummaryEntity>

    @Query(
        "SELECT * FROM user_summaries WHERE " +
            "((:filter IS NULL) OR (LOWER(login) LIKE '%' || :filter || '%' OR LOWER(type) LIKE '%' || :filter || '%')) AND " +
            "((:typeQualifier IS NULL) OR LOWER(type) = :typeQualifier) " +
            "ORDER BY id ASC"
    )
    fun searchUsersPaged(filter: String?, typeQualifier: String?): PagingSource<Int, UserSummaryEntity>

    @Query("DELETE FROM user_summaries")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM user_summaries")
    suspend fun count(): Int
}
