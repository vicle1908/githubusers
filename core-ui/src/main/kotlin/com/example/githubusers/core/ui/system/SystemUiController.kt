package com.example.githubusers.core.ui.system

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * System UI Controller using pure Android APIs without Accompanist.
 * Handles status bar and navigation bar appearance.
 */
@Composable
@Suppress("FunctionNaming")
fun SystemUiController(
    darkIcons: Boolean = false,
    darkNavigationIcons: Boolean = false,
    isNavigationBarContrastEnforced: Boolean = true
) {
    val view = LocalView.current

    DisposableEffect(
        darkIcons,
        darkNavigationIcons,
        isNavigationBarContrastEnforced
    ) {
        val window = (view.context as? Activity)?.window ?: return@DisposableEffect onDispose {}

        val insetsController = WindowCompat.getInsetsController(window, view)
        val previousStatusBarDarkIcons = insetsController.isAppearanceLightStatusBars
        val previousNavigationBarDarkIcons = insetsController.isAppearanceLightNavigationBars

        // Set icon colors
        insetsController.isAppearanceLightStatusBars = darkIcons
        insetsController.isAppearanceLightNavigationBars = darkNavigationIcons

        // Set navigation bar contrast (API 29+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = isNavigationBarContrastEnforced
        }

        onDispose {
            insetsController.isAppearanceLightStatusBars = previousStatusBarDarkIcons
            insetsController.isAppearanceLightNavigationBars = previousNavigationBarDarkIcons
        }
    }
}

/**
 * Hide system bars (status bar and navigation bar)
 */
@Composable
@Suppress("FunctionNaming", "Unused")
fun HideSystemBars() {
    val view = LocalView.current

    DisposableEffect(Unit) {
        val window = (view.context as? Activity)?.window ?: return@DisposableEffect onDispose {}
        val insetsController = WindowCompat.getInsetsController(window, view)

        // Configure the behavior of hidden system bars
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // Hide system bars
        insetsController.hide(WindowInsetsCompat.Type.systemBars())

        onDispose {
            // Show system bars when leaving
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}

/**
 * Make the app edge-to-edge (draw behind system bars)
 */
@Composable
@Suppress("FunctionNaming", "Unused")
fun EdgeToEdgeSystemUi() {
    val view = LocalView.current

    DisposableEffect(Unit) {
        val window = (view.context as? Activity)?.window ?: return@DisposableEffect onDispose {}

        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        onDispose {
            // Note: We don't restore this as it's typically a one-time setup
        }
    }
}

