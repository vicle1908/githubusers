package com.example.githubusers.plugins

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Convention plugin for configuring automated dependency update checking
 */
class DependencyUpdatePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.github.ben-manes.versions")
            }

            configureDependencyUpdates()
            createDependencyUpdateTask()
            createSecurityScanTask()
        }
    }
}

/**
 * Configures automated dependency update checking for GitHub Users project
 */
private fun Project.configureDependencyUpdates() {
    tasks.withType(DependencyUpdatesTask::class.java).configureEach {
        // The ben-manes versions plugin is not configuration-cache safe on Gradle 9 yet.
        // Mark the task as incompatible so Gradle will disable CC for invocations that include it.
        notCompatibleWithConfigurationCache("ben-manes versions plugin not CC-safe on Gradle 9")

        // Gradle release channel
        gradleReleaseChannel = "current"

        // Configure update resolution strategy to reject unstable versions
        rejectVersionIf {
            isNonStable(candidate.version) && !isNonStable(currentVersion)
        }

        // Configure output format
        outputFormatter = "json,xml,html"
        outputDir = "build/dependencyUpdates"
        reportfileName = "report"

        // Filter dependencies to check
        checkForGradleUpdate = true
        checkConstraints = true
        checkBuildEnvironmentConstraints = true
    }
}

/**
 * Determines if a version string represents a non-stable release
 */
private fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

/**
 * Creates a task for automatically applying dependency updates
 */
private fun Project.createDependencyUpdateTask() {
    tasks.register("updateDependencies") {
        group = "dependencies"
        description = "Updates project dependencies to latest stable versions"

        // Delegates to dependencyUpdates task whose plugin is not CC-safe on Gradle 9.
        notCompatibleWithConfigurationCache("Delegates to dependencyUpdates task which is not configuration-cache compatible")

        doLast {
            logger.lifecycle("Checking for dependency updates...")

            // Run dependency updates task
            project.tasks.named("dependencyUpdates").get().actions.forEach { action ->
                action.execute(project.tasks.named("dependencyUpdates").get())
            }

            logger.lifecycle("Dependency update check completed. Check build/dependencyUpdates/report.html for results.")
        }
    }
}

/**
 * Creates automated security vulnerability scanning task
 */
private fun Project.createSecurityScanTask() {
    tasks.register("securityScan") {
        group = "verification"
        description = "Scans dependencies for known security vulnerabilities"

        doLast {
            logger.lifecycle("Scanning dependencies for security vulnerabilities...")

            // This would integrate with tools like OWASP Dependency Check
            // For now, we'll output a placeholder
            logger.lifecycle(
                """
                Security scan would check:
                - Known CVEs in dependencies
                - License compliance
                - Outdated security-critical libraries
                
                Consider integrating with:
                - OWASP Dependency Check
                - Snyk
                - GitHub Security Advisories
                """.trimIndent()
            )
        }
    }
}
