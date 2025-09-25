# Paging and Search Implementation Patterns

This document outlines the standardized patterns for implementing paging and search functionality in feature modules, based on the repository feature implementation.

## Overview

The implementation follows a composition-over-inheritance approach using Kotlin delegates to create reusable components for paging and search functionality. This pattern has been successfully implemented in both `feature-users` and `feature-repository` modules.

## Core Components

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
class DefaultPagingSourceProvider<in Q, T : Any>(
    private val scope: CoroutineScope,
    initialQuery: Q,
    // The pagerFactory is a lambda that creates the Flow<PagingData<T>>.
    // It's provided by the layer that knows about the data source (e.g., repository).
    private val pagerFactory: (Q) -> Flow<PagingData<T>>
) : PagingSourceProvider<Q, T> {

    private val queryFlow = MutableStateFlow(initialQuery)

    override val query: StateFlow<Q> = queryFlow.asStateFlow()

    override fun updateQuery(query: Q) {
        queryFlow.value = query
    }

    override val pagingData: Flow<PagingData<T>> = queryFlow
        // Optional: Debounce to avoid spamming the network/DB during rapid typing.
        .debounce(300L)
        .flatMapLatest { query ->
            pagerFactory(query)
        }
        .cachedIn(scope) // Crucial for Paging 3 to work correctly.
}
```

### PagingSearchController

The `PagingSearchController` wraps the provider and handles reusable concerns:

- Trimming/normalizing queries
- Exposing reactive UI state
- Dispatching retries
- Surfacing errors

## ViewModel Integration

ViewModels integrate these components by creating instances and delegating functionality:

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

## Key Benefits

1. **Reusability**: Core paging and search logic is encapsulated in reusable components
2. **Separation of Concerns**: ViewModels coordinate but don't implement paging/search logic
3. **Reactive Design**: Properly handles query changes and data flow with Kotlin Flows
4. **Testability**: Each component can be tested independently
5. **Flexibility**: Easy to swap implementations or customize behavior

## Implementation Steps

1. **Create the PagingSourceProvider**: Define the interface for your specific query and data types
2. **Implement DefaultPagingSourceProvider**: Use the provided implementation or customize as needed
3. **Integrate with Domain Layer**: Ensure your repository or use case exposes `Flow<PagingData<T>>`
4. **Create ViewModel**: Instantiate the provider and controller in your ViewModel
5. **Connect to UI**: Expose the necessary state and event handlers to your Compose UI

## Best Practices

1. **Use cachedIn**: Always apply `cachedIn(viewModelScope)` to the pagingData flow
2. **Debounce Queries**: Consider debouncing rapid query changes to avoid excessive network requests
3. **Handle Errors**: Properly propagate and display errors from the paging source
4. **Normalize Queries**: Process and normalize user input before submitting queries
5. **Test Thoroughly**: Create unit tests for each component and integration tests for the full flow