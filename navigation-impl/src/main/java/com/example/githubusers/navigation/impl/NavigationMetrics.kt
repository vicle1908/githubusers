package com.example.githubusers.navigation.impl

/**
 * Telemetry placeholders for Navigation 3 persistence. No-op by default.
 */
interface NavigationMetrics {
    fun onPersistWriteSuccess(
        entryCount: Int,
        totalBytes: Int,
    ) {}

    fun onPersistWriteFailure(error: Throwable?) {}

    fun onPersistReadSuccess(entryCount: Int) {}

    fun onPersistReadFailure(reason: String) {}
}

object NoOpNavigationMetrics : NavigationMetrics
