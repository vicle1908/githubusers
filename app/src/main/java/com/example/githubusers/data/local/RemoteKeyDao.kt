package com.example.githubusers.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.githubusers.domain.entity.RemoteKey

@Dao
interface RemoteKeyDao {
    // Insert multiple RemoteKeys
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<RemoteKey>)

    // Get the remote key for a specific user
    @Query("SELECT * FROM remote_keys WHERE userId = :userId")
    suspend fun remoteKeyByUser(userId: Int): RemoteKey?

    // Clear all keys (used during refresh)
    @Query("DELETE FROM remote_keys")
    suspend fun clearAll()

    // Get the first RemoteKey to determine the previous page for PREPEND
    @Query("SELECT * FROM remote_keys ORDER BY userId ASC LIMIT 1")
    suspend fun remoteKeyForFirstItem(): RemoteKey?

    // Get the last RemoteKey to determine the next page for APPEND
    @Query("SELECT * FROM remote_keys ORDER BY userId DESC LIMIT 1")
    suspend fun remoteKeyForLastItem(): RemoteKey?
}
