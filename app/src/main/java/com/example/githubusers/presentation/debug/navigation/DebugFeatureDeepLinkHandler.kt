package com.example.githubusers.presentation.debug.navigation

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import com.example.githubusers.navigation.api.FeatureDeepLinkHandler
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class DebugFeatureDeepLinkHandler : FeatureDeepLinkHandler {
    override val moduleId: String = "app-debug"

    override fun supportedPatterns(): List<String> = listOf(DebugDeepLinks.CORE_PAGING)

    override fun handleDeepLink(uri: Uri): NavKey? {
        Timber.tag("DebugFeatureDeepLinkHandler").d("Handling deep link: %s", uri)
        val scheme = uri.scheme
        val host = uri.host
        val segments = uri.pathSegments
        val key = if (scheme.equals("app", ignoreCase = true) && host == "debug" && segments == listOf("core")) {
            DebugNavKey.CorePagingShowcase
        } else {
            null
        }
        Timber.tag("DebugFeatureDeepLinkHandler").d("Result: %s", key)
        return key
    }
}
