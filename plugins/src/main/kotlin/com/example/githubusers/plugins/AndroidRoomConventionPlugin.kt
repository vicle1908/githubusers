package com.example.githubusers.plugins

import androidx.room.gradle.RoomExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Note: Room plugin should already be applied by the module
            // Only configure the Room extension if it's available

            pluginManager.withPlugin("androidx.room") {
                extensions.configure<RoomExtension> {
                    // The schemas directory contains a schema file for each version of the Room database.
                    // This is required to enable Room auto migrations.
                    // See https://developer.android.com/reference/kotlin/androidx/room/AutoMigration.
                    schemaDirectory("$projectDir/schemas")
                }
            }

            // Note: Dependencies will be configured by the individual modules
            // or by other convention plugins that have access to the version catalog
        }
    }
}
