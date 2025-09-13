package com.example.githubusers.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.example.githubusers.navigation.api.LocalNavigateBack
import com.example.githubusers.navigation.api.LocalNavigateToDeepLink
import com.example.githubusers.navigation.impl.DeepLinkDispatcher
import com.example.githubusers.navigation.impl.Navigation3FeatureRegistry
import com.example.githubusers.presentation.theme.GithubUsersTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var registry: Navigation3FeatureRegistry

    @Inject
    lateinit var dispatcher: DeepLinkDispatcher

    // Removed StartupPerformanceTracker injection - using Firebase Performance Monitoring instead

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Firebase Performance Monitoring automatically tracks activity creation
        enableEdgeToEdge()

        setContent {
            GithubUsersTheme {
                // Initialize back stack from incoming deep link if present; otherwise push start destination
                // Using direct deep link instead of centralized constants for proper feature ownership
                val initialKey = intent?.data?.let { dispatcher.toKey(it) }
                    ?: dispatcher.toKey("app://users/list") // Direct deep link - feature-owned
                    ?: throw IllegalStateException("No valid initial destination found")

                // Debug logging
                Timber.tag("MainActivity").d("Initial key: $initialKey")
                Timber.tag("MainActivity").d("Start destination: app://users/list")

                val backStack = rememberNavBackStack(initialKey)

                CompositionLocalProvider(
                    LocalNavigateToDeepLink provides { deepLink ->
                        // Use DeepLinkDispatcher directly instead of ModuleNavigator wrapper
                        val key = dispatcher.toKey(deepLink)
                        if (key != null) {
                            backStack.add(key)
                        }
                    },
                    LocalNavigateBack provides {
                        backStack.removeLastOrNull() != null
                    }
                ) {
                    MainNavGraph(
                        backStack = backStack,
                        registry = registry,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Composable
fun MainNavGraph(
    backStack: NavBackStack<NavKey>,
    registry: Navigation3FeatureRegistry,
    modifier: Modifier = Modifier
) {
    // Firebase Performance Monitoring automatically tracks first frame and time-to-interactive
    LaunchedEffect(backStack) {
        Timber.tag("MainNavGraph").d("LaunchedEffect called with backStack: $backStack")
    }

    Timber.tag("MainNavGraph").d("About to call NavDisplay with backStack: $backStack")
    Timber.tag("MainNavGraph").d("BackStack size: ${backStack.size}")
    NavDisplay(
        backStack = backStack,
        onBack = { keysToRemove ->
            Timber.tag("MainNavGraph").d("onBack called with keysToRemove: $keysToRemove")
            repeat(keysToRemove) {
                backStack.removeLastOrNull()
            }
        },
        entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        modifier = modifier
    ) { key ->
        // Feature-owned entry provider - delegates to registry
        Timber.tag("MainNavGraph").d("EntryProvider called with key: $key")
        // Use the registry's entry provider function
        registry.createEntryProvider()(key)
    }
}
