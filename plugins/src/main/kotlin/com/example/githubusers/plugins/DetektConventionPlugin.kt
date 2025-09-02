package com.example.githubusers.plugins

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.net.URISyntaxException
import java.net.URL

/**
 * Detekt convention plugin that applies the Detekt plugin and configures it
 * for consistent code quality analysis across all modules.
 *
 * Based on official Detekt documentation patterns for multi-module projects.
 * Focus: Code analysis only (formatting handled by KtLint)
 *
 * Uses centralized configuration approach with plugin resources:
 * - Single config file embedded in the plugin resources
 * - All modules use the same configuration
 * - No need for duplicate config files in each module
 */
class DetektConventionPlugin : Plugin<Project> {

    @Throws(URISyntaxException::class)
    fun Any.getFileFromResource(fileName: String): File {
        val classLoader: ClassLoader = javaClass.classLoader
        val resource: URL? = classLoader.getResource(fileName)
        return if (resource == null) {
            throw IllegalArgumentException("file not found! $fileName")
        } else {
            val file: File = File.createTempFile("config", ".yml")
            val inputStream = resource.openStream()
            inputStream.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            file
        }
    }

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("io.gitlab.arturbosch.detekt")
            }

            val configOpt = this@DetektConventionPlugin.getFileFromResource("detekt/detekt.yml")

            // Configure the Detekt extension
            val detektExt = extensions.getByType(DetektExtension::class.java)
            detektExt.config.setFrom(configOpt)
            detektExt.buildUponDefaultConfig = false

            // Configure each Detekt task
            tasks.withType(Detekt::class.java).configureEach {
                jvmTarget = "21"

                // Exclude generated code
                exclude("**/build/**")
                exclude("**/generated/**")

                // Configure reports using correct API syntax
                reports {
                    html.required.set(true)
                    xml.required.set(true)
                    sarif.required.set(true)
                    md.required.set(true)
                }

                // Temporary: do not fail build on Detekt findings
                ignoreFailures = true
            }

            // Log configuration for debugging
            logger.info("Detekt configured for ${project.name} using centralized config from plugin resources")

            // Configure test tasks to not fail on no discovered tests
            // This is needed for modules that have test sources but no actual tests yet
            // Only apply to regular test tasks, not Android test tasks
            tasks.withType(org.gradle.api.tasks.testing.Test::class.java).configureEach {
                setProperty("failOnNoDiscoveredTests", false)
            }
        }
    }
}
