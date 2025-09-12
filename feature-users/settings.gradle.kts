pluginManagement {
    includeBuild("../plugins")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
    versionCatalogs {
        create("libs") {
            from(files("../catalog/gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "feature-users"
