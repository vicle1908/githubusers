package com.example.githubusers.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask

/**
 * Enhanced KtLint Convention Plugin with unified configuration support.
 *
 * Features:
 * - Android-specific code style configuration
 * - Consistent exclusion patterns across modules
 * - Integration with unified quality system
 * - Enhanced reporting and metrics
 */
class KtlintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Apply the KtLint plugin internally
            pluginManager.apply("org.jlleitschuh.gradle.ktlint")

            // Configure the KtLint plugin
            configureKtlintPlugin()
        }
    }

    private fun Project.configureKtlintPlugin() {
        // Configure ktlint defaults using the typed extension API
        val ext = extensions.getByType(KtlintExtension::class.java)

        // Use .editorconfig as the single source of truth for code style
        // (android_studio style is declared in .editorconfig)

        // Enable verbose output for better debugging
        ext.verbose.set(true)

        // Allow reports to be generated without failing the build
        ext.ignoreFailures.set(true)

        // Configure reporters for better integration
        ext.reporters {
            reporter(ReporterType.PLAIN)
            reporter(ReporterType.CHECKSTYLE)
            reporter(ReporterType.HTML)
        }

        // Exclude generated and build directories consistently (KSP-only)
        ext.filter {
            exclude("**/build/**")
            exclude("**/generated/**")
            exclude("**/build/generated/**")
            exclude("**/build/ksp/**")
            exclude("**/build/generated/ksp/**")
            exclude("**/build/intermediates/**")
            // Additional KSP exclusions for comprehensive coverage
            exclude("**/ksp/**")
        }

        // Wire ktlint checks into standard verification lifecycle
        tasks.matching { it.name == "check" }.configureEach {
            if (tasks.findByName("ktlintCheck") != null) {
                dependsOn("ktlintCheck")
            }
        }

        // Configure ktlint tasks for better performance
        tasks.withType(BaseKtLintCheckTask::class.java).configureEach {
            // Configuration cache compatible approach - avoid project access at execution time
            outputs.cacheIf { true }
        }
    }
}
