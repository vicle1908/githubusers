# Feature-Based Development Guide

## 🎯 Overview

This guide outlines the **feature-based development approach** used in the GitHub Users project, where each feature module owns its deep links, destinations, and navigation logic. This architecture ensures complete module isolation and enables scalable, maintainable development.

## 🏗️ Core Principles

### 1. **Feature Ownership**
Each feature module is responsible for:
- ✅ **Deep Link Patterns**: Define and own its deep link patterns
- ✅ **Navigation Destinations**: Define its navigation destinations
- ✅ **Deep Link Handlers**: Implement deep link resolution logic
- ✅ **Navigation Logic**: Handle navigation within the feature

### 2. **Module Isolation**
- ❌ **No Direct Dependencies**: Features don't directly reference other feature classes
- ✅ **Deep Link Communication**: All cross-module communication via deep links
- ✅ **Centralized Types**: Shared types in `navigation-api` module
- ✅ **Dependency Injection**: Hilt multibindings for handler registration

### 3. **Type Safety**
- ✅ **Compile-Time Safety**: `AppDestination` types prevent runtime errors
- ✅ **Serialization**: Kotlin serialization for type-safe argument passing
- ✅ **Validation**: Input validation in deep link handlers

## 📱 Feature Module Structure

### Consolidated Feature Module Layou

The project uses a consolidated feature approach where related features are grouped into single modules:

```tex
```tex
feature-users/
├── src/main/java/com/example/githubusers/feature/users/
│   ├── list/                                 # User list feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   └── domain/                           # Business logic
│   ├── detail/                               # User detail feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   └── domain/                           # Business logic
│   ├── search/                               # User search feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   ├── domain/                           # Business logic
│   │   └── navigation/                       # Deep link handling
│   └── data/                                 # Shared data layer
├── src/test/java/                            # Unit tests
└── build.gradle.kts                          # Module configuration
```

### Individual Feature Structure (if needed)

For future features that require complete isolation:

```tex
```tex
feature-users/
├── src/main/java/com/example/githubusers/feature/users/
│   ├── list/                                 # User list feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   └── domain/                           # Business logic
│   ├── detail/                               # User detail feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   └── domain/                           # Business logic
│   ├── search/                               # User search feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   ├── domain/                           # Business logic
│   │   └── navigation/                       # Deep link handling
│   └── data/                                 # Shared data layer
├── src/test/java/                            # Unit tests
└── build.gradle.kts                          # Module configuration
feature-{name}/
├── src/main/java/com/example/githubusers/feature/{name}/
│   ├── navigation/
│   │   ├── {Name}DeepLinkHandler.kt          # Deep link handling
│   │   ├── {Name}DeepLinks.kt                # Deep link builders
│   │   └── {Name}Destination.kt              # Feature-specific destinations
│   ├── presentation/
│   │   ├── ui/                               # Compose UI screens
│   │   └── viewmodel/                        # ViewModels
│   ├── domain/                               # Business logic
│   └── data/                                 # Data layer
├── src/test/java/                            # Unit tests
└── build.gradle.kts                          # Module configuration
```

## 🔗 Deep Link Implementation

### 1. **Create Deep Link Handler**

```kotlin
```kotlin
feature-users/
├── src/main/java/com/example/githubusers/feature/users/
│   ├── list/                                 # User list feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   └── domain/                           # Business logic
│   ├── detail/                               # User detail feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   └── domain/                           # Business logic
│   ├── search/                               # User search feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   ├── domain/                           # Business logic
│   │   └── navigation/                       # Deep link handling
│   └── data/                                 # Shared data layer
├── src/test/java/                            # Unit tests
└── build.gradle.kts                          # Module configuration
feature-{name}/
├── src/main/java/com/example/githubusers/feature/{name}/
│   ├── navigation/
│   │   ├── {Name}DeepLinkHandler.kt          # Deep link handling
│   │   ├── {Name}DeepLinks.kt                # Deep link builders
│   │   └── {Name}Destination.kt              # Feature-specific destinations
│   ├── presentation/
│   │   ├── ui/                               # Compose UI screens
│   │   └── viewmodel/                        # ViewModels
│   ├── domain/                               # Business logic
│   └── data/                                 # Data layer
├── src/test/java/                            # Unit tests
└── build.gradle.kts                          # Module configuration
@OwnsDeepLinks(moduleId = "users")
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> = listOf(
        "app://users/list",
        "app://users/detail/{username}",
        "app://users/search?q={query}",
        "githubusers://users",                    // Legacy compatibility
        "https://githubusers.example.com/users"   // Universal links
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        return when {
            isUserListUri(uri) -> DeepLinkResult(
                destination = AppDestination.UserLis
            )
            isUserDetailUri(uri) -> {
                val username = extractUsername(uri)
                DeepLinkResult(
                    destination = AppDestination.UserDetail(username),
                    arguments = mapOf("username" to username)
                )
            }
            else -> null
        }
    }

    // Helper methods for URI matching
    private fun isUserListUri(uri: Uri): Boolean = /* implementation */
    private fun isUserDetailUri(uri: Uri): Boolean = /* implementation */
    private fun extractUsername(uri: Uri): String? = /* implementation */
}
```

### 2. **Register with Hilt**

```kotlin
```kotlin
feature-users/
├── src/main/java/com/example/githubusers/feature/users/
│   ├── list/                                 # User list feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   └── domain/                           # Business logic
│   ├── detail/                               # User detail feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   └── domain/                           # Business logic
│   ├── search/                               # User search feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   ├── domain/                           # Business logic
│   │   └── navigation/                       # Deep link handling
│   └── data/                                 # Shared data layer
├── src/test/java/                            # Unit tests
└── build.gradle.kts                          # Module configuration
feature-{name}/
├── src/main/java/com/example/githubusers/feature/{name}/
│   ├── navigation/
│   │   ├── {Name}DeepLinkHandler.kt          # Deep link handling
│   │   ├── {Name}DeepLinks.kt                # Deep link builders
│   │   └── {Name}Destination.kt              # Feature-specific destinations
│   ├── presentation/
│   │   ├── ui/                               # Compose UI screens
│   │   └── viewmodel/                        # ViewModels
│   ├── domain/                               # Business logic
│   └── data/                                 # Data layer
├── src/test/java/                            # Unit tests
└── build.gradle.kts                          # Module configuration
@OwnsDeepLinks(moduleId = "users")
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> = listOf(
        "app://users/list",
        "app://users/detail/{username}",
        "app://users/search?q={query}",
        "githubusers://users",                    // Legacy compatibility
        "https://githubusers.example.com/users"   // Universal links
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        return when {
            isUserListUri(uri) -> DeepLinkResult(
                destination = AppDestination.UserLis
            )
            isUserDetailUri(uri) -> {
                val username = extractUsername(uri)
                DeepLinkResult(
                    destination = AppDestination.UserDetail(username),
                    arguments = mapOf("username" to username)
                )
            }
            else -> null
        }
    }

    // Helper methods for URI matching
    private fun isUserListUri(uri: Uri): Boolean = /* implementation */
    private fun isUserDetailUri(uri: Uri): Boolean = /* implementation */
    private fun extractUsername(uri: Uri): String? = /* implementation */
}
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

### 3. **Create Deep Link Builders**

```kotlin
```kotlin
feature-users/
├── src/main/java/com/example/githubusers/feature/users/
│   ├── list/                                 # User list feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   └── domain/                           # Business logic
│   ├── detail/                               # User detail feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   └── domain/                           # Business logic
│   ├── search/                               # User search feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   ├── domain/                           # Business logic
│   │   └── navigation/                       # Deep link handling
│   └── data/                                 # Shared data layer
├── src/test/java/                            # Unit tests
└── build.gradle.kts                          # Module configuration
feature-{name}/
├── src/main/java/com/example/githubusers/feature/{name}/
│   ├── navigation/
│   │   ├── {Name}DeepLinkHandler.kt          # Deep link handling
│   │   ├── {Name}DeepLinks.kt                # Deep link builders
│   │   └── {Name}Destination.kt              # Feature-specific destinations
│   ├── presentation/
│   │   ├── ui/                               # Compose UI screens
│   │   └── viewmodel/                        # ViewModels
│   ├── domain/                               # Business logic
│   └── data/                                 # Data layer
├── src/test/java/                            # Unit tests
└── build.gradle.kts                          # Module configuration
@OwnsDeepLinks(moduleId = "users")
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> = listOf(
        "app://users/list",
        "app://users/detail/{username}",
        "app://users/search?q={query}",
        "githubusers://users",                    // Legacy compatibility
        "https://githubusers.example.com/users"   // Universal links
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        return when {
            isUserListUri(uri) -> DeepLinkResult(
                destination = AppDestination.UserLis
            )
            isUserDetailUri(uri) -> {
                val username = extractUsername(uri)
                DeepLinkResult(
                    destination = AppDestination.UserDetail(username),
                    arguments = mapOf("username" to username)
                )
            }
            else -> null
        }
    }

    // Helper methods for URI matching
    private fun isUserListUri(uri: Uri): Boolean = /* implementation */
    private fun isUserDetailUri(uri: Uri): Boolean = /* implementation */
    private fun extractUsername(uri: Uri): String? = /* implementation */
}
@Module
@InstallIn(SingletonComponent::class)
abstract class UserNavigationModule {

    @Binds
    @IntoSe
    abstract fun bindUserDeepLinkHandler(
        handler: UserDeepLinkHandler
    ): DeepLinkHandler
}
object UserDeepLinks {
    fun userList(clearStack: Boolean = false): String =
        buildString {
            append("app://users/list")
            if (clearStack) append("?clear_stack=true")
        }

    fun userDetail(username: String, clearStack: Boolean = false): String =
        buildString {
            append("app://users/detail/")
            append(Uri.encode(username))
            if (clearStack) append("?clear_stack=true")
        }

    fun search(query: String, clearStack: Boolean = false): String =
        buildString {
            append("app://users/search")
            append("?q=${Uri.encode(query)}")
            if (clearStack) append("&clear_stack=true")
        }
}
```

## 🧭 Navigation Patterns

### 1. **Cross-Module Navigation**

```kotlin
```kotlin
feature-users/
├── src/main/java/com/example/githubusers/feature/users/
│   ├── list/                                 # User list feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   └── domain/                           # Business logic
│   ├── detail/                               # User detail feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   └── domain/                           # Business logic
│   ├── search/                               # User search feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   ├── domain/                           # Business logic
│   │   └── navigation/                       # Deep link handling
│   └── data/                                 # Shared data layer
├── src/test/java/                            # Unit tests
└── build.gradle.kts                          # Module configuration
feature-{name}/
├── src/main/java/com/example/githubusers/feature/{name}/
│   ├── navigation/
│   │   ├── {Name}DeepLinkHandler.kt          # Deep link handling
│   │   ├── {Name}DeepLinks.kt                # Deep link builders
│   │   └── {Name}Destination.kt              # Feature-specific destinations
│   ├── presentation/
│   │   ├── ui/                               # Compose UI screens
│   │   └── viewmodel/                        # ViewModels
│   ├── domain/                               # Business logic
│   └── data/                                 # Data layer
├── src/test/java/                            # Unit tests
└── build.gradle.kts                          # Module configuration
@OwnsDeepLinks(moduleId = "users")
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> = listOf(
        "app://users/list",
        "app://users/detail/{username}",
        "app://users/search?q={query}",
        "githubusers://users",                    // Legacy compatibility
        "https://githubusers.example.com/users"   // Universal links
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        return when {
            isUserListUri(uri) -> DeepLinkResult(
                destination = AppDestination.UserLis
            )
            isUserDetailUri(uri) -> {
                val username = extractUsername(uri)
                DeepLinkResult(
                    destination = AppDestination.UserDetail(username),
                    arguments = mapOf("username" to username)
                )
            }
            else -> null
        }
    }

    // Helper methods for URI matching
    private fun isUserListUri(uri: Uri): Boolean = /* implementation */
    private fun isUserDetailUri(uri: Uri): Boolean = /* implementation */
    private fun extractUsername(uri: Uri): String? = /* implementation */
}
@Module
@InstallIn(SingletonComponent::class)
abstract class UserNavigationModule {

    @Binds
    @IntoSe
    abstract fun bindUserDeepLinkHandler(
        handler: UserDeepLinkHandler
    ): DeepLinkHandler
}
object UserDeepLinks {
    fun userList(clearStack: Boolean = false): String =
        buildString {
            append("app://users/list")
            if (clearStack) append("?clear_stack=true")
        }

    fun userDetail(username: String, clearStack: Boolean = false): String =
        buildString {
            append("app://users/detail/")
            append(Uri.encode(username))
            if (clearStack) append("?clear_stack=true")
        }

    fun search(query: String, clearStack: Boolean = false): String =
        buildString {
            append("app://users/search")
            append("?q=${Uri.encode(query)}")
            if (clearStack) append("&clear_stack=true")
        }
}
// ✅ CORRECT: Use deep links for cross-module navigation
class UserListViewModel @Inject constructor(
    private val navigation: Navigation3Controller
) {
    fun onUserClicked(username: String) {
        viewModelScope.launch {
            navigation.navigate("app://users/detail/$username")
        }
    }

    fun onSearchClicked() {
        viewModelScope.launch {
            navigation.navigate("app://search")
        }
    }
}
```

### 2. **Typed Navigation**

```kotlin
```kotlin
feature-users/
├── src/main/java/com/example/githubusers/feature/users/
│   ├── list/                                 # User list feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   └── domain/                           # Business logic
│   ├── detail/                               # User detail feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   └── domain/                           # Business logic
│   ├── search/                               # User search feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   ├── domain/                           # Business logic
│   │   └── navigation/                       # Deep link handling
│   └── data/                                 # Shared data layer
├── src/test/java/                            # Unit tests
└── build.gradle.kts                          # Module configuration
feature-{name}/
├── src/main/java/com/example/githubusers/feature/{name}/
│   ├── navigation/
│   │   ├── {Name}DeepLinkHandler.kt          # Deep link handling
│   │   ├── {Name}DeepLinks.kt                # Deep link builders
│   │   └── {Name}Destination.kt              # Feature-specific destinations
│   ├── presentation/
│   │   ├── ui/                               # Compose UI screens
│   │   └── viewmodel/                        # ViewModels
│   ├── domain/                               # Business logic
│   └── data/                                 # Data layer
├── src/test/java/                            # Unit tests
└── build.gradle.kts                          # Module configuration
@OwnsDeepLinks(moduleId = "users")
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> = listOf(
        "app://users/list",
        "app://users/detail/{username}",
        "app://users/search?q={query}",
        "githubusers://users",                    // Legacy compatibility
        "https://githubusers.example.com/users"   // Universal links
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        return when {
            isUserListUri(uri) -> DeepLinkResult(
                destination = AppDestination.UserLis
            )
            isUserDetailUri(uri) -> {
                val username = extractUsername(uri)
                DeepLinkResult(
                    destination = AppDestination.UserDetail(username),
                    arguments = mapOf("username" to username)
                )
            }
            else -> null
        }
    }

    // Helper methods for URI matching
    private fun isUserListUri(uri: Uri): Boolean = /* implementation */
    private fun isUserDetailUri(uri: Uri): Boolean = /* implementation */
    private fun extractUsername(uri: Uri): String? = /* implementation */
}
@Module
@InstallIn(SingletonComponent::class)
abstract class UserNavigationModule {

    @Binds
    @IntoSe
    abstract fun bindUserDeepLinkHandler(
        handler: UserDeepLinkHandler
    ): DeepLinkHandler
}
object UserDeepLinks {
    fun userList(clearStack: Boolean = false): String =
        buildString {
            append("app://users/list")
            if (clearStack) append("?clear_stack=true")
        }

    fun userDetail(username: String, clearStack: Boolean = false): String =
        buildString {
            append("app://users/detail/")
            append(Uri.encode(username))
            if (clearStack) append("?clear_stack=true")
        }

    fun search(query: String, clearStack: Boolean = false): String =
        buildString {
            append("app://users/search")
            append("?q=${Uri.encode(query)}")
            if (clearStack) append("&clear_stack=true")
        }
}
// ✅ CORRECT: Use deep links for cross-module navigation
class UserListViewModel @Inject constructor(
    private val navigation: Navigation3Controller
) {
    fun onUserClicked(username: String) {
        viewModelScope.launch {
            navigation.navigate("app://users/detail/$username")
        }
    }

    fun onSearchClicked() {
        viewModelScope.launch {
            navigation.navigate("app://search")
        }
    }
}
// ✅ CORRECT: Use typed navigation for better type safety
class SearchViewModel @Inject constructor(
    private val moduleNavigator: ModuleNavigator
) {
    fun onUserClicked(username: String) {
        viewModelScope.launch {
            moduleNavigator.navigateTo(AppDestination.UserDetail(username))
        }
    }
}
```

### 3. **Navigation with Options**

```kotlin
```kotlin
feature-users/
├── src/main/java/com/example/githubusers/feature/users/
│   ├── list/                                 # User list feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   └── domain/                           # Business logic
│   ├── detail/                               # User detail feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   └── domain/                           # Business logic
│   ├── search/                               # User search feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   ├── domain/                           # Business logic
│   │   └── navigation/                       # Deep link handling
│   └── data/                                 # Shared data layer
├── src/test/java/                            # Unit tests
└── build.gradle.kts                          # Module configuration
feature-{name}/
├── src/main/java/com/example/githubusers/feature/{name}/
│   ├── navigation/
│   │   ├── {Name}DeepLinkHandler.kt          # Deep link handling
│   │   ├── {Name}DeepLinks.kt                # Deep link builders
│   │   └── {Name}Destination.kt              # Feature-specific destinations
│   ├── presentation/
│   │   ├── ui/                               # Compose UI screens
│   │   └── viewmodel/                        # ViewModels
│   ├── domain/                               # Business logic
│   └── data/                                 # Data layer
├── src/test/java/                            # Unit tests
└── build.gradle.kts                          # Module configuration
@OwnsDeepLinks(moduleId = "users")
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> = listOf(
        "app://users/list",
        "app://users/detail/{username}",
        "app://users/search?q={query}",
        "githubusers://users",                    // Legacy compatibility
        "https://githubusers.example.com/users"   // Universal links
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        return when {
            isUserListUri(uri) -> DeepLinkResult(
                destination = AppDestination.UserLis
            )
            isUserDetailUri(uri) -> {
                val username = extractUsername(uri)
                DeepLinkResult(
                    destination = AppDestination.UserDetail(username),
                    arguments = mapOf("username" to username)
                )
            }
            else -> null
        }
    }

    // Helper methods for URI matching
    private fun isUserListUri(uri: Uri): Boolean = /* implementation */
    private fun isUserDetailUri(uri: Uri): Boolean = /* implementation */
    private fun extractUsername(uri: Uri): String? = /* implementation */
}
@Module
@InstallIn(SingletonComponent::class)
abstract class UserNavigationModule {

    @Binds
    @IntoSe
    abstract fun bindUserDeepLinkHandler(
        handler: UserDeepLinkHandler
    ): DeepLinkHandler
}
object UserDeepLinks {
    fun userList(clearStack: Boolean = false): String =
        buildString {
            append("app://users/list")
            if (clearStack) append("?clear_stack=true")
        }

    fun userDetail(username: String, clearStack: Boolean = false): String =
        buildString {
            append("app://users/detail/")
            append(Uri.encode(username))
            if (clearStack) append("?clear_stack=true")
        }

    fun search(query: String, clearStack: Boolean = false): String =
        buildString {
            append("app://users/search")
            append("?q=${Uri.encode(query)}")
            if (clearStack) append("&clear_stack=true")
        }
}
// ✅ CORRECT: Use deep links for cross-module navigation
class UserListViewModel @Inject constructor(
    private val navigation: Navigation3Controller
) {
    fun onUserClicked(username: String) {
        viewModelScope.launch {
            navigation.navigate("app://users/detail/$username")
        }
    }

    fun onSearchClicked() {
        viewModelScope.launch {
            navigation.navigate("app://search")
        }
    }
}
// ✅ CORRECT: Use typed navigation for better type safety
class SearchViewModel @Inject constructor(
    private val moduleNavigator: ModuleNavigator
) {
    fun onUserClicked(username: String) {
        viewModelScope.launch {
            moduleNavigator.navigateTo(AppDestination.UserDetail(username))
        }
    }
}
// ✅ CORRECT: Use NavigationOptions for complex navigation
navigation.navigate(
    deepLink = "app://users/detail/$username",
    options = NavigationOptions(
        launchSingleTop = true,
        popUpTo = "app://users/list",
        popUpToInclusive = false
    )
)
```

## 🧪 Testing Strategy

### 1. **Unit Testing Deep Link Handlers**

```kotlin
```kotlin
feature-users/
├── src/main/java/com/example/githubusers/feature/users/
│   ├── list/                                 # User list feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   └── domain/                           # Business logic
│   ├── detail/                               # User detail feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   └── domain/                           # Business logic
│   ├── search/                               # User search feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   ├── domain/                           # Business logic
│   │   └── navigation/                       # Deep link handling
│   └── data/                                 # Shared data layer
├── src/test/java/                            # Unit tests
└── build.gradle.kts                          # Module configuration
feature-{name}/
├── src/main/java/com/example/githubusers/feature/{name}/
│   ├── navigation/
│   │   ├── {Name}DeepLinkHandler.kt          # Deep link handling
│   │   ├── {Name}DeepLinks.kt                # Deep link builders
│   │   └── {Name}Destination.kt              # Feature-specific destinations
│   ├── presentation/
│   │   ├── ui/                               # Compose UI screens
│   │   └── viewmodel/                        # ViewModels
│   ├── domain/                               # Business logic
│   └── data/                                 # Data layer
├── src/test/java/                            # Unit tests
└── build.gradle.kts                          # Module configuration
@OwnsDeepLinks(moduleId = "users")
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> = listOf(
        "app://users/list",
        "app://users/detail/{username}",
        "app://users/search?q={query}",
        "githubusers://users",                    // Legacy compatibility
        "https://githubusers.example.com/users"   // Universal links
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        return when {
            isUserListUri(uri) -> DeepLinkResult(
                destination = AppDestination.UserLis
            )
            isUserDetailUri(uri) -> {
                val username = extractUsername(uri)
                DeepLinkResult(
                    destination = AppDestination.UserDetail(username),
                    arguments = mapOf("username" to username)
                )
            }
            else -> null
        }
    }

    // Helper methods for URI matching
    private fun isUserListUri(uri: Uri): Boolean = /* implementation */
    private fun isUserDetailUri(uri: Uri): Boolean = /* implementation */
    private fun extractUsername(uri: Uri): String? = /* implementation */
}
@Module
@InstallIn(SingletonComponent::class)
abstract class UserNavigationModule {

    @Binds
    @IntoSe
    abstract fun bindUserDeepLinkHandler(
        handler: UserDeepLinkHandler
    ): DeepLinkHandler
}
object UserDeepLinks {
    fun userList(clearStack: Boolean = false): String =
        buildString {
            append("app://users/list")
            if (clearStack) append("?clear_stack=true")
        }

    fun userDetail(username: String, clearStack: Boolean = false): String =
        buildString {
            append("app://users/detail/")
            append(Uri.encode(username))
            if (clearStack) append("?clear_stack=true")
        }

    fun search(query: String, clearStack: Boolean = false): String =
        buildString {
            append("app://users/search")
            append("?q=${Uri.encode(query)}")
            if (clearStack) append("&clear_stack=true")
        }
}
// ✅ CORRECT: Use deep links for cross-module navigation
class UserListViewModel @Inject constructor(
    private val navigation: Navigation3Controller
) {
    fun onUserClicked(username: String) {
        viewModelScope.launch {
            navigation.navigate("app://users/detail/$username")
        }
    }

    fun onSearchClicked() {
        viewModelScope.launch {
            navigation.navigate("app://search")
        }
    }
}
// ✅ CORRECT: Use typed navigation for better type safety
class SearchViewModel @Inject constructor(
    private val moduleNavigator: ModuleNavigator
) {
    fun onUserClicked(username: String) {
        viewModelScope.launch {
            moduleNavigator.navigateTo(AppDestination.UserDetail(username))
        }
    }
}
// ✅ CORRECT: Use NavigationOptions for complex navigation
navigation.navigate(
    deepLink = "app://users/detail/$username",
    options = NavigationOptions(
        launchSingleTop = true,
        popUpTo = "app://users/list",
        popUpToInclusive = false
    )
)
class UserDeepLinkHandlerTest {

    private val handler = UserDeepLinkHandler()

    @Tes
    fun `should handle user list deep link`() {
        val uri = Uri.parse("app://users/list")
        val result = handler.handleDeepLink(uri)

        assertThat(result?.destination).isInstanceOf(AppDestination.UserList::class.java)
    }

    @Tes
    fun `should handle user detail deep link`() {
        val uri = Uri.parse("app://users/detail/octocat")
        val result = handler.handleDeepLink(uri)

        assertThat(result?.destination).isInstanceOf(AppDestination.UserDetail::class.java)
        assertThat((result?.destination as AppDestination.UserDetail).username)
            .isEqualTo("octocat")
    }

    @Tes
    fun `should reject invalid deep links`() {
        val uri = Uri.parse("app://invalid/path")
        val result = handler.handleDeepLink(uri)

        assertThat(result).isNull()
    }
}
```

### 2. **Integration Testing**

```kotlin
```kotlin
feature-users/
├── src/main/java/com/example/githubusers/feature/users/
│   ├── list/                                 # User list feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   └── domain/                           # Business logic
│   ├── detail/                               # User detail feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   └── domain/                           # Business logic
│   ├── search/                               # User search feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   ├── domain/                           # Business logic
│   │   └── navigation/                       # Deep link handling
│   └── data/                                 # Shared data layer
├── src/test/java/                            # Unit tests
└── build.gradle.kts                          # Module configuration
feature-{name}/
├── src/main/java/com/example/githubusers/feature/{name}/
│   ├── navigation/
│   │   ├── {Name}DeepLinkHandler.kt          # Deep link handling
│   │   ├── {Name}DeepLinks.kt                # Deep link builders
│   │   └── {Name}Destination.kt              # Feature-specific destinations
│   ├── presentation/
│   │   ├── ui/                               # Compose UI screens
│   │   └── viewmodel/                        # ViewModels
│   ├── domain/                               # Business logic
│   └── data/                                 # Data layer
├── src/test/java/                            # Unit tests
└── build.gradle.kts                          # Module configuration
@OwnsDeepLinks(moduleId = "users")
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> = listOf(
        "app://users/list",
        "app://users/detail/{username}",
        "app://users/search?q={query}",
        "githubusers://users",                    // Legacy compatibility
        "https://githubusers.example.com/users"   // Universal links
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        return when {
            isUserListUri(uri) -> DeepLinkResult(
                destination = AppDestination.UserLis
            )
            isUserDetailUri(uri) -> {
                val username = extractUsername(uri)
                DeepLinkResult(
                    destination = AppDestination.UserDetail(username),
                    arguments = mapOf("username" to username)
                )
            }
            else -> null
        }
    }

    // Helper methods for URI matching
    private fun isUserListUri(uri: Uri): Boolean = /* implementation */
    private fun isUserDetailUri(uri: Uri): Boolean = /* implementation */
    private fun extractUsername(uri: Uri): String? = /* implementation */
}
@Module
@InstallIn(SingletonComponent::class)
abstract class UserNavigationModule {

    @Binds
    @IntoSe
    abstract fun bindUserDeepLinkHandler(
        handler: UserDeepLinkHandler
    ): DeepLinkHandler
}
object UserDeepLinks {
    fun userList(clearStack: Boolean = false): String =
        buildString {
            append("app://users/list")
            if (clearStack) append("?clear_stack=true")
        }

    fun userDetail(username: String, clearStack: Boolean = false): String =
        buildString {
            append("app://users/detail/")
            append(Uri.encode(username))
            if (clearStack) append("?clear_stack=true")
        }

    fun search(query: String, clearStack: Boolean = false): String =
        buildString {
            append("app://users/search")
            append("?q=${Uri.encode(query)}")
            if (clearStack) append("&clear_stack=true")
        }
}
// ✅ CORRECT: Use deep links for cross-module navigation
class UserListViewModel @Inject constructor(
    private val navigation: Navigation3Controller
) {
    fun onUserClicked(username: String) {
        viewModelScope.launch {
            navigation.navigate("app://users/detail/$username")
        }
    }

    fun onSearchClicked() {
        viewModelScope.launch {
            navigation.navigate("app://search")
        }
    }
}
// ✅ CORRECT: Use typed navigation for better type safety
class SearchViewModel @Inject constructor(
    private val moduleNavigator: ModuleNavigator
) {
    fun onUserClicked(username: String) {
        viewModelScope.launch {
            moduleNavigator.navigateTo(AppDestination.UserDetail(username))
        }
    }
}
// ✅ CORRECT: Use NavigationOptions for complex navigation
navigation.navigate(
    deepLink = "app://users/detail/$username",
    options = NavigationOptions(
        launchSingleTop = true,
        popUpTo = "app://users/list",
        popUpToInclusive = false
    )
)
class UserDeepLinkHandlerTest {

    private val handler = UserDeepLinkHandler()

    @Tes
    fun `should handle user list deep link`() {
        val uri = Uri.parse("app://users/list")
        val result = handler.handleDeepLink(uri)

        assertThat(result?.destination).isInstanceOf(AppDestination.UserList::class.java)
    }

    @Tes
    fun `should handle user detail deep link`() {
        val uri = Uri.parse("app://users/detail/octocat")
        val result = handler.handleDeepLink(uri)

        assertThat(result?.destination).isInstanceOf(AppDestination.UserDetail::class.java)
        assertThat((result?.destination as AppDestination.UserDetail).username)
            .isEqualTo("octocat")
    }

    @Tes
    fun `should reject invalid deep links`() {
        val uri = Uri.parse("app://invalid/path")
        val result = handler.handleDeepLink(uri)

        assertThat(result).isNull()
    }
}
@Tes
fun `test cross-module navigation flow`() = runTest {
    val controller = Navigation3ControllerImpl(/* dependencies */)

    // Navigate to user lis
    controller.navigate("app://users/list")
    assertThat(controller.currentEntry.value?.destination)
        .isInstanceOf(AppDestination.UserList::class.java)

    // Navigate to user detail
    controller.navigate("app://users/detail/octocat")
    assertThat(controller.currentEntry.value?.destination)
        .isInstanceOf(AppDestination.UserDetail::class.java)

    // Navigate back
    controller.navigateBack()
    assertThat(controller.currentEntry.value?.destination)
        .isInstanceOf(AppDestination.UserList::class.java)
}
```

## 📋 Best Practices

### ✅ **DO**

1. **Feature Ownership**: Each feature owns its deep links and destinations
2. **Deep Link Patterns**: Define clear, consistent deep link patterns
3. **Input Validation**: Always validate parameters in deep link handlers
4. **Error Handling**: Handle invalid deep links gracefully
5. **Documentation**: Document all supported deep link patterns
6. **Testing**: Write comprehensive tests for deep link handling
7. **Type Safety**: Use `AppDestination` types for navigation
8. **Hilt Registration**: Register handlers via `@IntoSet` multibindings

### ❌ **DON'T**

1. **Direct References**: Never directly reference other feature classes
2. **Hardcoded Strings**: Don't hardcode deep link strings
3. **Bypass Validation**: Don't bypass deep link validation
4. **Expose Internals**: Don't expose internal navigation logic
5. **Skip Registration**: Don't forget to register handlers with Hil
6. **Ignore Errors**: Don't ignore deep link parsing errors
7. **Mixed Patterns**: Don't mix direct navigation with deep links

## 🔧 Development Workflow

### 1. **Adding a New Feature**

1. **Create Module**: Set up new feature module structure
2. **Define Destinations**: Add to `AppDestination` if needed
3. **Implement Handler**: Create `DeepLinkHandler` implementation
4. **Register Handler**: Add Hilt multibinding
5. **Create Builders**: Add deep link builder functions
6. **Write Tests**: Add comprehensive test coverage
7. **Update Documentation**: Document new deep link patterns

### 2. **Modifying Existing Features**

1. **Update Patterns**: Modify `supportedPatterns()` if needed
2. **Update Handler**: Modify `handleDeepLink()` logic
3. **Update Tests**: Update test cases
4. **Update Documentation**: Update pattern documentation
5. **Test Integration**: Verify cross-module navigation still works

### 3. **Adding New Deep Link Patterns**

1. **Add Pattern**: Add to `supportedPatterns()` lis
2. **Implement Logic**: Add handling logic in `handleDeepLink()`
3. **Add Builder**: Create builder function in `DeepLinks` objec
4. **Write Tests**: Add test cases for new pattern
5. **Update Docs**: Document new pattern

## 📊 Current Feature Status

| Feature | Deep Link Handler | Patterns | Tests | Status |
|---------|------------------|----------|-------|---------|
| **Users** | `UserDeepLinkHandler` | 6 patterns | ✅ Complete | ✅ Production |
| **Search** | `SearchDeepLinkHandler` | 6 patterns | ✅ Complete | ✅ Production |
| **Settings** | `SettingsModuleDeepLinkHandler` | 4 patterns | ✅ Complete | ✅ Production |

## 🚀 Advanced Features

### 1. **Dynamic Feature Modules**

```kotlin
```kotlin
feature-users/
├── src/main/java/com/example/githubusers/feature/users/
│   ├── list/                                 # User list feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   └── domain/                           # Business logic
│   ├── detail/                               # User detail feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   └── domain/                           # Business logic
│   ├── search/                               # User search feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   ├── domain/                           # Business logic
│   │   └── navigation/                       # Deep link handling
│   └── data/                                 # Shared data layer
├── src/test/java/                            # Unit tests
└── build.gradle.kts                          # Module configuration
feature-{name}/
├── src/main/java/com/example/githubusers/feature/{name}/
│   ├── navigation/
│   │   ├── {Name}DeepLinkHandler.kt          # Deep link handling
│   │   ├── {Name}DeepLinks.kt                # Deep link builders
│   │   └── {Name}Destination.kt              # Feature-specific destinations
│   ├── presentation/
│   │   ├── ui/                               # Compose UI screens
│   │   └── viewmodel/                        # ViewModels
│   ├── domain/                               # Business logic
│   └── data/                                 # Data layer
├── src/test/java/                            # Unit tests
└── build.gradle.kts                          # Module configuration
@OwnsDeepLinks(moduleId = "users")
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> = listOf(
        "app://users/list",
        "app://users/detail/{username}",
        "app://users/search?q={query}",
        "githubusers://users",                    // Legacy compatibility
        "https://githubusers.example.com/users"   // Universal links
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        return when {
            isUserListUri(uri) -> DeepLinkResult(
                destination = AppDestination.UserLis
            )
            isUserDetailUri(uri) -> {
                val username = extractUsername(uri)
                DeepLinkResult(
                    destination = AppDestination.UserDetail(username),
                    arguments = mapOf("username" to username)
                )
            }
            else -> null
        }
    }

    // Helper methods for URI matching
    private fun isUserListUri(uri: Uri): Boolean = /* implementation */
    private fun isUserDetailUri(uri: Uri): Boolean = /* implementation */
    private fun extractUsername(uri: Uri): String? = /* implementation */
}
@Module
@InstallIn(SingletonComponent::class)
abstract class UserNavigationModule {

    @Binds
    @IntoSe
    abstract fun bindUserDeepLinkHandler(
        handler: UserDeepLinkHandler
    ): DeepLinkHandler
}
object UserDeepLinks {
    fun userList(clearStack: Boolean = false): String =
        buildString {
            append("app://users/list")
            if (clearStack) append("?clear_stack=true")
        }

    fun userDetail(username: String, clearStack: Boolean = false): String =
        buildString {
            append("app://users/detail/")
            append(Uri.encode(username))
            if (clearStack) append("?clear_stack=true")
        }

    fun search(query: String, clearStack: Boolean = false): String =
        buildString {
            append("app://users/search")
            append("?q=${Uri.encode(query)}")
            if (clearStack) append("&clear_stack=true")
        }
}
// ✅ CORRECT: Use deep links for cross-module navigation
class UserListViewModel @Inject constructor(
    private val navigation: Navigation3Controller
) {
    fun onUserClicked(username: String) {
        viewModelScope.launch {
            navigation.navigate("app://users/detail/$username")
        }
    }

    fun onSearchClicked() {
        viewModelScope.launch {
            navigation.navigate("app://search")
        }
    }
}
// ✅ CORRECT: Use typed navigation for better type safety
class SearchViewModel @Inject constructor(
    private val moduleNavigator: ModuleNavigator
) {
    fun onUserClicked(username: String) {
        viewModelScope.launch {
            moduleNavigator.navigateTo(AppDestination.UserDetail(username))
        }
    }
}
// ✅ CORRECT: Use NavigationOptions for complex navigation
navigation.navigate(
    deepLink = "app://users/detail/$username",
    options = NavigationOptions(
        launchSingleTop = true,
        popUpTo = "app://users/list",
        popUpToInclusive = false
    )
)
class UserDeepLinkHandlerTest {

    private val handler = UserDeepLinkHandler()

    @Tes
    fun `should handle user list deep link`() {
        val uri = Uri.parse("app://users/list")
        val result = handler.handleDeepLink(uri)

        assertThat(result?.destination).isInstanceOf(AppDestination.UserList::class.java)
    }

    @Tes
    fun `should handle user detail deep link`() {
        val uri = Uri.parse("app://users/detail/octocat")
        val result = handler.handleDeepLink(uri)

        assertThat(result?.destination).isInstanceOf(AppDestination.UserDetail::class.java)
        assertThat((result?.destination as AppDestination.UserDetail).username)
            .isEqualTo("octocat")
    }

    @Tes
    fun `should reject invalid deep links`() {
        val uri = Uri.parse("app://invalid/path")
        val result = handler.handleDeepLink(uri)

        assertThat(result).isNull()
    }
}
@Tes
fun `test cross-module navigation flow`() = runTest {
    val controller = Navigation3ControllerImpl(/* dependencies */)

    // Navigate to user lis
    controller.navigate("app://users/list")
    assertThat(controller.currentEntry.value?.destination)
        .isInstanceOf(AppDestination.UserList::class.java)

    // Navigate to user detail
    controller.navigate("app://users/detail/octocat")
    assertThat(controller.currentEntry.value?.destination)
        .isInstanceOf(AppDestination.UserDetail::class.java)

    // Navigate back
    controller.navigateBack()
    assertThat(controller.currentEntry.value?.destination)
        .isInstanceOf(AppDestination.UserList::class.java)
}
class DynamicFeatureInterceptor : DeepLinkInterceptor {
    override suspend fun intercept(deepLink: String, options: NavigationOptions): Boolean {
        if (deepLink.startsWith("app://premium/")) {
            if (!isPremiumModuleInstalled()) {
                installPremiumModule()
                return true // Intercept and retry after installation
            }
        }
        return false
    }
}
```

### 2. **Analytics Integration**

```kotlin
```kotlin
feature-users/
├── src/main/java/com/example/githubusers/feature/users/
│   ├── list/                                 # User list feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   └── domain/                           # Business logic
│   ├── detail/                               # User detail feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   └── domain/                           # Business logic
│   ├── search/                               # User search feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   ├── domain/                           # Business logic
│   │   └── navigation/                       # Deep link handling
│   └── data/                                 # Shared data layer
├── src/test/java/                            # Unit tests
└── build.gradle.kts                          # Module configuration
feature-{name}/
├── src/main/java/com/example/githubusers/feature/{name}/
│   ├── navigation/
│   │   ├── {Name}DeepLinkHandler.kt          # Deep link handling
│   │   ├── {Name}DeepLinks.kt                # Deep link builders
│   │   └── {Name}Destination.kt              # Feature-specific destinations
│   ├── presentation/
│   │   ├── ui/                               # Compose UI screens
│   │   └── viewmodel/                        # ViewModels
│   ├── domain/                               # Business logic
│   └── data/                                 # Data layer
├── src/test/java/                            # Unit tests
└── build.gradle.kts                          # Module configuration
@OwnsDeepLinks(moduleId = "users")
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> = listOf(
        "app://users/list",
        "app://users/detail/{username}",
        "app://users/search?q={query}",
        "githubusers://users",                    // Legacy compatibility
        "https://githubusers.example.com/users"   // Universal links
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        return when {
            isUserListUri(uri) -> DeepLinkResult(
                destination = AppDestination.UserLis
            )
            isUserDetailUri(uri) -> {
                val username = extractUsername(uri)
                DeepLinkResult(
                    destination = AppDestination.UserDetail(username),
                    arguments = mapOf("username" to username)
                )
            }
            else -> null
        }
    }

    // Helper methods for URI matching
    private fun isUserListUri(uri: Uri): Boolean = /* implementation */
    private fun isUserDetailUri(uri: Uri): Boolean = /* implementation */
    private fun extractUsername(uri: Uri): String? = /* implementation */
}
@Module
@InstallIn(SingletonComponent::class)
abstract class UserNavigationModule {

    @Binds
    @IntoSe
    abstract fun bindUserDeepLinkHandler(
        handler: UserDeepLinkHandler
    ): DeepLinkHandler
}
object UserDeepLinks {
    fun userList(clearStack: Boolean = false): String =
        buildString {
            append("app://users/list")
            if (clearStack) append("?clear_stack=true")
        }

    fun userDetail(username: String, clearStack: Boolean = false): String =
        buildString {
            append("app://users/detail/")
            append(Uri.encode(username))
            if (clearStack) append("?clear_stack=true")
        }

    fun search(query: String, clearStack: Boolean = false): String =
        buildString {
            append("app://users/search")
            append("?q=${Uri.encode(query)}")
            if (clearStack) append("&clear_stack=true")
        }
}
// ✅ CORRECT: Use deep links for cross-module navigation
class UserListViewModel @Inject constructor(
    private val navigation: Navigation3Controller
) {
    fun onUserClicked(username: String) {
        viewModelScope.launch {
            navigation.navigate("app://users/detail/$username")
        }
    }

    fun onSearchClicked() {
        viewModelScope.launch {
            navigation.navigate("app://search")
        }
    }
}
// ✅ CORRECT: Use typed navigation for better type safety
class SearchViewModel @Inject constructor(
    private val moduleNavigator: ModuleNavigator
) {
    fun onUserClicked(username: String) {
        viewModelScope.launch {
            moduleNavigator.navigateTo(AppDestination.UserDetail(username))
        }
    }
}
// ✅ CORRECT: Use NavigationOptions for complex navigation
navigation.navigate(
    deepLink = "app://users/detail/$username",
    options = NavigationOptions(
        launchSingleTop = true,
        popUpTo = "app://users/list",
        popUpToInclusive = false
    )
)
class UserDeepLinkHandlerTest {

    private val handler = UserDeepLinkHandler()

    @Tes
    fun `should handle user list deep link`() {
        val uri = Uri.parse("app://users/list")
        val result = handler.handleDeepLink(uri)

        assertThat(result?.destination).isInstanceOf(AppDestination.UserList::class.java)
    }

    @Tes
    fun `should handle user detail deep link`() {
        val uri = Uri.parse("app://users/detail/octocat")
        val result = handler.handleDeepLink(uri)

        assertThat(result?.destination).isInstanceOf(AppDestination.UserDetail::class.java)
        assertThat((result?.destination as AppDestination.UserDetail).username)
            .isEqualTo("octocat")
    }

    @Tes
    fun `should reject invalid deep links`() {
        val uri = Uri.parse("app://invalid/path")
        val result = handler.handleDeepLink(uri)

        assertThat(result).isNull()
    }
}
@Tes
fun `test cross-module navigation flow`() = runTest {
    val controller = Navigation3ControllerImpl(/* dependencies */)

    // Navigate to user lis
    controller.navigate("app://users/list")
    assertThat(controller.currentEntry.value?.destination)
        .isInstanceOf(AppDestination.UserList::class.java)

    // Navigate to user detail
    controller.navigate("app://users/detail/octocat")
    assertThat(controller.currentEntry.value?.destination)
        .isInstanceOf(AppDestination.UserDetail::class.java)

    // Navigate back
    controller.navigateBack()
    assertThat(controller.currentEntry.value?.destination)
        .isInstanceOf(AppDestination.UserList::class.java)
}
class DynamicFeatureInterceptor : DeepLinkInterceptor {
    override suspend fun intercept(deepLink: String, options: NavigationOptions): Boolean {
        if (deepLink.startsWith("app://premium/")) {
            if (!isPremiumModuleInstalled()) {
                installPremiumModule()
                return true // Intercept and retry after installation
            }
        }
        return false
    }
}
class AnalyticsInterceptor : DeepLinkInterceptor {
    override suspend fun intercept(deepLink: String, options: NavigationOptions): Boolean {
        analytics.trackNavigation(deepLink, options)
        return false // Don't intercept, just track
    }
}
```

### 3. **Permission-Based Navigation**

```kotlin
```kotlin
feature-users/
├── src/main/java/com/example/githubusers/feature/users/
│   ├── list/                                 # User list feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   └── domain/                           # Business logic
│   ├── detail/                               # User detail feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   └── domain/                           # Business logic
│   ├── search/                               # User search feature
│   │   ├── presentation/                     # UI and ViewModels
│   │   ├── domain/                           # Business logic
│   │   └── navigation/                       # Deep link handling
│   └── data/                                 # Shared data layer
├── src/test/java/                            # Unit tests
└── build.gradle.kts                          # Module configuration
feature-{name}/
├── src/main/java/com/example/githubusers/feature/{name}/
│   ├── navigation/
│   │   ├── {Name}DeepLinkHandler.kt          # Deep link handling
│   │   ├── {Name}DeepLinks.kt                # Deep link builders
│   │   └── {Name}Destination.kt              # Feature-specific destinations
│   ├── presentation/
│   │   ├── ui/                               # Compose UI screens
│   │   └── viewmodel/                        # ViewModels
│   ├── domain/                               # Business logic
│   └── data/                                 # Data layer
├── src/test/java/                            # Unit tests
└── build.gradle.kts                          # Module configuration
@OwnsDeepLinks(moduleId = "users")
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {

    override val moduleId: String = "users"

    override fun supportedPatterns(): List<String> = listOf(
        "app://users/list",
        "app://users/detail/{username}",
        "app://users/search?q={query}",
        "githubusers://users",                    // Legacy compatibility
        "https://githubusers.example.com/users"   // Universal links
    )

    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        return when {
            isUserListUri(uri) -> DeepLinkResult(
                destination = AppDestination.UserLis
            )
            isUserDetailUri(uri) -> {
                val username = extractUsername(uri)
                DeepLinkResult(
                    destination = AppDestination.UserDetail(username),
                    arguments = mapOf("username" to username)
                )
            }
            else -> null
        }
    }

    // Helper methods for URI matching
    private fun isUserListUri(uri: Uri): Boolean = /* implementation */
    private fun isUserDetailUri(uri: Uri): Boolean = /* implementation */
    private fun extractUsername(uri: Uri): String? = /* implementation */
}
@Module
@InstallIn(SingletonComponent::class)
abstract class UserNavigationModule {

    @Binds
    @IntoSe
    abstract fun bindUserDeepLinkHandler(
        handler: UserDeepLinkHandler
    ): DeepLinkHandler
}
object UserDeepLinks {
    fun userList(clearStack: Boolean = false): String =
        buildString {
            append("app://users/list")
            if (clearStack) append("?clear_stack=true")
        }

    fun userDetail(username: String, clearStack: Boolean = false): String =
        buildString {
            append("app://users/detail/")
            append(Uri.encode(username))
            if (clearStack) append("?clear_stack=true")
        }

    fun search(query: String, clearStack: Boolean = false): String =
        buildString {
            append("app://users/search")
            append("?q=${Uri.encode(query)}")
            if (clearStack) append("&clear_stack=true")
        }
}
// ✅ CORRECT: Use deep links for cross-module navigation
class UserListViewModel @Inject constructor(
    private val navigation: Navigation3Controller
) {
    fun onUserClicked(username: String) {
        viewModelScope.launch {
            navigation.navigate("app://users/detail/$username")
        }
    }

    fun onSearchClicked() {
        viewModelScope.launch {
            navigation.navigate("app://search")
        }
    }
}
// ✅ CORRECT: Use typed navigation for better type safety
class SearchViewModel @Inject constructor(
    private val moduleNavigator: ModuleNavigator
) {
    fun onUserClicked(username: String) {
        viewModelScope.launch {
            moduleNavigator.navigateTo(AppDestination.UserDetail(username))
        }
    }
}
// ✅ CORRECT: Use NavigationOptions for complex navigation
navigation.navigate(
    deepLink = "app://users/detail/$username",
    options = NavigationOptions(
        launchSingleTop = true,
        popUpTo = "app://users/list",
        popUpToInclusive = false
    )
)
class UserDeepLinkHandlerTest {

    private val handler = UserDeepLinkHandler()

    @Tes
    fun `should handle user list deep link`() {
        val uri = Uri.parse("app://users/list")
        val result = handler.handleDeepLink(uri)

        assertThat(result?.destination).isInstanceOf(AppDestination.UserList::class.java)
    }

    @Tes
    fun `should handle user detail deep link`() {
        val uri = Uri.parse("app://users/detail/octocat")
        val result = handler.handleDeepLink(uri)

        assertThat(result?.destination).isInstanceOf(AppDestination.UserDetail::class.java)
        assertThat((result?.destination as AppDestination.UserDetail).username)
            .isEqualTo("octocat")
    }

    @Tes
    fun `should reject invalid deep links`() {
        val uri = Uri.parse("app://invalid/path")
        val result = handler.handleDeepLink(uri)

        assertThat(result).isNull()
    }
}
@Tes
fun `test cross-module navigation flow`() = runTest {
    val controller = Navigation3ControllerImpl(/* dependencies */)

    // Navigate to user lis
    controller.navigate("app://users/list")
    assertThat(controller.currentEntry.value?.destination)
        .isInstanceOf(AppDestination.UserList::class.java)

    // Navigate to user detail
    controller.navigate("app://users/detail/octocat")
    assertThat(controller.currentEntry.value?.destination)
        .isInstanceOf(AppDestination.UserDetail::class.java)

    // Navigate back
    controller.navigateBack()
    assertThat(controller.currentEntry.value?.destination)
        .isInstanceOf(AppDestination.UserList::class.java)
}
class DynamicFeatureInterceptor : DeepLinkInterceptor {
    override suspend fun intercept(deepLink: String, options: NavigationOptions): Boolean {
        if (deepLink.startsWith("app://premium/")) {
            if (!isPremiumModuleInstalled()) {
                installPremiumModule()
                return true // Intercept and retry after installation
            }
        }
        return false
    }
}
class AnalyticsInterceptor : DeepLinkInterceptor {
    override suspend fun intercept(deepLink: String, options: NavigationOptions): Boolean {
        analytics.trackNavigation(deepLink, options)
        return false // Don't intercept, just track
    }
}
class AuthInterceptor : DeepLinkInterceptor {
    override suspend fun intercept(deepLink: String, options: NavigationOptions): Boolean {
        if (deepLink.contains("premium") && !user.isPremium) {
            navigation.navigate("app://upgrade")
            return true // Intercept navigation
        }
        return false
    }
}
```

## 📚 Related Documentation

- [Navigation 3 Deep Link Architecture](navigation3/NAVIGATION_3_DEEPLINK_ARCHITECTURE.md)
- [Multi-Module Deep Link Architecture](navigation3/MULTI_MODULE_DEEPLINK_ARCHITECTURE.md)
- [Navigation 3 Implementation Guide](navigation3/NAVIGATION_3_IMPLEMENTATION.md)
- [KSP Ownership Guide](navigation3/runbooks/KSP_OWNERSHIP.md)

---

**Remember**: The feature-based architecture ensures complete module isolation while maintaining type safety and testability. Always use deep links for cross-module navigation and follow the established patterns for consistency.
