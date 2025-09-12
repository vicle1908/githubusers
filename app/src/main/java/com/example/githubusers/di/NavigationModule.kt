package com.example.githubusers.di

import com.example.githubusers.navigation.impl.DeepLinkDispatcher
import com.example.githubusers.presentation.navigation.deeplink.ModuleNavigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

/** Dagger module for navigation and deep link components */
@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {
    
    @Provides
    @Singleton
    fun provideModuleNavigator(dispatcher: DeepLinkDispatcher): ModuleNavigator =
        ModuleNavigator(dispatcher)
    
    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationCoroutineScope(): CoroutineScope =
        CoroutineScope(SupervisorJob())
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope
