package com.example.githubusers.feature.users.list.di

import com.example.githubusers.feature.users.list.domain.repository.UserListRepository
import com.example.githubusers.feature.users.list.domain.usecase.GetUsersUseCase
import com.example.githubusers.feature.users.list.domain.usecase.SearchUsersUseCase
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
    fun provideGetUsersUseCase(repository: UserListRepository): GetUsersUseCase = GetUsersUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideSearchUsersUseCase(repository: UserListRepository): SearchUsersUseCase = SearchUsersUseCase(repository)
}
