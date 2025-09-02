package com.example.githubusers.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlatformPlugin
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create

/**
 * Convention plugin for Java platform modules.
 * Provides standardized platform configuration for dependency management.
 * Follows the same patterns as other githubusers convention plugins.
 */
class PlatformModuleConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Apply base module configuration first
            pluginManager.apply("githubusers.base.module")
            
            // Apply Java platform plugin
            pluginManager.apply(JavaPlatformPlugin::class.java)
            
            // Configure platform-specific settings
            configurePlatform()
            
            // Configure publishing
            configurePublishing()
        }
    }
    
    private fun Project.configurePlatform() {
        extensions.configure<org.gradle.api.plugins.JavaPlatformExtension> {
            allowDependencies()
            // Enable strict version enforcement by default (as per Gradle best practices)
            // This ensures that versions defined in the platform are strictly enforced
        }
    }
    
    private fun Project.configurePublishing() {
        pluginManager.apply("maven-publish")
        
        extensions.configure<PublishingExtension> {
            publications {
                create<MavenPublication>("maven") {
                    from(components.named("javaPlatform").get())
                    
                    // Use same metadata pattern as other modules
                    groupId = "com.example.githubusers"
                    artifactId = project.name
                    version = project.version.toString()
                }
            }
        }
    }
}
