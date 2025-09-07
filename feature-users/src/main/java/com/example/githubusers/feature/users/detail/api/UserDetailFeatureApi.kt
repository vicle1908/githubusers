package com.example.githubusers.feature.users.detail.api

import androidx.compose.runtime.Composable
import com.example.githubusers.feature.users.detail.presentation.navigation.UserDetailNavigator
import com.example.githubusers.feature.users.detail.presentation.navigation.UserDetailRoute

/**
 * Public API for the User Detail feature module.
 * Provides entry points and navigation support for the feature.
 */
object UserDetailFeatureApi {
    /**
     * The route path for the user detail screen.
     */
    const val ROUTE = "users/{username}"

    /**
     * Deep link pattern for the user detail screen.
     */
    const val DEEP_LINK = "githubusers://users/{username}"

    /**
     * Argument key for username.
     */
    const val ARG_USERNAME = "username"

    /**
     * Composable entry point for the User Detail feature.
     *
     * @param username The username to display details for
     * @param navigator Navigation handler for the feature
     */
    @Composable
    fun UserDetailScreen(
        username: String,
        navigator: UserDetailNavigator,
    ) {
        UserDetailRoute(
            username = username,
            navigator = navigator,
        )
    }

    /**
     * Build navigation route for a specific user.
     *
     * @param username The username to navigate to
     * @return The navigation route string
     */
    fun buildRoute(username: String): String = "users/$username"

    /**
     * Build deep link for a specific user.
     *
     * @param username The username to create deep link for
     * @return The deep link URL string
     */
    fun buildDeepLink(username: String): String = "githubusers://users/$username"
}
