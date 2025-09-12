package com.example.githubusers.presentation.navigation.deeplink

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.example.githubusers.navigation.impl.DeepLinkDispatcher
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Navigator for cross-module navigation using deep links resolved to Nav3 keys.
 */
@Singleton
class ModuleNavigator
@Inject
constructor(
    private val dispatcher: DeepLinkDispatcher
) {
    /** Navigate to a destination using deep link. */
    fun navigateTo(backStack: NavBackStack<NavKey>, deepLink: String, clearBackStack: Boolean = false) {
        val key = dispatcher.toKey(deepLink) ?: return
        if (clearBackStack) {
            // Clear back stack by removing all entries except the first one
            while (backStack.size > 1) {
                backStack.removeLastOrNull()
            }
        }
        backStack.add(key)
    }
}
