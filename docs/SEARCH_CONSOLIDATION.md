# Search Feature Consolidation Guide

This guide walks AI assistants through consolidating the duplicated search functionality that currently lives in both `feature-users` and `feature-search`. Follow the steps sequentially to migrate to a single, dedicated search module while preserving existing capabilities (filters, trending, history) and avoiding regressions.

## Context

- `feature-users` owns the user list and details screens but also embeds a lightweight search implementation (custom repositories, DTOs, paging source, inline search bar and ViewModel logic).
- `feature-search` provides a richer search experience (filters, history, trending, dedicated ViewModel/UI) yet is not wired into the app navigation, leaving the module unused.
- Both modules duplicate the GitHub `/search/users` integration, paging setup, and data models.
- Navigation uses Navigation 3 (`NavKey`, feature destination providers, deep-link handlers).

## Target Architecture

- **Single owner:** `feature-search` becomes the canonical module for search UI/logic.
- **Delegation:** `feature-users` launches the search feature via Navigation 3 instead of maintaining its own implementation.
- **Shared contracts:** Repository interfaces, DTOs, and paging configuration live in shared/core modules so both features consume the same data layer.
- **Navigation:** Add `SearchNavKey.Search(query?)` as a first-class destination reachable from user list, deep links, or future entry points.

## Pre‑Migration Checklist

1. Confirm you have up-to-date knowledge of:
   - `feature-users` search-related files (`UserListViewModel`, `UserListScreen`, `UserListRepositoryImpl`, `UserListPagingSource`, `SearchUsersUseCase`, DTOs).
   - `feature-search` files (`SearchViewModel`, `SearchScreen`, `SearchRepositoryImpl`, DataStore history, paging sources, DI modules, navigation bindings).
2. Ensure Navigation 3 infrastructure is healthy (registry, deep-link dispatcher, feature providers bound via Hilt).
3. Identify any analytics or telemetry tied to user list search to carry forward.
4. If external consumers reference old deep links (e.g., `app://users/search`), list them for redirection.

## Phase 1 – Align Domain & Data Layer

1. **Unify repository contracts**
   - Move or create shared interfaces in `core-domain`/`core-data`:
     - `UsersRepository`: browse + detail.
     - `SearchRepository`: `searchUsers(query, filters)`, history APIs, trending feed.
   - Update both features to depend on these interfaces only.
2. **Centralize DTOs and mappers**
   - Consolidate duplicate models into `core-network` or `core-model` (e.g., `GitHubUser`, `SearchResponse`).
   - Delete redundant DTOs in `feature-users` once shared models exist.
3. **Standardize paging configuration**
   - Provide a shared `PagerFactory`/helper for search queries so page size, prefetch distance, and caching are consistent.
4. **DI wiring**
   - Bind concrete implementations once (likely in app-level Hilt modules or `feature-search` if it now owns the implementation).
   - Ensure app module no longer binds duplicate search repositories.

## Phase 2 – Navigation Integration

1. **Add Search NavKey**
   - Extend `SearchNavKey` (or create if missing) to accept `query: String?`, `origin: String`, and optional filter state (Parcelable/Serializable).
2. **Expose navigation helper**
   - Provide a `SearchNavigator` or `openSearch(query?, origin)` function accessible to other features via a shared interface/CompositionLocal.
3. **Wire feature-users entry point**
   - Replace the inline search button or search bar in `UserListScreen` with a trigger that calls `openSearch`.
   - Pass relevant context (e.g., origin = `user_list`, prefill query if needed).
4. **Handle legacy deep links**
   - Update `UsersFeatureDeepLinkHandler` to redirect `app://users/search` (and any legacy variants) to `SearchNavKey.Search` so existing external links immediately use the new destination.

## Phase 3 – UI & ViewModel Cleanup in feature-users

1. **Remove old intents/state immediately**
   - Delete `UserListIntent.UpdateSearchQuery`, `ExecuteSearch`, `ClearSearch`, and related branches from `UserListViewModel`.
   - Strip search-related properties from `UserListState` (`searchQuery`, `isSearchMode`, `isSearching`, etc.).
   - Update the UI to eliminate the inline search bar; ensure the entry point solely launches the dedicated search destination.
2. **Delete redundant data layer**
   - Remove `SearchUsersUseCase`, `UserListPagingSource`, duplicate DTOs, and search-specific branches in `UserListRepositoryImpl` so the module only handles browse/detail concerns.
   - Verify browse-only functionality still works (RemoteMediator for browsing) after the removal.
3. **Shared UI components**
   - If any search result item UI is duplicated, extract to `core-ui` or reuse components from `feature-search` where appropriate.

## Phase 4 – Enhance feature-search (if needed)

1. Ensure `SearchScreen` handles
   - Optional initial query (prefill focus and results).
   - Origin-specific analytics and back navigation.
   - Trending fallback when query empty.
2. Confirm DataStore history remains under the same file/key for continuity.
3. If trending/backfill should be shared across features, extract to a dedicated module but keep search as consumer.

## Validation & Testing

- **Unit tests**
  - Verify consolidated repositories return expected paging data.
  - Test DataStore migration if keys or structure changed.
- **UI tests**
  - From user list, tapping “Search” opens the search screen, prefilled query (if provided), and back returns to the list.
  - Search filters, history, and trending function as before.
- **Navigation tests**
  - No duplicate search instances on the stack; `singleTop` or state restoration behaves as intended.
  - Legacy deep links route to the new destination.
- **Performance**
  - Monitor network calls to confirm caching/paging isn’t duplicated across modules.

## Deployment & Monitoring

- Validate the integrated experience in QA/staging with automated and manual regression tests.
- Because legacy code is removed up front, treat rollback as a full revert of the migration commit if critical issues surface.
- Monitor crash logs, ANRs, search engagement metrics, and analytics parity throughout release to catch regressions early.

## Task Breakdown

Use the following to-do list to track progress. Each item can be broken down further into code changes, tests, or reviews as needed.

1. **Audit & Preparation**
   - [x] Review all search-related files in `feature-users` and `feature-search` (ViewModels, UI, repositories, DTOs, paging sources, DI modules).
   - [x] Document existing analytics and deep links related to search.
   - [x] Confirm Navigation 3 registry and deep-link dispatcher health (registry bindings, dispatcher behavior).

2. **Domain/Data Consolidation**
   - [x] Create shared `UsersRepository` and `SearchRepository` contracts in core modules.
   - [x] Consolidate GitHub search DTOs/mappers into shared modules and remove duplicates.
   - [x] Provide shared paging helpers/config for search queries.
   - [x] Update DI bindings so only one implementation of each repository exists.

3. **Navigation Integration**
   - [x] Extend `SearchNavKey` (query, origin, filters) and ensure it is serializable.
   - [x] Expose `openSearch(query?, origin)` helper via CompositionLocal or shared navigator interface.
   - [x] Update `UserListScreen` to launch `openSearch` and remove inline search controls.
   - [x] Redirect legacy deep links (e.g., `app://users/search`) through `SearchFeatureDeepLinkHandler`.

4. **feature-users Cleanup**
   - [x] Remove search-related intents/states from `UserListViewModel` and `UserListState`.
   - [x] Delete redundant use cases/paging sources/DTOs (e.g., `SearchUsersUseCase`, `UserListPagingSource`).
   - [ ] Verify browse-only functionality and RemoteMediator continue to operate.

5. **feature-search Enhancements**
   - [x] Ensure `SearchScreen` supports optional initial query, origin analytics, and trending fallback.
   - [x] Confirm DataStore history keys remain compatible (or migrate if unavoidable).
   - [ ] Extract shared UI components if other modules need them.

6. **Testing & Validation**
   - [ ] Update/add unit tests for unified repositories and DataStore behavior.
   - [ ] Update UI/E2E tests for navigation flow (open search from user list, deep links, back navigation).
   - [ ] Monitor network/paging behavior to confirm no duplication of calls.

7. **Deployment & Monitoring**
   - [ ] Validate in QA/staging and run regression suites.
   - [ ] Plan rollback by referencing the migration commit.
   - [ ] Monitor crash/ANR rates, search engagement metrics, and analytics parity post-release.

## References

- [Android app modularization guide](https://developer.android.com/topic/modularization)
- [Common modularization patterns](https://developer.android.com/topic/modularization/patterns)
- [Navigation 3 documentation](https://developer.android.com/guide/navigation/navigation-3)
