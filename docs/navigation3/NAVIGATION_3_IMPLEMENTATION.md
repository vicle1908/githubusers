# Navigation 3 Implementation Summary

## Overview

Navigation 3 is fully integrated with a typed, deep link–driven approach. The app now uses Navigation3Controller + Navigation3Host with typed AppDestination and AppDeepLinks for all navigation.

## Key Changes

### 1. Dependencies

- Added Navigation 3 dependencies:
  - `androidx.navigation3:navigation3-ui`
  - `androidx.navigation3:navigation3-runtime`
  - `androidx.lifecycle:lifecycle-viewmodel-navigation3`
  - `kotlinx-serialization-core` and `kotlinx-serialization-json` for type-safe navigation

### 2. Navigation 3 Core Components

#### Nav3Destinations.kt

- Type-safe destinations using Kotlin Serialization
- Sealed interface with `@Serializable` annotations
- Supports UserList, UserDetail, and Settings screens
- Deep link helper functions
- Note: Search functionality is integrated within UserList screen

#### Navigation3Host (typed)

- Primary Navigation 3 host
- Uses Navigation3Controller and typed AppDestination via AppDeepLinks
- Start destination defined as a typed AppDestination

### 3. Navigation 3 Key Concepts

**Typed Destinations + Deep Links:**

```kotlin
```kotlin

```kotlin
// Build from typed destination
val deepLink = AppDeepLinks.build(AppDestination.UserDetail("octocat"))
controller.navigate(deepLink)

// Or navigate with typed directly
controller.navigate(AppDestination.UserDetail("octocat"))
```

**Navigation Options:**

- launchSingleTop, popUpTo, popUpToInclusive, etc.

**Typed Destinations in navigation-api:**

```kotlin
```kotlin

```kotlin
// Build from typed destination
val deepLink = AppDeepLinks.build(AppDestination.UserDetail("octocat"))
controller.navigate(deepLink)

// Or navigate with typed directly
controller.navigate(AppDestination.UserDetail("octocat"))
// Build from typed destination
val deepLink = AppDeepLinks.build(AppDestination.UserDetail("octocat"))
controller.navigate(deepLink)

// Or navigate with typed directly
controller.navigate(AppDestination.UserDetail("octocat"))
sealed interface AppDestination {
    @Serializable data object UserList : AppDestination
    @Serializable data class UserDetail(val username: String) : AppDestination
    @Serializable data class Search(val query: String? = null) : AppDestination
    @Serializable data object Settings : AppDestination
}
```

### 4. Integration Points

- **MainActivity**: Uses typed `Navigation3Host` with `Navigation3Controller`
- **ViewModels**: Can inject `Navigation3Controller` directly for navigation
- **Deep Links**: Use AppDeepLinks and feature DeepLinkHandler contributions via Hilt into DefaultDestinationResolver

### 5. Benefits of Navigation 3

1. **Direct Control**: Full control over the navigation backstack
1. **Type Safety**: Using Kotlin Serialization for compile-time safety
1. **Simplicity**: No complex navigation graphs or XML files
1. **State Preservation**: Automatic state saving and restoration
1. **Flexibility**: Easy to implement custom navigation patterns

### 6. Migration from Navigation 2

Fully migrated to typed Navigation 3. Legacy NavigationManager and SimpleNav3Host have been removed.

## Usage Examples

**Navigate to User Detail (typed):**

```kotlin
```kotlin

```kotlin
// Build from typed destination
val deepLink = AppDeepLinks.build(AppDestination.UserDetail("octocat"))
controller.navigate(deepLink)

// Or navigate with typed directly
controller.navigate(AppDestination.UserDetail("octocat"))
// Build from typed destination
val deepLink = AppDeepLinks.build(AppDestination.UserDetail("octocat"))
controller.navigate(deepLink)

// Or navigate with typed directly
controller.navigate(AppDestination.UserDetail("octocat"))
sealed interface AppDestination {
    @Serializable data object UserList : AppDestination
    @Serializable data class UserDetail(val username: String) : AppDestination
    @Serializable data class Search(val query: String? = null) : AppDestination
    @Serializable data object Settings : AppDestination
}
// Build from typed destination
val deepLink = AppDeepLinks.build(AppDestination.UserDetail("octocat"))
controller.navigate(deepLink)

// Or navigate with typed directly
controller.navigate(AppDestination.UserDetail("octocat"))
sealed interface AppDestination {
    @Serializable data object UserList : AppDestination
    @Serializable data class UserDetail(val username: String) : AppDestination
    @Serializable data class Search(val query: String? = null) : AppDestination
    @Serializable data object Settings : AppDestination
}
controller.navigate(AppDestination.UserDetail("octocat"))
```

**Navigate via deep link:**

```kotlin
```kotlin

```kotlin
// Build from typed destination
val deepLink = AppDeepLinks.build(AppDestination.UserDetail("octocat"))
controller.navigate(deepLink)

// Or navigate with typed directly
controller.navigate(AppDestination.UserDetail("octocat"))
// Build from typed destination
val deepLink = AppDeepLinks.build(AppDestination.UserDetail("octocat"))
controller.navigate(deepLink)

// Or navigate with typed directly
controller.navigate(AppDestination.UserDetail("octocat"))
sealed interface AppDestination {
    @Serializable data object UserList : AppDestination
    @Serializable data class UserDetail(val username: String) : AppDestination
    @Serializable data class Search(val query: String? = null) : AppDestination
    @Serializable data object Settings : AppDestination
}
// Build from typed destination
val deepLink = AppDeepLinks.build(AppDestination.UserDetail("octocat"))
controller.navigate(deepLink)

// Or navigate with typed directly
controller.navigate(AppDestination.UserDetail("octocat"))
sealed interface AppDestination {
    @Serializable data object UserList : AppDestination
    @Serializable data class UserDetail(val username: String) : AppDestination
    @Serializable data class Search(val query: String? = null) : AppDestination
    @Serializable data object Settings : AppDestination
}
controller.navigate(AppDestination.UserDetail("octocat"))
// Build from typed destination
val deepLink = AppDeepLinks.build(AppDestination.UserDetail("octocat"))
controller.navigate(deepLink)

// Or navigate with typed directly
controller.navigate(AppDestination.UserDetail("octocat"))
sealed interface AppDestination {
    @Serializable data object UserList : AppDestination
    @Serializable data class UserDetail(val username: String) : AppDestination
    @Serializable data class Search(val query: String? = null) : AppDestination
    @Serializable data object Settings : AppDestination
}
controller.navigate(AppDestination.UserDetail("octocat"))
controller.navigate(AppDeepLinks.build(AppDestination.UserDetail("octocat")))
```

**Pop to start:**

```kotlin
```kotlin

```kotlin
// Build from typed destination
val deepLink = AppDeepLinks.build(AppDestination.UserDetail("octocat"))
controller.navigate(deepLink)

// Or navigate with typed directly
controller.navigate(AppDestination.UserDetail("octocat"))
// Build from typed destination
val deepLink = AppDeepLinks.build(AppDestination.UserDetail("octocat"))
controller.navigate(deepLink)

// Or navigate with typed directly
controller.navigate(AppDestination.UserDetail("octocat"))
sealed interface AppDestination {
    @Serializable data object UserList : AppDestination
    @Serializable data class UserDetail(val username: String) : AppDestination
    @Serializable data class Search(val query: String? = null) : AppDestination
    @Serializable data object Settings : AppDestination
}
// Build from typed destination
val deepLink = AppDeepLinks.build(AppDestination.UserDetail("octocat"))
controller.navigate(deepLink)

// Or navigate with typed directly
controller.navigate(AppDestination.UserDetail("octocat"))
sealed interface AppDestination {
    @Serializable data object UserList : AppDestination
    @Serializable data class UserDetail(val username: String) : AppDestination
    @Serializable data class Search(val query: String? = null) : AppDestination
    @Serializable data object Settings : AppDestination
}
controller.navigate(AppDestination.UserDetail("octocat"))
// Build from typed destination
val deepLink = AppDeepLinks.build(AppDestination.UserDetail("octocat"))
controller.navigate(deepLink)

// Or navigate with typed directly
controller.navigate(AppDestination.UserDetail("octocat"))
sealed interface AppDestination {
    @Serializable data object UserList : AppDestination
    @Serializable data class UserDetail(val username: String) : AppDestination
    @Serializable data class Search(val query: String? = null) : AppDestination
    @Serializable data object Settings : AppDestination
}
controller.navigate(AppDestination.UserDetail("octocat"))
controller.navigate(AppDeepLinks.build(AppDestination.UserDetail("octocat")))
// Build from typed destination
val deepLink = AppDeepLinks.build(AppDestination.UserDetail("octocat"))
controller.navigate(deepLink)

// Or navigate with typed directly
controller.navigate(AppDestination.UserDetail("octocat"))
sealed interface AppDestination {
    @Serializable data object UserList : AppDestination
    @Serializable data class UserDetail(val username: String) : AppDestination
    @Serializable data class Search(val query: String? = null) : AppDestination
    @Serializable data object Settings : AppDestination
}
controller.navigate(AppDestination.UserDetail("octocat"))
controller.navigate(AppDeepLinks.build(AppDestination.UserDetail("octocat")))
controller.popBackStackTo(AppDeepLinks.build(AppDestination.UserList), inclusive = false)
```

## Persistence (Minimal, Versioned)

We persist a compact, versioned representation of the back stack as deep links only.

- Schema: `{ schemaVersion, timestamp, appVersion, navGraphVersion, entries: [deepLink] }`
- Storage: SharedPreferences (Android) via `SharedPrefsBackStackStore`
- Guardrails:
  - Feature flag (kill switch) through `PersistenceConfig.enabled`
  - TTL (default 7 days)
  - Caps: max entries (default 5), total payload size (~20KB)
  - Version checks: `schemaVersion`, `navGraphVersion`, optional `appVersion` drop-on-mismatch
  - Privacy: only deep links; no PII/secrets; args re-derived from deep link query params at runtime
- Restore flow: `Navigation3Host` calls `controller.restoreFromPersistence()` before navigating to the start destination.
- Failure handling: on any check failure, drop persisted state and continue from start destination.

### Telemetry (Placeholders)

- Hooks exist at persistence read/write points for future metrics/logs (success/failure, sizes, latency).
- Logging should be rate-limited and structured.

### Kill Switch

- Controlled via DI-provided `PersistenceConfig.enabled`. If disabled, controller skips reads/writes.

## Next Steps (Optional)

1. Add transition animations between screens
1. Implement the Settings screen
1. Add navigation testing (restore success, TTL expiry, version mismatch, toggles)
1. Consider adding adaptive layouts when the library becomes stable
1. Wire telemetry counters/timers and a runtime-configurable kill switch if infra exists
