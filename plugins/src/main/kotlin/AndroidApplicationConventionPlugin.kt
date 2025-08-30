package com.example.githubusers.plugins

import com.android.build.api.dsl.ApplicationExtension
import com.example.githubusers.buildlogic.configureKotlinAndroid
import com.example.githubusers.buildlogic.configureLint
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create

/**
 * Extension for NDK configuration in application modules
 */
open class NdkExtension {
    var ndkVersion: String? = null
    var cmakeVersion: String? = null
    var cmakePath: String? = null
}

/**
 * Extension for application-specific configuration
 */
open class ApplicationConfigExtension {
    var applicationId: String? = null
    var versionCode: Int = 1
    var versionName: String = "1.0"
    var testInstrumentationRunner: String = "androidx.test.runner.AndroidJUnitRunner"
    var enableNav3Persistence: Boolean = true
    var enableNav3PersistenceWrite: Boolean = true
    var missingDimensionStrategy: Map<String, String> = emptyMap()
}

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Create extensions for configuration
            extensions.create("ndkConfig", NdkExtension::class.java)
            extensions.create("appConfig", ApplicationConfigExtension::class.java)
            
            // Ensure required plugins are applied
            pluginManager.apply("com.android.application")
            pluginManager.apply("org.jetbrains.kotlin.android")
            // Apply project-wide quality conventions
            pluginManager.apply("githubusers.quality.ktlint")
            pluginManager.apply("githubusers.quality.detekt")
            
            pluginManager.withPlugin("com.android.application") {
                pluginManager.withPlugin("org.jetbrains.kotlin.android") {
                    extensions.configure<ApplicationExtension> {
                        configureKotlinAndroid(this)
                        configureLint()
                        
                        // Get app config once
                        val appConfig = extensions.findByName("appConfig") as? ApplicationConfigExtension
                        
                        // Set namespace from application ID
                        if (appConfig != null && !appConfig.applicationId.isNullOrBlank()) {
                            namespace = appConfig.applicationId
                        }
                        
                        // Use default values for SDK versions
                        defaultConfig.targetSdk = 36
                        this.compileSdk = 36
                        
                        // Configure application-specific settings
                        if (appConfig != null) {
                            defaultConfig.applicationId = appConfig.applicationId
                            defaultConfig.versionCode = appConfig.versionCode
                            defaultConfig.versionName = appConfig.versionName
                            defaultConfig.testInstrumentationRunner = appConfig.testInstrumentationRunner
                            
                            // Navigation 3 persistence flags
                            defaultConfig.buildConfigField("boolean", "NAV3_PERSISTENCE_ENABLED", appConfig.enableNav3Persistence.toString())
                            defaultConfig.buildConfigField("boolean", "NAV3_PERSISTENCE_WRITE_ENABLED", appConfig.enableNav3PersistenceWrite.toString())
                            
                            // Missing dimension strategy
                            appConfig.missingDimensionStrategy.forEach { (dimension, strategy) ->
                                defaultConfig.missingDimensionStrategy(dimension, strategy)
                            }
                        }
                        
                        // Build types configuration
                        buildTypes {
                            getByName("release") {
                                isMinifyEnabled = true
                                isShrinkResources = true
                                enableUnitTestCoverage = false
                                enableAndroidTestCoverage = false
                            }
                            getByName("debug") {
                                isDebuggable = true
                                enableUnitTestCoverage = true
                                enableAndroidTestCoverage = true
                            }
                            create("staging") {
                                initWith(getByName("debug"))
                                applicationIdSuffix = ".staging"
                                versionNameSuffix = "-staging"
                                isDebuggable = true
                                // Use debug variants from dependencies when staging is not available
                                matchingFallbacks += listOf("debug")
                            }
                        }
                        
                        // Product flavors
                        flavorDimensions += "environment"
                        productFlavors {
                            create("dev") {
                                dimension = "environment"
                                applicationIdSuffix = ".dev"
                                versionNameSuffix = "-dev"
                            }
                            create("prod") {
                                dimension = "environment"
                            }
                        }
                        
                        // Enhanced packaging configuration with common excludes
                        packaging {
                            resources {
                                excludes += setOf(
                                    "/META-INF/{AL2.0,LGPL2.1}",
                                    "/META-INF/gradle/incremental.annotation.processors"
                                )
                            }
                        }
                        
                        @Suppress("UnstableApiUsage")
                        testOptions {
                            unitTests {
                                isIncludeAndroidResources = true
                                isReturnDefaultValues = true
                            }
                            animationsDisabled = true
                        }
                        
                        // Build features
                        buildFeatures {
                            buildConfig = true
                        }
                        
                        // Wire NDK settings when extension is configured
                        val ndk = extensions.findByName("ndkConfig") as? NdkExtension
                        if (ndk != null) {
                            if (!ndk.ndkVersion.isNullOrBlank()) {
                                ndkVersion = ndk.ndkVersion!!
                            }
                            if (!ndk.cmakeVersion.isNullOrBlank() || !ndk.cmakePath.isNullOrBlank()) {
                                externalNativeBuild.cmake {
                                    ndk.cmakeVersion?.let { version = it }
                                    ndk.cmakePath?.let { path = project.file(it) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
