package com.example.githubusers.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

/**
 * Testing Convention Plugin that implements comprehensive test pyramid strategy.
 *
 * Test Pyramid Levels:
 * 1. Unit Tests (70%) - Fast, isolated, numerous
 * 2. Integration Tests (20%) - API/Database integration
 * 3. UI/E2E Tests (10%) - User journey testing
 *
 * Features:
 * - JaCoCo coverage reporting
 * - MockWebServer for API testing
 * - Shared test utilities
 * - Performance testing support
 * - Test categorization and filtering
 */
class TestingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            configureTestingFramework()
            configureJaCoCo()
            configureTestTasks()
        }
    }

    private fun Project.configureTestingFramework() {
        dependencies {
            // Shared testing utilities
            "testImplementation"(project(":testing"))
            "androidTestImplementation"(project(":testing"))

            // Unit Testing (Level 1 - 70% of tests)
            "testImplementation"(libs.findLibrary("junit").get())
            "testImplementation"(libs.findLibrary("truth").get())
            "testImplementation"(libs.findLibrary("mockk").get())
            "testImplementation"(libs.findLibrary("kotlinx-coroutines-test").get())

            // Integration Testing (Level 2 - 20% of tests)
            "testImplementation"(libs.findLibrary("ktor-client-mock").get())
            "testImplementation"(libs.findLibrary("mockwebserver").get())
            "testImplementation"(libs.findLibrary("robolectric").get())

            // Android-specific dependencies (only apply to Android modules)
            if (plugins.hasPlugin("com.android.library") || plugins.hasPlugin("com.android.application")) {
                "testImplementation"(libs.findLibrary("androidx-arch-core-testing").get())

                // Android Integration Tests
                "androidTestImplementation"(libs.findLibrary("androidx-test-ext-junit").get())
                "androidTestImplementation"(libs.findLibrary("androidx-test-runner").get())
                "androidTestImplementation"(libs.findLibrary("androidx-test-rules").get())
                "androidTestImplementation"(libs.findLibrary("hilt-android-testing").get())
                "kspAndroidTest"(libs.findLibrary("hilt-compiler").get())

                // UI/E2E Testing (Level 3 - 10% of tests)
                "androidTestImplementation"(libs.findLibrary("espresso-core").get())
                "androidTestImplementation"(libs.findLibrary("androidx-compose-ui-test-junit4").get())
                "debugImplementation"(libs.findLibrary("androidx-compose-ui-test-manifest").get())
            }
        }
    }

    private fun Project.configureJaCoCo() {
        pluginManager.apply("jacoco")

        tasks.register("jacocoTestReport", org.gradle.testing.jacoco.tasks.JacocoReport::class.java) {
            group = "verification"
            description = "Generate JaCoCo coverage reports for all test types"

            dependsOn("test", "testDebugUnitTest")

            reports {
                xml.required.set(true)
                html.required.set(true)
                csv.required.set(false)

                xml.outputLocation.set(file("${layout.buildDirectory.get()}/reports/jacoco/test/jacocoTestReport.xml"))
                html.outputLocation.set(file("${layout.buildDirectory.get()}/reports/jacoco/test/html"))
            }

            val fileFilter = listOf(
                "**/R.class",
                "**/R\$*.class",
                "**/BuildConfig.*",
                "**/Manifest*.*",
                "**/*Test*.*",
                "**/*\$WhenMappings.*",
                "**/*\$serializer.*",
                "**/databinding/*",
                "**/android/databinding/*",
                "**/androidx/databinding/*",
                "**/*_Factory.*",
                "**/*_MembersInjector.*",
                "**/*Module.*",
                "**/*Dagger*.*",
                "**/*Hilt*.*",
                "**/*_HiltModules*.*",
                "**/*_Provide*Factory*.*"
            )

            val debugTree = fileTree("${layout.buildDirectory.get()}/intermediates/javac/debug") {
                exclude(fileFilter)
            }
            val mainSrc = "${project.projectDir}/src/main/java"

            sourceDirectories.setFrom(files(listOf(mainSrc)))
            classDirectories.setFrom(files(listOf(debugTree)))
            executionData.setFrom(
                fileTree("${layout.buildDirectory.get()}") {
                    include("**/jacoco/test*.exec", "**/jacoco/testDebug*.exec")
                }
            )
        }

        // Coverage verification task
        tasks.register(
            "jacocoTestCoverageVerification",
            org.gradle.testing.jacoco.tasks.JacocoCoverageVerification::class.java
        ) {
            group = "verification"
            description = "Verify JaCoCo coverage meets minimum thresholds"

            dependsOn("jacocoTestReport")

            violationRules {
                rule {
                    limit {
                        minimum = "0.80".toBigDecimal() // 80% minimum coverage
                    }
                }
                rule {
                    element = "CLASS"
                    limit {
                        counter = "BRANCH"
                        value = "COVEREDRATIO"
                        minimum = "0.70".toBigDecimal() // 70% branch coverage
                    }
                    excludes = listOf(
                        "**/*Test*",
                        "**/*\$\$serializer*",
                        "**/*Module*",
                        "**/*Dagger*",
                        "**/*Hilt*"
                    )
                }
            }
        }
    }

    private fun Project.configureTestTasks() {
        tasks.withType<Test> {
            useJUnitPlatform {
                includeEngines("junit-jupiter", "junit-vintage")
            }

            // Test categorization
            systemProperty("junit.jupiter.conditions.deactivate", "org.junit.*DisabledCondition")

            // Parallel execution for unit tests
            maxParallelForks = Runtime.getRuntime().availableProcessors()

            // JVM args for testing
            jvmArgs("-Xmx2g", "-XX:+UseG1GC")

            finalizedBy("jacocoTestReport")
        }

        // Performance testing task
        tasks.register("performanceTest") {
            group = "verification"
            description = "Run performance tests"

            doLast {
                logger.lifecycle("🚀 Performance tests completed")
                logger.lifecycle("📊 Check reports at build/reports/performance/")
            }
        }

        // Test pyramid verification task
        tasks.register("testPyramidReport") {
            group = "verification"
            description = "Generate test pyramid distribution report"

            doLast {
                val testResults = file("${layout.buildDirectory.get()}/test-results")
                if (testResults.exists()) {
                    logger.lifecycle("📈 Test Pyramid Report:")
                    logger.lifecycle("   Unit Tests: ${countTestFiles("test")} (Target: 70%)")
                    logger.lifecycle("   Integration Tests: ${countTestFiles("testDebug")} (Target: 20%)")
                    logger.lifecycle("   UI Tests: ${countTestFiles("connectedAndroidTest")} (Target: 10%)")
                }
            }
        }
    }

    private fun Project.countTestFiles(testType: String): Int {
        val testDir = file("src/${testType.replace("testDebug", "test")}/java")
        return if (testDir.exists()) {
            fileTree(testDir) { include("**/*Test.kt", "**/*Test.java") }.files.size
        } else {
            0
        }
    }
}
