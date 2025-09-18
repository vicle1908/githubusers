package com.example.githubusers.feature.settings.navigation

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import com.example.githubusers.navigation.api.FeatureDeepLinkHandler
import javax.inject.Inject
import javax.inject.Singleton

/** Settings feature deep link handler → Navigation 3 key. */
@Singleton
class SettingsFeatureDeepLinkHandler @Inject constructor() : FeatureDeepLinkHandler {
    override val moduleId: String = "feature-settings"

    override fun supportedPatterns(): List<String> = listOf(
        "app://settings",
        "app://settings?section={section}"
    )

    override fun handleDeepLink(uri: Uri): NavKey? {
        val scheme = uri.scheme ?: return null
        val host = uri.host ?: return null
        return when {
            scheme == "app" && host == "settings" && uri.query.isNullOrEmpty() -> SettingsNavKey.Settings(null)
            scheme == "app" && host == "settings" -> {
                val section = uri.getQueryParameter("section")
                SettingsNavKey.Settings(section)
            }
            else -> null
        }
    }
}
