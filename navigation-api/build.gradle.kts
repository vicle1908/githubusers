plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    // Quality plugins: our conventions now apply the underlying plugins internally
    id("githubusers.quality.detekt")
    id("githubusers.test.convention")
    id("githubusers.quality.ktlint")
    id("githubusers.android.library")
    id("githubusers.common.version")
    id("githubusers.android.publishing")
    id("githubusers.android.library.publishing")
}

dependencies {
    // Core dependencies only - no implementation details
    api(libs.kotlinx.serialization.json)
    api(libs.androidx.navigation3.runtime)

    // For Uri handling
    implementation(libs.androidx.core.ktx)

    // Unit testing
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
}
