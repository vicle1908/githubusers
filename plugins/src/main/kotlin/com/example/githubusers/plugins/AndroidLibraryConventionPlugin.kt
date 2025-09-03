package com.example.githubusers.plugins

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Apply base module configuration first
            plugins.apply(BaseModulePlugin::class.java)

            // Ensure required plugins are applied
            pluginManager.apply("com.android.library")
            pluginManager.apply("org.jetbrains.kotlin.android")
            // Apply project-wide quality conventions (individual plugins applied by modules)
            // Apply build guard to prevent local build logic blocks in module build scripts
            pluginManager.apply("githubusers.build.guard")

            // Configure when Android library plugin is applied
            pluginManager.withPlugin("com.android.library") {
                pluginManager.withPlugin("org.jetbrains.kotlin.android") {
                    extensions.configure<LibraryExtension> {
                        configureKotlinAndroid(this)
                        configureLint()

                        // Use shared configuration from base plugin
                        defaultConfig.targetSdk =
                            (project.findProperty("targetSdk")?.toString()?.toIntOrNull()) ?: 36
                        compileSdk = (project.findProperty("compileSdk")?.toString()?.toIntOrNull()) ?: 36

                        // Automatically derive namespace from module path if not already set
                        if (namespace.isNullOrEmpty()) {
                            // Create unique namespace based on module name and path
                            val moduleName = project.name
                            val rootPath = rootProject.projectDir.absolutePath
                            val modulePath = project.projectDir.absolutePath
                            val relativePath = modulePath.substringAfter(rootPath).trim('/')
                            
                            // Build a unique namespace that includes the module name
                            val namespaceBuilder = StringBuilder("com.example.githubusers")
                            
                            if (relativePath.isNotEmpty()) {
                                // Add path components but ensure uniqueness
                                val pathParts = relativePath.split("/")
                                pathParts.forEach { part ->
                                    if (part.isNotEmpty() && part != moduleName) {
                                        namespaceBuilder.append(".$part")
                                    }
                                }
                            }
                            
                            // Always add the module name to ensure uniqueness
                            namespaceBuilder.append(".$moduleName")
                            
                            namespace = namespaceBuilder.toString().replace("-", "")
                        }

                        // The resource prefix is derived from the module name
                        resourcePrefix = project.name.replace("-", "_").lowercase() + "_"

                        // Enhanced packaging configuration with common excludes
                        packaging {
                            resources {
                                excludes += setOf(
                                    "/META-INF/{AL2.0,LGPL2.1}",
                                    "/META-INF/gradle/incremental.annotation.processors"
                                )
                            }
                        }

                        @Suppress("UnstableApiUsage")
                        testOptions {
                            unitTests {
                                isIncludeAndroidResources = true
                            }
                        }

                        // Configure publishing variant to avoid warnings
                        publishing {
                            singleVariant("release")
                        }
                    }

                    disableUnnecessaryAndroidTests(target)
                }
            }

            // Configure module type when available
            pluginManager.withPlugin("githubusers.base.module") {
                extensions.configure<ModuleConfigExtension> {
                    isLibraryModule = true
                }
            }
        }
    }
}
