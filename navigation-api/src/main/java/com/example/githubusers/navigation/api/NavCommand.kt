package com.example.githubusers.navigation.api

/**
 * Sealed class representing different types of navigation commands.
 *
 * This sealed class provides type-safe navigation commands with different behaviors:
 * - Navigate: Navigate to a new destination
 * - Back: Navigate back in the back stack
 * - PopTo: Pop back stack to a specific destination
 * - Replace: Replace current destination with a new one
 *
 * Each command type has specific properties relevant to its behavior.
 */
sealed class NavCommand {
    /**
     * Navigate to a new destination.
     * This is the most common navigation command.
     */
    data class Navigate(
        val route: String,
        val deepLink: String,
        val arguments: Map<String, Any> = emptyMap(),
        val options: NavigationOptions = NavigationOptions(),
    ) : NavCommand()

    /**
     * Navigate back in the back stack.
     * This command pops the current destination from the back stack.
     */
    object Back : NavCommand()

    /**
     * Pop back stack to a specific destination.
     * This command removes all destinations above the specified one.
     */
    data class PopTo(
        val destination: String,
        val inclusive: Boolean = false,
    ) : NavCommand()

    /**
     * Replace the current destination with a new one.
     * This command replaces the current destination without adding to the back stack.
     */
    data class Replace(
        val route: String,
        val deepLink: String,
        val arguments: Map<String, Any> = emptyMap(),
        val options: NavigationOptions = NavigationOptions(),
    ) : NavCommand()
}
