# Knowledge Update for GitHubUsers Project
Generated: 2025-09-14T16:34:53Z

## Project Context
- **Location**: `/Users/vinhlekhanh/Downloads/project/company/times/githubusers`
- **Platform**: MacOS with zsh 5.9
- **Git Status**: main branch (clean)
- **Architecture**: Feature-based modular Android app with Navigation 3

## AGENTS.md Version 1.0 - Key Updates

### 1. MCP-First Development Policy
**CRITICAL**: All operations must use MCP servers when available. Manual commands only with explicit approval.

#### MCP Server Mapping
```kotlin
// Build Operations - ALWAYS use Gradle MCP
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
// NEVER use ./gradlew directly

// Code Search - ALWAYS use Claude Context
mcp_claude-context_search_code("pattern to find")
mcp_claude-context_index_codebase(path, splitter='ast')

// Android Operations
mcp_android_execute_adb_shell_command("logcat -d")
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers")

// Documentation
mcp_context7_get-library-docs() // OR mcp_docfork_get-library-docs()
mcp_deepwiki_ask_question("repo", "question")

// Memory Management
mcp_openmemory_add-memory() // Store user preferences & project knowledge
```

### 2. Android Development Standards

#### Navigation 3 Implementation (COMPLETE)
- Feature-owned destinations with deep links
- Type-safe navigation with Kotlin Serialization
- Cross-module communication via deep links ONLY
- Example pattern:
```kotlin
// Feature owns its destination
@Serializable
data class UserDetailDestination(
    val username: String
) : AppDestination

// Deep link handler per feature
class UserDetailDeepLinkHandler : DeepLinkHandler {
    override fun canHandle(uri: String) = 
        uri.startsWith("githubusers://users/")
    override fun handle(uri: String): NavigationDestination? = 
        // Parse and return destination
}
```

#### Build System Configuration
- **Version Catalog**: `catalog/gradle/libs.versions.toml` (single source of truth)
- **Convention Plugins**: Applied via composite builds in `plugins/`
- **No Hardcoded Versions**: ALL versions from catalog
- **OkHttp BOM**: Platform alignment via `platform(libs.okhttp.bom)`

#### Module Structure
```
githubusers/
├── app/                    # Single Activity host
├── core-*/                 # Core modules (common, mvi, networking, storage, ui)
├── feature-*/              # Feature modules (users, search, settings)
├── navigation-*/           # Navigation system (api, impl)
├── plugins/                # Convention plugins
└── catalog/                # Version catalog
```

### 3. Research Workflow (10 Steps)

**MANDATORY before any implementation**:
1. `mcp_claude-context_search_code()` - Find existing patterns
2. `mcp_context7_resolve-library-id()` + `get-library-docs()` OR `mcp_docfork_get-library-docs()`
3. `mcp_deepwiki_ask_question()` - Repository best practices
4. `mcp_grep-remote_searchGitHub()` - Real-world code examples
5. `mcp_exa_web_search_exa()` - Semantic understanding
6. `mcp_tavily_tavily-search()` - Current trends
7. `mcp_brave-search_brave_web_search()` - Multi-modal search
8. `mcp_medium-search_search_medium_topic()` - Articles
9. `mcp_zen_consensus()` - Multi-model validation
10. `mcp_zen_thinkdeep()` - Deep analysis

### 4. Quality Standards

#### Code Quality (Enforced)
- **Detekt**: Static analysis via convention plugin
- **KtLint**: Formatting via convention plugin
- **No Wildcard Imports**: Enforced in .editorconfig
- **KDoc**: Required for public APIs

#### Testing Strategy
- Unit tests for ViewModels, Use Cases, Repositories
- AAA Pattern: Arrange, Act, Assert
- Naming: `test_[method]_[condition]_[expectedResult]`

### 6. Planning Protocol

#### When to Use Plans
- Multi-step non-trivial tasks
- Tasks with logical phases/dependencies
- User requests for TODOs

#### Good Plan Example
1. Create feature module scaffold
2. Define destinations for feature
3. Implement deep link handler
4. Add ViewModel and UI state
5. Add unit/UI tests

### 7. Performance & Security

#### Performance Settings
- Configuration cache: `org.gradle.configuration-cache=true`
- Build cache: `org.gradle.caching=true`
- Parallel execution: `org.gradle.parallel=true`
- KSP over KAPT for annotation processing

#### Security Implementation
- EncryptedSharedPreferences for sensitive data
- OkHttp certificate pinning with backup pins
- Input validation on all API responses
- Token storage with rotation strategy

### 8. File Reference Standards

#### In Answers (Clickable Paths)
- `app/src/main/java/com/example/githubusers/MainActivity.kt:42`
- `navigation-api/src/main/java/.../DeepLinkHandler.kt:87`
- `build.gradle.kts#L10`

#### Code Blocks
```kotlin path=/absolute/path/to/file.kt start=42
// Real code from file
```

```kotlin path=null start=null
// Hypothetical example code
```

### 9. Critical Enforcement Rules

1. **MCP-First**: Never use manual commands without explicit approval
2. **Gradle MCP**: Never run `./gradlew` directly
3. **Navigation 3**: Cross-module via deep links only
4. **Version Catalog**: No hardcoded versions anywhere
5. **Research First**: Complete 10-step workflow before implementation
6. **Save Plans**: Immediately after user approval
7. **Memory Storage**: OpenMemory for context, Byterover for patterns
8. **Quality Checks**: Detekt + KtLint before commits

### 10. Development Workflow Summary

1. **Research** (10-step workflow)
2. **Plan** (if multi-step task)
3. **Index** codebase with Claude Context
4. **Search** existing patterns
5. **Implement** following conventions
6. **Test** with proper patterns
7. **Quality** checks via Gradle MCP
8. **Store** knowledge in Byterover
9. **Update** OpenMemory with decisions

## Project-Specific Patterns

### Navigation Pattern
```kotlin
// Feature module navigation setup
class FeatureNavigationModule {
    @Binds @IntoSet
    abstract fun bindDeepLinkHandler(
        handler: FeatureDeepLinkHandler
    ): DeepLinkHandler
}
```

### MVI Pattern
```kotlin
// Base MVI implementation
class FeatureViewModel : MviViewModel<State, Intent, Effect>() {
    private val stateDelegate = StateDelegate<State>()
    private val effectDelegate = EffectDelegate<Effect>()
    
    fun handleIntent(intent: Intent) {
        // Process intent → update state
    }
}
```

### Repository Pattern
```kotlin
// Offline-first with RemoteMediator
class UserRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val remoteMediator: UserRemoteMediator
) {
    fun getUsers() = Pager(
        config = PagingConfig(pageSize = 20),
        remoteMediator = remoteMediator,
        pagingSourceFactory = { localDataSource.pagingSource() }
    ).flow.cachedIn(scope)
}
```

## Environment Configuration

### Build Optimizations
```properties
# gradle.properties
org.gradle.caching=true
org.gradle.configuration-cache=true
org.gradle.parallel=true
kotlin.incremental=true
```

### Convention Plugin Usage
```kotlin
// Feature module build.gradle.kts
plugins {
    id("githubusers.android.library")
    id("githubusers.android.compose")
    id("githubusers.android.hilt")
}

dependencies {
    implementation(project(":core-common"))
    implementation(project(":navigation-api"))
    implementation(platform(libs.okhttp.bom))
}
```

## Next Steps
1. Ensure all MCP servers are properly configured
2. Index codebase with Claude Context if not already done
3. Set up Byterover modules for each feature
4. Configure OpenMemory with project preferences
5. Apply convention plugins consistently across all modules

---
This knowledge update represents the complete integration of AGENTS.md v1.0 with the GitHubUsers project context.
