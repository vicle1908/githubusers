package com.example.githubusers.plugins

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project

/**
 * Configure project lint options
 */
internal fun Project.configureLint() {
    android.lint {
        abortOnError = false
    }
}

private val Project.android: CommonExtension<*, *, *, *, *, *>
    get() = extensions.getByName("android") as CommonExtension<*, *, *, *, *, *>
