package com.example.githubusers.feature.settings.presentation.navigation

interface SettingsNavigator {
    fun openSection(section: String)

    fun navigateBack()
}
