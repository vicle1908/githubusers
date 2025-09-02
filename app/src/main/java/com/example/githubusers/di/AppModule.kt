package com.example.githubusers.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * App module that provides application-level dependencies only.
 *
 * This module follows the orchestrator pattern where the app module
 * only coordinates and provides application-level concerns, while
 * business logic is handled by feature and core modules.
 *
 * Network logic is provided by core-data module.
 * Image loading is provided by core-ui module.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // Application-level dependencies only
    // Network logic moved to core-data module
    // Image loading moved to core-ui module
}