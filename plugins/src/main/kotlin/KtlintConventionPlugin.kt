package com.example.githubusers.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.KtlintPlugin

class KtlintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Configure only when ktlint plugin is present
            pluginManager.withPlugin("org.jlleitschuh.gradle.ktlint") {
                // Configure ktlint defaults using the typed extension API
                val ext = extensions.getByType(KtlintExtension::class.java)
                ext.android.set(true)

                // Wire ktlint checks into standard verification lifecycle if available
                tasks.matching { it.name == "check" }.configureEach {
                    // ktlintCheck is the verification task provided by the plugin
                    if (tasks.findByName("ktlintCheck") != null) {
                        dependsOn("ktlintCheck")
                    }
                }
            }
        }
    }
}
