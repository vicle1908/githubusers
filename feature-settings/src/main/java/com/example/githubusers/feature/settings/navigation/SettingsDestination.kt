package com.example.githubusers.feature.settings.navigation

import com.example.githubusers.navigation.api.NavigationDestination
import kotlinx.serialization.Serializable

/**
 * Navigation destinations for the Settings feature module.
 * Owned by feature-settings per feature-based architecture.
 */
sealed interface SettingsDestination : NavigationDestination {
    @Serializable
    data class Settings(
        val section: String? = null,
    ) : SettingsDestination {
        override val route: String = "settings${section?.let { "/$it" } ?: ""}"
        override val deepLink: String = "app://settings${section?.let { "?section=$it" } ?: ""}"
    }
}
