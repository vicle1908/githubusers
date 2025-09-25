plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.room.gradle.plugin)
    id("githubusers.android.library")
    // Quality plugins: our conventions now apply the underlying plugins internally
    id("githubusers.quality.detekt")
    id("githubusers.quality.ktlint")
    id("githubusers.test.convention")
    id("githubusers.dependency.update")
    id("githubusers.android.room")
}

dependencies {
    api(libs.androidx.paging.runtime)
    api(libs.androidx.paging.common)
    api(libs.kotlinx.coroutines.core)
    implementation(libs.timber)
}
