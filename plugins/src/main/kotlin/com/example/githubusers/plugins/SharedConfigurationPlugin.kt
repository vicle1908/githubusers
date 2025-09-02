package com.example.githubusers.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Comprehensive shared configuration plugin that provides common setup
 * for all modules in the composite build architecture.
 */
class SharedConfigurationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Apply common configuration that all modules need
            configureCommonProperties()
        }
    }
}

private fun Project.configureCommonProperties() {
    // Set common project properties
    extensions.extraProperties.set("compileSdk", 36)
    extensions.extraProperties.set("minSdk", 24)
    extensions.extraProperties.set("targetSdk", 36)
    extensions.extraProperties.set("jvmTarget", 21)
    extensions.extraProperties.set("kotlinCompilerExtensionVersion", "1.6.0")
}
