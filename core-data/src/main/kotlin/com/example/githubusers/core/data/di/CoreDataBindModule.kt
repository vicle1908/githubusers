package com.example.githubusers.core.data.di

import com.example.githubusers.core.data.local.datasource.UserDetailLocalDataSource
import com.example.githubusers.core.data.local.datasource.UserDetailLocalDataSourceImpl
import com.example.githubusers.core.data.local.datasource.UserLocalDataSource
import com.example.githubusers.core.data.local.datasource.UserLocalDataSourceImpl
import com.example.githubusers.core.data.repository.UserRepositoryImpl
import com.example.githubusers.core.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreDataBindModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindUserLocalDataSource(impl: UserLocalDataSourceImpl): UserLocalDataSource

    @Binds
    @Singleton
    abstract fun bindUserDetailLocalDataSource(impl: UserDetailLocalDataSourceImpl): UserDetailLocalDataSource
}
