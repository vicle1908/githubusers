package com.example.githubusers.di

import android.content.Context
import com.example.githubusers.navigation.api.DeepLinkHandler
import com.example.githubusers.navigation.impl.BackStackStore
import com.example.githubusers.navigation.impl.DeepLinkOwnershipSource
import com.example.githubusers.navigation.impl.DefaultDestinationResolver
import com.example.githubusers.navigation.impl.DestinationResolver
import com.example.githubusers.navigation.impl.NavigationTelemetry
import com.example.githubusers.navigation.impl.SharedPrefsBackStackStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * App-level navigation module that provides navigation dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppNavigationModule {
    @Provides
    @Singleton
    fun provideDestinationResolver(
        handlers: Set<@JvmSuppressWildcards DeepLinkHandler>,
        ownershipSource: DeepLinkOwnershipSource,
        telemetry: NavigationTelemetry,
    ): DestinationResolver = DefaultDestinationResolver(handlers, ownershipSource, telemetry)

    @Provides
    @Singleton
    fun provideBackStackStore(
        @ApplicationContext context: Context,
    ): BackStackStore = SharedPrefsBackStackStore(context)
}
