package com.example.githubusers.navigation.api

/**
 * Base interface for all navigation destinations
 */
interface NavigationDestination {
    val route: String
    val deepLink: String
}
