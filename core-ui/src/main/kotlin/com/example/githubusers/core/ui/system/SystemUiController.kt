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
fun SystemUiController(
    statusBarColor: Color = Color.Transparent,
    navigationBarColor: Color = Color.Transparent,
    darkIcons: Boolean = false,
    darkNavigationIcons: Boolean = false,
    isNavigationBarContrastEnforced: Boolean = true
) {
    val view = LocalView.current
    
    DisposableEffect(
        statusBarColor,
        navigationBarColor,
        darkIcons,
        darkNavigationIcons,
        isNavigationBarContrastEnforced
    ) {
        val window = (view.context as? Activity)?.window ?: return@DisposableEffect onDispose {}
        
        val previousStatusBarColor = window.statusBarColor
        val previousNavigationBarColor = window.navigationBarColor
        val insetsController = WindowCompat.getInsetsController(window, view)
        val previousStatusBarDarkIcons = insetsController.isAppearanceLightStatusBars
        val previousNavigationBarDarkIcons = insetsController.isAppearanceLightNavigationBars
        
        // Set colors
        window.statusBarColor = statusBarColor.toArgb()
        window.navigationBarColor = navigationBarColor.toArgb()
        
        // Set icon colors
        insetsController.isAppearanceLightStatusBars = darkIcons
        insetsController.isAppearanceLightNavigationBars = darkNavigationIcons
        
        // Set navigation bar contrast (API 29+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = isNavigationBarContrastEnforced
        }
        
        onDispose {
            // Restore previous values
            window.statusBarColor = previousStatusBarColor
            window.navigationBarColor = previousNavigationBarColor
            insetsController.isAppearanceLightStatusBars = previousStatusBarDarkIcons
            insetsController.isAppearanceLightNavigationBars = previousNavigationBarDarkIcons
        }
    }
}

/**
 * Hide system bars (status bar and navigation bar)
 */
@Composable
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
fun EdgeToEdgeSystemUi() {
    val view = LocalView.current
    
    DisposableEffect(Unit) {
        val window = (view.context as? Activity)?.window ?: return@DisposableEffect onDispose {}
        
        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // Make system bars transparent
        window.statusBarColor = Color.Transparent.toArgb()
        window.navigationBarColor = Color.Transparent.toArgb()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        
        onDispose {
            // Note: We don't restore this as it's typically a one-time setup
        }
    }
}

/**
 * Control system UI visibility flags directly
 */
@Suppress("DEPRECATION")
fun Window.setSystemUiVisibility(
    fullscreen: Boolean = false,
    hideNavigation: Boolean = false,
    immersive: Boolean = false
) {
    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            (if (fullscreen) View.SYSTEM_UI_FLAG_FULLSCREEN else 0) or
            (if (hideNavigation) View.SYSTEM_UI_FLAG_HIDE_NAVIGATION else 0) or
            (if (immersive) View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY else 0)
}
