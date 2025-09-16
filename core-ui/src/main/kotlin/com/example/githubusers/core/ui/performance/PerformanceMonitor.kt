package com.example.githubusers.core.ui.performance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

/**
 * Performance monitoring utility for tracking app performance metrics
 *
 * Tracks:
 * - Screen composition time
 * - Memory usage patterns
 * - Navigation performance
 * - Network request latency
 */
@Singleton
class PerformanceMonitor @Inject constructor() {

    private var isEnabled = true

    /**
     * Track screen composition performance
     */
    fun trackComposition(screenName: String, startTime: Long = System.nanoTime()) {
        if (!isEnabled) return

        val compositionTime = (System.nanoTime() - startTime) / 1_000_000.0

        Timber.tag("Performance").d(
            "🎨 Screen composition: $screenName took %.2f ms",
            compositionTime
        )

        if (compositionTime > 16.0) { // > 60fps threshold
            Timber.tag("Performance").w(
                "⚠️ Slow composition detected: $screenName (%.2f ms)",
                compositionTime
            )
        }
    }

    /**
     * Track memory usage for specific operations
     */
    fun trackMemoryUsage(operation: String, block: () -> Unit) {
        if (!isEnabled) return

        val runtime = Runtime.getRuntime()
        val initialMemory = runtime.totalMemory() - runtime.freeMemory()

        block()

        val finalMemory = runtime.totalMemory() - runtime.freeMemory()
        val memoryDelta = (finalMemory - initialMemory) / (1024 * 1024) // Convert to MB

        Timber.tag("Performance").d(
            "🧠 Memory usage: $operation used %.2f MB",
            memoryDelta
        )

        if (memoryDelta > 10.0) { // > 10MB threshold
            Timber.tag("Performance").w(
                "⚠️ High memory usage: $operation (%.2f MB)",
                memoryDelta
            )
        }
    }

    /**
     * Track navigation performance
     */
    fun trackNavigation(from: String, to: String, duration: Long) {
        if (!isEnabled) return

        val navigationTime = duration / 1_000_000.0 // Convert to ms

        Timber.tag("Performance").d(
            "🧭 Navigation: $from → $to took %.2f ms",
            navigationTime
        )

        if (navigationTime > 300.0) { // > 300ms threshold
            Timber.tag("Performance").w(
                "⚠️ Slow navigation: $from → $to (%.2f ms)",
                navigationTime
            )
        }
    }

    /**
     * Track network request performance
     */
    fun trackNetworkRequest(
        endpoint: String,
        method: String,
        duration: Long,
        success: Boolean,
        cacheHit: Boolean = false
    ) {
        if (!isEnabled) return

        val requestTime = duration / 1_000_000.0 // Convert to ms
        val cacheStatus = if (cacheHit) "💾 CACHE HIT" else "🌐 NETWORK"
        val status = if (success) "✅" else "❌"

        Timber.tag("Performance").d(
            "$status $cacheStatus: $method $endpoint took %.2f ms",
            requestTime
        )

        if (requestTime > 2000.0 && !cacheHit) { // > 2s threshold for network
            Timber.tag("Performance").w(
                "⚠️ Slow network request: $method $endpoint (%.2f ms)",
                requestTime
            )
        }
    }

    /**
     * Generate performance report
     */
    fun generateReport(): PerformanceReport = PerformanceReport(
        isMonitoringEnabled = isEnabled,
        timestamp = System.currentTimeMillis()
    )

    /**
     * Enable/disable performance monitoring
     */
    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
        Timber.tag("Performance").i(
            "Performance monitoring ${if (enabled) "enabled" else "disabled"}"
        )
    }
}

/**
 * Composable function for tracking screen composition performance
 */
@Composable
fun TrackCompositionPerformance(screenName: String, monitor: PerformanceMonitor) {
    var startTime by remember { mutableLongStateOf(System.nanoTime()) }

    DisposableEffect(screenName) {
        startTime = System.nanoTime()

        onDispose {
            monitor.trackComposition(screenName, startTime)
        }
    }
}

/**
 * Performance report data class
 */
data class PerformanceReport(val isMonitoringEnabled: Boolean, val timestamp: Long)

/**
 * Extension function for easy memory tracking
 */
inline fun <T> PerformanceMonitor.withMemoryTracking(operation: String, crossinline block: () -> T): T {
    val runtime = Runtime.getRuntime()
    val initialMemory = runtime.totalMemory() - runtime.freeMemory()

    val result = block()

    val finalMemory = runtime.totalMemory() - runtime.freeMemory()
    val memoryDelta = (finalMemory - initialMemory) / (1024 * 1024) // Convert to MB

    Timber.tag("Performance").d(
        "🧠 Memory usage: $operation used %.2f MB",
        memoryDelta
    )

    if (memoryDelta > 10.0) { // > 10MB threshold
        Timber.tag("Performance").w(
            "⚠️ High memory usage: $operation (%.2f MB)",
            memoryDelta
        )
    }

    return result
}
