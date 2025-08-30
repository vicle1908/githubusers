package com.example.githubusers.feature.users.detail.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.githubusers.feature.users.detail.data.local.dao.RepositoryDao
import com.example.githubusers.feature.users.detail.data.local.dao.UserDetailDao
import com.example.githubusers.feature.users.detail.data.local.entity.RepositoryEntity
import com.example.githubusers.feature.users.detail.data.local.entity.UserDetailEntity

/**
 * Room database for user detail feature.
 */
@Database(
    entities = [
        UserDetailEntity::class,
        RepositoryEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class UserDetailDatabase : RoomDatabase() {
    abstract fun userDetailDao(): UserDetailDao

    abstract fun repositoryDao(): RepositoryDao

    companion object {
        const val DATABASE_NAME = "user_detail_database"
    }
}
