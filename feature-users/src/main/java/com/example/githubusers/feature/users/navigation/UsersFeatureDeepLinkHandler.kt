package com.example.githubusers.feature.users.navigation

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import com.example.githubusers.navigation.api.FeatureDeepLinkHandler
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/** Users feature deep link handler → Navigation 3 key. */
@Singleton
class UsersFeatureDeepLinkHandler
@Inject
constructor() : FeatureDeepLinkHandler {
    override val moduleId: String = "feature-users"

    override fun supportedPatterns(): List<String> =
        listOf(
            "app://users/list",
            "app://users/user/{username}",
        )

    override fun handleDeepLink(uri: Uri): NavKey? {
        Timber.tag("UsersFeatureDeepLinkHandler").d("Handling deep link: $uri")
        val scheme = uri.scheme ?: return null
        val host = uri.host
        val path = uri.path ?: ""
        val result = when {
            scheme == "app" && host == "users" && path == "/list" -> UserNavKey.UserList
            scheme == "app" && host == "users" && path.startsWith("/user/") -> {
                val username = path.substringAfterLast('/')
                UserNavKey.UserDetail(username)
            }
            else -> null
        }
        Timber.tag("UsersFeatureDeepLinkHandler").d("Result: $result")
        return result
    }
}
