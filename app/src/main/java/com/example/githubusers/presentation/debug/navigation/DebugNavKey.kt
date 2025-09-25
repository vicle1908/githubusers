package com.example.githubusers.presentation.debug.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface DebugNavKey : NavKey {
    @Serializable
    data object CorePagingShowcase : DebugNavKey
}
