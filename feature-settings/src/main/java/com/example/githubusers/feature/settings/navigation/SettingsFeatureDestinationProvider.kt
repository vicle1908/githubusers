package com.example.githubusers.feature.settings.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.example.githubusers.feature.settings.presentation.SettingsScreen
import com.example.githubusers.navigation.api.FeatureDestinationProvider
import com.example.githubusers.navigation.api.LocalNavigateToDeepLink
import javax.inject.Inject
import javax.inject.Singleton

/** Feature-owned destinations for Settings. */
@Singleton
class SettingsFeatureDestinationProvider @Inject constructor() : FeatureDestinationProvider {
    override fun canResolve(key: NavKey): Boolean = key is SettingsNavKey.Settings

    override fun createEntry(key: NavKey): NavEntry<NavKey> = NavEntry(key) {
        val navigateToDeepLink = LocalNavigateToDeepLink.current
        val section = (key as SettingsNavKey.Settings).section
        SettingsScreen(
            navigator = object : com.example.githubusers.feature.settings.presentation.navigation.SettingsNavigator {
                override fun openSection(section: String) {
                    navigateToDeepLink("app://settings?section=$section")
                }
                override fun navigateBack() {
                    navigateToDeepLink("app://users/list")
                }
            },
            section = section,
        )
    }
}
