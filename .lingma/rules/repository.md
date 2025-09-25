---
trigger: always_on
---

# Repository Feature Implementation Standards

## Core Architecture Principles

### Feature-Owned Navigation Contracts

- Each feature module defines its own deep links, destination providers, and navigation tabs
- Shared `navigation-api` module only exposes generic composition locals and helpers
- Feature modules build deep link URIs (e.g., `UsersDeepLinks.detail(username)`)
- App module orchestrates by reading injected `NavigationTab` set and dispatching deep links

### Composition Over Inheritance with Kotlin Delegates

- Favor composition over inheritance using Kotlin delegation feature
- Use interfaces and delegate implementations using the `by` keyword
- Create reusable components that can be easily swapped at runtime
- Reduce boilerplate with Kotlin's automatic delegation handling

### Reactive Paging and Search Approach

- ViewModel owns "input" state (search query)
- Delegate provider consumes input to produce "output" `PagingData` flow
- Use `StateFlow` and `flatMapLatest` for reactive logic
- Apply `cachedIn(scope)` for Paging 3 to work correctly

## Core Module Standards

### core-paging Module

- Implement generic `BaseRemoteMediator<Value : Any>` coordinating page increments
- Delegate data fetching, storage, and clearing responsibilities to injected lambdas
- Provide generic `BasePagingSource<T: Any>` for network-only pagination
- Create `PagingSourceProvider<Q, T>` interface with `updateQuery` method and `pagingData` flow

### core-search Module

- Expose reusable `PagingSearchController` and `SearchUiState`
- Coordinate query state with any `PagingSourceProvider`
- Handle query normalization, UI state exposure, and error dispatching

## Feature Implementation Guidelines

### ViewModel Integration Pattern

```kotlin
class RepositoryListViewModel @Inject constructor(
    repository: Repository
) : ViewModel() {

    private val pagingProvider = DefaultPagingSourceProvider(
        scope = viewModelScope,
        initialQuery = "",
        pagerFactory = repository::searchRepositories
    )

    private val searchController = PagingSearchController(
        provider = pagingProvider,
        scope = viewModelScope
    )

    val searchState: StateFlow<SearchUiState> = searchController.uiState
    val repositories: Flow<PagingData<Repository>> = searchController.results

    fun onQueryChanged(query: String) {
        searchController.submitQuery(query)
    }

    fun onRetry() {
        searchController.retry()
    }
}
```

### Navigation Implementation

- Create feature-specific `*DeepLinks` object alongside `FeatureDestinationProvider`
- Implement `FeatureDeepLinkHandler` for feature-owned deep links
- Use Hilt multibindings to bind providers and handlers
- Follow the Navigation 3 pattern with deep link dispatching

## Standardization Roadmap

### Shared Searchable List Scaffold

- Build `core-ui` composable owning search text entry, progress/error display
- Hand paging content to a slot for feature-specific content
- Migrate feature screens to this scaffold while keeping feature-specific actions

### Unified Query Normalization

- Promote `RepositoryQueryParser` to `core-search` as `SearchQueryNormalizer`
- Parse qualifiers and return structured filters
- Model output as typed AST consumed by DAO parameters
- Prevent raw string leakage past the domain boundary

### Reusable RemoteMediator Base

- Extend `core-paging` with `KeysetRemoteMediator` abstraction
- Support pluggable pagination strategies (since-id, cursor, page number)
- Design remote key schema with list identity, next/prev keys, and timestamps
- Handle duplicate inserts via Room transactions

### PagingConfig Profiles

- Define shared paging profiles in `core-paging` (default, compact presets)
- Use profiles instead of hard-coded values
- Centralize tuning guidance (API limits, cache size)
- Expose DI overrides for specific modules with alternate presets

### Navigation DSL

- Provide helper builders in `navigation-api` for feature tabs and deep-link handlers
- Keep DSL thin and Hilt-friendly
- Avoid circular dependencies
- Add integration tests covering deep-link resolution across tabs

## Verification and QA

### MCP Logging Playbook

| Surface | Tags | MCP command snippets |
| --- | --- | --- |
| Repository tab snapshot | `RepositoryListScreen`, `RepositoryRemoteMediator` | `android-mcp logcat --tags RepositoryListScreen,RepositoryRemoteMediator --since 5m` |
| Global search | `SearchViewModel` analytics | `android-mcp logcat --tags SearchViewModel --format raw` |
| Navigation plumbing | `DeepLinkDispatcher`, `Navigation3FeatureRegistry` | `android-mcp logcat --tags DeepLinkDispatcher,Navigation3FeatureRegistry --since 2m` |

### QA Checklist

1. Repository tab default list
   - Launch app and ensure `Repositories` tab selected
   - Repository cards visible with proper data
   - No user data mixed in repository tab

2. Repository tab search
   - Focus search field and type query
   - Filtered list shows matching repositories
   - Analytics logs show correct endpoint

3. Tab switching
   - Switch between Users and Repositories tabs
   - Previously loaded lists persist without refetch
   - No unnecessary network requests

4. Repository detail navigation
   - Tap repository result to navigate to detail
   - Back navigation returns to list
   - Proper state preservation

## Testing Standards

### Unit Testing

- Test `PagingSourceProvider` implementations
- Verify query normalization logic
- Test remote mediator behavior with mocked data sources
- Validate error propagation in paging flows

### Integration Testing

- Test complete paging flows with Room database
- Verify search functionality with real data
- Test navigation integration between features
- Validate deep link handling

### UI Testing

- Use `createAndroidComposeRule` for Compose screens
- Test search interactions and results display
- Verify error states and loading indicators
- Test tab switching and state preservation