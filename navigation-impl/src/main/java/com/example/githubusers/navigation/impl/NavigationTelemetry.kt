package com.example.githubusers.navigation.impl

/**
 * Navigation telemetry hooks. No-op by default.
 */
interface NavigationTelemetry {
    // Controller events
    fun onNavigate(deepLink: String) {}

    fun onNavigateBack(result: Boolean) {}

    fun onNavigateUp(result: Boolean) {}

    fun onPopBackStackTo(
        target: String,
        inclusive: Boolean,
        result: Boolean,
    ) {}

    fun onClearBackStack() {}

    fun onHandleDeepLinkAttempt(link: String) {}

    fun onHandleDeepLinkResult(
        link: String,
        success: Boolean,
    ) {}

    fun onGateBlocked(action: String) {}

    // Resolver events
    fun onResolveAttempt(link: String) {}

    fun onResolveSuccess(
        link: String,
        owned: Boolean,
    ) {}

    fun onResolveFallback(link: String) {}

    // Router events
    fun onRouteAttempt(uri: String) {}

    fun onRouteHandled(
        uri: String,
        handlerModuleId: String,
    ) {}

    fun onRouteMiss(uri: String) {}
}

object NoOpNavigationTelemetry : NavigationTelemetry
