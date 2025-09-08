package com.example.githubusers.navigation.api

import android.net.Uri

/**
 * Utilities for converting between typed destinations and deep links.
 */
object AppDeepLinks {
    const val SCHEME: String = "githubusers"
    const val WEB_HOST: String = "githubusers.example.com"

    /**
     * Build a deep link (app scheme) for a typed destination.
     * Note: Only handles shared destinations. Feature-specific destinations are handled by their respective modules.
     * Currently empty as all destinations are owned by feature modules.
     */
    fun build(deeplink: AppDestination): String =
        when (deeplink) {
            // All destinations are now owned by feature modules
            else -> throw IllegalArgumentException("No shared destinations available")
        }

    /**
     * Parse a deep link string into a typed destination when possible.
     */
    fun parse(uriString: String): AppDestination? = parse(Uri.parse(uriString))

    /**
     * Parse a deep link URI into a typed destination when possible.
     */
    fun parse(uri: Uri): AppDestination? {
        val scheme = uri.scheme ?: return null
        // Support app scheme and verified web host
        if (scheme != SCHEME && !(scheme == "https" && uri.host == WEB_HOST)) return null

        // All destinations are now owned by feature modules
        // This method is kept for future shared destinations if needed
        return null
    }
}
