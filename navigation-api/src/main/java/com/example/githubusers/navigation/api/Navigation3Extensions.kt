package com.example.githubusers.navigation.api

/** Convenience extensions around [Navigation3Controller]. */
fun Navigation3Controller.navigateSingleTop(deepLink: String) {
    navigate(deepLink, Nav3Options.singleTop())
}

/** Switch logical section keeping [rootDeepLink] as parent. */
fun Navigation3Controller.switchSection(
    deepLink: String,
    rootDeepLink: String,
) {
    navigate(deepLink, Nav3Options.switchSection(rootDeepLink))
}

/**
 * Try navigating up in-app. Returns true if handled. If false, callers should
 * allow system back to finish the activity rather than forcing an alternate route.
 */
fun Navigation3Controller.navigateUpOrFinish(): Boolean = navigateUp()
