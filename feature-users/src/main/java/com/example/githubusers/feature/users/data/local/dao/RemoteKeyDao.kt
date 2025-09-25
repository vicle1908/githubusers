package com.example.githubusers.feature.users.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.githubusers.feature.users.data.local.entity.RemoteKeyEntity

/**
 * Data Access Object for remote keys used in paging.
 */
@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(remoteKey: RemoteKeyEntity)

    @androidx.room.Transaction
    suspend fun refresh(remoteKey: RemoteKeyEntity) {
        clearAll()
        insertOrReplace(remoteKey)
    }

    @Query("SELECT * FROM remote_keys WHERE id = :id")
    suspend fun getRemoteKey(id: String): RemoteKeyEntity?

    @Query("DELETE FROM remote_keys")
    suspend fun clearAll()
}
