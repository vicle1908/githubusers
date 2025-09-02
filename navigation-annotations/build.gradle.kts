plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    // Quality plugins: our conventions now apply the underlying plugins internally
    id("githubusers.quality.detekt")
    id("githubusers.quality.ktlint")
    id("githubusers.android.library")
    id("githubusers.common.version")
    id("githubusers.android.publishing")
    id("githubusers.android.library.publishing")
}

dependencies {
    // Kotlin stdlib provided by android plugin; keep minimal
}
