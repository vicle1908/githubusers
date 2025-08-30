---
description: "Android Development Rule - Kotlin and Android framework best practices"
globs: ["**/*.kt", "**/*.java", "**/*.xml", "**/AndroidManifest.xml"]
alwaysApply: true
---

# Android Development Rule

Scope
- This guide covers Android platform/framework practices for this repository: Jetpack Compose (Material 3), Navigation 3, Hilt, Ktor client, Room/DataStore, MVI, testing, performance, accessibility, and build/tooling references.
- Kotlin language style (naming, imports, function style, generics, etc.) is out of scope. See .kilocode/rules/kotlin.md.

## Architecture

- Clean Architecture with feature-based modules and MVI.
  - Presentation: Compose UI + ViewModels expose immutable StateFlow and one-off SharedFlow effects.
  - Domain: use cases, entities, error models.
  - Data: repositories, Ktor services, Room DAOs, mappers.
- MVI conventions
  - State: immutable data classes; Events/Intents: sealed types; Effects: one-off channel (SharedFlow).
  - UI collects state with collectAsStateWithLifecycle; handle effects in LaunchedEffect.
  - Keep business logic in ViewModel/use cases, not in Composables.

## UI with Jetpack Compose (Material 3)

- Theming: central MaterialTheme (color/typography/shape). Support dark mode and dynamic color where relevant.
- State & stability: prefer stable/immutable models; use remember/derivedStateOf for computed values; avoid capturing changing state in lambdas; expose read-only StateFlow to UI.
- Lazy lists: always provide stable keys and contentType where applicable.
- Side-effects: use LaunchedEffect/DisposableEffect/rememberUpdatedState/produceState appropriately.
- Accessibility: provide semantics and contentDescription; respect minimum touch targets and font scaling.
- Avoid XML/ViewBinding/DataBinding-first guidance. Compose is the primary UI toolkit in this codebase.

## Navigation (Navigation 3)

- Single source of truth for navigation via Navigation 3.
  - Typed destinations and route contracts with explicit arguments; deep link support (app scheme + web URLs) with feature-owned handlers (`@OwnsDeepLinks`) and KSP-generated ownership providers discovered via ServiceLoader.
  - Back stack controller with versioning, TTL, and size caps; process-death restoration with schema/navGraph/appVersion validation. Offer a kill switch via `PersistenceConfig`.
  - Predictive back gestures: only intercept at the top screen using per-top-screen BackHandler policy; otherwise let system back proceed.
  - Multi-window support via ActivityRetained scoping of the controller to keep stacks isolated per window.
  - Exported DeepLink entry Activity with https and app-scheme intent filters; ensure verification for App Links where applicable.
  - Telemetry baseline via `ReadOnlyNavGate` and navigation telemetry events; NoOp by default with optional debug logging.
  - ViewModel scoping via hiltViewModel; use SavedStateHandle for args/state.
  - Do not mix with legacy navigation graphs in this app.
  - See repo docs: NAVIGATION_3_IMPLEMENTATION.md, docs/navigation3/runbooks/, and navigation-api/navigation-impl modules.

## Dependency Injection (Hilt)

- Scopes:
  - @Singleton for app-wide stateless infrastructure.
  - @ActivityRetainedScoped for objects tied to Activity lifecycle across config changes.
  - @ViewModelScoped for per-ViewModel dependencies.
- Qualifiers: use qualifiers for multiple HttpClient instances or data sources.
- Testing: @HiltAndroidTest + HiltAndroidRule, @UninstallModules for overrides.

## Networking (Ktor client)

- Client configuration (OkHttp engine):
  - ContentNegotiation(JSON with kotlinx.serialization), Logging (sanitize Authorization), DefaultRequest headers.
  - Timeouts: request/connect/socket; Retry with exponential backoff for transient failures.
  - Auth: Bearer tokens with single-flight refresh guarded by Mutex; update storage atomically; retry failed requests once after refresh.
  - Consider certificate pinning if threat model requires it.
- Error handling: map transport/protocol errors to domain-level sealed errors.

## Persistence (Room, DataStore)

- Room:
  - DAOs return Flow for observable data; keep DB operations off main thread; use transactions where needed; implement migrations (AutoMigration when feasible).
- DataStore:
  - Preferences for simple key-value; Proto for structured data. For sensitive values (e.g., tokens), use encryption (EncryptedDataStore or Security Crypto around Preferences).
  - Project-specific: persist the ghtk token in DataStore and expose a sync mechanism via a ContentProvider as planned. Use careful permissioning and a clear authority if cross-app access is required.

## Concurrency & lifecycle

- Use viewModelScope for UI flows; collect with repeatOnLifecycle in Activities/Fragments or collectAsStateWithLifecycle in Compose.
- Choose dispatchers correctly (Default for CPU, IO for blocking I/O); inject dispatchers where needed for testability.
- Use stateIn/shareIn to expose cold flows; avoid GlobalScope.

## Testing

- Unit tests: JUnit (prefer JUnit5 where available) + kotlinx-coroutines-test (runTest), MockK; test ViewModels/use cases/repositories; Flow testing with Turbine.
- UI tests: Compose UI tests with createAndroidComposeRule; assert semantics, accessibility, and predictive back behavior at top-of-stack.
- Integration/instrumented:
  - Robolectric for fast JVM-on-Android tests where appropriate.
  - Instrumented tests with Hilt test rules/modules; custom Hilt runner where needed (see app/src/androidTest and HiltTestRunner).
  - Ktor MockEngine for networking; Room in-memory DB for persistence.
- Operational tooling:
  - Use Android MCP tooling (if available in this repo) for adb install, log collection, and test orchestration.

## Performance & profiling

- Compose performance best practices: stable models, remember/derivedStateOf, keys for lazy lists, avoid heavy work in composition.
- Memory and startup: use App Startup optimizations, minimize reflection and classpath initialization; lazy initialize heavy components.
- Enable StrictMode in debug; trace critical paths.
- Baseline Profiles + Macrobenchmark for startup and jank-sensitive flows.
- See repo: COMPOSE_PERFORMANCE_ANALYSIS.md and RECOMPOSITION_BEST_PRACTICES.md.

## Accessibility & i18n

- Provide contentDescription, semantics, and proper roles; test with TalkBack.
- Support RTL and string resources; avoid hard-coded text; respect font scaling.

## Build & tooling (reference)

- Use gradle-mcp-server for Gradle tasks (build, assemble, tests). See .kilocode/rules/gradle-mcp.md.
- Project standardization: version catalog + convention plugins; Detekt + KtLint enforced.
- Note: Android resource files use snake_case by convention (this is separate from Kotlin file naming).

## Sources

- Navigation 3 (repo): NAVIGATION_3_IMPLEMENTATION.md, navigation-api, navigation-impl
- Compose performance (repo): COMPOSE_PERFORMANCE_ANALYSIS.md, RECOMPOSITION_BEST_PRACTICES.md
- Hilt ViewModel & scopes: https://dagger.dev/hilt/view-model , https://dagger.dev/hilt/components
- Ktor client (general docs): https://ktor.io/ (client configuration, JSON, timeouts, retries, logging)
- Room & DataStore (general docs): https://developer.android.com/jetpack/androidx/releases/room , https://developer.android.com/topic/libraries/architecture/datastore
- Detekt import rules: https://detekt.dev/docs/rules/style , https://detekt.dev/docs/rules/formatting
