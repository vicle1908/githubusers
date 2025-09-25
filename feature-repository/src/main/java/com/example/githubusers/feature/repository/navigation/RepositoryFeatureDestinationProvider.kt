package com.example.githubusers.feature.repository.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.example.githubusers.core.search.domain.SearchDomain
import com.example.githubusers.core.search.domain.SearchFilter
import com.example.githubusers.core.search.domain.SearchResultType
import com.example.githubusers.feature.repository.navigation.RepositoryDeepLinks
import com.example.githubusers.feature.repository.presentation.navigation.RepositoryListNavigator
import com.example.githubusers.feature.repository.presentation.ui.RepositoryDetailRoute
import com.example.githubusers.feature.repository.presentation.ui.RepositoryListRoute
import com.example.githubusers.navigation.api.FeatureDestinationProvider
import com.example.githubusers.navigation.api.LocalNavigateToDeepLink
import com.example.githubusers.navigation.api.openSearch
import javax.inject.Singleton

@Singleton
class RepositoryFeatureDestinationProvider : FeatureDestinationProvider {
    override fun canResolve(key: NavKey): Boolean = key is RepositoryNavKey

    override fun createEntry(key: NavKey, metadata: Map<String, Any>): NavEntry<NavKey> {
        val repositoryKey = key as? RepositoryNavKey
            ?: throw IllegalArgumentException("Key $key is not handled by RepositoryFeatureDestinationProvider")
        return when (repositoryKey) {
            RepositoryNavKey.RepositoryList -> NavEntry(repositoryKey, metadata = metadata) {
                RepositoryListContent()
            }
            is RepositoryNavKey.RepositoryDetail -> {
                val owner = repositoryKey.owner
                val name = repositoryKey.name
                NavEntry(repositoryKey, metadata = metadata) {
                    RepositoryDetailContent(owner = owner, name = name)
                }
            }
        }
    }

    @Composable
    private fun RepositoryListContent() {
        val navigateToDeepLink = LocalNavigateToDeepLink.current
        RepositoryListRoute(
            navigator = object : RepositoryListNavigator {
                override fun openRepositoryDetails(owner: String, name: String) {
                    navigateToDeepLink(RepositoryDeepLinks.detail(owner, name))
                }

                override fun openSearch(initialQuery: String) {
                    openSearch(
                        navigateToDeepLink = navigateToDeepLink,
                        query = initialQuery.takeIf { it.isNotBlank() },
                        origin = "repository",
                        filter = SearchFilter(
                            type = SearchResultType.REPOSITORY,
                            domain = SearchDomain.REPOSITORIES
                        )
                    )
                }
            }
        )
    }

    @Composable
    private fun RepositoryDetailContent(owner: String, name: String) {
        RepositoryDetailRoute(owner = owner, name = name)
    }
}
