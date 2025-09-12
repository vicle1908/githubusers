package com.example.githubusers.navigation.api

import androidx.compose.runtime.staticCompositionLocalOf

/** CompositionLocal exposing a back navigation lambda to features. */
val LocalNavigateBack =
    staticCompositionLocalOf<() -> Boolean> {
        error("NavigateBack not provided")
    }
