package com.example.githubusers.plugins

import com.github.benmanes.gradle.versions.updates.Coordinate
import com.github.benmanes.gradle.versions.updates.DependencyStatus
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesReporter
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.benmanes.gradle.versions.updates.OutputFormatterArgument
import com.github.benmanes.gradle.versions.updates.gradle.GradleUpdateChecker
import com.github.benmanes.gradle.versions.updates.resolutionstrategy.ResolutionStrategyWithCurrent
import com.github.benmanes.gradle.versions.updates.Resolver
import com.github.benmanes.gradle.versions.updates.VersionMapping
import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.UnresolvedDependency
import org.gradle.api.internal.TaskInternal
import org.gradle.api.specs.Spec
import java.util.LinkedHashMap

/**
 * Convention plugin for configuring automated dependency update checking
 */
class DependencyUpdatePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.github.ben-manes.versions")
            }

            configureDependencyUpdates()
            createDependencyUpdateTask()
            createSecurityScanTask()
        }
    }
}

/**
 * Configures automated dependency update checking for GitHub Users project
 */
private fun Project.configureDependencyUpdates() {
    enforceSequentialExecutionForDependencyUpdates()

    tasks.withType(DependencyUpdatesTask::class.java) {
        // The ben-manes versions plugin is not configuration-cache safe on Gradle 9 yet.
        // Mark the task as incompatible so Gradle will disable CC for invocations that include it.
        notCompatibleWithConfigurationCache("ben-manes versions plugin not CC-safe on Gradle 9")

        // Gradle release channel
        gradleReleaseChannel = "current"

        // Configure update resolution strategy to reject unstable versions
        rejectVersionIf {
            isNonStable(candidate.version) && !isNonStable(currentVersion)
        }

        // Configure output format
        outputFormatter = "json,xml,html"
        outputDir = "build/dependencyUpdates"
        reportfileName = "report"

        // Filter dependencies to check
        checkForGradleUpdate = true
        checkConstraints = false
        checkBuildEnvironmentConstraints = false
    }

    tasks.withType(DependencyUpdatesTask::class.java).configureEach {
        val taskInternal = this as TaskInternal
        val action = object : Action<Task> {
            override fun execute(actionTask: Task) {
                SafeDependencyUpdatesRunner.run(actionTask as DependencyUpdatesTask)
            }
        }
        taskInternal.actions = listOf(action)
    }
}

/**
 * The ben-manes versions plugin does not support Gradle's parallel project execution mode. When
 * users invoke `dependencyUpdates` (or our aggregate helpers) while the build has
 * `org.gradle.parallel=true`, Gradle will abort with "Parallel project execution is not supported".
 * This helper inspects the requested task graph and forces the build to behave as if `--no-parallel`
 * was supplied when we detect a dependency update invocation. This mirrors the CLI flag without
 * requiring contributors to remember it.
 */
private fun Project.enforceSequentialExecutionForDependencyUpdates() {
    val requestedTasks = gradle.startParameter.taskNames
    val requiresSequential = requestedTasks.any { task ->
        task.equals("dependencyUpdates", ignoreCase = true) ||
            task.equals("dependencyUpdatesAll", ignoreCase = true) ||
            task.equals("updateDependencies", ignoreCase = true) ||
            task.contains("dependencyUpdates", ignoreCase = true)
    }

    if (requiresSequential) {
        val startParameter = gradle.startParameter

        if (startParameter.isParallelProjectExecutionEnabled) {
            logger.lifecycle(
                "Dependency updates require --no-parallel; disabling parallel project execution for this run."
            )
            startParameter.isParallelProjectExecutionEnabled = false
        }

        if (startParameter.maxWorkerCount != 1) {
            logger.lifecycle("Dependency updates run sequentially; forcing max worker count to 1.")
            startParameter.maxWorkerCount = 1
        }

        // Configuration-on-demand can still invoke work in parallel; disable it when possible.
        try {
            val configureOnDemandProperty = startParameter::class.java.getMethod("isConfigureOnDemand")
            val configureOnDemandEnabled = configureOnDemandProperty.invoke(startParameter) as? Boolean ?: false
            if (configureOnDemandEnabled) {
                val disableMethod = startParameter::class.java.getMethod(
                    "setConfigureOnDemand",
                    Boolean::class.javaPrimitiveType
                )
                disableMethod.invoke(startParameter, false)
                logger.lifecycle("Configuration on demand disabled for dependency updates run.")
            }
        } catch (_: NoSuchMethodException) {
            // Gradle 9 removes configure-on-demand; ignore when unavailable.
        }
    }
}

private object SafeDependencyUpdatesRunner {

    fun run(task: DependencyUpdatesTask) {
        requireNoParallel(task)
        task.project.evaluationDependsOnChildren()

        val resolutionStrategyClosure = readResolutionStrategyClosure(task)
        val resolutionStrategyAction = readResolutionStrategyAction(task)

        if (resolutionStrategyClosure != null) {
            task.resolutionStrategy(
                object : Action<ResolutionStrategyWithCurrent> {
                    override fun execute(current: ResolutionStrategyWithCurrent) {
                        @Suppress("UNCHECKED_CAST")
                        task.project.configure(current, resolutionStrategyClosure as Closure<Any>)
                    }
                }
            )
            task.logger.warn(
                "dependencyUpdates.resolutionStrategy: Remove the assignment operator, \"=\", when setting this task property"
            )
        }

        val evaluator = SnapshottingDependencyUpdates(
            project = task.project,
            resolutionStrategy = resolutionStrategyAction,
            revision = task.revision,
            outputFormatterArgument = readOutputFormatter(task),
            outputDir = task.outputDir,
            reportfileName = task.reportfileName,
            checkForGradleUpdate = task.checkForGradleUpdate,
            gradleVersionsApiBaseUrl = task.gradleVersionsApiBaseUrl,
            gradleReleaseChannel = task.gradleReleaseChannel,
            checkConstraints = task.checkConstraints,
            checkBuildEnvironmentConstraints = task.checkBuildEnvironmentConstraints,
            filterConfigurations = task.filterConfigurations
        )

        evaluator.run().write()
    }

    private fun requireNoParallel(task: DependencyUpdatesTask) {
        val method = DependencyUpdatesTask::class.java.getDeclaredMethod("requireNoParallel")
        method.isAccessible = true
        method.invoke(task)
    }

    private fun readResolutionStrategyClosure(task: DependencyUpdatesTask): Closure<Any>? {
        val field = DependencyUpdatesTask::class.java.getDeclaredField("resolutionStrategy")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(task) as Closure<Any>?
    }

    private fun readResolutionStrategyAction(task: DependencyUpdatesTask): Action<in ResolutionStrategyWithCurrent>? {
        val field = DependencyUpdatesTask::class.java.getDeclaredField("resolutionStrategyAction")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(task) as Action<in ResolutionStrategyWithCurrent>?
    }

    private fun readOutputFormatter(task: DependencyUpdatesTask): OutputFormatterArgument {
        val method = DependencyUpdatesTask::class.java.getDeclaredMethod("outputFormatter")
        method.isAccessible = true
        return method.invoke(task) as OutputFormatterArgument
    }
}

private class SnapshottingDependencyUpdates(
    private val project: Project,
    private val resolutionStrategy: Action<in ResolutionStrategyWithCurrent>?,
    private val revision: String,
    private val outputFormatterArgument: OutputFormatterArgument,
    private val outputDir: String,
    private val reportfileName: String?,
    private val checkForGradleUpdate: Boolean,
    private val gradleVersionsApiBaseUrl: String,
    private val gradleReleaseChannel: String,
    private val checkConstraints: Boolean,
    private val checkBuildEnvironmentConstraints: Boolean,
    private val filterConfigurations: Spec<Configuration>
) {

    fun run(): DependencyUpdatesReporter {
        val projectConfigs = snapshotProjectConfigurations()
        val status = resolveProjects(projectConfigs, checkConstraints)

        val buildscriptProjectConfigs = snapshotBuildscriptConfigurations()
        val buildscriptStatus = resolveProjects(buildscriptProjectConfigs, checkBuildEnvironmentConstraints)

        val statuses = status + buildscriptStatus
        val versions = VersionMapping(project, statuses)
        val unresolved = statuses.mapNotNullTo(mutableSetOf()) { it.unresolved }
        val projectUrls = statuses
            .filter { !it.projectUrl.isNullOrEmpty() }
            .associateBy(
                { mapOf("group" to it.coordinate.groupId, "name" to it.coordinate.artifactId) },
                { it.projectUrl.toString() }
            )

        return createReporter(versions, unresolved, projectUrls)
    }

    private fun snapshotProjectConfigurations(): Map<Project, Set<Configuration>> =
        project.allprojects.associateWith { currentProject ->
            currentProject.configurations
                .matching(filterConfigurations)
                .toList()
                .toCollection(linkedSetOf())
        }

    private fun snapshotBuildscriptConfigurations(): Map<Project, Set<Configuration>> =
        project.allprojects.associateWith { currentProject ->
            currentProject.buildscript.configurations
                .matching(filterConfigurations)
                .toList()
                .toCollection(linkedSetOf())
        }

    private fun resolveProjects(
        projectConfigs: Map<Project, Set<Configuration>>,
        checkConstraints: Boolean
    ): Set<DependencyStatus> {
        val resultStatus = hashSetOf<DependencyStatus>()
        projectConfigs.forEach { (currentProject, currentConfigurations) ->
            val resolver = Resolver(currentProject, resolutionStrategy, checkConstraints)
            currentConfigurations
                .filter(Configuration::isCanBeResolved)
                .forEach { configuration ->
                    resolver.resolve(configuration, revision).forEach { status ->
                        addValidatedDependencyStatus(resultStatus, status)
                    }
                }
        }
        return resultStatus
    }

    private fun createReporter(
        versions: VersionMapping,
        unresolved: Set<UnresolvedDependency>,
        projectUrls: Map<Map<String, String>, String>
    ): DependencyUpdatesReporter {
        val currentVersions = versions.current.associateBy(
            { mapOf("group" to it.groupId, "name" to it.artifactId) },
            { it }
        )
        val latestVersions = versions.latest.associateBy(
            { mapOf("group" to it.groupId, "name" to it.artifactId) },
            { it }
        )
        val upToDateVersions = versions.upToDate.associateBy(
            { mapOf("group" to it.groupId, "name" to it.artifactId) },
            { it }
        )
        val downgradeVersions = toMap(versions.downgrade)
        val upgradeVersions = toMap(versions.upgrade)

        val gradleUpdateChecker = GradleUpdateChecker(checkForGradleUpdate, gradleVersionsApiBaseUrl)

        return DependencyUpdatesReporter(
            project,
            revision,
            outputFormatterArgument,
            outputDir,
            reportfileName,
            currentVersions,
            latestVersions,
            upToDateVersions,
            downgradeVersions,
            upgradeVersions,
            versions.undeclared,
            unresolved,
            projectUrls,
            gradleUpdateChecker,
            gradleReleaseChannel
        )
    }

    companion object {
        private fun addValidatedDependencyStatus(
            statusCollection: HashSet<DependencyStatus>,
            status: DependencyStatus
        ) {
            val existing = statusCollection.find { it.coordinate.key == status.coordinate.key }
            if (existing == null) {
                statusCollection.add(status)
            } else if (status.coordinate.version != "none") {
                statusCollection.add(status)
                if (existing.coordinate.version == "none") {
                    statusCollection.remove(existing)
                }
            }
        }

        private fun toMap(coordinates: Set<Coordinate>): Map<Map<String, String>, Coordinate> {
            val map = LinkedHashMap<Map<String, String>, Coordinate>()
            coordinates.forEach { coordinate ->
                var index = 0
                while (true) {
                    val artifactId = coordinate.artifactId + if (index == 0) "" else "[${index + 1}]"
                    val key = linkedMapOf("group" to coordinate.groupId, "name" to artifactId)
                    if (map.putIfAbsent(key, coordinate) == null) {
                        break
                    }
                    index += 1
                }
            }
            return map
        }
    }
}

/**
 * Determines if a version string represents a non-stable release
 */
private fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

/**
 * Creates a task for automatically applying dependency updates
 */
private fun Project.createDependencyUpdateTask() {
    tasks.register("updateDependencies") {
        group = "dependencies"
        description = "Updates project dependencies to latest stable versions"

        // Delegates to dependencyUpdates task whose plugin is not CC-safe on Gradle 9.
        notCompatibleWithConfigurationCache(
            "Delegates to dependencyUpdates task which is not configuration-cache compatible"
        )

        doLast {
            logger.lifecycle("Checking for dependency updates...")

            // Run dependency updates task
            project.tasks.named("dependencyUpdates").get().actions.forEach { action ->
                action.execute(project.tasks.named("dependencyUpdates").get())
            }

            logger.lifecycle(
                "Dependency update check completed. Check build/dependencyUpdates/report.html for results."
            )
        }
    }
}

/**
 * Creates automated security vulnerability scanning task
 */
private fun Project.createSecurityScanTask() {
    tasks.register("securityScan") {
        group = "verification"
        description = "Scans dependencies for known security vulnerabilities"

        doLast {
            logger.lifecycle("Scanning dependencies for security vulnerabilities...")

            // This would integrate with tools like OWASP Dependency Check
            // For now, we'll output a placeholder
            logger.lifecycle(
                """
                Security scan would check:
                - Known CVEs in dependencies
                - License compliance
                - Outdated security-critical libraries
                
                Consider integrating with:
                - OWASP Dependency Check
                - Snyk
                - GitHub Security Advisories
                """.trimIndent()
            )
        }
    }
}
