# Developer Guide

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Hedgehog (2023.1.1) or later
- **JDK**: Version 21
- **Android SDK**: API level 34
- **Gradle**: Version 8.13+

### Initial Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd githubusers
   ```

2. **Open in Android Studio**
   - Open the project in Android Studio
   - Wait for Gradle sync to complete
   - Ensure all dependencies are resolved

3. **Verify setup**
   ```bash
   ./gradlew build
   ```

## 🏗️ Project Architecture

### Feature-Based Modular Design

The project follows a feature-based modular architecture where each feature is completely isolated:

```
githubusers/
├── app/                    # Main application
├── core-*/                 # Core modules
├── feature-*/              # Feature modules
├── navigation-*/           # Navigation system
├── plugins/                # Build plugins
└── catalog/                # Version catalog
```

### Core Principles

1. **Feature Isolation**: Each feature module is self-contained
2. **Deep Link Communication**: Cross-module communication via deep links only
3. **Type Safety**: Compile-time navigation safety
4. **Clean Architecture**: Clear separation of concerns
5. **Navigation 3 Complete**: All Navigation 3 features implemented and verified

## 🛠️ Development Workflow

### 1. Creating a New Feature

#### Step 1: Create Feature Module
```bash
# Create new feature module directory
mkdir feature-your-feature
cd feature-your-feature

# Create basic structure
mkdir -p src/main/java/com/example/githubusers/feature/yourfeature
mkdir -p src/test/java/com/example/githubusers/feature/yourfeature
```

#### Step 2: Define Build Configuration
```kotlin
// build.gradle.kts
plugins {
    id("githubusers.android.library")
    id("githubusers.android.hilt")
    id("githubusers.android.compose")
}

dependencies {
    implementation(project(":core-common"))
    implementation(project(":core-ui"))
    implementation(project(":navigation-api"))
}
```

#### Step 3: Implement Feature Structure
```
feature-your-feature/
├── src/main/java/com/example/githubusers/feature/yourfeature/
│   ├── data/               # Data layer
│   ├── domain/             # Business logic
│   ├── presentation/       # UI and ViewModels
│   └── navigation/         # Navigation and deep links
└── build.gradle.kts
```

### 2. Adding Navigation

#### Define Destinations
```kotlin
// navigation/YourFeatureDestination.kt
@Serializable
data class YourFeatureDestination(
    val param: String
) : AppDestination
```

#### Implement Deep Link Handler
```kotlin
// navigation/YourFeatureDeepLinkHandler.kt
@Singleton
class YourFeatureDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    override fun canHandle(uri: String): Boolean {
        return uri.startsWith("githubusers://your-feature")
    }
    
    override fun handle(uri: String): NavigationDestination? {
        // Parse URI and return destination
        return YourFeatureDestination(...)
    }
}
```

#### Register with Hilt
```kotlin
// di/YourFeatureNavigationModule.kt
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

### 3. Implementing UI with Compose

#### Create Screen Composable
```kotlin
// presentation/YourFeatureScreen.kt
@Composable
fun YourFeatureScreen(
    viewModel: YourFeatureViewModel = hiltViewModel(),
    navigationActions: Navigation3Actions
) {
    val uiState by viewModel.uiState.collectAsState()
    
    YourFeatureContent(
        uiState = uiState,
        onEvent = viewModel::handleEvent,
        navigationActions = navigationActions
    )
}
```

#### Implement ViewModel
```kotlin
// presentation/YourFeatureViewModel.kt
@HiltViewModel
class YourFeatureViewModel @Inject constructor(
    private val useCase: YourFeatureUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(YourFeatureUiState())
    val uiState: StateFlow<YourFeatureUiState> = _uiState.asStateFlow()
    
    fun handleEvent(event: YourFeatureEvent) {
        // Handle UI events
    }
}
```

### 4. Data Layer Implementation

#### Repository
```kotlin
// data/repository/YourFeatureRepository.kt
@Singleton
class YourFeatureRepository @Inject constructor(
    private val apiService: YourFeatureApiService,
    private val localDataSource: YourFeatureLocalDataSource
) {
    suspend fun getData(): Result<YourFeatureData> {
        return try {
            val data = apiService.getData()
            localDataSource.saveData(data)
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

#### API Service
```kotlin
// data/api/YourFeatureApiService.kt
interface YourFeatureApiService {
    @GET("your-endpoint")
    suspend fun getData(): YourFeatureData
}
```

## 🧪 Testing

### Unit Testing

#### ViewModel Testing
```kotlin
// test/presentation/YourFeatureViewModelTest.kt
@Test
fun `should update state when event is handled`() = runTest {
    // Given
    val viewModel = YourFeatureViewModel(useCase)
    
    // When
    viewModel.handleEvent(YourFeatureEvent.LoadData)
    
    // Then
    val state = viewModel.uiState.value
    assertTrue(state.isLoading)
}
```

#### Repository Testing
```kotlin
// test/data/repository/YourFeatureRepositoryTest.kt
@Test
fun `should return data from API`() = runTest {
    // Given
    val expectedData = YourFeatureData("test")
    coEvery { apiService.getData() } returns expectedData
    
    // When
    val result = repository.getData()
    
    // Then
    assertTrue(result.isSuccess)
    assertEquals(expectedData, result.getOrNull())
}
```

### UI Testing

#### Compose Testing
```kotlin
// androidTest/presentation/YourFeatureScreenTest.kt
@Test
fun shouldDisplayDataWhenLoaded() {
    composeTestRule.setContent {
        YourFeatureScreen(
            viewModel = mockViewModel,
            navigationActions = mockNavigationActions
        )
    }
    
    composeTestRule.onNodeWithText("Expected Text").assertIsDisplayed()
}
```

### Running Tests
```bash
# Run all tests
./gradlew test

# Run specific module tests
./gradlew :feature-your-feature:test

# Run UI tests
./gradlew connectedAndroidTest

# Generate coverage report
./gradlew jacocoTestReport
```

## 🔧 Build System

### Version Catalog

All dependencies are managed through the version catalog:

```toml
# catalog/gradle/libs.versions.toml
[versions]
compose-bom = "2024.12.01"
navigation3 = "1.0.0-alpha15"

[libraries]
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
androidx-navigation3-ui = { group = "androidx.navigation3", name = "navigation3-ui", version.ref = "navigation3" }
```

### Convention Plugins

Use convention plugins for consistent configuration:

```kotlin
// plugins/src/main/kotlin/githubusers.android.library.gradle.kts
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("githubusers.android.detekt")
    id("githubusers.android.ktlint")
}
```

### Module Configuration

```kotlin
// feature-your-feature/build.gradle.kts
plugins {
    id("githubusers.android.library")
    id("githubusers.android.hilt")
    id("githubusers.android.compose")
}

dependencies {
    implementation(project(":core-common"))
    implementation(project(":core-ui"))
    implementation(project(":navigation-api"))
    
    // External dependencies
    implementation(libs.androidx.compose.bom)
    implementation(libs.androidx.navigation3.ui)
}
```

## 📱 Navigation

### Type-Safe Navigation

Use `AppDestination` types for navigation:

```kotlin
// Navigate to user detail
navigationActions.navigateToUserDetail("octocat")

// Navigate with custom destination
val destination = AppDestination.YourFeature("param")
controller.navigate(destination)
```

### Deep Link Handling

Implement deep link handlers for external navigation:

```kotlin
@Singleton
class YourFeatureDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    override fun canHandle(uri: String): Boolean {
        return uri.startsWith("githubusers://your-feature")
    }
    
    override fun handle(uri: String): NavigationDestination? {
        return try {
            val parsed = AppDeepLinks.parse(uri)
            when (parsed) {
                is AppDestination.YourFeature -> parsed
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
}
```

## 🎨 UI Development

### Material Design 3

Use Material Design 3 components and theming:

```kotlin
@Composable
fun YourFeatureContent(
    uiState: YourFeatureUiState,
    onEvent: (YourFeatureEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Your Feature",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Button(
            onClick = { onEvent(YourFeatureEvent.Action) }
        ) {
            Text("Action")
        }
    }
}
```

### Theme Integration

Use the app's theme system:

```kotlin
@Composable
fun YourFeatureScreen() {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    
    // Use theme colors and typography
}
```

## 🔍 Code Quality

### Static Analysis

The project uses Detekt and KtLint for code quality:

```bash
# Run Detekt
./gradlew detekt

# Run KtLint
./gradlew ktlintCheck

# Fix KtLint issues
./gradlew ktlintFormat
```

### Code Standards

Follow these coding standards:

1. **Naming Conventions**
   - Use descriptive names
   - Follow Kotlin naming conventions
   - Use camelCase for variables and functions
   - Use PascalCase for classes and interfaces

2. **Function Design**
   - Keep functions small and focused
   - Use single responsibility principle
   - Prefer pure functions when possible
   - Use meaningful parameter names

3. **Class Design**
   - Follow SOLID principles
   - Use composition over inheritance
   - Keep classes focused and cohesive
   - Use interfaces for abstractions

### Documentation

Document your code with KDoc:

```kotlin
/**
 * Handles user authentication and session management.
 * 
 * @param username The user's username
 * @param password The user's password
 * @return Result containing the authentication token or error
 */
suspend fun authenticate(username: String, password: String): Result<String>
```

## 🚀 Performance

### Build Performance

Optimize build performance:

1. **Use Convention Plugins**: Centralize build logic
2. **Minimize Dependencies**: Only include necessary dependencies
3. **Use Implementation**: Prefer `implementation` over `api`
4. **Parallel Builds**: Enable parallel execution

### Runtime Performance

Optimize app performance:

1. **Lazy Loading**: Use `LazyColumn` for large lists
2. **State Management**: Minimize recomposition
3. **Memory Management**: Proper lifecycle management
4. **Image Loading**: Use efficient image loading libraries

## 🐛 Debugging

### Logging

Use structured logging:

```kotlin
Timber.tag("YourFeatureViewModel").d("Handling event: $event")
    // Handle event
```

### Debugging Tools

1. **Android Studio Debugger**: Set breakpoints and inspect variables
2. **Logcat**: View application logs
3. **Layout Inspector**: Inspect UI hierarchy
4. **Network Inspector**: Monitor network requests

## 📚 Resources

### Documentation
- [Android Developer Guide](https://developer.android.com/guide)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Navigation 3](https://developer.android.com/guide/navigation/navigation3)
- [Hilt](https://dagger.dev/hilt/)

### Project-Specific
- [Navigation Architecture](NAVIGATION_ARCHITECTURE.md)
- [Feature Development Guide](FEATURE_BASED_DEVELOPMENT_GUIDE.md)
- [Build System Guide](BUILD_SYSTEM.md)

### Best Practices
- [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [Material Design](https://material.io/design)
- [Android Architecture Guide](https://developer.android.com/topic/architecture)

## 🤝 Contributing

### Pull Request Process

1. **Create Feature Branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Implement Changes**
   - Follow project patterns
   - Add tests
   - Update documentation

3. **Quality Checks**
   ```bash
   ./gradlew build
   ./gradlew test
   ./gradlew detekt
   ./gradlew ktlintCheck
   ```

4. **Submit Pull Request**
   - Clear description
   - Link to issues
   - Include screenshots if UI changes

### Code Review

Review checklist:
- [ ] Code follows project patterns
- [ ] Tests are comprehensive
- [ ] Documentation is updated
- [ ] Build passes
- [ ] No breaking changes
- [ ] Performance impact considered

---

This guide provides the foundation for effective development on the GitHub Users project. Follow these patterns and practices to maintain code quality and consistency across the project.
