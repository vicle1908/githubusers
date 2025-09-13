package com.example.githubusers.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Convention plugin for core modules that provides standardized configuration.
 * This plugin automatically applies common core module settings and dependencies.
 */
class CoreModuleConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Auto-apply core module plugins
            pluginManager.apply("githubusers.android.library")
            // Temporarily disabled due to Detekt compatibility issue
            // pluginManager.apply("githubusers.quality.ktlint")
            // pluginManager.apply("githubusers.quality.detekt")

            // Configure core-specific settings
            configureCoreModule()
        }
    }

    private fun Project.configureCoreModule() {
        // Configure module type extension
        extensions.configure<ModuleConfigExtension> {
            isCoreModule = true
            hasCompose = false
            hasHilt = false
        }

        // Note: Dependencies will be configured by the individual modules
        // or by other convention plugins that have access to the version catalog
    }
}
