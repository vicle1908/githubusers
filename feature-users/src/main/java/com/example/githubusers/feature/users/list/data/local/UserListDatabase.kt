package com.example.githubusers.feature.users.list.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.githubusers.feature.users.list.data.local.dao.RemoteKeyDao
import com.example.githubusers.feature.users.list.data.local.dao.UserSummaryDao
import com.example.githubusers.feature.users.list.data.local.entity.RemoteKeyEntity
import com.example.githubusers.feature.users.list.data.local.entity.UserSummaryEntity

/**
 * Room database for the user list feature.
 */
@Database(
    entities = [
        UserSummaryEntity::class,
        RemoteKeyEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class UserListDatabase : RoomDatabase() {
    abstract fun userSummaryDao(): UserSummaryDao

    abstract fun remoteKeyDao(): RemoteKeyDao

    companion object {
        const val DATABASE_NAME = "user_list_database"
    }
}
