package com.example.githubusers.navigation.api

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

/**
 * Core Navigation 3 controller interface.
 * Manages navigation state and operations using deep links.
 */
@Stable
interface Navigation3Controller {
    /**
     * Current navigation state
     */
    val currentEntry: StateFlow<Navigation3Entry?>

    /**
     * Navigation back stack
     */
    val backStack: StateFlow<List<Navigation3Entry>>

    /**
     * Navigate to a destination using deep link
     */
    fun navigate(deepLink: String)

    /**
     * Navigate to a destination with options
     */
    fun navigate(
        deepLink: String,
        options: NavigationOptions = NavigationOptions(),
    )

    /**
     * Type-safe navigate to a destination
     */
    fun navigate(destination: AppDestination)

    /**
     * Type-safe navigate with options
     */
    fun navigate(
        destination: AppDestination,
        options: NavigationOptions = NavigationOptions(),
    )

    /**
     * Navigate back
     */
    fun navigateBack(): Boolean

    /**
     * Navigate up (similar to back but respects hierarchy)
     */
    fun navigateUp(): Boolean

    /**
     * Pop back stack to a specific destination
     */
    fun popBackStackTo(
        deepLink: String,
        inclusive: Boolean = false,
    ): Boolean

    /**
     * Clear the entire back stack
     */
    fun clearBackStack()

    /**
     * Handle deep link from external source
     */
    fun handleDeepLink(deepLink: String): Boolean

    /**
     * Attempt to restore back stack from persistence.
     * Returns true if a valid stack was restored, false otherwise.
     */
    fun restoreFromPersistence(): Boolean
}

/**
 * Navigation entry representing a destination in the back stack
 */
@Stable
data class Navigation3Entry(
    val id: String,
    val destination: NavigationDestination,
    val deepLink: String,
    val arguments: Map<String, Any> = emptyMap(),
    val savedState: Map<String, Any> = emptyMap(),
)
