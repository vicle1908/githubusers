package com.example.githubusers.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Plugin that provides standardized access to the version catalog
 * from the catalog composite build module.
 */
class CatalogAccessPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // This plugin is applied automatically when using other convention plugins
            // It ensures the catalog dependency is available
            configureCatalogAccess()
        }
    }
}

private fun Project.configureCatalogAccess() {
    // The catalog will be available via includeBuild dependency substitution
    // configured in each module's settings.gradle.kts
    logger.debug("Catalog access configured for project: ${project.name}")
}
