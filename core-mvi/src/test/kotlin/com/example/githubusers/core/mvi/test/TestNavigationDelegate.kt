package com.example.githubusers.core.mvi.test

import com.example.githubusers.core.mvi.base.NavigationDelegate
import com.example.githubusers.core.mvi.navigation.NavigationDestination

/**
 * Test implementation of NavigationDelegate that tracks navigation events.
 * Useful for verifying navigation in tests.
 */
class TestNavigationDelegate : NavigationDelegate {
    sealed class NavigationEvent {
        data class Navigate(
            val destination: NavigationDestination,
        ) : NavigationEvent()

        object NavigateBack : NavigationEvent()

        data class NavigateDeepLink(
            val uri: String,
        ) : NavigationEvent()

        data class PopBackStackTo(
            val destination: NavigationDestination,
            val inclusive: Boolean,
        ) : NavigationEvent()

        data class ClearBackStackAndNavigate(
            val destination: NavigationDestination,
        ) : NavigationEvent()
    }

    private val _navigationHistory = mutableListOf<NavigationEvent>()
    val navigationHistory: List<NavigationEvent> get() = _navigationHistory.toList()

    override suspend fun navigate(destination: NavigationDestination) {
        _navigationHistory.add(NavigationEvent.Navigate(destination))
    }

    override suspend fun navigateBack() {
        _navigationHistory.add(NavigationEvent.NavigateBack)
    }

    override suspend fun navigateDeepLink(uri: String) {
        _navigationHistory.add(NavigationEvent.NavigateDeepLink(uri))
    }

    override suspend fun popBackStackTo(
        destination: NavigationDestination,
        inclusive: Boolean,
    ) {
        _navigationHistory.add(NavigationEvent.PopBackStackTo(destination, inclusive))
    }

    override suspend fun clearBackStackAndNavigate(destination: NavigationDestination) {
        _navigationHistory.add(NavigationEvent.ClearBackStackAndNavigate(destination))
    }

    /**
     * Get the last navigation event
     */
    val lastNavigationEvent: NavigationEvent? get() = _navigationHistory.lastOrNull()

    /**
     * Clear navigation history
     */
    fun clearHistory() {
        _navigationHistory.clear()
    }

    /**
     * Get all navigated destinations
     */
    val navigatedDestinations: List<NavigationDestination>
        get() =
            _navigationHistory.mapNotNull { event ->
                when (event) {
                    is NavigationEvent.Navigate -> event.destination
                    is NavigationEvent.ClearBackStackAndNavigate -> event.destination
                    else -> null
                }
            }
}
