plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.githubusers.shared.resources"
    
    compileSdk = libs.versions.sdk.compile.get().toInt()
    
    defaultConfig {
        minSdk = libs.versions.sdk.min.get().toInt()
    }
    
    buildFeatures {
        buildConfig = true
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    
    kotlinOptions {
        jvmTarget = libs.versions.jvm.target.get()
    }
}

dependencies {
    // No dependencies needed for a pure resources module
}
