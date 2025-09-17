package com.example.githubusers.feature.search.navigation

import android.net.Uri
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.example.githubusers.feature.search.presentation.navigation.SearchRoute
import com.example.githubusers.navigation.api.FeatureDestinationProvider
import com.example.githubusers.navigation.api.LocalNavigateBack
import com.example.githubusers.navigation.api.LocalNavigateToDeepLink
import javax.inject.Inject
import javax.inject.Singleton

/** Feature-owned destinations for Search. */
@Singleton
class SearchFeatureDestinationProvider @Inject constructor() : FeatureDestinationProvider {
    override fun canResolve(key: NavKey): Boolean = key is SearchNavKey.Search

    override fun createEntry(key: NavKey, metadata: Map<String, Any>): NavEntry<NavKey> =
        NavEntry(key, metadata = metadata) {
            val navigateToDeepLink = LocalNavigateToDeepLink.current
            val navigateBack = LocalNavigateBack.current
            val searchKey = key as SearchNavKey.Search
            SearchRoute(
                navigator =
                object : com.example.githubusers.feature.search.presentation.navigation.SearchNavigator {
                    override fun navigateToUserDetail(username: String) {
                        val deepLink = Uri.Builder()
                            .scheme("app")
                            .authority("users")
                            .appendPath("user")
                            .appendPath(username)
                            .build()
                            .toString()
                        navigateToDeepLink(deepLink)
                    }

                    override fun navigateBack() {
                        navigateBack()
                    }
                },
                initialQuery = searchKey.query,
                initialFilter = searchKey.filter,
                origin = searchKey.origin
            )
        }
}
