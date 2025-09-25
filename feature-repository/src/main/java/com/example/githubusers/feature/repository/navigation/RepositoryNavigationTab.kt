package com.example.githubusers.feature.repository.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import com.example.githubusers.navigation.api.NavigationTab
import com.example.githubusers.navigation.api.navigationTab

fun repositoryNavigationTab(): NavigationTab = navigationTab(
    label = "Repositories",
    route = RepositoryDeepLinks.list(),
    icon = Icons.Filled.List,
    order = 1
)
