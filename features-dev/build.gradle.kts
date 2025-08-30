plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.detekt)
    id("githubusers.android.library")
    id("githubusers.android.hilt")
    id("githubusers.android.publishing")
    id("githubusers.android.library.publishing")
}

dependencies {
    // Hilt dependencies are handled by convention plugin

    // Export all feature modules for dev flavor
    api(libs.local.feature.users.list)
    api(libs.local.feature.users.detail)
    api(libs.local.feature.search)

    // Shared modules
    api(libs.local.core.domain)
    api(libs.local.navigation.api)
}
