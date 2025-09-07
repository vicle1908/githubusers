plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    // Core dependencies for testing utilities
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)

    // JVM testing frameworks
    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.agent)
}
