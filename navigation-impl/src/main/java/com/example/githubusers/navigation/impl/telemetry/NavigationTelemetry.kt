package com.example.githubusers.navigation.impl.telemetry

/** Minimal telemetry interface for navigation events. */
interface NavigationTelemetry {
    fun onNavigate(deepLink: String) {}

    fun onPopBackTo(
        deepLink: String,
        inclusive: Boolean,
    ) {}

    fun onClearBackStack() {}

    fun onResolveDeepLink(
        deepLink: String,
        success: Boolean,
    ) {}

    fun onRestore(success: Boolean) {}
}

object NoopNavigationTelemetry : NavigationTelemetry
