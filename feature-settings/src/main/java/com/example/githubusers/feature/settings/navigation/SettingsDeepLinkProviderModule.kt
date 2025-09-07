package com.example.githubusers.feature.settings.navigation

import com.example.githubusers.navigation.api.DeepLinkHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

/**
 * Hilt module to expose the Settings feature deep link handler to the app.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsDeepLinkProviderModule {
    @Binds
    @IntoSet
    abstract fun bindSettingsDeepLinkHandler(handler: SettingsModuleDeepLinkHandler): DeepLinkHandler
}
