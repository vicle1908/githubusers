plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    // Core dependencies for testing utilities
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.coroutines.test)

    // Serialization for JSON fixtures
    api(libs.kotlinx.serialization.json)

    // JVM testing frameworks
    api(libs.junit)
    api(libs.truth)
    api(libs.mockk)
    api(libs.mockk.agent)

    // MockWebServer for API testing
    api(libs.mockwebserver)

    // Robolectric for Android unit testing
    api(libs.robolectric)

    // Test implementation
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}
