# Distributed Destinations Migration - COMPLETED ✅

## Overview

This document outlines the **completed migration** from the centralized `AppDestination` approach to a distributed, feature-owned destination architecture in the custom Navigation 3 system.

### 🎉 MIGRATION STATUS: COMPLETED SUCCESSFULLY

## Current Architecture Analysis

### New Distributed Architecture Components

1. **Navigation3Controller** - Custom navigation controller interface (updated to use NavCommand)
1. **Navigation3Host** - Custom Compose host for rendering screens (updated to use deep links)
1. **FeatureDestination** - Feature-owned destination interfaces (replaces AppDestination)
1. **FeatureApi** - Type-safe cross-feature navigation interfaces
1. **NavCommand** - Navigation command interface for type-safe navigation
1. **DeepLinkHandler** - Feature-specific deep link handlers via Hilt multibindings (updated)
1. **SecureDeepLinkHandler** - Security validation and telemetry
1. **DefaultDestinationResolver** - Resolves deep links to destinations using handlers (updated)

### New Distributed Flow

```text
```text

Navigation Request → Navigation3Controller → SecureDeepLinkHandler →
DeepLinkSecurityValidator → DefaultDestinationResolver → DeepLinkHandler →
FeatureDestination → Navigation3Host → UI Screen

```

## Migration Results: Complete Migration - COMPLETED ✅

We have successfully implemented a **complete migration** to distributed destinations, removing all legacy centralized code:

### Phase 1: Foundation - COMPLETED ✅

**Goal**: Create infrastructure for distributed destinations and prepare for complete migration.

**Deliverables**:

- ✅ `FeatureDestination` interface for feature-owned destinations
- ✅ `FeatureApi` interface for type-safe cross-feature navigation
- ✅ `FeatureNavigationModule` base class for migration suppor
- ✅ `NavCommand` interface for navigation commands
- ✅ Comprehensive migration strategy documentation

### Phase 2: Proof-of-Concept - COMPLETED ✅

**Goal**: Migrate users feature to validate distributed destinations approach.

**Results**:

- ✅ Created `UserFeatureApi` interface and implementation
- ✅ Updated `UserDeepLinkHandler` to use distributed destinations
- ✅ Updated `Navigation3Controller` to work with `NavCommand`
- ✅ Updated `DefaultDestinationResolver` for new `DeepLinkResult` forma
- ✅ Updated `Navigation3Actions` to use `NavCommand`
- ✅ Successfully validated the distributed approach

### Phase 3: Expansion - COMPLETED ✅

**Goal**: Migrate remaining features using proven patterns.

**Results**:

- ✅ Migrated search feature to use distributed destinations
- ✅ Created `SearchFeatureApi` interface and implementation
- ✅ Updated `SearchDeepLinkHandler` to use distributed destinations
- ✅ Updated `MainActivity` to use deep links instead of `AppDestination`
- ✅ Removed `AppDestination` overload from `Navigation3Host`
- ✅ Updated all navigation components and DI modules

### Phase 4: Cleanup - COMPLETED ✅

**Goal**: Remove all legacy centralized code and complete the migration.

**Results**:

- ✅ Removed `AppDestination.kt` file from navigation-api module
- ✅ Removed `AppDeepLinks.kt` file from navigation-api module
- ✅ Verified all modules compile successfully after cleanup
- ✅ Complete removal of centralized destination types
- ✅ All features now use distributed destinations exclusively

## 🎉 Migration Success Summary

### ✅ **COMPLETED ACHIEVEMENTS**

1. **Feature Independence**: Each feature now owns its destinations and navigation
2. **Type Safety**: Maintained through `FeatureApi` contracts
3. **No Centralized Dependencies**: Features don't depend on shared destination types
4. **Clean Architecture**: Clear separation of concerns
5. **Scalability**: Easy to add new features with their own destinations
6. **Maintainability**: Changes to one feature don't affect others

### 🏗️ **New Architecture**

**Before (Centralized)**:

```text

```text
Navigation Request → Navigation3Controller → SecureDeepLinkHandler →
DeepLinkSecurityValidator → DefaultDestinationResolver → DeepLinkHandler →
FeatureDestination → Navigation3Host → UI Screen
AppDestination (centralized) → DeepLinkHandler → Navigation3Controller
```

**After (Distributed)**:

```text
```text

Navigation Request → Navigation3Controller → SecureDeepLinkHandler →
DeepLinkSecurityValidator → DefaultDestinationResolver → DeepLinkHandler →
FeatureDestination → Navigation3Host → UI Screen
AppDestination (centralized) → DeepLinkHandler → Navigation3Controller
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller

```

### 📱 **Feature Ownership**

- **Users Feature**: Owns `UserDestination`, `UserFeatureApi`, `UserDeepLinkHandler`
- **Search Feature**: Owns `SearchDestination`, `SearchFeatureApi`, `SearchDeepLinkHandler`
- **Settings**: Uses distributed deep link handling
- **Navigation API**: Provides base interfaces and contracts only

## Implementation Examples

### 1. **Feature Destination Definition**

```kotlin

```kotlin
```kotlin

Navigation Request → Navigation3Controller → SecureDeepLinkHandler →
DeepLinkSecurityValidator → DefaultDestinationResolver → DeepLinkHandler →
FeatureDestination → Navigation3Host → UI Screen
AppDestination (centralized) → DeepLinkHandler → Navigation3Controller
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}

```

1. **Create UserFeatureApi**:

```kotlin

```kotlin
```kotlin

Navigation Request → Navigation3Controller → SecureDeepLinkHandler →
DeepLinkSecurityValidator → DefaultDestinationResolver → DeepLinkHandler →
FeatureDestination → Navigation3Host → UI Screen
AppDestination (centralized) → DeepLinkHandler → Navigation3Controller
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}
interface UserFeatureApi : FeatureApi {
    fun navigateToUserDetail(userId: String): NavCommand
    fun navigateToUserList(): NavCommand
}

class UserFeatureApiImpl @Inject constructor(
    private val navigationController: Navigation3Controller
) : UserFeatureApi {
    override val featureId = "users"

    override fun navigateToUserDetail(userId: String): NavCommand {
        return NavigateToUserDetail(userId)
    }

    override fun navigateToUserList(): NavCommand {
        return NavigateToUserList()
    }
}

```

1. **Update UserDeepLinkHandler**:

```kotlin

```kotlin
```kotlin

Navigation Request → Navigation3Controller → SecureDeepLinkHandler →
DeepLinkSecurityValidator → DefaultDestinationResolver → DeepLinkHandler →
FeatureDestination → Navigation3Host → UI Screen
AppDestination (centralized) → DeepLinkHandler → Navigation3Controller
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}
interface UserFeatureApi : FeatureApi {
    fun navigateToUserDetail(userId: String): NavCommand
    fun navigateToUserList(): NavCommand
}

class UserFeatureApiImpl @Inject constructor(
    private val navigationController: Navigation3Controller
) : UserFeatureApi {
    override val featureId = "users"

    override fun navigateToUserDetail(userId: String): NavCommand {
        return NavigateToUserDetail(userId)
    }

    override fun navigateToUserList(): NavCommand {
        return NavigateToUserList()
    }
}
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}
interface UserFeatureApi : FeatureApi {
    fun navigateToUserDetail(userId: String): NavCommand
    fun navigateToUserList(): NavCommand
}

class UserFeatureApiImpl @Inject constructor(
    private val navigationController: Navigation3Controller
) : UserFeatureApi {
    override val featureId = "users"

    override fun navigateToUserDetail(userId: String): NavCommand {
        return NavigateToUserDetail(userId)
    }

    override fun navigateToUserList(): NavCommand {
        return NavigateToUserList()
    }
}
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    override val moduleId = "users"

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        // Handle distributed destinations only
        return when {
            isUserListUri(uri) -> DeepLinkResult(
                destination = UserDestination.UserLis
            )
            isUserDetailUri(uri) -> {
                val userId = extractUserId(uri)
                DeepLinkResult(
                    destination = UserDestination.UserDetail(userId)
                )
            }
            else -> null
        }
    }
}

```

### Phase 3: Legacy Code Removal

**Goal**: Remove all centralized `AppDestination` code and complete the migration.

**Process**:

1. Remove `AppDestination` sealed interface from `navigation-api`
2. Remove `AppDeepLinks` utility class
3. Update `Navigation3Controller` to work with `FeatureDestination` only
4. Update `MainActivity` to use distributed destinations
5. Remove all adapter layers and compatibility shims
6. Clean up any remaining centralized destination references

## Benefits of This Approach

### 1. **Clean Architecture**

- Complete separation of concerns
- No legacy code to maintain
- Clear feature ownership boundaries

### 2. **Feature Isolation**

- Features own their destination types
- Reduced coupling between modules
- Better testability

### 3. **Type Safety**

- Maintained through `FeatureApi` contracts
- Compile-time navigation validation
- Clear interfaces between modules

### 4. **Flexibility**

- Features can evolve independently
- Easy to add new features
- Supports A/B testing of navigation patterns

## Implementation Guidelines

### For New Features

1. **Define FeatureDestination**:

```kotlin

```kotlin
```kotlin

Navigation Request → Navigation3Controller → SecureDeepLinkHandler →
DeepLinkSecurityValidator → DefaultDestinationResolver → DeepLinkHandler →
FeatureDestination → Navigation3Host → UI Screen
AppDestination (centralized) → DeepLinkHandler → Navigation3Controller
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}
interface UserFeatureApi : FeatureApi {
    fun navigateToUserDetail(userId: String): NavCommand
    fun navigateToUserList(): NavCommand
}

class UserFeatureApiImpl @Inject constructor(
    private val navigationController: Navigation3Controller
) : UserFeatureApi {
    override val featureId = "users"

    override fun navigateToUserDetail(userId: String): NavCommand {
        return NavigateToUserDetail(userId)
    }

    override fun navigateToUserList(): NavCommand {
        return NavigateToUserList()
    }
}
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}
interface UserFeatureApi : FeatureApi {
    fun navigateToUserDetail(userId: String): NavCommand
    fun navigateToUserList(): NavCommand
}

class UserFeatureApiImpl @Inject constructor(
    private val navigationController: Navigation3Controller
) : UserFeatureApi {
    override val featureId = "users"

    override fun navigateToUserDetail(userId: String): NavCommand {
        return NavigateToUserDetail(userId)
    }

    override fun navigateToUserList(): NavCommand {
        return NavigateToUserList()
    }
}
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    override val moduleId = "users"

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        // Handle distributed destinations only
        return when {
            isUserListUri(uri) -> DeepLinkResult(
                destination = UserDestination.UserLis
            )
            isUserDetailUri(uri) -> {
                val userId = extractUserId(uri)
                DeepLinkResult(
                    destination = UserDestination.UserDetail(userId)
                )
            }
            else -> null
        }
    }
}
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}
interface UserFeatureApi : FeatureApi {
    fun navigateToUserDetail(userId: String): NavCommand
    fun navigateToUserList(): NavCommand
}

class UserFeatureApiImpl @Inject constructor(
    private val navigationController: Navigation3Controller
) : UserFeatureApi {
    override val featureId = "users"

    override fun navigateToUserDetail(userId: String): NavCommand {
        return NavigateToUserDetail(userId)
    }

    override fun navigateToUserList(): NavCommand {
        return NavigateToUserList()
    }
}
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    override val moduleId = "users"

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        // Handle distributed destinations only
        return when {
            isUserListUri(uri) -> DeepLinkResult(
                destination = UserDestination.UserLis
            )
            isUserDetailUri(uri) -> {
                val userId = extractUserId(uri)
                DeepLinkResult(
                    destination = UserDestination.UserDetail(userId)
                )
            }
            else -> null
        }
    }
}
sealed class MyFeatureDestination : FeatureDestination {
    // Define destinations with route and deepLink
}

```

1. **Implement FeatureApi**:

```kotlin

```kotlin
```kotlin

Navigation Request → Navigation3Controller → SecureDeepLinkHandler →
DeepLinkSecurityValidator → DefaultDestinationResolver → DeepLinkHandler →
FeatureDestination → Navigation3Host → UI Screen
AppDestination (centralized) → DeepLinkHandler → Navigation3Controller
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}
interface UserFeatureApi : FeatureApi {
    fun navigateToUserDetail(userId: String): NavCommand
    fun navigateToUserList(): NavCommand
}

class UserFeatureApiImpl @Inject constructor(
    private val navigationController: Navigation3Controller
) : UserFeatureApi {
    override val featureId = "users"

    override fun navigateToUserDetail(userId: String): NavCommand {
        return NavigateToUserDetail(userId)
    }

    override fun navigateToUserList(): NavCommand {
        return NavigateToUserList()
    }
}
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}
interface UserFeatureApi : FeatureApi {
    fun navigateToUserDetail(userId: String): NavCommand
    fun navigateToUserList(): NavCommand
}

class UserFeatureApiImpl @Inject constructor(
    private val navigationController: Navigation3Controller
) : UserFeatureApi {
    override val featureId = "users"

    override fun navigateToUserDetail(userId: String): NavCommand {
        return NavigateToUserDetail(userId)
    }

    override fun navigateToUserList(): NavCommand {
        return NavigateToUserList()
    }
}
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    override val moduleId = "users"

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        // Handle distributed destinations only
        return when {
            isUserListUri(uri) -> DeepLinkResult(
                destination = UserDestination.UserLis
            )
            isUserDetailUri(uri) -> {
                val userId = extractUserId(uri)
                DeepLinkResult(
                    destination = UserDestination.UserDetail(userId)
                )
            }
            else -> null
        }
    }
}
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}
interface UserFeatureApi : FeatureApi {
    fun navigateToUserDetail(userId: String): NavCommand
    fun navigateToUserList(): NavCommand
}

class UserFeatureApiImpl @Inject constructor(
    private val navigationController: Navigation3Controller
) : UserFeatureApi {
    override val featureId = "users"

    override fun navigateToUserDetail(userId: String): NavCommand {
        return NavigateToUserDetail(userId)
    }

    override fun navigateToUserList(): NavCommand {
        return NavigateToUserList()
    }
}
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    override val moduleId = "users"

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        // Handle distributed destinations only
        return when {
            isUserListUri(uri) -> DeepLinkResult(
                destination = UserDestination.UserLis
            )
            isUserDetailUri(uri) -> {
                val userId = extractUserId(uri)
                DeepLinkResult(
                    destination = UserDestination.UserDetail(userId)
                )
            }
            else -> null
        }
    }
}
sealed class MyFeatureDestination : FeatureDestination {
    // Define destinations with route and deepLink
}
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}
interface UserFeatureApi : FeatureApi {
    fun navigateToUserDetail(userId: String): NavCommand
    fun navigateToUserList(): NavCommand
}

class UserFeatureApiImpl @Inject constructor(
    private val navigationController: Navigation3Controller
) : UserFeatureApi {
    override val featureId = "users"

    override fun navigateToUserDetail(userId: String): NavCommand {
        return NavigateToUserDetail(userId)
    }

    override fun navigateToUserList(): NavCommand {
        return NavigateToUserList()
    }
}
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    override val moduleId = "users"

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        // Handle distributed destinations only
        return when {
            isUserListUri(uri) -> DeepLinkResult(
                destination = UserDestination.UserLis
            )
            isUserDetailUri(uri) -> {
                val userId = extractUserId(uri)
                DeepLinkResult(
                    destination = UserDestination.UserDetail(userId)
                )
            }
            else -> null
        }
    }
}
sealed class MyFeatureDestination : FeatureDestination {
    // Define destinations with route and deepLink
}
interface MyFeatureApi : FeatureApi {
    // Define navigation methods returning NavCommand
}

```

1. **Create DeepLinkHandler**:

```kotlin

```kotlin
```kotlin

Navigation Request → Navigation3Controller → SecureDeepLinkHandler →
DeepLinkSecurityValidator → DefaultDestinationResolver → DeepLinkHandler →
FeatureDestination → Navigation3Host → UI Screen
AppDestination (centralized) → DeepLinkHandler → Navigation3Controller
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}
interface UserFeatureApi : FeatureApi {
    fun navigateToUserDetail(userId: String): NavCommand
    fun navigateToUserList(): NavCommand
}

class UserFeatureApiImpl @Inject constructor(
    private val navigationController: Navigation3Controller
) : UserFeatureApi {
    override val featureId = "users"

    override fun navigateToUserDetail(userId: String): NavCommand {
        return NavigateToUserDetail(userId)
    }

    override fun navigateToUserList(): NavCommand {
        return NavigateToUserList()
    }
}
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}
interface UserFeatureApi : FeatureApi {
    fun navigateToUserDetail(userId: String): NavCommand
    fun navigateToUserList(): NavCommand
}

class UserFeatureApiImpl @Inject constructor(
    private val navigationController: Navigation3Controller
) : UserFeatureApi {
    override val featureId = "users"

    override fun navigateToUserDetail(userId: String): NavCommand {
        return NavigateToUserDetail(userId)
    }

    override fun navigateToUserList(): NavCommand {
        return NavigateToUserList()
    }
}
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    override val moduleId = "users"

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        // Handle distributed destinations only
        return when {
            isUserListUri(uri) -> DeepLinkResult(
                destination = UserDestination.UserLis
            )
            isUserDetailUri(uri) -> {
                val userId = extractUserId(uri)
                DeepLinkResult(
                    destination = UserDestination.UserDetail(userId)
                )
            }
            else -> null
        }
    }
}
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}
interface UserFeatureApi : FeatureApi {
    fun navigateToUserDetail(userId: String): NavCommand
    fun navigateToUserList(): NavCommand
}

class UserFeatureApiImpl @Inject constructor(
    private val navigationController: Navigation3Controller
) : UserFeatureApi {
    override val featureId = "users"

    override fun navigateToUserDetail(userId: String): NavCommand {
        return NavigateToUserDetail(userId)
    }

    override fun navigateToUserList(): NavCommand {
        return NavigateToUserList()
    }
}
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    override val moduleId = "users"

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        // Handle distributed destinations only
        return when {
            isUserListUri(uri) -> DeepLinkResult(
                destination = UserDestination.UserLis
            )
            isUserDetailUri(uri) -> {
                val userId = extractUserId(uri)
                DeepLinkResult(
                    destination = UserDestination.UserDetail(userId)
                )
            }
            else -> null
        }
    }
}
sealed class MyFeatureDestination : FeatureDestination {
    // Define destinations with route and deepLink
}
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}
interface UserFeatureApi : FeatureApi {
    fun navigateToUserDetail(userId: String): NavCommand
    fun navigateToUserList(): NavCommand
}

class UserFeatureApiImpl @Inject constructor(
    private val navigationController: Navigation3Controller
) : UserFeatureApi {
    override val featureId = "users"

    override fun navigateToUserDetail(userId: String): NavCommand {
        return NavigateToUserDetail(userId)
    }

    override fun navigateToUserList(): NavCommand {
        return NavigateToUserList()
    }
}
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    override val moduleId = "users"

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        // Handle distributed destinations only
        return when {
            isUserListUri(uri) -> DeepLinkResult(
                destination = UserDestination.UserLis
            )
            isUserDetailUri(uri) -> {
                val userId = extractUserId(uri)
                DeepLinkResult(
                    destination = UserDestination.UserDetail(userId)
                )
            }
            else -> null
        }
    }
}
sealed class MyFeatureDestination : FeatureDestination {
    // Define destinations with route and deepLink
}
interface MyFeatureApi : FeatureApi {
    // Define navigation methods returning NavCommand
}
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}
interface UserFeatureApi : FeatureApi {
    fun navigateToUserDetail(userId: String): NavCommand
    fun navigateToUserList(): NavCommand
}

class UserFeatureApiImpl @Inject constructor(
    private val navigationController: Navigation3Controller
) : UserFeatureApi {
    override val featureId = "users"

    override fun navigateToUserDetail(userId: String): NavCommand {
        return NavigateToUserDetail(userId)
    }

    override fun navigateToUserList(): NavCommand {
        return NavigateToUserList()
    }
}
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    override val moduleId = "users"

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        // Handle distributed destinations only
        return when {
            isUserListUri(uri) -> DeepLinkResult(
                destination = UserDestination.UserLis
            )
            isUserDetailUri(uri) -> {
                val userId = extractUserId(uri)
                DeepLinkResult(
                    destination = UserDestination.UserDetail(userId)
                )
            }
            else -> null
        }
    }
}
sealed class MyFeatureDestination : FeatureDestination {
    // Define destinations with route and deepLink
}
interface MyFeatureApi : FeatureApi {
    // Define navigation methods returning NavCommand
}
class MyFeatureDeepLinkHandler : DeepLinkHandler {
    // Handle deep links and return AppDestination for compatibility
}

```

1. **Register with Hilt**:

```kotlin

```kotlin
```kotlin

Navigation Request → Navigation3Controller → SecureDeepLinkHandler →
DeepLinkSecurityValidator → DefaultDestinationResolver → DeepLinkHandler →
FeatureDestination → Navigation3Host → UI Screen
AppDestination (centralized) → DeepLinkHandler → Navigation3Controller
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}
interface UserFeatureApi : FeatureApi {
    fun navigateToUserDetail(userId: String): NavCommand
    fun navigateToUserList(): NavCommand
}

class UserFeatureApiImpl @Inject constructor(
    private val navigationController: Navigation3Controller
) : UserFeatureApi {
    override val featureId = "users"

    override fun navigateToUserDetail(userId: String): NavCommand {
        return NavigateToUserDetail(userId)
    }

    override fun navigateToUserList(): NavCommand {
        return NavigateToUserList()
    }
}
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}
interface UserFeatureApi : FeatureApi {
    fun navigateToUserDetail(userId: String): NavCommand
    fun navigateToUserList(): NavCommand
}

class UserFeatureApiImpl @Inject constructor(
    private val navigationController: Navigation3Controller
) : UserFeatureApi {
    override val featureId = "users"

    override fun navigateToUserDetail(userId: String): NavCommand {
        return NavigateToUserDetail(userId)
    }

    override fun navigateToUserList(): NavCommand {
        return NavigateToUserList()
    }
}
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    override val moduleId = "users"

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        // Handle distributed destinations only
        return when {
            isUserListUri(uri) -> DeepLinkResult(
                destination = UserDestination.UserLis
            )
            isUserDetailUri(uri) -> {
                val userId = extractUserId(uri)
                DeepLinkResult(
                    destination = UserDestination.UserDetail(userId)
                )
            }
            else -> null
        }
    }
}
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}
interface UserFeatureApi : FeatureApi {
    fun navigateToUserDetail(userId: String): NavCommand
    fun navigateToUserList(): NavCommand
}

class UserFeatureApiImpl @Inject constructor(
    private val navigationController: Navigation3Controller
) : UserFeatureApi {
    override val featureId = "users"

    override fun navigateToUserDetail(userId: String): NavCommand {
        return NavigateToUserDetail(userId)
    }

    override fun navigateToUserList(): NavCommand {
        return NavigateToUserList()
    }
}
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    override val moduleId = "users"

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        // Handle distributed destinations only
        return when {
            isUserListUri(uri) -> DeepLinkResult(
                destination = UserDestination.UserLis
            )
            isUserDetailUri(uri) -> {
                val userId = extractUserId(uri)
                DeepLinkResult(
                    destination = UserDestination.UserDetail(userId)
                )
            }
            else -> null
        }
    }
}
sealed class MyFeatureDestination : FeatureDestination {
    // Define destinations with route and deepLink
}
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}
interface UserFeatureApi : FeatureApi {
    fun navigateToUserDetail(userId: String): NavCommand
    fun navigateToUserList(): NavCommand
}

class UserFeatureApiImpl @Inject constructor(
    private val navigationController: Navigation3Controller
) : UserFeatureApi {
    override val featureId = "users"

    override fun navigateToUserDetail(userId: String): NavCommand {
        return NavigateToUserDetail(userId)
    }

    override fun navigateToUserList(): NavCommand {
        return NavigateToUserList()
    }
}
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    override val moduleId = "users"

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        // Handle distributed destinations only
        return when {
            isUserListUri(uri) -> DeepLinkResult(
                destination = UserDestination.UserLis
            )
            isUserDetailUri(uri) -> {
                val userId = extractUserId(uri)
                DeepLinkResult(
                    destination = UserDestination.UserDetail(userId)
                )
            }
            else -> null
        }
    }
}
sealed class MyFeatureDestination : FeatureDestination {
    // Define destinations with route and deepLink
}
interface MyFeatureApi : FeatureApi {
    // Define navigation methods returning NavCommand
}
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}
interface UserFeatureApi : FeatureApi {
    fun navigateToUserDetail(userId: String): NavCommand
    fun navigateToUserList(): NavCommand
}

class UserFeatureApiImpl @Inject constructor(
    private val navigationController: Navigation3Controller
) : UserFeatureApi {
    override val featureId = "users"

    override fun navigateToUserDetail(userId: String): NavCommand {
        return NavigateToUserDetail(userId)
    }

    override fun navigateToUserList(): NavCommand {
        return NavigateToUserList()
    }
}
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    override val moduleId = "users"

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        // Handle distributed destinations only
        return when {
            isUserListUri(uri) -> DeepLinkResult(
                destination = UserDestination.UserLis
            )
            isUserDetailUri(uri) -> {
                val userId = extractUserId(uri)
                DeepLinkResult(
                    destination = UserDestination.UserDetail(userId)
                )
            }
            else -> null
        }
    }
}
sealed class MyFeatureDestination : FeatureDestination {
    // Define destinations with route and deepLink
}
interface MyFeatureApi : FeatureApi {
    // Define navigation methods returning NavCommand
}
class MyFeatureDeepLinkHandler : DeepLinkHandler {
    // Handle deep links and return AppDestination for compatibility
}
FeatureDestination (per feature) → FeatureApi → NavCommand → Navigation3Controller
// In feature-users module
sealed class UserDestination : FeatureDestination {
    object UserList : UserDestination() {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val userId: String) : UserDestination() {
        override val route = "users/detail/$userId"
        override val deepLink = "app://users/user/$userId"
    }
}
interface UserFeatureApi : FeatureApi {
    fun navigateToUserDetail(userId: String): NavCommand
    fun navigateToUserList(): NavCommand
}

class UserFeatureApiImpl @Inject constructor(
    private val navigationController: Navigation3Controller
) : UserFeatureApi {
    override val featureId = "users"

    override fun navigateToUserDetail(userId: String): NavCommand {
        return NavigateToUserDetail(userId)
    }

    override fun navigateToUserList(): NavCommand {
        return NavigateToUserList()
    }
}
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    override val moduleId = "users"

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        // Handle distributed destinations only
        return when {
            isUserListUri(uri) -> DeepLinkResult(
                destination = UserDestination.UserLis
            )
            isUserDetailUri(uri) -> {
                val userId = extractUserId(uri)
                DeepLinkResult(
                    destination = UserDestination.UserDetail(userId)
                )
            }
            else -> null
        }
    }
}
sealed class MyFeatureDestination : FeatureDestination {
    // Define destinations with route and deepLink
}
interface MyFeatureApi : FeatureApi {
    // Define navigation methods returning NavCommand
}
class MyFeatureDeepLinkHandler : DeepLinkHandler {
    // Handle deep links and return AppDestination for compatibility
}
@Module
@InstallIn(SingletonComponent::class)
abstract class MyFeatureNavigationModule : FeatureNavigationModule() {
    // Register handlers and APIs
}

```

### For Existing Features

1. **Create distributed destinations to replace AppDestination usage**
2. **Implement FeatureApi for cross-module navigation**
3. **Update DeepLinkHandler to use distributed destinations only**
4. **Update call sites to use FeatureApi instead of AppDestination**
5. **Remove all AppDestination references completely**

## Success Metrics

- [ ] Features can compile independently
- [ ] Zero cross-feature destination references
- [ ] Zero centralized AppDestination references
- [ ] Build time reduction > 20%
- [ ] All existing navigation flows working
- [ ] Type safety maintained through FeatureApi contracts
- [ ] Complete removal of legacy navigation code

## Risk Mitigation

### Early Risks

- **Breaking existing navigation** - Mitigation: Comprehensive testing and validation
- **Complex migration** - Mitigation: Clear migration plan with validation steps

### Ongoing Risks

- **Coordination complexity** - Mitigation: Strong FeatureApi contracts
- **Team adoption** - Mitigation: Clear documentation and examples

### Long-term Risks

- **Performance impact** - Mitigation: Monitor and optimize
- **Maintenance overhead** - Mitigation: Automated validation and tooling

## Next Steps

1. **Complete Phase 1**: Finalize base interfaces and documentation ✅
2. **Start Phase 2**: Implement complete migration with users feature
3. **Remove legacy code**: Delete all AppDestination and centralized navigation code
4. **Validate approach**: Test and refine based on complete migration
5. **Execute remaining features**: Migrate all features to distributed destinations
