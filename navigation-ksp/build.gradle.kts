plugins {
    alias(libs.plugins.kotlin.jvm)
    // Quality plugins: our conventions now apply the underlying plugins internally
    id("githubusers.quality.detekt")
    id("githubusers.quality.ktlint")
    id("githubusers.jvm.library")
    id("githubusers.common.version")
    id("githubusers.android.publishing")
}

dependencies {
    // KSP symbol processing API
    api(libs.ksp.symbol.processing.api)
}
