package com.example.githubusers.core.ui.navigation

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember

/**
 * Utility class for managing predictive back gesture callbacks with proper priority system.
 *
 * This follows Android's best practices for predictive back gesture implementation:
 * - Use OnBackPressedCallback for UI logic (dialogs, forms, navigation)
 * - Use proper priority system (PRIORITY_DEFAULT, PRIORITY_OVERLAY, PRIORITY_SYSTEM_NAVIGATION_OBSERVER)
 * - Enable/disable callbacks based on UI state
 * - Single responsibility principle for each callback
 */
object PredictiveBackManager {

    /**
     * Creates and manages an OnBackPressedCallback with the specified priority.
     *
     * @param enabled Whether the callback is initially enabled
     * @param priority The priority level for the callback
     * @param onBackPressed The action to perform when back is pressed
     * @return A DisposableEffect that manages the callback lifecycle
     */
    @Composable
    fun rememberBackPressedCallback(
        enabled: Boolean = true,
        priority: Int = Priority.DEFAULT,
        onBackPressed: () -> Unit
    ): OnBackPressedCallback {
        val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

        val callback = remember {
            object : OnBackPressedCallback(enabled) {
                override fun handleOnBackPressed() {
                    onBackPressed()
                }
            }
        }

        DisposableEffect(onBackPressedDispatcher, enabled, priority) {
            onBackPressedDispatcher?.addCallback(callback)
            onDispose {
                callback.remove()
            }
        }

        return callback
    }

    /**
     * Priority levels for different types of callbacks.
     */
    object Priority {
        /**
         * Default priority for general navigation callbacks.
         * Use for standard back navigation and UI logic.
         */
        const val DEFAULT = 0

        /**
         * Overlay priority for dialog and modal callbacks.
         * Use for dialogs, bottom sheets, and overlay UI elements.
         */
        const val OVERLAY = 1

        /**
         * System navigation observer priority for logging and analytics.
         * Use for observing back events without consuming them.
         * Available on Android 16+ devices.
         */
        const val SYSTEM_NAVIGATION_OBSERVER = 2
    }

    /**
     * Common callback patterns for different UI scenarios.
     */
    object Patterns {

        /**
         * Dialog dismissal pattern.
         * Use for modal dialogs, bottom sheets, and overlay content.
         */
        @Composable
        fun dialogDismissal(enabled: Boolean = true, onDismiss: () -> Unit): OnBackPressedCallback =
            rememberBackPressedCallback(
                enabled = enabled,
                priority = Priority.OVERLAY,
                onBackPressed = onDismiss
            )

        /**
         * Form validation pattern.
         * Use for preventing navigation when forms have unsaved changes.
         */
        @Composable
        fun formValidation(hasUnsavedChanges: Boolean, onShowConfirmation: () -> Unit): OnBackPressedCallback =
            rememberBackPressedCallback(
                enabled = hasUnsavedChanges,
                priority = Priority.DEFAULT,
                onBackPressed = onShowConfirmation
            )

        /**
         * Navigation pattern.
         * Use for complex navigation scenarios and back stack management.
         */
        @Composable
        fun navigation(enabled: Boolean = true, onNavigateBack: () -> Unit): OnBackPressedCallback =
            rememberBackPressedCallback(
                enabled = enabled,
                priority = Priority.DEFAULT,
                onBackPressed = onNavigateBack
            )

        /**
         * System observer pattern.
         * Use for logging and analytics without consuming back events.
         * Available on Android 16+ devices.
         */
        @Composable
        fun systemObserver(enabled: Boolean = true, onBackObserved: () -> Unit): OnBackPressedCallback =
            rememberBackPressedCallback(
                enabled = enabled,
                priority = Priority.SYSTEM_NAVIGATION_OBSERVER,
                onBackPressed = onBackObserved
            )
    }
}
