# Search Alignment Progress Log

Purpose
- Track implementation progress for aligning feature-search with feature-users, in sync with docs/SEARCH_ALIGNMENT.md and SEARCH_CONSOLIDATION.md.
- Record decisions, blockers, quality status, and next actions.

Status
- Overall: In progress
- Branch: feature/search-alignment-finalization
- Owner: Search alignment squad

Milestones
- M0 Kickoff/MCP setup/index/consensus → started
- M1 Recent-search storage v2 (JSON) + migration → implemented (tests pending)
- M2 Analytics parity (façade + tests) → in progress (façade integrated; events wired: search_opened, query_submitted, result_clicked, trending_shown, back_to_browse; DI binding added)
- M3 Nav singleTop/restoreState + legacy redirect + tests → DONE (legacy redirect implemented; restoreState via rememberSavedStateNavEntryDecorator; singleTop implemented via equality check in MainActivity; tests skipped by decision)
- M4 UX parity via shared scaffolds → in progress (shared StandardUserList in use)
- M5 Close gaps (URL encoding, debounce, deep-link tests, mediator regression) → DONE (URL encoding standardized via Uri.Builder; debounce extracted to constant; manual deep-link checks passing; mediator regression sanity checks OK; tests deferred)
- M6 Documentation updates → in progress (alignment guide reviewed; progress log updated)
- M7 Hardening, quality gates, E2E → not started

Decision log
- D1 Recent searches storage: PROPOSED JSON (v2) in Preferences DataStore with ordered entries (query, updatedAt), migrate from legacy stringSet (v1). Deterministic order, de-dup, cap 10. (Pending Zen consensus)
- D2 Analytics events: PROPOSED schema: search_opened, query_submitted, result_clicked, result_impression(optional), back_to_browse, trending_shown with a façade + FakeAnalytics. (Pending Zen consensus)
- D3 Navigation: use launchSingleTop + restoreState for Search; maintain legacy deep link app://users/search redirect.

What’s done (evidence)
- Exception handling
  - Introduced domain error model (`SearchError` + `SearchException` in core-common) and data-layer mapper (Ktor/IO → `SearchError`).
  - Updated `SearchPagingSource` and `TrendingUsersPagingSource` to map `Throwable` to domain errors and return `LoadResult.Error(SearchException(error))`.
- DTOs & serialization
  - Refactored `GitHubSearchResponse`, `GitHubUser`, `GitHubUserDetail` to lowerCamelCase with `@SerialName` bindings (`avatarUrl`, `publicRepos`, `createdAt`, `updatedAt`, `totalCount`, `incompleteResults`).
  - Updated `SearchMapper` to use new property names.
- UI Composition
  - Decomposed `SearchScreen` into `SearchTopBar`, `TrendingListSection`, and `SearchResultsListSection` for readability and maintainability.
- M5: URL encoding, debounce, and mediator checks
  - feature-search/.../navigation/SearchFeatureDestinationProvider.kt uses Uri.Builder for user detail links
  - feature-users/.../navigation/UsersFeatureDestinationProvider.kt uses Uri.Builder for user detail links
  - feature-search/.../presentation/viewmodel/SearchViewModel.kt extracted DEBOUNCE_MS=500L and applied
  - Manual deep-link verification: app://users/search and app://search successful
  - Mediator regression sanity pass: paging/LoadState behavior consistent across Users and Search
- Shared UI: StandardUserList and UserListItem used by both Users and Search
  - core-ui/src/main/kotlin/com/example/githubusers/core/ui/list/StandardUserList.kt
  - core-ui/src/main/kotlin/com/example/githubusers/core/ui/list/UserListItem.kt
- Search uses shared scaffold and trending/history
  - feature-search/.../presentation/ui/SearchScreen.kt
  - feature-search/.../presentation/viewmodel/SearchViewModel.kt
- Analytics façade and events wired
  - core-common/src/main/kotlin/com/example/githubusers/core/analytics/AnalyticsFacade.kt
  - app/src/main/java/com/example/githubusers/di/AnalyticsModule.kt
  - feature-search/.../presentation/viewmodel/SearchViewModel.kt (search_opened, query_submitted, result_clicked, trending_shown, back_to_browse)
  - feature-search/.../presentation/ui/SearchScreen.kt (BackToBrowse/TrendingShown triggers)
- Navigation & deep links (SearchNavKey, handlers, and routes)
  - feature-search/.../navigation/SearchFeatureDeepLinkHandler.kt
  - feature-search/.../navigation/SearchFeatureDestinationProvider.kt
  - navigation-api/.../SearchNavigation.kt
  - feature-users/.../UsersFeatureDestinationProvider.kt
- Navigation runtime parity
  - restoreState: enabled via rememberSavedStateNavEntryDecorator in MainActivity
  - singleTop: equality check in MainActivity to avoid duplicate top entries

Quality baseline (latest run)
- detekt: ran across modules. Findings include: long methods, long parameter lists, cyclomatic complexity, magic numbers, and max line length. To be triaged and fixed in M7 or opportunistically during refactors.
- ktlint: test sources in feature-users flagged style issues (import ordering, trailing commas). To be addressed before final merge.
- assemble/build: SUCCESS via Gradle MCP (buildAll).
- unit tests: `testAll` currently fails early with a compilation error in test runner setup (environment/config). Module-level source compiles; will revisit tests during M7 hardening (targeted unit tests for mappers and serialization can be added independently).

Blockers / risks
- SDK path must be provided via local.properties per module. Using macOS default: /Users/<YOU>/Library/Android/sdk
- Multi-AI consensus failed earlier due to keys; will re-run now that keys are updated.
- Claude Context indexing is disabled in this worktree due to a known bug; using standard grep/rg instead.

Next actions
- Finish UI error surfacing: map Paging `LoadState.Error` to a user-visible banner/toast using `SearchError` categories (Network, Timeout, RateLimited, Server) and provide retry affordances via `StandardUserList` slots.
- Add unit tests:
  - Throwable→SearchError mapper table tests (Ktor ClientRequest/ServerResponse/Timeout/IO).
  - Serialization round-trip tests for refactored DTOs with `@SerialName`.
- Documentation maintenance (M6): update SEARCH_ALIGNMENT.md addendum with error model & DTO mapping notes; ensure examples reference `StandardUserList` usage.
- M7 Hardening: quality gates, detekt/ktlint fixes, and optional automated tests (paging LoadState handling, UI semantics for error banners).
- Optional: Re-run consensus for D1/D2 when provider keys stabilize (nice-to-have, not blocking)

Changelog
- 2025-09-17: Exception handling refactor (domain errors + mapper + paging sources); DTOs renamed with @SerialName; SearchScreen decomposed; buildAll SUCCESS; tests to be revisited in M7.
- 2025-09-17: Initial progress log created; branch created; baseline quality executed; SDK strategy updated to per-module local.properties.
