package com.example.githubusers.plugins

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

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
            // Apply project-wide quality conventions (individual plugins applied by modules)
            // Apply build guard to prevent local build logic blocks in module build scripts
            pluginManager.apply("githubusers.build.guard")

            pluginManager.withPlugin("com.android.application") {
                pluginManager.withPlugin("org.jetbrains.kotlin.android") {
                    extensions.configure<ApplicationExtension> {
                        configureKotlinAndroid(this)
                        configureLint()

                        lint {
                            abortOnError = true
                            checkReleaseBuilds = false
                        }

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
                            defaultConfig.buildConfigField(
                                "boolean",
                                "NAV3_PERSISTENCE_ENABLED",
                                appConfig.enableNav3Persistence.toString()
                            )
                            defaultConfig.buildConfigField(
                                "boolean",
                                "NAV3_PERSISTENCE_WRITE_ENABLED",
                                appConfig.enableNav3PersistenceWrite.toString()
                            )

                            // No more product flavors, so no missing dimension strategy needed
                        }

                        // Build types configuration (simplified - only debug and release)
                        buildTypes {
                            release {
                                isMinifyEnabled = true
                                isShrinkResources = true
                                proguardFiles(
                                    getDefaultProguardFile("proguard-android-optimize.txt"),
                                    "proguard-rules.pro"
                                )
                            }
                            debug {
                                isDebuggable = true
                                applicationIdSuffix = ".debug"
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
