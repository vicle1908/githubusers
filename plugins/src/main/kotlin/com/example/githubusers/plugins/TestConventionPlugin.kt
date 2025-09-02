package com.example.githubusers.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.AbstractTestTask

/**
 * Test convention plugin that configures test tasks consistently across all modules.
 * 
 * Responsibilities:
 * - Configure test task behavior (failOnNoDiscoveredTests, etc.)
 * - Handle test discovery failures gracefully
 * 
 * This plugin is separate from DetektConventionPlugin to maintain
 * separation of concerns and follow Gradle best practices.
 */
class TestConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            // Configure all test tasks to not fail on no discovered tests
            // This covers both regular Test tasks and Android test tasks
            // AbstractTestTask is the base class for all test tasks in Gradle
            tasks.withType(AbstractTestTask::class.java).configureEach {
                // Disable failing when no tests are discovered
                // This prevents build failures in modules with test sources but no actual tests
                setProperty("failOnNoDiscoveredTests", false)
            }

            // Log configuration for debugging
            logger.info("Test tasks configured for ${project.name}")
        }
    }
}
