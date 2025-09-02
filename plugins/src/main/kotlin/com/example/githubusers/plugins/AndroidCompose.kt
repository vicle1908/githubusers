package com.example.githubusers.plugins

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Configure Compose-specific options
 */
internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    // Apply Compose compiler plugin
    pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
    
    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = libs.findVersion("compose-compiler").get().toString()
        }
    }

    dependencies {
        val bom = libs.findLibrary("androidx-compose-bom").get()
        add("implementation", platform(bom))
        add("androidTestImplementation", platform(bom))
        add("implementation", libs.findLibrary("androidx-compose-ui-tooling-preview").get())
        add("debugImplementation", libs.findLibrary("androidx-compose-ui-tooling").get())
    }
}