package com.example.githubusers.navigation.api

import android.net.Uri

/**
 * Interface for handling deep links in feature modules.
 * Each feature module should implement this to handle its own deep links.
 */
interface DeepLinkHandler {
    /**
     * Unique identifier for this module's deep link handler
     */
    val moduleId: String

    /**
     * List of deep link patterns this handler supports.
     * Used for documentation and validation.
     */
    fun supportedPatterns(): List<String>

    /**
     * Handle a deep link URI and return navigation result.
     * Returns null if this handler doesn't support the given URI.
     */
    fun handleDeepLink(uri: Uri): DeepLinkResult?
}

/**
 * Result of deep link handling
 */
data class DeepLinkResult(
    val destination: AppDestination,
    val arguments: Map<String, String> = emptyMap(),
    val clearBackStack: Boolean = false,
    val singleTop: Boolean = true,
    val popUpTo: String? = null,
    val popUpToInclusive: Boolean = false,
)

/**
 * Extension function to safely get boolean query parameters
 */
fun Uri.getBooleanQueryParameter(
    key: String,
    defaultValue: Boolean = false,
): Boolean = getQueryParameter(key)?.toBooleanStrictOrNull() ?: defaultValue
