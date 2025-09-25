package com.example.githubusers.feature.repository.navigation

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import com.example.githubusers.navigation.api.FeatureDeepLinkHandler
import javax.inject.Singleton

@Singleton
class RepositoryFeatureDeepLinkHandler : FeatureDeepLinkHandler {
    override val moduleId: String = "feature-repository"

    override fun supportedPatterns(): List<String> = listOf(
        RepositoryDeepLinks.LIST,
        RepositoryDeepLinks.DETAIL_PATTERN
    )

    override fun handleDeepLink(uri: Uri): NavKey? {
        val scheme = uri.scheme?.lowercase() ?: return null
        val host = uri.host ?: return null
        val segments = uri.pathSegments
        return when {
            isRepositoryListDeepLink(scheme, host, segments) -> RepositoryNavKey.RepositoryList
            isRepositoryDetailDeepLink(scheme, host, segments) -> {
                val (owner, name) = segments
                RepositoryNavKey.RepositoryDetail(owner = owner, name = name)
            }
            else -> null
        }
    }

    private fun isRepositoryListDeepLink(scheme: String, host: String, segments: List<String>): Boolean =
        scheme == APP_SCHEME && host == REPOSITORY_HOST && segments == LIST_PATH

    private fun isRepositoryDetailDeepLink(scheme: String, host: String, segments: List<String>): Boolean =
        scheme == APP_SCHEME && host == REPOSITORY_HOST && segments.size == DETAIL_SEGMENT_COUNT

    private companion object {
        const val APP_SCHEME = "app"
        const val REPOSITORY_HOST = "repository"
        val LIST_PATH = listOf("list")
        const val DETAIL_SEGMENT_COUNT = 2
    }
}
