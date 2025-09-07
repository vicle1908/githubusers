package com.example.githubusers.navigation.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class OwnsDeepLinks(
    val moduleId: String,
)
