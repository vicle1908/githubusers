package com.example.githubusers.plugins

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/**
 * Centralized helper that forces tooling-related dependencies (ktlint CLI, Detekt reporters, etc.)
 * to align with the versions declared in the shared version catalog. Several third-party Gradle
 * plugins pull older transitive dependencies by default; without this alignment `dependencyUpdates`
 * reports these as outdated even though the catalog already points to newer releases.
 */
internal fun Project.configureToolingVersionAlignment() {
    val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

    val overrides = mapOf(
        "com.pinterest.ktlint:ktlint-cli" to libs.findVersion("ktlintCli").get().requiredVersion,
        "com.pinterest.ktlint:ktlint-cli-reporter-baseline" to libs.findVersion("ktlintCli").get().requiredVersion,
        "com.pinterest.ktlint:ktlint-ruleset-standard" to libs.findVersion("ktlintCli").get().requiredVersion,
        "io.github.detekt.sarif4k:sarif4k" to libs.findVersion("sarif4k").get().requiredVersion,
        "io.github.oshai:kotlin-logging" to libs.findVersion("kotlinLogging").get().requiredVersion,
        "org.apache.logging.log4j:log4j-core" to libs.findVersion("log4jCore").get().requiredVersion,
        "org.jetbrains.kotlin:kotlin-stdlib" to libs.findVersion("kotlin").get().requiredVersion
    )

    configurations.configureEach {
        if (!isCanBeResolved) return@configureEach

        resolutionStrategy.eachDependency {
            overrides["${requested.group}:${requested.name}"]?.let { target ->
                if (requested.version != target) {
                    useVersion(target)
                    because("Align with centralized tooling versions")
                }
            }
        }
    }
}
