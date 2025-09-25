@file:Suppress("ktlint:standard:function-naming")

package com.example.githubusers.feature.users.list.api

import androidx.compose.runtime.Composable
import com.example.githubusers.feature.users.list.presentation.navigation.UserListNavigator
import com.example.githubusers.feature.users.list.presentation.navigation.UserListRoute

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
