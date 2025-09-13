package com.example.githubusers.plugins

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin for Firebase Performance Monitoring configuration.
 *
 * This plugin:
 * - Applies Firebase Performance Monitoring plugin
 * - Adds Firebase Performance Monitoring dependencies
 * - Configures performance monitoring for different build types
 * - Provides consistent Firebase Performance setup across modules
 */
class FirebasePerformanceConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Apply Firebase Performance Monitoring plugin from Maven repository
            pluginManager.apply("com.google.firebase.firebase-perf")

            // Apply Google Services plugin (required for Firebase)
            pluginManager.apply("com.google.gms.google-services")

            // Configure Firebase Performance Monitoring based on module type
            when {
                pluginManager.hasPlugin("com.android.application") -> {
                    configureApplicationFirebasePerformance()
                }
                pluginManager.hasPlugin("com.android.library") -> {
                    configureLibraryFirebasePerformance()
                }
            }
        }
    }

    private fun Project.configureApplicationFirebasePerformance() {
        extensions.configure<ApplicationExtension> {
            buildTypes {
                debug {
                    // Enable Firebase Performance Monitoring in debug builds for development
                    buildConfigField("boolean", "FIREBASE_PERF_ENABLED", "true")
                }
                release {
                    // Enable Firebase Performance Monitoring in release builds for production
                    buildConfigField("boolean", "FIREBASE_PERF_ENABLED", "true")
                }
                // Note: Only configure build types that actually exist in the project
                // If benchmark build type is needed, it should be defined in the Android convention plugin first
            }
        }

        // Add Firebase Performance Monitoring dependencies
        dependencies {
            // Firebase BOM for version management
            add("implementation", platform(libs.findLibrary("firebase-bom").get()))

            // Firebase Performance Monitoring
            add("implementation", libs.findLibrary("firebase-perf-lib").get())
        }
    }

    private fun Project.configureLibraryFirebasePerformance() {
        // For library modules, we don't configure build types but still add dependencies
        // if they need Firebase Performance Monitoring
        dependencies {
            // Firebase BOM for version management
            add("implementation", platform(libs.findLibrary("firebase-bom").get()))

            // Firebase Performance Monitoring (optional for libraries)
            add("implementation", libs.findLibrary("firebase-perf-lib").get())
        }
    }
}
