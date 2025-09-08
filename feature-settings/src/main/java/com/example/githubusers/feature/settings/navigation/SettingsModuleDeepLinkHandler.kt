package com.example.githubusers.feature.settings.navigation

import android.net.Uri
import com.example.githubusers.feature.settings.navigation.SettingsDestination
import com.example.githubusers.navigation.api.DeepLinkHandler
import com.example.githubusers.navigation.api.DeepLinkResult
import com.example.githubusers.navigation.api.getBooleanQueryParameter
import javax.inject.Inject

/**
 * Deep link handler for the Settings feature implementing the Navigation API contract.
 * Owned by the feature module per feature-based architecture.
 */
class SettingsModuleDeepLinkHandler
    @Inject
    constructor() : DeepLinkHandler {
        companion object {
            private const val PATTERN_SETTINGS_HOME = "githubusers://settings"
            private const val PATTERN_SETTINGS_SECTION = "githubusers://settings/{section}"
            private const val WEB_PATTERN_SETTINGS = "https://githubusers.example.com/settings"
            private const val WEB_PATTERN_SETTINGS_SECTION = "https://githubusers.example.com/settings/{section}"
        }

        override val moduleId: String get() = "settings"

        override fun supportedPatterns(): List<String> =
            listOf(
                PATTERN_SETTINGS_HOME,
                PATTERN_SETTINGS_SECTION,
                WEB_PATTERN_SETTINGS,
                WEB_PATTERN_SETTINGS_SECTION,
            )

        override fun handleDeepLink(uri: Uri): DeepLinkResult? =
            if (isSettingsUri(uri)) {
                val section = extractSection(uri)
                DeepLinkResult(
                    destination = SettingsDestination.Settings(section = section),
                    clearBackStack = uri.getBooleanQueryParameter("clear_stack", false),
                )
            } else {
                null
            }

        private fun isSettingsUri(uri: Uri): Boolean {
            val scheme = uri.scheme ?: return false
            val host = uri.host
            val path = uri.path ?: ""
            return when {
                // Internal app scheme: app://settings or app://settings/{section}
                scheme == "app" && host == "settings" -> true
                // Legacy/internal scheme
                scheme == "githubusers" && path.startsWith("/settings") -> true
                // Web universal links
                (scheme == "http" || scheme == "https") && host == "githubusers.example.com" && path.startsWith("/settings") -> true
                else -> false
            }
        }

        private fun extractSection(uri: Uri): String? {
            val scheme = uri.scheme
            val host = uri.host
            val path = uri.path ?: return null
            return when {
                // app://settings or app://settings/{section}
                scheme == "app" && host == "settings" -> path.removePrefix("/").takeIf { it.isNotEmpty() }
                // githubusers://settings or githubusers://settings/{section}
                scheme == "githubusers" && path.startsWith("/settings/") -> path.removePrefix("/settings/").takeIf { it.isNotEmpty() }
                scheme == "githubusers" && path == "/settings" -> null
                // Web universal links
                (scheme == "http" || scheme == "https") &&
                    host == "githubusers.example.com" &&
                    path.startsWith(
                        "/settings/",
                    ) ->
                    path.removePrefix("/settings/").takeIf {
                        it.isNotEmpty()
                    }
                (scheme == "http" || scheme == "https") && host == "githubusers.example.com" && path == "/settings" -> null
                else -> null
            }
        }
    }
