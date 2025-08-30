package com.example.githubusers.navigation.impl

import android.net.Uri
import com.example.githubusers.navigation.api.DeepLinkHandler
import com.example.githubusers.navigation.api.DeepLinkResult
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Router that handles deep link routing using registered handlers.
 * Each feature module registers its DeepLinkHandler through Hilt multibinding.
 */
@Singleton
class DeepLinkRouter
    @Inject
    constructor(
        private val handlers: Set<@JvmSuppressWildcards DeepLinkHandler>,
        private val telemetry: NavigationTelemetry = NoOpNavigationTelemetry,
    ) {
        /**
         * Route a deep link URI to the appropriate feature handler.
         *
         * @param uri The deep link URI to route
         * @return DeepLinkResult if a handler can process the URI, null otherwise
         */
        fun route(uri: Uri): DeepLinkResult? {
            // Normalize inbound URI before matching/handling
            val normalized = UriNormalizer.normalize(uri)
            telemetry.onRouteAttempt(normalized.toString())
            // Try registered handlers
            for (handler in handlers) {
                val result = handler.handleDeepLink(normalized)
                if (result != null) {
                    telemetry.onRouteHandled(normalized.toString(), handler.moduleId)
                    return result
                }
            }

            // No handler found for this URI
            telemetry.onRouteMiss(normalized.toString())
            return null
        }

        /**
         * Route a deep link string to the appropriate feature handler.
         *
         * @param deepLink The deep link string to route
         * @return DeepLinkResult if a handler can process the URI, null otherwise
         */
        fun route(deepLink: String): DeepLinkResult? =
            try {
                val normalized = UriNormalizer.normalize(Uri.parse(deepLink))
                route(normalized)
            } catch (e: Exception) {
                // Invalid URI format
                null
            }

        /**
         * Get all registered deep link patterns from all handlers.
         * Useful for documentation and validation.
         *
         * @return List of all supported deep link patterns
         */
        fun getAllSupportedPatterns(): List<String> =
            handlers.flatMap { handler ->
                handler.supportedPatterns().map { pattern ->
                    "${handler.moduleId}: $pattern"
                }
            }
    }
