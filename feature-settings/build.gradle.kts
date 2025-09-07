plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    // Convention and quality plugins to align with project
    id("githubusers.quality.detekt")
    id("githubusers.test.convention")
    id("githubusers.quality.ktlint")
    id("githubusers.android.library")
    id("githubusers.android.library.compose")
    id("githubusers.android.hilt")
    id("githubusers.common.version")
}

dependencies {
    // Internal platform BOM for consistent versions
//    implementation(platform(libs.internal.platform))

    // Core modules (reuse shared UI/MVI primitives, no duplication)
    implementation(libs.local.core.ui)
    implementation(libs.local.core.mvi)
    implementation(libs.local.core.storage)

    // Navigation API (handler contract)
    implementation(libs.local.navigation.api)

    // Compose and UI basics (for any future settings UI)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.hilt.navigation.compose)

    // Hilt for DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Core Android
    implementation(libs.androidx.core.ktx)
    // DataStore for preferences
    implementation(libs.androidx.datastore.preferences)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
