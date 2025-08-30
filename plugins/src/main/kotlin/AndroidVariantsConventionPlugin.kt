package com.example.githubusers.plugins

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Convention plugin to apply consistent build variants (flavors and types) across all Android modules.
 * 
 * This plugin:
 * - Adds flavor dimension "environment" with "dev" and "prod" flavors
 * - Configures build types: debug, release, and staging
 * - Sets up missingDimensionStrategy for cross-module compatibility
 * - Applies consistent suffixes and build settings
 */
open class VariantsExtension {
    var defaultEnvironment: String = "prod" // or "dev"
}

class AndroidVariantsConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Extension to allow per-module overrides
            val ext = extensions.create("androidVariants", VariantsExtension::class.java)

            // Configure for application extension
            pluginManager.withPlugin("com.android.application") {
                extensions.configure<ApplicationExtension> {
                    configureApplicationVariants(target, ext)
                }
            }
            
            // Configure for library extension
            pluginManager.withPlugin("com.android.library") {
                extensions.configure<LibraryExtension> {
                    configureLibraryVariants(target, ext)
                }
            }
        }
    }
    
    private fun ApplicationExtension.configureApplicationVariants(project: Project, ext: VariantsExtension) {
        // Define flavor dimensions
        flavorDimensions += "environment"
        
        // Configure product flavors
        productFlavors {
            create("dev") {
                dimension = "environment"
                applicationIdSuffix = ".dev"
                versionNameSuffix = "-dev"
                
                // Dev-specific configuration
                buildConfigField("String", "ENVIRONMENT", "\"dev\"")
                buildConfigField("String", "BASE_URL", "\"https://api.github.com\"")
                buildConfigField("boolean", "ENABLE_LOGGING", "true")
                buildConfigField("boolean", "ENABLE_DEBUG_MENU", "true")
            }
            
            create("prod") {
                dimension = "environment"
                
                // Prod-specific configuration
                buildConfigField("String", "ENVIRONMENT", "\"prod\"")
                buildConfigField("String", "BASE_URL", "\"https://api.github.com\"")
                buildConfigField("boolean", "ENABLE_LOGGING", "false")
                buildConfigField("boolean", "ENABLE_DEBUG_MENU", "false")
            }
        }
        
        // Configure build types
        buildTypes {
            getByName("debug") {
                isDebuggable = true
                enableUnitTestCoverage = true
                enableAndroidTestCoverage = true
                isShrinkResources = false
                isMinifyEnabled = false
            }
            
            getByName("release") {
                isDebuggable = false
                enableUnitTestCoverage = false
                enableAndroidTestCoverage = false
                isShrinkResources = true
                isMinifyEnabled = true
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
            
            // Create staging build type
            create("staging") {
                initWith(getByName("debug"))
                applicationIdSuffix = ".staging"
                versionNameSuffix = "-staging"
                isDebuggable = true
                enableUnitTestCoverage = false
                enableAndroidTestCoverage = false
                matchingFallbacks += listOf("debug")
                
                // Staging-specific configuration
                buildConfigField("String", "BUILD_TYPE", "\"staging\"")
                buildConfigField("boolean", "ENABLE_STAGING_FEATURES", "true")
            }
        }
        
        // Set default configuration for missing dimensions
        defaultConfig {
            val prop = project.findProperty("defaultEnvironment") as String?
            val defaultEnv = (prop ?: ext.defaultEnvironment).lowercase()
            val chosen = if (defaultEnv == "dev") "dev" else "prod"
            missingDimensionStrategy("environment", chosen)
        }
        
        // Configure build features
        buildFeatures {
            buildConfig = true
        }
    }
    
    private fun LibraryExtension.configureLibraryVariants(project: Project, ext: VariantsExtension) {
        // Define flavor dimensions
        flavorDimensions += "environment"
        
        // Configure product flavors
        productFlavors {
            create("dev") {
                dimension = "environment"
                
                // Dev-specific configuration
                buildConfigField("String", "ENVIRONMENT", "\"dev\"")
                buildConfigField("String", "BASE_URL", "\"https://api.github.com\"")
                buildConfigField("boolean", "ENABLE_LOGGING", "true")
                buildConfigField("boolean", "ENABLE_DEBUG_MENU", "true")
            }
            
            create("prod") {
                dimension = "environment"
                
                // Prod-specific configuration
                buildConfigField("String", "ENVIRONMENT", "\"prod\"")
                buildConfigField("String", "BASE_URL", "\"https://api.github.com\"")
                buildConfigField("boolean", "ENABLE_LOGGING", "false")
                buildConfigField("boolean", "ENABLE_DEBUG_MENU", "false")
            }
        }
        
        // Configure build types
        buildTypes {
            getByName("debug") {
                isMinifyEnabled = false
                enableUnitTestCoverage = true
            }
            
            getByName("release") {
                isMinifyEnabled = false
                enableUnitTestCoverage = false
                consumerProguardFiles("consumer-rules.pro")
            }
            
            // Create staging build type
            create("staging") {
                initWith(getByName("debug"))
                isMinifyEnabled = false
                enableUnitTestCoverage = false
                matchingFallbacks += listOf("debug")
                
                // Staging-specific configuration
                buildConfigField("String", "BUILD_TYPE", "\"staging\"")
                buildConfigField("boolean", "ENABLE_STAGING_FEATURES", "true")
            }
        }
        
        // Set default configuration for missing dimensions
        defaultConfig {
            val prop = project.findProperty("defaultEnvironment") as String?
            val defaultEnv = (prop ?: ext.defaultEnvironment).lowercase()
            val chosen = if (defaultEnv == "dev") "dev" else "prod"
            missingDimensionStrategy("environment", chosen)
        }
        
        // Configure build features
        buildFeatures {
            buildConfig = true
        }
    }
}
