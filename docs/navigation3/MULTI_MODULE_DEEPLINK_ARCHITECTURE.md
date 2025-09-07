# Multi-Module Deep Link Navigation Architecture

## Overview

This document describes the multi-module deep link navigation architecture implemented for the GitHub Users app. The architecture allows independent modules to communicate via deep links, enabling loose coupling and better modularity.

## Architecture Components

### 1. Deep Link Resolution (navigation-impl)
- DefaultDestinationResolver resolves deep links to NavigationDestination
- Collects DeepLinkHandler contributions via Hilt multibindings from feature modules
- Supports both app scheme and web universal links

### 2. Module Navigator (`ModuleNavigator`)
- Main entry point for cross-module navigation
- Uses AppDeepLinks.parse(uri) to prefer typed AppDestination when possible
- Delegates to Navigation3Controller to navigate typed destinations or raw deep links

### 3. Deep Link Handlers (navigation-api)
Each feature module implements `com.example.githubusers.navigation.api.DeepLinkHandler` and contributes it with Hilt `@IntoSet` so the resolver can discover it.

### 4. Navigation Host (`Navigation3Host`)
- Typed host that renders current Navigation3Entry
- Start destination defined as a typed AppDestination

## Deep Link Patterns

### User Module
```text
githubusers://users                     - User lis
githubusers://user/{username}           - User detail
githubusers://search?q={query}          - Search (integrated in UserList)
https://githubusers.example.com/users   - Web URL for user lis
https://githubusers.example.com/user/{username} - Web URL for user detail
```

### Settings Module
```text
githubusers://settings                  - Settings home
githubusers://settings/profile          - Profile settings
githubusers://settings/theme            - Theme settings
githubusers://settings/about            - About page
https://githubusers.example.com/settings - Web URL for settings
```

### Special Parameters
- `clear_stack=true` - Clear navigation back stack before navigating

## Usage Examples

### Navigate Between Modules

```kotlin
```text
githubusers://settings                  - Settings home
githubusers://settings/profile          - Profile settings
githubusers://settings/theme            - Theme settings
githubusers://settings/about            - About page
https://githubusers.example.com/settings - Web URL for settings
// Typed navigation
moduleNavigator.navigateTo(AppDestination.UserDetail("octocat"))

// Deep link navigation (raw URI)
moduleNavigator.navigateToModule("githubusers://settings?clear_stack=true")
```

### Register a New Module

```kotlin
```kotlin
githubusers://settings                  - Settings home
githubusers://settings/profile          - Profile settings
githubusers://settings/theme            - Theme settings
githubusers://settings/about            - About page
https://githubusers.example.com/settings - Web URL for settings
// Typed navigation
moduleNavigator.navigateTo(AppDestination.UserDetail("octocat"))

// Deep link navigation (raw URI)
moduleNavigator.navigateToModule("githubusers://settings?clear_stack=true")
// 1. Create a handler in the feature module (navigation-api)
class MyModuleDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    override val moduleId = "mymodule"
    override fun supportedPatterns() = listOf(
        "githubusers://mymodule",
        "githubusers://mymodule/{id}"
    )
    override fun handleDeepLink(uri: Uri): DeepLinkResult? { /* ... */ }
}

// 2. Contribute via Hil
@Module
@InstallIn(SingletonComponent::class)
object MyModuleNavigationDi {
    @Provides @IntoSet @Singleton
    fun provideMyModuleHandler(): DeepLinkHandler = MyModuleDeepLinkHandler()
}
```

### Handle Deep Links from External Sources

Deep links are automatically handled when:
1. User clicks a link in browser/email
2. App is launched via `adb shell am start`
3. Another app sends an intent with the deep link

Example:
```bash
```kotlin
githubusers://settings                  - Settings home
githubusers://settings/profile          - Profile settings
githubusers://settings/theme            - Theme settings
githubusers://settings/about            - About page
https://githubusers.example.com/settings - Web URL for settings
// Typed navigation
moduleNavigator.navigateTo(AppDestination.UserDetail("octocat"))

// Deep link navigation (raw URI)
moduleNavigator.navigateToModule("githubusers://settings?clear_stack=true")
// 1. Create a handler in the feature module (navigation-api)
class MyModuleDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    override val moduleId = "mymodule"
    override fun supportedPatterns() = listOf(
        "githubusers://mymodule",
        "githubusers://mymodule/{id}"
    )
    override fun handleDeepLink(uri: Uri): DeepLinkResult? { /* ... */ }
}

// 2. Contribute via Hil
@Module
@InstallIn(SingletonComponent::class)
object MyModuleNavigationDi {
    @Provides @IntoSet @Singleton
    fun provideMyModuleHandler(): DeepLinkHandler = MyModuleDeepLinkHandler()
}
# Test deep link via adb
adb shell am start -W -a android.intent.action.VIEW
  -d "githubusers://user/google"
  com.example.githubusers
```

## Benefits of This Architecture

### 1. Module Independence
- Modules don't need direct references to each other
- Navigation is done through URIs, not direct class references
- Easy to move modules to separate Gradle modules

### 2. Testability
- Deep link handlers can be unit tested independently
- Mock navigation for testing
- Test deep link routing without UI

### 3. Flexibility
- Support multiple URI schemes (app://, https://)
- Easy to add new modules and routes
- Can integrate with web navigation

### 4. Dynamic Features
- Supports dynamic feature modules
- Can check if a module is installed before navigating
- Fallback to web URLs if module not available

## Testing Deep Links

### Unit Testing
```kotlin
```kotlin
githubusers://settings                  - Settings home
githubusers://settings/profile          - Profile settings
githubusers://settings/theme            - Theme settings
githubusers://settings/about            - About page
https://githubusers.example.com/settings - Web URL for settings
// Typed navigation
moduleNavigator.navigateTo(AppDestination.UserDetail("octocat"))

// Deep link navigation (raw URI)
moduleNavigator.navigateToModule("githubusers://settings?clear_stack=true")
// 1. Create a handler in the feature module (navigation-api)
class MyModuleDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    override val moduleId = "mymodule"
    override fun supportedPatterns() = listOf(
        "githubusers://mymodule",
        "githubusers://mymodule/{id}"
    )
    override fun handleDeepLink(uri: Uri): DeepLinkResult? { /* ... */ }
}

// 2. Contribute via Hil
@Module
@InstallIn(SingletonComponent::class)
object MyModuleNavigationDi {
    @Provides @IntoSet @Singleton
    fun provideMyModuleHandler(): DeepLinkHandler = MyModuleDeepLinkHandler()
}
# Test deep link via adb
adb shell am start -W -a android.intent.action.VIEW
  -d "githubusers://user/google"
  com.example.githubusers
@Tes
fun testUserDetailDeepLink() {
    val handler = UserModuleDeepLinkHandler()
    val uri = Uri.parse("githubusers://user/octocat")

    val result = handler.handleDeepLink(uri)

    assertNotNull(result)
    assertTrue(result.destination is Nav3Destination.UserDetail)
    assertEquals("octocat", (result.destination as Nav3Destination.UserDetail).username)
}
```

### Integration Testing
```kotlin
```kotlin
githubusers://settings                  - Settings home
githubusers://settings/profile          - Profile settings
githubusers://settings/theme            - Theme settings
githubusers://settings/about            - About page
https://githubusers.example.com/settings - Web URL for settings
// Typed navigation
moduleNavigator.navigateTo(AppDestination.UserDetail("octocat"))

// Deep link navigation (raw URI)
moduleNavigator.navigateToModule("githubusers://settings?clear_stack=true")
// 1. Create a handler in the feature module (navigation-api)
class MyModuleDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    override val moduleId = "mymodule"
    override fun supportedPatterns() = listOf(
        "githubusers://mymodule",
        "githubusers://mymodule/{id}"
    )
    override fun handleDeepLink(uri: Uri): DeepLinkResult? { /* ... */ }
}

// 2. Contribute via Hil
@Module
@InstallIn(SingletonComponent::class)
object MyModuleNavigationDi {
    @Provides @IntoSet @Singleton
    fun provideMyModuleHandler(): DeepLinkHandler = MyModuleDeepLinkHandler()
}
# Test deep link via adb
adb shell am start -W -a android.intent.action.VIEW
  -d "githubusers://user/google"
  com.example.githubusers
@Tes
fun testUserDetailDeepLink() {
    val handler = UserModuleDeepLinkHandler()
    val uri = Uri.parse("githubusers://user/octocat")

    val result = handler.handleDeepLink(uri)

    assertNotNull(result)
    assertTrue(result.destination is Nav3Destination.UserDetail)
    assertEquals("octocat", (result.destination as Nav3Destination.UserDetail).username)
}
@Tes
fun testDeepLinkNavigation() = runTest {
    val deepLink = "githubusers://user/google"
    moduleNavigator.navigateToModule(deepLink)
    // Verify Navigation3Controller currentEntry reflects target deep link
    assertThat(controller.currentEntry.value?.deepLink).isEqualTo(deepLink)
}
```

## Future Enhancements

### 1. Dynamic Module Suppor
```kotlin
```kotlin
githubusers://settings                  - Settings home
githubusers://settings/profile          - Profile settings
githubusers://settings/theme            - Theme settings
githubusers://settings/about            - About page
https://githubusers.example.com/settings - Web URL for settings
// Typed navigation
moduleNavigator.navigateTo(AppDestination.UserDetail("octocat"))

// Deep link navigation (raw URI)
moduleNavigator.navigateToModule("githubusers://settings?clear_stack=true")
// 1. Create a handler in the feature module (navigation-api)
class MyModuleDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    override val moduleId = "mymodule"
    override fun supportedPatterns() = listOf(
        "githubusers://mymodule",
        "githubusers://mymodule/{id}"
    )
    override fun handleDeepLink(uri: Uri): DeepLinkResult? { /* ... */ }
}

// 2. Contribute via Hil
@Module
@InstallIn(SingletonComponent::class)
object MyModuleNavigationDi {
    @Provides @IntoSet @Singleton
    fun provideMyModuleHandler(): DeepLinkHandler = MyModuleDeepLinkHandler()
}
# Test deep link via adb
adb shell am start -W -a android.intent.action.VIEW
  -d "githubusers://user/google"
  com.example.githubusers
@Tes
fun testUserDetailDeepLink() {
    val handler = UserModuleDeepLinkHandler()
    val uri = Uri.parse("githubusers://user/octocat")

    val result = handler.handleDeepLink(uri)

    assertNotNull(result)
    assertTrue(result.destination is Nav3Destination.UserDetail)
    assertEquals("octocat", (result.destination as Nav3Destination.UserDetail).username)
}
@Tes
fun testDeepLinkNavigation() = runTest {
    val deepLink = "githubusers://user/google"
    moduleNavigator.navigateToModule(deepLink)
    // Verify Navigation3Controller currentEntry reflects target deep link
    assertThat(controller.currentEntry.value?.deepLink).isEqualTo(deepLink)
}
// Check if module is installed
if (moduleManager.isModuleInstalled("premium")) {
    moduleNavigator.navigateToModule("githubusers://premium/features")
} else {
    // Prompt to install or fallback to web
}
```

### 2. Analytics Integration
```kotlin
```kotlin
githubusers://settings                  - Settings home
githubusers://settings/profile          - Profile settings
githubusers://settings/theme            - Theme settings
githubusers://settings/about            - About page
https://githubusers.example.com/settings - Web URL for settings
// Typed navigation
moduleNavigator.navigateTo(AppDestination.UserDetail("octocat"))

// Deep link navigation (raw URI)
moduleNavigator.navigateToModule("githubusers://settings?clear_stack=true")
// 1. Create a handler in the feature module (navigation-api)
class MyModuleDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    override val moduleId = "mymodule"
    override fun supportedPatterns() = listOf(
        "githubusers://mymodule",
        "githubusers://mymodule/{id}"
    )
    override fun handleDeepLink(uri: Uri): DeepLinkResult? { /* ... */ }
}

// 2. Contribute via Hil
@Module
@InstallIn(SingletonComponent::class)
object MyModuleNavigationDi {
    @Provides @IntoSet @Singleton
    fun provideMyModuleHandler(): DeepLinkHandler = MyModuleDeepLinkHandler()
}
# Test deep link via adb
adb shell am start -W -a android.intent.action.VIEW
  -d "githubusers://user/google"
  com.example.githubusers
@Tes
fun testUserDetailDeepLink() {
    val handler = UserModuleDeepLinkHandler()
    val uri = Uri.parse("githubusers://user/octocat")

    val result = handler.handleDeepLink(uri)

    assertNotNull(result)
    assertTrue(result.destination is Nav3Destination.UserDetail)
    assertEquals("octocat", (result.destination as Nav3Destination.UserDetail).username)
}
@Tes
fun testDeepLinkNavigation() = runTest {
    val deepLink = "githubusers://user/google"
    moduleNavigator.navigateToModule(deepLink)
    // Verify Navigation3Controller currentEntry reflects target deep link
    assertThat(controller.currentEntry.value?.deepLink).isEqualTo(deepLink)
}
// Check if module is installed
if (moduleManager.isModuleInstalled("premium")) {
    moduleNavigator.navigateToModule("githubusers://premium/features")
} else {
    // Prompt to install or fallback to web
}
// Track deep link usage
deepLinkRegistry.addInterceptor { deepLink ->
    analytics.trackDeepLink(deepLink)
}
```

### 3. Permission Checking
```kotlin
```kotlin
githubusers://settings                  - Settings home
githubusers://settings/profile          - Profile settings
githubusers://settings/theme            - Theme settings
githubusers://settings/about            - About page
https://githubusers.example.com/settings - Web URL for settings
// Typed navigation
moduleNavigator.navigateTo(AppDestination.UserDetail("octocat"))

// Deep link navigation (raw URI)
moduleNavigator.navigateToModule("githubusers://settings?clear_stack=true")
// 1. Create a handler in the feature module (navigation-api)
class MyModuleDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    override val moduleId = "mymodule"
    override fun supportedPatterns() = listOf(
        "githubusers://mymodule",
        "githubusers://mymodule/{id}"
    )
    override fun handleDeepLink(uri: Uri): DeepLinkResult? { /* ... */ }
}

// 2. Contribute via Hil
@Module
@InstallIn(SingletonComponent::class)
object MyModuleNavigationDi {
    @Provides @IntoSet @Singleton
    fun provideMyModuleHandler(): DeepLinkHandler = MyModuleDeepLinkHandler()
}
# Test deep link via adb
adb shell am start -W -a android.intent.action.VIEW
  -d "githubusers://user/google"
  com.example.githubusers
@Tes
fun testUserDetailDeepLink() {
    val handler = UserModuleDeepLinkHandler()
    val uri = Uri.parse("githubusers://user/octocat")

    val result = handler.handleDeepLink(uri)

    assertNotNull(result)
    assertTrue(result.destination is Nav3Destination.UserDetail)
    assertEquals("octocat", (result.destination as Nav3Destination.UserDetail).username)
}
@Tes
fun testDeepLinkNavigation() = runTest {
    val deepLink = "githubusers://user/google"
    moduleNavigator.navigateToModule(deepLink)
    // Verify Navigation3Controller currentEntry reflects target deep link
    assertThat(controller.currentEntry.value?.deepLink).isEqualTo(deepLink)
}
// Check if module is installed
if (moduleManager.isModuleInstalled("premium")) {
    moduleNavigator.navigateToModule("githubusers://premium/features")
} else {
    // Prompt to install or fallback to web
}
// Track deep link usage
deepLinkRegistry.addInterceptor { deepLink ->
    analytics.trackDeepLink(deepLink)
}
// Check if user has access before navigation
deepLinkRegistry.addPermissionChecker { deepLink ->
    userPermissions.canAccess(deepLink)
}
```

### 4. Deep Link Validation
```kotlin
```kotlin
githubusers://settings                  - Settings home
githubusers://settings/profile          - Profile settings
githubusers://settings/theme            - Theme settings
githubusers://settings/about            - About page
https://githubusers.example.com/settings - Web URL for settings
// Typed navigation
moduleNavigator.navigateTo(AppDestination.UserDetail("octocat"))

// Deep link navigation (raw URI)
moduleNavigator.navigateToModule("githubusers://settings?clear_stack=true")
// 1. Create a handler in the feature module (navigation-api)
class MyModuleDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    override val moduleId = "mymodule"
    override fun supportedPatterns() = listOf(
        "githubusers://mymodule",
        "githubusers://mymodule/{id}"
    )
    override fun handleDeepLink(uri: Uri): DeepLinkResult? { /* ... */ }
}

// 2. Contribute via Hil
@Module
@InstallIn(SingletonComponent::class)
object MyModuleNavigationDi {
    @Provides @IntoSet @Singleton
    fun provideMyModuleHandler(): DeepLinkHandler = MyModuleDeepLinkHandler()
}
# Test deep link via adb
adb shell am start -W -a android.intent.action.VIEW
  -d "githubusers://user/google"
  com.example.githubusers
@Tes
fun testUserDetailDeepLink() {
    val handler = UserModuleDeepLinkHandler()
    val uri = Uri.parse("githubusers://user/octocat")

    val result = handler.handleDeepLink(uri)

    assertNotNull(result)
    assertTrue(result.destination is Nav3Destination.UserDetail)
    assertEquals("octocat", (result.destination as Nav3Destination.UserDetail).username)
}
@Tes
fun testDeepLinkNavigation() = runTest {
    val deepLink = "githubusers://user/google"
    moduleNavigator.navigateToModule(deepLink)
    // Verify Navigation3Controller currentEntry reflects target deep link
    assertThat(controller.currentEntry.value?.deepLink).isEqualTo(deepLink)
}
// Check if module is installed
if (moduleManager.isModuleInstalled("premium")) {
    moduleNavigator.navigateToModule("githubusers://premium/features")
} else {
    // Prompt to install or fallback to web
}
// Track deep link usage
deepLinkRegistry.addInterceptor { deepLink ->
    analytics.trackDeepLink(deepLink)
}
// Check if user has access before navigation
deepLinkRegistry.addPermissionChecker { deepLink ->
    userPermissions.canAccess(deepLink)
}
// Validate deep links at compile time
@DeepLink("githubusers://user/{username}")
fun navigateToUser(username: String) {
    // Type-safe deep link generation
}
```

## Migration Guide

To migrate existing navigation to deep links:

1. **Identify Navigation Points**: Find all places where modules navigate to each other
2. **Define Deep Links**: Create URI patterns for each navigation destination
3. **Implement Handlers**: Create `DeepLinkHandler` for each module
4. **Update Navigation Code**: Replace direct navigation with `moduleNavigator.navigateToModule()`
5. **Test**: Verify all navigation paths work correctly

## Best Practices

1. **URI Design**
   - Use consistent naming: `scheme://module/action/parameter`
   - Support both app and web schemes
   - Use query parameters for optional data

2. **Error Handling**
   - Always provide fallbacks for unhandled deep links
   - Log failed deep link attempts
   - Show user-friendly error messages

3. **Security**
   - Validate all parameters from deep links
   - Don't expose sensitive operations via deep links
   - Use HTTPS for web deep links

4. **Documentation**
   - Document all supported deep links
   - Provide examples for each pattern
   - Keep deep link registry up to date

