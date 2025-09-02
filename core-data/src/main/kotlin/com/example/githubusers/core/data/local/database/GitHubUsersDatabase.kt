package com.example.githubusers.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.githubusers.core.data.local.dao.RepositoryDao
import com.example.githubusers.core.data.local.dao.UserDao
import com.example.githubusers.core.data.local.dao.UserDetailDao
import com.example.githubusers.core.data.local.entity.RepositoryEntity
import com.example.githubusers.core.data.local.entity.SearchResultEntity
import com.example.githubusers.core.data.local.entity.UserDetailEntity
import com.example.githubusers.core.data.local.entity.UserEntity

/**
 * Room database for GitHub Users app
 */
@Database(
    entities = [
        UserEntity::class,
        UserDetailEntity::class,
        RepositoryEntity::class,
        SearchResultEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class GitHubUsersDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    abstract fun userDetailDao(): UserDetailDao

    abstract fun repositoryDao(): RepositoryDao

    companion object {
        const val DATABASE_NAME = "github_users_database"
    }
}
