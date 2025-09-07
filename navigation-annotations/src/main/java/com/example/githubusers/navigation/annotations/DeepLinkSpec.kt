package com.example.githubusers.navigation.annotations

/**
 * Annotation to specify deep link information for a destination.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class DeepLinkSpec(
    val route: String,
    val deepLink: String
)
