plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.room.gradle.plugin)
}

android {
    namespace =
        libs.versions.application.id
            .get()
    compileSdk =
        libs.versions.sdk.compile
            .get()
            .toInt()

    defaultConfig {
        applicationId =
            libs.versions.application.id
                .get()
        minSdk =
            libs.versions.sdk.min
                .get()
                .toInt()
        targetSdk =
            libs.versions.sdk.target
                .get()
                .toInt()
        versionCode =
            libs.versions.app.version.code
                .get()
                .toInt()
        versionName =
            libs.versions.app.version.name
                .get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true // Enables code shrinking for release
            isShrinkResources = true // Removes unused resources
            enableUnitTestCoverage = false
            enableAndroidTestCoverage = false
        }

        getByName("debug") {
            isDebuggable = true // Enable debugging in debug builds
            applicationIdSuffix = ".debug" // Differentiate between release and debug builds
            versionNameSuffix = "-debug" // Adds '-debug' suffix to the version name
            isMinifyEnabled = false // Disable code shrinking in debug build
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget =
            libs.versions.jvm.target
                .get()
    }

    buildFeatures {
        compose = true
    }

    lint {
        abortOnError = true
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    // Ktor dependencies
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.serialization.kotlinx)
    implementation(libs.ktor.client.content)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.logging)

    // Coroutines dependencies
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // Compose dependencies
    implementation(platform(libs.compose.bom))
    implementation(libs.material3)
    implementation(libs.navigation.compose)
    implementation(libs.compose.ui)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.activity.compose)
    implementation(libs.ui.tooling.preview)
    implementation(libs.appcompat)
    debugImplementation(libs.ui.tooling)

    // Hilt dependencies
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // Room dependencies
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.room.paging)

    // Coil for image loading
    implementation(libs.coil.compose)

    // Paging
    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)

    // Room Testing
    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.room.testing)
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.agent)
    testImplementation(libs.mockk.android)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.paging.testing)
}
