plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
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
    // Import internal platform BOM for version management

    // Core modules
    implementation(libs.local.core.common)

    // Ktor for networking
    implementation(platform(libs.ktor.bom))
    implementation(platform(libs.okhttp.bom))
    implementation(libs.bundles.ktor)
    implementation(libs.ktor.client.resources)
    implementation(libs.ktor.client.serialization)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Hilt for DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Core Android
    implementation(libs.androidx.core.ktx)
    
    // DataStore for secure token storage
    implementation(libs.androidx.datastore.preferences)
    
    // Timber for logging
    implementation(libs.timber)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)
}
