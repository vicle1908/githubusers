package com.example.githubusers.feature.users.di

import com.example.githubusers.feature.users.navigation.UserDeepLinkHandler
import com.example.githubusers.navigation.api.DeepLinkHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

/**
 * Hilt module to expose the Users feature deep link handler to the app.
 * Owned by feature-users per feature-based architecture.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class UserNavigationModule {
    @Binds
    @IntoSet
    abstract fun bindUserDeepLinkHandler(handler: UserDeepLinkHandler): DeepLinkHandler
}
