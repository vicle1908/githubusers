package com.example.githubusers.feature.users.navigation

import com.example.githubusers.navigation.api.NavCommand

/**
 * Feature API for the Users module that provides type-safe navigation methods.
 *
 * This interface enables other modules to navigate to user screens without
 * directly depending on the users feature module's internal destination types.
 *
 * Example usage:
 * ```kotlin
 * // In another feature module
 * @Inject
 * lateinit var userFeatureApi: UserFeatureApi
 *
 * // Navigate to user list
 * navigationController.navigate(userFeatureApi.navigateToUserList())
 *
 * // Navigate to user detail
 * navigationController.navigate(userFeatureApi.navigateToUserDetail("octocat"))
 * ```
 */
interface UserFeatureApi {
    /**
     * Creates a navigation command to navigate to the user list screen.
     *
     * @param filter Optional filter parameter for the user list
     * @return NavCommand for navigating to user list
     */
    fun navigateToUserList(filter: String? = null): NavCommand

    /**
     * Creates a navigation command to navigate to a specific user's detail screen.
     *
     * @param username The username of the user to display
     * @return NavCommand for navigating to user detail
     */
    fun navigateToUserDetail(username: String): NavCommand

    /**
     * Creates a navigation command to navigate to the user search screen.
     *
     * @param query Optional search query to pre-populate
     * @return NavCommand for navigating to user search
     */
    fun navigateToUserSearch(query: String = ""): NavCommand
}
