package com.example.githubusers.feature.search.navigation

import androidx.compose.runtime.Composable
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

    override fun createEntry(key: NavKey): NavEntry<NavKey> = NavEntry(key) {
        val navigateToDeepLink = LocalNavigateToDeepLink.current
        val navigateBack = LocalNavigateBack.current
        val query = (key as SearchNavKey.Search).query
        SearchRoute(
            navigator =
            object : com.example.githubusers.feature.search.presentation.navigation.SearchNavigator {
                override fun navigateToUserDetail(username: String) {
                    navigateToDeepLink("app://users/user/$username")
                }

                override fun navigateBack() {
                    navigateBack()
                }
            },
            initialQuery = query
        )
    }
}
