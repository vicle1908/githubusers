package com.example.githubusers.presentation.navigation.deeplink

import com.example.githubusers.navigation.api.AppDestination
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Navigator for cross-module navigation using typed destinations via Navigation3Controller.
 */
@Singleton
class ModuleNavigator
    @Inject
    constructor(
        private val navigation3Controller: com.example.githubusers.navigation.api.Navigation3Controller,
    ) {
        /**
         * Navigate to a typed destination using Navigation 3 controller.
         */
        suspend fun navigateTo(
            destination: AppDestination,
            clearBackStack: Boolean = false,
        ) {
            if (clearBackStack) navigation3Controller.clearBackStack()
            navigation3Controller.navigate(destination)
        }
    }

/**
 * Extension functions for easier module navigation
 */
suspend fun ModuleNavigator.navigateToUserDetail(username: String) {
    navigateTo(AppDestination.UserDetail(username))
}

suspend fun ModuleNavigator.navigateToSettings() {
    navigateTo(AppDestination.Settings)
}

suspend fun ModuleNavigator.navigateToSearch(query: String) {
    navigateTo(AppDestination.Search(query))
}
