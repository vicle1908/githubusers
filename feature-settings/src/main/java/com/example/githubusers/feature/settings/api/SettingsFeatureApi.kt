package com.example.githubusers.feature.settings.api

import androidx.compose.runtime.Composable
import com.example.githubusers.feature.settings.presentation.SettingsScreen
import com.example.githubusers.feature.settings.presentation.navigation.SettingsNavigator

object SettingsFeatureApi {
    const val ROUTE: String = "settings"
    const val DEEP_LINK: String = "app://settings"

    @Composable
    fun SettingsHome(
        navigator: SettingsNavigator,
        section: String? = null,
    ) {
        SettingsScreen(navigator = navigator, section = section)
    }
}
