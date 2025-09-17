plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    // Apply our convention plugins
    id("githubusers.android.library")
    id("githubusers.common.version")
    // Quality plugins (expose detekt/ktlint tasks for aggregates)
    id("githubusers.quality.detekt")
    id("githubusers.quality.ktlint")
    // Optional publishing hooks (keeps parity with other modules)
    id("githubusers.android.publishing")
    id("githubusers.android.library.publishing")
    // Optional test conventions
    id("githubusers.test.convention")
}

dependencies {
    // Intentionally minimal; this module primarily provides security utilities/NDK
}