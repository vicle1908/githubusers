# MCP Patterns and Android Development Rules

## Critical MCP Tool Patterns

### Build Operations (Gradle MCP)
```bash
# ALWAYS use Gradle MCP - NEVER ./gradlew
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
mcp_gradle-mcp-server_execute_gradle_task("detekt")
mcp_gradle-mcp-server_execute_gradle_task("ktlintCheck")
mcp_gradle-mcp-server_get_gradle_project_info("/path/to/project")
```

### Code Search (Claude Context)
```bash
# Index before searching
mcp_claude-context_index_codebase("/path", splitter='ast', force=false)
mcp_claude-context_get_indexing_status("/path")
mcp_claude-context_search_code("search pattern", path="/path", limit=10)
mcp_claude-context_clear_index("/path")
```

### Android Device Operations
```bash
# Android MCP for ADB
mcp_android_execute_adb_shell_command("logcat -d | grep FATAL")
mcp_android_get_packages()
mcp_android_get_uilayout()
mcp_android_get_screenshot()

# Mobile MCP for UI automation
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers")
mcp_mobile-mcp_mobile_take_screenshot()
mcp_mobile-mcp_mobile_list_elements_on_screen()
mcp_mobile-mcp_mobile_click_on_screen_at_coordinates(x, y)
```

### Documentation Research
```bash
# Context7 (two-step process)
mcp_context7_resolve-library-id("androidx.navigation")
mcp_context7_get-library-docs("/androidx/navigation", "deep links")

# OR DocFork (single-step)
mcp_docfork_get-library-docs("androidx-navigation", "deep links")

# Repository docs
mcp_deepwiki_ask_question("android/architecture-samples", "Navigation 3 patterns")

# Real-world code
mcp_grep-remote_searchGitHub("NavHost(", language=["Kotlin"])
```

### Web Search and Analysis
```bash
# Current information
mcp_tavily_tavily-search("Android Navigation 3 2024")
mcp_brave-search_brave_web_search("query", result_filter=["web", "news"])
mcp_medium-search_search_medium_topic("Android Navigation", filters={"tags": ["android"]})

# Semantic search
mcp_exa_web_search_exa("query", numResults=5)
mcp_exa_deep_researcher_start("complex research question")
mcp_exa_deep_researcher_check("taskId")
```

### Memory Management
```bash
# OpenMemory for user context
mcp_openmemory_search-memories("query", limit=10)
mcp_openmemory_add-memory("content", "metadata")

# Byterover for programming patterns
mcp_byterover-mcp_byterover-retrieve-knowledge("query", limit=10)
mcp_byterover-mcp_byterover-store-knowledge("pattern", "context")
mcp_byterover-mcp_byterover-list-modules()
mcp_byterover-mcp_byterover-save-implementation-plan("plan")
mcp_byterover-mcp_byterover-update-plan-progress("plan", "task", true)
```

### AI Analysis
```bash
# Multi-model consensus
mcp_zen_consensus("question", models=[...], findings="...")
mcp_zen_thinkdeep("problem", step_number=1, findings="...")
mcp_zen_planner("task", step_number=1)
mcp_zen_debug("issue", hypothesis="...")
mcp_zen_codereview("files", review_type="full")
mcp_zen_refactor("files", refactor_type="codesmells")
mcp_zen_precommit("/path", compare_to="main")
```

### Git Operations
```bash
# Git MCP Server
mcp_git-mcp-server_git_set_working_dir("/absolute/path")
mcp_git-mcp-server_git_status()
mcp_git-mcp-server_git_diff(staged=true)
mcp_git-mcp-server_git_commit("feat(scope): message")
mcp_git-mcp-server_git_log(maxCount=10)
mcp_git-mcp-server_git_branch(mode="list")
```

## Android-Specific Patterns

### Navigation 3 Deep Link Pattern
```kotlin
// Feature owns its destination
@Serializable
data class FeatureDestination(
    val param: String
) : AppDestination

// Deep link handler
@Singleton
class FeatureDeepLinkHandler @Inject constructor() : DeepLinkHandler {
    override fun canHandle(uri: String): Boolean =
        uri.startsWith("githubusers://feature/")
    
    override fun handle(uri: String): NavigationDestination? {
        // Parse URI and return destination
        val param = uri.substringAfter("feature/")
        return FeatureDestination(param)
    }
}

// Register with Hilt
@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureNavigationModule {
    @Binds
    @IntoSet
    abstract fun bindDeepLinkHandler(
        handler: FeatureDeepLinkHandler
    ): DeepLinkHandler
}
```

### MVI Pattern
```kotlin
// State, Intent, Effect definitions
data class FeatureState(
    val isLoading: Boolean = false,
    val data: List<Item> = emptyList(),
    val error: String? = null
)

sealed interface FeatureIntent {
    data object LoadData : FeatureIntent
    data class SelectItem(val id: String) : FeatureIntent
}

sealed interface FeatureEffect {
    data class NavigateToDetail(val id: String) : FeatureEffect
    data class ShowError(val message: String) : FeatureEffect
}

// ViewModel implementation
@HiltViewModel
class FeatureViewModel @Inject constructor(
    private val useCase: FeatureUseCase
) : MviViewModel<FeatureState, FeatureIntent, FeatureEffect>() {
    
    override fun createInitialState() = FeatureState()
    
    override fun handleIntent(intent: FeatureIntent) {
        when (intent) {
            is FeatureIntent.LoadData -> loadData()
            is FeatureIntent.SelectItem -> selectItem(intent.id)
        }
    }
    
    private fun loadData() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            useCase.getData()
                .onSuccess { data ->
                    setState { 
                        copy(isLoading = false, data = data)
                    }
                }
                .onFailure { error ->
                    setState { 
                        copy(isLoading = false, error = error.message)
                    }
                    setEffect(FeatureEffect.ShowError(error.message))
                }
        }
    }
}
```

### Repository Pattern (Offline-First)
```kotlin
@Singleton
class FeatureRepository @Inject constructor(
    private val apiService: FeatureApiService,
    private val dao: FeatureDao,
    private val mediator: FeatureRemoteMediator
) {
    fun getPagedData(): Flow<PagingData<Item>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        remoteMediator = mediator,
        pagingSourceFactory = { dao.pagingSource() }
    ).flow.cachedIn(viewModelScope)
    
    suspend fun refreshData() {
        mediator.refresh()
    }
}
```

### Convention Plugin Usage
```kotlin
// Feature module build.gradle.kts
plugins {
    id("githubusers.android.library")
    id("githubusers.android.compose")
    id("githubusers.android.hilt")
}

android {
    namespace = "com.example.githubusers.feature.yourfeature"
}

dependencies {
    implementation(project(":core-common"))
    implementation(project(":core-ui"))
    implementation(project(":core-mvi"))
    implementation(project(":navigation-api"))
    
    // Version catalog usage
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    
    // OkHttp BOM
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.test.ext)
}
```

## Research Workflow Pattern
```kotlin
// Step-by-step research implementation
suspend fun researchAndImplement(feature: String) {
    // 1. Find existing patterns
    val existingPatterns = mcp_claude-context_search_code(feature)
    
    // 2. Get documentation
    val libraryId = mcp_context7_resolve-library-id(feature)
    val docs = mcp_context7_get-library-docs(libraryId, feature)
    
    // 3. Repository best practices
    val bestPractices = mcp_deepwiki_ask_question(
        "android/architecture-samples", 
        "$feature implementation"
    )
    
    // 4. Real-world examples
    val examples = mcp_grep-remote_searchGitHub(
        "$feature(", 
        language = ["Kotlin"]
    )
    
    // 5. Semantic understanding
    val semantic = mcp_exa_web_search_exa("$feature patterns")
    
    // 6. Current trends
    val trends = mcp_tavily_tavily-search("$feature 2024")
    
    // 7. Multi-modal search
    val comprehensive = mcp_brave-search_brave_web_search(feature)
    
    // 8. Articles
    val articles = mcp_medium-search_search_medium_topic(feature)
    
    // 9. AI consensus
    val consensus = mcp_zen_consensus(
        "Best approach for $feature",
        findings = combineFindings(/*...*/)
    )
    
    // 10. Deep analysis
    val analysis = mcp_zen_thinkdeep(
        "$feature implementation strategy",
        findings = consensus
    )
    
    // Store knowledge
    mcp_byterover-mcp_byterover-store-knowledge(
        pattern = analysis.pattern,
        context = analysis.context
    )
}
```

## Byterover Workflow Pattern
```kotlin
// Complete Byterover workflow
suspend fun byteroverWorkflow(task: String) {
    // 1. Check handbook
    val handbookExists = mcp_byterover-mcp_byterover-check-handbook-existence()
    if (!handbookExists) {
        mcp_byterover-mcp_byterover-create-handbook()
    } else {
        val syncStatus = mcp_byterover-mcp_byterover-check-handbook-sync()
        if (syncStatus.hasChanges) {
            mcp_byterover-mcp_byterover-update-handbook()
        }
    }
    
    // 2. List and retrieve
    val modules = mcp_byterover-mcp_byterover-list-modules()
    val knowledge = mcp_byterover-mcp_byterover-retrieve-knowledge(task)
    
    // 3. Create plan
    val plan = createImplementationPlan(task, knowledge)
    
    // 4. SAVE IMMEDIATELY after approval
    mcp_byterover-mcp_byterover-save-implementation-plan(plan)
    
    // 5. Execute with progress updates
    plan.tasks.forEach { task ->
        executeTask(task)
        mcp_byterover-mcp_byterover-update-plan-progress(
            plan_name = plan.name,
            task_name = task.name,
            is_completed = true
        )
    }
    
    // 6. Complete plan
    mcp_byterover-mcp_byterover-update-plan-progress(
        plan_name = plan.name,
        is_completed = true
    )
    
    // 7. Store knowledge
    mcp_byterover-mcp_byterover-store-knowledge(
        pattern = extractPattern(plan),
        context = extractContext(plan)
    )
}
```

## Quality Enforcement Pattern
```bash
# Run quality checks via Gradle MCP
mcp_gradle-mcp-server_execute_gradle_task("detekt")
mcp_gradle-mcp-server_execute_gradle_task("ktlintCheck")
mcp_gradle-mcp-server_execute_gradle_task("test")

# Pre-commit validation
mcp_zen_precommit(
    path = "/project/path",
    compare_to = "main",
    include_staged = true,
    include_unstaged = true
)

# Code review
mcp_zen_codereview(
    relevant_files = ["file1.kt", "file2.kt"],
    review_type = "full"
)
```

## Error Handling Pattern
```kotlin
// MCP error handling with fallbacks
suspend fun executeMcpWithFallback(
    primary: suspend () -> Result<T>,
    fallback: suspend () -> Result<T>? = null
): Result<T> {
    return try {
        primary()
    } catch (e: McpServerNotFoundException) {
        if (fallback != null && userApproval()) {
            fallback()
        } else {
            Result.failure(e)
        }
    }
}

// Usage
val result = executeMcpWithFallback(
    primary = { 
        mcp_gradle-mcp-server_execute_gradle_task(":app:build")
    },
    fallback = {
        // Only with explicit approval
        runCommand("./gradlew :app:build")
    }
)
```

---
These patterns represent the core MCP tool usage and Android development patterns from AGENTS.md v1.0