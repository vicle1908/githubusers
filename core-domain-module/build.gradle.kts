plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("githubusers.android.library")
    id("githubusers.android.publishing")
    id("githubusers.android.library.publishing")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    // Kotlin
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    
    // Paging - for common paging models
    api(libs.androidx.paging.common)
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
}
