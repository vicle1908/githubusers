package com.example.githubusers.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/**
 * Convention plugin for managing module versions in library modules.
 * Reads module versions from the version catalog and applies them to the project.
 */
class AndroidLibraryPublishingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            // Apply maven-publish plugin for publishing capabilities
            pluginManager.apply("maven-publish")

            // Configure version in a configuration-cache friendly way
            val provider = providers.provider {
                try {
                    val catalog = extensions.getByType<VersionCatalogsExtension>().named("libs")
                    when (name) {
                        "navigation-api" -> catalog.findVersion("navigationApiModule").get().requiredVersion
                        "navigation-impl" -> catalog.findVersion("navigationImplModule").get().requiredVersion
                        "feature-users" -> catalog.findVersion("featureUsersModule").get().requiredVersion
                        "app" -> catalog.findVersion("appModule").get().requiredVersion
                        "catalog" -> catalog.findVersion("catalogModule").get().requiredVersion
                        "internal-platform" -> catalog.findVersion("internalPlatformModule").get().requiredVersion
                        else -> "1.0.0-SNAPSHOT"
                    }
                } catch (e: Exception) {
                    "1.0.0-SNAPSHOT"
                }
            }
            version = provider.get()

            // Ensure publications reflect the computed project version
extensions.configure(org.gradle.api.publish.PublishingExtension::class.java) {
                publications.withType(org.gradle.api.publish.maven.MavenPublication::class.java).configureEach {
                    this.version = project.version.toString()
                }
            }
        }
    }
}
