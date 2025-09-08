package com.example.githubusers.feature.users.navigation

import com.example.githubusers.navigation.api.NavCommand
import javax.inject.Inject

/**
 * Implementation of UserFeatureApi that creates navigation commands
 * using the distributed UserDestination types.
 */
class UserFeatureApiImpl
    @Inject
    constructor() : UserFeatureApi {
        override fun navigateToUserList(filter: String?): NavCommand {
            val destination = UserDestination.UserList
            return NavCommand.Navigate(
                route = destination.route,
                deepLink = destination.deepLink,
                arguments = filter?.let { mapOf("filter" to it) } ?: emptyMap(),
            )
        }

        override fun navigateToUserDetail(username: String): NavCommand {
            val destination = UserDestination.UserDetail(username)
            return NavCommand.Navigate(
                route = destination.route,
                deepLink = destination.deepLink,
                arguments = mapOf("username" to username),
            )
        }

        override fun navigateToUserSearch(query: String): NavCommand {
            // Navigate to the search feature instead of a user-specific search
            return NavCommand.Navigate(
                route = "search",
                deepLink = "app://search${query.takeIf { it.isNotEmpty() }?.let { "?q=$it" } ?: ""}",
                arguments = query.takeIf { it.isNotEmpty() }?.let { mapOf("query" to it) } ?: emptyMap(),
            )
        }
    }
