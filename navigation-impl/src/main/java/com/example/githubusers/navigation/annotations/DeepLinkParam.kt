package com.example.githubusers.navigation.annotations

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.BINARY)
annotation class DeepLinkParam(
    val name: String,
)
