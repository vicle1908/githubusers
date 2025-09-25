package com.example.githubusers.feature.repository.di

import com.example.githubusers.feature.repository.navigation.RepositoryFeatureDeepLinkHandler
import com.example.githubusers.feature.repository.navigation.RepositoryFeatureDestinationProvider
import com.example.githubusers.navigation.api.FeatureDeepLinkHandler
import com.example.githubusers.navigation.api.FeatureDestinationProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object RepositoryNav3Bindings {

    @Provides
    @IntoSet
    fun provideRepositoryProvider(): FeatureDestinationProvider = RepositoryFeatureDestinationProvider()

    @Provides
    @IntoSet
    fun provideRepositoryHandler(): FeatureDeepLinkHandler = RepositoryFeatureDeepLinkHandler()
}
