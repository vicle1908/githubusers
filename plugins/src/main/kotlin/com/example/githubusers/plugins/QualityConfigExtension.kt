package com.example.githubusers.plugins

import org.gradle.api.provider.Property
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty

/**
 * Unified Quality Configuration Extension for all quality tools.
 * 
 * This extension provides a single configuration point for:
 * - Detekt configuration and rules
 * - KtLint formatting rules
 * - Quality thresholds and limits
 * - CI/CD integration settings
 * - Custom quality rules
 */
abstract class QualityConfigExtension {
    
    // ===== GENERAL QUALITY SETTINGS =====
    
    /**
     * Enable parallel execution of quality tools (default: true)
     */
    abstract val enableParallelExecution: Property<Boolean>
    
    /**
     * Enable quality metrics printing (default: true)
     */
    abstract val enableMetrics: Property<Boolean>
    
    /**
     * Enable unified reporting (default: true)
     */
    abstract val enableUnifiedReporting: Property<Boolean>
    
    /**
     * Quality thresholds and limits
     */
    abstract val maxIssues: Property<Int>
    abstract val failOnAnyIssue: Property<Boolean>
    
    // ===== DETEKT CONFIGURATION =====
    
    /**
     * Custom Detekt configuration file path
     */
    abstract val customDetektConfig: Property<String>
    
    /**
     * Detekt-specific settings
     */
    abstract val detektConfig: DetektConfig
    
    // ===== KTLINT CONFIGURATION =====
    
    /**
     * Custom KtLint configuration
     */
    abstract val customKtLintConfig: Property<String>
    
    /**
     * KtLint-specific settings
     */
    abstract val ktLintConfig: KtLintConfig
    
    // ===== CI/CD INTEGRATION =====
    
    /**
     * CI/CD specific settings
     */
    abstract val ciConfig: CIConfig
    
    // ===== CUSTOM QUALITY RULES =====
    
    /**
     * Custom quality rules and thresholds
     */
    abstract val customRules: MapProperty<String, Any>
    
    init {
        // Set default values
        enableParallelExecution.convention(true)
        enableMetrics.convention(true)
        enableUnifiedReporting.convention(true)
        maxIssues.convention(100)
        failOnAnyIssue.convention(false)
        customDetektConfig.convention("")
        customKtLintConfig.convention("")
        customRules.convention(emptyMap())
    }
    
    /**
     * Detekt-specific configuration
     */
    abstract class DetektConfig {
        /**
         * Enable Detekt formatting rules (default: false, handled by KtLint)
         */
        abstract val enableFormatting: Property<Boolean>
        
        /**
         * Custom Detekt rulesets to include
         */
        abstract val additionalRulesets: ListProperty<String>
        
        /**
         * Detekt baseline file path
         */
        abstract val baselineFile: Property<String>
        
        /**
         * Detekt JVM target (default: 21)
         */
        abstract val jvmTarget: Property<String>
        
        /**
         * Detekt exclusion patterns
         */
        abstract val exclusions: ListProperty<String>
        
        init {
            enableFormatting.convention(false)
            additionalRulesets.convention(listOf("detekt-formatting"))
            baselineFile.convention("config/detekt/baseline.xml")
            jvmTarget.convention("21")
            exclusions.convention(listOf(
                "**/build/**",
                "**/generated/**",
                "**/build/generated/**",
                "**/build/ksp/**"
            ))
        }
    }
    
    /**
     * KtLint-specific configuration
     */
    abstract class KtLintConfig {
        /**
         * Enable Android code style (default: true)
         */
        abstract val enableAndroidStyle: Property<Boolean>
        
        /**
         * Enable verbose output (default: true)
         */
        abstract val enableVerbose: Property<Boolean>
        
        /**
         * KtLint reporters to enable
         */
        abstract val reporters: ListProperty<String>
        
        /**
         * KtLint exclusion patterns
         */
        abstract val exclusions: ListProperty<String>
        
        init {
            enableAndroidStyle.convention(true)
            enableVerbose.convention(true)
            reporters.convention(listOf("plain", "checkstyle", "html"))
            exclusions.convention(listOf(
                "**/build/**",
                "**/generated/**",
                "**/build/generated/**",
                "**/build/ksp/**"
            ))
        }
    }
    
    /**
     * CI/CD specific configuration
     */
    abstract class CIConfig {
        /**
         * Enable CI mode (default: false)
         */
        abstract val enabled: Property<Boolean>
        
        /**
         * Fail build on quality issues in CI (default: true)
         */
        abstract val failOnIssues: Property<Boolean>
        
        /**
         * Generate SARIF reports for GitHub code scanning (default: true)
         */
        abstract val generateSARIF: Property<Boolean>
        
        /**
         * Quality thresholds for CI builds
         */
        abstract val qualityThresholds: MapProperty<String, Int>
        
        init {
            enabled.convention(false)
            failOnIssues.convention(true)
            generateSARIF.convention(true)
            qualityThresholds.convention(mapOf(
                "maxComplexity" to 15,
                "maxLineLength" to 120,
                "maxMethodLength" to 60,
                "maxClassLength" to 600
            ))
        }
    }
}
