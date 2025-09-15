package com.example.githubusers.feature.users.navigation

import com.example.githubusers.core.ui.navigation.PredictiveBackManager
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.example.githubusers.feature.users.detail.presentation.navigation.UserDetailNavigator
import com.example.githubusers.feature.users.detail.presentation.navigation.UserDetailRoute
import com.example.githubusers.feature.users.list.presentation.navigation.UserListNavigator
import com.example.githubusers.feature.users.list.presentation.navigation.UserListRoute
import com.example.githubusers.feature.users.navigation.UserNavKey
import com.example.githubusers.feature.users.navigation.UserNavigationEventInfo
import com.example.githubusers.navigation.api.FeatureDestinationProvider
import com.example.githubusers.navigation.api.LocalNavigateBack
import com.example.githubusers.navigation.api.LocalNavigateToDeepLink
import javax.inject.Inject
import javax.inject.Singleton

/** Feature-owned destinations for Users. */
@Singleton
class UsersFeatureDestinationProvider @Inject constructor() : FeatureDestinationProvider {
    override fun canResolve(key: NavKey): Boolean = key is UserNavKey.UserList || key is UserNavKey.UserDetail || key is UserNavKey.UserSettingsDialog

    override fun createEntry(key: NavKey, metadata: Map<String, Any>): NavEntry<NavKey> = NavEntry(key, metadata = metadata) {
        when (key) {
            is UserNavKey.UserList -> UserListContent()
            is UserNavKey.UserDetail -> UserDetailContent(key.username)
            is UserNavKey.UserSettingsDialog -> UserSettingsDialogContent(key.username)
            else -> UnknownUsersKeyContent(key)
        }
    }

    @Composable
    private fun UserListContent() {
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
    private fun UserDetailContent(username: String) {
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
                ) {
                    // Repository navigation not implemented yet
                    // Will be implemented when repository feature is added
                }

                override fun openUrl(url: String) {
                    // External URL opening not implemented yet
                    // Will be implemented when external browser integration is needed
                }
            },
        )
    }

    @Composable
    private fun UserSettingsDialogContent(username: String) {
        // Handle back press for dialog dismissal with proper priority
        PredictiveBackManager.Patterns.dialogDismissal(
            enabled = true,
            onDismiss = {
                // Dialog will be dismissed by Navigation 3's dialog handling
                // This callback provides additional control if needed
            }
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "User Settings Dialog",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Username: $username",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    private fun UnknownUsersKeyContent(key: NavKey) {
        Text(text = "Unknown users key: $key")
    }

    /**
     * Provides custom transition specifications for different user destinations.
     * This demonstrates how feature modules can define their own transition animations.
     */
    override fun getTransitionSpec(key: NavKey): (AnimatedContentTransitionScope<*>.() -> ContentTransform)? {
        return when (key) {
            is UserNavKey.UserList -> {
                // Simple fade transition for user list
                {
                    ContentTransform(
                        fadeIn(animationSpec = tween(300)),
                        fadeOut(animationSpec = tween(300))
                    )
                }
            }
            is UserNavKey.UserDetail -> {
                // Slide transition for user detail (more engaging)
                {
                    ContentTransform(
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = spring(dampingRatio = 0.8f, stiffness = 1000f)
                        ) + fadeIn(animationSpec = tween(300)),
                        slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = spring(dampingRatio = 0.8f, stiffness = 1000f)
                        ) + fadeOut(animationSpec = tween(300))
                    )
                }
            }
            else -> null // Use default transition
        }
    }

    /**
     * Provides custom pop transition specifications for navigation back.
     */
    override fun getPopTransitionSpec(key: NavKey): (AnimatedContentTransitionScope<*>.() -> ContentTransform)? {
        return when (key) {
            is UserNavKey.UserDetail -> {
                // Reverse slide transition when going back from user detail
                {
                    ContentTransform(
                        slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = spring(dampingRatio = 0.8f, stiffness = 1000f)
                        ) + fadeIn(animationSpec = tween(300)),
                        slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = spring(dampingRatio = 0.8f, stiffness = 1000f)
                        ) + fadeOut(animationSpec = tween(300))
                    )
                }
            }
            else -> null // Use default pop transition
        }
    }

    /**
     * Provides dialog properties for dialog destinations.
     * UserSettingsDialog will be displayed as a dialog overlay.
     */
    override fun getDialogProperties(key: NavKey): DialogProperties? {
        return when (key) {
            is UserNavKey.UserSettingsDialog -> {
                DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                    usePlatformDefaultWidth = false
                )
            }
            else -> null // Not a dialog destination
        }
    }

    /**
     * Determines if destinations should be part of a list-detail layout.
     * UserList and UserDetail can be displayed in a list-detail layout on larger screens.
     */
    override fun isListDetailDestination(key: NavKey): Boolean {
        return when (key) {
            is UserNavKey.UserList, is UserNavKey.UserDetail -> true
            else -> false
        }
    }

    /**
     * Determines if destinations should be part of a supporting pane layout.
     * Currently not used for user destinations.
     */
    override fun isSupportingPaneDestination(key: NavKey): Boolean = false

    /**
     * Provides custom NavigationEventInfo for advanced gesture handling.
     * This enables predictive back gestures and other navigation events for user destinations.
     */
    override fun getNavigationEventInfo(key: NavKey): UserNavigationEventInfo? {
        return when (key) {
            is UserNavKey.UserList -> UserNavigationEventInfo.forUserList()
            is UserNavKey.UserDetail -> UserNavigationEventInfo.forUserDetail(key.username)
            is UserNavKey.UserSettingsDialog -> UserNavigationEventInfo.forUserSettingsDialog(key.username)
            else -> null
        }
    }

    /**
     * Determines if destinations support predictive back gestures.
     * UserDetail and UserSettingsDialog support predictive back gestures for enhanced UX.
     */
    override fun supportsPredictiveBack(key: NavKey): Boolean {
        return when (key) {
            is UserNavKey.UserDetail, is UserNavKey.UserSettingsDialog -> true
            else -> false
        }
    }

    /**
     * Provides custom predictive pop transition specification for predictive back gestures.
     * This allows customization of the transition animation for predictive back gestures.
     * Enhanced animations are provided to improve user experience during predictive back navigation.
     */
    override fun getPredictivePopTransitionSpec(
        key: NavKey
    ): (AnimatedContentTransitionScope<*>.() -> ContentTransform)? {
        return when (key) {
            is UserNavKey.UserDetail -> {
                // Custom transition for user detail with enhanced spring animation
                {
                    slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f)
                    ) + fadeIn(animationSpec = tween(300)) togetherWith
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f)
                    ) + fadeOut(animationSpec = tween(300))
                }
            }
            is UserNavKey.UserSettingsDialog -> {
                // Custom transition for dialog with smooth fade and slide
                {
                    slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = spring(dampingRatio = 0.9f, stiffness = 400f)
                    ) + fadeIn(animationSpec = tween(250)) togetherWith
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = spring(dampingRatio = 0.9f, stiffness = 400f)
                    ) + fadeOut(animationSpec = tween(250))
                }
            }
            else -> null
        }
    }

}
