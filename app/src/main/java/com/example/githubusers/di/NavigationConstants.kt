package com.example.githubusers.di

/**
 * Navigation constants to avoid hardcoded strings throughout the app.
 * This provides a centralized place to manage all navigation routes and deep links.
 */
object NavigationConstants {

    // Deep Link Patterns
    const val DEEP_LINK_SCHEME = "app://"

    // User Feature Routes
    object Users {
        const val LIST_DEEP_LINK = "${DEEP_LINK_SCHEME}users/list"
    }

    // Start Destination
    const val START_DESTINATION = Users.LIST_DEEP_LINK
}
