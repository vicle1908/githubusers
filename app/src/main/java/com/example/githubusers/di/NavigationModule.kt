package com.example.githubusers.di

import com.example.githubusers.navigation.impl.DeepLinkDispatcher
import com.example.githubusers.presentation.navigation.deeplink.ModuleNavigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Dagger module for navigation and deep link components */
@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {
    @Provides
    @Singleton
    fun provideModuleNavigator(dispatcher: DeepLinkDispatcher): ModuleNavigator =
        ModuleNavigator(dispatcher)
}
