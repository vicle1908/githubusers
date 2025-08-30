rootProject.name = "githubusers"

// Configuration properties for build modes
val useCompositeBuilds = providers.gradleProperty("useCompositeBuilds").orNull?.toBoolean() ?: true
val devCatalogEnabled = providers.gradleProperty("devCatalog").map { it.toBoolean() }.getOrElse(true)

println(
    """========================================
Build Configuration:
  - Composite Builds: ${if (useCompositeBuilds) "ENABLED (local development)" else "DISABLED (using artifacts)"}
  - To change: add -PuseCompositeBuilds=false or -PincludeApp=false
========================================"""
)

if (useCompositeBuilds) {
    println("Using local composite builds for development")
    println("Note: Catalog default is to include from source; toggle with -PdevCatalog=<true|false>")

    // devCatalog toggle: include catalog from source only when enabled
    if (devCatalogEnabled) {
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
    includeBuild("core-mvi") {
        dependencySubstitution {
            substitute(module("com.example.githubusers:core-mvi")).using(project(":"))
        }
    }
    includeBuild("core-domain") {
        dependencySubstitution {
            substitute(module("com.example.githubusers:core-domain")).using(project(":"))
        }
    }
    includeBuild("core-data") {
        dependencySubstitution {
            substitute(module("com.example.githubusers:core-data")).using(project(":"))
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
    includeBuild("navigation-annotations") {
        dependencySubstitution {
            substitute(module("com.example.githubusers:navigation-annotations")).using(project(":"))
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
    includeBuild("feature-users-list") {
        dependencySubstitution {
            substitute(module("com.example.githubusers:feature-users-list")).using(project(":"))
        }
    }
    includeBuild("feature-users-detail") {
        dependencySubstitution {
            substitute(module("com.example.githubusers:feature-users-detail")).using(project(":"))
        }
    }
    includeBuild("feature-search") {
        dependencySubstitution {
            substitute(module("com.example.githubusers:feature-search")).using(project(":"))
        }
    }

    // Feature aggregators
    includeBuild("features-dev") {
        dependencySubstitution {
            substitute(module("com.example.githubusers:features-dev")).using(project(":"))
        }
    }
    includeBuild("features-prod") {
        dependencySubstitution {
            substitute(module("com.example.githubusers:features-prod")).using(project(":"))
        }
    }

    // Test module for convention plugin system
    includeBuild("test-module") {
        dependencySubstitution {
            substitute(module("com.example.githubusers:test-module")).using(project(":"))
        }
    }
    
    // ✅ NEW: Apply base convention plugin to all modules automatically
    // Note: Automatic plugin application will be handled in individual module build files
    // for now, until we can implement a proper solution
} else {
    println("Using published artifacts from repositories")
    println("Make sure all modules are published to Maven Local or remote repository")
    println("To publish all: ./gradlew publishToMavenLocal")
}

// App module is always included as composite
includeBuild("app")

// Build cache configuration (local always on; optional remote via properties)
// Temporarily disabled due to configuration issues
/*
buildCache {
    local {
        isEnabled = true
        // Periodically clean old cache entries to keep disk usage in check
        removeUnusedEntriesAfterDays = 7
    }
    // To enable remote build cache, define these in gradle.properties or via -P
    // remoteBuildCacheUrl=https://your-cache-server/cache/
    // remoteBuildCachePush=true
    val remoteUrl = providers.gradleProperty("remoteBuildCacheUrl").orNull
    if (!remoteUrl.isNullOrBlank()) {
        remote(HttpBuildCache::class) {
            url = uri(remoteUrl)
            isPush = providers.gradleProperty("remoteBuildCachePush").map { it.toBoolean() }.getOrElse(false)
            isEnabled = true
            credentials {
                username = providers.environmentVariable("GRADLE_CACHE_USER").orNull
                password = providers.environmentVariable("GRADLE_CACHE_PASS").orNull
            }
        }
    }
}
*/
