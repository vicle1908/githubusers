plugins {
    java
    `version-catalog`
    `maven-publish`
}

group = "com.example.githubusers"
version = "1.0.0"

catalog {
    versionCatalog {
        from(files("gradle/libs.versions.toml"))
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["versionCatalog"])

            // POM customization
            pom {
                name.set("GitHubUsers Catalog")
                description.set("Version catalog for GitHubUsers project")
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
