package com.example.githubusers.feature.users.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.example.githubusers.feature.users.detail.presentation.navigation.UserDetailNavigator
import com.example.githubusers.feature.users.detail.presentation.navigation.UserDetailRoute
import com.example.githubusers.feature.users.list.presentation.navigation.UserListNavigator
import com.example.githubusers.feature.users.list.presentation.navigation.UserListRoute
import com.example.githubusers.feature.users.navigation.UserNavKey
import com.example.githubusers.navigation.api.FeatureDestinationProvider
import com.example.githubusers.navigation.api.LocalNavigateBack
import com.example.githubusers.navigation.api.LocalNavigateToDeepLink
import javax.inject.Inject
import javax.inject.Singleton

/** Feature-owned destinations for Users. */
@Singleton
class UsersFeatureDestinationProvider @Inject constructor() : FeatureDestinationProvider {
    override fun canResolve(key: NavKey): Boolean = key is UserNavKey.UserList || key is UserNavKey.UserDetail

    override fun createEntry(key: NavKey): NavEntry<NavKey> = NavEntry(key) {
        when (key) {
            is UserNavKey.UserList -> listContent()
            is UserNavKey.UserDetail -> detailContent(key.username)
            else -> errorContent(key)
        }
    }

    @Composable
    private fun listContent() {
        // Pull host-provided deep link navigator from CompositionLocal
        val navigateToDeepLink = LocalNavigateToDeepLink.current
        UserListRoute(
            navigator =
            object : UserListNavigator {
                override fun navigateToUserDetail(username: String) {
                    navigateToDeepLink("app://users/user/$username")
                }

                override fun navigateBack() {
                    navigateToDeepLink("app://users/list")
                }

                override fun openSettings() {
                    navigateToDeepLink("app://settings")
                }
            },
        )
    }

    @Composable
    private fun detailContent(username: String) {
        val navigateBack = LocalNavigateBack.current
        UserDetailRoute(
            username = username,
            navigator =
            object : UserDetailNavigator {
                override fun navigateBack() {
                    navigateBack()
                }

                override fun navigateToRepository(
                    owner: String,
                    repo: String,
                ) { /* TODO: deeplink to repo screen when available */ }

                override fun openUrl(url: String) { /* TODO: external browser via Intent if needed */ }
            },
        )
    }

    @Composable
    private fun errorContent(key: NavKey) {
        Text(text = "Unknown users key: $key")
    }
}
