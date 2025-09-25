package com.example.githubusers.feature.users.navigation

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import com.example.githubusers.feature.users.navigation.UsersDeepLinks
import com.example.githubusers.navigation.api.FeatureDeepLinkHandler
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

/** Users feature deep link handler → Navigation 3 key. */
@Singleton
class UsersFeatureDeepLinkHandler @Inject constructor() : FeatureDeepLinkHandler {
    override val moduleId: String = "feature-users"

    override fun supportedPatterns(): List<String> = listOf(
        UsersDeepLinks.LIST,
        UsersDeepLinks.DETAIL_PATTERN
    )

    override fun handleDeepLink(uri: Uri): NavKey? {
        Timber.tag("UsersFeatureDeepLinkHandler").d("Handling deep link: $uri")
        val scheme = uri.scheme ?: return null
        val host = uri.host
        val segments = uri.pathSegments
        val result = when {
            isUsersListDeepLink(scheme, host, segments) -> UserNavKey.UserList
            isUsersDetailDeepLink(scheme, host, segments) -> UserNavKey.UserDetail(segments[DETAIL_SEGMENT_INDEX])
            else -> null
        }
        Timber.tag("UsersFeatureDeepLinkHandler").d("Result: $result")
        return result
    }

    private fun isUsersListDeepLink(scheme: String, host: String?, segments: List<String>): Boolean =
        scheme.equals(APP_SCHEME, ignoreCase = true) && host == USERS_HOST && segments == LIST_PATH

    private fun isUsersDetailDeepLink(scheme: String, host: String?, segments: List<String>): Boolean =
        scheme.equals(APP_SCHEME, ignoreCase = true) &&
            host == USERS_HOST &&
            segments.firstOrNull() == DETAIL_PREFIX &&
            segments.size == DETAIL_SEGMENT_COUNT

    private companion object {
        const val APP_SCHEME = "app"
        const val USERS_HOST = "users"
        const val DETAIL_PREFIX = "user"
        const val DETAIL_SEGMENT_COUNT = 2
        const val DETAIL_SEGMENT_INDEX = 1
        val LIST_PATH = listOf("list")
    }
}
