package com.example.githubusers.feature.users.detail.di

import android.content.Context
import androidx.room.Room
import com.example.githubusers.feature.users.detail.data.local.UserDetailDatabase
import com.example.githubusers.feature.users.detail.data.local.dao.RepositoryDao
import com.example.githubusers.feature.users.detail.data.local.dao.UserDetailDao
import com.example.githubusers.feature.users.detail.data.remote.api.UserDetailRemoteDataSource
import com.example.githubusers.feature.users.detail.data.remote.api.UserDetailRemoteDataSourceImpl
import com.example.githubusers.feature.users.detail.data.repository.UserDetailRepositoryImpl
import com.example.githubusers.feature.users.detail.domain.repository.UserDetailRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for data layer dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    abstract fun bindUserDetailRepository(impl: UserDetailRepositoryImpl): UserDetailRepository

    @Binds
    abstract fun bindUserDetailRemoteDataSource(impl: UserDetailRemoteDataSourceImpl): UserDetailRemoteDataSource

    companion object {
        @Provides
        @Singleton
        fun provideUserDetailDatabase(
            @ApplicationContext context: Context,
        ): UserDetailDatabase =
            Room
                .databaseBuilder(
                    context,
                    UserDetailDatabase::class.java,
                    UserDetailDatabase.DATABASE_NAME,
                ).build()

        @Provides
        @Singleton
        fun provideUserDetailDao(database: UserDetailDatabase): UserDetailDao = database.userDetailDao()

        @Provides
        @Singleton
        fun provideRepositoryDao(database: UserDetailDatabase): RepositoryDao = database.repositoryDao()
    }
}
