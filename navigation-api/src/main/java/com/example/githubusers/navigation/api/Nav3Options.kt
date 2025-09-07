package com.example.githubusers.navigation.api

/**
 * Common builders for [NavigationOptions] to promote consistent back stack behavior.
 */
object Nav3Options {
    /** SingleTop navigation: avoid duplicating the same destination at the top. */
    fun singleTop(): NavigationOptions =
        NavigationOptions(
            singleTop = true,
        )

    /**
     * Switch within a logical section (e.g., settings subsections) while keeping the
     * section root as the back stack parent.
     */
    fun switchSection(rootDeepLink: String): NavigationOptions =
        NavigationOptions(
            singleTop = true,
            popUpTo = rootDeepLink,
            popUpToInclusive = false,
        )
}
