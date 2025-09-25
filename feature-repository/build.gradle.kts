plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)

    id("githubusers.quality.detekt")
    id("githubusers.test.convention")
    id("githubusers.quality.ktlint")
    id("githubusers.feature.module")
    id("githubusers.android.room")
    id("githubusers.dependency.update")
}

dependencies {
    implementation(libs.local.core.common)
    implementation(libs.local.core.ui)
    implementation(libs.local.navigation.api)
    implementation(libs.local.core.networking)
    implementation(libs.local.core.paging)
    implementation(libs.local.core.search)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.local.core.storage)
    implementation(libs.timber)
    implementation(libs.ktor.client.core)

    testImplementation(libs.robolectric)
    testImplementation(libs.ktor.client.content.negotiation)
    testImplementation(libs.kotlinx.serialization.json)
}
