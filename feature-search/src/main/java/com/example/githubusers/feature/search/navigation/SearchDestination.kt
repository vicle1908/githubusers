package com.example.githubusers.feature.search.navigation

import com.example.githubusers.navigation.api.NavigationDestination
import kotlinx.serialization.Serializable

/**
 * Navigation destinations for the Search feature module.
 * Owned by feature-search per feature-based architecture.
 */
sealed interface SearchDestination : NavigationDestination {
    @Serializable
    data class Search(
        val query: String? = null,
    ) : SearchDestination {
        override val route: String = "search"
        override val deepLink: String = "app://search${query?.let { "?q=$it" } ?: ""}"
    }
}
