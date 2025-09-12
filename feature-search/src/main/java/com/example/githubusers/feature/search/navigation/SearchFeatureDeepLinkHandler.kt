package com.example.githubusers.feature.search.navigation

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import com.example.githubusers.navigation.api.FeatureDeepLinkHandler
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
        "app://search?q={query}"
    )

    override fun handleDeepLink(uri: Uri): NavKey? {
        val scheme = uri.scheme ?: return null
        val host = uri.host ?: return null
        return when {
            scheme == "app" && host == "search" && uri.query.isNullOrEmpty() -> SearchNavKey.Search(null)
            scheme == "app" && host == "search" -> {
                val query = uri.getQueryParameter("q")
                SearchNavKey.Search(query)
            }
            else -> null
        }
    }
}
