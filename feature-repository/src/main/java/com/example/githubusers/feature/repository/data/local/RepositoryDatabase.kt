package com.example.githubusers.feature.repository.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [RepositoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RepositoryDatabase : RoomDatabase() {
    abstract fun repositoryDao(): RepositoryDao

    companion object {
        const val DATABASE_NAME: String = "repository_cache.db"
    }
}
