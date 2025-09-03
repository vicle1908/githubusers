# Navigation: Current Implementation vs Navigation 3 Typed (Comparison and Migration Plan)

Status: initial draft
Owner: Android Platform
Scope: Compose-only app with feature-based modules

Overview
- Goal: converge on Navigation 3 typed architecture (deep-link–driven, type-safe destinations) and deprecate legacy string-based navigation-compose usage in app UI.
- Current state: Navigation 3 typed stack is already implemented in navigation-api and navigation-impl and wired via DI, but MainActivity still uses Navigation Compose NavHost/composable with string routes.
- Outcome: a practical, low-risk migration plan to replace the NavHost with Navigation3Host and adopt typed destinations end-to-end.

Current implementation (what’s in this repo today)
- UI graph entry uses Navigation Compose with string routes and rememberNavController().

```kotlin path=/Users/vinhlekhanh/Downloads/project/company/times/githubusers/app/src/main/java/com/example/githubusers/presentation/MainActivity.kt start=97
NavHost(
    navController = navController,
    startDestination = "userList",
    modifier = Modifier.fillMaxSize().padding(padding),
) {
    composable("userList") {
        val viewModel: UserListViewModel = hiltViewModel()
        val state by viewModel.state.collectAsState()

        // Navigate when a user is selected
        LaunchedEffect(state.selectedUser) {
            state.selectedUser?.let { user ->
                navController.navigate("userDetail/${user.login}")
            }
        }

        UserListScreen(
            viewModel = viewModel,
        )
    }
    composable("userDetail/{username}") { backStackEntry ->
        val viewModel: UserDetailViewModel = hiltViewModel()
        val username = backStackEntry.arguments?.getString("username")
        if (username == null) {
            navController.navigateUp()
            return@composable
        }
        val uiState by viewModel.uiState.collectAsState()
        val repositoriesFlow = viewModel.repositoriesFlow.collectAsLazyPagingItems()

        UserDetailScreen(
            uiState = uiState,
            repositoriesFlow = repositoriesFlow,
            onIntent = { intent ->
                viewModel.onIntent(intent)
            },
            onBackClick = {
                navController.navigateUp()
            },
        )
    }
}
```

- Typed Navigation 3 API already exists and is wired for deep link ownership and back stack persistence.

```kotlin path=/Users/vinhlekhanh/Downloads/project/company/times/githubusers/navigation-api/src/main/java/com/example/githubusers/navigation/api/AppDestination.kt start=1
package com.example.githubusers.navigation.api

import kotlinx.serialization.Serializable

/**
 * Project-wide typed destinations for Navigation 3.
 * Keep this in navigation-api so features can reference types without app dependency.
 */
sealed interface AppDestination {
    @Serializable
    data object UserList : AppDestination

    @Serializable
    data class UserDetail(
        val username: String,
    ) : AppDestination

    @Serializable
    data class Search(
        val query: String? = null,
    ) : AppDestination

    @Serializable
    data object Settings : AppDestination
}
```

```kotlin path=/Users/vinhlekhanh/Downloads/project/company/times/githubusers/navigation-impl/src/main/java/com/example/githubusers/navigation/impl/Navigation3Host.kt start=18
@Composable
fun Navigation3Host(
    controller: Navigation3Controller,
    startDestination: String,
    modifier: Modifier = Modifier,
    canInterceptBack: (Navigation3Entry) -> Boolean = { false },
    onInterceptBack: (Navigation3Entry) -> Unit = {},
    content: @Composable (Navigation3Entry) -> Unit,
) {
    val currentEntry by controller.currentEntry.collectAsState()

    // Initialize with persistence restore, then navigate to start if still empty
    LaunchedEffect(Unit) {
        val restored = controller.restoreFromPersistence()
        if (!restored && currentEntry == null) {
            controller.navigate(startDestination)
        }
    }

    // Handle back gestures at top-of-stack only
    currentEntry?.let { entry ->
        BackHandler(true) {
            if (canInterceptBack(entry)) {
                onInterceptBack(entry)
            } else {
                controller.navigateBack()
            }
        }
        // Display current destination
        content(entry)
    }
}
```

```kotlin path=/Users/vinhlekhanh/Downloads/project/company/times/githubusers/navigation-impl/src/main/java/com/example/githubusers/navigation/impl/DefaultDestinationResolver.kt start=21
override fun resolve(deepLink: String): NavigationDestination? {
    // Normalize before resolution
    val normalizedUri = UriNormalizer.normalize(Uri.parse(deepLink))
    val uri = normalizedUri
    val normalized = normalizedUri.toString()

    telemetry.onResolveAttempt(normalized)

    // Try core destinations first
    when {
        normalized == "app://home" -> return CoreNavigationDestination.Home
        normalized == "app://settings" -> return CoreNavigationDestination.Settings
        normalized.startsWith("app://error") -> {
            val message = uri.getQueryParameter("message") ?: "Unknown error"
            val code = uri.getQueryParameter("code")?.toIntOrNull()
            return CoreNavigationDestination.Error(message, code)
        }
    }

    // Prefer handlers whose moduleId is present in the (optional) ownership registry
    val owners = ownershipSource.owners
    val (ownedHandlers, otherHandlers) = handlers.partition { it.moduleId in owners.keys }
    val orderedHandlers = if (ownedHandlers.isNotEmpty()) ownedHandlers + otherHandlers else handlers.toList()

    // Prepare a URI optimized for handler matching, while keeping the original normalized string for fallback/telemetry
    val handlerUri = handlerUriForMatching(uri)

    // Try registered handlers
    for (handler in orderedHandlers) {
        val result = handler.handleDeepLink(handlerUri)
        if (result != null) {
            val owned = handler.moduleId in owners
            telemetry.onResolveSuccess(normalized, owned)
            // Convert typed destination to canonical deep link for downstream handling
            return GenericDestination(AppDeepLinks.build(result.destination))
        }
    }

    // Fallback - create a generic destination
    telemetry.onResolveFallback(normalized)
    return GenericDestination(normalized)
}
```

Public references (searchGitHub)
- The official AndroidX repo contains early Navigation 3 runtime/ui code and samples using androidx.navigation3.* (NavBackStack, NavEntry, NavDisplay, etc.). This confirms the API family exists upstream and aligns with a composable, entry-driven model.
  - androidx/androidx – navigation3/navigation3-ui, navigation3/navigation3-runtime (samples and tests)
- Third-party apps using androidx.navigation3.* are emerging (e.g., ReadYou, Neko), suggesting the ecosystem is starting to adopt it.
- We did not find references to our local custom types (Navigation3Host, Navigation3Controller), which are project-specific abstractions wrapping the Navigation 3 approach with typed deep links and persistence.

Side-by-side comparison
- Route representation
  - Current: string routes ("userDetail/{username}").
  - Navigation 3 typed: sealed AppDestination with @Serializable payloads; deep links generated via AppDeepLinks.
- Argument passing
  - Current: manual path segments and bundle extraction.
  - Nav3 typed: Kotlin serialization + AppDeepLinks ensures type-safe encoding/decoding.
- Deep links
  - Current: ad-hoc strings.
  - Nav3 typed: centralized DeepLinkHandlers per feature + DefaultDestinationResolver (ownership-aware, normalized URIs, web/app schemes).
- Back handling & stack control
  - Current: NavController.navigateUp/back stack implicit.
  - Nav3 typed: explicit Navigation3Controller API (navigate, popBackStackTo, singleTop, persistence restore) and Navigation3Host handling back.
- Persistence
  - Current: none.
  - Nav3 typed: BackStackStore with TTL, schema and navGraphVersion checks, kill switch, size caps.
- Testing
  - Current: none specific to navigation.
  - Nav3 typed: dedicated tests exist in navigation-impl (parity, persistence, read-only gate) enabling confidence.

Mapping (current → Nav3 typed)
- String route "userDetail/{username}" → AppDestination.UserDetail(username) with AppDeepLinks.build(AppDestination.UserDetail("octocat")).
- navController.navigate("userDetail/${login}") → navigation3Controller.navigate(AppDestination.UserDetail(login)).
- Back/up via navController.navigateUp() → navigation3Controller.navigateBack().
- Start destination string → typed start via Navigation3Host(controller, AppDestination.UserList) overload.
- Deep link parsing in fragments/activities → module DeepLinkHandler + DefaultDestinationResolver.

Dependencies and build-logic plan
- Keep version catalog as the single source of truth (already set up). No hardcoded versions.
- Ensure kotlinx-serialization is applied where needed by typed destinations.
- Maintain androidx.navigation.compose temporarily for fallback until migration is complete; remove once all screens use Navigation3Host.
- Keep the current policy: single import, no wildcard imports; enforce via ktlint and detekt.

Incremental migration strategy
- Phase 0: Preparation
  - Confirm Navigation 3 dependencies (runtime/ui/viewmodel integration) resolvable for current toolchain.
  - Verify persistence kill switch flags and defaults.
- Phase 1: Replace entry NavHost with Navigation3Host
  - In MainActivity, render the current entry via Navigation3Host, mapping entries to screens with when(entry.destination).
  - Use AppDeepLinks to build deep links for typed navigation.
- Phase 2: Feature-by-feature adoption
  - Replace string-based navigation with ModuleNavigator or direct Navigation3Controller.navigate(AppDestination.X(...)).
  - Ensure each feature exposes a DeepLinkHandler and documents supported patterns.
- Phase 3: Deep link parity & results
  - Validate every existing route has a deep link equivalent; add tests for parse/build parity.
  - Where result passing is needed, add typed return channels or state restoration as appropriate.
- Phase 4: Cleanup
  - Remove androidx.navigation.compose usage and string routes when all screens are migrated.
  - Keep persistence enabled by default; adjust TTL/limits based on telemetry.

Example MainActivity migration (illustrative)
```kotlin path=null start=null
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  @Inject lateinit var controller: Navigation3Controller

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      GithubUsersTheme {
        Navigation3Host(
          controller = controller,
          startDestination = AppDestination.UserList,
        ) { entry ->
          when (val dest = entry.destination) {
            is NavigationDestination.Generic -> {/* optional generic renderer or error */}
            is CoreNavigationDestination.Home -> {/* home */}
            is CoreNavigationDestination.Settings -> {/* settings */}
            is UserDestination.UserList -> UserListScreen(/* obtain VM via hiltViewModel() */)
            is UserDestination.UserDetail -> UserDetailScreen(/* username = dest.username */)
            is SearchDestination.Search -> {/* search UI */}
          }
        }
      }
    }
  }
}
```

Testing and validation plan
- Unit tests
  - AppDeepLinks.build/parse parity for every destination.
  - DeepLinkHandlers: supportedPatterns coverage, edge-case URIs, ownership collisions.
  - Navigation3Controller persistence TTL and schema/appVersion mismatch cases.
- UI tests
  - Smoke flows for list → detail → back; process death restore parity.
  - Predictive back behavior (top-of-stack only interception).
- Quality gates
  - Run detekt and ktlintCheck on navigation-* and feature-* modules.
  - AssembleDebug via Gradle MCP prior to rollout.

Risks and mitigations
- Mismatch between legacy routes and deep links → Maintain dual mapping during migration; add parity tests.
- Serialization changes → Use versioned payloads if schema evolves; keep arguments minimal, use IDs.
- Restore loops or invalid stacks → TTL + size caps + schema/navGraphVersion checks (already implemented).
- Cross-module ownership conflicts → Centralize ownership registry; resolve conflicts in DefaultDestinationResolver.

References (from searchGitHub)
- AndroidX upstream samples and tests using androidx.navigation3.* (runtime/ui/viewmodel integration).
- Third-party repos adopting navigation3 runtime/ui.

Next steps
- Implement Phase 1 replacement of NavHost with Navigation3Host in app MainActivity.
- Migrate the user list → detail flow to typed navigation; keep navigation-compose as fallback for any remaining screens until complete.
- Wire detekt/ktlint checks and assembleDebug via Gradle MCP.

