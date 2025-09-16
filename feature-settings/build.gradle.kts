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
    id("githubusers.feature.module")
    id("githubusers.common.version")
    id("githubusers.dependency.update")
}

dependencies {
    // Internal platform BOM for consistent versions

    // Core modules (reuse shared UI/MVI primitives, no duplication)
    implementation(libs.local.core.ui)
    implementation(libs.local.core.mvi)
    implementation(libs.local.core.storage)

    // Navigation API (provided by convention plugin)

    // Compose and UI basics (for any future settings UI)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.hilt.navigation.compose)

    // Hilt runtime + compiler for feature DI (@IntoSet bindings)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Hilt and Core Android (provided by convention plugin)
    // DataStore for preferences
    implementation(libs.androidx.datastore.preferences)

    // Navigation3
    implementation(libs.androidx.navigation3.runtime)

    // Testing (common ones provided by convention plugin)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
