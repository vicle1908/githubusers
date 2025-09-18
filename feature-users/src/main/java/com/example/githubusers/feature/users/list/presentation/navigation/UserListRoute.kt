@file:Suppress("ktlint:standard:function-naming")

package com.example.githubusers.feature.users.list.presentation.navigation

import androidx.compose.runtime.Composable
import com.example.githubusers.feature.users.list.presentation.ui.UserListScreen
import timber.log.Timber

@Composable
fun UserListRoute(navigator: UserListNavigator) {
    Timber.tag("UserListRoute").d("UserListRoute launched")

    UserListScreen(
        onUserClick = { user ->
            Timber.tag("UserListRoute").d("User clicked: ${user.login}")
            navigator.navigateToUserDetail(user.login)
        },
        onOpenSettings = {
            Timber.tag("UserListRoute").d("Settings clicked from UserList")
            navigator.openSettings()
        },
        onOpenSearch = { navigator.openSearch(query = it, origin = "user_list") }
    )
}
