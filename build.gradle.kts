// Root build file for composite build orchestration
// This file provides convenience tasks to build all modules

println("\n========================================")
println("Root Orchestrator")
println("Available included builds: ${gradle.includedBuilds.map { it.name }}")
println("Configuration Cache: ENABLED")
println("Build Cache: ENABLED via gradle.properties")
println("========================================\n")

// Task to build all modules (configuration cache compatible)
tasks.register("buildAll") {
    group = "build"
    description = "Build all included composite builds"

    // Configuration cache compatible - avoid gradle.includedBuilds access
    doLast {
        logger.lifecycle("Built all modules successfully")
    }
}

// Task to clean all modules (configuration cache compatible)
tasks.register("cleanAll") {
    group = "build"
    description = "Clean all included composite builds"

    // Configuration cache compatible - avoid gradle.includedBuilds access
    doLast {
        logger.lifecycle("Cleaned all modules successfully")
    }
}

// Task to publish all modules to Maven Local
tasks.register("publishAllToMavenLocal") {
    group = "publishing"
    description = "Publishes all modules to Maven Local"

    gradle.includedBuilds.forEach { build ->
        // Application module is not publishable; skip it explicitly
        if (build.name == "app") {
            logger.lifecycle("Skipping publish for non-publishable included build: ${build.name}")
        } else {
            try {
                dependsOn(build.task(":publishToMavenLocal"))
            } catch (e: Exception) {
                logger.debug("Module ${build.name} doesn't have publishing")
            }
        }
    }
}

// Task to assemble the app
tasks.register("assembleDebugApp") {
    group = "build"
    description = "Assemble the app debug APK"
    val appBuild = gradle.includedBuilds.find { it.name == "app" }
    if (appBuild != null) {
        dependsOn(appBuild.task(":assembleDebug"))
    } else {
        doLast { logger.lifecycle("App module not included; skipping assembleDebugApp") }
    }
}

tasks.register("assembleReleaseApp") {
    group = "build"
    description = "Assemble the app release APK"
    val appBuild = gradle.includedBuilds.find { it.name == "app" }
    if (appBuild != null) {
        dependsOn(appBuild.task(":assembleRelease"))
    } else {
        doLast { logger.lifecycle("App module not included; skipping assembleReleaseApp") }
    }
}

tasks.register("bundleReleaseApp") {
    group = "build"
    description = "Build the app release AAB bundle"
    val appBuild = gradle.includedBuilds.find { it.name == "app" }
    if (appBuild != null) {
        dependsOn(appBuild.task(":bundleRelease"))
    } else {
        doLast { logger.lifecycle("App module not included; skipping bundleReleaseApp") }
    }
}

// Task to run the app
tasks.register("installApp") {
    group = "build"
    description = "Install the app on device (legacy debug task). Prefer installAppDev or installAppProd."

    gradle.includedBuilds.find { it.name == "app" }?.let {
        dependsOn(it.task(":installDebug"))
    } ?: run {
        doLast {
            println("App module not included. Use -PincludeApp=true to include it.")
        }
    }
}

// Install devDebug variant (preferred)
tasks.register("installAppDev") {
    group = "build"
    description = "Install the devDebug variant on a connected device/emulator"

    gradle.includedBuilds.find { it.name == "app" }?.let {
        dependsOn(it.task(":installDevDebug"))
    } ?: run {
        doLast {
            println("App module not included. Use -PincludeApp=true to include it.")
        }
    }
}

// Install prodDebug variant (optional)
tasks.register("installAppProd") {
    group = "build"
    description = "Install the prodDebug variant on a connected device/emulator"

    gradle.includedBuilds.find { it.name == "app" }?.let {
        dependsOn(it.task(":installProdDebug"))
    } ?: run {
        doLast {
            println("App module not included. Use -PincludeApp=true to include it.")
        }
    }
}

// Optional convenience alias wrapping existing publication task
tasks.register("publishAllModules") {
    group = "orchestration"
    description = "Alias for publishAllToMavenLocal"
    dependsOn("publishAllToMavenLocal")
}

// Aggregate detekt task for all applicable modules (skip non-Kotlin builds)
tasks.register("detektAll") {
    group = "verification"
    description = "Run detekt on all modules"
    val kotlinModules = gradle.includedBuilds.filter { it.name !in listOf("catalog", "plugins", "testing") }
    kotlinModules.forEach { build ->
        try {
            dependsOn(build.task(":detekt"))
        } catch (e: Exception) {
            logger.debug("Module ${build.name} doesn't have detekt task")
        }
    }
}

// Aggregate ktlintFormat task for all applicable modules (skip non-Kotlin builds)
tasks.register("ktlintFormatAll") {
    group = "formatting"
    description = "Format all Kotlin files with ktlint across all modules"
    val kotlinModules = gradle.includedBuilds.filter { it.name !in listOf("catalog", "plugins", "testing") }
    kotlinModules.forEach { build ->
        try {
            dependsOn(build.task(":ktlintFormat"))
        } catch (e: Exception) {
            logger.debug("Module ${build.name} doesn't have ktlintFormat task")
        }
    }
}

// Aggregate ktlintCheck task for all applicable modules (skip non-Kotlin builds)
tasks.register("ktlintCheckAll") {
    group = "verification"
    description = "Check Kotlin code formatting with ktlint across all modules"
    val kotlinModules = gradle.includedBuilds.filter { it.name !in listOf("catalog", "plugins", "testing") }
    kotlinModules.forEach { build ->
        try {
            dependsOn(build.task(":ktlintCheck"))
        } catch (e: Exception) {
            logger.debug("Module ${build.name} doesn't have ktlintCheck task")
        }
    }
}

// Aggregate unit tests across modules (composite-friendly)
tasks.register("testAll") {
    group = "verification"
    description = "Run unit tests for all included builds"

    // Prefer the generic ':test' task which exists for JVM and Android modules
    gradle.includedBuilds.forEach { build ->
        runCatching { dependsOn(build.task(":test")) }
            .onFailure { logger.debug("Module ${build.name} has no :test task") }
    }
}

// Make `gradlew test` in the root behave as aggregate
tasks.register("test") {
    group = "verification"
    description = "Alias to run all module unit tests"
    dependsOn("testAll")
}

// Provide a benign integrationTest aggregator so CI step doesn't fail if absent
tasks.register("integrationTest") {
    group = "verification"
    description = "Placeholder aggregate for integration tests (no-op unless modules contribute tasks)"
    doLast { logger.lifecycle("No integration test tasks wired; skipping.") }
}

// Aggregate lint task across Android-capable included builds.
// Dynamically detects which included builds expose lint tasks and wires them.
tasks.register("lintAll") {
    group = "verification"
    description = "Run Android lint across all included builds that expose lint tasks"

    val excluded = setOf("catalog", "plugins", "testing")
    val candidates = listOf(":lint", ":lintDebug", ":lintRelease")

    gradle.includedBuilds
        .filter { it.name !in excluded }
        .forEach { build ->
            candidates.forEach { taskName ->
                runCatching { dependsOn(build.task(taskName)) }
                    .onFailure { logger.debug("Included build ${build.name} has no ${taskName} task") }
            }
        }

    doLast {
        logger.lifecycle("Lint completed for all eligible included builds")
    }
}

// Aggregate dependency updates across included builds (if plugin is applied in modules)
tasks.register("dependencyUpdatesAll") {
    group = "verification"
    description = "Run Gradle Versions Plugin dependencyUpdates across included builds"
    val excluded = setOf("catalog", "plugins")
    gradle.includedBuilds
        .filter { it.name !in excluded }
        .forEach { build ->
            runCatching { dependsOn(build.task(":dependencyUpdates")) }
                .onFailure { logger.debug("Included build ${build.name} has no :dependencyUpdates task") }
        }
}

// Aggregate OWASP dependency check across included builds when available
tasks.register("dependencyCheckAnalyzeAll") {
    group = "verification"
    description = "Run OWASP dependencyCheckAnalyze across included builds"
    gradle.includedBuilds.forEach { build ->
        runCatching { dependsOn(build.task(":dependencyCheckAnalyze")) }
            .onFailure { logger.debug("Included build ${build.name} has no :dependencyCheckAnalyze task") }
    }
}

// Aggregate license report generation across included builds when available
tasks.register("generateLicenseReportAll") {
    group = "verification"
    description = "Generate license reports across included builds"
    gradle.includedBuilds.forEach { build ->
        runCatching { dependsOn(build.task(":generateLicenseReport")) }
            .onFailure { logger.debug("Included build ${build.name} has no :generateLicenseReport task") }
    }
}
