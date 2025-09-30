plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room.gradle.plugin)
    id("githubusers.android.application")
    id("githubusers.android.application.compose")
    id("githubusers.android.hilt")
    id("githubusers.android.room")
    id("githubusers.firebase.performance")
    // Quality plugins: our conventions now apply the underlying plugins internally
    id("githubusers.quality.detekt")
    id("githubusers.test.convention")
    id("githubusers.quality.ktlint")
    id("githubusers.dependency.update")
}

// Configure application-specific settings via convention plugin extensions
extensions.configure<com.example.githubusers.plugins.ApplicationConfigExtension>(
    "appConfig"
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

extensions.configure<com.example.githubusers.plugins.NdkExtension>("ndkConfig") {
    ndkVersion = libs.versions.ndk.get()
    cmakeVersion = libs.versions.cmake.get()
    cmakePath = "src/main/cpp/CMakeLists.txt"
}

// Native toolchain configuration is provided via the ndkConfig extension above

// Advanced build optimizations and configuration
android {
    namespace =
        libs.versions.application.id
            .get()

    // Build optimizations
    buildTypes {
        debug {
            // Development optimizations
            isDebuggable = true
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"

            buildConfigField("boolean", "ENABLE_CRASH_REPORTING", "false")
            buildConfigField("boolean", "ENABLE_ANALYTICS", "false")
        }

        release {
            // Production optimizations
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true

            // Enable R8 full mode for better optimization
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // Performance and analytics in release
            buildConfigField("boolean", "ENABLE_CRASH_REPORTING", "true")
            buildConfigField("boolean", "ENABLE_ANALYTICS", "true")

            // Signature configuration for release builds
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    // Compilation optimizations
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

    // Bundle configuration for optimal APK splits
    bundle {
        language {
            // Disable language splits for now
            enableSplit = false
        }
        density {
            // Enable density splits for smaller APKs
            enableSplit = true
        }
        abi {
            // Enable ABI splits for smaller APKs
            enableSplit = true
        }
    }
}

dependencies {
    // Core modules
    implementation(libs.local.core.ui)
    implementation(libs.local.core.security)
    implementation(libs.local.core.paging)
    implementation(libs.local.core.search)

    // Feature modules
    implementation(libs.local.feature.users)
    implementation(libs.local.feature.search)
    implementation(libs.local.feature.settings)
    implementation(libs.local.feature.repository)

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

    // Image Loading (provided by core-ui module)
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

    // Core library desugaring for modern Java APIs
    coreLibraryDesugaring(libs.android.desugarJdkLibs)

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
