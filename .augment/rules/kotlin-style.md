# Kotlin Style Guide

## Import Rules (CRITICAL)

### No Wildcard Imports (MANDATORY)


- **NEVER** use wildcard/star imports
- **ALWAYS** use single imports only
- Enforced via `.editorconfig` and Detekt rules

### Import Ordering


```kotlin
```kotlin

// Correct - Single imports only
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Injec

// WRONG - Never use wildcards
import kotlinx.coroutines.flow.*  // FORBIDDEN

```

## Naming Conventions

### Classes and Interfaces
- **PascalCase**: `UserDetailViewModel`, `NavigationController`
- Interfaces don't use "I" prefix: `UserRepository` not `IUserRepository`

### Functions and Properties
- **camelCase**: `getUserData()`, `isLoading`
- Boolean properties/functions use prefixes: `isX`, `hasX`, `canX`

### Files and Packages
- Files: Match class name in PascalCase or use descriptive names
- Packages: lowercase, no underscores

### Constants
- **UPPER_SNAKE_CASE**: `MAX_RETRY_COUNT`, `DEFAULT_TIMEOUT`

## Code Style

### Function Design
- Keep functions small (< 20 lines)
- Single responsibility principle
- Use early returns to avoid nesting
- Prefer expression bodies for simple functions

### Type Declaration
- Always declare explicit types for public APIs
- Type inference allowed for local variables when obvious
- Avoid using `Any` type

### Data Classes
```kotlin


```kotlin
// Correct - Single imports only
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Injec

// WRONG - Never use wildcards
import kotlinx.coroutines.flow.*  // FORBIDDEN
// Correc
data class User(
    val id: String,
    val username: String,
    val createdAt: Instan
)

// Use sealed classes for state
sealed interface ViewState {
    data object Loading : ViewState
    data class Success(val data: List<User>) : ViewState
    data class Error(val message: String) : ViewState
}
```

## Null Safety

### Safe Calls


```kotlin
```kotlin

// Correct - Single imports only
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Injec

// WRONG - Never use wildcards
import kotlinx.coroutines.flow.*  // FORBIDDEN
// Correc
data class User(
    val id: String,
    val username: String,
    val createdAt: Instan
)

// Use sealed classes for state
sealed interface ViewState {
    data object Loading : ViewState
    data class Success(val data: List<User>) : ViewState
    data class Error(val message: String) : ViewState
}
// Correct - Use safe calls
val length = username?.length ?: 0

// WRONG - Avoid !!
val length = username!!.length  // AVOID

```

### Validation
```kotlin


```kotlin
// Correct - Single imports only
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Injec

// WRONG - Never use wildcards
import kotlinx.coroutines.flow.*  // FORBIDDEN
// Correc
data class User(
    val id: String,
    val username: String,
    val createdAt: Instan
)

// Use sealed classes for state
sealed interface ViewState {
    data object Loading : ViewState
    data class Success(val data: List<User>) : ViewState
    data class Error(val message: String) : ViewState
}
// Correct - Use safe calls
val length = username?.length ?: 0

// WRONG - Avoid !!
val length = username!!.length  // AVOID
// Use require/check for preconditions
fun processUser(user: User?) {
    requireNotNull(user) { "User cannot be null" }
    check(user.id.isNotEmpty()) { "User ID cannot be empty" }
}
```

## Coroutines and Flow

### Structured Concurrency


- Never use `GlobalScope`
- Use proper scope managemen
- Handle cancellation properly

### Flow Best Practices


```kotlin
```kotlin

// Correct - Single imports only
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Injec

// WRONG - Never use wildcards
import kotlinx.coroutines.flow.*  // FORBIDDEN
// Correc
data class User(
    val id: String,
    val username: String,
    val createdAt: Instan
)

// Use sealed classes for state
sealed interface ViewState {
    data object Loading : ViewState
    data class Success(val data: List<User>) : ViewState
    data class Error(val message: String) : ViewState
}
// Correct - Use safe calls
val length = username?.length ?: 0

// WRONG - Avoid !!
val length = username!!.length  // AVOID
// Use require/check for preconditions
fun processUser(user: User?) {
    requireNotNull(user) { "User cannot be null" }
    check(user.id.isNotEmpty()) { "User ID cannot be empty" }
}
// Expose read-only StateFlow
private val _state = MutableStateFlow(initialState)
val state: StateFlow<ViewState> = _state.asStateFlow()

// Use stateIn/shareIn for cold flows
val users = userRepository.getUsers()
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

```

## Collection Operations

### Immutability
- Prefer immutable collections at API boundaries
- Use `List`, `Set`, `Map` instead of mutable variants
- Create defensive copies when needed

### Functional Operations
```kotlin


```kotlin
// Correct - Single imports only
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Injec

// WRONG - Never use wildcards
import kotlinx.coroutines.flow.*  // FORBIDDEN
// Correc
data class User(
    val id: String,
    val username: String,
    val createdAt: Instan
)

// Use sealed classes for state
sealed interface ViewState {
    data object Loading : ViewState
    data class Success(val data: List<User>) : ViewState
    data class Error(val message: String) : ViewState
}
// Correct - Use safe calls
val length = username?.length ?: 0

// WRONG - Avoid !!
val length = username!!.length  // AVOID
// Use require/check for preconditions
fun processUser(user: User?) {
    requireNotNull(user) { "User cannot be null" }
    check(user.id.isNotEmpty()) { "User ID cannot be empty" }
}
// Expose read-only StateFlow
private val _state = MutableStateFlow(initialState)
val state: StateFlow<ViewState> = _state.asStateFlow()

// Use stateIn/shareIn for cold flows
val users = userRepository.getUsers()
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
// Prefer functional style
val activeUsers = users
    .filter { it.isActive }
    .map { it.username }
    .sorted()

// Use sequences for large collections
val result = largeList.asSequence()
    .filter { it.meetsCriteria() }
    .map { transform(it) }
    .toList()
```

## Scope Functions

### Usage Guidelines


- **let**: Null checks and transformations
- **apply**: Object configuration
- **run**: Computing results with receiver
- **also**: Side effects without changing the value
- **with**: Rarely used, prefer `run`

```kotlin
```kotlin

// Correct - Single imports only
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Injec

// WRONG - Never use wildcards
import kotlinx.coroutines.flow.*  // FORBIDDEN
// Correc
data class User(
    val id: String,
    val username: String,
    val createdAt: Instan
)

// Use sealed classes for state
sealed interface ViewState {
    data object Loading : ViewState
    data class Success(val data: List<User>) : ViewState
    data class Error(val message: String) : ViewState
}
// Correct - Use safe calls
val length = username?.length ?: 0

// WRONG - Avoid !!
val length = username!!.length  // AVOID
// Use require/check for preconditions
fun processUser(user: User?) {
    requireNotNull(user) { "User cannot be null" }
    check(user.id.isNotEmpty()) { "User ID cannot be empty" }
}
// Expose read-only StateFlow
private val _state = MutableStateFlow(initialState)
val state: StateFlow<ViewState> = _state.asStateFlow()

// Use stateIn/shareIn for cold flows
val users = userRepository.getUsers()
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
// Prefer functional style
val activeUsers = users
    .filter { it.isActive }
    .map { it.username }
    .sorted()

// Use sequences for large collections
val result = largeList.asSequence()
    .filter { it.meetsCriteria() }
    .map { transform(it) }
    .toList()
// Correct usage
user?.let { activeUser ->
    processUser(activeUser)
}

// Object configuration
val intent = Intent().apply {
    action = Intent.ACTION_VIEW
    data = Uri.parse(url)
}

```

## Quality Enforcemen

### KtLin
- Format code automatically
- Enforce Kotlin conventions
- Integrated via convention plugin

### Detek
- Static code analysis
- Code smell detection
- Custom rules for project standards
- No wildcard imports rule enforced

### EditorConfig
```properties


```kotlin
// Correct - Single imports only
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Injec

// WRONG - Never use wildcards
import kotlinx.coroutines.flow.*  // FORBIDDEN
// Correc
data class User(
    val id: String,
    val username: String,
    val createdAt: Instan
)

// Use sealed classes for state
sealed interface ViewState {
    data object Loading : ViewState
    data class Success(val data: List<User>) : ViewState
    data class Error(val message: String) : ViewState
}
// Correct - Use safe calls
val length = username?.length ?: 0

// WRONG - Avoid !!
val length = username!!.length  // AVOID
// Use require/check for preconditions
fun processUser(user: User?) {
    requireNotNull(user) { "User cannot be null" }
    check(user.id.isNotEmpty()) { "User ID cannot be empty" }
}
// Expose read-only StateFlow
private val _state = MutableStateFlow(initialState)
val state: StateFlow<ViewState> = _state.asStateFlow()

// Use stateIn/shareIn for cold flows
val users = userRepository.getUsers()
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
// Prefer functional style
val activeUsers = users
    .filter { it.isActive }
    .map { it.username }
    .sorted()

// Use sequences for large collections
val result = largeList.asSequence()
    .filter { it.meetsCriteria() }
    .map { transform(it) }
    .toList()
// Correct usage
user?.let { activeUser ->
    processUser(activeUser)
}

// Object configuration
val intent = Intent().apply {
    action = Intent.ACTION_VIEW
    data = Uri.parse(url)
}
# .editorconfig
[*.{kt,kts}]
indent_size = 4
max_line_length = 120
kotlin_imports_layout = single
wildcard_import = false
```

## Testing Conventions

### Test Naming


```kotlin
```kotlin

// Correct - Single imports only
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Injec

// WRONG - Never use wildcards
import kotlinx.coroutines.flow.*  // FORBIDDEN
// Correc
data class User(
    val id: String,
    val username: String,
    val createdAt: Instan
)

// Use sealed classes for state
sealed interface ViewState {
    data object Loading : ViewState
    data class Success(val data: List<User>) : ViewState
    data class Error(val message: String) : ViewState
}
// Correct - Use safe calls
val length = username?.length ?: 0

// WRONG - Avoid !!
val length = username!!.length  // AVOID
// Use require/check for preconditions
fun processUser(user: User?) {
    requireNotNull(user) { "User cannot be null" }
    check(user.id.isNotEmpty()) { "User ID cannot be empty" }
}
// Expose read-only StateFlow
private val _state = MutableStateFlow(initialState)
val state: StateFlow<ViewState> = _state.asStateFlow()

// Use stateIn/shareIn for cold flows
val users = userRepository.getUsers()
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
// Prefer functional style
val activeUsers = users
    .filter { it.isActive }
    .map { it.username }
    .sorted()

// Use sequences for large collections
val result = largeList.asSequence()
    .filter { it.meetsCriteria() }
    .map { transform(it) }
    .toList()
// Correct usage
user?.let { activeUser ->
    processUser(activeUser)
}

// Object configuration
val intent = Intent().apply {
    action = Intent.ACTION_VIEW
    data = Uri.parse(url)
}

# .editorconfig

[*.{kt,kts}]
indent_size = 4
max_line_length = 120
kotlin_imports_layout = single
wildcard_import = false
@Tes
fun `test getUserById with valid ID returns user`() {
    // Test implementation
}

// Or traditional naming
@Tes
fun test_getUserById_withValidId_returnsUser() {
    // Test implementation
}

```

### Test Structure
- Follow AAA pattern: Arrange, Act, Asser
- Use descriptive test names
- One assertion per test when possible

## Documentation

### KDoc
```kotlin


```kotlin
// Correct - Single imports only
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Injec

// WRONG - Never use wildcards
import kotlinx.coroutines.flow.*  // FORBIDDEN
// Correc
data class User(
    val id: String,
    val username: String,
    val createdAt: Instan
)

// Use sealed classes for state
sealed interface ViewState {
    data object Loading : ViewState
    data class Success(val data: List<User>) : ViewState
    data class Error(val message: String) : ViewState
}
// Correct - Use safe calls
val length = username?.length ?: 0

// WRONG - Avoid !!
val length = username!!.length  // AVOID
// Use require/check for preconditions
fun processUser(user: User?) {
    requireNotNull(user) { "User cannot be null" }
    check(user.id.isNotEmpty()) { "User ID cannot be empty" }
}
// Expose read-only StateFlow
private val _state = MutableStateFlow(initialState)
val state: StateFlow<ViewState> = _state.asStateFlow()

// Use stateIn/shareIn for cold flows
val users = userRepository.getUsers()
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
// Prefer functional style
val activeUsers = users
    .filter { it.isActive }
    .map { it.username }
    .sorted()

// Use sequences for large collections
val result = largeList.asSequence()
    .filter { it.meetsCriteria() }
    .map { transform(it) }
    .toList()
// Correct usage
user?.let { activeUser ->
    processUser(activeUser)
}

// Object configuration
val intent = Intent().apply {
    action = Intent.ACTION_VIEW
    data = Uri.parse(url)
}
# .editorconfig
[*.{kt,kts}]
indent_size = 4
max_line_length = 120
kotlin_imports_layout = single
wildcard_import = false
@Tes
fun `test getUserById with valid ID returns user`() {
    // Test implementation
}

// Or traditional naming
@Tes
fun test_getUserById_withValidId_returnsUser() {
    // Test implementation
}
/**
 * Retrieves user details from the repository.
 *
 * @param userId The unique identifier of the user
 * @return User details or null if not found
 * @throws NetworkException if network request fails
 */
suspend fun getUserDetails(userId: String): User?
```

### Comments


- Prefer self-documenting code over comments
- Use KDoc for public APIs
- Explain "why" not "what" in comments

## MCP Integration Notes

### Build and Quality Checks


- **ALWAYS** run quality checks via Gradle MCP
- Use `ktlintCheck` and `detekt` tasks
- Never bypass quality gates

### Enforcemen


- All code must pass KtLint and Detek
- No wildcard imports policy is non-negotiable
- Version catalog usage is mandatory
