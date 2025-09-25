package com.example.githubusers.navigation.api

import androidx.compose.ui.graphics.vector.ImageVector

interface NavigationTab {
    val label: String
    val route: String
    val icon: ImageVector
    val selectedIcon: ImageVector? get() = null
    val order: Int get() = DEFAULT_ORDER

    companion object {
        const val DEFAULT_ORDER: Int = Int.MAX_VALUE
    }
}
