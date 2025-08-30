package com.example.githubusers.navigation.impl

import android.util.Log

/** Simple Logcat-based telemetry for Navigation 3. */
class LoggingNavigationTelemetry(
    private val tag: String = "Nav3",
) : NavigationTelemetry {
    override fun onNavigate(deepLink: String) {
        Log.d(tag, "navigate: $deepLink")
    }

    override fun onNavigateBack(result: Boolean) {
        Log.d(tag, "navigateBack: $result")
    }

    override fun onNavigateUp(result: Boolean) {
        Log.d(tag, "navigateUp: $result")
    }

    override fun onPopBackStackTo(
        target: String,
        inclusive: Boolean,
        result: Boolean,
    ) {
        Log.d(tag, "popBackStackTo: target=$target inclusive=$inclusive result=$result")
    }

    override fun onClearBackStack() {
        Log.d(tag, "clearBackStack")
    }

    override fun onHandleDeepLinkAttempt(link: String) {
        Log.d(tag, "handleDeepLink.attempt: $link")
    }

    override fun onHandleDeepLinkResult(
        link: String,
        success: Boolean,
    ) {
        Log.d(tag, "handleDeepLink.result: $link => $success")
    }

    override fun onGateBlocked(action: String) {
        Log.w(tag, "gateBlocked: $action")
    }

    override fun onResolveAttempt(link: String) {
        Log.d(tag, "resolve.attempt: $link")
    }

    override fun onResolveSuccess(
        link: String,
        owned: Boolean,
    ) {
        Log.d(tag, "resolve.success: $link owned=$owned")
    }

    override fun onResolveFallback(link: String) {
        Log.d(tag, "resolve.fallback: $link")
    }

    override fun onRouteAttempt(uri: String) {
        Log.d(tag, "route.attempt: $uri")
    }

    override fun onRouteHandled(
        uri: String,
        handlerModuleId: String,
    ) {
        Log.d(tag, "route.handled: $uri by=$handlerModuleId")
    }

    override fun onRouteMiss(uri: String) {
        Log.d(tag, "route.miss: $uri")
    }
}
