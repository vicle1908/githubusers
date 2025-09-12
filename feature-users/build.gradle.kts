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
    id("githubusers.feature.module")
    id("githubusers.common.version")
    id("githubusers.android.publishing")
    id("githubusers.android.library.publishing")
}

dependencies {

    // Core modules
    implementation(libs.local.core.networking)
    implementation(libs.local.core.storage)
    implementation(libs.local.core.ui)

    // Ktor for networking (needed for HttpClient type)
    implementation(platform(libs.ktor.bom))
    implementation(libs.ktor.client.core)

    // Room for local database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    ksp(libs.androidx.room.compiler)

    // Core MVI base
    implementation(libs.local.core.mvi)

    // Navigation runtime for deep link dispatch + back stack
    implementation(libs.local.navigation.impl)

    // Compose and UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)

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

    // Hilt runtime + compiler for multibindings (@IntoSet) in this feature
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Unit tests (common ones provided by convention plugin)
}
