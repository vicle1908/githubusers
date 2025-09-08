# Navigation Architecture Guide

## 🎯 Overview

This guide documents the complete Navigation 3 implementation in the GitHub Users project, featuring a multi-module deep link architecture that enables type-safe, modular navigation across feature boundaries.

## 🏗️ Architecture Principles

### 1. **Feature-Based Navigation Ownership**
- Each feature module owns its deep links and navigation destinations
- No direct dependencies between feature modules
- All cross-module communication via deep links

### 2. **Type-Safe Navigation**
- Compile-time navigation safety with `AppDestination` types
- Kotlin Serialization for type-safe argument passing
- Deep link validation and error handling

### 3. **Multi-Module Deep Link Architecture**
- Centralized deep link resolution in `navigation-impl`
- Feature modules contribute deep link handlers via Hilt multibindings
- Support for both app scheme and web universal links

## 📱 Core Components

### Navigation API (`navigation-api`)

**AppDestination.kt** - Type-safe destination definitions:
```kotlin
@Serializable
sealed interface AppDestination {
    @Serializable
    data class UserList(val query: String = "") : AppDestination
    
    @Serializable
    data class UserDetail(val username: String) : AppDestination
    
    @Serializable
    data class Settings(val section: String = "general") : AppDestination
}
```

**AppDeepLinks.kt** - Deep link utilities:
```kotlin
object AppDeepLinks {
    fun build(destination: AppDestination): String
    fun parse(uri: String): AppDestination?
    fun isValid(uri: String): Boolean
}
```

**DeepLinkHandler.kt** - Interface for feature deep link handling:
```kotlin
interface DeepLinkHandler {
    fun canHandle(uri: String): Boolean
    fun handle(uri: String): NavigationDestination?
}
```

### Navigation Implementation (`navigation-impl`)

**DefaultDestinationResolver.kt** - Central deep link resolution:
```kotlin
@Singleton
class DefaultDestinationResolver @Inject constructor(
    private val deepLinkHandlers: Set<@JvmSuppressWildcards DeepLinkHandler>
) {
    fun resolve(uri: String): NavigationDestination?
}
```

**Navigation3Actions.kt** - Navigation actions and utilities:
```kotlin
@Singleton
class Navigation3Actions @Inject constructor(
    private val controller: Navigation3Controller
) {
    fun navigateToUserDetail(username: String)
    fun navigateToUserList(query: String = "")
    fun navigateToSettings(section: String = "general")
}
```

**Navigation3Host** - Typed navigation host:
```kotlin
@Composable
fun Navigation3Host(
    controller: Navigation3Controller,
    startDestination: AppDestination = AppDestination.UserList()
)
```

## 🔗 Deep Link Patterns

### User Module Deep Links

| Pattern | Description | Example |
|---------|-------------|---------|
| `githubusers://users` | User list screen | `githubusers://users` |
| `githubusers://users?q={query}` | User list with search | `githubusers://users?q=octocat` |
| `githubusers://user/{username}` | User detail screen | `githubusers://user/octocat` |
| `https://githubusers.example.com/users` | Web URL for user list | `https://githubusers.example.com/users` |
| `https://githubusers.example.com/user/{username}` | Web URL for user detail | `https://githubusers.example.com/user/octocat` |

### Settings Module Deep Links

| Pattern | Description | Example |
|---------|-------------|---------|
| `githubusers://settings` | Settings screen | `githubusers://settings` |
| `githubusers://settings/{section}` | Settings with section | `githubusers://settings/theme` |

## 🚀 Implementation Guide

### 1. **Creating a New Feature Module**

#### Step 1: Define Destinations
```kotlin
// In your feature module
@Serializable
data class YourFeatureDestination(val param: String) : AppDestination
```

#### Step 2: Implement Deep Link Handler
```kotlin
@Singleton
class YourFeatureDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    override fun canHandle(uri: String): Boolean {
        return uri.startsWith("githubusers://your-feature")
    }
    
    override fun handle(uri: String): NavigationDestination? {
        // Parse URI and return NavigationDestination
        return YourFeatureDestination(...)
    }
}
```

#### Step 3: Register with Hilt
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class YourFeatureNavigationModule {
    @Binds
    @IntoSet
    abstract fun bindDeepLinkHandler(
        handler: YourFeatureDeepLinkHandler
    ): DeepLinkHandler
}
```

### 2. **Navigation from UI Components**

#### Type-Safe Navigation
```kotlin
@Composable
fun UserListScreen(
    navigationActions: Navigation3Actions
) {
    Button(
        onClick = { 
            navigationActions.navigateToUserDetail("octocat") 
        }
    ) {
        Text("View User")
    }
}
```

#### Direct Deep Link Navigation
```kotlin
val controller = LocalNavigation3Controller.current
controller.navigate(AppDestination.UserDetail("octocat"))
```

### 3. **Testing Navigation**

#### Unit Testing Deep Link Handlers
```kotlin
@Test
fun `should handle user detail deep link`() {
    val handler = UserDeepLinkHandler()
    val uri = "githubusers://user/octocat"
    
    assertTrue(handler.canHandle(uri))
    val destination = handler.handle(uri)
    assertNotNull(destination)
}
```

#### Integration Testing
```kotlin
@Test
fun `should navigate to user detail from deep link`() {
    // Test complete navigation flow
    val deepLink = "githubusers://user/octocat"
    val result = navigationActions.navigate(deepLink)
    // Verify navigation state
}
```

## 🔧 Configuration

### Dependencies

**Version Catalog** (`catalog/gradle/libs.versions.toml`):
```toml
[navigation3]
navigation3-ui = "1.0.0-alpha15"
navigation3-runtime = "1.0.0-alpha15"
lifecycle-viewmodel-navigation3 = "2.8.7"

[serialization]
kotlinx-serialization-core = "1.7.3"
kotlinx-serialization-json = "1.7.3"
```

**Module Dependencies**:
```kotlin
dependencies {
    implementation(libs.navigation3.ui)
    implementation(libs.navigation3.runtime)
    implementation(libs.lifecycle.viewmodel.navigation3)
    implementation(libs.serialization.kotlinx.serialization.core)
    implementation(libs.serialization.kotlinx.serialization.json)
}
```

### Hilt Configuration

**Navigation Module** (`app/di/NavigationModule.kt`):
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationModule {
    @Binds
    abstract fun bindDestinationResolver(
        impl: DefaultDestinationResolver
    ): DestinationResolver
}
```

## 📊 Performance Considerations

### 1. **Deep Link Resolution**
- Deep link handlers are registered once at startup
- Resolution is O(n) where n is the number of handlers
- Consider handler ordering for performance-critical paths

### 2. **Type Safety Overhead**
- Kotlin Serialization adds minimal overhead
- Compile-time safety prevents runtime navigation errors
- Deep link validation prevents invalid navigation attempts

### 3. **Memory Management**
- Navigation state is managed by Navigation 3
- Deep link handlers are singletons with minimal memory footprint
- Proper cleanup in navigation lifecycle

## 🧪 Testing Strategy

### 1. **Unit Tests**
- Test individual deep link handlers
- Test destination parsing and validation
- Test navigation action creation

### 2. **Integration Tests**
- Test complete navigation flows
- Test deep link resolution across modules
- Test navigation state management

### 3. **UI Tests**
- Test navigation from user interactions
- Test deep link handling from external sources
- Test navigation state restoration

## 🚨 Common Issues and Solutions

### 1. **Deep Link Not Resolved**
**Problem**: Deep link returns null destination
**Solution**: 
- Check handler registration in Hilt module
- Verify `canHandle()` method logic
- Ensure proper URI format

### 2. **Type Safety Errors**
**Problem**: Compilation errors with destination types
**Solution**:
- Add `@Serializable` annotation to destination classes
- Check serialization dependencies
- Verify destination parameter types

### 3. **Navigation State Issues**
**Problem**: Navigation state not preserved
**Solution**:
- Use Navigation 3's built-in state management
- Check navigation options configuration
- Verify deep link parameter handling

## 🔮 Future Enhancements

### 1. **Advanced Deep Link Features**
- Dynamic deep link generation
- Deep link analytics and tracking
- A/B testing for navigation flows

### 2. **Performance Optimizations**
- Deep link handler caching
- Lazy loading of navigation destinations
- Navigation state compression

### 3. **Developer Experience**
- Deep link validation tools
- Navigation flow visualization
- Automated navigation testing

## 📚 Additional Resources

- [Navigation 3 Documentation](https://developer.android.com/guide/navigation/navigation3)
- [Kotlin Serialization Guide](https://kotlinlang.org/docs/serialization.html)
- [Hilt Dependency Injection](https://dagger.dev/hilt/)
- [Deep Links Best Practices](https://developer.android.com/training/app-links)

---

This architecture provides a robust, scalable foundation for navigation in the GitHub Users app, enabling type-safe, modular navigation while maintaining excellent performance and developer experience.
