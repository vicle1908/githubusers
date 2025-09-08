package com.example.githubusers.presentation.navigation.deeplink

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Navigator for cross-module navigation using deep links via Navigation3Controller.
 * Uses deep links to maintain feature ownership of destinations.
 */
@Singleton
class ModuleNavigator
    @Inject
    constructor(
        private val navigation3Controller: com.example.githubusers.navigation.api.Navigation3Controller,
    ) {
        /**
         * Navigate to a destination using deep link.
         */
        suspend fun navigateTo(
            deepLink: String,
            clearBackStack: Boolean = false,
        ) {
            if (clearBackStack) navigation3Controller.clearBackStack()
            navigation3Controller.navigate(deepLink)
        }
    }

/**
 * Extension functions for easier module navigation using deep links
 */
suspend fun ModuleNavigator.navigateToUserDetail(username: String) {
    navigateTo("app://users/user/$username")
}

suspend fun ModuleNavigator.navigateToSettings() {
    navigateTo("app://settings")
}

suspend fun ModuleNavigator.navigateToSearch(query: String) {
    navigateTo("app://search?q=$query")
}
