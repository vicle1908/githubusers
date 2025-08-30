package com.example.githubusers.plugins

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

class DetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Only configure when the Detekt plugin is actually applied
            pluginManager.withPlugin("io.gitlab.arturbosch.detekt") {
                // Configure the Detekt extension consistently across modules
                extensions.configure<DetektExtension> {
                    buildUponDefaultConfig = true
                    allRules = false
                    // Keep ignoreFailures aligned with current project policy
                    // (the app module currently sets true while features mature)
                    ignoreFailures = true
                    config.setFrom("${rootDir}/detekt.yml")
                    // Do not set a baseline at extension level; handled per-task only if present
                }

                // Ensure detekt-formatting is present to provide formatting rules via ktlint
                dependencies {
                    add("detektPlugins", "io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8")
                }

                // Configure tasks to use a supported JVM target and shared config
                tasks.withType<Detekt>().configureEach {
                    jvmTarget = "21"
                    config.setFrom("${rootDir}/detekt.yml")
                    val baselineFile = file("config/detekt/baseline.xml")
                    if (baselineFile.exists()) {
                        baseline.set(baselineFile)
                    }
                }
                tasks.withType<DetektCreateBaselineTask>().configureEach {
                    config.setFrom("${rootDir}/detekt.yml")
                    baseline.set(file("config/detekt/baseline.xml"))
                    doFirst {
                        baseline.get().asFile.parentFile.mkdirs()
                    }
                }
            }
        }
    }
}
