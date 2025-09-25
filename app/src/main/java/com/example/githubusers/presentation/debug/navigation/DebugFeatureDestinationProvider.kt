package com.example.githubusers.presentation.debug.navigation

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.example.githubusers.navigation.api.FeatureDestinationProvider
import com.example.githubusers.presentation.DebugScreen
import javax.inject.Singleton

@Singleton
class DebugFeatureDestinationProvider : FeatureDestinationProvider {

    override fun canResolve(key: NavKey): Boolean = key is DebugNavKey

    override fun createEntry(key: NavKey, metadata: Map<String, Any>): NavEntry<NavKey> {
        val debugKey = key as? DebugNavKey
            ?: throw IllegalArgumentException("Unknown debug key: $key")
        return when (debugKey) {
            DebugNavKey.CorePagingShowcase -> NavEntry(debugKey, metadata = metadata) {
                DebugScreen()
            }
        }
    }
}
