package com.example.githubusers.navigation.annotations

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class DeepLinkSpec(
    val patterns: Array<String>,
)
