package com.example.githubusers.core.mvi.test

import com.example.githubusers.core.mvi.base.NavigationDelegate

/**
 * Test implementation of NavigationDelegate that tracks navigation events.
 * Useful for verifying navigation in tests.
 */
class TestNavigationDelegate : NavigationDelegate {
    sealed class NavigationEvent {
        data class NavigateDeepLink(val uri: String) : NavigationEvent()
        object NavigateBack : NavigationEvent()
        data class ClearBackStackAndNavigate(val uri: String) : NavigationEvent()
    }

    private val _navigationHistory = mutableListOf<NavigationEvent>()
    val navigationHistory: List<NavigationEvent> get() = _navigationHistory.toList()

    override suspend fun navigateDeepLink(uri: String) {
        _navigationHistory.add(NavigationEvent.NavigateDeepLink(uri))
    }

    override suspend fun navigateBack() {
        _navigationHistory.add(NavigationEvent.NavigateBack)
    }

    override suspend fun clearBackStackAndNavigate(uri: String) {
        _navigationHistory.add(NavigationEvent.ClearBackStackAndNavigate(uri))
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
}
