package com.example.githubusers.feature.users.list.di

import com.example.githubusers.feature.users.list.domain.repository.UserListRepository
import com.example.githubusers.feature.users.list.domain.usecase.ObserveUserListUseCase
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
    fun provideObserveUserListUseCase(repository: UserListRepository): ObserveUserListUseCase =
        ObserveUserListUseCase(repository)
}
