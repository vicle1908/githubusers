package com.example.githubusers.navigation.api

/**
 * Interface for feature modules to register their navigation components.
 *
 * Each feature module should implement this interface to provide:
 * - Navigation destinations
 * - Deep link handlers
 * - UI composables for screens
 *
 * Features are discovered via Hilt multibindings:
 *
 * ```kotlin
 * @Module
 * @InstallIn(SingletonComponent::class)
 * abstract class UserNavigationModule {
 *     @Binds
 *     @IntoSet
 *     abstract fun bindUserFeatureEntry(
 *         entry: UserFeatureEntry
 *     ): FeatureEntry
 * }
 * ```
 */
interface FeatureEntry {
    /**
     * Unique identifier for this feature module.
     * Should match the module name (e.g., "users", "search", "settings").
     */
    val featureId: String

    /**
     * The deep link handler for this feature.
     * Handles deep link resolution and destination creation.
     */
    val deepLinkHandler: DeepLinkHandler

    /**
     * Function that provides the screen composable for a given destination.
     * This allows features to own their UI while being composable into the main app.
     *
     * @param destination The destination to render
     * @param onNavigate Callback for navigation events
     * @return The composable content for the destination
     */
    fun getScreenComposable(
        destination: Destination,
        onNavigate: (NavCommand) -> Unit,
    ): Any // Will be @Composable function in actual implementation
}
