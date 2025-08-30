package com.example.githubusers.navigation.impl

/**
 * Optional ownership registry for deep links. Implementations may be generated via KSP.
 */
interface DeepLinkOwnershipSource {
    /** Map of moduleId -> owner class (for debugging/trace). */
    val owners: Map<String, String>
}

/** Default no-op registry when no generated registry is on classpath. */
object NoOpDeepLinkOwnershipSource : DeepLinkOwnershipSource {
    override val owners: Map<String, String> = emptyMap()
}
