package com.example.githubusers.feature.users.list.presentation.navigation

/**
 * Navigator interface for the User List feature.
 * Provides navigation actions from the user list screen.
 */
interface UserListNavigator {
    /**
     * Navigate to user detail screen.
     *
     * @param username The username of the user to show details for
     */
    fun navigateToUserDetail(username: String)

    /**
     * Navigate back from the current screen.
     */
    fun navigateBack()

    /**
     * Navigate to the settings screen.
     */
    fun openSettings()
}
