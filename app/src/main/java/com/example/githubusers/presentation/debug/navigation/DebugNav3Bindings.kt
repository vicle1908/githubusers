package com.example.githubusers.presentation.debug.navigation

import com.example.githubusers.navigation.api.FeatureDeepLinkHandler
import com.example.githubusers.navigation.api.FeatureDestinationProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object DebugNav3Bindings {

    @Provides
    @IntoSet
    fun provideDebugDestination(): FeatureDestinationProvider = DebugFeatureDestinationProvider()

    @Provides
    @IntoSet
    fun provideDebugDeepLinkHandler(): FeatureDeepLinkHandler = DebugFeatureDeepLinkHandler()
}
