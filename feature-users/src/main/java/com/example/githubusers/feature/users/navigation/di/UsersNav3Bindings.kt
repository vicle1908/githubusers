package com.example.githubusers.feature.users.navigation.di

import com.example.githubusers.feature.users.navigation.UsersFeatureDeepLinkHandler
import com.example.githubusers.feature.users.navigation.UsersFeatureDestinationProvider
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
abstract class UsersNav3Bindings {
    @Binds
    @IntoSet
    @Singleton
    abstract fun bindUsersProvider(impl: UsersFeatureDestinationProvider): FeatureDestinationProvider

    @Binds
    @IntoSet
    @Singleton
    abstract fun bindUsersHandler(impl: UsersFeatureDeepLinkHandler): FeatureDeepLinkHandler
}
