package com.example.githubusers.feature.users.detail.presentation.navigation

/**
 * Navigator interface for the User Detail feature.
 * Provides navigation actions from the user detail screen.
 */
interface UserDetailNavigator {
    /**
     * Navigate back from the current screen.
     */
    fun navigateBack()

    /**
     * Navigate to repository detail.
     *
     * @param owner Repository owner username
     * @param repo Repository name
     */
    fun navigateToRepository(
        owner: String,
        repo: String,
    )

    /**
     * Open URL in external browser.
     *
     * @param url URL to open
     */
    fun openUrl(url: String)
}
