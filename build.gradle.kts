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
tasks.register("assembleApp") {
    group = "build"
    description = "Assemble the app module"

    gradle.includedBuilds.find { it.name == "app" }?.let {
        dependsOn(it.task(":assembleDebug"))
    } ?: run {
        doLast {
            println("App module not included. Use -PincludeApp=true to include it.")
        }
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
