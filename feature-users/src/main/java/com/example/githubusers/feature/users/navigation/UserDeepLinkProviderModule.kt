package com.example.githubusers.feature.users.navigation

import com.example.githubusers.navigation.api.DeepLinkHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

/**
 * Hilt provider for the Users feature deep link handler.
 * Exposes the handler to the app via DI without depending on navigation-impl.
 */
@Module
@InstallIn(SingletonComponent::class)
object UserDeepLinkProviderModule {
    @Provides
    @IntoSet
    @Singleton
    fun provideUserDeepLinkHandler(): DeepLinkHandler = UserDeepLinkHandler()
}
