plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    // Apply our convention plugins
    id("githubusers.android.library")
    id("githubusers.common.version")
    id("githubusers.android.hilt")
    // Quality plugins (expose detekt/ktlint tasks for aggregates)
    id("githubusers.quality.detekt")
    id("githubusers.quality.ktlint")
    // Optional publishing hooks (keeps parity with other modules)
    id("githubusers.android.publishing")
    id("githubusers.android.library.publishing")
    // Optional test conventions
    id("githubusers.test.convention")
}

// Configure Android with NDK support
android {
    namespace = "com.example.githubusers.core.security"

    defaultConfig {
        minSdk = libs.versions.sdk.min.get().toInt()
        consumerProguardFiles("consumer-proguard-rules.pro")

        externalNativeBuild {
            cmake {
                cppFlags("-std=c++17", "-fvisibility=hidden")
                arguments("-DANDROID_STL=c++_shared")
                abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
            }
        }
        ndk {
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }

    packaging {
        jniLibs {
            useLegacyPackaging = false
            pickFirsts += listOf("**/libc++_shared.so")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    
    // Hilt for dependency injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    
    // Testing dependencies
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)
}
