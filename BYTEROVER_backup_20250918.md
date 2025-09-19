# Byterover Handbook: githubusers

## Layer 1: System Overview

### Project Purpose

This is a modern Android application demonstrating GitHub user discovery and profile viewing. The app showcases feature-based modular architecture with custom Navigation 3 implementation, offline-first data strategy, and clean architecture principles. Users can search for GitHub users, view profiles, explore repositories, and access detailed user information with full offline support.

### Architecture

The application follows **Clean Architecture** principles with **MVI (Model-View-Intent)** pattern and **feature-based modular design**:

*   **Clean Architecture**: Strict separation of concerns with Presentation, Domain, and Data layers
*   **MVI Pattern**: Unidirectional data flow with Intent processors, State management, and Effect handling
*   **Feature-Based Modules**: Complete isolation of features with deep link communication only
*   **Composite Builds**: Each module as separate Gradle build for scalability and parallel development
*   **Single Activity Architecture**: Navigation 3 with type-safe destinations and deep linking
*   **Offline-First**: Room database with RemoteMediator for intelligent caching strategy

### Key Technologies

*   **Programming Language**: Kotlin with Coroutines for async programming
*   **Build System**: Gradle with composite builds and convention plugins
*   **UI Framework**: Jetpack Compose with Material Design 3
*   **Architecture Pattern**: MVI with StateDelegate and EffectDelegate
*   **Dependency Injection**: Hilt for modular dependency management
*   **Navigation**: Custom Navigation 3 with type-safe destinations
*   **Networking**: Ktor HTTP client with Kotlin Serialization
*   **Database**: Room with offline-first caching via RemoteMediator
*   **Image Loading**: Coil with Compose integration
*   **Pagination**: Jetpack Paging 3 with offline support
*   **Testing**: JUnit, MockK, Compose UI Testing, AndroidX Test

## Layer 2: Module Map

### Application Module

#### app
**Purpose**: Orchestrator and host for single‑activity Compose UI and Navigation 3
```kotlin
// Key Responsibilities
- @HiltAndroidApp application setup
- Single Activity host and Nav3 back stack wiring
- Deep link entry and dispatch to feature modules
- App‑wide Material 3 theme
```

### Core Modules

#### core-security
**Purpose**: Security, cryptography, and sensitive data handling utilities
```kotlin
// Key Components
- Token encryption/decryption helpers
- EncryptedSharedPreferences/DataStore wrappers
- Certificate pinning configuration helpers
- Input validation and sanitization utilities
```

#### core-mvi
**Purpose**: MVI pattern implementation and base classes
```kotlin
// Key Components
- MviViewModel<S, I, E>: Base ViewModel with MVI contract
- StateDelegate: Manages ViewState with StateFlow
- EffectDelegate: Handles one-time effects via Channel
- IntentProcessor: Processes user intents into state changes
```

#### core-networking  
**Purpose**: HTTP client configuration and network abstraction
```kotlin
// Key Components
- HttpClientProvider: Configured Ktor client with JSON serialization
- BaseApiService: Common HTTP operations for features
- NetworkModule: Hilt module for networking dependencies
```

#### core-storage
**Purpose**: Local storage and database abstractions  
```kotlin
// Key Components
- RoomDatabaseProvider: Room database configuration
- DataStoreProvider: Preferences and app settings storage
- StorageModule: Hilt module for storage dependencies
```

#### core-ui
**Purpose**: Shared Compose components and design system
```kotlin
// Key Components
- Common Compose components and UI utilities
- Material Design 3 theme and styling
- Shared UI patterns and reusable elements
```

#### core-common
**Purpose**: Cross‑cutting utilities and shared types used across modules
```kotlin
// Key Components
- Kotlin extensions and result helpers
- Common models and constants
- Error handling and tracing utilities
```

### Feature Modules

#### feature-auth
**Purpose**: Authentication flows (sign-in, sign-out) and token management
```kotlin
// Layers
- Data: AuthApiService, TokenStorage
- Domain: SignInUseCase, SignOutUseCase
- Presentation: AuthViewModel, AuthScreen(s)
```

#### feature-users
**Purpose**: User profiles and repository viewing
```kotlin
// Data Layer
- UserListApiService: GitHub Users API integration  
- UserDetailApiService: Individual user details and repos
- UserListDatabase: Room database for offline caching
- UserListRemoteMediator: Paging 3 offline-first strategy

// Domain Layer
- GetUsersUseCase: Business logic for user retrieval
- GetUserDetailUseCase: User profile and repository logic
- UserRepository: Data access abstraction

// Presentation Layer
- UserListViewModel: MVI ViewModel for user lists
- UserDetailViewModel: MVI ViewModel for user details
- UserListScreen: Compose UI with paging and search
- UserDetailScreen: User profile with repository list
```

#### feature-search
**Purpose**: GitHub user search with real-time results
```kotlin
// Data Layer  
- SearchApiService: GitHub Search API integration
- SearchPagingSource: Paging source for search results

// Domain Layer
- SearchUsersUseCase: Search business logic

// Presentation Layer
- SearchScreen: Compose search UI with suggestions
- SearchViewModel: MVI pattern for search state management
```

#### feature-settings
**Purpose**: App preferences and configuration
```kotlin
// Data Layer
- SettingsRepository: DataStore for app preferences

// Presentation Layer  
- SettingsScreen: Material 3 settings UI
- SettingsViewModel: MVI pattern for settings
```

### Navigation System

#### navigation-api
**Purpose**: Navigation contracts and feature integration
```kotlin
// Key Components
- FeatureDeepLinkHandler: Feature-specific deep link processing
- FeatureDestinationProvider: Feature navigation contracts  
- BackStackStore: Navigation state storage interface
- LocalNavigateBack: Compose local for back navigation
- LocalNavigateToDeepLink: Compose local for deep link navigation
```

#### navigation-impl
**Purpose**: Navigation 3 runtime implementation
```kotlin
// Key Components
- Navigation3FeatureRegistry: Feature handler registration
- DeepLinkDispatcher: Multi-module deep link routing
- Navigation3BackStack: Back stack management
- DataStoreBackStackStore: Persistent navigation state
- NavigationRuntimeModule: Runtime dependency injection
```

### Supporting Modules

#### catalog
**Purpose**: Centralized dependency versions via `libs.versions.toml` (version catalog)

#### plugins
**Purpose**: Gradle convention plugins for consistent build configuration (Detekt, KtLint, Android, Hilt, Compose, testing)



#### testing
**Purpose**: Shared test utilities (Coroutine rules, MockWebServer helpers, fixtures)

## Layer 3: Integration Patterns

### Data Flow Architecture

#### Repository Pattern
```kotlin
// Implementation Pattern
interface UserRepository {
    fun getUsers(): Flow<PagingData<User>>
    suspend fun getUserDetail(username: String): UserDetail
}

class UserRepositoryImpl(
    private val remoteDataSource: UserRemoteDataSource,
    private val localDataSource: UserLocalDataSource,
    private val remoteMediator: UserListRemoteMediator
) : UserRepository
```

#### MVI Pattern Integration
```kotlin
// Intent Processing
sealed interface UserListIntent : ViewIntent {
    data object LoadUsers : UserListIntent
    data class SearchUsers(val query: String) : UserListIntent
    data class SelectUser(val user: User) : UserListIntent
}

// State Management  
data class UserListState(
    val users: PagingData<User> = PagingData.empty(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
) : ViewState

// Effect Handling
sealed interface UserListEffect : ViewEffect {
    data class NavigateToDetail(val username: String) : UserListEffect
    data class ShowError(val message: String) : UserListEffect
}
```

#### Offline-First Strategy
```kotlin
// RemoteMediator Pattern
class UserListRemoteMediator(
    private val apiService: UserListApiService,
    private val database: UserListDatabase
) : RemoteMediator<Int, UserEntity>() {
    
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UserEntity>
    ): MediatorResult {
        // Network fetch -> Database cache -> UI update
    }
}
```

### Deep Link Architecture

#### Multi-Module Deep Linking
```kotlin
// Feature Registration Pattern  
@Singleton
class UsersFeatureDeepLinkHandler @Inject constructor() : FeatureDeepLinkHandler {
    override fun handles(deepLink: String): Boolean = 
        deepLink.startsWith("app://users")
    
    override fun createDestination(deepLink: String): String? = when {
        deepLink.startsWith("app://users/detail/") -> 
            deepLink.substringAfter("app://users/detail/")
        else -> null
    }
}

// Feature Destination Provider
@Singleton  
class UsersFeatureDestinationProvider @Inject constructor() : FeatureDestinationProvider {
    override fun getDestination(route: String): (@Composable () -> Unit)? = when (route) {
        "users_list" -> { { UserListRoute() } }
        else -> null
    }
}

// Cross-Module Navigation using Compose Locals
val navigateToDeepLink = LocalNavigateToDeepLink.current
navigateToDeepLink("app://users/detail/mojombo")
navigateToDeepLink("app://search?q=android")
```

### Testing Infrastructure

#### Test Pyramid Strategy  
**Implementation**: TestingConventionPlugin with 70/20/10 distribution
```kotlin
// Testing Convention Plugin Configuration
class TestingConventionPlugin : Plugin<Project> {
    // Unit Tests (70%) - Fast, isolated, numerous
    // Integration Tests (20%) - API/Database integration  
    // UI/E2E Tests (10%) - User journey testing
}

// Test Utilities
object TestUtils {
    fun runTestWithDispatcher(testBody: suspend () -> Unit)
    fun assertExecutionTimeUnder(maxTime: Long, block: () -> Unit)
    fun measureMemoryUsage(block: () -> Unit): MemoryStats
}

// MockWebServer Integration
object MockWebServerUtils {
    fun createMockWebServer(): MockWebServer
    fun createJsonResponse(json: String): MockResponse
    fun createDelayedResponse(delay: Long): MockResponse
}
```

#### Code Coverage with JaCoCo
```kotlin
// Coverage Configuration (80% minimum threshold)
tasks.register("jacocoTestReport") {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    
    violationRules {
        rule {
            limit { minimum = "0.80".toBigDecimal() }
        }
    }
}
```

#### Repository Testing
```kotlin
// Unit Test Pattern
@Test
fun `getUsers returns paged data from repository`() = runTest {
    // Given
    val mockUsers = listOf(createMockUser())
    coEvery { remoteDataSource.getUsers(any()) } returns mockUsers
    
    // When  
    val result = repository.getUsers().first()
    
    // Then
    assertThat(result).isEqualTo(expectedPagingData)
}
```

#### API Integration Testing
```kotlin
// MockWebServer Integration Test
@Test
fun `API service handles rate limiting correctly`() = runTest {
    // Given
    mockWebServer.enqueue(
        MockWebServerUtils.createErrorResponse(
            httpCode = HttpURLConnection.HTTP_FORBIDDEN,
            message = "API rate limit exceeded"
        )
    )
    
    // When & Then
    assertThrows<ApiException> {
        apiService.getUsers(since = 0, perPage = 30)
    }
}
```

#### Compose UI Testing
```kotlin
// UI Test Pattern
@Test
fun userListScreen_displaysUsers() {
    composeTestRule.setContent {
        UserListScreen(
            state = UserListState(users = mockPagingData),
            onIntent = {}
        )
    }
    
    composeTestRule.onNodeWithText("mojombo").assertIsDisplayed()
}
```

## Layer 4: Extension Points

### Adding New Features

#### Feature Module Template
```kotlin
// Required Structure
feature-[name]/
├── src/main/java/com/example/githubusers/feature/[name]/
│   ├── data/                    # Data layer
│   │   ├── api/                # API services
│   │   ├── repository/         # Repository implementations  
│   │   └── local/              # Room database components
│   ├── domain/                 # Domain layer
│   │   ├── repository/         # Repository interfaces
│   │   ├── usecase/           # Business logic use cases
│   │   └── entity/            # Domain models
│   ├── presentation/          # Presentation layer
│   │   ├── ui/                # Compose screens
│   │   ├── viewmodel/         # MVI ViewModels
│   │   └── navigation/        # Navigation components
│   └── di/                    # Hilt modules
```

#### Deep Link Registration
```kotlin
// 1. Create deep link handler
@Singleton
class NewFeatureDeepLinkHandler @Inject constructor() : FeatureDeepLinkHandler {
    override fun handles(deepLink: String) = deepLink.startsWith("app://newfeature")
    override fun createDestination(deepLink: String): String? = when {
        deepLink.startsWith("app://newfeature/detail/") -> 
            deepLink.substringAfter("app://newfeature/detail/")
        else -> null
    }
}

// 2. Create destination provider  
@Singleton
class NewFeatureDestinationProvider @Inject constructor() : FeatureDestinationProvider {
    override fun getDestination(route: String): (@Composable () -> Unit)? = when (route) {
        "newfeature_main" -> { { NewFeatureRoute() } }
        else -> null
    }
}

// 3. Register in feature's Nav3Bindings module
@Module
@InstallIn(SingletonComponent::class)
abstract class NewFeatureNav3Bindings {
    @Binds
    @IntoSet
    abstract fun bindHandler(handler: NewFeatureDeepLinkHandler): FeatureDeepLinkHandler
    
    @Binds
    @IntoSet  
    abstract fun bindProvider(provider: NewFeatureDestinationProvider): FeatureDestinationProvider
}
```

### API Integration

#### GitHub API Extension
```kotlin
// Add new API service
@Singleton
class NewApiService @Inject constructor(
    private val httpClient: HttpClient
) : BaseApiService(httpClient) {
    
    suspend fun getNewData(param: String): NewDataResponse = 
        get("https://api.github.com/new-endpoint/$param")
}

// Register in NetworkModule
@Provides
@Singleton
fun provideNewApiService(httpClient: HttpClient): NewApiService = 
    NewApiService(httpClient)
```

### Database Extensions

#### Adding New Entities
```kotlin
// 1. Create entity
@Entity(tableName = "new_data")
data class NewDataEntity(
    @PrimaryId val id: String,
    val name: String,
    val description: String?
)

// 2. Create DAO
@Dao
interface NewDataDao {
    @Query("SELECT * FROM new_data")
    fun getAll(): Flow<List<NewDataEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: List<NewDataEntity>)
}

// 3. Update database
@Database(
    entities = [NewDataEntity::class], 
    version = 2
)
abstract class FeatureDatabase : RoomDatabase() {
    abstract fun newDataDao(): NewDataDao
}
```

### Build System Extensions

#### Convention Plugin Usage
```kotlin
// build.gradle.kts
plugins {
    id("githubusers.android.feature")  // Applies standard feature configuration
    id("githubusers.android.compose")  // Adds Compose dependencies  
    id("githubusers.android.hilt")     // Configures Hilt DI
}

// Custom dependencies
dependencies {
    implementation(projects.coreNetworking)
    implementation(projects.coreStorage) 
    implementation(projects.navigationApi)
    
    // Feature-specific dependencies
    implementation(libs.androidx.paging.compose)
    implementation(libs.coil.compose)
}
```

### Configuration

#### Development vs Production
```kotlin
// HttpClientProvider configuration
fun createHttpClient(): HttpClient = HttpClient(Android) {
    install(Logging) {
        level = if (BuildConfig.DEBUG) LogLevel.BODY else LogLevel.NONE
    }
    
    install(ContentNegotiation) {
        json(Json { 
            ignoreUnknownKeys = true
            isLenient = !BuildConfig.DEBUG  // Strict in debug
        })
    }
}
```

#### Build Variants
```kotlin
// app/build.gradle.kts
android {
    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            buildConfigField("String", "API_BASE_URL", "\"https://api.github.com\"")
        }
        release {
            buildConfigField("String", "API_BASE_URL", "\"https://api.github.com\"")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }
}
```

---

**Generated**: 2025-09-11 | **Architecture**: Clean + MVI + Modular | **Navigation**: Type-Safe Navigation 3

## Byterover Integration

Byterover MCP integrates with the project as a shared long-term memory layer for AI coding agents. To set up non-disruptively:

1. Install the ByteRover extension in your IDE (Cursor, Zed, VS Code, etc.) from the marketplace.
2. Create an access token at https://byterover.dev.
3. Configure the MCP server in your IDE settings to point to the Byterover server.
4. Use tools like `byterover-retrieve-knowledge` and `byterover-store-knowledge` for memory management in AI workflows.

Reference: GitHub repo https://github.com/campfirein/cipher, NPM package @byterover/cipher.

For AI agent workflows, Byterover captures interactions, reasoning, and feedback to build persistent memory for coding tasks.