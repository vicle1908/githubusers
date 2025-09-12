package com.example.githubusers.navigation.api

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey

/**
 * Feature-owned provider that registers Navigation 3 destination entries.
 * Each feature implements this to own its destinations without exposing implementation details.
 */
interface FeatureDestinationProvider {
    /**
     * True when this provider can resolve [key] to content.
     * This method should be fast and not perform heavy operations.
     */
    fun canResolve(key: NavKey): Boolean

    /**
     * Creates a NavEntry for the given key. This allows features to provide their own
     * NavEntry creation logic while maintaining encapsulation.
     *
     * @param key The navigation key to create an entry for
     * @return A NavEntry that contains the feature's composable content
     * @throws IllegalArgumentException if the key cannot be resolved by this provider
     */
    fun createEntry(key: NavKey): NavEntry<NavKey>
}
