package com.example.githubusers.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.named

/**
 * Convention plugin for Android publishing configuration.
 * Provides standardized publishing setup for all Android modules.
 */
class AndroidPublishingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Apply maven-publish plugin
            pluginManager.apply("maven-publish")

            // Configure publishing
            configurePublishing()
        }
    }

    private fun Project.configurePublishing() {
        extensions.configure<PublishingExtension> {
            publications {
                create<MavenPublication>("release") {
                    // Inherit coordinates from the project (managed by other convention plugins)
                    groupId = project.group.toString()
                    artifactId = project.name
                    version = project.version.toString()
                }
            }
        }

        // Attach Android library component only after the Android library plugin is applied
pluginManager.withPlugin("com.android.library") {
            // Defer until after variants/components are realized
            afterEvaluate {
                extensions.configure<PublishingExtension> {
                    publications.named<MavenPublication>("release") {
                        from(components.getByName("release"))
                    }
                }
            }
        }
    }
}
