package com.example.githubusers.buildlogic

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.Lint
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

private fun CommonExtension<*, *, *, *, *, *>.lint(action: Lint.() -> Unit) = lint.action()