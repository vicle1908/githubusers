package com.example.githubusers.feature.users.list.di

import android.content.Context
import androidx.room.Room
import com.example.githubusers.core.ui.performance.PerformanceMonitor
import com.example.githubusers.feature.users.list.data.local.UserListDatabase
import com.example.githubusers.feature.users.list.data.local.UserListLocalDataSource
import com.example.githubusers.feature.users.list.data.local.UserListLocalDataSourceImpl
import com.example.githubusers.feature.users.list.data.local.dao.RemoteKeyDao
import com.example.githubusers.feature.users.list.data.local.dao.UserSummaryDao
import com.example.githubusers.feature.users.list.data.remote.UserListApiService
import com.example.githubusers.feature.users.list.data.remote.UserListRemoteDataSource
import com.example.githubusers.feature.users.list.data.remote.UserListRemoteDataSourceImpl
import com.example.githubusers.feature.users.list.data.repository.UserListRepositoryImpl
import com.example.githubusers.feature.users.list.domain.repository.UserListRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

/**
 * Hilt module for data layer dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    abstract fun bindUserListRepository(impl: UserListRepositoryImpl): UserListRepository

    @Binds
    abstract fun bindUserListLocalDataSource(impl: UserListLocalDataSourceImpl): UserListLocalDataSource

    @Binds
    abstract fun bindUserListRemoteDataSource(impl: UserListRemoteDataSourceImpl): UserListRemoteDataSource

    companion object {
        @Provides
        @Singleton
        fun provideUserListDatabase(
            @ApplicationContext context: Context,
        ): UserListDatabase =
            Room
                .databaseBuilder(
                    context,
                    UserListDatabase::class.java,
                    UserListDatabase.DATABASE_NAME,
                ).build()

        @Provides
        @Singleton
        fun provideUserSummaryDao(database: UserListDatabase): UserSummaryDao = database.userSummaryDao()

        @Provides
        @Singleton
        fun provideRemoteKeyDao(database: UserListDatabase): RemoteKeyDao = database.remoteKeyDao()

        @Provides
        @Singleton
        fun provideUserListApiService(
            client: HttpClient,
            performanceMonitor: PerformanceMonitor,
        ): UserListApiService = UserListApiService(client, performanceMonitor)
    }
}
