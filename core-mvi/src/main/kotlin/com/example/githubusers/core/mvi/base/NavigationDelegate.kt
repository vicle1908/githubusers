package com.example.githubusers.core.mvi.base

/**
 * Delegation interface for navigation in MVI architecture.
 * Provides a clean API for navigation operations compatible with Navigation3.
 */
interface NavigationDelegate {
    /**
     * Navigate using a deep link URI resolved by the app dispatcher.
     */
    suspend fun navigateDeepLink(uri: String)

    /**
     * Navigate back in the back stack.
     */
    suspend fun navigateBack()

    /**
     * Clear the entire back stack and navigate via deep link.
     */
    suspend fun clearBackStackAndNavigate(uri: String)
}
