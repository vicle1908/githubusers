@file:Suppress("ktlint:standard:function-naming")

package com.example.githubusers.feature.users.list.api

import androidx.compose.runtime.Composable
import com.example.githubusers.feature.users.list.presentation.navigation.UserListNavigator
import com.example.githubusers.feature.users.list.presentation.navigation.UserListRoute

/**
 * Public API for the User List feature module.
 * Provides entry points and navigation support for the feature.
 */
object UserListFeatureApi {
    /**
     * The route path for the user list screen.
     */
    const val ROUTE = "users/list"

    /**
     * The route path with optional search query parameter.
     */
    const val ROUTE_WITH_ARGS = "$ROUTE?query={query}"

    /**
     * Deep link pattern for the user list screen.
     */
    const val DEEP_LINK = "githubusers://users/list"

    /**
     * Deep link pattern with search query.
     */
    const val DEEP_LINK_WITH_QUERY = "$DEEP_LINK?query={query}"

    /**
     * Composable entry point for the User List feature.
     *
     * @param navigator Navigation handler for the feature
     * @param initialQuery Optional initial search query
     */
    @Composable
    fun UserListScreen(
        navigator: UserListNavigator,
        initialQuery: String? = null,
    ) {
        UserListRoute(
            navigator = navigator,
            initialQuery = initialQuery,
        )
    }

    /**
     * Build navigation route with optional query parameter.
     *
     * @param query Optional search query to include in the route
     * @return The navigation route string
     */
    fun buildRoute(query: String? = null): String =
        if (query != null) {
            "$ROUTE?query=$query"
        } else {
            ROUTE
        }

    /**
     * Build deep link with optional query parameter.
     *
     * @param query Optional search query to include in the deep link
     * @return The deep link URL string
     */
    fun buildDeepLink(query: String? = null): String =
        if (query != null) {
            "$DEEP_LINK?query=$query"
        } else {
            DEEP_LINK
        }
}
