package com.example.githubusers.navigation.impl

import com.example.githubusers.navigation.api.NavigationDestination

/**
 * Generic destination for unknown deep links.
 * Used as a fallback when no specific handler is found.
 */
data class GenericDestination(
    override val deepLink: String,
) : NavigationDestination {
    override val route: String = "generic/${deepLink.hashCode()}"
}
