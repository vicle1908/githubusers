package com.example.githubusers.presentation.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.githubusers.feature.search.presentation.navigation.SearchNavigator
import com.example.githubusers.feature.search.presentation.navigation.SearchRoute
import com.example.githubusers.feature.users.list.presentation.navigation.UserListNavigator
import com.example.githubusers.feature.users.list.presentation.navigation.UserListRoute
import com.example.githubusers.navigation.api.AppDeepLinks
import com.example.githubusers.navigation.api.AppDestination
import com.example.githubusers.navigation.api.CoreNavigationDestination
import com.example.githubusers.navigation.api.Navigation3Controller
import com.example.githubusers.navigation.api.Navigation3Entry
import com.example.githubusers.presentation.ui.userdetail.UserDetailScreen
import com.example.githubusers.presentation.ui.userdetail.UserDetailViewModel

/**
 * Central router that maps Navigation3Entry to feature screens.
 * Prefers typed AppDestination by parsing entry.deepLink. Falls back to core destinations or a simple text.
 */
@Composable
fun AppRouter(
    controller: Navigation3Controller,
    entry: Navigation3Entry,
    modifier: Modifier = Modifier,
) {
    val typed: AppDestination? = AppDeepLinks.parse(entry.deepLink)
    when (typed) {
        is AppDestination.UserList -> {
            UserListRoute(
                navigator =
                    object : UserListNavigator {
                        override fun navigateToUserDetail(username: String) {
                            controller.navigate(AppDestination.UserDetail(username))
                        }

                        override fun navigateBack() {
                            controller.navigateBack()
                        }
                    },
            )
        }
        is AppDestination.UserDetail -> {
            val vm: UserDetailViewModel = hiltViewModel()
            UserDetailScreen(
                username = typed.username,
                viewModel = vm,
            )
        }
        is AppDestination.Search -> {
            SearchRoute(
                navigator =
                    object : SearchNavigator {
                        override fun navigateToUserDetail(username: String) {
                            controller.navigate(AppDestination.UserDetail(username))
                        }

                        override fun navigateBack() {
                            controller.navigateBack()
                        }
                    },
            )
        }
        is AppDestination.Settings -> {
            Text("Settings", style = MaterialTheme.typography.headlineSmall)
        }
        null -> {
            when (val dest = entry.destination) {
                is CoreNavigationDestination.Error -> Text("Error: ${dest.message}")
                is CoreNavigationDestination.Settings -> Text("Settings", style = MaterialTheme.typography.headlineSmall)
                else -> Text("Screen: ${entry.destination.route}")
            }
        }
    }
}
