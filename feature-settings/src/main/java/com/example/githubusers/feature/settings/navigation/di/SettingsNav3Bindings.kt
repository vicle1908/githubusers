package com.example.githubusers.feature.settings.navigation.di

import com.example.githubusers.feature.settings.navigation.SettingsFeatureDeepLinkHandler
import com.example.githubusers.feature.settings.navigation.SettingsFeatureDestinationProvider
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
object SettingsNav3Bindings {
    @Provides
    @IntoSet
    @Singleton
    fun provideSettingsProvider(): FeatureDestinationProvider = SettingsFeatureDestinationProvider()

    @Provides
    @IntoSet
    @Singleton
    fun provideSettingsHandler(): FeatureDeepLinkHandler = SettingsFeatureDeepLinkHandler()
}
