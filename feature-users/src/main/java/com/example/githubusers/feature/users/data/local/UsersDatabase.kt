package com.example.githubusers.feature.users.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.githubusers.feature.users.data.local.dao.RemoteKeyDao
import com.example.githubusers.feature.users.data.local.dao.RepositoryDao
import com.example.githubusers.feature.users.data.local.dao.UserDetailDao
import com.example.githubusers.feature.users.data.local.dao.UserSummaryDao
import com.example.githubusers.feature.users.data.local.entity.RemoteKeyEntity
import com.example.githubusers.feature.users.data.local.entity.RepositoryEntity
import com.example.githubusers.feature.users.data.local.entity.UserDetailEntity
import com.example.githubusers.feature.users.data.local.entity.UserSummaryEntity

/**
 * Unified Room database for user list and detail caching.
 */
@Database(
    entities = [
        UserSummaryEntity::class,
        RemoteKeyEntity::class,
        UserDetailEntity::class,
        RepositoryEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class UsersDatabase : RoomDatabase() {
    abstract fun userSummaryDao(): UserSummaryDao

    abstract fun remoteKeyDao(): RemoteKeyDao

    abstract fun userDetailDao(): UserDetailDao

    abstract fun repositoryDao(): RepositoryDao

    companion object {
        const val DATABASE_NAME: String = "user_list_database"

        val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `user_details` (" +
                        "`login` TEXT NOT NULL, " +
                        "`id` INTEGER NOT NULL, " +
                        "`avatarUrl` TEXT NOT NULL, " +
                        "`htmlUrl` TEXT NOT NULL, " +
                        "`name` TEXT, " +
                        "`company` TEXT, " +
                        "`blog` TEXT, " +
                        "`location` TEXT, " +
                        "`email` TEXT, " +
                        "`bio` TEXT, " +
                        "`twitterUsername` TEXT, " +
                        "`publicRepos` INTEGER NOT NULL, " +
                        "`publicGists` INTEGER NOT NULL, " +
                        "`followers` INTEGER NOT NULL, " +
                        "`following` INTEGER NOT NULL, " +
                        "`createdAt` INTEGER NOT NULL, " +
                        "`updatedAt` INTEGER NOT NULL, " +
                        "`type` TEXT NOT NULL, " +
                        "`siteAdmin` INTEGER NOT NULL, " +
                        "`hireable` INTEGER, " +
                        "`cachedAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`login`)" +
                        ")"
                )

                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `repositories` (" +
                        "`id` INTEGER NOT NULL, " +
                        "`ownerLogin` TEXT NOT NULL, " +
                        "`name` TEXT NOT NULL, " +
                        "`fullName` TEXT NOT NULL, " +
                        "`description` TEXT, " +
                        "`htmlUrl` TEXT NOT NULL, " +
                        "`language` TEXT, " +
                        "`stargazersCount` INTEGER NOT NULL, " +
                        "`watchersCount` INTEGER NOT NULL, " +
                        "`forksCount` INTEGER NOT NULL, " +
                        "`openIssuesCount` INTEGER NOT NULL, " +
                        "`isPrivate` INTEGER NOT NULL, " +
                        "`isFork` INTEGER NOT NULL, " +
                        "`createdAt` INTEGER NOT NULL, " +
                        "`updatedAt` INTEGER NOT NULL, " +
                        "`pushedAt` INTEGER, " +
                        "`size` INTEGER NOT NULL, " +
                        "`defaultBranch` TEXT NOT NULL, " +
                        "`topics` TEXT NOT NULL, " +
                        "`licenseKey` TEXT, " +
                        "`licenseName` TEXT, " +
                        "`visibility` TEXT NOT NULL, " +
                        "`cachedAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`id`)" +
                        ")"
                )
            }
        }
    }
}
