# Feature-Owned Navigation Implementation Patterns

This document outlines the standardized patterns for implementing navigation in feature modules, following the "Navigation 3" approach used in the repository feature.

## Overview

The navigation system follows a feature-owned contract pattern where each feature module defines its own deep links, destinations, and navigation handlers. This approach maintains clear ownership boundaries while enabling flexible cross-module navigation.

## Core Principles

### Feature-Owned Navigation Contracts

Each feature module is responsible for:
1. Defining its own deep links
2. Providing destination definitions
3. Handling deep link routing
4. Managing its own navigation tab configuration

### Shared Navigation API

The `navigation-api` module provides:
1. Generic composition locals (`LocalNavigateToDeepLink`, `LocalNavigateBack`)
2. Helper functions for common navigation patterns
3. Base interfaces for feature destination providers
4. Navigation tab registry mechanisms

## Implementation Components

### Deep Link Definitions

Each feature defines its deep links in a dedicated object:

```kotlin
object RepositoryDeepLinks {
    const val LIST = "app://repository/list"
    fun detail(id: String) = "app://repository/$id"
}
```

### Feature Destination Provider

Implement the `FeatureDestinationProvider` interface to define feature destinations:

```kotlin
@Singleton
class RepositoryFeatureDestinationProvider @Inject constructor() : FeatureDestinationProvider {
    override fun provideDestinations(): Set<NavigationDestination> = setOf(
        NavigationDestination(
            route = "repository/list",
            deepLinks = listOf(
                DeepLink(RepositoryDeepLinks.LIST)
            )
        )
    )
}
```

### Deep Link Handler

Implement the `FeatureDeepLinkHandler` to handle incoming deep links:

```kotlin
@Singleton
class RepositoryFeatureDeepLinkHandler @Inject constructor(
    private val navigator: Navigator
) : FeatureDeepLinkHandler {
    override fun handleDeepLink(uri: String): Boolean {
        return when {
            uri.startsWith("app://repository/") -> {
                navigator.navigateTo(uri)
                true
            }
            else -> false
        }
    }
}
```

### Navigation Tab Configuration

Define feature tabs using the navigation DSL:

```kotlin
val repositoryTab = navigationTab(
    label = "Repositories",
    icon = Icons.Default.List,
    route = "repository/list",
    order = 2
)
```

## Hilt Integration

Use Hilt multibindings to register feature components:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryNavigationModule {
    @Binds
    @IntoSet
    abstract fun bindDestinationProvider(
        provider: RepositoryFeatureDestinationProvider
    ): FeatureDestinationProvider

    @Binds
    @IntoSet
    abstract fun bindDeepLinkHandler(
        handler: RepositoryFeatureDeepLinkHandler
    ): FeatureDeepLinkHandler

    @Provides
    @IntoSet
    fun provideNavigationTab(): NavigationTab = repositoryTab
}
```

## App Module Integration

The app module orchestrates navigation by:
1. Collecting all registered `NavigationTab` instances
2. Collecting all `FeatureDestinationProvider` implementations
3. Collecting all `FeatureDeepLinkHandler` implementations
4. Setting up the navigation host with all destinations
5. Dispatching deep links through the `DeepLinkDispatcher`

### Saved State Persistence

- The app module applies the Kotlin serialization Gradle plugin so Navigation 3 can persist the back stack without reflection.
- Every `NavKey` hierarchy must be `@Serializable` and registered inside the shared `navSavedStateConfiguration()` helper before calling `rememberNavBackStack`.
- When new features add `NavKey` implementations, update the shared serializers module immediately to avoid runtime `SerializationException` crashes.

## Best Practices

1. **Own Your Navigation**: Each feature should define and own its navigation contracts
2. **Use Type-Safe Routes**: Prefer sealed classes or typed route builders over raw strings
3. **Handle Deep Links Locally**: Each feature should handle its own deep links
4. **Register Components Properly**: Use Hilt multibindings for automatic registration
5. **Maintain Order**: Use order properties to ensure consistent tab ordering
6. **Test Navigation**: Create integration tests for deep link handling and navigation flows
7. **Document Deep Links**: Maintain a central registry of all feature deep links
8. **Log Navigation Events**: Implement proper analytics for navigation events

## Verification

Use MCP tools to verify navigation implementation:

1. **Logcat Verification**:
   ```bash
   android-mcp logcat --tags DeepLinkDispatcher,Navigation3FeatureRegistry --since 2m
   ```

2. **UI Testing**:
   - Test tab navigation
   - Verify deep link handling
   - Check back stack behavior
   - Confirm proper state preservation

3. **Integration Testing**:
   - Test cross-feature navigation
   - Verify deep link resolution
   - Check navigation tab ordering
