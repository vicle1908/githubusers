package com.example.githubusers.navigation.api

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigationevent.NavigationEventInfo

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
     * @param metadata Optional metadata to include in the NavEntry (e.g., transition specs)
     * @return A NavEntry that contains the feature's composable content
     * @throws IllegalArgumentException if the key cannot be resolved by this provider
     */
    fun createEntry(key: NavKey, metadata: Map<String, Any> = emptyMap()): NavEntry<NavKey>

    /**
     * Optional: Provides custom transition specification for navigation to this destination.
     * If null, the default transition will be used.
     *
     * @param key The navigation key
     * @return Custom transition specification or null for default
     */
    fun getTransitionSpec(key: NavKey): (AnimatedContentTransitionScope<*>.() -> ContentTransform)? = null

    /**
     * Optional: Provides custom pop transition specification for navigation back from this destination.
     * If null, the default pop transition will be used.
     *
     * @param key The navigation key
     * @return Custom pop transition specification or null for default
     */
    fun getPopTransitionSpec(key: NavKey): (AnimatedContentTransitionScope<*>.() -> ContentTransform)? = null

    /**
     * Optional: Determines if this destination should be displayed as a dialog.
     * If true, the destination will be rendered using DialogSceneStrategy.
     *
     * @param key The navigation key
     * @return Dialog properties if this should be a dialog, null otherwise
     */
    fun getDialogProperties(key: NavKey): DialogProperties? = null

    /**
     * Optional: Determines if this destination should be displayed in a list-detail layout.
     * If true, the destination will be rendered using ListDetailSceneStrategy.
     *
     * @param key The navigation key
     * @return true if this should be part of a list-detail layout, false otherwise
     */
    fun isListDetailDestination(key: NavKey): Boolean = false

    /**
     * Optional: Determines if this destination should be displayed in a supporting pane layout.
     * If true, the destination will be rendered using SupportingPaneSceneStrategy.
     *
     * @param key The navigation key
     * @return true if this should be part of a supporting pane layout, false otherwise
     */
    fun isSupportingPaneDestination(key: NavKey): Boolean = false

    /**
     * Optional: Provides custom NavigationEventInfo for advanced gesture handling.
     * This enables features to provide contextual information for predictive back gestures
     * and other navigation events.
     *
     * @param key The navigation key
     * @return Custom NavigationEventInfo or null for default behavior
     */
    fun getNavigationEventInfo(key: NavKey): NavigationEventInfo? = null

    /**
     * Optional: Determines if this destination supports predictive back gestures.
     * If true, the destination will participate in predictive back gesture handling.
     *
     * @param key The navigation key
     * @return true if this destination supports predictive back gestures, false otherwise
     */
    fun supportsPredictiveBack(key: NavKey): Boolean = false

    /**
     * Optional: Provides custom predictive pop transition specification for predictive back gestures.
     * This allows customization of the transition animation for predictive back gestures.
     * If null, the default predictive pop transition will be used.
     *
     * @param key The navigation key
     * @return Custom predictive pop transition specification or null for default
     */
    fun getPredictivePopTransitionSpec(key: NavKey): (AnimatedContentTransitionScope<*>.() -> ContentTransform)? = null
}
