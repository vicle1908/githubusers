package com.example.githubusers.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
                // Let individual modules apply serialization plugin if needed
                // apply("org.jetbrains.kotlin.plugin.serialization")
                // Apply quality conventions (individual plugins applied by modules)
                // Apply build guard to prevent local build logic blocks in module build scripts
                apply("githubusers.build.guard")
            }
            configureKotlinJvm()
            configureToolingVersionAlignment()
        }
    }
}
