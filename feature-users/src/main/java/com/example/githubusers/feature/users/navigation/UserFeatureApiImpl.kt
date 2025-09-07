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
            val destination = UserDestination.UserList(filter)
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
            val destination = UserDestination.UserSearch(query)
            return NavCommand.Navigate(
                route = destination.route,
                deepLink = destination.deepLink,
                arguments = mapOf("query" to query),
            )
        }
    }
