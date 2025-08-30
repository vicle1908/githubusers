package com.example.githubusers.plugins

import com.example.githubusers.buildlogic.configureKotlinJvm
import org.gradle.api.Plugin
import org.gradle.api.Project

class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
                // Let individual modules apply serialization plugin if needed
                // apply("org.jetbrains.kotlin.plugin.serialization")
                // Apply quality conventions (detekt will only configure when present)
                // apply("githubusers.quality.ktlint")
                apply("githubusers.quality.detekt")
            }
            configureKotlinJvm()
        }
    }
}
