package com.example.githubusers.navigation.api

/**
 * Core navigation destinations available across the app
 */
sealed class CoreNavigationDestination : NavigationDestination {
    data object Home : CoreNavigationDestination() {
        override val route = "home"
        override val deepLink = "app://home"
    }

    data object Settings : CoreNavigationDestination() {
        override val route = "settings"
        override val deepLink = "app://settings"
    }

    data class Error(
        val message: String,
        val code: Int? = null,
    ) : CoreNavigationDestination() {
        override val route = "error"
        override val deepLink = "app://error?message=$message${code?.let { "&code=$it" } ?: ""}"
    }
}
