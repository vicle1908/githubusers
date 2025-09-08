package com.example.githubusers.feature.users.navigation

import android.net.Uri
import com.example.githubusers.navigation.api.DeepLinkHandler
import com.example.githubusers.navigation.api.DeepLinkResult
import com.example.githubusers.navigation.api.getBooleanQueryParameter
import javax.inject.Inject

/**
 * Deep link handler for the Users feature module.
 * Owned by feature-users per feature-based architecture.
 */
class UserDeepLinkHandler
    @Inject
    constructor() : DeepLinkHandler {
        companion object {
            private const val PATTERN_USERS_LIST = "app://users/list"
            private const val PATTERN_USERS_DETAIL = "app://users/user/{username}"
            private const val PATTERN_USERS_LEGACY = "githubusers://users"
            private const val PATTERN_USERS_LEGACY_DETAIL = "githubusers://users/user/{username}"
            private const val WEB_PATTERN_USERS = "https://githubusers.example.com/users"
            private const val WEB_PATTERN_USERS_DETAIL = "https://githubusers.example.com/user/{username}"
        }

        override val moduleId: String get() = "users"

        override fun supportedPatterns(): List<String> =
            listOf(
                PATTERN_USERS_LIST,
                PATTERN_USERS_DETAIL,
                PATTERN_USERS_LEGACY,
                PATTERN_USERS_LEGACY_DETAIL,
                WEB_PATTERN_USERS,
                WEB_PATTERN_USERS_DETAIL,
            )

        override fun handleDeepLink(uri: Uri): DeepLinkResult? =
            when {
                isUsersListUri(uri) -> {
                    DeepLinkResult(
                        destination = UserDestination.UserList,
                        clearBackStack = uri.getBooleanQueryParameter("clear_stack", false),
                    )
                }
                isUsersDetailUri(uri) -> {
                    val username = extractUsername(uri)
                    if (username != null) {
                        DeepLinkResult(
                            destination = UserDestination.UserDetail(username = username),
                            clearBackStack = uri.getBooleanQueryParameter("clear_stack", false),
                        )
                    } else {
                        null
                    }
                }
                else -> null
            }

        private fun isUsersListUri(uri: Uri): Boolean {
            val scheme = uri.scheme ?: return false
            val host = uri.host
            val path = uri.path ?: ""
            return when {
                // App scheme: app://users/list
                scheme == "app" && host == "users" && path == "/list" -> true
                // Legacy scheme: githubusers://users
                scheme == "githubusers" && path == "/users" -> true
                // Web universal links: https://githubusers.example.com/users
                (scheme == "http" || scheme == "https") && host == "githubusers.example.com" && path == "/users" -> true
                else -> false
            }
        }

        private fun isUsersDetailUri(uri: Uri): Boolean {
            val scheme = uri.scheme ?: return false
            val host = uri.host
            val path = uri.path ?: ""
            return when {
                // App scheme: app://users/user/{username}
                scheme == "app" && host == "users" && path.startsWith("/user/") -> true
                // Legacy scheme: githubusers://users/user/{username}
                scheme == "githubusers" && path.startsWith("/users/user/") -> true
                // Web universal links: https://githubusers.example.com/user/{username}
                (scheme == "http" || scheme == "https") && host == "githubusers.example.com" && path.startsWith("/user/") -> true
                else -> false
            }
        }

        private fun extractUsername(uri: Uri): String? {
            val path = uri.path ?: return null
            return when {
                path.startsWith("/user/") -> path.removePrefix("/user/")
                path.startsWith("/users/user/") -> path.removePrefix("/users/user/")
                else -> null
            }
        }
    }
