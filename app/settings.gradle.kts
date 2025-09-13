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

rootProject.name = "app"
