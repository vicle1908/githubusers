package com.example.githubusers.features.prod

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Production-specific feature module for dependency injection
 * Provides production-only features and configurations
 */
@Module
@InstallIn(SingletonComponent::class)
object ProdFeatureModule {
    @Provides
    @Singleton
    fun provideFeatureFlags(): FeatureFlags =
        FeatureFlags(
            enableAnalytics = true,
            enableCrashReporting = true,
            enablePerformanceMonitoring = true,
            enableRemoteConfig = true,
            apiEndpoint = "https://api.github.com",
        )
}

/**
 * Feature flags for production flavor
 */
data class FeatureFlags(
    val enableAnalytics: Boolean,
    val enableCrashReporting: Boolean,
    val enablePerformanceMonitoring: Boolean,
    val enableRemoteConfig: Boolean,
    val apiEndpoint: String,
)
