package com.example.githubusers.navigation.api

/**
 * Base interface for all navigation destinations in the distributed architecture.
 *
 * This interface replaces the centralized AppDestination approach, allowing each
 * feature module to define its own destination types while maintaining type safety.
 *
 * Each feature should define its own sealed class/interface extending this:
 *
 * ```kotlin
 * // In feature-users module
 * sealed class UserDestination : Destination {
 *     object UserList : UserDestination()
 *     data class UserDetail(val userId: String) : UserDestination()
 * }
 * ```
 */
interface Destination {
    /**
     * The route string for this destination.
     * Should be unique within the feature module.
     */
    val route: String

    /**
     * The deep link pattern for this destination.
     * Used for external navigation and deep link handling.
     */
    val deepLink: String
}
