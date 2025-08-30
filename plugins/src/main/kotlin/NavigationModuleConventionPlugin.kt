package com.example.githubusers.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Convention plugin for navigation modules that provides standardized configuration.
 * This plugin automatically applies common navigation module settings and dependencies.
 */
class NavigationModuleConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Auto-apply navigation module plugins
            pluginManager.apply("githubusers.android.library")
            pluginManager.apply("githubusers.quality.ktlint")
            pluginManager.apply("githubusers.quality.detekt")
            
            // Configure navigation-specific settings
            configureNavigationModule()
        }
    }
    
    private fun Project.configureNavigationModule() {
        // Configure module type extension
        extensions.configure<ModuleConfigExtension> {
            isNavigationModule = true
            hasCompose = false
            hasHilt = false
        }
        
        // Note: Dependencies will be configured by the individual modules
        // or by other convention plugins that have access to the version catalog
    }
}
