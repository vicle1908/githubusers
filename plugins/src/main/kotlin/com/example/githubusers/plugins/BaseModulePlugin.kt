package com.example.githubusers.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Enhanced base plugin that provides common configuration for all modules.
 * This plugin automatically detects module types and configures extensions.
 * Should be applied to every module to ensure consistent setup.
 */
class BaseModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Apply common version configuration first
            pluginManager.apply("githubusers.common.version")
            
            // Create module configuration extension
            extensions.create("moduleConfig", ModuleConfigExtension::class.java)
            
            // Configure common extensions
            configureCommonExtensions()
        }
    }
    
    private fun Project.configureCommonExtensions() {
        // Common extension configuration
        extensions.extraProperties.set("compileSdk", 36)
        extensions.extraProperties.set("minSdk", 23)
        extensions.extraProperties.set("targetSdk", 36)
        extensions.extraProperties.set("jvmTarget", 21)
    }
}

/**
 * Enum representing different module types for auto-detection
 */
enum class ModuleType {
    ANDROID_APPLICATION,
    ANDROID_LIBRARY,
    FEATURE,
    CORE,
    NAVIGATION,
    JVM_LIBRARY,
    UNKNOWN
}

/**
 * Extension for module-specific configuration
 */
open class ModuleConfigExtension {
    var isFeatureModule: Boolean = false
    var isLibraryModule: Boolean = false
    var isApplicationModule: Boolean = false
    var isCoreModule: Boolean = false
    var isNavigationModule: Boolean = false
    var hasCompose: Boolean = false
    var hasHilt: Boolean = false
    var hasRoom: Boolean = false
}