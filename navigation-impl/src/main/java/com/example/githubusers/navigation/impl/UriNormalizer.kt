package com.example.githubusers.navigation.impl

import android.net.Uri

/**
 * Normalizes inbound URIs before resolution.
 * - Lowercase host
 * - Normalize slashes
 * - Strip UTM parameters
 * - Ensure encoding consistency
 */
object UriNormalizer {
    private val stripParams = setOf("utm_source", "utm_medium", "utm_campaign", "utm_term", "utm_content")

    fun normalize(input: Uri): Uri {
        var host = input.host?.lowercase()
        val scheme = input.scheme
        val path = input.path ?: ""
        val builder =
            Uri
                .Builder()
                .scheme(scheme)
                .authority(host)
                .path(path.replace(Regex("/+"), "/"))

        input.queryParameterNames
            .filterNot { it in stripParams }
            .sorted()
            .forEach { key ->
                input.getQueryParameters(key).forEach { value ->
                    builder.appendQueryParameter(key, value)
                }
            }

        return builder.build()
    }
}
