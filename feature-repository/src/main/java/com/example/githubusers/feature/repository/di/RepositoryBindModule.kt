package com.example.githubusers.feature.repository.di

import com.example.githubusers.feature.repository.data.repository.RepositoryRepositoryImpl
import com.example.githubusers.feature.repository.domain.repository.RepositoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindModule {

    @Binds
    @Singleton
    abstract fun bindRepositoryRepository(impl: RepositoryRepositoryImpl): RepositoryRepository
}
