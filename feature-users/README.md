#Feature Users Module

## Domain Overview

The feature owns two domain abstractions that sit on top of the shared `core-users` models:

- `UserDetail` — rich profile information surfaced on the detail screen.
- `Repository` / `RepositoryLicense` — repositories displayed in the inline tabs for a given user.

Alllist-facing models (`UserSummary`) are provided by `core-users` to avoid duplication.

### Layered Flow

```
UI (Compose screens, Routes)
   │
   ├─ ViewModels
   │    └─ invoke Use Cases (ObserveUserList, GetUserDetail, GetUserRepositories, FollowUser)
│
   ├─ Use Cases (domain)
   │    └─ delegate to UserRepository / UserDetailRepository
   │
   ├─ Repositories (data)
   │    ├─ UserListRepository ↔ UsersDatabase.userSummaryDao + UserListApiService
   │    └─ UserDetailRepository↔ UsersDatabase.userDetailDao + UserDetailRemoteDataSource
   │
   └─ core infrastructure (Room, Ktor, analytics)
```

## Key Contracts

| Layer | Type | Location | Responsibility |
| ----- | ---- | -------- | -------------- |
| Domain | `UserRepository` | `feature/users/domain/repository` | Entry point for list + detail orchestration; aggregates list/detail repositories. |
| Domain | `UserDetailRepository` | `feature/users/detail/domain/repository` | Fetches profile data and user repositories, supports follow/unfollow. |
| Domain | Use Cases (`ObserveUserListUseCase`, `GetUserDetailUseCase`, `GetUserRepositoriesUseCase`, `FollowUserUseCase`) | `feature/users/.../domain/usecase` | Thin coordinators; all annotated with `@Reusable` for Hilt provisioning. |
| Data | Room (`UsersDatabase`, DAOs, Entities) | `feature/users/data/local` | Unified cache for summaries, details, repositories, remote keys. |
| Data | Remote | `feature/users/data/remote` | DTOs and APIs shared by list/detail flows. |
| Presentation | ViewModels | `feature/users/list/detail/presentation` | Consume use cases, expose search state and detailstate to Compose. |

The aim is to keep only orchestration logic in ViewModels while business rules sit in use cases and repositories.

## Pending Improvements

The implementationplan in `docs/FEATURE_USERS_IMPLEMENTATION_PLAN.md` tracks ongoing work (presentation refactor, testing, observability). Refer there for task status and verification steps.
