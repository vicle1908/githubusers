package com.example.githubusers.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Convention plugin for feature modules that provides standardized configuration.
 * This plugin automatically applies common feature module settings and dependencies.
 */
class FeatureModuleConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Auto-apply common feature module plugins
            pluginManager.apply("githubusers.android.library")
            pluginManager.apply("githubusers.android.library.compose")
            pluginManager.apply("githubusers.android.hilt")
            pluginManager.apply("githubusers.quality.ktlint")
            pluginManager.apply("githubusers.quality.detekt")
            
            // Configure feature-specific settings
            configureFeatureModule()
        }
    }
    
    private fun Project.configureFeatureModule() {
        // Configure module type extension
        extensions.configure<ModuleConfigExtension> {
            isFeatureModule = true
            hasCompose = true
            hasHilt = true
        }
        
        // Note: Dependencies will be configured by the individual modules
        // or by other convention plugins that have access to the version catalog
    }
}
