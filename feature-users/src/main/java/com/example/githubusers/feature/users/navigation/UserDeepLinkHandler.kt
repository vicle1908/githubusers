package com.example.githubusers.feature.users.navigation

import android.net.Uri
import com.example.githubusers.navigation.annotations.OwnsDeepLinks
import com.example.githubusers.navigation.api.AppDestination
import com.example.githubusers.navigation.api.DeepLinkHandler
import com.example.githubusers.navigation.api.DeepLinkResult
import com.example.githubusers.navigation.api.getBooleanQueryParameter
import javax.inject.Inject

/**
 * Deep link handler for the Users feature module.
 * This class is completely self-contained and only depends on the navigation API.
 */
@OwnsDeepLinks(moduleId = "users")
class UserDeepLinkHandler
    @Inject
    constructor() : DeepLinkHandler {
        override val moduleId: String = "users"

        override fun supportedPatterns(): List<String> =
            listOf(
                // App scheme patterns
                "app://users",
                "app://users/list",
                "app://users/user/{username}",
                "app://users/search",
                "app://users/search?q={query}",
                // Legacy scheme patterns (backward compatibility)
                "githubusers://users",
                "githubusers://user/{username}",
                "githubusers://search",
                // Web patterns (for universal links)
                "https://githubusers.example.com/users",
                "https://githubusers.example.com/user/{username}",
                "https://githubusers.example.com/search",
            )

        override fun handleDeepLink(uri: Uri): DeepLinkResult? {
            val path = uri.path ?: return null
            val scheme = uri.scheme ?: return null
            val host = uri.host

            return when {
                // User list patterns
                isUserListUri(scheme, host, path) -> {
                    val filter = uri.getQueryParameter("filter")
                    DeepLinkResult(
                        destination = AppDestination.UserList,
                        clearBackStack = uri.getBooleanQueryParameter("clear_stack", false),
                    )
                }

                // User detail patterns
                isUserDetailUri(scheme, host, path) -> {
                    val username = extractUsername(path)
                    if (username != null) {
                        DeepLinkResult(
                            destination = AppDestination.UserDetail(username),
                            arguments = mapOf("username" to username),
                            clearBackStack = uri.getBooleanQueryParameter("clear_stack", false),
                        )
                    } else {
                        null
                    }
                }

                // Search patterns
                isSearchUri(scheme, host, path) -> {
                    val query = uri.getQueryParameter("q") ?: ""
                    DeepLinkResult(
                        destination = AppDestination.Search(query),
                        arguments = mapOf("query" to query),
                        clearBackStack = uri.getBooleanQueryParameter("clear_stack", false),
                    )
                }

                else -> null
            }
        }

        private fun isUserListUri(
            scheme: String,
            host: String?,
            path: String,
        ): Boolean =
            when {
                scheme == "app" && path in listOf("/users", "/users/list") -> true
                scheme == "githubusers" && path == "/users" -> true
                host == "githubusers.example.com" && path == "/users" -> true
                else -> false
            }

        private fun isUserDetailUri(
            scheme: String,
            host: String?,
            path: String,
        ): Boolean =
            when {
                scheme == "app" && path.startsWith("/users/user/") -> true
                scheme == "githubusers" && path.startsWith("/user/") -> true
                host == "githubusers.example.com" && path.startsWith("/user/") -> true
                else -> false
            }

        private fun isSearchUri(
            scheme: String,
            host: String?,
            path: String,
        ): Boolean =
            when {
                scheme == "app" && path.startsWith("/users/search") -> true
                scheme == "githubusers" && path == "/search" -> true
                host == "githubusers.example.com" && path == "/search" -> true
                else -> false
            }

        private fun extractUsername(path: String): String? =
            when {
                path.startsWith("/users/user/") -> path.removePrefix("/users/user/").takeIf { it.isNotEmpty() }
                path.startsWith("/user/") -> path.removePrefix("/user/").takeIf { it.isNotEmpty() }
                else -> null
            }
    }
