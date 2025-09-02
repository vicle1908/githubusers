@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("githubusers.android.library")
    id("githubusers.android.library.compose")
    id("githubusers.android.hilt")
    id("githubusers.common.version")
    id("githubusers.android.publishing")
    id("githubusers.android.library.publishing")
    // Quality plugins: our conventions now apply the underlying plugins internally
    id("githubusers.quality.detekt")
    id("githubusers.test.convention")
    id("githubusers.quality.ktlint")
}

dependencies {
    // Explicitly expose the base ViewModel artifact for KSP/Hilt visibility across modules
    api(libs.androidx.lifecycle.viewmodel)
    api(libs.androidx.lifecycle.viewmodel.compose)
    api(libs.androidx.lifecycle.runtime.compose)
    api(libs.androidx.lifecycle.runtime.ktx)
    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.kotlinx.coroutines.core)

    // No navigation runtime needed in core-mvi after decoupling

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
}
