package com.example.githubusers.feature.search.navigation

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import com.example.githubusers.navigation.api.FeatureDeepLinkHandler
import com.example.githubusers.navigation.api.decodeSearchFilter
import javax.inject.Inject
import javax.inject.Singleton

/** Search feature deep link handler → Navigation 3 key. */
@Singleton
class SearchFeatureDeepLinkHandler
@Inject
constructor() : FeatureDeepLinkHandler {
    override val moduleId: String = "feature-search"

    override fun supportedPatterns(): List<String> = listOf(
        "app://search",
        "app://search?q={query}",
        "app://users/search",
        "app://users/search?q={query}"
    )

    override fun handleDeepLink(uri: Uri): NavKey? {
        val scheme = uri.scheme ?: return null
        val host = uri.host ?: return null
        val isSearchHost = host == "search"
        val isLegacyUsersSearch = host == "users" && uri.path == "/search"
        return when {
            scheme == "app" &&
                (isSearchHost || isLegacyUsersSearch) &&
                uri.query.isNullOrEmpty() ->
                SearchNavKey.Search()
            scheme == "app" &&
                (isSearchHost || isLegacyUsersSearch) -> {
                val query = uri.getQueryParameter("q")
                val origin = uri.getQueryParameter("origin")
                val filter = decodeSearchFilter(uri.getQueryParameter("filter"))
                SearchNavKey.Search(query = query, origin = origin, filter = filter)
            }
            else -> null
        }
    }
}
