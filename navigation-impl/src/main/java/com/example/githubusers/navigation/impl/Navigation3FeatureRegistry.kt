package com.example.githubusers.navigation.impl

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.material3.Text
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.example.githubusers.navigation.api.FeatureDestinationProvider
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

/**
 * Central registry that coordinates Navigation 3 key → NavEntry resolution
 * while preserving feature ownership (each feature provides a resolver).
 *
 * This follows the official Navigation 3 pattern of simple entry provider
 * delegation without complex caching or pooling logic.
 */
@Singleton
class Navigation3FeatureRegistry @Inject constructor(
    private val providers: Set<@JvmSuppressWildcards FeatureDestinationProvider>
) {

    /**
     * Creates a simple entry provider that delegates to feature providers.
     * This follows the official Navigation 3 pattern of using a when expression
     * to map NavKey types to their corresponding NavEntry content.
     */
    fun createEntryProvider(): (NavKey) -> NavEntry<NavKey> = { key ->
        val provider = findProvider(key)
        if (provider == null) {
            createErrorEntry(key, "Unknown destination: $key")
        } else {
            runCatching { createEntryWithMetadata(key, provider) }
                .getOrElse { e ->
                    Timber.tag("Navigation3FeatureRegistry").e(e, "Error creating entry for $key")
                    createErrorEntry(key, "Error loading destination: $key")
                }
        }
    }

    /**
     * Find the appropriate provider for the given NavKey.
     * Uses simple iteration since the number of providers is typically small.
     */
    private fun findProvider(key: NavKey): FeatureDestinationProvider? = providers.firstOrNull { it.canResolve(key) }

    /**
     * Create NavEntry with transition specifications, scene strategy metadata,
     * and NavigationEventInfo from the provider.
     * This enables feature modules to define custom transition animations, scene strategies, and gesture handling.
     */
    private fun createEntryWithMetadata(key: NavKey, provider: FeatureDestinationProvider): NavEntry<NavKey> {
        val transitionSpec = provider.getTransitionSpec(key)
        val popTransitionSpec = provider.getPopTransitionSpec(key)
        val dialogProperties = provider.getDialogProperties(key)
        val navigationEventInfo = provider.getNavigationEventInfo(key)
        val supportsPredictiveBack = provider.supportsPredictiveBack(key)

        val hasCustomizations =
            transitionSpec != null ||
                popTransitionSpec != null ||
                dialogProperties != null ||
                navigationEventInfo != null ||
                supportsPredictiveBack

        return if (hasCustomizations) {
            // Create entry with custom transition specs, scene strategy metadata,
            // NavigationEventInfo, and predictive back support
            val metadata = mutableMapOf<String, Any>()

            transitionSpec?.let {
                @Suppress("UNCHECKED_CAST")
                metadata.putAll(
                    NavDisplay.transitionSpec(
                        it as (AnimatedContentTransitionScope<*>.() -> ContentTransform)
                    )
                )
            }
            popTransitionSpec?.let {
                @Suppress("UNCHECKED_CAST")
                metadata.putAll(
                    NavDisplay.popTransitionSpec(
                        it as (AnimatedContentTransitionScope<*>.() -> ContentTransform)
                    )
                )
            }
            dialogProperties?.let {
                metadata.putAll(DialogSceneStrategy.dialog(it))
            }
            navigationEventInfo?.let {
                metadata["navigationEventInfo"] = it
            }

            // Note: Predictive back gesture support is available in the API but requires NavigationEventSwipeEdge
            // which is not available in the current version. This will be implemented when the API is stable.
            // if (supportsPredictiveBack) {
            //     val predictivePopTransitionSpec = provider.getPredictivePopTransitionSpec(key)
            //     if (predictivePopTransitionSpec != null) {
            //         metadata.putAll(NavDisplay.predictivePopTransitionSpec(predictivePopTransitionSpec))
            //     }
            // }

            // Pass metadata to the provider's createEntry method
            provider.createEntry(key, metadata)
        } else {
            // Use the provider's default createEntry method
            provider.createEntry(key)
        }
    }

    /**
     * Create error entry for unknown or failed destinations.
     */
    private fun createErrorEntry(key: NavKey, message: String): NavEntry<NavKey> = NavEntry(key) {
        Text(text = message)
    }
}
