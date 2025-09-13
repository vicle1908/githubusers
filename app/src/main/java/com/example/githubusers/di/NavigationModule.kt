package com.example.githubusers.di

// Removed ModuleNavigator import - using DeepLinkDispatcher directly for simplicity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/** Dagger module for app-level coordination only - features handle their own navigation */
@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob())
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope
