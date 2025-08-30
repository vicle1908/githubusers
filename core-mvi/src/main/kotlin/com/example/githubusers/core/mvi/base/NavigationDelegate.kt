package com.example.githubusers.core.mvi.base

import com.example.githubusers.core.mvi.navigation.NavigationDestination

/**
 * Delegation interface for navigation in MVI architecture.
 * Provides a clean API for navigation operations compatible with Navigation3.
 */
interface NavigationDelegate {
    /**
     * Navigate to a specific destination
     */
    suspend fun navigate(destination: NavigationDestination)
    
    /**
     * Navigate back in the navigation stack
     */
    suspend fun navigateBack()
    
    /**
     * Navigate using a deep link URI
     */
    suspend fun navigateDeepLink(uri: String)
    
    /**
     * Pop back stack to a specific destination
     */
    suspend fun popBackStackTo(destination: NavigationDestination, inclusive: Boolean = false)
    
    /**
     * Clear the entire back stack and navigate to destination
     */
    suspend fun clearBackStackAndNavigate(destination: NavigationDestination)
}
