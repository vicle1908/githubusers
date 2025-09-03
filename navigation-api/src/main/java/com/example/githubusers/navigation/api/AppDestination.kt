package com.example.githubusers.navigation.api

import kotlinx.serialization.Serializable

/**
 * Project-wide typed destinations for Navigation 3.
 * Keep this in navigation-api so features can reference types without app dependency.
 */
@Serializable
sealed interface AppDestination {

    @Serializable
    data object UserList : AppDestination

    @Serializable
    data class UserDetail(
        val username: String,
    ) : AppDestination

    @Serializable
    data class Search(
        val query: String? = null,
    ) : AppDestination

    @Serializable
    data object Settings : AppDestination
}
