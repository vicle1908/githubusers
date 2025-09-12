package com.example.githubusers.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

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
            // Temporarily disabled due to Detekt compatibility issue
            // pluginManager.apply("githubusers.quality.ktlint")
            // pluginManager.apply("githubusers.quality.detekt")

            // Configure feature-specific settings and common dependencies
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

        // Add common feature module dependencies
        dependencies {
            // Core Android
            add("implementation", libs.findLibrary("androidx-core-ktx").get())

            // Hilt
            add("implementation", libs.findLibrary("hilt-android").get())
            add("ksp", libs.findLibrary("hilt-compiler").get())

            // Navigation API (common to all features)
            add("implementation", libs.findLibrary("local-navigation-api").get())

            // Testing
            add("testImplementation", libs.findLibrary("junit").get())
            add("testImplementation", libs.findLibrary("mockk").get())
            add("testImplementation", libs.findLibrary("kotlinx-coroutines-test").get())
            add("androidTestImplementation", libs.findLibrary("androidx-test-ext-junit").get())
            add("androidTestImplementation", libs.findLibrary("espresso-core").get())
            add("androidTestImplementation", libs.findLibrary("hilt-android-testing").get())
            add("kspAndroidTest", libs.findLibrary("hilt-compiler").get())
        }
    }
}
