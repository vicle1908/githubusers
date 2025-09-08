package com.example.githubusers.navigation.impl

import android.net.Uri
import com.example.githubusers.navigation.api.CoreNavigationDestination
import com.example.githubusers.navigation.api.DeepLinkHandler
import com.example.githubusers.navigation.api.NavigationDestination
import javax.inject.Inject

/**
 * Default implementation of DestinationResolver.
 * Resolves deep links to navigation destinations using registered handlers.
 */
class DefaultDestinationResolver
    @Inject
    constructor(
        private val handlers: Set<@JvmSuppressWildcards DeepLinkHandler>,
        private val ownershipSource: DeepLinkOwnershipSource = NoOpDeepLinkOwnershipSource,
        private val telemetry: NavigationTelemetry = NoOpNavigationTelemetry,
    ) : DestinationResolver {
        override fun resolve(deepLink: String): NavigationDestination? {
            // Normalize before resolution
            val normalizedUri = UriNormalizer.normalize(Uri.parse(deepLink))
            val uri = normalizedUri
            val normalized = normalizedUri.toString()

            telemetry.onResolveAttempt(normalized)

            // Try core destinations first
            when {
                normalized == "app://home" -> return CoreNavigationDestination.Home
                normalized == "app://settings" -> return CoreNavigationDestination.Settings
                normalized.startsWith("app://error") -> {
                    val message = uri.getQueryParameter("message") ?: "Unknown error"
                    val code = uri.getQueryParameter("code")?.toIntOrNull()
                    return CoreNavigationDestination.Error(message, code)
                }
            }

            // Prefer handlers whose moduleId is present in the (optional) ownership registry
            val owners = ownershipSource.owners
            val (ownedHandlers, otherHandlers) = handlers.partition { it.moduleId in owners.keys }
            val orderedHandlers = if (ownedHandlers.isNotEmpty()) ownedHandlers + otherHandlers else handlers.toList()

            // Prepare a URI optimized for handler matching, while keeping the original normalized string for fallback/telemetry
            val handlerUri = handlerUriForMatching(uri)

            // Try registered handlers
            for (handler in orderedHandlers) {
                val result = handler.handleDeepLink(handlerUri)
                if (result != null) {
                    val owned = handler.moduleId in owners
                    telemetry.onResolveSuccess(normalized, owned)
                    // Convert typed destination to canonical deep link for downstream handling
                    val deepLink =
                        when (val dest = result.destination) {
                            is com.example.githubusers.navigation.api.NavigationDestination -> dest.deepLink
                            else -> {
                                // Since AppDestination is empty, we can't build a deep link from a generic destination
                                // This should not happen in practice as all feature destinations should implement NavigationDestination
                                throw IllegalArgumentException("Cannot build deep link from destination: $dest")
                            }
                        }
                    return GenericDestination(deepLink)
                }
            }

            // Fallback - create a generic destination
            telemetry.onResolveFallback(normalized)
            return GenericDestination(normalized)
        }

        /**
         * Some inbound app-scheme URIs may be provided as authority-only (e.g., app://users).
         * Many handlers expect a path-based pattern (e.g., /users). For matching only, add a synthetic
         * path when the scheme is app, the host is present, and there is no path.
         */
        private fun handlerUriForMatching(uri: Uri): Uri =
            if (uri.scheme == "app" && (uri.path.isNullOrEmpty() || uri.path == "/") && !uri.host.isNullOrEmpty()) {
                uri.buildUpon().path("/${uri.host}").build()
            } else {
                uri
            }
    }
