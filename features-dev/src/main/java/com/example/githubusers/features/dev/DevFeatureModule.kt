package com.example.githubusers.features.dev

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dev-specific feature module for dependency injection
 * Provides dev-only features and configurations
 */
@Module
@InstallIn(SingletonComponent::class)
object DevFeatureModule {
    @Provides
    @Singleton
    fun provideFeatureFlags(): FeatureFlags =
        FeatureFlags(
            enableDebugMenu = true,
            enableNetworkLogging = true,
            enableStrictMode = true,
            enableLeakCanary = true,
            enableMockMode = false,
        )
}

/**
 * Feature flags for dev flavor
 */
data class FeatureFlags(
    val enableDebugMenu: Boolean,
    val enableNetworkLogging: Boolean,
    val enableStrictMode: Boolean,
    val enableLeakCanary: Boolean,
    val enableMockMode: Boolean,
)
