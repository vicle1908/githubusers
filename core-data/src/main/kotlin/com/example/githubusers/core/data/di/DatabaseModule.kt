package com.example.githubusers.core.data.di

import android.content.Context
import androidx.room.Room
import com.example.githubusers.core.data.local.dao.UserDao
import com.example.githubusers.core.data.local.dao.UserDetailDao
import com.example.githubusers.core.data.local.database.GitHubUsersDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): GitHubUsersDatabase =
        Room
            .databaseBuilder(context, GitHubUsersDatabase::class.java, GitHubUsersDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideUserDao(db: GitHubUsersDatabase): UserDao = db.userDao()

    @Provides
    @Singleton
    fun provideUserDetailDao(db: GitHubUsersDatabase): UserDetailDao = db.userDetailDao()
}
