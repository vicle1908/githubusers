# Feature Users Audit & Consolidation Plan
_Last updated: September 26, 2025_

## Scope
- Audit `feature-users` module directories spanning data, domain, navigation, presentation, shared UI, and tests.
- Surface redundant or unused assets that can be removed or merged with shared modules.
- Provide a sequenced cleanup plan with explicit status tracking and validation guardrails.

## Module Snapshot
- **Data**: `feature-users/src/main/java/com/example/githubusers/feature/users/data` hosts detail, list, remote, local, and aggregation code; Room schema exports live under `feature-users/schemas`. Key entry point: `UsersDataModule` (`feature-users/src/main/java/com/example/githubusers/feature/users/data/di/UsersDataModule.kt:16`).
- **Domain**: Use-case and repository abstractions under `feature-users/src/main/java/com/example/githubusers/feature/users/domain`; aggregator contract `UserRepository` (`feature-users/src/main/java/com/example/githubusers/feature/users/domain/repository/UserRepository.kt:9`).
- **Navigation**: Nav3 bindings plus deep link helpers in `feature-users/src/main/java/com/example/githubusers/feature/users/navigation`; `UsersFeatureDestinationProvider` orchestrates host integration (`feature-users/src/main/java/com/example/githubusers/feature/users/navigation/UsersFeatureDestinationProvider.kt:52`).
- **Presentation**: List/detail MVI stacks under `feature-users/src/main/java/com/example/githubusers/feature/users/presentation`; shared Compose widgets in `feature-users/src/main/java/com/example/githubusers/feature/users/shared/ui`.
- **Tests**: Instrumentation coverage in `feature-users/src/androidTest`, unit tests in `feature-users/src/test`; currently includes placeholder sanity test (`feature-users/src/test/java/com/example/githubusers/feature/users/SanityTest.kt:1`).

## Key Findings
- Duplicate deep link utilities exist: `feature-users/src/main/java/com/example/githubusers/feature/users/navigation/UserDeepLinks.kt:1` is unused while `feature-users/src/main/java/com/example/githubusers/feature/users/navigation/UsersDeepLinks.kt:5` powers all runtime navigation.
- Legacy Feature API wrappers (`feature-users/src/main/java/com/example/githubusers/feature/users/navigation/UserDetailFeatureApi.kt:1` and `feature-users/src/main/java/com/example/githubusers/feature/users/navigation/UserListFeatureApi.kt:1`) are orphaned after Nav3 adoption.
- `feature-users/src/main/java/com/example/githubusers/feature/users/shared/ui/UserItem.kt:1` duplicates `core-ui/src/main/kotlin/com/example/githubusers/core/ui/list/UserListItem.kt:1`; shared error helpers also replicate `core-ui` defaults.
- Repository DTOs diverge between modules: `feature-users/src/main/java/com/example/githubusers/feature/users/data/remote/dto/RepositoryDto.kt:10`, `core-common/src/main/kotlin/com/example/githubusers/core/model/RepositoryDto.kt:8`, and `feature-repository/src/main/java/com/example/githubusers/feature/repository/data/remote/RepositoryDto.kt:14` define overlapping models.
- Room schema export folder `feature-users/schemas/com.example.githubusers.feature.users.list.data.local.UserListDatabase/1.json` still references the pre-merge database name and version, while `UsersDatabase` now lives at version 2 (`feature-users/src/main/java/com/example/githubusers/feature/users/data/local/UsersDatabase.kt:21`).
- `UsersFeatureDestinationProvider` retains a TODO for external URL handling (`feature-users/src/main/java/com/example/githubusers/feature/users/navigation/UsersFeatureDestinationProvider.kt:118`).
- Module README links to a non-existent plan file (`feature-users/README.md:45`).
- Placeholder unit test offers no coverage (`feature-users/src/test/java/com/example/githubusers/feature/users/SanityTest.kt:6`).

## Execution Phases
```
Phase 1  Navigation
Phase 2  UI Consolidation
Phase 3  Data & Schema Alignment
Phase 4  Validation & Documentation
```
- **Phase 1 – Navigation**: Confirm no external references (repo-wide search via Desktop Commander), delete `UserDeepLinks.kt`, `UserDetailFeatureApi.kt`, and `UserListFeatureApi.kt`, and run `UsersNavigationIntegrationTest` to guard regressions.
- **Phase 2 – UI Consolidation**: Swap `feature-users` shared UI widgets (`UserItem`, `ColumnCenteredMessage`, `TextButtonLink`) for `core-ui` equivalents; verify accessibility semantics and analytics hooks remain intact.
- **Phase 3 – Data & Schema Alignment**: Extend the shared `core-common` `RepositoryDto` with required fields (topics, timestamps, visibility, size), update mappers/entities, remove feature-specific DTOs, and regenerate Room schemas to match `UsersDatabase` v2.
- **Phase 4 – Validation & Documentation**: Run quality gates (`:feature-users:detekt`, `:feature-users:ktlintCheck`, `:feature-users:test`, plus instrumentation when schema changes land), address the external URL TODO, replace `SanityTest` with targeted coverage, and update README + this plan with outcomes.

## Cleanup Workstream
| ID | Task | Status | Owner | Target | Notes & Dependencies |
|----|------|--------|-------|--------|-----------------------|
| NAV-01 | Remove `UserDeepLinks.kt` and migrate any callers to `UsersDeepLinks` | Not Started | TBD | 2025-10-01 | Confirm no external modules import the legacy helper; rerun `UsersNavigationIntegrationTest`. |
| NAV-02 | Delete `UserDetailFeatureApi.kt` and `UserListFeatureApi.kt`; document Nav3 entry point contract | Not Started | TBD | 2025-10-02 | Update Nav3 notes and ensure DI bindings still expose `UsersFeatureDestinationProvider`. |
| UI-03 | Replace `UserItem` usages with `UserListItem` from `core-ui`; retire duplicate shared UI helpers or promote them to `core-ui` if still needed | Not Started | TBD | 2025-10-04 | Verify accessibility semantics/analytics parity before removal. |
| DATA-04 | Adopt unified DTOs from `core-common` (or extend them) and update mappers/tests across `feature-users` and `feature-repository` | Not Started | TBD | 2025-10-06 | Extend shared DTO with topics/timestamps/visibility and update mappers + tests. |
| DATA-05 | Regenerate Room schema for `UsersDatabase` and relocate exports under the correct package name | Not Started | TBD | 2025-10-06 | Run `./gradlew :feature-users:generateRoomSchema` after DTO/entity changes; commit new exports. |
| UX-06 | Implement external URL navigation in `UsersFeatureDestinationProvider` and remove TODO | Not Started | TBD | 2025-10-03 | Wire host callback or document deferral in navigation API guide. |
| QA-07 | Replace `SanityTest` with targeted unit coverage (e.g., use-case happy path) | Not Started | TBD | 2025-10-05 | Cover repository aggregator + paging flows; ensure `:feature-users:test` stays green. |
| DOC-08 | Update `feature-users/README.md` to reference this plan and remove stale links | Not Started | TBD | 2025-09-27 | Point README to this plan and summarize phase status. |

## Progress Tracker
- [ ] Navigation cleanup (NAV-01, NAV-02)
- [ ] UI consolidation (UI-03)
- [ ] Data alignment (DATA-04, DATA-05)
- [ ] Experience polish (UX-06)
- [ ] Quality coverage (QA-07)
- [ ] Documentation updates (DOC-08)

## Validation Checklist
- Re-run `./gradlew :feature-users:detekt :feature-users:ktlintCheck :feature-users:test` after each structural change.
- Execute instrumentation smoke via `./gradlew :feature-users:connectedDebugAndroidTest` when Room schema or navigation wiring changes.
- Verify deep link handling manually with Navigation MCP once NAV tasks complete.

## Open Questions
- Should shared DTOs live in `core-common` or a dedicated `core-networking` package to avoid feature coupling?
- Is `UsersFeatureDestinationProvider` the right layer for browser routing, or should `navigation-api` expose a host callback?

---
Progress will be updated here as tasks transition states; please keep status and target dates current when work begins.
