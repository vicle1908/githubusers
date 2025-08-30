# Navigation 3 Deep Link Architecture

## 🎯 Overview

This document describes the Navigation 3 implementation with **mandatory deep link based navigation** for all cross-module communication in the GitHub Users app.

**Key Principle**: All cross-module navigation MUST use deep links. Direct navigation between modules is strictly prohibited.

---

## 🏗️ Architecture Components

### 1. Navigation API (`navigation-api` module)

The API module provides the contracts that all modules depend on:

- **`Navigation3Controller`**: Main navigation controller interface
- **`NavigationDestination`**: Base interface for all destinations
- **`DeepLinkHandler`**: Interface for module-specific deep link handlers
- **`DeepLinkSpec`**: Alternative deep link specification interface
- **`NavigationOptions`**: Navigation behavior configuration

### 2. Navigation Implementation (`navigation-impl` module)

The implementation module provides:

- **`Navigation3ControllerImpl`**: Core navigation logic with deep link resolution
- **`Navigation3Host`**: Compose UI host for rendering screens
- **`DefaultDestinationResolver`**: Resolves URIs to NavigationDestination using DeepLinkHandler set

### 3. Feature Modules

Each feature module:
- Defines its own `NavigationDestination` implementations
- Provides a `DeepLinkHandler` for its deep links
- Registers handlers via Hilt multibindings
- Uses only deep links for external navigation

---

## 📱 Deep Link Patterns

### Standard App Scheme Patterns

All modules use the `githubusers://` scheme for internal navigation (and https for universal links):

#### User Feature Module
```
app://users/list                          # User list screen
app://users/detail/{username}             # User detail screen
app://users/search?q={query}              # User search
```

#### Search Feature Module
```
app://search                              # Search screen
app://search?q={query}                    # Search with query
app://search/trending                     # Trending searches
app://search/history                      # Search history
```

#### Core Navigation
```
app://home                                # Home screen
app://settings                            # Settings screen
app://error?message={message}             # Error screen
```

### Web Link Patterns (Universal Links)

For external deep linking:

```
https://githubusers.example.com/user/{username}
https://githubusers.example.com/search?q={query}
https://githubusers.example.com/settings
```


---

## 🔄 Navigation Flow

### 1. Navigation Request

```kotlin
// From any module, navigate using deep link
navigation.navigate("app://users/detail/octocat")
```

### 2. Deep Link Resolution

```
Request → Navigation3Controller → DeepLinkResolver → DeepLinkHandler → NavigationDestination
```

### 3. Screen Rendering

```kotlin
Navigation3Host { entry ->
    when (entry.destination) {
        is UserDestination.UserDetail -> UserDetailScreen(...)
        is SearchDestination.Search -> SearchScreen(...)
        // ... other destinations
    }
}
```

---

## 💻 Implementation Examples

### Defining a Feature's Deep Links

```kotlin
// In feature-users module
class UserDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    
    override val moduleId = "users"
    
    override fun supportedPatterns() = listOf(
        "app://users/list",
        "app://users/detail/{username}",
        "app://users/search"
    )
    
    override fun handleDeepLink(uri: Uri): DeepLinkResult? {
        return when {
            uri.path == "/users/list" -> DeepLinkResult(
                destination = UserDestination.UserList
            )
            uri.path?.startsWith("/users/detail/") == true -> {
                val username = extractUsername(uri)
                DeepLinkResult(
                    destination = UserDestination.UserDetail(username)
                )
            }
            else -> null
        }
    }
}
```

### Registering Deep Link Handler

```kotlin
// In feature's Hilt module
@Module
@InstallIn(SingletonComponent::class)
abstract class UserNavigationModule {
    
    @Binds
    @IntoSet
    abstract fun bindUserDeepLinkHandler(
        handler: UserDeepLinkHandler
    ): DeepLinkHandler
}
```

### Navigating Between Modules

```kotlin
// From Search module to User Detail
class SearchViewModel @Inject constructor(
    private val navigation: Navigation3Controller
) {
    fun onUserClicked(username: String) {
        viewModelScope.launch {
            // MUST use deep link for cross-module navigation
            navigation.navigate("app://users/detail/$username")
        }
    }
}
```

### Building Deep Links

```kotlin
// Helper object for building deep links
object UserDeepLinks {
    fun userDetail(username: String, clearStack: Boolean = false): String {
        return buildString {
            append("app://users/detail/")
            append(Uri.encode(username))
            if (clearStack) append("?clear_stack=true")
        }
    }
}
```

---

## 🎯 Navigation Options

Control navigation behavior with `NavigationOptions`:

```kotlin
navigation.navigate(
    deepLink = "app://users/detail/octocat",
    options = NavigationOptions(
        launchSingleTop = true,        // Avoid duplicate destinations
        popUpTo = "app://home",        // Clear back stack up to home
        popUpToInclusive = false,      // Keep home in stack
        animate = true,                 // Enable animations
        transition = NavigationTransition.SlideHorizontal
    )
)
```

---

## 🔒 Security Considerations

### 1. Input Validation

Always validate deep link parameters:

```kotlin
override fun handleDeepLink(uri: Uri): DeepLinkResult? {
    val username = uri.getQueryParameter("username")
    
    // Validate username
    if (!isValidUsername(username)) {
        return null // Reject invalid input
    }
    
    return DeepLinkResult(...)
}
```

### 2. Permission Checks

Implement interceptors for permission-based navigation:

```kotlin
class AuthInterceptor : DeepLinkInterceptor {
    override suspend fun intercept(deepLink: String, options: NavigationOptions): Boolean {
        if (deepLink.contains("premium") && !user.isPremium) {
            // Redirect to upgrade screen
            navigation.navigate("app://upgrade")
            return true // Intercept navigation
        }
        return false
    }
}
```

---

## 🧪 Testing Deep Links

### Unit Testing

```kotlin
@Test
fun `test user detail deep link`() {
    val handler = UserDeepLinkHandler()
    val uri = Uri.parse("app://users/detail/octocat")
    
    val result = handler.handleDeepLink(uri)
    
    assertThat(result?.destination).isInstanceOf(UserDestination.UserDetail::class.java)
    assertThat((result?.destination as UserDestination.UserDetail).username).isEqualTo("octocat")
}
```

### Integration Testing

```kotlin
@Test
fun `test navigation flow`() = runTest {
    val controller = Navigation3ControllerImpl(registry, resolver)
    
    controller.navigate("app://users/list")
    controller.navigate("app://users/detail/octocat")
    
    val backStack = controller.currentBackStack.value
    assertThat(backStack).hasSize(2)
    assertThat(backStack.last().deepLink).isEqualTo("app://users/detail/octocat")
}
```

---

## 📋 Best Practices

### ✅ DO

1. **Always use deep links for cross-module navigation**
2. **Define deep link patterns in the feature module**
3. **Use helper objects for building deep links**
4. **Validate all input parameters**
5. **Handle navigation failures gracefully**
6. **Use NavigationOptions for complex navigation**
7. **Document all deep link patterns**

### ❌ DON'T

1. **Never directly reference other modules' classes**
2. **Don't hardcode deep link strings**
3. **Don't bypass deep link navigation**
4. **Don't expose internal navigation logic**
5. **Don't forget to register handlers with Hilt**

---

## 🔄 Migration Guide

### From Navigation Compose to Navigation 3

1. **Replace NavHost with Navigation3Host**:

```kotlin
// Before
NavHost(navController, startDestination = "home") {
    composable("home") { HomeScreen() }
}

// After
Navigation3Host(controller, startDestination = "app://home") { entry ->
    when (entry.destination) {
        is HomeDestination -> HomeScreen()
    }
}
```

2. **Update navigation calls**:

```kotlin
// Before
navController.navigate("detail/$id")

// After
controller.navigate("app://detail/$id")
```

3. **Register deep link handlers**:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureNavigationModule {
    @Binds
    @IntoSet
    abstract fun bindDeepLinkHandler(handler: FeatureDeepLinkHandler): DeepLinkHandler
}
```

---

## 🚀 Advanced Features

### Dynamic Feature Modules

Support for dynamic feature modules with lazy loading:

```kotlin
class DynamicFeatureInterceptor : DeepLinkInterceptor {
    override suspend fun intercept(deepLink: String, options: NavigationOptions): Boolean {
        if (deepLink.startsWith("app://premium/")) {
            // Load dynamic module if needed
            if (!isPremiumModuleInstalled()) {
                installPremiumModule()
                return true // Intercept and retry after installation
            }
        }
        return false
    }
}
```

### Analytics Integration

Track navigation events:

```kotlin
class AnalyticsInterceptor : DeepLinkInterceptor {
    override suspend fun intercept(deepLink: String, options: NavigationOptions): Boolean {
        analytics.trackNavigation(deepLink, options)
        return false // Don't intercept, just track
    }
}
```

---

## 📊 Architecture Decision Records (ADR)

### Why Navigation 3 with Deep Links?

1. **Complete module isolation**: Modules don't know about each other
2. **Universal navigation**: Same pattern for internal and external links
3. **Testability**: Easy to test navigation in isolation
4. **Flexibility**: Easy to add new modules without changing existing code
5. **Security**: Centralized validation and permission checking
6. **Analytics**: Single point for tracking all navigation

### Trade-offs

**Pros:**
- Complete decoupling between modules
- Consistent navigation pattern
- Easy to test and debug
- Support for universal links
- Better security through validation

**Cons:**
- Slightly more verbose than direct navigation
- Need to maintain deep link patterns
- Runtime resolution overhead (minimal)

---

## 🔗 Related Documentation

- [Module Architecture Guide](../MODULAR_ARCHITECTURE_GUIDE.md)
- [Feature-Based Composite Build Guide](../FEATURE_BASED_COMPOSITE_BUILD_GUIDE.md)
- [Mobile MCP Testing Workflow](../MOBILE_MCP_TESTING_WORKFLOW.md)

---

## 📝 Appendix: Complete Deep Link Registry

| Feature | Deep Link Pattern | Description |
|---------|------------------|-------------|
| **Users** | | |
| | `app://users/list` | User list screen |
| | `app://users/detail/{username}` | User detail screen |
| | `app://users/search?q={query}` | User search |
| **Search** | | |
| | `app://search` | Search screen |
| | `app://search?q={query}` | Search with query |
| | `app://search/trending` | Trending searches |
| | `app://search/history` | Search history |
| **Core** | | |
| | `app://home` | Home screen |
| | `app://settings` | Settings screen |
| | `app://error?message={msg}` | Error screen |

---

*Last Updated: 2025-08-30*
*Version: 1.0.0*
