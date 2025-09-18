package com.example.githubusers.feature.users.detail.di

import com.example.githubusers.feature.users.detail.domain.repository.UserDetailRepository
import com.example.githubusers.feature.users.detail.domain.usecase.FollowUserUseCase
import com.example.githubusers.feature.users.detail.domain.usecase.GetUserDetailUseCase
import com.example.githubusers.feature.users.detail.domain.usecase.GetUserRepositoriesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Hilt module for domain layer dependencies.
 */
@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {
    @Provides
    @ViewModelScoped
    fun provideGetUserDetailUseCase(repository: UserDetailRepository): GetUserDetailUseCase =
        GetUserDetailUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideGetUserRepositoriesUseCase(repository: UserDetailRepository): GetUserRepositoriesUseCase =
        GetUserRepositoriesUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideFollowUserUseCase(repository: UserDetailRepository): FollowUserUseCase = FollowUserUseCase(repository)
}
