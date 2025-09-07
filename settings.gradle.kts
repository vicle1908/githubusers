rootProject.name = "githubusers"
// Configuration properties for build modes
val useCompositeBuilds = providers.gradleProperty("useCompositeBuilds").orNull?.toBoolean() ?: true
val devCatalogEnabled = providers.gradleProperty("devCatalogEnabled").orNull?.toBoolean() ?: true

println(
    """========================================
Build Configuration:
  - Composite Builds: ${if (useCompositeBuilds) "ENABLED (local development)" else "DISABLED (using artifacts)"}
========================================"""
)

if (useCompositeBuilds) {
    println("Using local composite builds for development")

    // devCatalog toggle: include catalog from source only when enabled
    if (devCatalogEnabled) {
        // Catalog is included as a regular project above
        println("Catalog included as regular project for version catalog access")
        includeBuild("catalog") {
            dependencySubstitution {
                substitute(module("com.example.githubusers:catalog")).using(project(":"))
            }
        }
    } else {
        println("Using published catalog artifact; enable -PdevCatalog=true to develop catalog from source")
    }

    // Convention plugins
    includeBuild("plugins") {
        dependencySubstitution {
            substitute(module("com.example.githubusers:plugins")).using(project(":"))
        }
    }

    // Internal platform BOM
    includeBuild("internal-platform") {
        dependencySubstitution {
            substitute(module("com.example.githubusers:internal-platform")).using(project(":"))
        }
    }

    // Core modules
    includeBuild("core-common") {
        dependencySubstitution {
            substitute(module("com.example.githubusers:core-common")).using(project(":"))
        }
    }
    includeBuild("core-data") {
        dependencySubstitution {
            substitute(module("com.example.githubusers:core-data")).using(project(":"))
        }
    }
    includeBuild("core-networking") {
        dependencySubstitution {
            substitute(module("com.example.githubusers:core-networking")).using(project(":"))
        }
    }
    includeBuild("core-storage") {
        dependencySubstitution {
            substitute(module("com.example.githubusers:core-storage")).using(project(":"))
        }
    }
    includeBuild("navigation-annotations") {
        dependencySubstitution {
            substitute(module("com.example.githubusers:navigation-annotations")).using(project(":"))
        }
    }
    includeBuild("core-mvi") {
        dependencySubstitution {
            substitute(module("com.example.githubusers:core-mvi")).using(project(":"))
        }
    }
    includeBuild("core-ui") {
        dependencySubstitution {
            substitute(module("com.example.githubusers:core-ui")).using(project(":"))
        }
    }

    // Navigation modules
    includeBuild("navigation-api") {
        dependencySubstitution {
            substitute(module("com.example.githubusers:navigation-api")).using(project(":"))
        }
    }
    includeBuild("navigation-impl") {
        dependencySubstitution {
            substitute(module("com.example.githubusers:navigation-impl")).using(project(":"))
        }
    }
    includeBuild("navigation-ksp") {
        dependencySubstitution {
            substitute(module("com.example.githubusers:navigation-ksp")).using(project(":"))
        }
    }

    // Feature modules
    includeBuild("feature-users") {
        dependencySubstitution {
            substitute(module("com.example.githubusers:feature-users")).using(project(":"))
        }
    }
    includeBuild("feature-search") {
        dependencySubstitution {
            substitute(module("com.example.githubusers:feature-search")).using(project(":"))
        }
    }
    includeBuild("feature-settings") {
        dependencySubstitution {
            substitute(module("com.example.githubusers:feature-settings")).using(project(":"))
        }
    }

    // Feature aggregators removed - direct feature module dependencies used instead
} else {
    println("Using published artifacts from repositories")
    println("Make sure all modules are published to Maven Local or remote repository")
    println("To publish all: ./gradlew publishToMavenLocal")
}

// App module is always included as composite
includeBuild("app")
