package com.example.githubusers.feature.users.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface UserNavKey : NavKey {
    @Serializable
    data object UserList : UserNavKey

    @Serializable
    data class UserDetail(val username: String) : UserNavKey

    @Serializable
    data class UserSettingsDialog(val username: String) : UserNavKey
}
