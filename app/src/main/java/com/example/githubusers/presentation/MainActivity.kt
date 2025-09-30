package com.example.githubusers.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.scene.rememberSceneSetupNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.githubusers.navigation.api.LocalNavigateBack
import com.example.githubusers.navigation.api.LocalNavigateToDeepLink
import com.example.githubusers.navigation.api.NavigationTab
import com.example.githubusers.navigation.impl.DeepLinkDispatcher
import com.example.githubusers.navigation.impl.Navigation3FeatureRegistry
import com.example.githubusers.presentation.theme.GithubUsersTheme
import com.example.githubusers.presentation.navigation.navSavedStateConfiguration
import dagger.hilt.android.AndroidEntryPoint
import java.util.Collections
import javax.inject.Inject
import kotlin.jvm.JvmSuppressWildcards
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    @Suppress("LateinitUsage")
    lateinit var registry: Navigation3FeatureRegistry

    @Inject
    @Suppress("LateinitUsage")
    lateinit var dispatcher: DeepLinkDispatcher

    @Inject
    @Suppress("LateinitUsage")
    lateinit var navigationTabs: Set<@JvmSuppressWildcards NavigationTab>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MainContent()
        }
    }

    @Composable
    @Suppress("LongMethod")
    private fun MainContent() {
        GithubUsersTheme {
            val tabs = rememberTabs()
            val tabRoutesDescription = remember(tabs) { tabs.joinToString(separator = ",") { it.route } }
            Timber.tag("MainActivity").d(
                "Navigation tabs (recompose) count=%d routes=%s",
                tabs.size,
                tabRoutesDescription
            )
            LaunchedEffect(tabs) {
                Timber.tag("MainActivity").d(
                    "Navigation tabs (effect) count=%d routes=%s",
                    tabs.size,
                    tabRoutesDescription
                )
            }
            val defaultRoute = tabs.firstOrNull()?.route
                ?: throw IllegalStateException("No navigation tabs registered")
            val initialKey = rememberInitialKey(defaultRoute)

            val savedStateConfig = remember { navSavedStateConfiguration() }

            val backStack = rememberNavBackStack<NavKey>(
                savedStateConfig,
                initialKey
            )
            val routeToKey = rememberRouteToKey(tabs)

            val navigateToDeepLink = rememberNavigateToDeepLink(backStack)

            val selectedRoute = rememberSelectedRoute(routeToKey, backStack)

            val onTabSelected: (NavigationTab) -> Unit = remember(backStack, routeToKey) {
                { tab: NavigationTab ->
                    val key = routeToKey[tab.route]
                    if (key == null) {
                        Timber.tag("MainActivity").w("No NavKey registered for route %s", tab.route)
                    } else {
                        val existingIndex = backStack.indexOf(key)
                        if (existingIndex >= 0) {
                            var index = existingIndex
                            while (index < backStack.lastIndex) {
                                Collections.swap(backStack, index, index + 1)
                                index++
                            }
                        } else {
                            backStack.add(key)
                        }
                    }
                    Unit
                }
            }

            Scaffold(
                bottomBar = {
                    MainBottomBar(
                        tabs = tabs,
                        selectedRoute = selectedRoute,
                        onTabSelected = onTabSelected
                    )
                }
            ) { innerPadding ->
                CompositionLocalProvider(
                    LocalNavigateToDeepLink provides navigateToDeepLink,
                    LocalNavigateBack provides {
                        if (backStack.size > 1) {
                            backStack.removeAt(backStack.lastIndex)
                            true
                        } else {
                            false
                        }
                    }
                ) {
                    MainNavGraph(
                        backStack = backStack,
                        registry = registry,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    @Composable
    private fun rememberTabs(): List<NavigationTab> = remember(navigationTabs) {
        navigationTabs.sortedWith(compareBy<NavigationTab> { it.order }.thenBy { it.route })
    }

    @Composable
    private fun rememberInitialKey(defaultRoute: String): NavKey = remember(intent?.data, defaultRoute, dispatcher) {
        intent?.data?.let { dispatcher.toKey(it) }
            ?: dispatcher.toKey(defaultRoute)
            ?: throw IllegalStateException("No valid initial destination found")
    }

    @Composable
    private fun rememberRouteToKey(tabs: List<NavigationTab>): Map<String, NavKey> = remember(dispatcher, tabs) {
        buildMap {
            tabs.forEach { tab ->
                dispatcher.toKey(tab.route)?.let { put(tab.route, it) }
            }
        }
    }

    @Composable
    private fun rememberNavigateToDeepLink(backStack: NavBackStack<NavKey>): (String) -> Unit =
        remember(backStack, dispatcher) {
            { route ->
                val key = dispatcher.toKey(route)
                if (key == null) {
                    Timber.tag("MainActivity").w("Deep link not recognized: %s", route)
                } else {
                    val existingIndex = backStack.indexOf(key)
                    if (existingIndex >= 0) {
                        while (backStack.size > existingIndex + 1) {
                            backStack.removeAt(backStack.lastIndex)
                        }
                    } else {
                        backStack.add(key)
                    }
                }
            }
        }

    @Composable
    private fun rememberSelectedRoute(routeToKey: Map<String, NavKey>, backStack: NavBackStack<NavKey>): String? {
        val keyToRoute = remember(routeToKey) {
            routeToKey.entries.associate { (route, key) -> key to route }
        }
        val selectedRoute by remember(backStack, keyToRoute) {
            derivedStateOf {
                backStack.asReversed().firstNotNullOfOrNull { key -> keyToRoute[key] }
                    ?: keyToRoute.entries.firstOrNull { (key, _) -> backStack.contains(key) }?.value
            }
        }
        return selectedRoute
    }
}

@Composable
private fun MainBottomBar(tabs: List<NavigationTab>, selectedRoute: String?, onTabSelected: (NavigationTab) -> Unit) {
    NavigationBar {
        tabs.forEach { tab ->
            val iconImage = tab.selectedIcon?.takeIf { selectedRoute == tab.route } ?: tab.icon
            NavigationBarItem(
                icon = { Icon(imageVector = iconImage, contentDescription = tab.label) },
                label = { Text(tab.label) },
                selected = selectedRoute == tab.route,
                onClick = { onTabSelected(tab) }
            )
        }
    }
}

@Composable
fun MainNavGraph(backStack: NavBackStack<NavKey>, registry: Navigation3FeatureRegistry, modifier: Modifier = Modifier) {
    val entryProvider = remember(registry) { registry.createEntryProvider() }

    NavDisplay(
        backStack = backStack,
        onBack = { keysToRemove ->
            val pops = keysToRemove.coerceAtMost(backStack.size - 1).coerceAtLeast(0)
            repeat(pops) {
                backStack.removeAt(backStack.lastIndex)
            }
        },
        entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        modifier = modifier
    ) { key ->
        entryProvider(key)
    }
}
