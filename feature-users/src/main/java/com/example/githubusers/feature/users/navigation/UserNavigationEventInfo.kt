package com.example.githubusers.feature.users.navigation

import androidx.navigationevent.NavigationEventInfo

/**
 * User-specific NavigationEventInfo for user-related destinations.
 * This provides contextual information for predictive back gestures and navigation events.
 *
 * This class is owned by the feature-users module and should not be used by other features.
 */
data class UserNavigationEventInfo(
    val destinationType: UserDestinationType,
    val username: String? = null,
    val isDialog: Boolean = false
() : NavigationEventInfo() {

    enum class UserDestinationType {
        USER_LIST,
        USER_DETAIL,
        USER_SETTINGS_DIALOG
    }

    companion object {
        fun forUserList(): UserNavigationEventInfo = UserNavigationEventInfo(
            destinationType = UserDestinationType.USER_LIST
        )

        fun forUserDetail(username: String): UserNavigationEventInfo = UserNavigationEventInfo(
            destinationType = UserDestinationType.USER_DETAIL,
            username = username
        )

        fun forUserSettingsDialog(username: String): UserNavigationEventInfo = UserNavigationEventInfo(
            destinationType = UserDestinationType.USER_SETTINGS_DIALOG,
            username = username,
            isDialog = true
        )
    }
}
