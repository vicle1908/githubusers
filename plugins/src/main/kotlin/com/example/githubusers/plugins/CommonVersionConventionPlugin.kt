package com.example.githubusers.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Convention plugin for common version configuration.
 * Provides standardized version management and removes hardcoded versions.
 */
class CommonVersionConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Configure common project properties
            configureCommonProperties()
        }
    }
    
    private fun Project.configureCommonProperties() {
        // Set common project properties from version catalog
        group = "com.example.githubusers"
        version = "1.0.0" // Use default version for now
        
        // Set common extension properties
        extensions.extraProperties.set("compileSdk", 36)
        extensions.extraProperties.set("minSdk", 23)
        extensions.extraProperties.set("targetSdk", 36)
        extensions.extraProperties.set("jvmTarget", 21)
        extensions.extraProperties.set("kotlinCompilerExtensionVersion", "1.6.0")
    }
}
