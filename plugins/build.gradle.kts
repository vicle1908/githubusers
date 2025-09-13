import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
    `maven-publish`
}

group = "com.example.githubusers"
version = libs.versions.githubusersPlugins.get()

// Configure the convention plugins to target JDK 21
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

repositories {
    google()
    mavenCentral()
    mavenLocal()
    gradlePluginPortal()
}

dependencies {
    // Use version catalog for Gradle plugin classpath dependencies
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)

    // Firebase plugins for Firebase Performance Monitoring
    compileOnly(libs.google.services)
    compileOnly(libs.firebase.perf.plugin)

    // Detekt Gradle plugin for typed access in convention plugins
    implementation(libs.detektGradlePlugin)

    // Bring ktlint-gradle onto the classpath via version catalog
    implementation(libs.ktlint.gradle)

    // Version update plugin for dependency management (use library alias, not plugin alias)
    implementation(libs.gradle.versions.plugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "githubusers.android.application"
            implementationClass = "com.example.githubusers.plugins.AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "githubusers.android.application.compose"
            implementationClass = "com.example.githubusers.plugins.AndroidApplicationComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "githubusers.android.library"
            implementationClass = "com.example.githubusers.plugins.AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "githubusers.android.library.compose"
            implementationClass = "com.example.githubusers.plugins.AndroidLibraryComposeConventionPlugin"
        }
        register("androidHilt") {
            id = "githubusers.android.hilt"
            implementationClass = "com.example.githubusers.plugins.AndroidHiltConventionPlugin"
        }
        register("androidRoom") {
            id = "githubusers.android.room"
            implementationClass = "com.example.githubusers.plugins.AndroidRoomConventionPlugin"
        }
        register("jvmLibrary") {
            id = "githubusers.jvm.library"
            implementationClass = "com.example.githubusers.plugins.JvmLibraryConventionPlugin"
        }
        register("androidPublishing") {
            id = "githubusers.android.publishing"
            implementationClass = "com.example.githubusers.plugins.AndroidPublishingConventionPlugin"
        }
        register("baseModule") {
            id = "githubusers.base.module"
            implementationClass = "com.example.githubusers.plugins.BaseModulePlugin"
        }
        register("sharedConfiguration") {
            id = "githubusers.shared.configuration"
            implementationClass = "com.example.githubusers.plugins.SharedConfigurationPlugin"
        }
        register("catalogAccess") {
            id = "githubusers.catalog.access"
            implementationClass = "com.example.githubusers.plugins.CatalogAccessPlugin"
        }
        register("androidLibraryPublishing") {
            id = "githubusers.android.library.publishing"
            implementationClass = "com.example.githubusers.plugins.AndroidLibraryPublishingConventionPlugin"
        }
        register("qualityKtlint") {
            id = "githubusers.quality.ktlint"
            implementationClass = "com.example.githubusers.plugins.KtlintConventionPlugin"
        }
        register("qualityDetekt") {
            id = "githubusers.quality.detekt"
            implementationClass = "com.example.githubusers.plugins.DetektConventionPlugin"
        }
        register("testConvention") {
            id = "githubusers.test.convention"
            implementationClass = "com.example.githubusers.plugins.TestConventionPlugin"
        }

        // Build guard to enforce no local build logic blocks
        register("buildGuard") {
            id = "githubusers.build.guard"
            implementationClass = "com.example.githubusers.plugins.BuildGuardConventionPlugin"
        }
        // ✅ NEW: Specialized convention plugins for auto-detection
        register("featureModule") {
            id = "githubusers.feature.module"
            implementationClass = "com.example.githubusers.plugins.FeatureModuleConventionPlugin"
        }
        register("coreModule") {
            id = "githubusers.core.module"
            implementationClass = "com.example.githubusers.plugins.CoreModuleConventionPlugin"
        }
        register("navigationModule") {
            id = "githubusers.navigation.module"
            implementationClass = "com.example.githubusers.plugins.NavigationModuleConventionPlugin"
        }
        register("commonVersion") {
            id = "githubusers.common.version"
            implementationClass = "com.example.githubusers.plugins.CommonVersionConventionPlugin"
        }
        register("platformModule") {
            id = "githubusers.platform.module"
            implementationClass = "com.example.githubusers.plugins.PlatformModuleConventionPlugin"
        }
        register("dependencyUpdate") {
            id = "githubusers.dependency.update"
            implementationClass = "com.example.githubusers.plugins.DependencyUpdatePlugin"
        }
        register("firebasePerformance") {
            id = "githubusers.firebase.performance"
            implementationClass = "com.example.githubusers.plugins.FirebasePerformanceConventionPlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            // Suppress duplicate publication warning by using different artifactId
            artifactId = "githubusers-plugins"

            // POM customization
            pom {
                name.set("GitHubUsers Gradle Plugins")
                description.set("Convention plugins for GitHubUsers project")
                url.set("https://github.com/your-org/githubusers")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("your-id")
                        name.set("Your Name")
                        email.set("your-email@example.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/your-org/githubusers.git")
                    developerConnection.set("scm:git:ssh://github.com/your-org/githubusers.git")
                    url.set("https://github.com/your-org/githubusers")
                }
            }
        }
    }

    repositories {
        // Local Maven repository
        mavenLocal()

        // Remote Maven repository (Nexus/Artifactory) - only if configured
        val nexusReleaseUrl = project.findProperty("nexusReleaseUrl") as String?
        val nexusSnapshotUrl = project.findProperty("nexusSnapshotUrl") as String?

        if (!nexusReleaseUrl.isNullOrBlank() && !nexusSnapshotUrl.isNullOrBlank()) {
            maven {
                name = "nexus"
                url = uri(if (version.toString().endsWith("SNAPSHOT")) nexusSnapshotUrl else nexusReleaseUrl)

                credentials {
                    username = project.findProperty("nexusUsername") as String? ?: ""
                    password = project.findProperty("nexusPassword") as String? ?: ""
                }
            }
        }
    }
}
