package com.example.githubusers.presentation.navigation.deeplink

import android.net.Uri
import com.example.githubusers.navigation.api.AppDestination
import com.example.githubusers.navigation.api.DeepLinkHandler
import com.example.githubusers.navigation.api.DeepLinkResult
import com.example.githubusers.navigation.api.getBooleanQueryParameter
import javax.inject.Inject

/**
 * Deep link handler for the Settings module implementing the Navigation API contract.
 */
class SettingsModuleDeepLinkHandler
    @Inject
    constructor() : DeepLinkHandler {
        companion object {
            // Deep link patterns
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
                    destination = AppDestination.Settings,
                    arguments = section?.let { mapOf("section" to it) } ?: emptyMap(),
                    clearBackStack = uri.getBooleanQueryParameter("clear_stack", false),
                )
            } else {
                null
            }

        private fun isSettingsUri(uri: Uri): Boolean {
            val path = uri.path ?: return false
            return when {
                uri.scheme == "githubusers" && path.startsWith("/settings") -> true
                uri.host == "githubusers.example.com" && path.startsWith("/settings") -> true
                else -> false
            }
        }

        private fun extractSection(uri: Uri): String? {
            val path = uri.path ?: return null
            return when {
                path == "/settings" -> null
                path.startsWith("/settings/") -> path.removePrefix("/settings/").takeIf { it.isNotEmpty() }
                else -> null
            }
        }
    }

/**
 * Builder for settings module deep links (unchanged)
 */
object SettingsModuleDeepLinks {
    fun settings(
        section: String? = null,
        clearStack: Boolean = false,
    ): String =
        buildString {
            append("githubusers://settings")
            if (!section.isNullOrEmpty()) {
                append("/")
                append(Uri.encode(section))
            }
            if (clearStack) {
                append(if (section == null) "?" else "&")
                append("clear_stack=true")
            }
        }

    fun profile(clearStack: Boolean = false): String = settings("profile", clearStack)

    fun theme(clearStack: Boolean = false): String = settings("theme", clearStack)

    fun about(clearStack: Boolean = false): String = settings("about", clearStack)
}
