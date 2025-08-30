package com.example.githubusers.buildlogic

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/**
 * Extension to get the libs version catalog
 */
val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

/**
 * Disable unnecessary Android tests for modules that don't need them
 */
internal fun disableUnnecessaryAndroidTests(project: Project) {
    project.tasks.matching { it.name == "testReleaseUnitTest" }.configureEach {
        enabled = false
    }
}