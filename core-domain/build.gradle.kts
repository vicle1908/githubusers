plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
    id("githubusers.jvm.library")
    id("githubusers.common.version")
    alias(libs.plugins.kotlin.serialization)
    id("githubusers.android.publishing")
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    api(libs.androidx.paging.common)
    api(libs.kotlinx.serialization.json)
}
