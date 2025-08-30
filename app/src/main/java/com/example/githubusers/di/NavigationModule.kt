package com.example.githubusers.di

import com.example.githubusers.navigation.api.DeepLinkHandler
import com.example.githubusers.navigation.api.Navigation3Controller
import com.example.githubusers.presentation.navigation.deeplink.ModuleNavigator
import com.example.githubusers.presentation.navigation.deeplink.SettingsModuleDeepLinkHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

/**
 * Dagger module for navigation and deep link components
 */
@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {
    @Provides
    @Singleton
    fun provideModuleNavigator(
        navigation3Controller: Navigation3Controller,
    ): ModuleNavigator =
        ModuleNavigator(navigation3Controller)

    // Contribute Settings deep link handler to the Navigation API set
    @Provides
    @IntoSet
    @Singleton
    fun provideSettingsDeepLinkHandler(handler: SettingsModuleDeepLinkHandler): DeepLinkHandler = handler
}
