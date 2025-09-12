package com.example.githubusers.feature.search.navigation.di

import com.example.githubusers.feature.search.navigation.SearchFeatureDeepLinkHandler
import com.example.githubusers.feature.search.navigation.SearchFeatureDestinationProvider
import com.example.githubusers.navigation.api.FeatureDeepLinkHandler
import com.example.githubusers.navigation.api.FeatureDestinationProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchNav3Bindings {
    @Binds
    @IntoSet
    @Singleton
    abstract fun bindSearchProvider(impl: SearchFeatureDestinationProvider): FeatureDestinationProvider

    @Binds
    @IntoSet
    @Singleton
    abstract fun bindSearchHandler(impl: SearchFeatureDeepLinkHandler): FeatureDeepLinkHandler
}
