plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.detekt)
    id("githubusers.quality.detekt")
    id("githubusers.test.convention")
    id("githubusers.quality.ktlint")
    id("githubusers.android.library")
    id("githubusers.android.library.compose")
    id("githubusers.android.hilt")
    id("githubusers.common.version")
    id("githubusers.android.publishing")
    id("githubusers.android.library.publishing")
    id("githubusers.dependency.update")
}

dependencies {
    implementation(libs.local.core.common)
    implementation(libs.local.core.search)

    // Compose
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.graphics)
    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material.icons.extended)

    // Paging (Compose integration)
    api(libs.androidx.paging.compose)
    api(libs.androidx.paging.runtime)

    // Compose utilities
    api(libs.androidx.activity.compose)
    api(libs.androidx.lifecycle.viewmodel.compose)
    api(libs.androidx.hilt.navigation.compose)

    // Image loading
    api(platform(libs.coil.bom))
    api(libs.coil.compose)
    implementation(libs.coil.network.ktor3)

    // UI utilities
    api(libs.androidx.core.ktx)
    api(libs.androidx.lifecycle.runtime.ktx)
    api(libs.androidx.lifecycle.runtime.compose)

    // Logging
    api(libs.timber)

    // Hilt for dependency injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Debug tooling
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Testing
    testImplementation(libs.bundles.testing.unit)
    androidTestImplementation(libs.bundles.testing.android)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}