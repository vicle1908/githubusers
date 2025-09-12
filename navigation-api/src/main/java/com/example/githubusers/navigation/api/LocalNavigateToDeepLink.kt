package com.example.githubusers.navigation.api

import androidx.compose.runtime.staticCompositionLocalOf

/** CompositionLocal exposing a deep link navigation lambda to features. */
val LocalNavigateToDeepLink =
    staticCompositionLocalOf<(String) -> Unit> {
        error("NavigateToDeepLink not provided")
    }
