package com.example.githubusers.plugins

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

/**
 * Task to sync module versions from catalog to platform BOM.
 * This ensures the platform BOM always uses the same versions as defined in the catalog.
 */
@CacheableTask
abstract class PlatformVersionSyncTask : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val catalogFile: RegularFileProperty

    @get:OutputFile
    abstract val platformBuildFile: RegularFileProperty

    @TaskAction
    fun syncVersions() {
        val catalog = catalogFile.get().asFile
        val platform = platformBuildFile.get().asFile

        // Read versions from catalog
        val versions = mutableMapOf<String, String>()
        catalog.readLines().forEach { line ->
            when {
                line.contains("navigation-api-module = ") -> {
                    versions["navigation-api"] = line.substringAfter("\"").substringBefore("\"")
                }
                line.contains("navigation-impl-module = ") -> {
                    versions["navigation-impl"] = line.substringAfter("\"").substringBefore("\"")
                }
                line.contains("feature-users-module = ") -> {
                    versions["feature-users"] = line.substringAfter("\"").substringBefore("\"")
                }
                line.contains("app-module = ") -> {
                    versions["app"] = line.substringAfter("\"").substringBefore("\"")
                }
            }
        }

        // Update platform build file
        val platformContent = platform.readText()
        var updatedContent = platformContent

        versions.forEach { (module, version) ->
            val pattern = """(api\("com\.example\.githubusers:$module"\) \{\s*version \{\s*//.*\s*strictly\(")[^"]+("\))"""
                .toRegex(RegexOption.MULTILINE)
            updatedContent = updatedContent.replace(pattern) { matchResult ->
                "${matchResult.groupValues[1]}$version${matchResult.groupValues[2]}"
            }
        }

        if (updatedContent != platformContent) {
            platform.writeText(updatedContent)
            logger.lifecycle("Synced platform versions from catalog: $versions")
        } else {
            logger.lifecycle("Platform versions already in sync with catalog")
        }
    }
}
