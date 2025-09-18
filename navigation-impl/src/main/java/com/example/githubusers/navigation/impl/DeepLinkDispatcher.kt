package com.example.githubusers.navigation.impl

import android.net.Uri
import androidx.core.net.toUri
import androidx.navigation3.runtime.NavKey
import com.example.githubusers.navigation.api.FeatureDeepLinkHandler
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

/**
 * Dispatches incoming URIs to the owning feature deep link handler and returns
 * a Navigation 3 destination key.
 */
@Singleton
class DeepLinkDispatcher @Inject constructor(private val handlers: Set<@JvmSuppressWildcards FeatureDeepLinkHandler>) {
    fun toKey(deepLink: String): NavKey? {
        Timber.tag("DeepLinkDispatcher").d("Converting deep link: $deepLink")
        val result = runCatching { deepLink.toUri() }.getOrNull()?.let { toKey(it) }
        Timber.tag("DeepLinkDispatcher").d("Result: $result")
        return result
    }

    fun toKey(uri: Uri): NavKey? {
        val normalized = normalize(uri)
        for (handler in handlers) {
            val key = handler.handleDeepLink(normalized)
            if (key != null) return key
        }
        return null
    }

    private fun normalize(uri: Uri): Uri = uri
}
