# Navigation Performance Monitoring

This document describes the performance monitoring system for the Navigation 3 implementation, which provides comprehensive tracking and analysis of navigation performance metrics.

## Overview

The performance monitoring system tracks:
- Navigation operation timing
- Deep link resolution performance
- Feature API call performance
- Memory usage patterns (optional)
- CPU usage patterns (optional)

## Components

### Core Components

#### `NavigationPerformanceMonitor`
The main performance monitoring class that tracks and stores performance metrics.

```kotlin
```kotlin
@Inject
lateinit var performanceMonitor: NavigationPerformanceMonitor

// Track navigation performance
performanceMonitor.startNavigation("users/list")
// ... perform navigation
performanceMonitor.endNavigation("users/list")

// Get performance statistics
val stats = performanceMonitor.getPerformanceStats()
```

#### `NavigationPerformanceConfig`
Configuration class for performance monitoring settings.

```kotlin
```kotlin
@Inject
lateinit var performanceMonitor: NavigationPerformanceMonitor

// Track navigation performance
performanceMonitor.startNavigation("users/list")
// ... perform navigation
performanceMonitor.endNavigation("users/list")

// Get performance statistics
val stats = performanceMonitor.getPerformanceStats()
@Inject
lateinit var performanceConfig: NavigationPerformanceConfig

if (performanceConfig.isPerformanceMonitoringEnabled) {
    // Track performance
}
```

### Interceptors

#### `NavigationPerformanceInterceptor`
Automatically tracks performance for all navigation operations.

```kotlin
```kotlin
@Inject
lateinit var performanceMonitor: NavigationPerformanceMonitor

// Track navigation performance
performanceMonitor.startNavigation("users/list")
// ... perform navigation
performanceMonitor.endNavigation("users/list")

// Get performance statistics
val stats = performanceMonitor.getPerformanceStats()
@Inject
lateinit var performanceConfig: NavigationPerformanceConfig

if (performanceConfig.isPerformanceMonitoringEnabled) {
    // Track performance
}
@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {
    @Provides
    @Singleton
    fun provideNavigation3Controller(
        impl: Navigation3ControllerImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): Navigation3Controller {
        return NavigationPerformanceInterceptor(impl, performanceMonitor)
    }
}
```

#### `DeepLinkPerformanceInterceptor`
Automatically tracks performance for deep link resolution.

```kotlin
```kotlin
@Inject
lateinit var performanceMonitor: NavigationPerformanceMonitor

// Track navigation performance
performanceMonitor.startNavigation("users/list")
// ... perform navigation
performanceMonitor.endNavigation("users/list")

// Get performance statistics
val stats = performanceMonitor.getPerformanceStats()
@Inject
lateinit var performanceConfig: NavigationPerformanceConfig

if (performanceConfig.isPerformanceMonitoringEnabled) {
    // Track performance
}
@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {
    @Provides
    @Singleton
    fun provideNavigation3Controller(
        impl: Navigation3ControllerImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): Navigation3Controller {
        return NavigationPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object DeepLinkHandlerModule {
    @Provides
    @IntoSet
    fun provideUserDeepLinkHandler(
        impl: UserDeepLinkHandler,
        performanceMonitor: NavigationPerformanceMonitor
    ): DeepLinkHandler {
        return DeepLinkPerformanceInterceptor(impl, performanceMonitor)
    }
}
```

#### `FeatureApiPerformanceInterceptor`
Automatically tracks performance for feature API calls.

```kotlin
```kotlin
@Inject
lateinit var performanceMonitor: NavigationPerformanceMonitor

// Track navigation performance
performanceMonitor.startNavigation("users/list")
// ... perform navigation
performanceMonitor.endNavigation("users/list")

// Get performance statistics
val stats = performanceMonitor.getPerformanceStats()
@Inject
lateinit var performanceConfig: NavigationPerformanceConfig

if (performanceConfig.isPerformanceMonitoringEnabled) {
    // Track performance
}
@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {
    @Provides
    @Singleton
    fun provideNavigation3Controller(
        impl: Navigation3ControllerImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): Navigation3Controller {
        return NavigationPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object DeepLinkHandlerModule {
    @Provides
    @IntoSet
    fun provideUserDeepLinkHandler(
        impl: UserDeepLinkHandler,
        performanceMonitor: NavigationPerformanceMonitor
    ): DeepLinkHandler {
        return DeepLinkPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object FeatureApiModule {
    @Provides
    fun provideUserFeatureApi(
        impl: UserFeatureApiImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): UserFeatureApi {
        return FeatureApiPerformanceInterceptor(impl, performanceMonitor) as UserFeatureApi
    }
}
```

### Dashboard

#### `NavigationPerformanceDashboard`
Composable dashboard for real-time performance monitoring.

```kotlin
```kotlin
@Inject
lateinit var performanceMonitor: NavigationPerformanceMonitor

// Track navigation performance
performanceMonitor.startNavigation("users/list")
// ... perform navigation
performanceMonitor.endNavigation("users/list")

// Get performance statistics
val stats = performanceMonitor.getPerformanceStats()
@Inject
lateinit var performanceConfig: NavigationPerformanceConfig

if (performanceConfig.isPerformanceMonitoringEnabled) {
    // Track performance
}
@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {
    @Provides
    @Singleton
    fun provideNavigation3Controller(
        impl: Navigation3ControllerImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): Navigation3Controller {
        return NavigationPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object DeepLinkHandlerModule {
    @Provides
    @IntoSet
    fun provideUserDeepLinkHandler(
        impl: UserDeepLinkHandler,
        performanceMonitor: NavigationPerformanceMonitor
    ): DeepLinkHandler {
        return DeepLinkPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object FeatureApiModule {
    @Provides
    fun provideUserFeatureApi(
        impl: UserFeatureApiImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): UserFeatureApi {
        return FeatureApiPerformanceInterceptor(impl, performanceMonitor) as UserFeatureApi
    }
}
@Composable
fun DebugScreen() {
    NavigationPerformanceDashboard()
}
```

## Configuration

### Performance Thresholds

```kotlin
```kotlin
@Inject
lateinit var performanceMonitor: NavigationPerformanceMonitor

// Track navigation performance
performanceMonitor.startNavigation("users/list")
// ... perform navigation
performanceMonitor.endNavigation("users/list")

// Get performance statistics
val stats = performanceMonitor.getPerformanceStats()
@Inject
lateinit var performanceConfig: NavigationPerformanceConfig

if (performanceConfig.isPerformanceMonitoringEnabled) {
    // Track performance
}
@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {
    @Provides
    @Singleton
    fun provideNavigation3Controller(
        impl: Navigation3ControllerImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): Navigation3Controller {
        return NavigationPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object DeepLinkHandlerModule {
    @Provides
    @IntoSet
    fun provideUserDeepLinkHandler(
        impl: UserDeepLinkHandler,
        performanceMonitor: NavigationPerformanceMonitor
    ): DeepLinkHandler {
        return DeepLinkPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object FeatureApiModule {
    @Provides
    fun provideUserFeatureApi(
        impl: UserFeatureApiImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): UserFeatureApi {
        return FeatureApiPerformanceInterceptor(impl, performanceMonitor) as UserFeatureApi
    }
}
@Composable
fun DebugScreen() {
    NavigationPerformanceDashboard()
}
val config = NavigationPerformanceConfig()

// Navigation thresholds
config.slowNavigationThreshold = 1000L // 1 second
config.slowDeepLinkResolutionThreshold = 500L // 500ms
config.slowFeatureApiCallThreshold = 200L // 200ms

// Memory limits
config.maxPerformanceSamples = 1000

// Feature toggles
config.isPerformanceMonitoringEnabled = true
config.isLoggingEnabled = true
config.isMemoryTrackingEnabled = false
config.isCpuTrackingEnabled = false
```

### Environment-Based Configuration

Performance monitoring is automatically enabled in debug builds and disabled in release builds:

```kotlin
```kotlin
@Inject
lateinit var performanceMonitor: NavigationPerformanceMonitor

// Track navigation performance
performanceMonitor.startNavigation("users/list")
// ... perform navigation
performanceMonitor.endNavigation("users/list")

// Get performance statistics
val stats = performanceMonitor.getPerformanceStats()
@Inject
lateinit var performanceConfig: NavigationPerformanceConfig

if (performanceConfig.isPerformanceMonitoringEnabled) {
    // Track performance
}
@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {
    @Provides
    @Singleton
    fun provideNavigation3Controller(
        impl: Navigation3ControllerImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): Navigation3Controller {
        return NavigationPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object DeepLinkHandlerModule {
    @Provides
    @IntoSet
    fun provideUserDeepLinkHandler(
        impl: UserDeepLinkHandler,
        performanceMonitor: NavigationPerformanceMonitor
    ): DeepLinkHandler {
        return DeepLinkPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object FeatureApiModule {
    @Provides
    fun provideUserFeatureApi(
        impl: UserFeatureApiImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): UserFeatureApi {
        return FeatureApiPerformanceInterceptor(impl, performanceMonitor) as UserFeatureApi
    }
}
@Composable
fun DebugScreen() {
    NavigationPerformanceDashboard()
}
val config = NavigationPerformanceConfig()

// Navigation thresholds
config.slowNavigationThreshold = 1000L // 1 second
config.slowDeepLinkResolutionThreshold = 500L // 500ms
config.slowFeatureApiCallThreshold = 200L // 200ms

// Memory limits
config.maxPerformanceSamples = 1000

// Feature toggles
config.isPerformanceMonitoringEnabled = true
config.isLoggingEnabled = true
config.isMemoryTrackingEnabled = false
config.isCpuTrackingEnabled = false
val isPerformanceMonitoringEnabled: Boolean
    get() = BuildConfig.DEBUG
```

## Usage

### Basic Performance Tracking

```kotlin
```kotlin
@Inject
lateinit var performanceMonitor: NavigationPerformanceMonitor

// Track navigation performance
performanceMonitor.startNavigation("users/list")
// ... perform navigation
performanceMonitor.endNavigation("users/list")

// Get performance statistics
val stats = performanceMonitor.getPerformanceStats()
@Inject
lateinit var performanceConfig: NavigationPerformanceConfig

if (performanceConfig.isPerformanceMonitoringEnabled) {
    // Track performance
}
@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {
    @Provides
    @Singleton
    fun provideNavigation3Controller(
        impl: Navigation3ControllerImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): Navigation3Controller {
        return NavigationPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object DeepLinkHandlerModule {
    @Provides
    @IntoSet
    fun provideUserDeepLinkHandler(
        impl: UserDeepLinkHandler,
        performanceMonitor: NavigationPerformanceMonitor
    ): DeepLinkHandler {
        return DeepLinkPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object FeatureApiModule {
    @Provides
    fun provideUserFeatureApi(
        impl: UserFeatureApiImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): UserFeatureApi {
        return FeatureApiPerformanceInterceptor(impl, performanceMonitor) as UserFeatureApi
    }
}
@Composable
fun DebugScreen() {
    NavigationPerformanceDashboard()
}
val config = NavigationPerformanceConfig()

// Navigation thresholds
config.slowNavigationThreshold = 1000L // 1 second
config.slowDeepLinkResolutionThreshold = 500L // 500ms
config.slowFeatureApiCallThreshold = 200L // 200ms

// Memory limits
config.maxPerformanceSamples = 1000

// Feature toggles
config.isPerformanceMonitoringEnabled = true
config.isLoggingEnabled = true
config.isMemoryTrackingEnabled = false
config.isCpuTrackingEnabled = false
val isPerformanceMonitoringEnabled: Boolean
    get() = BuildConfig.DEBUG
class UserListViewModel @Inject constructor(
    private val performanceMonitor: NavigationPerformanceMonitor
) {
    fun navigateToUserDetail(username: String) {
        performanceMonitor.startNavigation("users/detail/$username")
        try {
            // Perform navigation
            navigationController.navigate(
                NavCommand(
                    route = "users/detail/$username",
                    deepLink = "githubusers://users/detail/$username"
                )
            )
        } finally {
            performanceMonitor.endNavigation("users/detail/$username")
        }
    }
}
```

### Performance Statistics

```kotlin
```kotlin
@Inject
lateinit var performanceMonitor: NavigationPerformanceMonitor

// Track navigation performance
performanceMonitor.startNavigation("users/list")
// ... perform navigation
performanceMonitor.endNavigation("users/list")

// Get performance statistics
val stats = performanceMonitor.getPerformanceStats()
@Inject
lateinit var performanceConfig: NavigationPerformanceConfig

if (performanceConfig.isPerformanceMonitoringEnabled) {
    // Track performance
}
@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {
    @Provides
    @Singleton
    fun provideNavigation3Controller(
        impl: Navigation3ControllerImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): Navigation3Controller {
        return NavigationPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object DeepLinkHandlerModule {
    @Provides
    @IntoSet
    fun provideUserDeepLinkHandler(
        impl: UserDeepLinkHandler,
        performanceMonitor: NavigationPerformanceMonitor
    ): DeepLinkHandler {
        return DeepLinkPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object FeatureApiModule {
    @Provides
    fun provideUserFeatureApi(
        impl: UserFeatureApiImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): UserFeatureApi {
        return FeatureApiPerformanceInterceptor(impl, performanceMonitor) as UserFeatureApi
    }
}
@Composable
fun DebugScreen() {
    NavigationPerformanceDashboard()
}
val config = NavigationPerformanceConfig()

// Navigation thresholds
config.slowNavigationThreshold = 1000L // 1 second
config.slowDeepLinkResolutionThreshold = 500L // 500ms
config.slowFeatureApiCallThreshold = 200L // 200ms

// Memory limits
config.maxPerformanceSamples = 1000

// Feature toggles
config.isPerformanceMonitoringEnabled = true
config.isLoggingEnabled = true
config.isMemoryTrackingEnabled = false
config.isCpuTrackingEnabled = false
val isPerformanceMonitoringEnabled: Boolean
    get() = BuildConfig.DEBUG
class UserListViewModel @Inject constructor(
    private val performanceMonitor: NavigationPerformanceMonitor
) {
    fun navigateToUserDetail(username: String) {
        performanceMonitor.startNavigation("users/detail/$username")
        try {
            // Perform navigation
            navigationController.navigate(
                NavCommand(
                    route = "users/detail/$username",
                    deepLink = "githubusers://users/detail/$username"
                )
            )
        } finally {
            performanceMonitor.endNavigation("users/detail/$username")
        }
    }
}
// Get overall performance statistics
val stats = performanceMonitor.getPerformanceStats()
println("Total navigation calls: ${stats.totalNavigationCalls}")
println("Average navigation time: ${stats.averageNavigationTime}ms")

// Get specific navigation performance
val userListPerformance = performanceMonitor.getNavigationPerformance("users/list")
userListPerformance?.let {
    println("User list navigation - Average: ${it.averageTime}ms, Max: ${it.maxTime}ms")
}

// Get deep link performance
val deepLinkPerformance = performanceMonitor.getDeepLinkPerformance("githubusers://users/list")
deepLinkPerformance?.let {
    println("Deep link resolution - Average: ${it.averageTime}ms, Max: ${it.maxTime}ms")
}
```

### Performance Reports

```kotlin
```kotlin
@Inject
lateinit var performanceMonitor: NavigationPerformanceMonitor

// Track navigation performance
performanceMonitor.startNavigation("users/list")
// ... perform navigation
performanceMonitor.endNavigation("users/list")

// Get performance statistics
val stats = performanceMonitor.getPerformanceStats()
@Inject
lateinit var performanceConfig: NavigationPerformanceConfig

if (performanceConfig.isPerformanceMonitoringEnabled) {
    // Track performance
}
@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {
    @Provides
    @Singleton
    fun provideNavigation3Controller(
        impl: Navigation3ControllerImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): Navigation3Controller {
        return NavigationPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object DeepLinkHandlerModule {
    @Provides
    @IntoSet
    fun provideUserDeepLinkHandler(
        impl: UserDeepLinkHandler,
        performanceMonitor: NavigationPerformanceMonitor
    ): DeepLinkHandler {
        return DeepLinkPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object FeatureApiModule {
    @Provides
    fun provideUserFeatureApi(
        impl: UserFeatureApiImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): UserFeatureApi {
        return FeatureApiPerformanceInterceptor(impl, performanceMonitor) as UserFeatureApi
    }
}
@Composable
fun DebugScreen() {
    NavigationPerformanceDashboard()
}
val config = NavigationPerformanceConfig()

// Navigation thresholds
config.slowNavigationThreshold = 1000L // 1 second
config.slowDeepLinkResolutionThreshold = 500L // 500ms
config.slowFeatureApiCallThreshold = 200L // 200ms

// Memory limits
config.maxPerformanceSamples = 1000

// Feature toggles
config.isPerformanceMonitoringEnabled = true
config.isLoggingEnabled = true
config.isMemoryTrackingEnabled = false
config.isCpuTrackingEnabled = false
val isPerformanceMonitoringEnabled: Boolean
    get() = BuildConfig.DEBUG
class UserListViewModel @Inject constructor(
    private val performanceMonitor: NavigationPerformanceMonitor
) {
    fun navigateToUserDetail(username: String) {
        performanceMonitor.startNavigation("users/detail/$username")
        try {
            // Perform navigation
            navigationController.navigate(
                NavCommand(
                    route = "users/detail/$username",
                    deepLink = "githubusers://users/detail/$username"
                )
            )
        } finally {
            performanceMonitor.endNavigation("users/detail/$username")
        }
    }
}
// Get overall performance statistics
val stats = performanceMonitor.getPerformanceStats()
println("Total navigation calls: ${stats.totalNavigationCalls}")
println("Average navigation time: ${stats.averageNavigationTime}ms")

// Get specific navigation performance
val userListPerformance = performanceMonitor.getNavigationPerformance("users/list")
userListPerformance?.let {
    println("User list navigation - Average: ${it.averageTime}ms, Max: ${it.maxTime}ms")
}

// Get deep link performance
val deepLinkPerformance = performanceMonitor.getDeepLinkPerformance("githubusers://users/list")
deepLinkPerformance?.let {
    println("Deep link resolution - Average: ${it.averageTime}ms, Max: ${it.maxTime}ms")
}
// Get formatted performance report
val report = performanceMonitor.getPerformanceReport()
println(report)

// Export performance data
val stats = performanceMonitor.getPerformanceStats()
// Save to file, send to analytics, etc.
```

### Dashboard Integration

```kotlin
```kotlin
@Inject
lateinit var performanceMonitor: NavigationPerformanceMonitor

// Track navigation performance
performanceMonitor.startNavigation("users/list")
// ... perform navigation
performanceMonitor.endNavigation("users/list")

// Get performance statistics
val stats = performanceMonitor.getPerformanceStats()
@Inject
lateinit var performanceConfig: NavigationPerformanceConfig

if (performanceConfig.isPerformanceMonitoringEnabled) {
    // Track performance
}
@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {
    @Provides
    @Singleton
    fun provideNavigation3Controller(
        impl: Navigation3ControllerImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): Navigation3Controller {
        return NavigationPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object DeepLinkHandlerModule {
    @Provides
    @IntoSet
    fun provideUserDeepLinkHandler(
        impl: UserDeepLinkHandler,
        performanceMonitor: NavigationPerformanceMonitor
    ): DeepLinkHandler {
        return DeepLinkPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object FeatureApiModule {
    @Provides
    fun provideUserFeatureApi(
        impl: UserFeatureApiImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): UserFeatureApi {
        return FeatureApiPerformanceInterceptor(impl, performanceMonitor) as UserFeatureApi
    }
}
@Composable
fun DebugScreen() {
    NavigationPerformanceDashboard()
}
val config = NavigationPerformanceConfig()

// Navigation thresholds
config.slowNavigationThreshold = 1000L // 1 second
config.slowDeepLinkResolutionThreshold = 500L // 500ms
config.slowFeatureApiCallThreshold = 200L // 200ms

// Memory limits
config.maxPerformanceSamples = 1000

// Feature toggles
config.isPerformanceMonitoringEnabled = true
config.isLoggingEnabled = true
config.isMemoryTrackingEnabled = false
config.isCpuTrackingEnabled = false
val isPerformanceMonitoringEnabled: Boolean
    get() = BuildConfig.DEBUG
class UserListViewModel @Inject constructor(
    private val performanceMonitor: NavigationPerformanceMonitor
) {
    fun navigateToUserDetail(username: String) {
        performanceMonitor.startNavigation("users/detail/$username")
        try {
            // Perform navigation
            navigationController.navigate(
                NavCommand(
                    route = "users/detail/$username",
                    deepLink = "githubusers://users/detail/$username"
                )
            )
        } finally {
            performanceMonitor.endNavigation("users/detail/$username")
        }
    }
}
// Get overall performance statistics
val stats = performanceMonitor.getPerformanceStats()
println("Total navigation calls: ${stats.totalNavigationCalls}")
println("Average navigation time: ${stats.averageNavigationTime}ms")

// Get specific navigation performance
val userListPerformance = performanceMonitor.getNavigationPerformance("users/list")
userListPerformance?.let {
    println("User list navigation - Average: ${it.averageTime}ms, Max: ${it.maxTime}ms")
}

// Get deep link performance
val deepLinkPerformance = performanceMonitor.getDeepLinkPerformance("githubusers://users/list")
deepLinkPerformance?.let {
    println("Deep link resolution - Average: ${it.averageTime}ms, Max: ${it.maxTime}ms")
}
// Get formatted performance report
val report = performanceMonitor.getPerformanceReport()
println(report)

// Export performance data
val stats = performanceMonitor.getPerformanceStats()
// Save to file, send to analytics, etc.
@Composable
fun DebugNavigationScreen() {
    NavigationPerformanceDashboard(
        performanceMonitor = hiltViewModel<DebugViewModel>().performanceMonitor
    )
}
```

## Performance Metrics

### Navigation Metrics

- **Total Navigation Calls**: Number of navigation operations performed
- **Average Navigation Time**: Average time for navigation operations
- **Slowest Navigations**: Top 5 slowest navigation operations
- **Navigation Performance by Destination**: Performance breakdown by destination

### Deep Link Metrics

- **Total Deep Link Resolutions**: Number of deep link resolution operations
- **Average Deep Link Resolution Time**: Average time for deep link resolution
- **Slowest Deep Link Resolutions**: Top 5 slowest deep link resolution operations
- **Deep Link Performance by URI**: Performance breakdown by deep link URI

### Feature API Metrics

- **Total Feature API Calls**: Number of feature API calls made
- **Average Feature API Call Time**: Average time for feature API calls
- **Feature API Performance by Method**: Performance breakdown by API method

## Best Practices

### 1. Use Interceptors for Automatic Tracking

Prefer using interceptors over manual tracking for consistent performance monitoring:

```kotlin
```kotlin
@Inject
lateinit var performanceMonitor: NavigationPerformanceMonitor

// Track navigation performance
performanceMonitor.startNavigation("users/list")
// ... perform navigation
performanceMonitor.endNavigation("users/list")

// Get performance statistics
val stats = performanceMonitor.getPerformanceStats()
@Inject
lateinit var performanceConfig: NavigationPerformanceConfig

if (performanceConfig.isPerformanceMonitoringEnabled) {
    // Track performance
}
@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {
    @Provides
    @Singleton
    fun provideNavigation3Controller(
        impl: Navigation3ControllerImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): Navigation3Controller {
        return NavigationPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object DeepLinkHandlerModule {
    @Provides
    @IntoSet
    fun provideUserDeepLinkHandler(
        impl: UserDeepLinkHandler,
        performanceMonitor: NavigationPerformanceMonitor
    ): DeepLinkHandler {
        return DeepLinkPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object FeatureApiModule {
    @Provides
    fun provideUserFeatureApi(
        impl: UserFeatureApiImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): UserFeatureApi {
        return FeatureApiPerformanceInterceptor(impl, performanceMonitor) as UserFeatureApi
    }
}
@Composable
fun DebugScreen() {
    NavigationPerformanceDashboard()
}
val config = NavigationPerformanceConfig()

// Navigation thresholds
config.slowNavigationThreshold = 1000L // 1 second
config.slowDeepLinkResolutionThreshold = 500L // 500ms
config.slowFeatureApiCallThreshold = 200L // 200ms

// Memory limits
config.maxPerformanceSamples = 1000

// Feature toggles
config.isPerformanceMonitoringEnabled = true
config.isLoggingEnabled = true
config.isMemoryTrackingEnabled = false
config.isCpuTrackingEnabled = false
val isPerformanceMonitoringEnabled: Boolean
    get() = BuildConfig.DEBUG
class UserListViewModel @Inject constructor(
    private val performanceMonitor: NavigationPerformanceMonitor
) {
    fun navigateToUserDetail(username: String) {
        performanceMonitor.startNavigation("users/detail/$username")
        try {
            // Perform navigation
            navigationController.navigate(
                NavCommand(
                    route = "users/detail/$username",
                    deepLink = "githubusers://users/detail/$username"
                )
            )
        } finally {
            performanceMonitor.endNavigation("users/detail/$username")
        }
    }
}
// Get overall performance statistics
val stats = performanceMonitor.getPerformanceStats()
println("Total navigation calls: ${stats.totalNavigationCalls}")
println("Average navigation time: ${stats.averageNavigationTime}ms")

// Get specific navigation performance
val userListPerformance = performanceMonitor.getNavigationPerformance("users/list")
userListPerformance?.let {
    println("User list navigation - Average: ${it.averageTime}ms, Max: ${it.maxTime}ms")
}

// Get deep link performance
val deepLinkPerformance = performanceMonitor.getDeepLinkPerformance("githubusers://users/list")
deepLinkPerformance?.let {
    println("Deep link resolution - Average: ${it.averageTime}ms, Max: ${it.maxTime}ms")
}
// Get formatted performance report
val report = performanceMonitor.getPerformanceReport()
println(report)

// Export performance data
val stats = performanceMonitor.getPerformanceStats()
// Save to file, send to analytics, etc.
@Composable
fun DebugNavigationScreen() {
    NavigationPerformanceDashboard(
        performanceMonitor = hiltViewModel<DebugViewModel>().performanceMonitor
    )
}
// ✅ Good: Use interceptor
@Provides
@Singleton
fun provideNavigation3Controller(
    impl: Navigation3ControllerImpl,
    performanceMonitor: NavigationPerformanceMonitor
): Navigation3Controller {
    return NavigationPerformanceInterceptor(impl, performanceMonitor)
}

// ❌ Avoid: Manual tracking everywhere
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        performanceMonitor.startNavigation("users/detail/$username")
        // ... navigation logic
        performanceMonitor.endNavigation("users/detail/$username")
    }
}
```

### 2. Configure Appropriate Thresholds

Set performance thresholds based on your app's requirements:

```kotlin
```kotlin
@Inject
lateinit var performanceMonitor: NavigationPerformanceMonitor

// Track navigation performance
performanceMonitor.startNavigation("users/list")
// ... perform navigation
performanceMonitor.endNavigation("users/list")

// Get performance statistics
val stats = performanceMonitor.getPerformanceStats()
@Inject
lateinit var performanceConfig: NavigationPerformanceConfig

if (performanceConfig.isPerformanceMonitoringEnabled) {
    // Track performance
}
@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {
    @Provides
    @Singleton
    fun provideNavigation3Controller(
        impl: Navigation3ControllerImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): Navigation3Controller {
        return NavigationPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object DeepLinkHandlerModule {
    @Provides
    @IntoSet
    fun provideUserDeepLinkHandler(
        impl: UserDeepLinkHandler,
        performanceMonitor: NavigationPerformanceMonitor
    ): DeepLinkHandler {
        return DeepLinkPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object FeatureApiModule {
    @Provides
    fun provideUserFeatureApi(
        impl: UserFeatureApiImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): UserFeatureApi {
        return FeatureApiPerformanceInterceptor(impl, performanceMonitor) as UserFeatureApi
    }
}
@Composable
fun DebugScreen() {
    NavigationPerformanceDashboard()
}
val config = NavigationPerformanceConfig()

// Navigation thresholds
config.slowNavigationThreshold = 1000L // 1 second
config.slowDeepLinkResolutionThreshold = 500L // 500ms
config.slowFeatureApiCallThreshold = 200L // 200ms

// Memory limits
config.maxPerformanceSamples = 1000

// Feature toggles
config.isPerformanceMonitoringEnabled = true
config.isLoggingEnabled = true
config.isMemoryTrackingEnabled = false
config.isCpuTrackingEnabled = false
val isPerformanceMonitoringEnabled: Boolean
    get() = BuildConfig.DEBUG
class UserListViewModel @Inject constructor(
    private val performanceMonitor: NavigationPerformanceMonitor
) {
    fun navigateToUserDetail(username: String) {
        performanceMonitor.startNavigation("users/detail/$username")
        try {
            // Perform navigation
            navigationController.navigate(
                NavCommand(
                    route = "users/detail/$username",
                    deepLink = "githubusers://users/detail/$username"
                )
            )
        } finally {
            performanceMonitor.endNavigation("users/detail/$username")
        }
    }
}
// Get overall performance statistics
val stats = performanceMonitor.getPerformanceStats()
println("Total navigation calls: ${stats.totalNavigationCalls}")
println("Average navigation time: ${stats.averageNavigationTime}ms")

// Get specific navigation performance
val userListPerformance = performanceMonitor.getNavigationPerformance("users/list")
userListPerformance?.let {
    println("User list navigation - Average: ${it.averageTime}ms, Max: ${it.maxTime}ms")
}

// Get deep link performance
val deepLinkPerformance = performanceMonitor.getDeepLinkPerformance("githubusers://users/list")
deepLinkPerformance?.let {
    println("Deep link resolution - Average: ${it.averageTime}ms, Max: ${it.maxTime}ms")
}
// Get formatted performance report
val report = performanceMonitor.getPerformanceReport()
println(report)

// Export performance data
val stats = performanceMonitor.getPerformanceStats()
// Save to file, send to analytics, etc.
@Composable
fun DebugNavigationScreen() {
    NavigationPerformanceDashboard(
        performanceMonitor = hiltViewModel<DebugViewModel>().performanceMonitor
    )
}
// ✅ Good: Use interceptor
@Provides
@Singleton
fun provideNavigation3Controller(
    impl: Navigation3ControllerImpl,
    performanceMonitor: NavigationPerformanceMonitor
): Navigation3Controller {
    return NavigationPerformanceInterceptor(impl, performanceMonitor)
}

// ❌ Avoid: Manual tracking everywhere
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        performanceMonitor.startNavigation("users/detail/$username")
        // ... navigation logic
        performanceMonitor.endNavigation("users/detail/$username")
    }
}
val config = NavigationPerformanceConfig().apply {
    slowNavigationThreshold = 1000L // 1 second
    slowDeepLinkResolutionThreshold = 500L // 500ms
    slowFeatureApiCallThreshold = 200L // 200ms
}
```

### 3. Monitor Performance in Debug Builds

Enable performance monitoring in debug builds for development and testing:

```kotlin
```kotlin
@Inject
lateinit var performanceMonitor: NavigationPerformanceMonitor

// Track navigation performance
performanceMonitor.startNavigation("users/list")
// ... perform navigation
performanceMonitor.endNavigation("users/list")

// Get performance statistics
val stats = performanceMonitor.getPerformanceStats()
@Inject
lateinit var performanceConfig: NavigationPerformanceConfig

if (performanceConfig.isPerformanceMonitoringEnabled) {
    // Track performance
}
@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {
    @Provides
    @Singleton
    fun provideNavigation3Controller(
        impl: Navigation3ControllerImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): Navigation3Controller {
        return NavigationPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object DeepLinkHandlerModule {
    @Provides
    @IntoSet
    fun provideUserDeepLinkHandler(
        impl: UserDeepLinkHandler,
        performanceMonitor: NavigationPerformanceMonitor
    ): DeepLinkHandler {
        return DeepLinkPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object FeatureApiModule {
    @Provides
    fun provideUserFeatureApi(
        impl: UserFeatureApiImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): UserFeatureApi {
        return FeatureApiPerformanceInterceptor(impl, performanceMonitor) as UserFeatureApi
    }
}
@Composable
fun DebugScreen() {
    NavigationPerformanceDashboard()
}
val config = NavigationPerformanceConfig()

// Navigation thresholds
config.slowNavigationThreshold = 1000L // 1 second
config.slowDeepLinkResolutionThreshold = 500L // 500ms
config.slowFeatureApiCallThreshold = 200L // 200ms

// Memory limits
config.maxPerformanceSamples = 1000

// Feature toggles
config.isPerformanceMonitoringEnabled = true
config.isLoggingEnabled = true
config.isMemoryTrackingEnabled = false
config.isCpuTrackingEnabled = false
val isPerformanceMonitoringEnabled: Boolean
    get() = BuildConfig.DEBUG
class UserListViewModel @Inject constructor(
    private val performanceMonitor: NavigationPerformanceMonitor
) {
    fun navigateToUserDetail(username: String) {
        performanceMonitor.startNavigation("users/detail/$username")
        try {
            // Perform navigation
            navigationController.navigate(
                NavCommand(
                    route = "users/detail/$username",
                    deepLink = "githubusers://users/detail/$username"
                )
            )
        } finally {
            performanceMonitor.endNavigation("users/detail/$username")
        }
    }
}
// Get overall performance statistics
val stats = performanceMonitor.getPerformanceStats()
println("Total navigation calls: ${stats.totalNavigationCalls}")
println("Average navigation time: ${stats.averageNavigationTime}ms")

// Get specific navigation performance
val userListPerformance = performanceMonitor.getNavigationPerformance("users/list")
userListPerformance?.let {
    println("User list navigation - Average: ${it.averageTime}ms, Max: ${it.maxTime}ms")
}

// Get deep link performance
val deepLinkPerformance = performanceMonitor.getDeepLinkPerformance("githubusers://users/list")
deepLinkPerformance?.let {
    println("Deep link resolution - Average: ${it.averageTime}ms, Max: ${it.maxTime}ms")
}
// Get formatted performance report
val report = performanceMonitor.getPerformanceReport()
println(report)

// Export performance data
val stats = performanceMonitor.getPerformanceStats()
// Save to file, send to analytics, etc.
@Composable
fun DebugNavigationScreen() {
    NavigationPerformanceDashboard(
        performanceMonitor = hiltViewModel<DebugViewModel>().performanceMonitor
    )
}
// ✅ Good: Use interceptor
@Provides
@Singleton
fun provideNavigation3Controller(
    impl: Navigation3ControllerImpl,
    performanceMonitor: NavigationPerformanceMonitor
): Navigation3Controller {
    return NavigationPerformanceInterceptor(impl, performanceMonitor)
}

// ❌ Avoid: Manual tracking everywhere
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        performanceMonitor.startNavigation("users/detail/$username")
        // ... navigation logic
        performanceMonitor.endNavigation("users/detail/$username")
    }
}
val config = NavigationPerformanceConfig().apply {
    slowNavigationThreshold = 1000L // 1 second
    slowDeepLinkResolutionThreshold = 500L // 500ms
    slowFeatureApiCallThreshold = 200L // 200ms
}
val isPerformanceMonitoringEnabled: Boolean
    get() = BuildConfig.DEBUG
```

### 4. Use Dashboard for Real-Time Monitoring

Use the performance dashboard during development to identify performance issues:

```kotlin
```kotlin
@Inject
lateinit var performanceMonitor: NavigationPerformanceMonitor

// Track navigation performance
performanceMonitor.startNavigation("users/list")
// ... perform navigation
performanceMonitor.endNavigation("users/list")

// Get performance statistics
val stats = performanceMonitor.getPerformanceStats()
@Inject
lateinit var performanceConfig: NavigationPerformanceConfig

if (performanceConfig.isPerformanceMonitoringEnabled) {
    // Track performance
}
@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {
    @Provides
    @Singleton
    fun provideNavigation3Controller(
        impl: Navigation3ControllerImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): Navigation3Controller {
        return NavigationPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object DeepLinkHandlerModule {
    @Provides
    @IntoSet
    fun provideUserDeepLinkHandler(
        impl: UserDeepLinkHandler,
        performanceMonitor: NavigationPerformanceMonitor
    ): DeepLinkHandler {
        return DeepLinkPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object FeatureApiModule {
    @Provides
    fun provideUserFeatureApi(
        impl: UserFeatureApiImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): UserFeatureApi {
        return FeatureApiPerformanceInterceptor(impl, performanceMonitor) as UserFeatureApi
    }
}
@Composable
fun DebugScreen() {
    NavigationPerformanceDashboard()
}
val config = NavigationPerformanceConfig()

// Navigation thresholds
config.slowNavigationThreshold = 1000L // 1 second
config.slowDeepLinkResolutionThreshold = 500L // 500ms
config.slowFeatureApiCallThreshold = 200L // 200ms

// Memory limits
config.maxPerformanceSamples = 1000

// Feature toggles
config.isPerformanceMonitoringEnabled = true
config.isLoggingEnabled = true
config.isMemoryTrackingEnabled = false
config.isCpuTrackingEnabled = false
val isPerformanceMonitoringEnabled: Boolean
    get() = BuildConfig.DEBUG
class UserListViewModel @Inject constructor(
    private val performanceMonitor: NavigationPerformanceMonitor
) {
    fun navigateToUserDetail(username: String) {
        performanceMonitor.startNavigation("users/detail/$username")
        try {
            // Perform navigation
            navigationController.navigate(
                NavCommand(
                    route = "users/detail/$username",
                    deepLink = "githubusers://users/detail/$username"
                )
            )
        } finally {
            performanceMonitor.endNavigation("users/detail/$username")
        }
    }
}
// Get overall performance statistics
val stats = performanceMonitor.getPerformanceStats()
println("Total navigation calls: ${stats.totalNavigationCalls}")
println("Average navigation time: ${stats.averageNavigationTime}ms")

// Get specific navigation performance
val userListPerformance = performanceMonitor.getNavigationPerformance("users/list")
userListPerformance?.let {
    println("User list navigation - Average: ${it.averageTime}ms, Max: ${it.maxTime}ms")
}

// Get deep link performance
val deepLinkPerformance = performanceMonitor.getDeepLinkPerformance("githubusers://users/list")
deepLinkPerformance?.let {
    println("Deep link resolution - Average: ${it.averageTime}ms, Max: ${it.maxTime}ms")
}
// Get formatted performance report
val report = performanceMonitor.getPerformanceReport()
println(report)

// Export performance data
val stats = performanceMonitor.getPerformanceStats()
// Save to file, send to analytics, etc.
@Composable
fun DebugNavigationScreen() {
    NavigationPerformanceDashboard(
        performanceMonitor = hiltViewModel<DebugViewModel>().performanceMonitor
    )
}
// ✅ Good: Use interceptor
@Provides
@Singleton
fun provideNavigation3Controller(
    impl: Navigation3ControllerImpl,
    performanceMonitor: NavigationPerformanceMonitor
): Navigation3Controller {
    return NavigationPerformanceInterceptor(impl, performanceMonitor)
}

// ❌ Avoid: Manual tracking everywhere
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        performanceMonitor.startNavigation("users/detail/$username")
        // ... navigation logic
        performanceMonitor.endNavigation("users/detail/$username")
    }
}
val config = NavigationPerformanceConfig().apply {
    slowNavigationThreshold = 1000L // 1 second
    slowDeepLinkResolutionThreshold = 500L // 500ms
    slowFeatureApiCallThreshold = 200L // 200ms
}
val isPerformanceMonitoringEnabled: Boolean
    get() = BuildConfig.DEBUG
@Composable
fun DebugScreen() {
    NavigationPerformanceDashboard()
}
```

### 5. Export Performance Reports

Regularly export performance reports for analysis:

```kotlin
```kotlin
@Inject
lateinit var performanceMonitor: NavigationPerformanceMonitor

// Track navigation performance
performanceMonitor.startNavigation("users/list")
// ... perform navigation
performanceMonitor.endNavigation("users/list")

// Get performance statistics
val stats = performanceMonitor.getPerformanceStats()
@Inject
lateinit var performanceConfig: NavigationPerformanceConfig

if (performanceConfig.isPerformanceMonitoringEnabled) {
    // Track performance
}
@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {
    @Provides
    @Singleton
    fun provideNavigation3Controller(
        impl: Navigation3ControllerImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): Navigation3Controller {
        return NavigationPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object DeepLinkHandlerModule {
    @Provides
    @IntoSet
    fun provideUserDeepLinkHandler(
        impl: UserDeepLinkHandler,
        performanceMonitor: NavigationPerformanceMonitor
    ): DeepLinkHandler {
        return DeepLinkPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object FeatureApiModule {
    @Provides
    fun provideUserFeatureApi(
        impl: UserFeatureApiImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): UserFeatureApi {
        return FeatureApiPerformanceInterceptor(impl, performanceMonitor) as UserFeatureApi
    }
}
@Composable
fun DebugScreen() {
    NavigationPerformanceDashboard()
}
val config = NavigationPerformanceConfig()

// Navigation thresholds
config.slowNavigationThreshold = 1000L // 1 second
config.slowDeepLinkResolutionThreshold = 500L // 500ms
config.slowFeatureApiCallThreshold = 200L // 200ms

// Memory limits
config.maxPerformanceSamples = 1000

// Feature toggles
config.isPerformanceMonitoringEnabled = true
config.isLoggingEnabled = true
config.isMemoryTrackingEnabled = false
config.isCpuTrackingEnabled = false
val isPerformanceMonitoringEnabled: Boolean
    get() = BuildConfig.DEBUG
class UserListViewModel @Inject constructor(
    private val performanceMonitor: NavigationPerformanceMonitor
) {
    fun navigateToUserDetail(username: String) {
        performanceMonitor.startNavigation("users/detail/$username")
        try {
            // Perform navigation
            navigationController.navigate(
                NavCommand(
                    route = "users/detail/$username",
                    deepLink = "githubusers://users/detail/$username"
                )
            )
        } finally {
            performanceMonitor.endNavigation("users/detail/$username")
        }
    }
}
// Get overall performance statistics
val stats = performanceMonitor.getPerformanceStats()
println("Total navigation calls: ${stats.totalNavigationCalls}")
println("Average navigation time: ${stats.averageNavigationTime}ms")

// Get specific navigation performance
val userListPerformance = performanceMonitor.getNavigationPerformance("users/list")
userListPerformance?.let {
    println("User list navigation - Average: ${it.averageTime}ms, Max: ${it.maxTime}ms")
}

// Get deep link performance
val deepLinkPerformance = performanceMonitor.getDeepLinkPerformance("githubusers://users/list")
deepLinkPerformance?.let {
    println("Deep link resolution - Average: ${it.averageTime}ms, Max: ${it.maxTime}ms")
}
// Get formatted performance report
val report = performanceMonitor.getPerformanceReport()
println(report)

// Export performance data
val stats = performanceMonitor.getPerformanceStats()
// Save to file, send to analytics, etc.
@Composable
fun DebugNavigationScreen() {
    NavigationPerformanceDashboard(
        performanceMonitor = hiltViewModel<DebugViewModel>().performanceMonitor
    )
}
// ✅ Good: Use interceptor
@Provides
@Singleton
fun provideNavigation3Controller(
    impl: Navigation3ControllerImpl,
    performanceMonitor: NavigationPerformanceMonitor
): Navigation3Controller {
    return NavigationPerformanceInterceptor(impl, performanceMonitor)
}

// ❌ Avoid: Manual tracking everywhere
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        performanceMonitor.startNavigation("users/detail/$username")
        // ... navigation logic
        performanceMonitor.endNavigation("users/detail/$username")
    }
}
val config = NavigationPerformanceConfig().apply {
    slowNavigationThreshold = 1000L // 1 second
    slowDeepLinkResolutionThreshold = 500L // 500ms
    slowFeatureApiCallThreshold = 200L // 200ms
}
val isPerformanceMonitoringEnabled: Boolean
    get() = BuildConfig.DEBUG
@Composable
fun DebugScreen() {
    NavigationPerformanceDashboard()
}
// Export performance report
val report = performanceMonitor.getPerformanceReport()
// Save to file, send to analytics, etc.
```

## Troubleshooting

### Performance Monitoring Not Working

1. **Check Configuration**: Ensure `isPerformanceMonitoringEnabled` is true
2. **Verify Interceptors**: Make sure interceptors are properly configured in Hilt modules
3. **Check Logs**: Look for performance monitoring logs in the console

### High Memory Usage

1. **Limit Samples**: Reduce `maxPerformanceSamples` in configuration
2. **Disable Memory Tracking**: Set `isMemoryTrackingEnabled` to false
3. **Clear Data**: Regularly call `clearPerformanceData()` to free memory

### Slow Performance

1. **Check Thresholds**: Verify performance thresholds are appropriate
2. **Review Interceptors**: Ensure interceptors are not adding significant overhead
3. **Profile Code**: Use Android Studio profiler to identify bottlenecks

## Future Enhancements

### Planned Features

1. **Automatic Performance Reports**: Scheduled export of performance reports
2. **Performance Alerts**: Notifications when performance thresholds are exceeded
3. **Performance Trends**: Historical performance data and trend analysis
4. **Integration with Analytics**: Send performance data to analytics services
5. **Performance Testing**: Automated performance testing in CI/CD pipeline

### Custom Metrics

The performance monitoring system can be extended to track custom metrics:

```kotlin
```kotlin
@Inject
lateinit var performanceMonitor: NavigationPerformanceMonitor

// Track navigation performance
performanceMonitor.startNavigation("users/list")
// ... perform navigation
performanceMonitor.endNavigation("users/list")

// Get performance statistics
val stats = performanceMonitor.getPerformanceStats()
@Inject
lateinit var performanceConfig: NavigationPerformanceConfig

if (performanceConfig.isPerformanceMonitoringEnabled) {
    // Track performance
}
@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {
    @Provides
    @Singleton
    fun provideNavigation3Controller(
        impl: Navigation3ControllerImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): Navigation3Controller {
        return NavigationPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object DeepLinkHandlerModule {
    @Provides
    @IntoSet
    fun provideUserDeepLinkHandler(
        impl: UserDeepLinkHandler,
        performanceMonitor: NavigationPerformanceMonitor
    ): DeepLinkHandler {
        return DeepLinkPerformanceInterceptor(impl, performanceMonitor)
    }
}
@Module
@InstallIn(SingletonComponent::class)
object FeatureApiModule {
    @Provides
    fun provideUserFeatureApi(
        impl: UserFeatureApiImpl,
        performanceMonitor: NavigationPerformanceMonitor
    ): UserFeatureApi {
        return FeatureApiPerformanceInterceptor(impl, performanceMonitor) as UserFeatureApi
    }
}
@Composable
fun DebugScreen() {
    NavigationPerformanceDashboard()
}
val config = NavigationPerformanceConfig()

// Navigation thresholds
config.slowNavigationThreshold = 1000L // 1 second
config.slowDeepLinkResolutionThreshold = 500L // 500ms
config.slowFeatureApiCallThreshold = 200L // 200ms

// Memory limits
config.maxPerformanceSamples = 1000

// Feature toggles
config.isPerformanceMonitoringEnabled = true
config.isLoggingEnabled = true
config.isMemoryTrackingEnabled = false
config.isCpuTrackingEnabled = false
val isPerformanceMonitoringEnabled: Boolean
    get() = BuildConfig.DEBUG
class UserListViewModel @Inject constructor(
    private val performanceMonitor: NavigationPerformanceMonitor
) {
    fun navigateToUserDetail(username: String) {
        performanceMonitor.startNavigation("users/detail/$username")
        try {
            // Perform navigation
            navigationController.navigate(
                NavCommand(
                    route = "users/detail/$username",
                    deepLink = "githubusers://users/detail/$username"
                )
            )
        } finally {
            performanceMonitor.endNavigation("users/detail/$username")
        }
    }
}
// Get overall performance statistics
val stats = performanceMonitor.getPerformanceStats()
println("Total navigation calls: ${stats.totalNavigationCalls}")
println("Average navigation time: ${stats.averageNavigationTime}ms")

// Get specific navigation performance
val userListPerformance = performanceMonitor.getNavigationPerformance("users/list")
userListPerformance?.let {
    println("User list navigation - Average: ${it.averageTime}ms, Max: ${it.maxTime}ms")
}

// Get deep link performance
val deepLinkPerformance = performanceMonitor.getDeepLinkPerformance("githubusers://users/list")
deepLinkPerformance?.let {
    println("Deep link resolution - Average: ${it.averageTime}ms, Max: ${it.maxTime}ms")
}
// Get formatted performance report
val report = performanceMonitor.getPerformanceReport()
println(report)

// Export performance data
val stats = performanceMonitor.getPerformanceStats()
// Save to file, send to analytics, etc.
@Composable
fun DebugNavigationScreen() {
    NavigationPerformanceDashboard(
        performanceMonitor = hiltViewModel<DebugViewModel>().performanceMonitor
    )
}
// ✅ Good: Use interceptor
@Provides
@Singleton
fun provideNavigation3Controller(
    impl: Navigation3ControllerImpl,
    performanceMonitor: NavigationPerformanceMonitor
): Navigation3Controller {
    return NavigationPerformanceInterceptor(impl, performanceMonitor)
}

// ❌ Avoid: Manual tracking everywhere
class UserListViewModel {
    fun navigateToUserDetail(username: String) {
        performanceMonitor.startNavigation("users/detail/$username")
        // ... navigation logic
        performanceMonitor.endNavigation("users/detail/$username")
    }
}
val config = NavigationPerformanceConfig().apply {
    slowNavigationThreshold = 1000L // 1 second
    slowDeepLinkResolutionThreshold = 500L // 500ms
    slowFeatureApiCallThreshold = 200L // 200ms
}
val isPerformanceMonitoringEnabled: Boolean
    get() = BuildConfig.DEBUG
@Composable
fun DebugScreen() {
    NavigationPerformanceDashboard()
}
// Export performance report
val report = performanceMonitor.getPerformanceReport()
// Save to file, send to analytics, etc.
class CustomPerformanceMonitor @Inject constructor(
    private val delegate: NavigationPerformanceMonitor
) {
    fun trackCustomMetric(metricName: String, value: Long) {
        // Custom metric tracking logic
    }
}
```

## Conclusion

The navigation performance monitoring system provides comprehensive tracking and analysis of navigation performance metrics. By using interceptors, configuration, and the dashboard, developers can identify and resolve performance issues in the navigation system.

For more information, see:
- [Navigation 3 Architecture](NAVIGATION3_ARCHITECTURE.md)
- [Distributed Destinations Guide](DISTRIBUTED_DESTINATIONS_GUIDE.md)
- [Migration Guide](DISTRIBUTED_DESTINATIONS_MIGRATION.md)
