package com.example.githubusers.navigation.api

/**
 * Navigation options for controlling navigation behavior.
 *
 * This class provides options for controlling how navigation commands are executed,
 * including back stack management and navigation behavior.
 */
data class NavigationOptions(
    /**
     * Whether to clear the back stack before navigating.
     * When true, all previous destinations are removed from the back stack.
     */
    val clearBackStack: Boolean = false,
    /**
     * Whether to use single top behavior.
     * When true, if the destination is already at the top of the back stack,
     * it won't be added again.
     */
    val singleTop: Boolean = true,
    /**
     * The destination to pop up to.
     * When specified, all destinations above this one will be removed from the back stack.
     */
    val popUpTo: String? = null,
    /**
     * Whether to include the popUpTo destination in the removal.
     * When true, the popUpTo destination is also removed from the back stack.
     */
    val popUpToInclusive: Boolean = false,
)
