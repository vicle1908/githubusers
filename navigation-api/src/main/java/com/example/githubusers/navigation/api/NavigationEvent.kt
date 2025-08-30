package com.example.githubusers.navigation.api

/**
 * Sealed class representing navigation events that can be emitted by ViewModels.
 * These events are consumed by the UI layer to perform navigation.
 */
sealed class NavigationEvent {
    /**
     * Navigate to a specific route.
     */
    data class NavigateTo(
        val route: String,
        val popUpTo: String? = null,
        val inclusive: Boolean = false,
    ) : NavigationEvent()

    /**
     * Navigate back to the previous screen.
     */
    object NavigateBack : NavigationEvent()

    /**
     * Navigate back to a specific route.
     */
    data class NavigateBackTo(
        val route: String,
        val inclusive: Boolean = false,
    ) : NavigationEvent()

    /**
     * Clear the back stack and navigate to a route.
     */
    data class NavigateAndClearStack(
        val route: String,
    ) : NavigationEvent()

    /**
     * Open an external URL.
     */
    data class OpenUrl(
        val url: String,
    ) : NavigationEvent()

    /**
     * Show a bottom sheet.
     */
    data class ShowBottomSheet(
        val route: String,
    ) : NavigationEvent()

    /**
     * Show a dialog.
     */
    data class ShowDialog(
        val route: String,
    ) : NavigationEvent()
}
