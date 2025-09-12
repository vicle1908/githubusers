package com.example.githubusers.navigation.api

import android.net.Uri
import androidx.navigation3.runtime.NavKey

/**
 * Feature-owned deep link handler that converts URIs to Navigation 3 destination keys.
 */
interface FeatureDeepLinkHandler {
    /** Unique module identifier (e.g., "feature-users"). */
    val moduleId: String

    /** List of supported deep link patterns, for docs/validation. */
    fun supportedPatterns(): List<String>

    /**
     * Attempts to convert the [uri] to a Navigation 3 destination key.
     * Returns null if this feature does not own/handle the link.
     */
    fun handleDeepLink(uri: Uri): NavKey?
}
