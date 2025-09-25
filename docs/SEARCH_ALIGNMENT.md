# Search Alignment Implementation Plan

## Status Snapshot (2025-09-24)
- **Overall:** Implementation complete — awaiting ownership follow-up and scheduled instrumentation run.
- **Branch:** `feature/search-alignment-finalization` (stale).
- **Owner:** Platform Foundations squad (handoff scheduled 2025-09-26).

## Reality Check vs Original Goals
| Area | Original Claim | Current Code Reality | Gap |
| ---- | -------------- | -------------------- | --- |
| Shared history manager | "SearchHistoryManager" DataStore module | Implemented as `SearchHistoryDataSource` + `ManageSearchHistoryUseCase` in feature-search | ✅ Behaviour matches, naming differs |
| SearchableListScaffold adoption | Search UI rewired to shared scaffold | All list-driven screens (`feature-users`, `feature-repository`, `feature-search`) call the shared scaffold (`core-ui/src/main/kotlin/com/example/githubusers/core/ui/SearchableListScaffold.kt`) with header slots for toggles/suggestions | ✅ |
| PagingSearchController usage | Users/search ViewModels delegate to controller | `SearchViewModel` now wraps search flows via `PagingSearchController` for both user and repository domains | ✅ |
| Navigation integration tests | 95% coverage incl. Navigation3 | No Navigation3 or Compose instrumentation tests in feature-search; users coverage just added | ❌ Add tests |
| Analytics parity | Search analytics façade wired | Events logged in `SearchViewModel.executeSearch` | ✅ |

## Outstanding Work Items
1. **Stabilise SearchableListScaffold contract**
   - [x] Align feature list screens with the shared scaffold (users + repositories) now that the legacy wrapper is removed.
   - [x] Confirmed the shared scaffold’s header slot satisfies search UI chrome; a dedicated variant is unnecessary for now (documented in `feature-search` usage).
2. **UI Alignment**
   - [x] `SearchScreen` composes through the shared scaffold header slot while preserving domain toggles, suggestions, and trending sections.
3. **ViewModel Simplification**
   - [x] Search feature now uses dual `PagingSearchController` instances (users + repositories) instead of manual debounce/flatMap plumbing.
4. **Navigation & Testing**
   - [x] Added Navigation3 integration coverage (`feature-search/src/test/java/com/example/githubusers/feature/search/navigation/SearchNavigationIntegrationTest.kt`).
   - [x] Added Compose instrumentation coverage for query submission, history selection, and domain toggles (`feature-search/src/androidTest/java/com/example/githubusers/feature/search/presentation/ui/SearchScreenTest.kt`).
5. **Documentation & Ownership**
   - [x] Update README/changelog after implementation to reflect the accurate architecture.
   - [x] Confirmed Platform Foundations squad as interim owner; backlog review scheduled for 2025-09-26.

### Ownership Handoff
- Owner: Platform Foundations squad (primary: alex.nguyen@company.dev, backup: priya.sharma@company.dev)
- Actions:
  1. Review remaining follow-ups during the 2025-09-26 guild sync.
  2. Capture additional tasks (if any) in `docs/SEARCH_ALIGNMENT.md` under a new "Follow-ups" section.
  3. Revisit instrumentation metrics after two beta cycles (tracked via Platform board issue PF-421).

## Completed Milestones
- M0: MCP setup, repo indexing, consensus plan.
- M1: Recent search storage v2 JSON + migration (see `SearchHistoryDataSource`).
- M2: Analytics events (`query_submitted`, toggles) funnelled through `AnalyticsFacade`.
- M3: `SearchNavKey` with singleTop/restoreState and legacy redirect from users.
- M4 (partial): Standard user list components extracted to `core-ui`; users feature already consuming scaffold pending API alignment.
- M5: URL normalisation and error handling improvements in search data layer.
- M6: Search screen and ViewModel migrated to the shared scaffold + paging controller stack (2025-09-24).
- M7: Search navigation + Compose instrumentation tests added to guard regression scenarios (2025-09-24).

## Next Steps (Sequenced)
1. Platform Foundations to review PF-421 on 2025-09-26 and confirm no new actions.
2. Schedule and record the device-lab `:feature-search:connectedDebugAndroidTest` run post review.

## References
- `core-search/src/main/java/com/example/githubusers/core/search/PagingSearchController.kt`
- `core-ui/src/main/kotlin/com/example/githubusers/core/ui/SearchableListScaffold.kt`
- `feature-search/src/main/java/com/example/githubusers/feature/search/data/local/SearchHistoryDataSource.kt`
- `navigation-api/src/main/java/com/example/githubusers/navigation/api/SearchNavigation.kt`

## Follow-ups
- Platform Foundations to review metrics and capture any new actions (PF-421, 2025-09-26).
- Device lab run of `:feature-search:connectedDebugAndroidTest` to be scheduled post ownership sync.
