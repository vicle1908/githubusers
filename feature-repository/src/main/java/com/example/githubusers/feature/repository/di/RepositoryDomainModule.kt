package com.example.githubusers.feature.repository.di

import com.example.githubusers.feature.repository.domain.repository.RepositoryRepository
import com.example.githubusers.feature.repository.domain.usecase.GetRepositoryDetailUseCase
import com.example.githubusers.feature.repository.domain.usecase.ObserveRepositorySearchUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryDomainModule {

    @Provides
    fun provideObserveRepositorySearchUseCase(repository: RepositoryRepository): ObserveRepositorySearchUseCase =
        ObserveRepositorySearchUseCase(repository)

    @Provides
    fun provideGetRepositoryDetailUseCase(repository: RepositoryRepository): GetRepositoryDetailUseCase =
        GetRepositoryDetailUseCase(repository)
}
