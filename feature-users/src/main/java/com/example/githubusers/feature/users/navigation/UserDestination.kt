package com.example.githubusers.feature.users.navigation

import com.example.githubusers.navigation.api.NavigationDestination
import kotlinx.serialization.Serializable

/**
 * Navigation destinations for the Users feature module.
 * Owned by feature-users per feature-based architecture.
 */
sealed interface UserDestination : NavigationDestination {
    @Serializable
    data object UserList : UserDestination {
        override val route: String = "users/list"
        override val deepLink: String = "app://users/list"
    }

    @Serializable
    data class UserDetail(
        val username: String,
    ) : UserDestination {
        override val route: String = "users/detail/$username"
        override val deepLink: String = "app://users/user/$username"
    }
}
