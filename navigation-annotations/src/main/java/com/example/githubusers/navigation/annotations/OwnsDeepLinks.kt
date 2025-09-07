package com.example.githubusers.navigation.annotations

/**
 * Annotation to mark classes that own deep links.
 * This is used by the KSP processor to generate deep link registry.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class OwnsDeepLinks
