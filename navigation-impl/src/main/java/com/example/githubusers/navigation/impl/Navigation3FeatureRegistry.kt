package com.example.githubusers.navigation.impl

import androidx.compose.material3.Text
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.example.githubusers.navigation.api.FeatureDestinationProvider
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central registry that coordinates Navigation 3 key → NavEntry resolution
 * while preserving feature ownership (each feature provides a resolver).
 */
@Singleton
class Navigation3FeatureRegistry @Inject constructor(
    val providers: Set<@JvmSuppressWildcards FeatureDestinationProvider>
) {

    // Cache the entry provider function to avoid recreating it on every call
    private val entryProvider: (NavKey) -> NavEntry<NavKey> by lazy {
        {
                key ->
            val provider = providers.firstOrNull { it.canResolve(key) }
            if (provider == null) {
                NavEntry(key) {
                    Text(text = "Unknown destination: $key")
                }
            } else {
                try {
                    provider.createEntry(key)
                } catch (e: IllegalArgumentException) {
                    // Fallback to error entry if provider fails
                    NavEntry(key) {
                        Text(text = "Error loading destination: $key")
                    }
                }
            }
        }
    }

    /**
     * Returns the cached entry provider function that delegates to feature providers.
     * This maintains feature ownership while working with Navigation 3's entry provider pattern.
     */
    fun createEntryProvider(): (NavKey) -> NavEntry<NavKey> = entryProvider
}
