package com.example.githubusers.core.mvi.navigation

import kotlinx.coroutines.flow.Flow

/**
 * Temporary NavigationManager interface.
 * This will be moved to the navigation module later.
 */
interface NavigationManager {
    val navigationCommands: Flow<NavigationCommand>
    suspend fun navigateTo(destination: NavigationDestination)
    suspend fun popBackStack()
    suspend fun navigateDeepLink(uri: String)
    suspend fun popBackStackTo(destination: NavigationDestination, inclusive: Boolean = false)
    suspend fun clearBackStackAndNavigate(destination: NavigationDestination)
}

/**
 * Temporary navigation command sealed class
 */
sealed class NavigationCommand {
    data class NavigateTo(val destination: NavigationDestination) : NavigationCommand()
    object PopBackStack : NavigationCommand()
    data class NavigateDeepLink(val uri: String) : NavigationCommand()
    data class PopBackStackTo(val destination: NavigationDestination, val inclusive: Boolean) : NavigationCommand()
    data class ClearBackStackAndNavigate(val destination: NavigationDestination) : NavigationCommand()
}
