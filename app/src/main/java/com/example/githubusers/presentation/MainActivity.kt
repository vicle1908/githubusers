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
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.example.githubusers.di.NavigationConstants
import com.example.githubusers.navigation.api.LocalNavigateBack
import com.example.githubusers.navigation.api.LocalNavigateToDeepLink
import com.example.githubusers.navigation.impl.DeepLinkDispatcher
import com.example.githubusers.navigation.impl.Navigation3FeatureRegistry
import com.example.githubusers.performance.StartupPerformanceTracker
import com.example.githubusers.presentation.navigation.deeplink.ModuleNavigator
import com.example.githubusers.presentation.theme.GithubUsersTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var registry: Navigation3FeatureRegistry

    @Inject
    lateinit var dispatcher: DeepLinkDispatcher

    @Inject
    lateinit var startupPerformanceTracker: StartupPerformanceTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startupPerformanceTracker.markActivityCreated()
        enableEdgeToEdge()

        setContent {
            GithubUsersTheme {
                // Initialize back stack from incoming deep link if present; otherwise push start destination
                val initialKey = intent?.data?.let { dispatcher.toKey(it) }
                    ?: dispatcher.toKey(NavigationConstants.START_DESTINATION)
                    ?: throw IllegalStateException("No valid initial destination found")
                
                // Debug logging
                android.util.Log.d("MainActivity", "Initial key: $initialKey")
                android.util.Log.d("MainActivity", "Start destination: ${NavigationConstants.START_DESTINATION}")

                val backStack = rememberNavBackStack(initialKey)

                CompositionLocalProvider(
                    LocalNavigateToDeepLink provides { deepLink ->
                        val navigator = ModuleNavigator(dispatcher)
                        navigator.navigateTo(backStack, deepLink)
                    },
                    LocalNavigateBack provides {
                        backStack.removeLastOrNull() != null
                    }
                ) {
                    MainNavGraph(
                        backStack = backStack,
                        registry = registry,
                        dispatcher = dispatcher,
                        startupPerformanceTracker = startupPerformanceTracker,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Composable
fun MainNavGraph(
    backStack: androidx.navigation3.runtime.NavBackStack<NavKey>,
    registry: Navigation3FeatureRegistry,
    dispatcher: DeepLinkDispatcher,
    modifier: Modifier = Modifier,
    startupPerformanceTracker: StartupPerformanceTracker? = null
) {
    // Track first frame rendered and time-to-interactive
    LaunchedEffect(backStack) {
        android.util.Log.d("MainNavGraph", "LaunchedEffect called with backStack: $backStack")
        startupPerformanceTracker?.markFirstFrameRendered()
        startupPerformanceTracker?.markTimeToInteractive()
    }

    android.util.Log.d("MainNavGraph", "About to call NavDisplay with backStack: $backStack")
    android.util.Log.d("MainNavGraph", "BackStack size: ${backStack.size}")
    NavDisplay(
        backStack = backStack,
        onBack = { keysToRemove ->
            android.util.Log.d("MainNavGraph", "onBack called with keysToRemove: $keysToRemove")
            repeat(keysToRemove) {
                backStack.removeLastOrNull()
            }
        },
        entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        modifier = modifier
    ) { key ->
        // Feature-owned entry provider - delegates to registry
        android.util.Log.d("MainNavGraph", "EntryProvider called with key: $key")
        // Use the registry's entry provider function
        registry.createEntryProvider()(key)
    }
}
