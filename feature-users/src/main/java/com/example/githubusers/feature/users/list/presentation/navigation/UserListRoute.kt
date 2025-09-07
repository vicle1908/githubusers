@file:Suppress("ktlint:standard:function-naming")

package com.example.githubusers.feature.users.list.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.example.githubusers.feature.users.list.presentation.ui.UserListScreen

/**
 * Route composable for the User List feature.
 * This is the entry point for the user list screen.
 *
 * @param navigator Navigator for handling navigation actions
 * @param initialQuery Optional initial search query to pre-populate
 */
@Composable
fun UserListRoute(
    navigator: UserListNavigator,
    initialQuery: String? = null,
) {
    android.util.Log.d("UserListRoute", "UserListRoute called with initialQuery: $initialQuery")

    // Use the first UserListScreen that manages its own ViewModel
    UserListScreen(
        onUserClick = { user ->
            android.util.Log.d("UserListRoute", "User clicked: ${user.login}")
            navigator.navigateToUserDetail(user.login)
        },
        onOpenSettings = {
            android.util.Log.d("UserListRoute", "Settings clicked from UserList")
            navigator.openSettings()
        },
    )
}
