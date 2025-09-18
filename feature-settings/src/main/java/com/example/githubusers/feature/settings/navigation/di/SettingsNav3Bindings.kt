package com.example.githubusers.feature.settings.navigation.di

import com.example.githubusers.feature.settings.navigation.SettingsFeatureDeepLinkHandler
import com.example.githubusers.feature.settings.navigation.SettingsFeatureDestinationProvider
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
abstract class SettingsNav3Bindings {
    @Binds
    @IntoSet
    @Singleton
    abstract fun bindSettingsProvider(impl: SettingsFeatureDestinationProvider): FeatureDestinationProvider

    @Binds
    @IntoSet
    @Singleton
    abstract fun bindSettingsHandler(impl: SettingsFeatureDeepLinkHandler): FeatureDeepLinkHandler
}
