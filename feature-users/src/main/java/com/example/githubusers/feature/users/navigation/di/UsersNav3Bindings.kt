package com.example.githubusers.feature.users.navigation.di

import com.example.githubusers.feature.users.navigation.UsersFeatureDeepLinkHandler
import com.example.githubusers.feature.users.navigation.UsersFeatureDestinationProvider
import com.example.githubusers.feature.users.navigation.usersNavigationTab
import com.example.githubusers.navigation.api.FeatureDeepLinkHandler
import com.example.githubusers.navigation.api.FeatureDestinationProvider
import com.example.githubusers.navigation.api.NavigationTab
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class UsersNav3Bindings {

    @Binds
    @IntoSet
    abstract fun bindUsersFeatureDestination(provider: UsersFeatureDestinationProvider): FeatureDestinationProvider

    @Binds
    @IntoSet
    abstract fun bindUsersFeatureDeepLinkHandler(handler: UsersFeatureDeepLinkHandler): FeatureDeepLinkHandler

    companion object {
        @Provides
        @IntoSet
        fun provideUsersNavigationTab(): NavigationTab = usersNavigationTab()
    }
}
