plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
    // Quality plugins: our conventions now apply the underlying plugins internally
    id("githubusers.quality.detekt")
    id("githubusers.test.convention")
    id("githubusers.quality.ktlint")
    id("githubusers.common.version")
}

dependencies {
    // Import internal platform BOM for version management
    implementation(platform(libs.internal.platform))

    // Core modules
    implementation(libs.local.navigation.annotations)

    // KSP API
    implementation(libs.ksp.symbol.processing.api)

    // Testing
    testImplementation(libs.junit)
}
