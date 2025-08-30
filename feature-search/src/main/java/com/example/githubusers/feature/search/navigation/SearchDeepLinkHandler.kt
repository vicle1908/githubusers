package com.example.githubusers.feature.search.navigation

import android.net.Uri
import com.example.githubusers.navigation.annotations.OwnsDeepLinks
import com.example.githubusers.navigation.api.AppDestination
import com.example.githubusers.navigation.api.DeepLinkHandler
import com.example.githubusers.navigation.api.DeepLinkResult
import javax.inject.Inject

/**
 * Deep link handler for the Search feature module.
 * All navigation to search screens MUST use deep links.
 */
@OwnsDeepLinks(moduleId = "search")
class SearchDeepLinkHandler
    @Inject
    constructor() : DeepLinkHandler {
        override val moduleId: String = "search"

        override fun supportedPatterns(): List<String> =
            listOf(
                // Primary patterns
                "app://search",
                "app://search?q={query}",
                "app://search/trending",
                "app://search/history",
                // Web patterns for universal links
                "https://githubusers.example.com/search",
                "https://githubusers.example.com/search?q={query}",
                // Legacy patterns for backward compatibility
                "githubusers://search",
                "githubusers://search?q={query}",
            )

        override fun handleDeepLink(uri: Uri): DeepLinkResult? {
            val scheme = uri.scheme ?: return null
            val path = uri.path ?: ""

            return when {
                isSearchUri(scheme, uri.host, path) -> {
                    val query = uri.getQueryParameter("q") ?: ""
                    val showTrending = uri.getBooleanQueryParameter("trending", false)
                    val showHistory = path.endsWith("/history")

                    DeepLinkResult(
                        destination = AppDestination.Search(query),
                        arguments =
                            buildMap {
                                put("query", query)
                                put("trending", showTrending.toString())
                                put("history", showHistory.toString())
                            },
                        clearBackStack = uri.getBooleanQueryParameter("clear_stack", false),
                        singleTop = uri.getBooleanQueryParameter("single_top", true),
                    )
                }
                else -> null
            }
        }

        private fun isSearchUri(
            scheme: String,
            host: String?,
            path: String,
        ): Boolean =
            when {
                scheme == "app" && path.startsWith("/search") -> true
                scheme == "githubusers" && (host == "search" || path == "/search") -> true
                scheme in listOf("http", "https") && host == "githubusers.example.com" && path.startsWith("/search") -> true
                else -> false
            }
    }

/**
 * Deep link builders for the Search module.
 * Other modules should use these to navigate to search screens.
 */
object SearchDeepLinks {
    /**
     * Navigate to search screen
     */
    fun search(
        query: String = "",
        showTrending: Boolean = false,
        clearStack: Boolean = false,
        singleTop: Boolean = true,
    ): String =
        buildString {
            append("app://search")
            val params = mutableListOf<String>()

            if (query.isNotEmpty()) {
                params.add("q=${Uri.encode(query)}")
            }
            if (showTrending) {
                params.add("trending=true")
            }
            if (clearStack) {
                params.add("clear_stack=true")
            }
            if (!singleTop) {
                params.add("single_top=false")
            }

            if (params.isNotEmpty()) {
                append("?")
                append(params.joinToString("&"))
            }
        }

    /**
     * Navigate to search history
     */
    fun searchHistory(clearStack: Boolean = false): String =
        buildString {
            append("app://search/history")
            if (clearStack) {
                append("?clear_stack=true")
            }
        }

    /**
     * Navigate to trending searches
     */
    fun trending(clearStack: Boolean = false): String =
        buildString {
            append("app://search/trending")
            if (clearStack) {
                append("?clear_stack=true")
            }
        }
}
