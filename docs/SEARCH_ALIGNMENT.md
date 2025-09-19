# Search Feature Alignment Guide

This document captures best practices and recommendations for aligning the dedicated search experience (`feature-search`) with the existing user list experience (`feature-users`). The goal is to reuse established UX patterns, ensure functional parity, and provide a cohesive feel across the app.

## Why Alignment Matters

- Users already understand the user list interaction model (list layout, item affordances).
- Reusing the same UI paradigms reduces cognitive load and testing surface.
- Shared components simplify maintenance and enable consistent feature improvements.

## Best Practice Principles

1. **Reuse existing presentation layer where possible**
   - Leverage the same list item composables (`UserListItem`, avatars, metadata rows) for search results.
   - Maintain identical spacing, typography, and styling.
   - Ensure actions (tap to detail, overflow menus) behave consistently.

2. **Keep search state transitions predictable**
   - When search is activated from the user list, overlay the search UI or navigate to a dedicated screen without losing browsing context.
   - On dismiss/exit, restore the previous list state (scroll position, filters, selection).
   - Avoid clearing results unexpectedly; only clear when the user explicitly clears the query.

3. **Display search results and user list similarly**
   - Render results inside the same `LazyColumn` pattern used in `feature-users` (shared `LazyListState`, item spacing, paddings).
   - Reuse `UserListItem` (extract to `core-ui` if needed) so avatars, typography, and action affordances remain identical.
   - Provide the same loading/empty/error states (progress indicator, empty copy, retry CTA) and paging behaviors (pull-to-refresh, append indicators).
   - Supply stable item keys (e.g., `key = { it.id }`) and cache costly transforms via `remember`/`derivedStateOf` per Compose guidance.

4. **Trending & history as contextual enhancements**
   - When the query is blank, display recent searches and trending people as supplemental sections (chips or rows) styled like existing list content.
   - After a query executes, replace the contextual sections with results rendered in the shared list layout.
   - Support one-tap reuse of recent queries and navigation from trending rows directly to detail screens.

5. **Deep link and intent parity**
   - Launch search via canonical deep links (`app://search`, `app://search?q={query}`) and redirect legacy links handled by `feature-users` to the new destination.
   - Propagate origin metadata (`user_list`, `deeplink`, etc.) so analytics and dismissal logic understand the entry point.
   - Mirror navigation transitions and back semantics from the user list so exiting search restores the previous browse state.

6. **Data & architecture alignment**
   - Map search responses to the existing `UserSummary`/`UserUi` models before they reach UI code.
   - Delegate all search paging to one repository/Pager; share `PagingConfig` with browse flows so tuning remains consistent.
   - Keep search state in `SearchViewModel` and browse state in `UsersListViewModel`, sharing helper utilities (history, trending) where appropriate.

7. **State handling best practices**
   - Cache expensive computations with `remember`/`rememberSaveable` so recompositions don\'t redo sorting or heavy transforms.
   - Use `LazyListState` (hoisted to the view model when necessary) to preserve scroll position across navigation and configuration changes.
   - Apply `derivedStateOf` and lambda-based modifiers for frequently changing state (e.g., scroll offsets) to limit recomposition cost.
   - Hoist state to the caller layer (`SearchRoute`, host screen) to keep composables stateless and easy to reuse.

## Recommended Changes

### UI / UX
- Replace custom search list items with the shared `UserListItem` composable (extract to `core-ui` if needed).
- Align list container styles (background, paddings, columns) with `UserListContent`; reuse the same `LazyListState` and remember blocks.
- Provide a consistent top app bar with search entry/back/settings; IME action should trigger search execution.
- Ensure pull-to-refresh and list loading indicators behave identically to the user list.

### ViewModel / State
- Expose search results as `Flow<PagingData<UserUi>>` cached in `viewModelScope`, mirroring `UsersListViewModel.users`.
- Maintain history/trending datasets and render them as chips/rows with the same visual language as the list.
- Track search activation/dismissal so exiting search restores the previous browse state without clearing users or scroll position.
- Use `rememberSaveable` for query text, filters, and `LazyListState` snapshot data to survive configuration changes.
- Prefer `derivedStateOf` for computed UI flags (e.g., “show history”, “show loading”) so recomposition happens only when inputs change.

### Navigation
- Use the shared `openSearch(query?, origin)` helper via CompositionLocal or navigator interface.
- Configure navigation with `launchSingleTop = true` and `restoreState = true` so returning to search preserves query/results.
- Build deep links with `Uri.Builder` (`appendPath`, `appendQueryParameter`) to ensure proper URL encoding (e.g., usernames and query strings).
- Map deep links (including legacy `app://users/search`) to `SearchNavKey.Search`, and ensure back/pop restores the user list scroll position.

### Data Layer
- Remove duplicate search logic from `feature-users`; rely on a single `SearchRepository` for queries/history/trending.
- Convert search responses to `UserSummary`/`UserUi` upstream so composables reuse existing models.
- Share DataStore persistence (same file name/keys), trending, and analytics helpers as needed between modules.

## Implementation Checklist

- [x] Extract shared row/list composables (for example, `UserListItem`) to `core-ui` or shared package.
- [x] Update `SearchScreen` to reuse the shared list container (`StandardUserList`) so search results render identically to the browse experience.
- [ ] Shared list refactor implementation plan:
  1. **Design shared API surface**
     - Draft a slot-based `StandardUserList` composable in `core-ui` (or a dedicated `ui-users` module) that wraps `PullToRefreshBox` ([Compose docs](https://developer.android.com/develop/ui/compose/components/pull-to-refresh)) and the grid/list behaviour already proven in `UserListContent`. Centralize Paging `LoadState` handling (refresh/append errors, empty, loading) and bake in accessibility semantics (collection roles, live status announcements).
     - Define an optional `StandardUserRow` helper around `UserListItem`, exposing lambdas for click/follow/badge decoration. Keep the list generic (`StandardUserList<T>`) via key/itemContent extractors or a lightweight `StandardUserUi` interface so features stay decoupled (see slot API best practices in [Compose Slot APIs](https://proandroiddev.com/designing-slot-apis-in-jetpack-compose-4a981ddbc776)).
  2. **Implementation & hardening**
     - Support header/footer/content slots so search can inject recent query chips or trending sections while browse remains minimal. Include hooks for custom empty/error content, sticky headers, inline append-error rows, and optional pull-to-refresh overrides.
     - Build comprehensive tests: unit tests that map Paging `CombinedLoadStates`, Compose UI tests for pull-to-refresh gestures, semantics tests validating TalkBack output, and screenshot tests for empty/error/loading states. Reuse patterns documented in the JetNews `LoadingContent` implementation (see DeepWiki summary of compose-samples search).
     - Publish usage docs (KDoc + MD snippet) covering typical scenarios (browse-only, search with header/footer, custom retry copy) and note required dependencies (`compose-material3`, `foundation-pullrefresh`, `paging-compose`).
  3. **Phased adoption**
     - Phase 1: Migrate `feature-users` to the shared scaffold, validating portrait/landscape parity and analytics hooks. Keep a thin wrapper if feature-specific behaviour (e.g., multi-select) is pending.
     - Phase 2: Migrate `feature-search`, providing header slot for recent history/trending and verifying analytics events (`search_opened`, `result_clicked`) still emit. Update instrumentation/UI tests to cover search load states with the new component.
     - Phase 3: Remove deprecated search list code, align docs/handbook, and file follow-up tasks for optional enhancements (skeletons, sticky section headers).
  4. **Regression safeguards**
     - Extend CI to run new UI/semantics tests for both screens.
     - Track migration risks (API churn, performance regressions) in the rollout checklist and add monitoring for Paging load metrics once deployed.
     - Revisit the shared API quarterly to ensure it stays lean and doesn't accumulate unused options.
- [x] Align loading/empty/error states (progress, empty copy, retry) across both features.
- [x] Propagate query/origin/filter through `SearchNavKey` and initialize `SearchViewModel` with `initialize(initialQuery, initialFilter, origin)`.
- [x] Ensure `feature-users` search affordance calls `openSearch` and removes inline search logic/state.
- [x] Validate analytics events for browse/search flows (search_opened, query_submitted, result_clicked, trending_shown, back_to_browse).
- [x] Add regression tests covering search activation, trending/history, state preservation, and dismissal back to user list.
- [x] Verify DataStore history migration (JSON v2 implemented; tests deferred) and ensure performance best practices (remember, derivedStateOf, item keys) are applied.
- [x] Confirm `rememberSaveable`/`LazyListState` usage restores query text and scroll position across navigation/back.
- [x] Audit lazy list keys and `derivedStateOf` usage to prevent unnecessary recompositions.

## References & Further Reading

- [Mobile Search UX Best Practices (Algolia)](https://www.algolia.com/blog/ux/mobile-search-ux-best-practices)
- [UI Patterns: Search, Sort, Filter (Smashing Magazine)](https://www.smashingmagazine.com/2012/04/ui-patterns-for-mobile-apps-search-sort-filter/)
- Android Jetpack recommendations for search & navigation
- Existing `feature-users` documentation for layout and interactions

## Conclusion

By unifying search and user list experiences, we deliver a coherent user journey, simplify maintenance, and make future enhancements easier to roll out. Use this guide to ensure any search-related work reuses established components, handles state transitions reliably, and adheres to best-practice UX patterns.

---

## Addendum: Current Implementation Notes (2025-09-17)

This section documents the concrete implementation state to help align engineering work with this guide.

- Navigation (Navigation 3)
  - Deep links owned by features:
    - feature-search: `app://search`, `app://search?q={query}`, legacy: `app://users/search` (+ `q`)
    - feature-users: `app://users/list`, `app://users/user/{username}`
  - Type-safe keys:
    - feature-search: `SearchNavKey.Search(query, origin, filter)`
  - Runtime semantics:
    - restoreState: enabled via `rememberSavedStateNavEntryDecorator` in `MainActivity`
    - singleTop: implemented via equality check to avoid pushing duplicate top entries

- Analytics façade and events
  - Façade: `core-common` defines `AnalyticsFacade` and `FakeAnalytics` (Hilt bound in app `AnalyticsModule`)
  - Search events wired in `SearchViewModel`:
    - `search_opened`, `query_submitted`, `result_clicked`, `trending_shown`, `back_to_browse`
  - Screen triggers:
    - `TrendingShown` emitted on first trending visibility; back arrow emits `BackToBrowse` before navigating back

- Next verification (manual, no tests per scope):
  - Deep link smoke tests (`app://search`, `app://users/search`, `app://users/user/{username}`)
  - Confirm singleTop prevents duplicate Search/User entries
  - Confirm returning to Search restores state (query/results/scroll) and Users list scroll is preserved

- Error model & DTO mapping (new)
  - Domain error model: `SearchError` (Network, Timeout, RateLimited, Client, Server, Unknown) with `SearchException(error, message, cause)`.
  - Data-layer mapper: map Ktor/IO exceptions to `SearchError` and propagate to Paging via `LoadResult.Error(SearchException(error))`.
  - UI surfacing: Search screen shows a small banner for `LoadState.Error` with category-specific copy and a Retry button (uses `pagingItems.retry()`).
  - DTOs: snake_case from GitHub mapped using `@SerialName` to lowerCamelCase (`avatarUrl`, `publicRepos`, `createdAt`, `updatedAt`, `totalCount`, `incompleteResults`).
  - Mappers: `SearchMapper` consumes the new properties; no behavioral changes to UI models.
