package com.example.githubusers.feature.users.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import com.example.githubusers.navigation.api.NavigationTab
import com.example.githubusers.navigation.api.navigationTab

fun usersNavigationTab(): NavigationTab = navigationTab(
    label = "Users",
    route = UsersDeepLinks.list(),
    icon = Icons.Filled.Group,
    order = 0
)
