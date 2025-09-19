plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    // Provide Compose and common processors so convention plugin resolution succeeds
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)

    // Apply our convention plugins
    id("githubusers.feature.module")
    id("githubusers.common.version")

    // Quality plugins (expose detekt/ktlint tasks for aggregates)
    id("githubusers.quality.detekt")
    id("githubusers.quality.ktlint")

    // Dependency update checks (ben-manes versions via convention plugin)
    id("githubusers.dependency.update")

    // Optional publishing hooks
    id("githubusers.android.publishing")
    id("githubusers.android.library.publishing")

    // Optional test conventions
    id("githubusers.test.convention")
}

dependencies {
    // Intentionally minimal for quality aggregation; add real deps with implementation phase
}