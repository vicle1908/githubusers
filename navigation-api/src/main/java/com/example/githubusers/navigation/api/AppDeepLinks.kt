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
     */
    fun build(deeplink: AppDestination): String =
        when (deeplink) {
            is AppDestination.UserList -> "$SCHEME://users"
            is AppDestination.UserDetail -> "$SCHEME://user/${Uri.encode(deeplink.username)}"
            is AppDestination.Search ->
                deeplink.query?.let { q ->
                    "$SCHEME://search?q=${Uri.encode(q)}"
                } ?: "$SCHEME://search"
            is AppDestination.Settings -> "$SCHEME://settings"
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

        return if (scheme == SCHEME) {
            // githubusers://<host>/<optional path>
            when (uri.host?.lowercase()) {
                "users" -> AppDestination.UserList
                "user" -> {
                    val username = uri.pathSegments.firstOrNull()?.let { Uri.decode(it) }
                    if (!username.isNullOrEmpty()) AppDestination.UserDetail(username) else null
                }
                "search" -> {
                    val q = uri.getQueryParameter("q")
                    AppDestination.Search(q)
                }
                "settings" -> AppDestination.Settings
                else -> null
            }
        } else {
            // https://githubusers.example.com/<path>
            val segments = uri.pathSegments
            if (segments.isEmpty()) return null
            when (segments.first().lowercase()) {
                "users" -> AppDestination.UserList
                "user" -> {
                    val username = segments.getOrNull(1)?.let { Uri.decode(it) }
                    if (!username.isNullOrEmpty()) AppDestination.UserDetail(username) else null
                }
                "search" -> {
                    val q = uri.getQueryParameter("q")
                    AppDestination.Search(q)
                }
                "settings" -> AppDestination.Settings
                else -> null
            }
        }
    }
}
