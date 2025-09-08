package com.example.githubusers.navigation.api

import kotlinx.serialization.Serializable

/**
 * Project-wide typed destinations for Navigation 3.
 * Only contains destinations that are truly shared across multiple features.
 * Feature-specific destinations should be owned by their respective feature modules.
 *
 * Currently empty as all destinations are now owned by their respective feature modules.
 */
@Serializable
sealed interface AppDestination {
    // All destinations are now owned by their respective feature modules
    // This interface is kept for future shared destinations if needed
}
