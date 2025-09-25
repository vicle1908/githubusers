package com.example.githubusers.core.ui.performance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.util.Locale
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

        Timber.tag(PERFORMANCE_TAG).d(
            "🎨 Screen composition: $screenName took ${compositionTime.formatMs()} ms"
        )

        if (compositionTime > 16.0) { // > 60fps threshold
            Timber.tag(PERFORMANCE_TAG).w(
                "⚠️ Slow composition detected: $screenName (${compositionTime.formatMs()} ms)"
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
        val memoryDeltaMb = (finalMemory - initialMemory).toDouble() / (1024.0 * 1024.0)

        Timber.tag(PERFORMANCE_TAG).d(
            "🧠 Memory usage: $operation used ${memoryDeltaMb.formatMb()} MB"
        )

        if (memoryDeltaMb > 10.0) { // > 10MB threshold
            Timber.tag(PERFORMANCE_TAG).w(
                "⚠️ High memory usage: $operation (${memoryDeltaMb.formatMb()} MB)"
            )
        }
    }

    /**
     * Track navigation performance
     */
    fun trackNavigation(from: String, to: String, duration: Long) {
        if (!isEnabled) return

        val navigationTime = duration / 1_000_000.0 // Convert to ms

        Timber.tag(PERFORMANCE_TAG).d(
            "🧭 Navigation: $from → $to took ${navigationTime.formatMs()} ms"
        )

        if (navigationTime > 300.0) { // > 300ms threshold
            Timber.tag(PERFORMANCE_TAG).w(
                "⚠️ Slow navigation: $from → $to (${navigationTime.formatMs()} ms)"
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

        Timber.tag(PERFORMANCE_TAG).d(
            "$status $cacheStatus: $method $endpoint took ${requestTime.formatMs()} ms"
        )

        if (requestTime > 2000.0 && !cacheHit) { // > 2s threshold for network
            Timber.tag(PERFORMANCE_TAG).w(
                "⚠️ Slow network request: $method $endpoint (${requestTime.formatMs()} ms)"
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
        Timber.tag(PERFORMANCE_TAG).i(
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
    val memoryDeltaMb = (finalMemory - initialMemory).toDouble() / (1024.0 * 1024.0)

    Timber.tag(PERFORMANCE_TAG).d(
        "🧠 Memory usage: $operation used ${memoryDeltaMb.formatMb()} MB"
    )

    if (memoryDeltaMb > 10.0) { // > 10MB threshold
        Timber.tag(PERFORMANCE_TAG).w(
            "⚠️ High memory usage: $operation (${memoryDeltaMb.formatMb()} MB)"
        )
    }

    return result
}

@PublishedApi
internal const val PERFORMANCE_TAG: String = "Performance"

@PublishedApi
internal fun Double.formatMs(): String = String.format(Locale.US, "%.2f", this)

@PublishedApi
internal fun Double.formatMb(): String = String.format(Locale.US, "%.2f", this)
