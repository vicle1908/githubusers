plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    // Quality plugins: our conventions now apply the underlying plugins internally
    id("githubusers.quality.detekt")
    id("githubusers.test.convention")
    id("githubusers.quality.ktlint")
    id("githubusers.android.library")
    id("githubusers.android.library.compose")
    id("githubusers.android.hilt")
    id("githubusers.common.version")
    id("githubusers.android.publishing")
    id("githubusers.android.library.publishing")
}

dependencies {
    // Import internal platform BOM for version management (implementation since we don't expose it)
    implementation(platform(libs.internal.platform))

    // Core modules
    implementation(libs.local.core.networking)
    implementation(libs.local.core.storage)
    implementation(libs.local.navigation.annotations)
    ksp(libs.local.navigation.ksp)

    // Navigation API for deep links
    implementation(libs.local.navigation.api)

    // Core MVI base
    implementation(libs.local.core.mvi)

    // Compose and UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)

    // Hilt for DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Paging
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    // Image loading
    implementation(platform(libs.coil.bom))
    implementation(libs.coil.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Hilt navigation-compose for hiltViewModel()
    implementation(libs.androidx.hilt.navigation.compose)

    // Core
    implementation(libs.androidx.core.ktx)

    // Unit tests
    testImplementation(libs.junit)
}
