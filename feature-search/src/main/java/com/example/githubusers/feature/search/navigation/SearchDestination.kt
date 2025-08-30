package com.example.githubusers.feature.search.navigation

import com.example.githubusers.navigation.api.NavigationDestination
import kotlinx.serialization.Serializable

/**
 * Navigation destinations for the Search feature module.
 * These are only exposed through deep links.
 */
sealed interface SearchDestination : NavigationDestination {
    @Serializable
    data class Search(
        val query: String = "",
        val showTrending: Boolean = false,
        val showHistory: Boolean = false,
    ) : SearchDestination {
        override val route = "search"
        override val deepLink = "app://search${buildQueryString()}"

        private fun buildQueryString(): String {
            val params = mutableListOf<String>()
            if (query.isNotEmpty()) params.add("q=$query")
            if (showTrending) params.add("trending=true")
            if (showHistory) params.add("history=true")
            return if (params.isNotEmpty()) "?${params.joinToString("&")}" else ""
        }
    }
}
