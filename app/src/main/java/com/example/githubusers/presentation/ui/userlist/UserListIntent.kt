package com.example.githubusers.presentation.ui.userlist

import com.example.githubusers.domain.entity.User

/**
 * MVI Intent for User List screen actions.
 * Represents all possible user interactions with the screen.
 */
sealed class UserListIntent {
    // Search query management
    data class UpdateSearchQuery(
        val query: String,
    ) : UserListIntent()

    data class ExecuteSearch(
        val query: String,
    ) : UserListIntent()

    object ClearSearch : UserListIntent()

    // Search mode management
    object ActivateSearch : UserListIntent() // User wants to start searching

    object DismissSearch : UserListIntent() // User wants to exit search (via X, Escape, Back)

    // User list management
    object RefreshUsers : UserListIntent()

    data class UserClicked(
        val user: User,
    ) : UserListIntent()

    // UI interactions
    object SearchBarClicked : UserListIntent() // User clicked on collapsed search bar

    object SearchIconClicked : UserListIntent() // User clicked search icon

    // Legacy - to be removed after migration
    @Deprecated("Use ActivateSearch or DismissSearch instead")
    data class SetSearchMode(
        val enabled: Boolean,
    ) : UserListIntent()
}
