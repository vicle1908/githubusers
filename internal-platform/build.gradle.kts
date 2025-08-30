import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    id("java-platform")
    id("githubusers.android.publishing")
}

description = "Internal GitHubUsers Platform BOM - manages all internal module versions"

javaPlatform {
    allowDependencies()
}

// Define internal modules with their catalog version keys and default versions
data class ModuleConfig(
    val artifact: String,
    val versionKey: String,
    val defaultVersion: String
)

val internalModules = listOf(
    ModuleConfig("com.example.githubusers:core-mvi", "coreMviModule", "1.0.0"),
    ModuleConfig("com.example.githubusers:navigation-api", "navigationApiModule", "1.2.0"),
    ModuleConfig("com.example.githubusers:navigation-impl", "navigationImplModule", "1.0.0"),
    ModuleConfig("com.example.githubusers:feature-users", "featureUsersModule", "1.0.0"),
    ModuleConfig("com.example.githubusers:app", "appModule", "1.0.0")
)

// Extension function to safely get version from catalog
fun VersionCatalog?.getVersionOrDefault(versionKey: String, defaultVersion: String): String {
    return try {
        this?.findVersion(versionKey)?.get()?.requiredVersion ?: defaultVersion
    } catch (e: Exception) {
        defaultVersion
    }
}

val catalog = runCatching {
    extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
}.getOrNull()

dependencies {
    constraints {
        // Apply version constraints for all internal modules
        internalModules.forEach { module ->
            val version = catalog.getVersionOrDefault(module.versionKey, module.defaultVersion)
            api(module.artifact) {
                version { strictly(version) }
            }
        }

        // External library constraints for transitive dependency management
        api("org.jetbrains.kotlin:kotlin-stdlib") {
            version { require("[2.2.0, 3.0.0)") }
            because("All modules must use Kotlin 2.2.x")
        }
        api("org.jetbrains.kotlinx:kotlinx-coroutines-core") { version { require("[1.10.0, 2.0.0)") } }
        api("org.jetbrains.kotlinx:kotlinx-coroutines-android") { version { require("[1.10.0, 2.0.0)") } }
    }

    // Import external BOMs
    // These versions are defined in the catalog but we can't use catalog accessors in platform
    // To update versions, modify them in catalog/gradle/libs.versions.toml
    api(platform("io.ktor:ktor-bom:${catalog.getVersionOrDefault("ktor", "3.2.2")}"))
    api(platform("androidx.compose:compose-bom:${catalog.getVersionOrDefault("compose-bom", "2025.07.00")}"))
    api(platform("io.coil-kt.coil3:coil-bom:${catalog.getVersionOrDefault("coil3", "3.3.0")}"))
}
