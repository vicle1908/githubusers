package com.example.githubusers.navigation.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.githubusers.navigation.api.Navigation3Controller
import com.example.githubusers.navigation.api.NavigationOptions

/**
 * Helper class for navigation actions using Navigation 3.
 * Provides convenient methods for common navigation patterns.
 */
class Navigation3Actions(
    private val controller: Navigation3Controller,
) {
    /**
     * Navigate to a destination using deep link
     */
    fun navigate(deepLink: String) {
        controller.navigate(deepLink)
    }

    /**
     * Type-safe navigate to a destination
     */
    fun navigate(destination: com.example.githubusers.navigation.api.AppDestination) {
        controller.navigate(destination)
    }

    /**
     * Navigate to a destination with options
     */
    fun navigate(
        deepLink: String,
        options: NavigationOptions,
    ) {
        controller.navigate(deepLink, options)
    }

    /**
     * Type-safe navigate to a destination with options
     */
    fun navigate(
        destination: com.example.githubusers.navigation.api.AppDestination,
        options: NavigationOptions,
    ) {
        controller.navigate(destination, options)
    }

    /**
     * Navigate back
     */
    fun navigateBack() {
        controller.navigateBack()
    }

    /**
     * Navigate up
     */
    fun navigateUp() {
        controller.navigateUp()
    }

    /**
     * Navigate to home (typed)
     */
    fun navigateToHome() {
        controller.navigate(com.example.githubusers.navigation.api.AppDestination.UserList)
    }

    /**
     * Navigate to settings (typed)
     */
    fun navigateToSettings() {
        controller.navigate(com.example.githubusers.navigation.api.AppDestination.Settings)
    }

    /**
     * Navigate to error screen
     */
    fun navigateToError(
        message: String,
        code: Int? = null,
    ) {
        val deepLink = "app://error?message=$message${code?.let { "&code=$it" } ?: ""}"
        controller.navigate(deepLink)
    }

    /**
     * Clear back stack and navigate to a destination
     */
    fun navigateAndClearBackStack(deepLink: String) {
        controller.clearBackStack()
        controller.navigate(deepLink)
    }

    /**
     * Pop back stack to a specific destination
     */
    fun popBackStackTo(
        deepLink: String,
        inclusive: Boolean = false,
    ) {
        controller.popBackStackTo(deepLink, inclusive)
    }
}

/**
 * Remember Navigation3Actions in composition
 */
@Composable
fun rememberNavigation3Actions(controller: Navigation3Controller): Navigation3Actions =
    remember(controller) {
        Navigation3Actions(controller)
    }
