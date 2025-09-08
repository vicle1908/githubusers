package com.example.githubusers.feature.search.di

import com.example.githubusers.feature.search.navigation.SearchDeepLinkHandler
import com.example.githubusers.navigation.api.DeepLinkHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

/**
 * Hilt module to expose the Search feature deep link handler to the app.
 * Owned by feature-search per feature-based architecture.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SearchNavigationModule {
    @Binds
    @IntoSet
    abstract fun bindSearchDeepLinkHandler(handler: SearchDeepLinkHandler): DeepLinkHandler
}
