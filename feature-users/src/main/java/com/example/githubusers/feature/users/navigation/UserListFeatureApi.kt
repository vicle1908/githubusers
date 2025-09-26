package com.example.githubusers.feature.users.navigation

import androidx.compose.runtime.Composable
import com.example.githubusers.feature.users.presentation.list.navigation.UserListNavigator
import com.example.githubusers.feature.users.presentation.list.navigation.UserListRoute

object UserListFeatureApi {
    const val ROUTE = "users/list"
    const val DEEP_LINK = "githubusers://users/list"

    @Composable
    fun UserListScreen(navigator: UserListNavigator) {
        UserListRoute(navigator = navigator)
    }

    fun buildRoute(): String = ROUTE

    fun buildDeepLink(): String = DEEP_LINK
}
