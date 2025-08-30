plugins {
    alias(libs.plugins.kotlin.jvm)
    id("githubusers.jvm.library")
    id("githubusers.common.version")
    id("githubusers.android.publishing")
}

dependencies {
    // KSP symbol processing API
    api(libs.ksp.symbol.processing.api)
}

