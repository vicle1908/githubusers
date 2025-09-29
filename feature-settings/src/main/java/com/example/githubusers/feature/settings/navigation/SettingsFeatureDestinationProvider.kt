package com.example.githubusers.feature.settings.navigation

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.example.githubusers.feature.settings.navigation.SettingsDeepLinks
import com.example.githubusers.feature.settings.presentation.SettingsScreen
import com.example.githubusers.feature.settings.presentation.navigation.SettingsNavigator
import com.example.githubusers.navigation.api.FeatureDestinationProvider
import com.example.githubusers.navigation.api.LocalNavigateBack
import com.example.githubusers.navigation.api.LocalNavigateToDeepLink
import javax.inject.Singleton

/** Feature-owned destinations for Settings. */
@Singleton
class SettingsFeatureDestinationProvider : FeatureDestinationProvider {
    override fun canResolve(key: NavKey): Boolean = key is SettingsNavKey.Settings

    override fun createEntry(key: NavKey, metadata: Map<String, Any>): NavEntry<NavKey> =
        NavEntry(key, metadata = metadata) {
            val navigateToDeepLink = LocalNavigateToDeepLink.current
            val navigateBack = LocalNavigateBack.current
            val section = (key as SettingsNavKey.Settings).section
            SettingsScreen(
                navigator = object : SettingsNavigator {
                    override fun openSection(section: String) {
                        navigateToDeepLink(SettingsDeepLinks.root(section))
                    }
                    override fun navigateBack() {
                        navigateBack()
                    }
                },
                section = section
            )
        }
}
