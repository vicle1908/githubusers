package com.example.githubusers.feature.users.navigation

import com.example.githubusers.navigation.api.NavigationDestination

/**
 * Navigation destinations for the Users feature module.
 * All navigation to user screens MUST use deep links.
 */
sealed interface UserDestination : NavigationDestination {
    /**
     * User list screen destination
     */
    data class UserList(
        val filter: String? = null,
    ) : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users${filter?.let { "?filter=$it" } ?: ""}"
    }

    /**
     * User detail screen destination
     */
    data class UserDetail(
        val username: String,
    ) : UserDestination {
        override val route = "users/detail/$username"
        override val deepLink = "app://users/$username"
    }

    /**
     * User search screen destination
     */
    data class UserSearch(
        val query: String = "",
    ) : UserDestination {
        override val route = "users/search"
        override val deepLink = "app://users/search${query.takeIf { it.isNotEmpty() }?.let { "?q=$it" } ?: ""}"
    }
}
