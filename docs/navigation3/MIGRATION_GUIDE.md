# Navigation 3 Migration Guide

## 🎯 Overview

This guide provides step-by-step instructions for migrating from the legacy navigation system to the new Navigation 3 architecture. The migration focuses on feature-based navigation with deep link ownership and distributed destinations.

**Target Audience**: Developers working on the GitHub Users projec
**Migration Type**: Complete migration (no backward compatibility)
**Estimated Time**: 2-4 days per feature module

---

## 📋 Pre-Migration Checklis

### ✅ **Prerequisites**
- [ ] Understand the new Navigation 3 architecture
- [ ] Review the [Navigation 3 Deep Link Architecture](NAVIGATION_3_DEEPLINK_ARCHITECTURE.md)
- [ ] Read the [Feature-Based Development Guide](../../FEATURE_BASED_DEVELOPMENT_GUIDE.md)
- [ ] Ensure all tests are passing before migration

### ✅ **Preparation**
- [ ] Backup current code
- [ ] Create feature branch for migration
- [ ] Review current navigation patterns in your feature
- [ ] Identify all cross-module navigation points

---

## 🏗️ Architecture Changes

### Before (Legacy System)
```kotlin
```kotlin
// ❌ OLD: Centralized navigation with AppDestination
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }
}
```

### After (Navigation 3)
```kotlin
```kotlin
// ❌ OLD: Centralized navigation with AppDestination
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }
}
// ✅ NEW: Feature-owned navigation with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }
}
```

---

## 🔄 Migration Steps

### Step 1: Update Feature Module Dependencies

#### Before
```kotlin
```kotlin
// ❌ OLD: Centralized navigation with AppDestination
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }
}
// ✅ NEW: Feature-owned navigation with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
}
```

#### After
```kotlin
```kotlin
// ❌ OLD: Centralized navigation with AppDestination
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }
}
// ✅ NEW: Feature-owned navigation with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
    // Remove any legacy navigation dependencies
}
```

### Step 2: Create Feature Destination Interface

#### Before
```kotlin
```kotlin
// ❌ OLD: Centralized navigation with AppDestination
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }
}
// ✅ NEW: Feature-owned navigation with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
    // Remove any legacy navigation dependencies
}
// ❌ OLD: Using centralized AppDestination
import com.example.githubusers.navigation.api.AppDestination

sealed interface UserDestination : AppDestination {
    data object UserList : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }
}
```

#### After
```kotlin
```kotlin
// ❌ OLD: Centralized navigation with AppDestination
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }
}
// ✅ NEW: Feature-owned navigation with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
    // Remove any legacy navigation dependencies
}
// ❌ OLD: Using centralized AppDestination
import com.example.githubusers.navigation.api.AppDestination

sealed interface UserDestination : AppDestination {
    data object UserList : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }
}
// ✅ NEW: Feature-owned destinations
import com.example.githubusers.navigation.api.Destination

sealed interface UserDestination : Destination {
    data class UserList(val filter: String? = null) : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val username: String) : UserDestination {
        override val route = "users/detail/$username"
        override val deepLink = "app://users/user/$username"
    }
}
```

### Step 3: Implement Deep Link Handler

#### Before
```kotlin
```kotlin
// ❌ OLD: Centralized navigation with AppDestination
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }
}
// ✅ NEW: Feature-owned navigation with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
    // Remove any legacy navigation dependencies
}
// ❌ OLD: Using centralized AppDestination
import com.example.githubusers.navigation.api.AppDestination

sealed interface UserDestination : AppDestination {
    data object UserList : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }
}
// ✅ NEW: Feature-owned destinations
import com.example.githubusers.navigation.api.Destination

sealed interface UserDestination : Destination {
    data class UserList(val filter: String? = null) : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val username: String) : UserDestination {
        override val route = "users/detail/$username"
        override val deepLink = "app://users/user/$username"
    }
}
// ❌ OLD: No deep link handler needed
// Navigation was handled centrally
```

#### After
```kotlin
```kotlin
// ❌ OLD: Centralized navigation with AppDestination
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }
}
// ✅ NEW: Feature-owned navigation with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
    // Remove any legacy navigation dependencies
}
// ❌ OLD: Using centralized AppDestination
import com.example.githubusers.navigation.api.AppDestination

sealed interface UserDestination : AppDestination {
    data object UserList : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }
}
// ✅ NEW: Feature-owned destinations
import com.example.githubusers.navigation.api.Destination

sealed interface UserDestination : Destination {
    data class UserList(val filter: String? = null) : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val username: String) : UserDestination {
        override val route = "users/detail/$username"
        override val deepLink = "app://users/user/$username"
    }
}
// ❌ OLD: No deep link handler needed
// Navigation was handled centrally
// ✅ NEW: Feature-owned deep link handler
@OwnsDeepLinks(moduleId = "users")
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> = listOf(
        "app://users",
        "app://users/list",
        "app://users/user/{username}",
        "githubusers://users",
        "githubusers://user/{username}",
        "https://githubusers.example.com/users",
        "https://githubusers.example.com/user/{username}"
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        val path = uri.path ?: return null
        val scheme = uri.scheme ?: return null

        return when {
            isUserListUri(scheme, uri.host, path) -> {
                val filter = uri.getQueryParameter("filter")
                val destination = UserDestination.UserList(filter)
                DeepLinkResult(
                    route = destination.route,
                    deepLink = destination.deepLink,
                    arguments = filter?.let { mapOf("filter" to it) } ?: emptyMap()
                )
            }

            isUserDetailUri(scheme, uri.host, path) -> {
                val username = extractUsername(path)
                if (username != null) {
                    val destination = UserDestination.UserDetail(username)
                    DeepLinkResult(
                        route = destination.route,
                        deepLink = destination.deepLink,
                        arguments = mapOf("username" to username)
                    )
                } else null
            }

            else -> null
        }
    }

    // Helper methods for URI pattern matching
    private fun isUserListUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path in listOf("/users", "/users/list") -> true
            scheme == "githubusers" && path == "/users" -> true
            host == "githubusers.example.com" && path == "/users" -> true
            else -> false
        }

    private fun isUserDetailUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path.startsWith("/users/user/") -> true
            scheme == "githubusers" && path.startsWith("/user/") -> true
            host == "githubusers.example.com" && path.startsWith("/user/") -> true
            else -> false
        }

    private fun extractUsername(path: String): String? =
        when {
            path.startsWith("/users/user/") -> path.removePrefix("/users/user/").takeIf { it.isNotEmpty() }
            path.startsWith("/user/") -> path.removePrefix("/user/").takeIf { it.isNotEmpty() }
            else -> null
        }
}
```

### Step 4: Register Deep Link Handler

#### Before
```kotlin
```kotlin
// ❌ OLD: Centralized navigation with AppDestination
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }
}
// ✅ NEW: Feature-owned navigation with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
    // Remove any legacy navigation dependencies
}
// ❌ OLD: Using centralized AppDestination
import com.example.githubusers.navigation.api.AppDestination

sealed interface UserDestination : AppDestination {
    data object UserList : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }
}
// ✅ NEW: Feature-owned destinations
import com.example.githubusers.navigation.api.Destination

sealed interface UserDestination : Destination {
    data class UserList(val filter: String? = null) : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val username: String) : UserDestination {
        override val route = "users/detail/$username"
        override val deepLink = "app://users/user/$username"
    }
}
// ❌ OLD: No deep link handler needed
// Navigation was handled centrally
// ✅ NEW: Feature-owned deep link handler
@OwnsDeepLinks(moduleId = "users")
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> = listOf(
        "app://users",
        "app://users/list",
        "app://users/user/{username}",
        "githubusers://users",
        "githubusers://user/{username}",
        "https://githubusers.example.com/users",
        "https://githubusers.example.com/user/{username}"
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        val path = uri.path ?: return null
        val scheme = uri.scheme ?: return null

        return when {
            isUserListUri(scheme, uri.host, path) -> {
                val filter = uri.getQueryParameter("filter")
                val destination = UserDestination.UserList(filter)
                DeepLinkResult(
                    route = destination.route,
                    deepLink = destination.deepLink,
                    arguments = filter?.let { mapOf("filter" to it) } ?: emptyMap()
                )
            }

            isUserDetailUri(scheme, uri.host, path) -> {
                val username = extractUsername(path)
                if (username != null) {
                    val destination = UserDestination.UserDetail(username)
                    DeepLinkResult(
                        route = destination.route,
                        deepLink = destination.deepLink,
                        arguments = mapOf("username" to username)
                    )
                } else null
            }

            else -> null
        }
    }

    // Helper methods for URI pattern matching
    private fun isUserListUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path in listOf("/users", "/users/list") -> true
            scheme == "githubusers" && path == "/users" -> true
            host == "githubusers.example.com" && path == "/users" -> true
            else -> false
        }

    private fun isUserDetailUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path.startsWith("/users/user/") -> true
            scheme == "githubusers" && path.startsWith("/user/") -> true
            host == "githubusers.example.com" && path.startsWith("/user/") -> true
            else -> false
        }

    private fun extractUsername(path: String): String? =
        when {
            path.startsWith("/users/user/") -> path.removePrefix("/users/user/").takeIf { it.isNotEmpty() }
            path.startsWith("/user/") -> path.removePrefix("/user/").takeIf { it.isNotEmpty() }
            else -> null
        }
}
// ❌ OLD: No registration needed
// Handlers were managed centrally
```

#### After
```kotlin
```kotlin
// ❌ OLD: Centralized navigation with AppDestination
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }
}
// ✅ NEW: Feature-owned navigation with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
    // Remove any legacy navigation dependencies
}
// ❌ OLD: Using centralized AppDestination
import com.example.githubusers.navigation.api.AppDestination

sealed interface UserDestination : AppDestination {
    data object UserList : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }
}
// ✅ NEW: Feature-owned destinations
import com.example.githubusers.navigation.api.Destination

sealed interface UserDestination : Destination {
    data class UserList(val filter: String? = null) : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val username: String) : UserDestination {
        override val route = "users/detail/$username"
        override val deepLink = "app://users/user/$username"
    }
}
// ❌ OLD: No deep link handler needed
// Navigation was handled centrally
// ✅ NEW: Feature-owned deep link handler
@OwnsDeepLinks(moduleId = "users")
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> = listOf(
        "app://users",
        "app://users/list",
        "app://users/user/{username}",
        "githubusers://users",
        "githubusers://user/{username}",
        "https://githubusers.example.com/users",
        "https://githubusers.example.com/user/{username}"
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        val path = uri.path ?: return null
        val scheme = uri.scheme ?: return null

        return when {
            isUserListUri(scheme, uri.host, path) -> {
                val filter = uri.getQueryParameter("filter")
                val destination = UserDestination.UserList(filter)
                DeepLinkResult(
                    route = destination.route,
                    deepLink = destination.deepLink,
                    arguments = filter?.let { mapOf("filter" to it) } ?: emptyMap()
                )
            }

            isUserDetailUri(scheme, uri.host, path) -> {
                val username = extractUsername(path)
                if (username != null) {
                    val destination = UserDestination.UserDetail(username)
                    DeepLinkResult(
                        route = destination.route,
                        deepLink = destination.deepLink,
                        arguments = mapOf("username" to username)
                    )
                } else null
            }

            else -> null
        }
    }

    // Helper methods for URI pattern matching
    private fun isUserListUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path in listOf("/users", "/users/list") -> true
            scheme == "githubusers" && path == "/users" -> true
            host == "githubusers.example.com" && path == "/users" -> true
            else -> false
        }

    private fun isUserDetailUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path.startsWith("/users/user/") -> true
            scheme == "githubusers" && path.startsWith("/user/") -> true
            host == "githubusers.example.com" && path.startsWith("/user/") -> true
            else -> false
        }

    private fun extractUsername(path: String): String? =
        when {
            path.startsWith("/users/user/") -> path.removePrefix("/users/user/").takeIf { it.isNotEmpty() }
            path.startsWith("/user/") -> path.removePrefix("/user/").takeIf { it.isNotEmpty() }
            else -> null
        }
}
// ❌ OLD: No registration needed
// Handlers were managed centrally
// ✅ NEW: Register with Hilt multibindings
@Module
@InstallIn(SingletonComponent::class)
abstract class UserNavigationModule {

    @Binds
    @IntoSe
    abstract fun bindUserDeepLinkHandler(
        handler: UserDeepLinkHandler
    ): DeepLinkHandler
}
```

### Step 5: Update Navigation Calls

#### Before
```kotlin
```kotlin
// ❌ OLD: Centralized navigation with AppDestination
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }
}
// ✅ NEW: Feature-owned navigation with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
    // Remove any legacy navigation dependencies
}
// ❌ OLD: Using centralized AppDestination
import com.example.githubusers.navigation.api.AppDestination

sealed interface UserDestination : AppDestination {
    data object UserList : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }
}
// ✅ NEW: Feature-owned destinations
import com.example.githubusers.navigation.api.Destination

sealed interface UserDestination : Destination {
    data class UserList(val filter: String? = null) : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val username: String) : UserDestination {
        override val route = "users/detail/$username"
        override val deepLink = "app://users/user/$username"
    }
}
// ❌ OLD: No deep link handler needed
// Navigation was handled centrally
// ✅ NEW: Feature-owned deep link handler
@OwnsDeepLinks(moduleId = "users")
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> = listOf(
        "app://users",
        "app://users/list",
        "app://users/user/{username}",
        "githubusers://users",
        "githubusers://user/{username}",
        "https://githubusers.example.com/users",
        "https://githubusers.example.com/user/{username}"
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        val path = uri.path ?: return null
        val scheme = uri.scheme ?: return null

        return when {
            isUserListUri(scheme, uri.host, path) -> {
                val filter = uri.getQueryParameter("filter")
                val destination = UserDestination.UserList(filter)
                DeepLinkResult(
                    route = destination.route,
                    deepLink = destination.deepLink,
                    arguments = filter?.let { mapOf("filter" to it) } ?: emptyMap()
                )
            }

            isUserDetailUri(scheme, uri.host, path) -> {
                val username = extractUsername(path)
                if (username != null) {
                    val destination = UserDestination.UserDetail(username)
                    DeepLinkResult(
                        route = destination.route,
                        deepLink = destination.deepLink,
                        arguments = mapOf("username" to username)
                    )
                } else null
            }

            else -> null
        }
    }

    // Helper methods for URI pattern matching
    private fun isUserListUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path in listOf("/users", "/users/list") -> true
            scheme == "githubusers" && path == "/users" -> true
            host == "githubusers.example.com" && path == "/users" -> true
            else -> false
        }

    private fun isUserDetailUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path.startsWith("/users/user/") -> true
            scheme == "githubusers" && path.startsWith("/user/") -> true
            host == "githubusers.example.com" && path.startsWith("/user/") -> true
            else -> false
        }

    private fun extractUsername(path: String): String? =
        when {
            path.startsWith("/users/user/") -> path.removePrefix("/users/user/").takeIf { it.isNotEmpty() }
            path.startsWith("/user/") -> path.removePrefix("/user/").takeIf { it.isNotEmpty() }
            else -> null
        }
}
// ❌ OLD: No registration needed
// Handlers were managed centrally
// ✅ NEW: Register with Hilt multibindings
@Module
@InstallIn(SingletonComponent::class)
abstract class UserNavigationModule {

    @Binds
    @IntoSe
    abstract fun bindUserDeepLinkHandler(
        handler: UserDeepLinkHandler
    ): DeepLinkHandler
}
// ❌ OLD: Direct AppDestination usage
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }

    fun navigateToSearch(query: String) {
        navigationController.navigate(
            AppDestination.Search(query)
        )
    }
}
```

#### After
```kotlin
```kotlin
// ❌ OLD: Centralized navigation with AppDestination
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }
}
// ✅ NEW: Feature-owned navigation with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
    // Remove any legacy navigation dependencies
}
// ❌ OLD: Using centralized AppDestination
import com.example.githubusers.navigation.api.AppDestination

sealed interface UserDestination : AppDestination {
    data object UserList : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }
}
// ✅ NEW: Feature-owned destinations
import com.example.githubusers.navigation.api.Destination

sealed interface UserDestination : Destination {
    data class UserList(val filter: String? = null) : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val username: String) : UserDestination {
        override val route = "users/detail/$username"
        override val deepLink = "app://users/user/$username"
    }
}
// ❌ OLD: No deep link handler needed
// Navigation was handled centrally
// ✅ NEW: Feature-owned deep link handler
@OwnsDeepLinks(moduleId = "users")
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> = listOf(
        "app://users",
        "app://users/list",
        "app://users/user/{username}",
        "githubusers://users",
        "githubusers://user/{username}",
        "https://githubusers.example.com/users",
        "https://githubusers.example.com/user/{username}"
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        val path = uri.path ?: return null
        val scheme = uri.scheme ?: return null

        return when {
            isUserListUri(scheme, uri.host, path) -> {
                val filter = uri.getQueryParameter("filter")
                val destination = UserDestination.UserList(filter)
                DeepLinkResult(
                    route = destination.route,
                    deepLink = destination.deepLink,
                    arguments = filter?.let { mapOf("filter" to it) } ?: emptyMap()
                )
            }

            isUserDetailUri(scheme, uri.host, path) -> {
                val username = extractUsername(path)
                if (username != null) {
                    val destination = UserDestination.UserDetail(username)
                    DeepLinkResult(
                        route = destination.route,
                        deepLink = destination.deepLink,
                        arguments = mapOf("username" to username)
                    )
                } else null
            }

            else -> null
        }
    }

    // Helper methods for URI pattern matching
    private fun isUserListUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path in listOf("/users", "/users/list") -> true
            scheme == "githubusers" && path == "/users" -> true
            host == "githubusers.example.com" && path == "/users" -> true
            else -> false
        }

    private fun isUserDetailUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path.startsWith("/users/user/") -> true
            scheme == "githubusers" && path.startsWith("/user/") -> true
            host == "githubusers.example.com" && path.startsWith("/user/") -> true
            else -> false
        }

    private fun extractUsername(path: String): String? =
        when {
            path.startsWith("/users/user/") -> path.removePrefix("/users/user/").takeIf { it.isNotEmpty() }
            path.startsWith("/user/") -> path.removePrefix("/user/").takeIf { it.isNotEmpty() }
            else -> null
        }
}
// ❌ OLD: No registration needed
// Handlers were managed centrally
// ✅ NEW: Register with Hilt multibindings
@Module
@InstallIn(SingletonComponent::class)
abstract class UserNavigationModule {

    @Binds
    @IntoSe
    abstract fun bindUserDeepLinkHandler(
        handler: UserDeepLinkHandler
    ): DeepLinkHandler
}
// ❌ OLD: Direct AppDestination usage
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }

    fun navigateToSearch(query: String) {
        navigationController.navigate(
            AppDestination.Search(query)
        )
    }
}
// ✅ NEW: Use NavCommand with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }

    fun navigateToSearch(query: String) {
        navigationController.navigate(
            NavCommand(
                route = "search",
                deepLink = "app://search?q=${Uri.encode(query)}"
            )
        )
    }
}
```

### Step 6: Update Feature API (Optional)

#### Before
```kotlin
```kotlin
// ❌ OLD: Centralized navigation with AppDestination
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }
}
// ✅ NEW: Feature-owned navigation with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
    // Remove any legacy navigation dependencies
}
// ❌ OLD: Using centralized AppDestination
import com.example.githubusers.navigation.api.AppDestination

sealed interface UserDestination : AppDestination {
    data object UserList : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }
}
// ✅ NEW: Feature-owned destinations
import com.example.githubusers.navigation.api.Destination

sealed interface UserDestination : Destination {
    data class UserList(val filter: String? = null) : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val username: String) : UserDestination {
        override val route = "users/detail/$username"
        override val deepLink = "app://users/user/$username"
    }
}
// ❌ OLD: No deep link handler needed
// Navigation was handled centrally
// ✅ NEW: Feature-owned deep link handler
@OwnsDeepLinks(moduleId = "users")
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> = listOf(
        "app://users",
        "app://users/list",
        "app://users/user/{username}",
        "githubusers://users",
        "githubusers://user/{username}",
        "https://githubusers.example.com/users",
        "https://githubusers.example.com/user/{username}"
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        val path = uri.path ?: return null
        val scheme = uri.scheme ?: return null

        return when {
            isUserListUri(scheme, uri.host, path) -> {
                val filter = uri.getQueryParameter("filter")
                val destination = UserDestination.UserList(filter)
                DeepLinkResult(
                    route = destination.route,
                    deepLink = destination.deepLink,
                    arguments = filter?.let { mapOf("filter" to it) } ?: emptyMap()
                )
            }

            isUserDetailUri(scheme, uri.host, path) -> {
                val username = extractUsername(path)
                if (username != null) {
                    val destination = UserDestination.UserDetail(username)
                    DeepLinkResult(
                        route = destination.route,
                        deepLink = destination.deepLink,
                        arguments = mapOf("username" to username)
                    )
                } else null
            }

            else -> null
        }
    }

    // Helper methods for URI pattern matching
    private fun isUserListUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path in listOf("/users", "/users/list") -> true
            scheme == "githubusers" && path == "/users" -> true
            host == "githubusers.example.com" && path == "/users" -> true
            else -> false
        }

    private fun isUserDetailUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path.startsWith("/users/user/") -> true
            scheme == "githubusers" && path.startsWith("/user/") -> true
            host == "githubusers.example.com" && path.startsWith("/user/") -> true
            else -> false
        }

    private fun extractUsername(path: String): String? =
        when {
            path.startsWith("/users/user/") -> path.removePrefix("/users/user/").takeIf { it.isNotEmpty() }
            path.startsWith("/user/") -> path.removePrefix("/user/").takeIf { it.isNotEmpty() }
            else -> null
        }
}
// ❌ OLD: No registration needed
// Handlers were managed centrally
// ✅ NEW: Register with Hilt multibindings
@Module
@InstallIn(SingletonComponent::class)
abstract class UserNavigationModule {

    @Binds
    @IntoSe
    abstract fun bindUserDeepLinkHandler(
        handler: UserDeepLinkHandler
    ): DeepLinkHandler
}
// ❌ OLD: Direct AppDestination usage
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }

    fun navigateToSearch(query: String) {
        navigationController.navigate(
            AppDestination.Search(query)
        )
    }
}
// ✅ NEW: Use NavCommand with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }

    fun navigateToSearch(query: String) {
        navigationController.navigate(
            NavCommand(
                route = "search",
                deepLink = "app://search?q=${Uri.encode(query)}"
            )
        )
    }
}
// ❌ OLD: FeatureApi with AppDestination
interface UserFeatureApi {
    fun navigateToUserList(): AppDestination.UserLis
    fun navigateToUserDetail(username: String): AppDestination.UserDetail
}
```

#### After
```kotlin
```kotlin
// ❌ OLD: Centralized navigation with AppDestination
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }
}
// ✅ NEW: Feature-owned navigation with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
    // Remove any legacy navigation dependencies
}
// ❌ OLD: Using centralized AppDestination
import com.example.githubusers.navigation.api.AppDestination

sealed interface UserDestination : AppDestination {
    data object UserList : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }
}
// ✅ NEW: Feature-owned destinations
import com.example.githubusers.navigation.api.Destination

sealed interface UserDestination : Destination {
    data class UserList(val filter: String? = null) : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val username: String) : UserDestination {
        override val route = "users/detail/$username"
        override val deepLink = "app://users/user/$username"
    }
}
// ❌ OLD: No deep link handler needed
// Navigation was handled centrally
// ✅ NEW: Feature-owned deep link handler
@OwnsDeepLinks(moduleId = "users")
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> = listOf(
        "app://users",
        "app://users/list",
        "app://users/user/{username}",
        "githubusers://users",
        "githubusers://user/{username}",
        "https://githubusers.example.com/users",
        "https://githubusers.example.com/user/{username}"
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        val path = uri.path ?: return null
        val scheme = uri.scheme ?: return null

        return when {
            isUserListUri(scheme, uri.host, path) -> {
                val filter = uri.getQueryParameter("filter")
                val destination = UserDestination.UserList(filter)
                DeepLinkResult(
                    route = destination.route,
                    deepLink = destination.deepLink,
                    arguments = filter?.let { mapOf("filter" to it) } ?: emptyMap()
                )
            }

            isUserDetailUri(scheme, uri.host, path) -> {
                val username = extractUsername(path)
                if (username != null) {
                    val destination = UserDestination.UserDetail(username)
                    DeepLinkResult(
                        route = destination.route,
                        deepLink = destination.deepLink,
                        arguments = mapOf("username" to username)
                    )
                } else null
            }

            else -> null
        }
    }

    // Helper methods for URI pattern matching
    private fun isUserListUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path in listOf("/users", "/users/list") -> true
            scheme == "githubusers" && path == "/users" -> true
            host == "githubusers.example.com" && path == "/users" -> true
            else -> false
        }

    private fun isUserDetailUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path.startsWith("/users/user/") -> true
            scheme == "githubusers" && path.startsWith("/user/") -> true
            host == "githubusers.example.com" && path.startsWith("/user/") -> true
            else -> false
        }

    private fun extractUsername(path: String): String? =
        when {
            path.startsWith("/users/user/") -> path.removePrefix("/users/user/").takeIf { it.isNotEmpty() }
            path.startsWith("/user/") -> path.removePrefix("/user/").takeIf { it.isNotEmpty() }
            else -> null
        }
}
// ❌ OLD: No registration needed
// Handlers were managed centrally
// ✅ NEW: Register with Hilt multibindings
@Module
@InstallIn(SingletonComponent::class)
abstract class UserNavigationModule {

    @Binds
    @IntoSe
    abstract fun bindUserDeepLinkHandler(
        handler: UserDeepLinkHandler
    ): DeepLinkHandler
}
// ❌ OLD: Direct AppDestination usage
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }

    fun navigateToSearch(query: String) {
        navigationController.navigate(
            AppDestination.Search(query)
        )
    }
}
// ✅ NEW: Use NavCommand with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }

    fun navigateToSearch(query: String) {
        navigationController.navigate(
            NavCommand(
                route = "search",
                deepLink = "app://search?q=${Uri.encode(query)}"
            )
        )
    }
}
// ❌ OLD: FeatureApi with AppDestination
interface UserFeatureApi {
    fun navigateToUserList(): AppDestination.UserLis
    fun navigateToUserDetail(username: String): AppDestination.UserDetail
}
// ✅ NEW: Feature API with NavCommand
interface UserFeatureApi {
    fun navigateToUserList(filter: String? = null): NavCommand
    fun navigateToUserDetail(username: String): NavCommand
}

class UserFeatureApiImpl @Inject constructor() : UserFeatureApi {

    override fun navigateToUserList(filter: String?): NavCommand {
        val deepLink = buildString {
            append("app://users/list")
            if (filter != null) {
                append("?filter=${Uri.encode(filter)}")
            }
        }
        return NavCommand(
            route = "users/list",
            deepLink = deepLink,
            arguments = filter?.let { mapOf("filter" to it) } ?: emptyMap()
        )
    }

    override fun navigateToUserDetail(username: String): NavCommand {
        return NavCommand(
            route = "users/detail/$username",
            deepLink = "app://users/user/$username",
            arguments = mapOf("username" to username)
        )
    }
}
```

### Step 7: Update Tests

#### Before
```kotlin
```kotlin
// ❌ OLD: Centralized navigation with AppDestination
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }
}
// ✅ NEW: Feature-owned navigation with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
    // Remove any legacy navigation dependencies
}
// ❌ OLD: Using centralized AppDestination
import com.example.githubusers.navigation.api.AppDestination

sealed interface UserDestination : AppDestination {
    data object UserList : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }
}
// ✅ NEW: Feature-owned destinations
import com.example.githubusers.navigation.api.Destination

sealed interface UserDestination : Destination {
    data class UserList(val filter: String? = null) : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val username: String) : UserDestination {
        override val route = "users/detail/$username"
        override val deepLink = "app://users/user/$username"
    }
}
// ❌ OLD: No deep link handler needed
// Navigation was handled centrally
// ✅ NEW: Feature-owned deep link handler
@OwnsDeepLinks(moduleId = "users")
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> = listOf(
        "app://users",
        "app://users/list",
        "app://users/user/{username}",
        "githubusers://users",
        "githubusers://user/{username}",
        "https://githubusers.example.com/users",
        "https://githubusers.example.com/user/{username}"
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        val path = uri.path ?: return null
        val scheme = uri.scheme ?: return null

        return when {
            isUserListUri(scheme, uri.host, path) -> {
                val filter = uri.getQueryParameter("filter")
                val destination = UserDestination.UserList(filter)
                DeepLinkResult(
                    route = destination.route,
                    deepLink = destination.deepLink,
                    arguments = filter?.let { mapOf("filter" to it) } ?: emptyMap()
                )
            }

            isUserDetailUri(scheme, uri.host, path) -> {
                val username = extractUsername(path)
                if (username != null) {
                    val destination = UserDestination.UserDetail(username)
                    DeepLinkResult(
                        route = destination.route,
                        deepLink = destination.deepLink,
                        arguments = mapOf("username" to username)
                    )
                } else null
            }

            else -> null
        }
    }

    // Helper methods for URI pattern matching
    private fun isUserListUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path in listOf("/users", "/users/list") -> true
            scheme == "githubusers" && path == "/users" -> true
            host == "githubusers.example.com" && path == "/users" -> true
            else -> false
        }

    private fun isUserDetailUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path.startsWith("/users/user/") -> true
            scheme == "githubusers" && path.startsWith("/user/") -> true
            host == "githubusers.example.com" && path.startsWith("/user/") -> true
            else -> false
        }

    private fun extractUsername(path: String): String? =
        when {
            path.startsWith("/users/user/") -> path.removePrefix("/users/user/").takeIf { it.isNotEmpty() }
            path.startsWith("/user/") -> path.removePrefix("/user/").takeIf { it.isNotEmpty() }
            else -> null
        }
}
// ❌ OLD: No registration needed
// Handlers were managed centrally
// ✅ NEW: Register with Hilt multibindings
@Module
@InstallIn(SingletonComponent::class)
abstract class UserNavigationModule {

    @Binds
    @IntoSe
    abstract fun bindUserDeepLinkHandler(
        handler: UserDeepLinkHandler
    ): DeepLinkHandler
}
// ❌ OLD: Direct AppDestination usage
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }

    fun navigateToSearch(query: String) {
        navigationController.navigate(
            AppDestination.Search(query)
        )
    }
}
// ✅ NEW: Use NavCommand with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }

    fun navigateToSearch(query: String) {
        navigationController.navigate(
            NavCommand(
                route = "search",
                deepLink = "app://search?q=${Uri.encode(query)}"
            )
        )
    }
}
// ❌ OLD: FeatureApi with AppDestination
interface UserFeatureApi {
    fun navigateToUserList(): AppDestination.UserLis
    fun navigateToUserDetail(username: String): AppDestination.UserDetail
}
// ✅ NEW: Feature API with NavCommand
interface UserFeatureApi {
    fun navigateToUserList(filter: String? = null): NavCommand
    fun navigateToUserDetail(username: String): NavCommand
}

class UserFeatureApiImpl @Inject constructor() : UserFeatureApi {

    override fun navigateToUserList(filter: String?): NavCommand {
        val deepLink = buildString {
            append("app://users/list")
            if (filter != null) {
                append("?filter=${Uri.encode(filter)}")
            }
        }
        return NavCommand(
            route = "users/list",
            deepLink = deepLink,
            arguments = filter?.let { mapOf("filter" to it) } ?: emptyMap()
        )
    }

    override fun navigateToUserDetail(username: String): NavCommand {
        return NavCommand(
            route = "users/detail/$username",
            deepLink = "app://users/user/$username",
            arguments = mapOf("username" to username)
        )
    }
}
// ❌ OLD: Testing with AppDestination
@Tes
fun testNavigationToUserDetail() {
    viewModel.navigateToUserDetail("octocat")

    verify(navigationController).navigate(
        AppDestination.UserDetail("octocat")
    )
}
```

#### After
```kotlin
```kotlin
// ❌ OLD: Centralized navigation with AppDestination
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }
}
// ✅ NEW: Feature-owned navigation with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
    // Remove any legacy navigation dependencies
}
// ❌ OLD: Using centralized AppDestination
import com.example.githubusers.navigation.api.AppDestination

sealed interface UserDestination : AppDestination {
    data object UserList : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }
}
// ✅ NEW: Feature-owned destinations
import com.example.githubusers.navigation.api.Destination

sealed interface UserDestination : Destination {
    data class UserList(val filter: String? = null) : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val username: String) : UserDestination {
        override val route = "users/detail/$username"
        override val deepLink = "app://users/user/$username"
    }
}
// ❌ OLD: No deep link handler needed
// Navigation was handled centrally
// ✅ NEW: Feature-owned deep link handler
@OwnsDeepLinks(moduleId = "users")
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> = listOf(
        "app://users",
        "app://users/list",
        "app://users/user/{username}",
        "githubusers://users",
        "githubusers://user/{username}",
        "https://githubusers.example.com/users",
        "https://githubusers.example.com/user/{username}"
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        val path = uri.path ?: return null
        val scheme = uri.scheme ?: return null

        return when {
            isUserListUri(scheme, uri.host, path) -> {
                val filter = uri.getQueryParameter("filter")
                val destination = UserDestination.UserList(filter)
                DeepLinkResult(
                    route = destination.route,
                    deepLink = destination.deepLink,
                    arguments = filter?.let { mapOf("filter" to it) } ?: emptyMap()
                )
            }

            isUserDetailUri(scheme, uri.host, path) -> {
                val username = extractUsername(path)
                if (username != null) {
                    val destination = UserDestination.UserDetail(username)
                    DeepLinkResult(
                        route = destination.route,
                        deepLink = destination.deepLink,
                        arguments = mapOf("username" to username)
                    )
                } else null
            }

            else -> null
        }
    }

    // Helper methods for URI pattern matching
    private fun isUserListUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path in listOf("/users", "/users/list") -> true
            scheme == "githubusers" && path == "/users" -> true
            host == "githubusers.example.com" && path == "/users" -> true
            else -> false
        }

    private fun isUserDetailUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path.startsWith("/users/user/") -> true
            scheme == "githubusers" && path.startsWith("/user/") -> true
            host == "githubusers.example.com" && path.startsWith("/user/") -> true
            else -> false
        }

    private fun extractUsername(path: String): String? =
        when {
            path.startsWith("/users/user/") -> path.removePrefix("/users/user/").takeIf { it.isNotEmpty() }
            path.startsWith("/user/") -> path.removePrefix("/user/").takeIf { it.isNotEmpty() }
            else -> null
        }
}
// ❌ OLD: No registration needed
// Handlers were managed centrally
// ✅ NEW: Register with Hilt multibindings
@Module
@InstallIn(SingletonComponent::class)
abstract class UserNavigationModule {

    @Binds
    @IntoSe
    abstract fun bindUserDeepLinkHandler(
        handler: UserDeepLinkHandler
    ): DeepLinkHandler
}
// ❌ OLD: Direct AppDestination usage
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }

    fun navigateToSearch(query: String) {
        navigationController.navigate(
            AppDestination.Search(query)
        )
    }
}
// ✅ NEW: Use NavCommand with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }

    fun navigateToSearch(query: String) {
        navigationController.navigate(
            NavCommand(
                route = "search",
                deepLink = "app://search?q=${Uri.encode(query)}"
            )
        )
    }
}
// ❌ OLD: FeatureApi with AppDestination
interface UserFeatureApi {
    fun navigateToUserList(): AppDestination.UserLis
    fun navigateToUserDetail(username: String): AppDestination.UserDetail
}
// ✅ NEW: Feature API with NavCommand
interface UserFeatureApi {
    fun navigateToUserList(filter: String? = null): NavCommand
    fun navigateToUserDetail(username: String): NavCommand
}

class UserFeatureApiImpl @Inject constructor() : UserFeatureApi {

    override fun navigateToUserList(filter: String?): NavCommand {
        val deepLink = buildString {
            append("app://users/list")
            if (filter != null) {
                append("?filter=${Uri.encode(filter)}")
            }
        }
        return NavCommand(
            route = "users/list",
            deepLink = deepLink,
            arguments = filter?.let { mapOf("filter" to it) } ?: emptyMap()
        )
    }

    override fun navigateToUserDetail(username: String): NavCommand {
        return NavCommand(
            route = "users/detail/$username",
            deepLink = "app://users/user/$username",
            arguments = mapOf("username" to username)
        )
    }
}
// ❌ OLD: Testing with AppDestination
@Tes
fun testNavigationToUserDetail() {
    viewModel.navigateToUserDetail("octocat")

    verify(navigationController).navigate(
        AppDestination.UserDetail("octocat")
    )
}
// ✅ NEW: Testing with NavCommand
@Tes
fun testNavigationToUserDetail() {
    viewModel.navigateToUserDetail("octocat")

    verify(navigationController).navigate(
        NavCommand(
            route = "users/detail/octocat",
            deepLink = "app://users/user/octocat",
            arguments = mapOf("username" to "octocat")
        )
    )
}

@Tes
fun testDeepLinkHandler() {
    val uri = Uri.parse("app://users/user/octocat")
    val result = userDeepLinkHandler.handleDeepLink(uri)

    assertThat(result).isNotNull()
    assertThat(result?.route).isEqualTo("users/detail/octocat")
    assertThat(result?.deepLink).isEqualTo("app://users/user/octocat")
    assertThat(result?.arguments).containsEntry("username", "octocat")
}
```

---

## 🧪 Testing Migration

### Unit Tests
```kotlin
```kotlin
// ❌ OLD: Centralized navigation with AppDestination
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }
}
// ✅ NEW: Feature-owned navigation with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
    // Remove any legacy navigation dependencies
}
// ❌ OLD: Using centralized AppDestination
import com.example.githubusers.navigation.api.AppDestination

sealed interface UserDestination : AppDestination {
    data object UserList : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }
}
// ✅ NEW: Feature-owned destinations
import com.example.githubusers.navigation.api.Destination

sealed interface UserDestination : Destination {
    data class UserList(val filter: String? = null) : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val username: String) : UserDestination {
        override val route = "users/detail/$username"
        override val deepLink = "app://users/user/$username"
    }
}
// ❌ OLD: No deep link handler needed
// Navigation was handled centrally
// ✅ NEW: Feature-owned deep link handler
@OwnsDeepLinks(moduleId = "users")
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> = listOf(
        "app://users",
        "app://users/list",
        "app://users/user/{username}",
        "githubusers://users",
        "githubusers://user/{username}",
        "https://githubusers.example.com/users",
        "https://githubusers.example.com/user/{username}"
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        val path = uri.path ?: return null
        val scheme = uri.scheme ?: return null

        return when {
            isUserListUri(scheme, uri.host, path) -> {
                val filter = uri.getQueryParameter("filter")
                val destination = UserDestination.UserList(filter)
                DeepLinkResult(
                    route = destination.route,
                    deepLink = destination.deepLink,
                    arguments = filter?.let { mapOf("filter" to it) } ?: emptyMap()
                )
            }

            isUserDetailUri(scheme, uri.host, path) -> {
                val username = extractUsername(path)
                if (username != null) {
                    val destination = UserDestination.UserDetail(username)
                    DeepLinkResult(
                        route = destination.route,
                        deepLink = destination.deepLink,
                        arguments = mapOf("username" to username)
                    )
                } else null
            }

            else -> null
        }
    }

    // Helper methods for URI pattern matching
    private fun isUserListUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path in listOf("/users", "/users/list") -> true
            scheme == "githubusers" && path == "/users" -> true
            host == "githubusers.example.com" && path == "/users" -> true
            else -> false
        }

    private fun isUserDetailUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path.startsWith("/users/user/") -> true
            scheme == "githubusers" && path.startsWith("/user/") -> true
            host == "githubusers.example.com" && path.startsWith("/user/") -> true
            else -> false
        }

    private fun extractUsername(path: String): String? =
        when {
            path.startsWith("/users/user/") -> path.removePrefix("/users/user/").takeIf { it.isNotEmpty() }
            path.startsWith("/user/") -> path.removePrefix("/user/").takeIf { it.isNotEmpty() }
            else -> null
        }
}
// ❌ OLD: No registration needed
// Handlers were managed centrally
// ✅ NEW: Register with Hilt multibindings
@Module
@InstallIn(SingletonComponent::class)
abstract class UserNavigationModule {

    @Binds
    @IntoSe
    abstract fun bindUserDeepLinkHandler(
        handler: UserDeepLinkHandler
    ): DeepLinkHandler
}
// ❌ OLD: Direct AppDestination usage
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }

    fun navigateToSearch(query: String) {
        navigationController.navigate(
            AppDestination.Search(query)
        )
    }
}
// ✅ NEW: Use NavCommand with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }

    fun navigateToSearch(query: String) {
        navigationController.navigate(
            NavCommand(
                route = "search",
                deepLink = "app://search?q=${Uri.encode(query)}"
            )
        )
    }
}
// ❌ OLD: FeatureApi with AppDestination
interface UserFeatureApi {
    fun navigateToUserList(): AppDestination.UserLis
    fun navigateToUserDetail(username: String): AppDestination.UserDetail
}
// ✅ NEW: Feature API with NavCommand
interface UserFeatureApi {
    fun navigateToUserList(filter: String? = null): NavCommand
    fun navigateToUserDetail(username: String): NavCommand
}

class UserFeatureApiImpl @Inject constructor() : UserFeatureApi {

    override fun navigateToUserList(filter: String?): NavCommand {
        val deepLink = buildString {
            append("app://users/list")
            if (filter != null) {
                append("?filter=${Uri.encode(filter)}")
            }
        }
        return NavCommand(
            route = "users/list",
            deepLink = deepLink,
            arguments = filter?.let { mapOf("filter" to it) } ?: emptyMap()
        )
    }

    override fun navigateToUserDetail(username: String): NavCommand {
        return NavCommand(
            route = "users/detail/$username",
            deepLink = "app://users/user/$username",
            arguments = mapOf("username" to username)
        )
    }
}
// ❌ OLD: Testing with AppDestination
@Tes
fun testNavigationToUserDetail() {
    viewModel.navigateToUserDetail("octocat")

    verify(navigationController).navigate(
        AppDestination.UserDetail("octocat")
    )
}
// ✅ NEW: Testing with NavCommand
@Tes
fun testNavigationToUserDetail() {
    viewModel.navigateToUserDetail("octocat")

    verify(navigationController).navigate(
        NavCommand(
            route = "users/detail/octocat",
            deepLink = "app://users/user/octocat",
            arguments = mapOf("username" to "octocat")
        )
    )
}

@Tes
fun testDeepLinkHandler() {
    val uri = Uri.parse("app://users/user/octocat")
    val result = userDeepLinkHandler.handleDeepLink(uri)

    assertThat(result).isNotNull()
    assertThat(result?.route).isEqualTo("users/detail/octocat")
    assertThat(result?.deepLink).isEqualTo("app://users/user/octocat")
    assertThat(result?.arguments).containsEntry("username", "octocat")
}
class UserDeepLinkHandlerTest {

    @Tes
    fun `handleDeepLink with user list URI returns correct result`() {
        val uri = Uri.parse("app://users/list")
        val result = handler.handleDeepLink(uri)

        assertThat(result).isNotNull()
        assertThat(result?.route).isEqualTo("users/list")
        assertThat(result?.deepLink).isEqualTo("app://users/list")
    }

    @Tes
    fun `handleDeepLink with user detail URI returns correct result`() {
        val uri = Uri.parse("app://users/user/octocat")
        val result = handler.handleDeepLink(uri)

        assertThat(result).isNotNull()
        assertThat(result?.route).isEqualTo("users/detail/octocat")
        assertThat(result?.arguments).containsEntry("username", "octocat")
    }

    @Tes
    fun `handleDeepLink with unsupported URI returns null`() {
        val uri = Uri.parse("app://unsupported/path")
        val result = handler.handleDeepLink(uri)

        assertThat(result).isNull()
    }
}
```

### Integration Tests
```kotlin
```kotlin
// ❌ OLD: Centralized navigation with AppDestination
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }
}
// ✅ NEW: Feature-owned navigation with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
    // Remove any legacy navigation dependencies
}
// ❌ OLD: Using centralized AppDestination
import com.example.githubusers.navigation.api.AppDestination

sealed interface UserDestination : AppDestination {
    data object UserList : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }
}
// ✅ NEW: Feature-owned destinations
import com.example.githubusers.navigation.api.Destination

sealed interface UserDestination : Destination {
    data class UserList(val filter: String? = null) : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val username: String) : UserDestination {
        override val route = "users/detail/$username"
        override val deepLink = "app://users/user/$username"
    }
}
// ❌ OLD: No deep link handler needed
// Navigation was handled centrally
// ✅ NEW: Feature-owned deep link handler
@OwnsDeepLinks(moduleId = "users")
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> = listOf(
        "app://users",
        "app://users/list",
        "app://users/user/{username}",
        "githubusers://users",
        "githubusers://user/{username}",
        "https://githubusers.example.com/users",
        "https://githubusers.example.com/user/{username}"
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        val path = uri.path ?: return null
        val scheme = uri.scheme ?: return null

        return when {
            isUserListUri(scheme, uri.host, path) -> {
                val filter = uri.getQueryParameter("filter")
                val destination = UserDestination.UserList(filter)
                DeepLinkResult(
                    route = destination.route,
                    deepLink = destination.deepLink,
                    arguments = filter?.let { mapOf("filter" to it) } ?: emptyMap()
                )
            }

            isUserDetailUri(scheme, uri.host, path) -> {
                val username = extractUsername(path)
                if (username != null) {
                    val destination = UserDestination.UserDetail(username)
                    DeepLinkResult(
                        route = destination.route,
                        deepLink = destination.deepLink,
                        arguments = mapOf("username" to username)
                    )
                } else null
            }

            else -> null
        }
    }

    // Helper methods for URI pattern matching
    private fun isUserListUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path in listOf("/users", "/users/list") -> true
            scheme == "githubusers" && path == "/users" -> true
            host == "githubusers.example.com" && path == "/users" -> true
            else -> false
        }

    private fun isUserDetailUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path.startsWith("/users/user/") -> true
            scheme == "githubusers" && path.startsWith("/user/") -> true
            host == "githubusers.example.com" && path.startsWith("/user/") -> true
            else -> false
        }

    private fun extractUsername(path: String): String? =
        when {
            path.startsWith("/users/user/") -> path.removePrefix("/users/user/").takeIf { it.isNotEmpty() }
            path.startsWith("/user/") -> path.removePrefix("/user/").takeIf { it.isNotEmpty() }
            else -> null
        }
}
// ❌ OLD: No registration needed
// Handlers were managed centrally
// ✅ NEW: Register with Hilt multibindings
@Module
@InstallIn(SingletonComponent::class)
abstract class UserNavigationModule {

    @Binds
    @IntoSe
    abstract fun bindUserDeepLinkHandler(
        handler: UserDeepLinkHandler
    ): DeepLinkHandler
}
// ❌ OLD: Direct AppDestination usage
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }

    fun navigateToSearch(query: String) {
        navigationController.navigate(
            AppDestination.Search(query)
        )
    }
}
// ✅ NEW: Use NavCommand with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }

    fun navigateToSearch(query: String) {
        navigationController.navigate(
            NavCommand(
                route = "search",
                deepLink = "app://search?q=${Uri.encode(query)}"
            )
        )
    }
}
// ❌ OLD: FeatureApi with AppDestination
interface UserFeatureApi {
    fun navigateToUserList(): AppDestination.UserLis
    fun navigateToUserDetail(username: String): AppDestination.UserDetail
}
// ✅ NEW: Feature API with NavCommand
interface UserFeatureApi {
    fun navigateToUserList(filter: String? = null): NavCommand
    fun navigateToUserDetail(username: String): NavCommand
}

class UserFeatureApiImpl @Inject constructor() : UserFeatureApi {

    override fun navigateToUserList(filter: String?): NavCommand {
        val deepLink = buildString {
            append("app://users/list")
            if (filter != null) {
                append("?filter=${Uri.encode(filter)}")
            }
        }
        return NavCommand(
            route = "users/list",
            deepLink = deepLink,
            arguments = filter?.let { mapOf("filter" to it) } ?: emptyMap()
        )
    }

    override fun navigateToUserDetail(username: String): NavCommand {
        return NavCommand(
            route = "users/detail/$username",
            deepLink = "app://users/user/$username",
            arguments = mapOf("username" to username)
        )
    }
}
// ❌ OLD: Testing with AppDestination
@Tes
fun testNavigationToUserDetail() {
    viewModel.navigateToUserDetail("octocat")

    verify(navigationController).navigate(
        AppDestination.UserDetail("octocat")
    )
}
// ✅ NEW: Testing with NavCommand
@Tes
fun testNavigationToUserDetail() {
    viewModel.navigateToUserDetail("octocat")

    verify(navigationController).navigate(
        NavCommand(
            route = "users/detail/octocat",
            deepLink = "app://users/user/octocat",
            arguments = mapOf("username" to "octocat")
        )
    )
}

@Tes
fun testDeepLinkHandler() {
    val uri = Uri.parse("app://users/user/octocat")
    val result = userDeepLinkHandler.handleDeepLink(uri)

    assertThat(result).isNotNull()
    assertThat(result?.route).isEqualTo("users/detail/octocat")
    assertThat(result?.deepLink).isEqualTo("app://users/user/octocat")
    assertThat(result?.arguments).containsEntry("username", "octocat")
}
class UserDeepLinkHandlerTest {

    @Tes
    fun `handleDeepLink with user list URI returns correct result`() {
        val uri = Uri.parse("app://users/list")
        val result = handler.handleDeepLink(uri)

        assertThat(result).isNotNull()
        assertThat(result?.route).isEqualTo("users/list")
        assertThat(result?.deepLink).isEqualTo("app://users/list")
    }

    @Tes
    fun `handleDeepLink with user detail URI returns correct result`() {
        val uri = Uri.parse("app://users/user/octocat")
        val result = handler.handleDeepLink(uri)

        assertThat(result).isNotNull()
        assertThat(result?.route).isEqualTo("users/detail/octocat")
        assertThat(result?.arguments).containsEntry("username", "octocat")
    }

    @Tes
    fun `handleDeepLink with unsupported URI returns null`() {
        val uri = Uri.parse("app://unsupported/path")
        val result = handler.handleDeepLink(uri)

        assertThat(result).isNull()
    }
}
class UserNavigationIntegrationTest {

    @Tes
    fun testCrossModuleNavigation() = runTest {
        // Navigate from users to search
        val searchCommand = NavCommand(
            route = "search",
            deepLink = "app://search?q=test"
        )

        navigationController.navigate(searchCommand)

        // Verify navigation occurred
        assertThat(navigationController.currentEntry.value?.deepLink)
            .isEqualTo("app://search?q=test")
    }
}
```

---

## 🚨 Common Migration Issues

### Issue 1: Missing Deep Link Handler Registration
**Problem**: Deep links not being handled
**Solution**: Ensure handler is registered with Hilt `@IntoSet`

```kotlin
```kotlin
// ❌ OLD: Centralized navigation with AppDestination
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }
}
// ✅ NEW: Feature-owned navigation with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
    // Remove any legacy navigation dependencies
}
// ❌ OLD: Using centralized AppDestination
import com.example.githubusers.navigation.api.AppDestination

sealed interface UserDestination : AppDestination {
    data object UserList : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }
}
// ✅ NEW: Feature-owned destinations
import com.example.githubusers.navigation.api.Destination

sealed interface UserDestination : Destination {
    data class UserList(val filter: String? = null) : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val username: String) : UserDestination {
        override val route = "users/detail/$username"
        override val deepLink = "app://users/user/$username"
    }
}
// ❌ OLD: No deep link handler needed
// Navigation was handled centrally
// ✅ NEW: Feature-owned deep link handler
@OwnsDeepLinks(moduleId = "users")
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> = listOf(
        "app://users",
        "app://users/list",
        "app://users/user/{username}",
        "githubusers://users",
        "githubusers://user/{username}",
        "https://githubusers.example.com/users",
        "https://githubusers.example.com/user/{username}"
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        val path = uri.path ?: return null
        val scheme = uri.scheme ?: return null

        return when {
            isUserListUri(scheme, uri.host, path) -> {
                val filter = uri.getQueryParameter("filter")
                val destination = UserDestination.UserList(filter)
                DeepLinkResult(
                    route = destination.route,
                    deepLink = destination.deepLink,
                    arguments = filter?.let { mapOf("filter" to it) } ?: emptyMap()
                )
            }

            isUserDetailUri(scheme, uri.host, path) -> {
                val username = extractUsername(path)
                if (username != null) {
                    val destination = UserDestination.UserDetail(username)
                    DeepLinkResult(
                        route = destination.route,
                        deepLink = destination.deepLink,
                        arguments = mapOf("username" to username)
                    )
                } else null
            }

            else -> null
        }
    }

    // Helper methods for URI pattern matching
    private fun isUserListUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path in listOf("/users", "/users/list") -> true
            scheme == "githubusers" && path == "/users" -> true
            host == "githubusers.example.com" && path == "/users" -> true
            else -> false
        }

    private fun isUserDetailUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path.startsWith("/users/user/") -> true
            scheme == "githubusers" && path.startsWith("/user/") -> true
            host == "githubusers.example.com" && path.startsWith("/user/") -> true
            else -> false
        }

    private fun extractUsername(path: String): String? =
        when {
            path.startsWith("/users/user/") -> path.removePrefix("/users/user/").takeIf { it.isNotEmpty() }
            path.startsWith("/user/") -> path.removePrefix("/user/").takeIf { it.isNotEmpty() }
            else -> null
        }
}
// ❌ OLD: No registration needed
// Handlers were managed centrally
// ✅ NEW: Register with Hilt multibindings
@Module
@InstallIn(SingletonComponent::class)
abstract class UserNavigationModule {

    @Binds
    @IntoSe
    abstract fun bindUserDeepLinkHandler(
        handler: UserDeepLinkHandler
    ): DeepLinkHandler
}
// ❌ OLD: Direct AppDestination usage
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }

    fun navigateToSearch(query: String) {
        navigationController.navigate(
            AppDestination.Search(query)
        )
    }
}
// ✅ NEW: Use NavCommand with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }

    fun navigateToSearch(query: String) {
        navigationController.navigate(
            NavCommand(
                route = "search",
                deepLink = "app://search?q=${Uri.encode(query)}"
            )
        )
    }
}
// ❌ OLD: FeatureApi with AppDestination
interface UserFeatureApi {
    fun navigateToUserList(): AppDestination.UserLis
    fun navigateToUserDetail(username: String): AppDestination.UserDetail
}
// ✅ NEW: Feature API with NavCommand
interface UserFeatureApi {
    fun navigateToUserList(filter: String? = null): NavCommand
    fun navigateToUserDetail(username: String): NavCommand
}

class UserFeatureApiImpl @Inject constructor() : UserFeatureApi {

    override fun navigateToUserList(filter: String?): NavCommand {
        val deepLink = buildString {
            append("app://users/list")
            if (filter != null) {
                append("?filter=${Uri.encode(filter)}")
            }
        }
        return NavCommand(
            route = "users/list",
            deepLink = deepLink,
            arguments = filter?.let { mapOf("filter" to it) } ?: emptyMap()
        )
    }

    override fun navigateToUserDetail(username: String): NavCommand {
        return NavCommand(
            route = "users/detail/$username",
            deepLink = "app://users/user/$username",
            arguments = mapOf("username" to username)
        )
    }
}
// ❌ OLD: Testing with AppDestination
@Tes
fun testNavigationToUserDetail() {
    viewModel.navigateToUserDetail("octocat")

    verify(navigationController).navigate(
        AppDestination.UserDetail("octocat")
    )
}
// ✅ NEW: Testing with NavCommand
@Tes
fun testNavigationToUserDetail() {
    viewModel.navigateToUserDetail("octocat")

    verify(navigationController).navigate(
        NavCommand(
            route = "users/detail/octocat",
            deepLink = "app://users/user/octocat",
            arguments = mapOf("username" to "octocat")
        )
    )
}

@Tes
fun testDeepLinkHandler() {
    val uri = Uri.parse("app://users/user/octocat")
    val result = userDeepLinkHandler.handleDeepLink(uri)

    assertThat(result).isNotNull()
    assertThat(result?.route).isEqualTo("users/detail/octocat")
    assertThat(result?.deepLink).isEqualTo("app://users/user/octocat")
    assertThat(result?.arguments).containsEntry("username", "octocat")
}
class UserDeepLinkHandlerTest {

    @Tes
    fun `handleDeepLink with user list URI returns correct result`() {
        val uri = Uri.parse("app://users/list")
        val result = handler.handleDeepLink(uri)

        assertThat(result).isNotNull()
        assertThat(result?.route).isEqualTo("users/list")
        assertThat(result?.deepLink).isEqualTo("app://users/list")
    }

    @Tes
    fun `handleDeepLink with user detail URI returns correct result`() {
        val uri = Uri.parse("app://users/user/octocat")
        val result = handler.handleDeepLink(uri)

        assertThat(result).isNotNull()
        assertThat(result?.route).isEqualTo("users/detail/octocat")
        assertThat(result?.arguments).containsEntry("username", "octocat")
    }

    @Tes
    fun `handleDeepLink with unsupported URI returns null`() {
        val uri = Uri.parse("app://unsupported/path")
        val result = handler.handleDeepLink(uri)

        assertThat(result).isNull()
    }
}
class UserNavigationIntegrationTest {

    @Tes
    fun testCrossModuleNavigation() = runTest {
        // Navigate from users to search
        val searchCommand = NavCommand(
            route = "search",
            deepLink = "app://search?q=test"
        )

        navigationController.navigate(searchCommand)

        // Verify navigation occurred
        assertThat(navigationController.currentEntry.value?.deepLink)
            .isEqualTo("app://search?q=test")
    }
}
// ✅ CORRECT: Register with @IntoSe
@Binds
@IntoSe
abstract fun bindUserDeepLinkHandler(
    handler: UserDeepLinkHandler
): DeepLinkHandler
```

### Issue 2: Incorrect URI Pattern Matching
**Problem**: Deep links not matching expected patterns
**Solution**: Use exact pattern matching in handler

```kotlin
```kotlin
// ❌ OLD: Centralized navigation with AppDestination
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }
}
// ✅ NEW: Feature-owned navigation with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
    // Remove any legacy navigation dependencies
}
// ❌ OLD: Using centralized AppDestination
import com.example.githubusers.navigation.api.AppDestination

sealed interface UserDestination : AppDestination {
    data object UserList : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }
}
// ✅ NEW: Feature-owned destinations
import com.example.githubusers.navigation.api.Destination

sealed interface UserDestination : Destination {
    data class UserList(val filter: String? = null) : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val username: String) : UserDestination {
        override val route = "users/detail/$username"
        override val deepLink = "app://users/user/$username"
    }
}
// ❌ OLD: No deep link handler needed
// Navigation was handled centrally
// ✅ NEW: Feature-owned deep link handler
@OwnsDeepLinks(moduleId = "users")
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> = listOf(
        "app://users",
        "app://users/list",
        "app://users/user/{username}",
        "githubusers://users",
        "githubusers://user/{username}",
        "https://githubusers.example.com/users",
        "https://githubusers.example.com/user/{username}"
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        val path = uri.path ?: return null
        val scheme = uri.scheme ?: return null

        return when {
            isUserListUri(scheme, uri.host, path) -> {
                val filter = uri.getQueryParameter("filter")
                val destination = UserDestination.UserList(filter)
                DeepLinkResult(
                    route = destination.route,
                    deepLink = destination.deepLink,
                    arguments = filter?.let { mapOf("filter" to it) } ?: emptyMap()
                )
            }

            isUserDetailUri(scheme, uri.host, path) -> {
                val username = extractUsername(path)
                if (username != null) {
                    val destination = UserDestination.UserDetail(username)
                    DeepLinkResult(
                        route = destination.route,
                        deepLink = destination.deepLink,
                        arguments = mapOf("username" to username)
                    )
                } else null
            }

            else -> null
        }
    }

    // Helper methods for URI pattern matching
    private fun isUserListUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path in listOf("/users", "/users/list") -> true
            scheme == "githubusers" && path == "/users" -> true
            host == "githubusers.example.com" && path == "/users" -> true
            else -> false
        }

    private fun isUserDetailUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path.startsWith("/users/user/") -> true
            scheme == "githubusers" && path.startsWith("/user/") -> true
            host == "githubusers.example.com" && path.startsWith("/user/") -> true
            else -> false
        }

    private fun extractUsername(path: String): String? =
        when {
            path.startsWith("/users/user/") -> path.removePrefix("/users/user/").takeIf { it.isNotEmpty() }
            path.startsWith("/user/") -> path.removePrefix("/user/").takeIf { it.isNotEmpty() }
            else -> null
        }
}
// ❌ OLD: No registration needed
// Handlers were managed centrally
// ✅ NEW: Register with Hilt multibindings
@Module
@InstallIn(SingletonComponent::class)
abstract class UserNavigationModule {

    @Binds
    @IntoSe
    abstract fun bindUserDeepLinkHandler(
        handler: UserDeepLinkHandler
    ): DeepLinkHandler
}
// ❌ OLD: Direct AppDestination usage
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }

    fun navigateToSearch(query: String) {
        navigationController.navigate(
            AppDestination.Search(query)
        )
    }
}
// ✅ NEW: Use NavCommand with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }

    fun navigateToSearch(query: String) {
        navigationController.navigate(
            NavCommand(
                route = "search",
                deepLink = "app://search?q=${Uri.encode(query)}"
            )
        )
    }
}
// ❌ OLD: FeatureApi with AppDestination
interface UserFeatureApi {
    fun navigateToUserList(): AppDestination.UserLis
    fun navigateToUserDetail(username: String): AppDestination.UserDetail
}
// ✅ NEW: Feature API with NavCommand
interface UserFeatureApi {
    fun navigateToUserList(filter: String? = null): NavCommand
    fun navigateToUserDetail(username: String): NavCommand
}

class UserFeatureApiImpl @Inject constructor() : UserFeatureApi {

    override fun navigateToUserList(filter: String?): NavCommand {
        val deepLink = buildString {
            append("app://users/list")
            if (filter != null) {
                append("?filter=${Uri.encode(filter)}")
            }
        }
        return NavCommand(
            route = "users/list",
            deepLink = deepLink,
            arguments = filter?.let { mapOf("filter" to it) } ?: emptyMap()
        )
    }

    override fun navigateToUserDetail(username: String): NavCommand {
        return NavCommand(
            route = "users/detail/$username",
            deepLink = "app://users/user/$username",
            arguments = mapOf("username" to username)
        )
    }
}
// ❌ OLD: Testing with AppDestination
@Tes
fun testNavigationToUserDetail() {
    viewModel.navigateToUserDetail("octocat")

    verify(navigationController).navigate(
        AppDestination.UserDetail("octocat")
    )
}
// ✅ NEW: Testing with NavCommand
@Tes
fun testNavigationToUserDetail() {
    viewModel.navigateToUserDetail("octocat")

    verify(navigationController).navigate(
        NavCommand(
            route = "users/detail/octocat",
            deepLink = "app://users/user/octocat",
            arguments = mapOf("username" to "octocat")
        )
    )
}

@Tes
fun testDeepLinkHandler() {
    val uri = Uri.parse("app://users/user/octocat")
    val result = userDeepLinkHandler.handleDeepLink(uri)

    assertThat(result).isNotNull()
    assertThat(result?.route).isEqualTo("users/detail/octocat")
    assertThat(result?.deepLink).isEqualTo("app://users/user/octocat")
    assertThat(result?.arguments).containsEntry("username", "octocat")
}
class UserDeepLinkHandlerTest {

    @Tes
    fun `handleDeepLink with user list URI returns correct result`() {
        val uri = Uri.parse("app://users/list")
        val result = handler.handleDeepLink(uri)

        assertThat(result).isNotNull()
        assertThat(result?.route).isEqualTo("users/list")
        assertThat(result?.deepLink).isEqualTo("app://users/list")
    }

    @Tes
    fun `handleDeepLink with user detail URI returns correct result`() {
        val uri = Uri.parse("app://users/user/octocat")
        val result = handler.handleDeepLink(uri)

        assertThat(result).isNotNull()
        assertThat(result?.route).isEqualTo("users/detail/octocat")
        assertThat(result?.arguments).containsEntry("username", "octocat")
    }

    @Tes
    fun `handleDeepLink with unsupported URI returns null`() {
        val uri = Uri.parse("app://unsupported/path")
        val result = handler.handleDeepLink(uri)

        assertThat(result).isNull()
    }
}
class UserNavigationIntegrationTest {

    @Tes
    fun testCrossModuleNavigation() = runTest {
        // Navigate from users to search
        val searchCommand = NavCommand(
            route = "search",
            deepLink = "app://search?q=test"
        )

        navigationController.navigate(searchCommand)

        // Verify navigation occurred
        assertThat(navigationController.currentEntry.value?.deepLink)
            .isEqualTo("app://search?q=test")
    }
}
// ✅ CORRECT: Register with @IntoSe
@Binds
@IntoSe
abstract fun bindUserDeepLinkHandler(
    handler: UserDeepLinkHandler
): DeepLinkHandler
// ✅ CORRECT: Exact pattern matching
private fun isUserListUri(scheme: String, host: String?, path: String): Boolean =
    when {
        scheme == "app" && path in listOf("/users", "/users/list") -> true
        scheme == "githubusers" && path == "/users" -> true
        host == "githubusers.example.com" && path == "/users" -> true
        else -> false
    }
```

### Issue 3: Missing Arguments in NavCommand
**Problem**: Arguments not being passed correctly
**Solution**: Include all necessary arguments in NavCommand

```kotlin
```kotlin
// ❌ OLD: Centralized navigation with AppDestination
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }
}
// ✅ NEW: Feature-owned navigation with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
    // Remove any legacy navigation dependencies
}
// ❌ OLD: Using centralized AppDestination
import com.example.githubusers.navigation.api.AppDestination

sealed interface UserDestination : AppDestination {
    data object UserList : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }
}
// ✅ NEW: Feature-owned destinations
import com.example.githubusers.navigation.api.Destination

sealed interface UserDestination : Destination {
    data class UserList(val filter: String? = null) : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val username: String) : UserDestination {
        override val route = "users/detail/$username"
        override val deepLink = "app://users/user/$username"
    }
}
// ❌ OLD: No deep link handler needed
// Navigation was handled centrally
// ✅ NEW: Feature-owned deep link handler
@OwnsDeepLinks(moduleId = "users")
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> = listOf(
        "app://users",
        "app://users/list",
        "app://users/user/{username}",
        "githubusers://users",
        "githubusers://user/{username}",
        "https://githubusers.example.com/users",
        "https://githubusers.example.com/user/{username}"
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        val path = uri.path ?: return null
        val scheme = uri.scheme ?: return null

        return when {
            isUserListUri(scheme, uri.host, path) -> {
                val filter = uri.getQueryParameter("filter")
                val destination = UserDestination.UserList(filter)
                DeepLinkResult(
                    route = destination.route,
                    deepLink = destination.deepLink,
                    arguments = filter?.let { mapOf("filter" to it) } ?: emptyMap()
                )
            }

            isUserDetailUri(scheme, uri.host, path) -> {
                val username = extractUsername(path)
                if (username != null) {
                    val destination = UserDestination.UserDetail(username)
                    DeepLinkResult(
                        route = destination.route,
                        deepLink = destination.deepLink,
                        arguments = mapOf("username" to username)
                    )
                } else null
            }

            else -> null
        }
    }

    // Helper methods for URI pattern matching
    private fun isUserListUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path in listOf("/users", "/users/list") -> true
            scheme == "githubusers" && path == "/users" -> true
            host == "githubusers.example.com" && path == "/users" -> true
            else -> false
        }

    private fun isUserDetailUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path.startsWith("/users/user/") -> true
            scheme == "githubusers" && path.startsWith("/user/") -> true
            host == "githubusers.example.com" && path.startsWith("/user/") -> true
            else -> false
        }

    private fun extractUsername(path: String): String? =
        when {
            path.startsWith("/users/user/") -> path.removePrefix("/users/user/").takeIf { it.isNotEmpty() }
            path.startsWith("/user/") -> path.removePrefix("/user/").takeIf { it.isNotEmpty() }
            else -> null
        }
}
// ❌ OLD: No registration needed
// Handlers were managed centrally
// ✅ NEW: Register with Hilt multibindings
@Module
@InstallIn(SingletonComponent::class)
abstract class UserNavigationModule {

    @Binds
    @IntoSe
    abstract fun bindUserDeepLinkHandler(
        handler: UserDeepLinkHandler
    ): DeepLinkHandler
}
// ❌ OLD: Direct AppDestination usage
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }

    fun navigateToSearch(query: String) {
        navigationController.navigate(
            AppDestination.Search(query)
        )
    }
}
// ✅ NEW: Use NavCommand with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }

    fun navigateToSearch(query: String) {
        navigationController.navigate(
            NavCommand(
                route = "search",
                deepLink = "app://search?q=${Uri.encode(query)}"
            )
        )
    }
}
// ❌ OLD: FeatureApi with AppDestination
interface UserFeatureApi {
    fun navigateToUserList(): AppDestination.UserLis
    fun navigateToUserDetail(username: String): AppDestination.UserDetail
}
// ✅ NEW: Feature API with NavCommand
interface UserFeatureApi {
    fun navigateToUserList(filter: String? = null): NavCommand
    fun navigateToUserDetail(username: String): NavCommand
}

class UserFeatureApiImpl @Inject constructor() : UserFeatureApi {

    override fun navigateToUserList(filter: String?): NavCommand {
        val deepLink = buildString {
            append("app://users/list")
            if (filter != null) {
                append("?filter=${Uri.encode(filter)}")
            }
        }
        return NavCommand(
            route = "users/list",
            deepLink = deepLink,
            arguments = filter?.let { mapOf("filter" to it) } ?: emptyMap()
        )
    }

    override fun navigateToUserDetail(username: String): NavCommand {
        return NavCommand(
            route = "users/detail/$username",
            deepLink = "app://users/user/$username",
            arguments = mapOf("username" to username)
        )
    }
}
// ❌ OLD: Testing with AppDestination
@Tes
fun testNavigationToUserDetail() {
    viewModel.navigateToUserDetail("octocat")

    verify(navigationController).navigate(
        AppDestination.UserDetail("octocat")
    )
}
// ✅ NEW: Testing with NavCommand
@Tes
fun testNavigationToUserDetail() {
    viewModel.navigateToUserDetail("octocat")

    verify(navigationController).navigate(
        NavCommand(
            route = "users/detail/octocat",
            deepLink = "app://users/user/octocat",
            arguments = mapOf("username" to "octocat")
        )
    )
}

@Tes
fun testDeepLinkHandler() {
    val uri = Uri.parse("app://users/user/octocat")
    val result = userDeepLinkHandler.handleDeepLink(uri)

    assertThat(result).isNotNull()
    assertThat(result?.route).isEqualTo("users/detail/octocat")
    assertThat(result?.deepLink).isEqualTo("app://users/user/octocat")
    assertThat(result?.arguments).containsEntry("username", "octocat")
}
class UserDeepLinkHandlerTest {

    @Tes
    fun `handleDeepLink with user list URI returns correct result`() {
        val uri = Uri.parse("app://users/list")
        val result = handler.handleDeepLink(uri)

        assertThat(result).isNotNull()
        assertThat(result?.route).isEqualTo("users/list")
        assertThat(result?.deepLink).isEqualTo("app://users/list")
    }

    @Tes
    fun `handleDeepLink with user detail URI returns correct result`() {
        val uri = Uri.parse("app://users/user/octocat")
        val result = handler.handleDeepLink(uri)

        assertThat(result).isNotNull()
        assertThat(result?.route).isEqualTo("users/detail/octocat")
        assertThat(result?.arguments).containsEntry("username", "octocat")
    }

    @Tes
    fun `handleDeepLink with unsupported URI returns null`() {
        val uri = Uri.parse("app://unsupported/path")
        val result = handler.handleDeepLink(uri)

        assertThat(result).isNull()
    }
}
class UserNavigationIntegrationTest {

    @Tes
    fun testCrossModuleNavigation() = runTest {
        // Navigate from users to search
        val searchCommand = NavCommand(
            route = "search",
            deepLink = "app://search?q=test"
        )

        navigationController.navigate(searchCommand)

        // Verify navigation occurred
        assertThat(navigationController.currentEntry.value?.deepLink)
            .isEqualTo("app://search?q=test")
    }
}
// ✅ CORRECT: Register with @IntoSe
@Binds
@IntoSe
abstract fun bindUserDeepLinkHandler(
    handler: UserDeepLinkHandler
): DeepLinkHandler
// ✅ CORRECT: Exact pattern matching
private fun isUserListUri(scheme: String, host: String?, path: String): Boolean =
    when {
        scheme == "app" && path in listOf("/users", "/users/list") -> true
        scheme == "githubusers" && path == "/users" -> true
        host == "githubusers.example.com" && path == "/users" -> true
        else -> false
    }
// ✅ CORRECT: Include all arguments
NavCommand(
    route = "users/detail/$username",
    deepLink = "app://users/user/$username",
    arguments = mapOf("username" to username)
)
```

---

## 📊 Migration Validation

### Checklist for Each Feature Module

- [ ] **Deep Link Handler**: Implemented and registered
- [ ] **Destination Interface**: Updated to extend `Destination`
- [ ] **Navigation Calls**: Updated to use `NavCommand`
- [ ] **Feature API**: Updated (if applicable)
- [ ] **Tests**: Updated and passing
- [ ] **Documentation**: Updated deep link patterns

### Validation Commands

```bash
```kotlin
// ❌ OLD: Centralized navigation with AppDestination
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }
}
// ✅ NEW: Feature-owned navigation with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
}
// build.gradle.kts
dependencies {
    implementation(project(":navigation-api"))
    implementation(project(":navigation-impl"))
    // Remove any legacy navigation dependencies
}
// ❌ OLD: Using centralized AppDestination
import com.example.githubusers.navigation.api.AppDestination

sealed interface UserDestination : AppDestination {
    data object UserList : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }
}
// ✅ NEW: Feature-owned destinations
import com.example.githubusers.navigation.api.Destination

sealed interface UserDestination : Destination {
    data class UserList(val filter: String? = null) : UserDestination {
        override val route = "users/list"
        override val deepLink = "app://users/list"
    }

    data class UserDetail(val username: String) : UserDestination {
        override val route = "users/detail/$username"
        override val deepLink = "app://users/user/$username"
    }
}
// ❌ OLD: No deep link handler needed
// Navigation was handled centrally
// ✅ NEW: Feature-owned deep link handler
@OwnsDeepLinks(moduleId = "users")
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> = listOf(
        "app://users",
        "app://users/list",
        "app://users/user/{username}",
        "githubusers://users",
        "githubusers://user/{username}",
        "https://githubusers.example.com/users",
        "https://githubusers.example.com/user/{username}"
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        val path = uri.path ?: return null
        val scheme = uri.scheme ?: return null

        return when {
            isUserListUri(scheme, uri.host, path) -> {
                val filter = uri.getQueryParameter("filter")
                val destination = UserDestination.UserList(filter)
                DeepLinkResult(
                    route = destination.route,
                    deepLink = destination.deepLink,
                    arguments = filter?.let { mapOf("filter" to it) } ?: emptyMap()
                )
            }

            isUserDetailUri(scheme, uri.host, path) -> {
                val username = extractUsername(path)
                if (username != null) {
                    val destination = UserDestination.UserDetail(username)
                    DeepLinkResult(
                        route = destination.route,
                        deepLink = destination.deepLink,
                        arguments = mapOf("username" to username)
                    )
                } else null
            }

            else -> null
        }
    }

    // Helper methods for URI pattern matching
    private fun isUserListUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path in listOf("/users", "/users/list") -> true
            scheme == "githubusers" && path == "/users" -> true
            host == "githubusers.example.com" && path == "/users" -> true
            else -> false
        }

    private fun isUserDetailUri(scheme: String, host: String?, path: String): Boolean =
        when {
            scheme == "app" && path.startsWith("/users/user/") -> true
            scheme == "githubusers" && path.startsWith("/user/") -> true
            host == "githubusers.example.com" && path.startsWith("/user/") -> true
            else -> false
        }

    private fun extractUsername(path: String): String? =
        when {
            path.startsWith("/users/user/") -> path.removePrefix("/users/user/").takeIf { it.isNotEmpty() }
            path.startsWith("/user/") -> path.removePrefix("/user/").takeIf { it.isNotEmpty() }
            else -> null
        }
}
// ❌ OLD: No registration needed
// Handlers were managed centrally
// ✅ NEW: Register with Hilt multibindings
@Module
@InstallIn(SingletonComponent::class)
abstract class UserNavigationModule {

    @Binds
    @IntoSe
    abstract fun bindUserDeepLinkHandler(
        handler: UserDeepLinkHandler
    ): DeepLinkHandler
}
// ❌ OLD: Direct AppDestination usage
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            AppDestination.UserDetail(username)
        )
    }

    fun navigateToSearch(query: String) {
        navigationController.navigate(
            AppDestination.Search(query)
        )
    }
}
// ✅ NEW: Use NavCommand with deep links
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        navigationController.navigate(
            NavCommand(
                route = "users/detail/$username",
                deepLink = "app://users/user/$username"
            )
        )
    }

    fun navigateToSearch(query: String) {
        navigationController.navigate(
            NavCommand(
                route = "search",
                deepLink = "app://search?q=${Uri.encode(query)}"
            )
        )
    }
}
// ❌ OLD: FeatureApi with AppDestination
interface UserFeatureApi {
    fun navigateToUserList(): AppDestination.UserLis
    fun navigateToUserDetail(username: String): AppDestination.UserDetail
}
// ✅ NEW: Feature API with NavCommand
interface UserFeatureApi {
    fun navigateToUserList(filter: String? = null): NavCommand
    fun navigateToUserDetail(username: String): NavCommand
}

class UserFeatureApiImpl @Inject constructor() : UserFeatureApi {

    override fun navigateToUserList(filter: String?): NavCommand {
        val deepLink = buildString {
            append("app://users/list")
            if (filter != null) {
                append("?filter=${Uri.encode(filter)}")
            }
        }
        return NavCommand(
            route = "users/list",
            deepLink = deepLink,
            arguments = filter?.let { mapOf("filter" to it) } ?: emptyMap()
        )
    }

    override fun navigateToUserDetail(username: String): NavCommand {
        return NavCommand(
            route = "users/detail/$username",
            deepLink = "app://users/user/$username",
            arguments = mapOf("username" to username)
        )
    }
}
// ❌ OLD: Testing with AppDestination
@Tes
fun testNavigationToUserDetail() {
    viewModel.navigateToUserDetail("octocat")

    verify(navigationController).navigate(
        AppDestination.UserDetail("octocat")
    )
}
// ✅ NEW: Testing with NavCommand
@Tes
fun testNavigationToUserDetail() {
    viewModel.navigateToUserDetail("octocat")

    verify(navigationController).navigate(
        NavCommand(
            route = "users/detail/octocat",
            deepLink = "app://users/user/octocat",
            arguments = mapOf("username" to "octocat")
        )
    )
}

@Tes
fun testDeepLinkHandler() {
    val uri = Uri.parse("app://users/user/octocat")
    val result = userDeepLinkHandler.handleDeepLink(uri)

    assertThat(result).isNotNull()
    assertThat(result?.route).isEqualTo("users/detail/octocat")
    assertThat(result?.deepLink).isEqualTo("app://users/user/octocat")
    assertThat(result?.arguments).containsEntry("username", "octocat")
}
class UserDeepLinkHandlerTest {

    @Tes
    fun `handleDeepLink with user list URI returns correct result`() {
        val uri = Uri.parse("app://users/list")
        val result = handler.handleDeepLink(uri)

        assertThat(result).isNotNull()
        assertThat(result?.route).isEqualTo("users/list")
        assertThat(result?.deepLink).isEqualTo("app://users/list")
    }

    @Tes
    fun `handleDeepLink with user detail URI returns correct result`() {
        val uri = Uri.parse("app://users/user/octocat")
        val result = handler.handleDeepLink(uri)

        assertThat(result).isNotNull()
        assertThat(result?.route).isEqualTo("users/detail/octocat")
        assertThat(result?.arguments).containsEntry("username", "octocat")
    }

    @Tes
    fun `handleDeepLink with unsupported URI returns null`() {
        val uri = Uri.parse("app://unsupported/path")
        val result = handler.handleDeepLink(uri)

        assertThat(result).isNull()
    }
}
class UserNavigationIntegrationTest {

    @Tes
    fun testCrossModuleNavigation() = runTest {
        // Navigate from users to search
        val searchCommand = NavCommand(
            route = "search",
            deepLink = "app://search?q=test"
        )

        navigationController.navigate(searchCommand)

        // Verify navigation occurred
        assertThat(navigationController.currentEntry.value?.deepLink)
            .isEqualTo("app://search?q=test")
    }
}
// ✅ CORRECT: Register with @IntoSe
@Binds
@IntoSe
abstract fun bindUserDeepLinkHandler(
    handler: UserDeepLinkHandler
): DeepLinkHandler
// ✅ CORRECT: Exact pattern matching
private fun isUserListUri(scheme: String, host: String?, path: String): Boolean =
    when {
        scheme == "app" && path in listOf("/users", "/users/list") -> true
        scheme == "githubusers" && path == "/users" -> true
        host == "githubusers.example.com" && path == "/users" -> true
        else -> false
    }
// ✅ CORRECT: Include all arguments
NavCommand(
    route = "users/detail/$username",
    deepLink = "app://users/user/$username",
    arguments = mapOf("username" to username)
)
# Build the projec
./gradlew :app:assembleDebug

# Run tests
./gradlew :feature-users:tes
./gradlew :feature-search:tes

# Run integration tests
./gradlew :app:connectedAndroidTes
```

---

## 🎯 Post-Migration

### Immediate Actions
1. **Test All Navigation Flows**: Verify all deep links work correctly
2. **Update Documentation**: Update any feature-specific documentation
3. **Code Review**: Have team review the migration
4. **Deploy**: Deploy to staging for testing

### Long-term Maintenance
1. **Monitor Performance**: Watch for any performance regressions
2. **Update Tests**: Keep tests up to date with new patterns
3. **Documentation**: Keep deep link patterns documented
4. **Training**: Train team on new navigation patterns

---

## 📚 Additional Resources

- [Navigation 3 Deep Link Architecture](NAVIGATION_3_DEEPLINK_ARCHITECTURE.md)
- [Feature-Based Development Guide](../../FEATURE_BASED_DEVELOPMENT_GUIDE.md)
- [Navigation 3 README](README.md)
- [Audit Report](NAVIGATION_AUDIT_REPORT.md)

---

## 🆘 Suppor

If you encounter issues during migration:

1. **Check the Audit Report**: Review the current architecture
2. **Review Existing Implementations**: Look at `feature-users` and `feature-search` for examples
3. **Run Tests**: Ensure all tests are passing
4. **Contact Team**: Reach out to the development team for assistance

---

*This migration guide is part of the Navigation 3 cleanup and migration project. For questions or clarifications, refer to the project documentation or contact the development team.*
