pluginManagement {
    includeBuild("../plugins")
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        mavenLocal()
    }
    versionCatalogs {
        create("libs") {
            from(files("../catalog/gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "feature-search"
