package com.example.githubusers.feature.repository.navigation.di

import com.example.githubusers.feature.repository.navigation.repositoryNavigationTab
import com.example.githubusers.navigation.api.NavigationTab
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object RepositoryNavigationBindings {

    @Provides
    @IntoSet
    fun provideRepositoryTab(): NavigationTab = repositoryNavigationTab()
}
