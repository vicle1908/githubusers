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
        mavenLocal()
    }
    versionCatalogs {
        create("libs") {
            from("com.example.githubusers:catalog:1.0.0")
        }
    }
}

rootProject.name = "internal-platform"
