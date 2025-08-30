@file:Suppress("ktlint:standard:function-naming")

package com.example.githubusers.navigation.impl

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.githubusers.navigation.api.Navigation3Controller
import com.example.githubusers.navigation.api.Navigation3Entry

/**
 * Navigation 3 host that manages navigation between destinations.
 * All navigation is done through deep links.
 */
@Composable
fun Navigation3Host(
    controller: Navigation3Controller,
    startDestination: String,
    modifier: Modifier = Modifier,
    canInterceptBack: (Navigation3Entry) -> Boolean = { false },
    onInterceptBack: (Navigation3Entry) -> Unit = {},
    content: @Composable (Navigation3Entry) -> Unit,
) {
    val currentEntry by controller.currentEntry.collectAsState()

    // Initialize with persistence restore, then navigate to start if still empty
    LaunchedEffect(Unit) {
        val restored = controller.restoreFromPersistence()
        if (!restored && currentEntry == null) {
            controller.navigate(startDestination)
        }
    }

    // Handle back gestures at top-of-stack only
    currentEntry?.let { entry ->
        BackHandler(true) {
            if (canInterceptBack(entry)) {
                onInterceptBack(entry)
            } else {
                controller.navigateBack()
            }
        }
        // Display current destination
        content(entry)
    }
}

/**
 * Overload that accepts a typed destination for start.
 */
@Composable
fun Navigation3Host(
    controller: Navigation3Controller,
    startDestination: com.example.githubusers.navigation.api.AppDestination,
    modifier: Modifier = Modifier,
    canInterceptBack: (Navigation3Entry) -> Boolean = { false },
    onInterceptBack: (Navigation3Entry) -> Unit = {},
    content: @Composable (Navigation3Entry) -> Unit,
) {
    val deepLink =
        com.example.githubusers.navigation.api.AppDeepLinks
            .build(startDestination)
    Navigation3Host(
        controller = controller,
        startDestination = deepLink,
        modifier = modifier,
        canInterceptBack = canInterceptBack,
        onInterceptBack = onInterceptBack,
        content = content,
    )
}
