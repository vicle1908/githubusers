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
    id("githubusers.dependency.update")
}

dependencies {
    // Compose BOM to provide versions for androidx.compose artifacts
    implementation(platform(libs.androidx.compose.bom))
    // Core dependencies only - no implementation details
    api(libs.kotlinx.serialization.json)
    api(libs.androidx.navigation3.runtime)
    api(libs.local.core.common)
    implementation(libs.androidx.navigationevent)
    api(libs.androidx.navigationevent.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.core.ktx)

    // Unit testing
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
}
