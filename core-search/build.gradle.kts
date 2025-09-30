plugins {
    alias(libs.plugins.kotlin.jvm)
    id("githubusers.jvm.library")
    id("githubusers.common.version")
    alias(libs.plugins.kotlin.serialization)
    // id("githubusers.android.publishing") // Removed for JVM library
    id("githubusers.quality.detekt")
    id("githubusers.quality.ktlint")
    id("githubusers.test.convention")
    id("githubusers.dependency.update")
}

dependencies {
    // Pure JVM facade; no Android/Hilt dependencies to avoid AAR variant issues
    // If DI is needed, wire in platform modules instead and keep core-common dependency-free for Android
    api(libs.kotlinx.coroutines.core)
    api(libs.androidx.paging.common)
    api(libs.kotlinx.serialization.json)
    implementation(libs.androidx.datastore.preferences)
}

kotlin {
    sourceSets {
        val test by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}
