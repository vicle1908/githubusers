plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("githubusers.android.library")
    id("githubusers.android.library.compose")
    id("githubusers.android.hilt")
    id("maven-publish")
}

dependencies {
    // Core dependencies only - no implementation details
    api(libs.kotlinx.serialization.json)
    api(libs.androidx.navigation3.runtime)

    // For Uri handling
    implementation(libs.androidx.core.ktx)

    // Unit testing
    testImplementation(libs.junit)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            // Convention plugin handles groupId, artifactId, version
            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
