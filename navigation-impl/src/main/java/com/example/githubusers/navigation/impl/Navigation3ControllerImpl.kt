package com.example.githubusers.navigation.impl

import com.example.githubusers.navigation.api.Navigation3Controller
import com.example.githubusers.navigation.api.Navigation3Entry
import com.example.githubusers.navigation.api.NavigationDestination
import com.example.githubusers.navigation.api.NavigationOptions
import com.example.githubusers.navigation.impl.guard.ReadOnlyNavGate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject

/**
 * Default implementation of Navigation3Controller.
 * Manages navigation state and deep link routing.
 */
class Navigation3ControllerImpl
    @Inject
    constructor(
        private val destinationResolver: DestinationResolver,
        private val backStackStore: BackStackStore? = null,
        private val persistenceConfig: PersistenceConfig = PersistenceConfig(),
        private val metrics: NavigationMetrics = NoOpNavigationMetrics,
        private val navGate: ReadOnlyNavGate = ReadOnlyNavGate(false),
        private val telemetry: NavigationTelemetry = NoOpNavigationTelemetry,
    ) : Navigation3Controller {
        private val _currentEntry = MutableStateFlow<Navigation3Entry?>(null)
        override val currentEntry: StateFlow<Navigation3Entry?> = _currentEntry.asStateFlow()

        private val _backStack = MutableStateFlow<List<Navigation3Entry>>(emptyList())
        override val backStack: StateFlow<List<Navigation3Entry>> = _backStack.asStateFlow()

        override fun navigate(deepLink: String) {
            navigate(deepLink, NavigationOptions())
        }

        override fun navigate(destination: com.example.githubusers.navigation.api.AppDestination) {
            val deepLink =
                com.example.githubusers.navigation.api.AppDeepLinks
                    .build(destination)
            navigate(deepLink, NavigationOptions())
        }

        override fun navigate(
            destination: com.example.githubusers.navigation.api.AppDestination,
            options: NavigationOptions,
        ) {
            val deepLink =
                com.example.githubusers.navigation.api.AppDeepLinks
                    .build(destination)
            navigate(deepLink, options)
        }

        override fun navigate(
            deepLink: String,
            options: NavigationOptions,
        ) {
            if (!navGate.allowMutation()) {
                telemetry.onGateBlocked("navigate:$deepLink")
                return
            }
            val destination = destinationResolver.resolve(deepLink) ?: return
            val entry = createEntry(destination, deepLink)

            val popUpTo = options.popUpTo
            when {
                popUpTo != null -> {
                    handlePopUpTo(popUpTo, options.popUpToInclusive)
                }
                options.launchSingleTop -> {
                    handleSingleTop(entry)
                }
                else -> {
                    addToBackStack(entry)
                }
            }

            _currentEntry.value = entry
            telemetry.onNavigate(deepLink)
            persistIfEnabled()
        }

        override fun navigateBack(): Boolean {
            if (!navGate.allowMutation()) {
                telemetry.onGateBlocked("navigateBack")
                telemetry.onNavigateBack(false)
                return false
            }
            val stack = _backStack.value
            val result =
                if (stack.size > 1) {
                    val newStack = stack.dropLast(1)
                    _backStack.value = newStack
                    _currentEntry.value = newStack.lastOrNull()
                    persistIfEnabled()
                    true
                } else {
                    false
                }
            telemetry.onNavigateBack(result)
            return result
        }

        override fun navigateUp(): Boolean {
            // For simplicity, navigateUp behaves like navigateBack
            // In a full implementation, this would respect parent-child relationships
            val result = navigateBack()
            telemetry.onNavigateUp(result)
            return result
        }

        override fun popBackStackTo(
            deepLink: String,
            inclusive: Boolean,
        ): Boolean {
            if (!navGate.allowMutation()) {
                telemetry.onGateBlocked("popBackStackTo:$deepLink:$inclusive")
                telemetry.onPopBackStackTo(deepLink, inclusive, false)
                return false
            }
            val stack = _backStack.value
            val index = stack.indexOfLast { it.deepLink == deepLink }

            val result =
                if (index >= 0) {
                    val endIndex = if (inclusive) index else index + 1
                    val newStack = stack.take(endIndex)
                    _backStack.value = newStack
                    _currentEntry.value = newStack.lastOrNull()
                    persistIfEnabled()
                    true
                } else {
                    false
                }
            telemetry.onPopBackStackTo(deepLink, inclusive, result)
            return result
        }

        override fun clearBackStack() {
            if (!navGate.allowMutation()) {
                telemetry.onGateBlocked("clearBackStack")
                return
            }
            _backStack.value = emptyList()
            _currentEntry.value = null
            telemetry.onClearBackStack()
            persistIfEnabled()
        }

        override fun handleDeepLink(deepLink: String): Boolean {
            telemetry.onHandleDeepLinkAttempt(deepLink)
            if (!navGate.allowMutation()) {
                telemetry.onGateBlocked("handleDeepLink:$deepLink")
                telemetry.onHandleDeepLinkResult(deepLink, false)
                return false
            }
            val destination = destinationResolver.resolve(deepLink)
            val success =
                if (destination != null) {
                    navigate(deepLink)
                    true
                } else {
                    false
                }
            telemetry.onHandleDeepLinkResult(deepLink, success)
            return success
        }

        override fun restoreFromPersistence(): Boolean {
            if (!persistenceConfig.enabled) return false
            val store = backStackStore ?: return false
            val persisted =
                store.load() ?: run {
                    metrics.onPersistReadFailure("empty")
                    return false
                }
            val now = System.currentTimeMillis()
            if (now - persisted.timestamp > persistenceConfig.ttlMillis) {
                store.clear()
                metrics.onPersistReadFailure("ttl_expired")
                return false
            }
            if (persisted.schemaVersion != persistenceConfig.schemaVersion) {
                store.clear()
                metrics.onPersistReadFailure("schema_mismatch")
                return false
            }
            if (persisted.navGraphVersion != persistenceConfig.navGraphVersion) {
                store.clear()
                metrics.onPersistReadFailure("navgraph_mismatch")
                return false
            }
            val appVersion = persistenceConfig.appVersionProvider.invoke()
            if (appVersion != null && persisted.appVersion != null && appVersion != persisted.appVersion) {
                // On app version change, prefer dropping to avoid restore loops.
                store.clear()
                metrics.onPersistReadFailure("app_version_mismatch")
                return false
            }
            // Rebuild back stack from deep links
            val rebuilt = mutableListOf<Navigation3Entry>()
            for (link in persisted.entries) {
                val dest = destinationResolver.resolve(link) ?: continue
                rebuilt.add(createEntry(dest, link))
            }
            if (rebuilt.isEmpty()) {
                store.clear()
                metrics.onPersistReadFailure("no_resolvable_links")
                return false
            }
            _backStack.value = rebuilt
            _currentEntry.value = rebuilt.lastOrNull()
            metrics.onPersistReadSuccess(rebuilt.size)
            return true
        }

        private fun createEntry(
            destination: NavigationDestination,
            deepLink: String,
        ): Navigation3Entry =
            Navigation3Entry(
                id = UUID.randomUUID().toString(),
                destination = destination,
                deepLink = deepLink,
                arguments = extractArguments(deepLink),
            )

        private fun addToBackStack(entry: Navigation3Entry) {
            _backStack.value = _backStack.value + entry
        }

        private fun persistIfEnabled() {
            if (!persistenceConfig.enabled) return
            val store = backStackStore ?: return
            val links = _backStack.value.takeLast(persistenceConfig.maxEntries).map { it.deepLink }
            val total = links.sumOf { it.length }
            if (total > persistenceConfig.maxTotalEntriesLength) {
                // Trim aggressively
                val trimmed = links.takeLast(1)
                try {
                    store.save(
                        PersistedBackStack(
                            schemaVersion = persistenceConfig.schemaVersion,
                            timestamp = System.currentTimeMillis(),
                            appVersion = persistenceConfig.appVersionProvider.invoke(),
                            navGraphVersion = persistenceConfig.navGraphVersion,
                            entries = trimmed,
                        ),
                    )
                    metrics.onPersistWriteSuccess(trimmed.size, trimmed.sumOf { it.length })
                } catch (t: Throwable) {
                    metrics.onPersistWriteFailure(t)
                }
                return
            }
            try {
                store.save(
                    PersistedBackStack(
                        schemaVersion = persistenceConfig.schemaVersion,
                        timestamp = System.currentTimeMillis(),
                        appVersion = persistenceConfig.appVersionProvider.invoke(),
                        navGraphVersion = persistenceConfig.navGraphVersion,
                        entries = links,
                    ),
                )
                metrics.onPersistWriteSuccess(links.size, links.sumOf { it.length })
            } catch (t: Throwable) {
                metrics.onPersistWriteFailure(t)
            }
        }

        private fun handleSingleTop(entry: Navigation3Entry) {
            val stack = _backStack.value
            if (stack.lastOrNull()?.deepLink != entry.deepLink) {
                addToBackStack(entry)
            }
        }

        private fun handlePopUpTo(
            popUpTo: String,
            inclusive: Boolean,
        ) {
            popBackStackTo(popUpTo, inclusive)
        }

        private fun extractArguments(deepLink: String): Map<String, Any> {
            // Simple argument extraction from deep link query parameters
            val uri = android.net.Uri.parse(deepLink)
            return uri.queryParameterNames.associateWith { name ->
                uri.getQueryParameter(name) ?: ""
            }
        }
    }

/**
 * Interface for resolving deep links to destinations
 */
interface DestinationResolver {
    fun resolve(deepLink: String): NavigationDestination?
}
