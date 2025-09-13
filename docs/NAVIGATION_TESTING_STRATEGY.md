# Navigation 3 Testing Strategy

## 🎯 Overview

This document outlines the comprehensive testing strategy for the Navigation 3 performance optimizations, including unit tests, integration tests, performance tests, and regression testing. The strategy ensures that performance optimizations work correctly and don't introduce regressions.

## 🧪 Testing Categories

### 1. **Unit Tests**

#### Navigation3FeatureRegistry Tests

```kotlin
@ExtendWith(MockKExtension::class)
class Navigation3FeatureRegistryTest {
    
    @MockK
    private lateinit var mockProvider: FeatureDestinationProvider
    
    // Note: NavigationPerformanceMonitor removed as it's not needed
    // Navigation 3 is designed to be performant by default
    
    private lateinit var registry: Navigation3FeatureRegistry
    
    @BeforeEach
    fun setup() {
        registry = Navigation3FeatureRegistry(
            providers = setOf(mockProvider),
            performanceMonitor = mockPerformanceMonitor
        )
    }
    
    @Test
    fun `provider resolution should cache providers after first lookup`() {
        // Given
        val testKey = UserList
        every { mockProvider.canResolve(testKey) } returns true
        every { mockProvider.createEntry(testKey) } returns mockEntry()
        
        // When - First resolution
        val entry1 = registry.createEntryProvider().invoke(testKey)
        
        // When - Second resolution (should use cache)
        val entry2 = registry.createEntryProvider().invoke(testKey)
        
        // Then
        verify(exactly = 1) { mockProvider.canResolve(testKey) }
        verify(exactly = 1) { mockProvider.createEntry(testKey) }
    }
    
    @Test
    fun `entry pooling should reuse entries for poolable destinations`() {
        // Given
        val testKey = UserList // Poolable destination
        every { mockProvider.canResolve(testKey) } returns true
        every { mockProvider.createEntry(testKey) } returns mockEntry()
        
        // When
        val entry1 = registry.createEntryProvider().invoke(testKey)
        val entry2 = registry.createEntryProvider().invoke(testKey)
        
        // Then
        verify(exactly = 1) { mockProvider.createEntry(testKey) }
        assertThat(entry1).isSameAs(entry2)
    }
    
    @Test
    fun `cache eviction should remove oldest entries when cache is full`() {
        // Given
        val maxCacheSize = 2
        val registry = Navigation3FeatureRegistry(
            providers = setOf(mockProvider),
            performanceMonitor = mockPerformanceMonitor,
            maxProviderCacheSize = maxCacheSize
        )
        
        // When - Fill cache beyond limit
        repeat(maxCacheSize + 1) { index ->
            val key = createTestKey("key$index")
            every { mockProvider.canResolve(key) } returns true
            every { mockProvider.createEntry(key) } returns mockEntry()
            registry.createEntryProvider().invoke(key)
        }
        
        // Then
        val metrics = registry.getPerformanceMetrics()
        assertThat(metrics["cacheEvictions"] as Int).isGreaterThan(0)
    }
}
```

#### Navigation Performance Tests (Removed)

```kotlin
// Note: NavigationPerformanceMonitor tests removed as the class is no longer needed
// Navigation 3 is designed to be performant by default
class NavigationPerformanceTest {
    
    // Performance monitoring is not needed for Navigation 3
    // The framework handles performance optimization internally
    
    @Test
    fun `should record provider resolution metrics correctly`() {
        // Given
        val testKey = UserList
        val duration = 1_500_000L // 1.5ms
        val isCacheHit = true
        
        // When
        monitor.recordProviderResolution(testKey, duration, isCacheHit)
        
        // Then
        val metrics = monitor.performanceMetrics.value
        assertThat(metrics.totalResolutions).isEqualTo(1)
        assertThat(metrics.cacheHits).isEqualTo(1)
        assertThat(metrics.averageResolutionTime).isEqualTo(duration.toDouble())
    }
    
    @Test
    fun `should identify slow resolutions correctly`() {
        // Given
        val slowDuration = 2_000_000L // 2ms (above 1ms threshold)
        
        // When
        monitor.recordProviderResolution(UserList, slowDuration, false)
        
        // Then
        val metrics = monitor.performanceMetrics.value
        assertThat(metrics.slowResolutions).isEqualTo(1)
    }
    
    @Test
    fun `should calculate cache hit rate correctly`() {
        // Given
        monitor.recordProviderResolution(UserList, 1000L, true)  // Cache hit
        monitor.recordProviderResolution(UserDetail("test"), 2000L, false) // Cache miss
        
        // When
        val summary = monitor.getPerformanceSummary()
        
        // Then
        assertThat(summary).contains("Cache Hits: 1 (50.00%)")
    }
}
```

### 2. **Performance Tests**

#### Provider Resolution Performance Tests

```kotlin
class NavigationPerformanceTest {
    
    @Test
    fun `provider resolution should be under 2ms for cached providers`() {
        // Given
        val registry = createTestRegistry()
        val testKey = UserList
        
        // Warm up cache
        registry.createEntryProvider().invoke(testKey)
        
        // When
        val startTime = System.nanoTime()
        val entry = registry.createEntryProvider().invoke(testKey)
        val duration = System.nanoTime() - startTime
        
        // Then
        assertThat(duration).isLessThan(2_000_000) // <2ms
        assertThat(entry).isNotNull()
    }
    
    @Test
    fun `memory usage should be bounded with cache limits`() {
        // Given
        val registry = createTestRegistry()
        val initialMemory = getMemoryUsage()
        
        // When - Perform many navigation operations
        repeat(1000) {
            val key = createTestKey("key$it")
            registry.createEntryProvider().invoke(key)
        }
        
        // Then
        val finalMemory = getMemoryUsage()
        val memoryIncrease = finalMemory - initialMemory
        
        assertThat(memoryIncrease).isLessThan(200_000) // <200KB
    }
    
    @Test
    fun `cache hit rate should be above 80% for repeated navigation`() {
        // Given
        val registry = createTestRegistry()
        val testKeys = listOf(UserList, UserDetail("test"), Settings)
        
        // When - Navigate to same destinations multiple times
        repeat(100) {
            testKeys.forEach { key ->
                registry.createEntryProvider().invoke(key)
            }
        }
        
        // Then
        val metrics = registry.getPerformanceMetrics()
        val cacheHitRate = metrics["cacheHitRate"] as String
        val hitRate = cacheHitRate.replace("%", "").toDouble()
        
        assertThat(hitRate).isGreaterThan(80.0)
    }
}
```

#### Build Time Performance Tests

```kotlin
class BuildTimePerformanceTest {
    
    @Test
    fun `build time should be under 60 seconds`() {
        // Given
        val startTime = System.currentTimeMillis()
        
        // When
        val result = runGradleTask(":app:assembleDebug")
        
        // Then
        val buildTime = System.currentTimeMillis() - startTime
        assertThat(buildTime).isLessThan(60_000) // <60s
        assertThat(result.exitCode).isEqualTo(0)
    }
    
    @Test
    fun `incremental build should be significantly faster`() {
        // Given
        val fullBuildTime = measureBuildTime(":app:assembleDebug")
        
        // When - Make small change and rebuild
        makeSmallChange()
        val incrementalBuildTime = measureBuildTime(":app:assembleDebug")
        
        // Then
        val improvement = (fullBuildTime - incrementalBuildTime).toDouble() / fullBuildTime
        assertThat(improvement).isGreaterThan(0.5) // >50% improvement
    }
}
```

### 3. **Integration Tests**

#### End-to-End Navigation Tests

```kotlin
@HiltAndroidTest
class NavigationIntegrationTest {
    
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Test
    fun `navigation between features should work correctly`() {
        // Given
        composeTestRule.setContent {
            GithubUsersTheme {
                MainNavigation()
            }
        }
        
        // When - Navigate to user list
        composeTestRule.onNodeWithText("GitHub Users").performClick()
        
        // Then
        composeTestRule.onNodeWithText("mojombo").assertIsDisplayed()
        
        // When - Navigate to user detail
        composeTestRule.onNodeWithText("mojombo").performClick()
        
        // Then
        composeTestRule.onNodeWithText("User Details").assertIsDisplayed()
    }
    
    @Test
    fun `deep link navigation should work correctly`() {
        // Given
        val deepLink = "app://users/user/mojombo"
        
        // When
        composeTestRule.activityRule.scenario.onActivity { activity ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink))
            activity.startActivity(intent)
        }
        
        // Then
        composeTestRule.onNodeWithText("User Details").assertIsDisplayed()
        composeTestRule.onNodeWithText("mojombo").assertIsDisplayed()
    }
    
    @Test
    fun `error handling should show user-friendly messages`() {
        // Given
        val invalidDeepLink = "app://invalid/destination"
        
        // When
        composeTestRule.activityRule.scenario.onActivity { activity ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(invalidDeepLink))
            activity.startActivity(intent)
        }
        
        // Then
        composeTestRule.onNodeWithText("Page Not Found").assertIsDisplayed()
        composeTestRule.onNodeWithText("Go Back").assertIsDisplayed()
    }
}
```

### 4. **Regression Tests**

#### Performance Regression Tests

```kotlin
class PerformanceRegressionTest {
    
    @Test
    fun `provider resolution should not regress in performance`() {
        // Given - Baseline performance
        val baselineRegistry = createBaselineRegistry()
        val baselineTime = measureProviderResolutionTime(baselineRegistry)
        
        // When - Current implementation
        val currentRegistry = createCurrentRegistry()
        val currentTime = measureProviderResolutionTime(currentRegistry)
        
        // Then - Should not be more than 10% slower
        val regression = (currentTime - baselineTime).toDouble() / baselineTime
        assertThat(regression).isLessThan(0.1) // <10% regression
    }
    
    @Test
    fun `memory usage should not increase significantly`() {
        // Given - Baseline memory usage
        val baselineMemory = measureBaselineMemoryUsage()
        
        // When - Current implementation
        val currentMemory = measureCurrentMemoryUsage()
        
        // Then - Should not increase by more than 20%
        val increase = (currentMemory - baselineMemory).toDouble() / baselineMemory
        assertThat(increase).isLessThan(0.2) // <20% increase
    }
    
    @Test
    fun `build time should not regress significantly`() {
        // Given - Baseline build time
        val baselineBuildTime = measureBaselineBuildTime()
        
        // When - Current build time
        val currentBuildTime = measureCurrentBuildTime()
        
        // Then - Should not increase by more than 30%
        val regression = (currentBuildTime - baselineBuildTime).toDouble() / baselineBuildTime
        assertThat(regression).isLessThan(0.3) // <30% regression
    }
}
```

## 🔧 Test Utilities

### Performance Measurement Utilities

```kotlin
object PerformanceTestUtils {
    
    fun measureProviderResolutionTime(registry: Navigation3FeatureRegistry): Long {
        val testKey = UserList
        val iterations = 1000
        
        // Warm up
        registry.createEntryProvider().invoke(testKey)
        
        val startTime = System.nanoTime()
        repeat(iterations) {
            registry.createEntryProvider().invoke(testKey)
        }
        val endTime = System.nanoTime()
        
        return (endTime - startTime) / iterations
    }
    
    fun measureMemoryUsage(): Long {
        val runtime = Runtime.getRuntime()
        runtime.gc()
        return runtime.totalMemory() - runtime.freeMemory()
    }
    
    fun measureBuildTime(task: String): Long {
        val startTime = System.currentTimeMillis()
        runGradleTask(task)
        return System.currentTimeMillis() - startTime
    }
    
    fun getMemoryUsage(): Long {
        val runtime = Runtime.getRuntime()
        return runtime.totalMemory() - runtime.freeMemory()
    }
}
```

### Test Data Builders

```kotlin
object NavigationTestDataBuilder {
    
    fun createTestRegistry(
        providers: Set<FeatureDestinationProvider> = setOf(),
        maxProviderCacheSize: Int = 50,
        maxEntryPoolSize: Int = 100
    ): Navigation3FeatureRegistry {
        // Note: NavigationPerformanceMonitor removed as it's not needed
        // Navigation 3 is designed to be performant by default
        
        return Navigation3FeatureRegistry(providers, mockMonitor)
    }
    
    fun createTestKey(name: String): NavKey {
        return object : NavKey {
            override fun toString(): String = name
        }
    }
    
    fun mockEntry(): NavEntry<NavKey> {
        return NavEntry(createTestKey("test")) {
            Text("Test Screen")
        }
    }
}
```

## 📊 Test Coverage Requirements

### Unit Test Coverage
- **Navigation3FeatureRegistry**: 95%+ coverage
- **Navigation Performance**: Not needed - Navigation 3 is performant by default
- **Error handling**: 100% coverage
- **Cache eviction logic**: 100% coverage

### Integration Test Coverage
- **End-to-end navigation flows**: 80%+ coverage
- **Deep link handling**: 100% coverage
- **Error scenarios**: 100% coverage
- **Performance monitoring**: 100% coverage

### Performance Test Coverage
- **Provider resolution**: <2ms target
- **Memory usage**: <200KB target
- **Build time**: <60s target
- **Cache hit rate**: >80% target

## 🚀 Continuous Integration

### GitHub Actions Workflow

```yaml
name: Navigation Performance Tests

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  performance-tests:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Run unit tests
      run: ./gradlew test
    
    - name: Run performance tests
      run: ./gradlew :app:testPerformance
    
    - name: Run integration tests
      run: ./gradlew :app:connectedAndroidTest
    
    - name: Measure build time
      run: |
        start_time=$(date +%s)
        ./gradlew :app:assembleDebug
        end_time=$(date +%s)
        build_time=$((end_time - start_time))
        echo "Build time: ${build_time}s"
        if [ $build_time -gt 60 ]; then
          echo "Build time exceeded 60s threshold"
          exit 1
        fi
    
    - name: Performance regression check
      run: ./gradlew :app:performanceRegressionTest
```

## 📈 Performance Monitoring

### Test Metrics Dashboard

```kotlin
class PerformanceTestDashboard {
    
    fun generateTestReport(): TestReport {
        return TestReport(
            providerResolutionTime = measureProviderResolutionTime(),
            memoryUsage = measureMemoryUsage(),
            buildTime = measureBuildTime(),
            cacheHitRate = measureCacheHitRate(),
            testCoverage = calculateTestCoverage(),
            regressionStatus = checkRegressionStatus()
        )
    }
    
    data class TestReport(
        val providerResolutionTime: Long,
        val memoryUsage: Long,
        val buildTime: Long,
        val cacheHitRate: Double,
        val testCoverage: Double,
        val regressionStatus: RegressionStatus
    )
    
    enum class RegressionStatus {
        PASSED,
        MINOR_REGRESSION,
        MAJOR_REGRESSION
    }
}
```

## 🎯 Conclusion

This comprehensive testing strategy ensures:

- **Performance optimizations work correctly**
- **No regressions are introduced**
- **Memory usage remains bounded**
- **Build times stay within acceptable limits**
- **Navigation functionality remains intact**

The testing strategy includes unit tests, integration tests, performance tests, and regression tests, providing comprehensive coverage of the Navigation 3 performance optimizations.

