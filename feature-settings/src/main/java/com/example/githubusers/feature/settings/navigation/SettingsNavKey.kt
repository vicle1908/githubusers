package com.example.githubusers.feature.settings.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface SettingsNavKey : NavKey {
    @Serializable
    data class Settings(val section: String?) : SettingsNavKey
}


