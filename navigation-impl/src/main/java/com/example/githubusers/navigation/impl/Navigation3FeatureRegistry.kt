package com.example.githubusers.navigation.impl

import android.util.Log
import androidx.compose.material3.Text
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.example.githubusers.navigation.api.FeatureDestinationProvider
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central registry that coordinates Navigation 3 key → NavEntry resolution
 * while preserving feature ownership (each feature provides a resolver).
 * 
 * Performance optimizations:
 * - Provider caching by NavKey type to reduce O(n) complexity
 * - Entry pooling for common destinations to reduce memory allocation
 * - Lazy initialization to minimize startup overhead
 */
@Singleton
class Navigation3FeatureRegistry @Inject constructor(
    val providers: Set<@JvmSuppressWildcards FeatureDestinationProvider>,
    private val performanceMonitor: NavigationPerformanceMonitor
) {

    // Cache resolved providers by NavKey type to avoid O(n) lookup on every navigation
    private val providerCache = mutableMapOf<Class<*>, FeatureDestinationProvider>()
    
    // Cache common NavEntry instances to reduce memory allocation
    private val entryPool = mutableMapOf<String, NavEntry<NavKey>>()
    
    // Performance monitoring
    private var totalResolutions = 0
    private var cacheHits = 0
    private var slowResolutions = 0

    // Cache the entry provider function to avoid recreating it on every call
    private val entryProvider: (NavKey) -> NavEntry<NavKey> by lazy {
        { key ->
            val startTime = System.nanoTime()
            totalResolutions++
            
            val provider = getCachedProvider(key)
            val entry = if (provider == null) {
                createErrorEntry(key, "Unknown destination: $key")
            } else {
                try {
                    getPooledEntry(key, provider)
                } catch (e: IllegalArgumentException) {
                    Log.e("Navigation3FeatureRegistry", "Provider failed to create entry for $key", e)
                    createErrorEntry(key, "Error loading destination: $key")
                }
            }
            
            val duration = System.nanoTime() - startTime
            val wasCacheHit = providerCache.containsKey(key::class.java)
            
            // Record performance metrics
            performanceMonitor.recordProviderResolution(key, duration, wasCacheHit)
            performanceMonitor.recordMemoryUsage(providerCache.size, entryPool.size)
            
            if (duration > 1_000_000) { // Log if > 1ms
                slowResolutions++
                Log.w("Navigation3FeatureRegistry", "Slow provider resolution: ${duration}ns for $key")
            }
            
            entry
        }
    }

    /**
     * Returns the cached entry provider function that delegates to feature providers.
     * This maintains feature ownership while working with Navigation 3's entry provider pattern.
     */
    fun createEntryProvider(): (NavKey) -> NavEntry<NavKey> = entryProvider
    
    /**
     * Get cached provider for NavKey type, or resolve and cache if not found.
     * This reduces O(n) complexity to O(1) for subsequent lookups of the same type.
     */
    private fun getCachedProvider(key: NavKey): FeatureDestinationProvider? {
        val keyType = key::class.java
        return providerCache[keyType] ?: run {
            val provider = providers.firstOrNull { it.canResolve(key) }
            if (provider != null) {
                providerCache[keyType] = provider
                Log.d("Navigation3FeatureRegistry", "Cached provider for ${keyType.simpleName}")
            }
            provider
        }
    }
    
    /**
     * Get pooled NavEntry for common destinations, or create new one if not in pool.
     * This reduces memory allocation for frequently accessed destinations.
     */
    private fun getPooledEntry(key: NavKey, provider: FeatureDestinationProvider): NavEntry<NavKey> {
        val keyString = key.toString()
        return entryPool[keyString] ?: run {
            val entry = provider.createEntry(key)
            // Only pool simple destinations to avoid memory bloat
            if (isPoolableDestination(key)) {
                entryPool[keyString] = entry
                Log.d("Navigation3FeatureRegistry", "Pooled entry for $keyString")
            }
            entry
        }
    }
    
    /**
     * Create error entry for unknown or failed destinations.
     */
    private fun createErrorEntry(key: NavKey, message: String): NavEntry<NavKey> {
        return NavEntry(key) {
            Text(text = message)
        }
    }
    
    /**
     * Determine if a destination should be pooled based on its complexity.
     * Simple destinations like UserList, Settings are poolable.
     * Complex destinations with parameters are not pooled to avoid memory issues.
     */
    private fun isPoolableDestination(key: NavKey): Boolean {
        val keyString = key.toString()
        return keyString.contains("List") || 
               keyString.contains("Settings") || 
               keyString.contains("Home") ||
               keyString.contains("Main")
    }
    
    /**
     * Get performance metrics for monitoring and debugging.
     */
    fun getPerformanceMetrics(): Map<String, Any> {
        val metrics = performanceMonitor.performanceMetrics.value
        val cacheHitRate = if (metrics.totalResolutions > 0) {
            metrics.cacheHits.toDouble() / metrics.totalResolutions
        } else 0.0
        
        return mapOf(
            "totalResolutions" to metrics.totalResolutions,
            "cacheHits" to metrics.cacheHits,
            "cacheHitRate" to String.format("%.2f%%", cacheHitRate * 100),
            "slowResolutions" to metrics.slowResolutions,
            "averageResolutionTime" to String.format("%.2fms", metrics.averageResolutionTime / 1_000_000.0),
            "providerCacheSize" to metrics.providerCacheSize,
            "entryPoolSize" to metrics.entryPoolSize,
            "deepLinkResolutions" to metrics.deepLinkResolutions,
            "entryCreations" to metrics.entryCreations
        )
    }
    
    /**
     * Get detailed performance summary for debugging.
     */
    fun getPerformanceSummary(): String {
        return performanceMonitor.getPerformanceSummary()
    }
    
    /**
     * Clear caches for testing or memory management.
     */
    fun clearCaches() {
        providerCache.clear()
        entryPool.clear()
        totalResolutions = 0
        cacheHits = 0
        slowResolutions = 0
        Log.d("Navigation3FeatureRegistry", "Caches cleared")
    }
}
