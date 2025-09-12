# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## GitHub Users Android - MCP-First Development Portal

**IMPORTANT:** Canonical rules, policies, and deep documentation live in [`docs/assistants/`](docs/assistants/). This WARP.md serves as a quickstart and operator guide tailored for WARP terminal + MCP usage. If there are conflicts, `docs/assistants/` is the source of truth.

### Quick Links (Canonical Documentation)
- **[MCP Server Usage Guide](docs/assistants/mcp-guide.md)** - MCP-first policy, server selection and usage
- **[Android Development Standards](docs/assistants/android-standards.md)** - Clean Architecture, MVI, Navigation 3, build conventions
- **[Enhanced Research Strategy](docs/assistants/enhanced-research-strategy.md)** - 10-step research workflow and tool selection
- **[Multi-AI Consultation](docs/assistants/multi-ai-consultation.md)** - Multi-model consensus and validation
- **[Android Debugging Guide](docs/assistants/android-debugging.md)** - MCP-based debugging workflows
- **[Kotlin Style Guide](docs/assistants/kotlin-style.md)** - Code style and quality standards
- **[ByteRover Workflows](docs/assistants/byterover-rules.md)** - Knowledge management and planning

---

## 1. WARP + MCP Quickstart

### Prerequisites
WARP terminal with MCP servers configured:
- **Gradle MCP** (builds, tests, quality) - MANDATORY
- **Android MCP** (device operations, logcat)
- **Mobile-MCP** (UI automation, screenshots)
- **Claude Context** (code search, indexing)
- **Git MCP Server** (version control operations)
- **Zen MCP** (consensus, deep thinking)
- **ByteRover MCP** (knowledge, plans, modules)

### Critical Policy: NEVER use ./gradlew directly
**ALL builds must go through Gradle MCP.** Manual `./gradlew` usage is prohibited.

### Common Build Commands (via Gradle MCP)

```bash
# Discover available tasks
mcp_gradle-mcp-server_execute_gradle_task(":app:tasks")

# Clean builds
mcp_gradle-mcp-server_execute_gradle_task(":app:clean")
mcp_gradle-mcp-server_execute_gradle_task("clean")  # all modules

# Build variants
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleRelease")

# Testing
mcp_gradle-mcp-server_execute_gradle_task(":app:testDebugUnitTest")
mcp_gradle-mcp-server_execute_gradle_task(":feature-users:test")
mcp_gradle-mcp-server_execute_gradle_task("test")  # all modules

# Quality checks
mcp_gradle-mcp-server_execute_gradle_task(":app:detekt")
mcp_gradle-mcp-server_execute_gradle_task(":app:ktlintCheck")
mcp_gradle-mcp-server_execute_gradle_task(":app:lintDebug")

# Build system (convention plugins)
mcp_gradle-mcp-server_execute_gradle_task(":plugins:build")
```

---

## 2. Device Operations and Testing (MCP-Only)

### Install and Launch App

```bash
# Build APK first
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")

# Install via Android MCP
mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")

# Launch app via Mobile-MCP
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers.debug")

# Take screenshot
mcp_mobile-mcp_mobile_take_screenshot()
```

### Debugging and Logs

```bash
# Crash detection
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception|Error)' | tail -20")

# Detailed crash analysis
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")

# Component-specific logs
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|MainActivity)' | tail -15")

# Real-time monitoring
mcp_android_execute_adb_shell_command("logcat -c && logcat | grep -E '(YourTag)' | head -10")
```

---

## 3. Architecture Overview (Summary - see docs/assistants/ for details)

### Clean Architecture (Mandatory)
- **Presentation Layer**: Activities, Fragments, ViewModels, Compose UI
- **Domain Layer**: Use cases, business logic, entities  
- **Data Layer**: Repositories, data sources, DTOs

### MVI Pattern
- **Unidirectional Data Flow**: Intent → ViewModel → State → UI
- Single source of truth for UI state
- Sealed classes for type-safe state management

### Feature-Based Modular Design
```
githubusers/
├── app/                    # Main application entry point
├── core-*/                 # Core functionality modules
│   ├── core-common/        # Common utilities and extensions
│   ├── core-mvi/           # MVI pattern implementation
│   ├── core-networking/    # Network layer and API clients
│   ├── core-storage/       # Local storage and caching
│   └── core-ui/            # Shared UI components and themes
├── feature-*/              # Feature-specific modules
│   ├── feature-users/      # User management and profiles
│   ├── feature-search/     # Search functionality
│   └── feature-settings/   # App settings and preferences
├── navigation-*/           # Navigation system
│   ├── navigation-api/     # Navigation contracts
│   └── navigation-impl/    # Navigation implementation
├── plugins/                # Build system convention plugins
└── catalog/                # Dependency version management
```

### Navigation 3 Architecture (MANDATORY)
- **Type-Safe Navigation**: All cross-module navigation MUST use deep links
- **Deep Link Patterns**: 
  ```kotlin
  navigation.navigate("githubusers://users/detail/octocat")
  navigation.navigate(AppDestination.UserDetail("octocat"))
  ```

### Build System Standards
- **Version Catalog**: ALL modules MUST use the version catalog - NO hardcoded versions
- **Platform BOMs**: 
  ```kotlin
  implementation(platform(libs.okhttp.bom))
  implementation(libs.okhttp)
  implementation(libs.okhttp.logging)
  ```
- **Convention Plugins**: All modules apply convention plugins from `plugins/`

---

## 4. MCP-First Policy (Strictly Enforced)

| Task Type | MCP Server | NEVER Use |
|-----------|------------|-----------|
| **All Builds** | Gradle MCP | `./gradlew` |
| **Device Operations** | Android MCP | Manual ADB |
| **UI Automation** | Mobile-MCP | Manual device interaction |
| **Code Search** | Claude Context | File browsing |
| **Version Control** | Git MCP Server | Direct git commands |
| **Documentation** | Context7/DocFork + DeepWiki | Manual web search |

**Fallback Policy**: Manual commands ONLY as last resort with explicit user approval when MCP servers are unavailable.

---

## 5. Research and Multi-AI Workflow (Required)

### 10-Step Research Workflow (Follow docs/assistants/enhanced-research-strategy.md)

```bash
# 1. Existing patterns
mcp_claude-context_search_code("Android Navigation 3 implementation")

# 2. Official documentation  
mcp_context7_resolve-library-id("androidx.navigation")
mcp_context7_get-library-docs("/androidx/navigation", "deep links")

# 3. Repository best practices
mcp_deepwiki_ask_question("android/architecture-samples", "Navigation 3 best practices")

# 4. Real-world code examples
mcp_grep-remote_searchGitHub("NavHost(", language=["Kotlin"])

# 5. Current trends
mcp_tavily_tavily-search("Android Navigation 3 2024 best practices")

# 6. Multi-AI validation
mcp_zen_consensus("Navigation 3 implementation strategy", research_findings)

# 7. Store findings
mcp_byterover-mcp_byterover-store-knowledge(findings)
```

---

## 6. ByteRover Onboarding and Planning (Daily Workflow)

### Session Start (Mandatory)
```bash
# 1. Check/create handbook
byterover-check-handbook-existence
# If missing → byterover-create-handbook
# If exists → byterover-check-handbook-sync → byterover-update-handbook

# 2. Module management
byterover-list-modules
byterover-store-module / byterover-update-module (as needed)
```

### Implementation Planning
```bash
# 1. Retrieve context
byterover-retrieve-knowledge("relevant patterns")

# 2. Create plan → Get approval → IMMEDIATELY persist
byterover-save-implementation-plan(plan_name, implementation, todo_items)

# 3. Track progress
byterover-update-plan-progress(plan_name, task_name, is_completed=true)

# 4. Store insights
byterover-store-knowledge("implementation patterns and decisions")
```

---

## 7. Git Operations (MCP-Only)

```bash
# Use git-mcp-server for all git operations
git_branch(mode="create", branchName="feature/new-feature")
git_add(files=".")
git_commit(message="feat(users): add user profile deep linking")
git_push(branch="feature/new-feature", setUpstream=true)
```

**Conventional Commit Examples:**
- `feat(users): add user profile navigation with deep links`
- `fix(navigation): resolve deep link pattern encoding`
- `docs(warp): add MCP-first development portal`
- `refactor(core): migrate to Navigation 3 type-safe destinations`

---

## 8. Common Debugging Patterns (MCP-Based)

### Date/Time Formatting Issues
```kotlin
// ❌ Crashes
DateTimeFormatter.ofPattern("MMMM yyyy").format(instant)

// ✅ Correct
DateTimeFormatter.ofPattern("MMMM yyyy")
    .format(instant.atZone(ZoneId.systemDefault()).toLocalDate())
```

### Null Safety with SavedStateHandle
```kotlin
// ❌ Can be null
val username = savedStateHandle["username"]

// ✅ Safe default
val username = savedStateHandle["username"] ?: ""
```

### Navigation Deep Link Patterns
```kotlin
// ❌ Incorrect
"app://users/$username"

// ✅ Correct  
"githubusers://users/detail/$username"
```

---

## 9. Code Quality Standards

### Static Analysis (Mandatory)
- **Detekt**: Code smell detection and quality enforcement
- **KtLint**: Code formatting and style enforcement  
- Both applied via convention plugins

### Key Rules
- **NO wildcard imports** - Single imports only
- **Version catalog only** - No hardcoded dependency versions
- **OkHttp BOM** via `platform(libs.okhttp.bom)`
- **Plugin versions** via `version.ref`

### Testing Strategy
```bash
# Unit tests
mcp_gradle-mcp-server_execute_gradle_task(":feature-users:testDebugUnitTest")

# Integration tests  
mcp_gradle-mcp-server_execute_gradle_task(":app:connectedDebugAndroidTest")

# UI tests with screenshots
mcp_mobile-mcp_mobile_take_screenshot()
```

---

## 10. Technology Stack

### Core Technologies
- **Kotlin 2.2.10** - Primary language
- **Android Gradle Plugin 8.13.0** - Build system
- **Jetpack Compose** - Declarative UI
- **Navigation 3** - Type-safe navigation
- **Material Design 3** - Design system

### Architecture Components  
- **Hilt** - Dependency injection
- **Room** - Local database
- **Ktor** - HTTP client
- **Coroutines & Flow** - Asynchronous programming
- **Kotlin Serialization** - JSON handling

---

## 11. Maintenance and Change Management

### Updating This Document
1. **Research changes** using the 10-step workflow
2. **Validate updates** with `mcp_zen_consensus`
3. **Update via Git MCP** with conventional commits
4. **Keep aligned** with `docs/assistants/` canonical documentation

### Regular Tasks
- **Weekly**: Verify MCP server configurations
- **Monthly**: Review and update common command examples
- **Quarterly**: Validate architecture patterns and dependencies
- **When policies change**: Update portal and re-validate with multi-AI consensus

---

## Support and Troubleshooting

### MCP Server Issues
1. **Check server status** and connectivity
2. **Use fallback servers** if available (Context7 ↔ DocFork)
3. **Request explicit approval** before any manual operations

### Build Issues
1. **Clean build** via Gradle MCP: `mcp_gradle-mcp-server_execute_gradle_task("clean")`
2. **Check version catalog** alignment
3. **Verify convention plugin** configuration

### Navigation Issues
1. **Validate deep link patterns** against Navigation 3 spec
2. **Check type safety** with sealed destinations
3. **Test cross-module navigation** with real device

---

**Remember**: This WARP.md is your MCP-first portal. For comprehensive rules and detailed policies, always reference the canonical documentation in [`docs/assistants/`](docs/assistants/).
