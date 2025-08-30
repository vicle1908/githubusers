plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("githubusers.android.library")
    id("githubusers.android.hilt")
    id("githubusers.common.version")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.detekt)
    id("githubusers.android.publishing")
    id("githubusers.android.library.publishing")
}

dependencies {
    // Platform for version alignment
    api(platform(libs.internal.platform))
    
    implementation(libs.local.core.domain)
    implementation(libs.local.core.common)
    
    // Network - Ktor
    implementation(platform(libs.ktor.bom))
    implementation(libs.bundles.ktor)
    
    // Serialization
    implementation(libs.kotlinx.serialization.json)
    
    // Local storage
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    ksp(libs.androidx.room.compiler)
    
    // Dependency injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    
    // Paging
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.common)
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    
    // Testing
    testImplementation(libs.bundles.testing.unit)
    androidTestImplementation(libs.bundles.testing.android.base)
}
