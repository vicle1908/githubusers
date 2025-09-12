package com.example.githubusers.navigation.impl

import android.util.Log
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Performance monitoring utility for Navigation 3 operations.
 * Tracks navigation performance metrics, provider resolution times, and memory usage.
 */
@Singleton
class NavigationPerformanceMonitor @Inject constructor() {
    
    private val _performanceMetrics = MutableStateFlow(NavigationMetrics())
    val performanceMetrics: StateFlow<NavigationMetrics> = _performanceMetrics.asStateFlow()
    
    private val resolutionTimes = mutableListOf<Long>()
    private val maxResolutionTimes = 100 // Keep last 100 measurements
    
    /**
     * Record a provider resolution operation with timing.
     */
    fun recordProviderResolution(key: NavKey, duration: Long, cacheHit: Boolean) {
        val current = _performanceMetrics.value
        val updated = current.copy(
            totalResolutions = current.totalResolutions + 1,
            cacheHits = current.cacheHits + if (cacheHit) 1 else 0,
            slowResolutions = current.slowResolutions + if (duration > 1_000_000) 1 else 0,
            totalResolutionTime = current.totalResolutionTime + duration,
            averageResolutionTime = calculateAverageResolutionTime(duration)
        )
        
        _performanceMetrics.value = updated
        
        // Log slow resolutions for debugging
        if (duration > 1_000_000) { // > 1ms
            Log.w("NavigationPerformance", "Slow provider resolution: ${duration}ns for $key")
        }
        
        // Log performance warnings
        if (updated.totalResolutions % 10 == 0) {
            logPerformanceSummary(updated)
        }
    }
    
    /**
     * Record memory usage metrics.
     */
    fun recordMemoryUsage(providerCacheSize: Int, entryPoolSize: Int) {
        val current = _performanceMetrics.value
        _performanceMetrics.value = current.copy(
            providerCacheSize = providerCacheSize,
            entryPoolSize = entryPoolSize,
            lastMemoryUpdate = System.currentTimeMillis()
        )
    }
    
    /**
     * Record navigation operation timing.
     */
    fun recordNavigationOperation(operation: String, duration: Long) {
        val current = _performanceMetrics.value
        val updated = when (operation) {
            "deepLinkResolution" -> current.copy(
                deepLinkResolutions = current.deepLinkResolutions + 1,
                totalDeepLinkTime = current.totalDeepLinkTime + duration
            )
            "entryCreation" -> current.copy(
                entryCreations = current.entryCreations + 1,
                totalEntryCreationTime = current.totalEntryCreationTime + duration
            )
            else -> current
        }
        
        _performanceMetrics.value = updated
        
        if (duration > 5_000_000) { // > 5ms
            Log.w("NavigationPerformance", "Slow $operation: ${duration}ns")
        }
    }
    
    /**
     * Get current performance summary for logging.
     */
    fun getPerformanceSummary(): String {
        val metrics = _performanceMetrics.value
        val cacheHitRate = if (metrics.totalResolutions > 0) {
            (metrics.cacheHits.toDouble() / metrics.totalResolutions * 100)
        } else 0.0
        
        return buildString {
            appendLine("=== Navigation Performance Summary ===")
            appendLine("Total Resolutions: ${metrics.totalResolutions}")
            appendLine("Cache Hit Rate: ${String.format("%.1f%%", cacheHitRate)}")
            appendLine("Average Resolution Time: ${String.format("%.2fms", metrics.averageResolutionTime / 1_000_000.0)}")
            appendLine("Slow Resolutions: ${metrics.slowResolutions}")
            appendLine("Provider Cache Size: ${metrics.providerCacheSize}")
            appendLine("Entry Pool Size: ${metrics.entryPoolSize}")
            appendLine("Deep Link Resolutions: ${metrics.deepLinkResolutions}")
            appendLine("Entry Creations: ${metrics.entryCreations}")
            appendLine("=====================================")
        }
    }
    
    /**
     * Reset all performance metrics.
     */
    fun resetMetrics() {
        _performanceMetrics.value = NavigationMetrics()
        resolutionTimes.clear()
        Log.d("NavigationPerformance", "Performance metrics reset")
    }
    
    private fun calculateAverageResolutionTime(newDuration: Long): Long {
        resolutionTimes.add(newDuration)
        if (resolutionTimes.size > maxResolutionTimes) {
            resolutionTimes.removeAt(0)
        }
        return resolutionTimes.average().toLong()
    }
    
    private fun logPerformanceSummary(metrics: NavigationMetrics) {
        val cacheHitRate = if (metrics.totalResolutions > 0) {
            (metrics.cacheHits.toDouble() / metrics.totalResolutions * 100)
        } else 0.0
        
        Log.i("NavigationPerformance", 
            "Resolutions: ${metrics.totalResolutions}, " +
            "Cache Hit Rate: ${String.format("%.1f%%", cacheHitRate)}, " +
            "Avg Time: ${String.format("%.2fms", metrics.averageResolutionTime / 1_000_000.0)}"
        )
    }
}

/**
 * Data class containing navigation performance metrics.
 */
data class NavigationMetrics(
    val totalResolutions: Int = 0,
    val cacheHits: Int = 0,
    val slowResolutions: Int = 0,
    val totalResolutionTime: Long = 0,
    val averageResolutionTime: Long = 0,
    val providerCacheSize: Int = 0,
    val entryPoolSize: Int = 0,
    val deepLinkResolutions: Int = 0,
    val totalDeepLinkTime: Long = 0,
    val entryCreations: Int = 0,
    val totalEntryCreationTime: Long = 0,
    val lastMemoryUpdate: Long = 0
)
