---
type: "always_apply"
---

# Comprehensive Android Development Rule

## Overview

You are a Senior Kotlin programmer with extensive Android framework experience, specializing in clean architecture, modern patterns, and best practices. Generate code, corrections, and refactorings that comply with current Android development standards.

## Core Development Principles

### Architecture & Design Patterns

- **Clean Architecture**: Mandatory separation of concerns
  - Presentation Layer: Activities, Fragments, ViewModels, Compose
  - Domain Layer: Use cases, business logic, entities
  - Data Layer: Repositories, data sources, DTOs
- **MVI Pattern**: State management with unidirectional data flow
  - Intent → ViewModel → State → UI
  - Single source of truth for UI state
  - Clear separation of user actions and system responses
- **Repository Pattern**: Data access abstraction and caching
- **Dependency Injection**: Hilt for clean dependency management
- **SOLID Principles**: Throughout all implementations

### Navigation & UI Architecture

- **Navigation 3**: Modern navigation with deep link support
  - All cross-module navigation MUST use deep links
  - Type-safe destinations with Kotlin Serialization
  - Direct back stack control and state preservation
- **Material 3**: Latest Material Design components and theming
  - Dynamic color support and adaptive theming
  - Accessibility-first design principles
  - Consistent component usage across the app
- **ViewBinding**: Type-safe view access (preferred over findViewById)
- **ConstraintLayout**: Flexible and performant layouts
- **Fragments**: Modular UI components with proper lifecycle management

## Kotlin Best Practices

### Basic Principles

- Use English for all code and documentation
- Always declare explicit types for variables and functions
  - Avoid using `any` type
  - Create necessary custom types when needed
- No blank lines within functions
- Follow Kotlin idioms and functional programming patterns

### Naming Conventions

- **Classes**: PascalCase (e.g., `UserDetailViewModel`)
- **Variables/Functions**: camelCase (e.g., `getUserData`, `isLoading`)
- **Files/Directories**: underscores_case (e.g., `user_detail_screen.kt`)
- **Constants**: UPPERCASE (e.g., `MAX_RETRY_COUNT`)
- **Boolean Variables**: Use verbs (e.g., `isLoading`, `hasError`, `canDelete`)
- **Complete Words**: Avoid abbreviations except standard ones (API, URL, etc.)

### Function Design

- Write short functions with single purpose (< 20 instructions)
- Name functions with verb + description
  - Boolean returns: `isX`, `hasX`, `canX`
 - Actions: `executeX`, `saveX`, `loadX`
- Use early returns to avoid nesting
- Extract utility functions for complex logic
- Use higher-order functions (map, filter, reduce) when appropriate
- Use default parameter values instead of null checks
- Follow RO-RO pattern for complex parameters (Receive Object, Return Object)

### Data Management

- Use data classes for data structures
- Encapsulate data in composite types, avoid primitive obsession
- Implement validation in data classes, not in functions
- Prefer immutability with `val` for read-only data
- Use sealed classes/interfaces for type-safe state management

### Class Design

- Follow SOLID principles strictly
- Prefer composition over inheritance
- Declare interfaces to define contracts
- Keep classes small and focused
  - < 200 instructions per class
  - < 10 public methods
  - < 10 properties
- Single responsibility principle

### Exception Handling

- Use exceptions only for unexpected errors
- Catch exceptions to:
  - Fix expected problems
  - Add context information
  - Otherwise, use global handlers
- Implement proper error boundaries

## Android-Specific Implementation

### State Management

- **MVI Pattern Implementation**:

```kotlin
sealed interface ViewIntent {
    data class SearchUsers(val query: String) : ViewIntent
    data object RefreshUsers : ViewIntent
}

data class ViewState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface ViewEffect {
    data class NavigateToDetail(val username: String) : ViewEffect
    data class ShowError(val message: String) : ViewEffect
}
```

### Navigation Implementation

- **Deep Link Navigation**:

```kotlin
// Navigate using deep links
navigation.navigate("githubusers://users/detail/octocat")

// Type-safe navigation
navigation.navigate(AppDestination.UserDetail("octocat"))
```

### UI Components

- **Material 3 Components**:
  - Use `TopAppBar` with proper scroll behavior
  - Implement `SearchBar` for search functionality
  - Use `LazyColumn` with `LazyPagingItems` for lists
  - Implement proper focus management and keyboard handling
- **Accessibility**:
  - Add content descriptions for all interactive elements
  - Implement proper focus navigation
  - Support screen readers and accessibility services
  - Follow WCAG guidelines

### Performance Optimization

- **Memory Management**:
  - Use `remember` and `derivedStateOf` appropriately
  - Implement proper coroutine scope management
  - Use `cachedIn` for Flow operations in ViewModels
- **UI Performance**:
  - Minimize recomposition with proper state management
  - Use `LaunchedEffect` for side effects
  - Implement proper loading states and error handling

## Testing Strategy

### Unit Testing

- **ViewModels**: Test all intents and state changes
- **Use Cases**: Test business logic with mocked dependencies
- **Repositories**: Test data operations and caching
- **Mappers**: Test data transformations
- **Follow AAA Pattern**: Arrange, Act, Assert

### Integration Testing

- **Navigation Flows**: Test complete user journeys
- **API Integration**: Test real API calls with test data
- **Database Operations**: Test Room database operations
- **Dependency Injection**: Test Hilt module configurations

### UI Testing

- **Compose Testing**: Use `createAndroidComposeRule`
- **Navigation Testing**: Test deep link handling
- **User Interactions**: Test search, navigation, and error scenarios
- **Accessibility Testing**: Verify screen reader compatibility

### Test Naming Convention

- Use descriptive test names: `test_searchUsers_withValidQuery_returnsResults()`
- Follow naming pattern: `test_[method]_[condition]_[expectedResult]`
- Use clear variable names: `inputQuery`, `expectedUsers`, `actualResult`

## Code Quality Standards

### Static Analysis

- **Detekt**: Code smell detection and Kotlin best practices
- **Lint**: Android-specific checks and warnings
- **Kotlin Compiler**: Strict mode and null safety checks

### Code Organization

- **Feature-based Modules**: High cohesion, low coupling
- **Package Structure**: Follow feature → layer organization
- **Consistent Formatting**: Use ktlint for code formatting
- **Documentation**: KDoc for public APIs and complex logic

### Error Handling

- **Network Errors**: Implement proper retry logic and user feedback
- **Validation Errors**: Clear error messages and field-level validation
- **System Errors**: Graceful degradation and user guidance
- **Logging**: Structured logging for debugging and monitoring

## Security Implementation

### Data Protection

- **Secure Storage**: Use EncryptedSharedPreferences for sensitive data
- **Network Security**: Implement certificate pinning and secure communication
- **Input Validation**: Validate all user inputs and API responses
- **Token Management**: Secure storage and rotation of authentication tokens

### Privacy

- **Data Minimization**: Collect only necessary user data
- **User Consent**: Implement proper consent mechanisms
- **Data Deletion**: Support user data deletion requests
- **Audit Logging**: Track data access and modifications

## Multi-AI Consultation Integration

### Automatic Triggers

The system automatically activates multi-AI consultation for:

- **Architecture Decisions**: Choosing between patterns, libraries, or frameworks
- **Navigation Implementation**: Complex navigation flows or deep linking
- **Performance Issues**: App startup, memory usage, battery consumption
- **Security Implementation**: Authentication, data protection, API security
- **Material Design**: Complex UI patterns or accessibility requirements
- **Testing Strategy**: Comprehensive test coverage and automation

### Manual Triggers

Users can explicitly request multi-AI consultation with:

- "Multi-AI review of Android [component]"
- "Analyze [Android feature] with all models"
- "Get consensus on [Android decision]"
- "Comprehensive assessment of [Android implementation]"

## Implementation Examples

### ViewModel with MVI Pattern

```kotlin
@HiltViewModel
class UserListViewModel @Inject constructor(
    private val observeUserListUseCase: ObserveUserListUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    
    private val _state = MutableStateFlow(UserListState())
    val state: StateFlow<UserListState> = _state.asStateFlow()
    
    fun processIntent(intent: UserListIntent) {
        when (intent) {
            is UserListIntent.SearchUsers -> executeSearch(intent.query)
            is UserListIntent.RefreshUsers -> refreshUsers()
            is UserListIntent.UserClicked -> navigateToDetail(intent.user)
        }
    }
    
    private fun executeSearch(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            // Implementation details...
        }
    }
}
```

### Deep Link Navigation

```kotlin
@Composable
fun UserListScreen(
    viewModel: UserListViewModel,
    onUserClick: (User) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LazyColumn {
        items(state.users) { user ->
            UserItem(
                user = user,
                onClick = { onUserClick(user) }
            )
        }
    }
}
```

### Material 3 Search Implementation

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    SearchBar(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = onSearch,
        placeholder = { Text("Search GitHub users...") },
        modifier = modifier
    ) {
        // Search suggestions...
    }
}
```

## Best Practices Summary

### ✅ DO

- Use Clean Architecture with clear layer separation
- Implement MVI pattern for state management
- Use Navigation 3 with deep link navigation
- Follow Material 3 design guidelines
- Write comprehensive tests for all layers
- Use proper error handling and user feedback
- Implement accessibility features
- Follow Kotlin best practices and idioms

### ❌ DON'T

- Mix business logic with UI code
- Use deprecated APIs or patterns
- Ignore accessibility requirements
- Skip error handling and edge cases
- Write untested code
- Use hardcoded values or magic numbers
- Ignore performance implications
- Violate SOLID principles

## Continuous Improvement

### Code Review Checklist

- [ ] Follows Clean Architecture principles
- [ ] Implements MVI pattern correctly
- [ ] Uses proper navigation patterns
- [ ] Follows Material 3 guidelines
- [ ] Includes proper error handling
- [ ] Has comprehensive test coverage
- [ ] Follows accessibility guidelines
- [ ] Uses proper Kotlin idioms

### Performance Monitoring

- Monitor app startup time
- Track memory usage and leaks
- Measure UI frame rates
- Analyze network call efficiency
- Monitor battery consumption

This rule ensures the highest quality Android development by combining modern best practices with comprehensive testing and multi-AI validation for critical decisions.

- Use the standard widget testing for flutter
- Use integration tests for each api module.