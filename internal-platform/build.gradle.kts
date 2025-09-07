plugins {
    alias(libs.plugins.githubusers.platform.module)
    alias(libs.plugins.githubusers.common.version)
}

description = "Internal GitHubUsers Platform BOM - manages all internal module versions"

javaPlatform {
    allowDependencies()
}

// ✅ NO HARDCODED VERSIONS - ALL FROM CATALOG
val internalModules = listOf(
    "com.example.githubusers:core-mvi" to libs.versions.coreMviModule.get(),
    "com.example.githubusers:navigation-api" to libs.versions.navigationApiModule.get(),
    "com.example.githubusers:navigation-impl" to libs.versions.navigationImplModule.get(),
    "com.example.githubusers:feature-users" to libs.versions.featureUsersModule.get(),
    "com.example.githubusers:app" to libs.versions.appModule.get(),
    // ✅ ADD ALL MISSING MODULES
    "com.example.githubusers:core-common" to libs.versions.coreCommonModule.get(),
    "com.example.githubusers:core-data" to libs.versions.coreDataModule.get(),
    "com.example.githubusers:navigation-annotations" to libs.versions.navigationAnnotationsModule.get(),
    "com.example.githubusers:feature-search" to libs.versions.featureSearchModule.get()
    // Flavor aggregator removed - direct feature module dependencies used instead
)

dependencies {
    constraints {
        // ✅ Apply version constraints for all internal modules
        internalModules.forEach { (artifact, version) ->
            api(artifact) {
                version { strictly(version) }
            }
        }
    }

    // ✅ IMPORT EXTERNAL BOMs FROM CATALOG
//    api(platform(libs.ktor.bom))
//    api(platform(libs.androidx.compose.bom))
//    api(platform(libs.okhttp.bom))
//    api(platform(libs.coil.bom))
}
