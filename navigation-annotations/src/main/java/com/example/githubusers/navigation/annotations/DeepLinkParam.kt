package com.example.githubusers.navigation.annotations

/**
 * Annotation to specify deep link parameters.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class DeepLinkParam(
    val name: String
)
