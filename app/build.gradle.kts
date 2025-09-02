import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room.gradle.plugin)
    id("githubusers.android.application")
    id("githubusers.android.application.compose")
    id("githubusers.android.hilt")
    id("githubusers.android.room")
    alias(libs.plugins.version.update)
    // Quality plugins: our conventions now apply the underlying plugins internally
    id("githubusers.quality.detekt")
    id("githubusers.test.convention")
    id("githubusers.quality.ktlint")
}

// Configure application-specific settings via convention plugin extensions
extensions.configure<com.example.githubusers.plugins.ApplicationConfigExtension>(
    "appConfig",
) {
    applicationId =
        libs.versions.application.id
            .get()
    versionCode =
        libs.versions.app.version.code
            .get()
            .toInt()
    versionName =
        libs.versions.app.version.name
            .get()
    testInstrumentationRunner = "com.example.githubusers.HiltTestRunner"
    enableNav3Persistence = true
    enableNav3PersistenceWrite = true
}

// Configure NDK settings via convention plugin extensions
extensions.configure<com.example.githubusers.plugins.NdkExtension>(
    "ndkConfig",
) {
    ndkVersion = libs.versions.ndk.get()
    cmakeVersion = libs.versions.ndk.get()
    cmakePath = "src/main/cpp/CMakeLists.txt"
}

// Temporary android block for namespace until convention plugin is fully working
android {
    namespace =
        libs.versions.application.id
            .get()
}

dependencies {
    // Core modules
    implementation(libs.local.core.data)
    implementation(libs.local.core.domain)

    // Feature modules
    implementation(libs.local.feature.users.list)
    implementation(libs.local.feature.users.detail)
    implementation(libs.local.feature.search)

    // Navigation modules (composite builds)
    implementation(libs.local.navigation.api)
    implementation(libs.local.navigation.impl)

    // Hilt for dependency injection
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Navigation 3
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    // Networking
    implementation(platform(libs.ktor.bom))
    implementation(platform(libs.okhttp.bom))
    implementation(libs.bundles.ktor)
    implementation(libs.ktor.client.resources)
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.client.mock)

    // Image Loading
    implementation(platform(libs.coil.bom))
    implementation(libs.coil.compose)

    // Paging
    implementation(libs.bundles.paging)

    // Room Paging support
    implementation(libs.androidx.room.paging)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.androidx.paging.testing)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)
}

// Version updates configuration
fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

tasks.withType<DependencyUpdatesTask> {
    gradleReleaseChannel = "current"

    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}
