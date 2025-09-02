package com.example.githubusers.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

/**
 * Enhanced KtLint Convention Plugin with unified configuration support.
 * 
 * Features:
 * - Android-specific code style configuration
 * - Consistent exclusion patterns across modules
 * - Integration with unified quality system
 * - Enhanced reporting and metrics
 */
class KtlintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Apply the KtLint plugin internally
            pluginManager.apply("org.jlleitschuh.gradle.ktlint")
            
            // Configure the KtLint plugin
            configureKtlintPlugin()
        }
    }
    
    private fun Project.configureKtlintPlugin() {
        // Configure ktlint defaults using the typed extension API
        val ext = extensions.getByType(KtlintExtension::class.java)
        
        // Enable Android code style
        ext.android.set(true)
        
        // Enable verbose output for better debugging
        ext.verbose.set(true)
        
        // Configure reporters for better integration
        ext.reporters {
            reporter(ReporterType.PLAIN)
            reporter(ReporterType.CHECKSTYLE)
            reporter(ReporterType.HTML)
        }
        
        // Exclude generated and build directories consistently
        ext.filter {
            exclude("**/build/**")
            exclude("**/generated/**")
            exclude("**/build/generated/**")
            exclude("**/build/ksp/**")
            exclude("**/build/tmp/kapt3/**")
            exclude("**/build/generated/kapt/**")
            exclude("**/build/generated/ksp/**")
            exclude("**/build/intermediates/**")
        }

        // Wire ktlint checks into standard verification lifecycle
        tasks.matching { it.name == "check" }.configureEach {
            if (tasks.findByName("ktlintCheck") != null) {
                dependsOn("ktlintCheck")
            }
        }
        
        // Configure ktlint tasks for better performance and metrics
        tasks.withType(org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask::class.java).configureEach {
            // Add quality metrics collection
            doLast {
                logger.lifecycle("🎨 KtLint completed for ${project.name}")
                collectKtLintMetrics()
            }
        }
    }
    
    /**
     * Collect KtLint quality metrics for reporting
     */
    private fun Project.collectKtLintMetrics() {
        val reportDir = file("${layout.buildDirectory.get().asFile}/reports/ktlint")
        if (reportDir.exists()) {
            val htmlReport = reportDir.resolve("ktlintMain.html")
            if (htmlReport.exists()) {
                logger.lifecycle("   📊 KtLint report: ${htmlReport.absolutePath}")
            }
            
            val checkstyleReport = reportDir.resolve("ktlintMain.xml")
            if (checkstyleReport.exists()) {
                logger.lifecycle("   📋 KtLint checkstyle: ${checkstyleReport.absolutePath}")
            }
        }
    }
    

}
