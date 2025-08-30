package com.example.githubusers.navigation.api

/**
 * Core navigation interface for navigating between screens.
 * Each feature module can implement its own navigator extending this interface.
 */
interface Navigator {
    /**
     * Navigate to a destination using a route string.
     *
     * @param route The destination route
     * @param popUpTo Optional route to pop up to
     * @param inclusive Whether the popUpTo should be inclusive
     */
    fun navigate(
        route: String,
        popUpTo: String? = null,
        inclusive: Boolean = false,
    )

    /**
     * Navigate back to the previous screen.
     */
    fun navigateBack()

    /**
     * Navigate back to a specific route.
     *
     * @param route The route to navigate back to
     * @param inclusive Whether to include the route in the back stack
     */
    fun navigateBackTo(
        route: String,
        inclusive: Boolean = false,
    )

    /**
     * Clear the back stack and navigate to a route.
     *
     * @param route The destination route
     */
    fun navigateAndClearStack(route: String)
}
