package com.example.githubusers.feature.search.navigation.di

import com.example.githubusers.feature.search.navigation.SearchFeatureDeepLinkHandler
import com.example.githubusers.feature.search.navigation.SearchFeatureDestinationProvider
import com.example.githubusers.navigation.api.FeatureDeepLinkHandler
import com.example.githubusers.navigation.api.FeatureDestinationProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchNav3Bindings {
    @Provides
    @IntoSet
    @Singleton
    fun provideSearchProvider(): FeatureDestinationProvider = SearchFeatureDestinationProvider()

    @Provides
    @IntoSet
    @Singleton
    fun provideSearchHandler(): FeatureDeepLinkHandler = SearchFeatureDeepLinkHandler()
}
