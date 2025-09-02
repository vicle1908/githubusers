plugins {
    alias(libs.plugins.kotlin.jvm)
    id("githubusers.jvm.library")
    id("githubusers.common.version")
    alias(libs.plugins.kotlin.serialization)
    id("githubusers.android.publishing")
    // Quality plugins: our conventions now apply the underlying plugins internally
    id("githubusers.quality.detekt")
    id("githubusers.quality.ktlint")
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    api(libs.androidx.paging.common)
    api(libs.kotlinx.serialization.json)
}
