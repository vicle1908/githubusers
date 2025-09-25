# Search Alignment Updates (2025-09-24)

## Summary
- Unified `SearchableListScaffold` so feature modules share focus handling, inline submit, and error UI.
- Moved repository list onto the shared `DefaultPagingSourceProvider` + `PagingSearchController` stack matching feature-users.
- Added Navigation3 integration and Compose instrumentation tests to guard search flows end-to-end.

## Details
- `core-ui/src/main/kotlin/com/example/githubusers/core/ui/SearchableListScaffold.kt`
  - Added focus callbacks, search semantics, and header slot usage.
- `feature-repository/src/main/java/com/example/githubusers/feature/repository/presentation/viewmodel/RepositoryListViewModel.kt`
  - Aligned query debounce and controller usage with feature-users.
- `feature-search/src/test/java/com/example/githubusers/feature/search/navigation/SearchNavigationIntegrationTest.kt`
  - Ensures SearchNavKey resolves correctly.
- `feature-search/src/androidTest/java/com/example/githubusers/feature/search/presentation/ui/SearchScreenTest.kt`
  - Verifies query submission, history replay, and domain toggle.

## Follow-up
- Ownership hand-off: designate a maintainer for ongoing search alignment work.
- Monitor instrumentation run results once connected devices are available.
