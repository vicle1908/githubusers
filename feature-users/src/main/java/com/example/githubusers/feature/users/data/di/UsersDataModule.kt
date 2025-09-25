package com.example.githubusers.feature.users.data.di

import android.content.Context
import androidx.room.Room
import com.example.githubusers.core.ui.performance.PerformanceMonitor
import com.example.githubusers.feature.users.data.local.UsersDatabase
import com.example.githubusers.feature.users.data.local.dao.RemoteKeyDao
import com.example.githubusers.feature.users.data.local.dao.RepositoryDao
import com.example.githubusers.feature.users.data.local.dao.UserDetailDao
import com.example.githubusers.feature.users.data.local.dao.UserSummaryDao
import com.example.githubusers.feature.users.data.remote.UserListApiService
import com.example.githubusers.feature.users.data.remote.api.UserDetailRemoteDataSource
import com.example.githubusers.feature.users.data.remote.api.UserDetailRemoteDataSourceImpl
import com.example.githubusers.feature.users.data.repository.UsersRepositoryImpl
import com.example.githubusers.feature.users.detail.data.repository.UserDetailRepositoryImpl
import com.example.githubusers.feature.users.detail.domain.repository.UserDetailRepository
import com.example.githubusers.feature.users.domain.repository.UserRepository
import com.example.githubusers.feature.users.list.data.repository.UserListRepositoryImpl
import com.example.githubusers.feature.users.list.domain.repository.UserListRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UsersDataModule {

    @Provides
    @Singleton
    fun provideUsersDatabase(@ApplicationContext context: Context): UsersDatabase = Room
        .databaseBuilder(context, UsersDatabase::class.java, UsersDatabase.DATABASE_NAME)
        .addMigrations(UsersDatabase.MIGRATION_1_2)
        .build()

    @Provides
    fun provideUserSummaryDao(database: UsersDatabase): UserSummaryDao = database.userSummaryDao()

    @Provides
    fun provideRemoteKeyDao(database: UsersDatabase): RemoteKeyDao = database.remoteKeyDao()

    @Provides
    fun provideUserDetailDao(database: UsersDatabase): UserDetailDao = database.userDetailDao()

    @Provides
    fun provideRepositoryDao(database: UsersDatabase): RepositoryDao = database.repositoryDao()

    @Provides
    @Singleton
    fun provideUserListRepository(impl: UserListRepositoryImpl): UserListRepository = impl

    @Provides
    @Singleton
    fun provideUserDetailRepository(impl: UserDetailRepositoryImpl): UserDetailRepository = impl

    @Provides
    @Singleton
    fun provideUserListApiService(client: HttpClient, performanceMonitor: PerformanceMonitor): UserListApiService =
        UserListApiService(client, performanceMonitor)

    @Provides
    @Singleton
    fun provideUserDetailRemoteDataSource(impl: UserDetailRemoteDataSourceImpl): UserDetailRemoteDataSource = impl

    @Provides
    @Singleton
    fun provideUserRepository(impl: UsersRepositoryImpl): UserRepository = impl
}
