package com.example.githubusers.navigation.api

import androidx.compose.ui.graphics.vector.ImageVector

fun navigationTab(
    label: String,
    route: String,
    icon: ImageVector,
    selectedIcon: ImageVector? = null,
    order: Int = NavigationTab.DEFAULT_ORDER
): NavigationTab = object : NavigationTab {
    override val label: String = label
    override val route: String = route
    override val icon: ImageVector = icon
    override val selectedIcon: ImageVector? = selectedIcon
    override val order: Int = order
}
