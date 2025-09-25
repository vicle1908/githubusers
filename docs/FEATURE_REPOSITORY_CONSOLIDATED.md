# Feature Repository - Complete Documentation

## Overview

This document provides complete documentation for the `feature-repository` module, consolidating all information previously found in separate files. It covers the module's architecture, implementation details, testing procedures, and verification playbooks.

The repository feature demonstrates good practices by utilizing the core modules:
- It uses `DefaultPagingSourceProvider` from `core-paging` for reactive paging
- It leverages `PagingSearchController` from `core-search` for search state management
- It properly implements Clean Architecture with clear separation of presentation, domain, and data layers

## Module Structure

```
feature-repository/
├── src/
│   ├── main/
│   │   ├── java/com/example/githubusers/feature/repository/
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   │   ├── dao/
│   │   │   │   │   │   └── RepositoryDao.kt
│   │   │   │   │   ├── entity/
│   │   │   │   │   │   └── RepositoryEntity.kt
│   │   │   │   │   ├── Converters.kt
│   │   │   │   │   └── RepositoryDatabase.kt
│   │   │   │   ├── mapper/
│   │   │   │   │   ├── RepositoryMappers.kt
│   │   │   │   ├── paging/
│   │   │   │   │   └── RepositoryRemoteMediator.kt
│   │   │   │   ├── remote/
│   │   │   │   │   ├── dto/
│   │   │   │   │   │   └── RepositoryDto.kt
│   │   │   │   │   └── RepositoryApiService.kt
│   │   │   │   ├── repository/
│   │   │   │   │   └── RepositoryRepositoryImpl.kt
│   │   │   │   └── RepositoryQueryParser.kt
│   │   │   ├── di/
│   │   │   │   └── RepositoryDataModule.kt
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   │   ├── Repository.kt
│   │   │   │   │   └── RepositoryDetail.kt
│   │   │   │   ├── repository/
│   │   │   │   │   └── RepositoryRepository.kt
│   │   │   │   ├── usecase/
│   │   │   │   │   └── ObserveRepositorySearchUseCase.kt
│   │   │   │   └── RepositorySearchDefaults.kt
│   │   │   ├── navigation/
│   │   │   │   ├── RepositoryDeepLinks.kt
│   │   │   │   ├── RepositoryFeatureDeepLinkHandler.kt
│   │   │   │   ├── RepositoryFeatureDestinationProvider.kt
│   │   │   │   ├── RepositoryNavKey.kt
│   │   │   │   └── di/
│   │   │   │       └── RepositoryNavigationModule.kt
│   │   │   └── presentation/
│   │   │       ├── intent/
│   │   │       │   └── RepositoryListIntent.kt
│   │   │       ├── navigation/
│   │   │       │   └── RepositoryListNavigator.kt
│   │   │       ├── state/
│   │   │       │   └── RepositoryListState.kt
│   │   │       ├── ui/
│   │   │       │   ├── RepositoryDetailContent.kt
│   │   │       │   ├── RepositoryDetailScreen.kt
│   │   │       │   ├── RepositoryItem.kt
│   │   │       │   ├── RepositoryListContent.kt
│   │   │       │   └── RepositoryListScreen.kt
│   │   │       └── viewmodel/
│   │   │           └── RepositoryListViewModel.kt
│   └── test/
│       └── java/com/example/githubusers/feature/repository/
│           ├── data/
│           │   └── repository/
│           │       └── RepositoryRepositoryImplTest.kt
│           └── domain/
│               └── usecase/
│                   └── ObserveRepositorySearchUseCaseTest.kt
└── build.gradle.kts
```

## Implementation Summary

### Architecture Principles

The feature-repository module follows Clean Architecture principles with a clear separation of concerns across three layers:

1. **Presentation Layer**: Contains ViewModels, UI components, and navigation logic
2. **Domain Layer**: Contains business logic, use cases, and domain models
3. **Data Layer**: Contains data sources, repositories, and data mapping logic

### Core Module Usage

The repository feature correctly uses core modules:
- Uses `DefaultPagingSourceProvider` with a `pagerFactory` that consumes normalized queries
- Leverages `PagingSearchController` for search state management and UI state exposure
- Implements proper query transformation with `SearchQueryNormalizer`

### ViewModel Implementation

The `RepositoryListViewModel` demonstrates the proper pattern for integrating core modules:

```kotlin
@HiltViewModel
class RepositoryListViewModel @Inject constructor(
    observeRepositorySearchUseCase: ObserveRepositorySearchUseCase
) : ViewModel() {

    private val pagingProvider = DefaultPagingSourceProvider(
        scope = viewModelScope,
        initialQuery = RepositorySearchDefaults.DEFAULT_QUERY,
        pagerFactory = { rawQuery ->
            val normalized = SearchQueryNormalizer.normalize(rawQuery)
            observeRepositorySearchUseCase(normalized)
        },
        debounceMillis = 200L
    )

    private val searchController = PagingSearchController(
        scope = viewModelScope,
        queryState = pagingProvider.query,
        resultsSource = pagingProvider.pagingData,
        updateQuery = pagingProvider::updateQuery,
        queryTransformer = { query -> query.trim().ifBlank { RepositorySearchDefaults.DEFAULT_QUERY } }
    )

    val searchState: StateFlow<SearchUiState> = searchController.uiState
    val repositories: Flow<PagingData<Repository>> = searchController.results

    fun process(intent: RepositoryListIntent) {
        when (intent) {
            is RepositoryListIntent.SearchQueryChanged -> {
                searchController.submitQuery(intent.query)
            }
            RepositoryListIntent.Retry -> {
                searchController.retry()
            }
            // ... other intents
        }
    }
}
```

### Domain Layer Implementation

The domain layer follows the use case pattern with a clear separation of concerns:

```kotlin
class ObserveRepositorySearchUseCase @Inject constructor(
    private val repository: RepositoryRepository
) {
    operator fun invoke(query: SearchQueryNormalizer.NormalizedSearchQuery): Flow<PagingData<Repository>> =
        repository.observeRepositories(query)
}
```

### Data Layer Implementation

The data layer properly implements the repository pattern with both local and remote data sources:

```kotlin
@OptIn(ExperimentalPagingApi::class)
@Singleton
class RepositoryRepositoryImpl @Inject constructor(
    private val database: RepositoryDatabase,
    private val apiService: RepositoryApiService
) : RepositoryRepository {

    override fun observeRepositories(query: SearchQueryNormalizer.NormalizedSearchQuery): Flow<PagingData<Repository>> {
        val dao = database.repositoryDao()
        val parsedQuery = RepositoryQueryParser.parse(
            normalized = query,
            fallbackQuery = RepositorySearchDefaults.DEFAULT_QUERY
        )
        val pagingConfig: PagingConfig = PagingProfiles.defaultList()

        return Pager(
            config = pagingConfig,
            remoteMediator = RepositoryRemoteMediator(
                apiService = apiService,
                database = database,
                query = parsedQuery.networkQuery
            ),
            pagingSourceFactory = {
                dao.searchPagingSource(
                    language = parsedQuery.language,
                    plainQuery = parsedQuery.plainQuery
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomain() }
        }
    }

    override suspend fun getRepository(owner: String, name: String): Result<RepositoryDetail?> =
        apiService.getRepository(owner, name)
            .map { it?.toDomain() }
}
```

## Key Components

### Presentation Layer

#### RepositoryListScreen
Located at: `feature-repository/src/main/java/com/example/githubusers/feature/repository/presentation/ui/RepositoryListScreen.kt`

The main screen displaying a list of repositories with search functionality. Uses `SearchableListScaffold` for consistent UI.

Key features:
- Search bar with real-time filtering
- Paging support for large datasets
- Error and loading states
- Pull-to-refresh capability

#### RepositoryDetailScreen
Located at: `feature-repository/src/main/java/com/example/githubusers/feature/repository/presentation/ui/RepositoryDetailScreen.kt`

Displays detailed information about a specific repository.

### Domain Layer

#### Use Cases
Located at: `feature-repository/src/main/java/com/example/githubusers/feature/repository/domain/usecase/`

1. `ObserveRepositorySearchUseCase` - Observes repositories with search capabilities

All use cases are annotated with `@Reusable` and injected via constructor injection.

### Data Layer

#### Repository
Located at: `feature-repository/src/main/java/com/example/githubusers/feature/repository/data/repository/`

`RepositoryRepositoryImpl` implements the repository interface, providing data access through both local and remote sources.

#### Database
Located at: `feature-repository/src/main/java/com/example/githubusers/feature/repository/data/local/`

Uses Room database with the following DAOs:
1. `RepositoryDao` - Handles repository data

#### Remote Data Sources
Located at: `feature-repository/src/main/java/com/example/githubusers/feature/repository/data/remote/`

Handles API communication with GitHub's repository APIs.

## DI Graph

### Hilt Modules

#### RepositoryNavigationModule

Located at: `feature-repository/src/main/java/com/example/githubusers/feature/repository/navigation/di/RepositoryNavigationModule.kt`

This module provides multibindings for feature-owned navigation components:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryNavigationModule {

    @Binds
    @IntoSet
    abstract fun bindRepositoryFeatureDestinationProvider(
        provider: RepositoryFeatureDestinationProvider
    ): FeatureDestinationProvider

    @Binds
    @IntoSet
    abstract fun bindRepositoryFeatureDeepLinkHandler(
        handler: RepositoryFeatureDeepLinkHandler
    ): FeatureDeepLinkHandler
}
```

### Key Dependencies

#### FeatureDestinationProvider

Implementation: `RepositoryFeatureDestinationProvider`
Scope: Singleton

Responsible for providing the feature's destination definitions to the navigation system.

#### FeatureDeepLinkHandler

Implementation: `RepositoryFeatureDeepLinkHandler`
Scope: Singleton

Handles deep link routing for the repository feature, parsing URIs and navigating to appropriate destinations.

### ViewModel Bindings

#### RepositoryListViewModel

Scope: ViewModel

Injected dependencies:
- `ObserveRepositorySearchUseCase`
- `CoroutineDispatcher` (IO)

### Use Case Bindings

All use cases are annotated with `@Reusable` scope:

- `ObserveRepositorySearchUseCase`

### Navigation Components

#### RepositoryDeepLinks

Object containing helper functions for generating deep link URIs:
- `list()`: URI for repository list screen
- `detail(owner: String, name: String)`: URI for repository detail screen

#### RepositoryListNavigator

Interface for navigation actions from the repository list screen:
- `openRepositoryDetails(owner: String, name: String)`: Navigate to repository detail
- `openSearch(initialQuery: String)`: Navigate to search screen

## Paging and Search Implementation

### PagingSourceProvider

The `PagingSourceProvider` interface defines the contract for components that transform a query into a paged data stream:

```kotlin
interface PagingSourceProvider<in Q, T : Any> {
    /**
     * Updates the provider with a new query or parameter.
     */
    fun updateQuery(query: Q)

    /**
     * Exposes the current query so UI can render it directly.
     */
    val query: StateFlow<Q>

    /**
     * The resulting stream of paged data, which reacts to query changes.
     */
    val pagingData: Flow<PagingData<T>>
}
```

### DefaultPagingSourceProvider

The default implementation handles the reactive logic using `StateFlow` and `flatMapLatest`:

```kotlin
class DefaultPagingSourceProvider<Q, T : Any>(
    private val scope: CoroutineScope,
    initialQuery: Q,
    private val pagerFactory: (Q) -> Flow<PagingData<T>>,
    private val debounceMillis: Long = 300L
) : PagingSourceProvider<Q, T> {

    private val queryFlow = MutableStateFlow(initialQuery)

    override val query: StateFlow<Q> = queryFlow.asStateFlow()

    override fun updateQuery(query: Q) {
        if (queryFlow.value == query) return
        queryFlow.value = query
    }

    override val pagingData: Flow<PagingData<T>> = queryFlow
        .debounce(debounceMillis)
        .flatMapLatest { query ->
            pagerFactory(query)
        }
        .cachedIn(scope)
}
```

### PagingSearchController

The `PagingSearchController` wraps the provider and handles reusable concerns:
- Trimming/normalizing queries
- Exposing reactive UI state
- Dispatching retries
- Surfacing errors

## Implementation Patterns

### Composition Over Inheritance

To promote code reusability and flexibility, this implementation favors composition over inheritance, leveraging Kotlin's powerful delegation feature. Instead of creating rigid base classes, we use interfaces and delegate implementations using the `by` keyword.

This approach offers several advantages:
- **Flexibility**: Components can be easily swapped at runtime.
- **Reduced Boilerplate**: Kotlin's `by` keyword handles the delegation automatically.
- **Clearer Intent**: The "has-a" relationship of composition is often a more accurate model for the relationships between our components.

### Reactive Approach

The core idea is that the ViewModel owns the "input" state (the search query), and the delegate provider consumes that input to produce the "output" `PagingData` flow.

1. **Redefine the Interface**: Create a focused provider that accepts a query (`Q`) and produces `PagingData<T>`. The ViewModel is responsible for calling `updateQuery`, and the UI is responsible for collecting `pagingData`.

2. **Create a Default Implementation**: This implementation contains the reactive logic using `StateFlow` and `flatMapLatest`.

3. **Integrate into the ViewModel**: Rather than having ViewModels implement the provider interface, we create an instance and keep it as a collaborator. This keeps concerns separated and allows multiple features to reuse the same delegate.

### Feature-Owned Navigation Contracts

Navigation remains feature-driven: each feature module defines its own deep links, destination providers, and navigation tabs. The shared `navigation-api` module only exposes generic composition locals (`LocalNavigateToDeepLink`, `LocalNavigateBack`) and helpers, while the app module orchestrates by reading the injected `NavigationTab` set and dispatching deep links through `DeepLinkDispatcher`. To keep ownership clear:

- Each feature declares a `*DeepLinks` object alongside its `FeatureDestinationProvider` and `FeatureDeepLinkHandler`.
- Feature modules, not the app, build deep link URIs—for example, `RepositoryDeepLinks.detail(owner, name)` is used when routing to a repository detail.
- The app derives its initial entry and tab metadata from the injected `NavigationTab` list rather than hard-coded URIs.

## Testing Entry Points

### Unit Tests
Located at: `feature-repository/src/test/`

Run with: `./gradlew :feature-repository:testDebugUnitTest`

Key test areas:
- ViewModels (`RepositoryListViewModelTest`)
- Use Cases (`ObserveRepositorySearchUseCaseTest`)
- Repository implementations (`RepositoryRepositoryImplTest`)
- Data mappers and normalizers

### UI Tests
Currently, there are no UI tests for the repository feature. Consider adding UI tests to verify:
- Screen composition and rendering
- User interactions (search, tap, pull-to-refresh)
- State management (loading, error, empty states)
- Navigation flows

## Tool Usage

### Building
```bash
# Assemble debug APK
./gradlew :feature-repository:assembleDebug

# Run lint checks
./gradlew :feature-repository:lint

# Run Detekt
./gradlew :feature-repository:detekt

# Run KtLint
./gradlew :feature-repository:ktlintCheck
```

### Testing
```bash
# Run unit tests
./gradlew :feature-repository:testDebugUnitTest

# Run instrumentation tests
./gradlew :feature-repository:connectedDebugAndroidTest

# Run all tests
./gradlew :feature-repository:test
```

## Common Development Tasks

### Adding a New Use Case
1. Create use case in appropriate domain/usecase package
2. Annotate with `@Reusable`
3. Add constructor injection for dependencies
4. Write unit tests
5. Update ViewModel to use the new use case

### Adding a New Screen
1. Create screen composable in presentation/ui package
2. Create ViewModel with Hilt injection
3. Add navigation destination in `RepositoryFeatureDestinationProvider`
4. Add deep link helper in `RepositoryDeepLinks`
5. Update `RepositoryNavigationModule` if needed
6. Write UI and integration tests

### Modifying Data Models
1. Update Room entities if needed
2. Update DAOs and repositories
3. Update mappers and normalizers
4. Update use cases and ViewModels
5. Update tests
6. Update UI components
7. Verify database migrations if schema changed

## Code Quality

### Static Analysis
- Detekt configuration: `config/detekt/detekt.yml`
- KtLint configuration: `.editorconfig`

### Code Coverage
Target: 80% line coverage for unit tests
Focus areas:
- ViewModels
- Use cases
- Repositories
- Data mappers

### Documentation
- KDoc for public APIs
- Inline comments for complex logic
- README updates for significant changes
- Architecture decision records for major changes

## Troubleshooting

### Build Issues
1. Check for version catalog mismatches
2. Verify Hilt annotations
3. Ensure all dependencies are properly declared
4. Run `./gradlew :feature-repository:clean` and rebuild

### Runtime Issues
1. Check logcat for errors
2. Verify dependency injection
3. Check network connectivity
4. Validate Room database operations

### Test Failures
1. Check for test-specific configurations
2. Verify test data setup
3. Ensure mocks are properly configured
4. Check for timing issues in UI tests

## Standardization Roadmap

The repository feature already demonstrates several standardized patterns that should be adopted by other features:

1. **Shared Searchable List Scaffold**
   - Build a `core-ui` composable that owns search text entry, progress/error display, and hands paging content to a slot.
   - Migrate screens to this scaffold; keep feature-specific top-bar actions via slots.

2. **Unified Query Normalisation**
   - Promote `RepositoryQueryParser` into `core-search` as a generic `SearchQueryNormalizer` that can parse qualifiers and return structured filters.
   - Model the output as a typed AST (filters, ranges, sort) consumed by DAO parameters or safe query builders.

3. **Reusable RemoteMediator Base**
   - Extend `core-paging` with a `KeysetRemoteMediator` abstraction to cover the GitHub "since" pagination pattern.
   - Support pluggable pagination strategies (e.g., since-id, cursor, page number).

4. **PagingConfig Profiles**
   - Define shared paging profiles (default page size, prefetch distance, initial load size) in `core-paging`.
   - Update features to request the profile (e.g., `PagingProfiles.defaultList()`) instead of hard-coding values.

5. **Navigation DSL**
   - Provide helper builders in `navigation-api` for feature tabs and deep-link handlers to remove boilerplate duplication.
   - Adopt the DSL in modules, verifying that tab order, icons, and deep-link URIs emit the same analytics metadata.

## Implementation Timeline

### Completed Work
1. ✅ **Core Infrastructure Setup**
   - ✅ Created `core-paging` module with generic `BaseRemoteMediator` and `BasePagingSource`
   - ✅ Created `core-search` module with reusable `PagingSearchController` and `SearchUiState`

2. ✅ **Feature Implementation**
   - ✅ Implemented data layer with `RepositoryDto`, `RepositoryEntity`, `RepositoryDao`, and `RepositoryDatabase`
   - ✅ Implemented paging with `RepositoryRemoteMediator` extending `BaseRemoteMediator`
   - ✅ Implemented search by composing `DefaultPagingSourceProvider` with `PagingSearchController`
   - ✅ Created UI components (`RepositoryListScreen`, `RepositoryListItem`)
   - ✅ Added MVI `RepositoryListViewModel` with intents and UI route
   - ✅ Implemented navigation with `RepositoryFeatureDestinationProvider` and `RepositoryFeatureDeepLinkHandler`

3. ✅ **Standardization**
   - ✅ Shared Searchable List Scaffold implemented in `core-ui`
   - ✅ Unified Query Normalization with `SearchQueryNormalizer` in `core-search`
   - ✅ Reusable RemoteMediator Base with `KeysetRemoteMediator` in `core-paging`
   - ✅ PagingConfig Profiles with `PagingProfiles` in `core-paging`
   - ✅ Navigation DSL helpers in `navigation-api`

### Future Work
1. **Testing and Quality Assurance**
   - Implement comprehensive test coverage
   - Perform quality checks and code reviews
   - Conduct manual QA and validation

2. **Documentation and Knowledge Sharing**
   - Update project documentation
   - Create training materials
   - Conduct knowledge transfer sessions

## Success Metrics

1. **Code Quality**:
   - Zero Detekt and KtLint errors across all modules
   - 90%+ code coverage for core modules
   - Consistent code style and architecture adherence

2. **Performance**:
   - Search response time < 500ms for cached results
   - Paging load time < 1s for initial page
   - Memory usage within acceptable limits
   - Smooth scrolling with 60fps target

3. **Maintainability**:
   - Reduced code duplication between features
   - Clear module boundaries and dependencies
   - Comprehensive documentation and examples
   - Easy refactoring with loose coupling

4. **Developer Experience**:
   - Simplified feature implementation process
   - Reduced time to implement new features
   - Improved onboarding experience for new developers
   - Consistent patterns and practices across features

## Risks and Mitigation Strategies

### Risk 1: Breaking Changes
**Description**: Refactoring may introduce breaking changes that affect existing functionality
**Mitigation**: 
- Implement changes incrementally with feature flags
- Maintain backward compatibility during transition
- Thoroughly test all user flows before deployment

### Risk 2: Performance Degradation
**Description**: New abstractions may introduce performance overhead
**Mitigation**:
- Profile performance before and after changes
- Optimize critical paths and frequently used components
- Implement caching and lazy loading where appropriate

### Risk 3: Adoption Resistance
**Description**: Team members may resist adopting new patterns and standards
**Mitigation**:
- Provide comprehensive training and documentation
- Demonstrate benefits through examples and metrics
- Provide support during transition period

---

*This document serves as the unified source of truth for the feature-repository module documentation and implementation plan. All previous fragmented documents have been consolidated into this single comprehensive resource.*