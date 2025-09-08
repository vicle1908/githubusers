package com.example.githubusers.feature.search.navigation

import android.net.Uri
import com.example.githubusers.navigation.api.DeepLinkHandler
import com.example.githubusers.navigation.api.DeepLinkResult
import com.example.githubusers.navigation.api.getBooleanQueryParameter
import javax.inject.Inject

/**
 * Deep link handler for the Search feature module.
 * Owned by feature-search per feature-based architecture.
 */
class SearchDeepLinkHandler
    @Inject
    constructor() : DeepLinkHandler {
        companion object {
            private const val PATTERN_SEARCH = "app://search"
            private const val PATTERN_SEARCH_WITH_QUERY = "app://search?q={query}"
            private const val PATTERN_SEARCH_LEGACY = "githubusers://search"
            private const val PATTERN_SEARCH_LEGACY_WITH_QUERY = "githubusers://search?q={query}"
            private const val WEB_PATTERN_SEARCH = "https://githubusers.example.com/search"
            private const val WEB_PATTERN_SEARCH_WITH_QUERY = "https://githubusers.example.com/search?q={query}"
        }

        override val moduleId: String get() = "search"

        override fun supportedPatterns(): List<String> =
            listOf(
                PATTERN_SEARCH,
                PATTERN_SEARCH_WITH_QUERY,
                PATTERN_SEARCH_LEGACY,
                PATTERN_SEARCH_LEGACY_WITH_QUERY,
                WEB_PATTERN_SEARCH,
                WEB_PATTERN_SEARCH_WITH_QUERY,
            )

        override fun handleDeepLink(uri: Uri): DeepLinkResult? =
            if (isSearchUri(uri)) {
                val query = uri.getQueryParameter("q")
                DeepLinkResult(
                    destination = SearchDestination.Search(query = query),
                    clearBackStack = uri.getBooleanQueryParameter("clear_stack", false),
                )
            } else {
                null
            }

        private fun isSearchUri(uri: Uri): Boolean {
            val scheme = uri.scheme ?: return false
            val host = uri.host
            val path = uri.path ?: ""
            return when {
                // App scheme: app://search
                scheme == "app" && host == "search" -> true
                // Legacy scheme: githubusers://search
                scheme == "githubusers" && path == "/search" -> true
                // Web universal links: https://githubusers.example.com/search
                (scheme == "http" || scheme == "https") && host == "githubusers.example.com" && path == "/search" -> true
                else -> false
            }
        }
    }
