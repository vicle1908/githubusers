package com.example.githubusers.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create

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
                    groupId = "com.example.githubusers"
                    artifactId = project.name
                    version = "1.0.0"
                }
            }
        }
    }
}