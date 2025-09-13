# MCP Server Usage Guide

## Core MCP-First Enforcement

**MANDATORY**: Always use MCP servers for supported tasks. Manual commands are last resort with explicit approval only.

## MCP Server Selection Rules

| Task Type | MCP Server | Primary Tools | When to Use |
|-----------|------------|---------------|-------------|
| **Code Search** | Claude Context | `mcp_claude-context_search_code` | Finding functions, classes, patterns |
| **Build Tasks** | Gradle MCP | `mcp_gradle-mcp-server_execute_gradle_task` | **ALL** builds and tests - NEVER use ./gradlew |
| **Android Device** | Android MCP | `mcp_android_get_packages` | Device management, ADB operations |
| **Android Automation** | Mobile-MCP | `mcp_mobile-mcp_mobile_launch_app` | UI testing, device automation |
| **Library Docs** | Context7/DocFork | `mcp_context7_get-library-docs` | Official documentation |
| **Repository Docs** | DeepWiki | `mcp_deepwiki_ask_question` | GitHub repository best practices |
| **Real-World Code** | Grep-Remote | `mcp_grep-remote_searchGitHub` | Production code examples |
| **Web Search** | Tavily/Brave | `mcp_tavily_tavily-search` | Current information, trends |
| **Article Research** | Medium Search | `mcp_medium-search_search_medium_topic` | In-depth articles, tutorials |
| **Semantic Search** | Exa | `mcp_exa_web_search_exa` | Neural understanding |
| **Memory Storage** | OpenMemory | `mcp_openmemory_add-memory` | Persistent information storage |
| **Knowledge Management** | Byterover | `mcp_byterover-mcp_byterover-store-knowledge` | Programming patterns storage |
| **AI Analysis** | Zen MCP | `mcp_zen_consensus` | Multi-model validation |

## Usage Enforcement Rules

### Rule 1: Build Operations (CRITICAL)

- **ALWAYS** use Gradle MCP for ALL build tasks
- **NEVER** run `./gradlew` directly
- **VERIFY** project structure before executing tasks

### Rule 2: Android Operations

- **ALWAYS** use Android MCP for device operations
- **ALWAYS** use Mobile-MCP for automation and testing
- Manual ADB commands **ONLY** with explicit user approval as last resort

### Rule 3: Code Search

- **ALWAYS** use Claude Context MCP for code search
- **NEVER** manually browse files or use basic search tools
- **ENSURE** codebase is indexed before searching

### Rule 4: External Information

- **ALWAYS** use external search for current information
- **NEVER** rely solely on training data for time-sensitive info
- **USE** appropriate service: Context7/DocFork for docs, DeepWiki for repos

### Rule 5: Memory Management

- **ALWAYS** use OpenMemory for persistent storage
- **ALWAYS** use Byterover for programming patterns
- **NEVER** rely on conversation memory for important information

### Rule 6: Research Validation

- **ALWAYS** use multiple sources (minimum 3 different MCP tools)
- **ALWAYS** feed research results to Zen MCP for consensus
- **FOLLOW** the 10-step research workflow from enhanced-research-strategy.md

## Error Handling and Fallbacks

### Primary Server Unavailable

1. **Check** server status and connectivity
2. **Use** alternative server if available
3. **Manual** execution ONLY as last resort with explicit user approval

### Fallback Priority

- **Build Tasks**: Gradle MCP → Manual with approval only
- **Android Ops**: Android MCP → Manual ADB with approval only
- **Code Search**: Claude Context → Manual file browsing
- **External Info**: DeepWiki → Context7 → DocFork → Tavily → Brave

## Knowledge Integration

### Before Using Any MCP Server

1. **Retrieve** relevant patterns: `mcp_byterover-mcp_byterover-retrieve-knowledge`
2. **Search** project context: `mcp_openmemory_search-memories`
3. **Combine** both knowledge sources for optimal usage

### After Successful Operations

1. **Store** important findings: `mcp_byterover-mcp_byterover-store-knowledge`
2. **Update** module insights: `mcp_byterover-mcp_byterover-update-module`
3. **Save** implementation plans: `mcp_byterover-mcp_byterover-save-implementation-plan`

## Quality Standards

- **Completeness**: 80%+ coverage across tool categories
- **Accuracy**: 90%+ consensus on recommendations
- **Efficiency**: 85%+ automation in workflow
- **Relevance**: 0.3+ scores for knowledge retrieval

## Server Configuration Requirements and Indirect Access

For Byterover MCP, configuration is required for full functionality. Since byterover-mcp is not directly configured in this project, use indirect access via mcp-router if available. Requirements:

- Install the ByteRover extension in IDEs like Cursor or Zed from the marketplace.
- Create an access token at https://byterover.dev.
- Configure MCP in IDE settings to connect to the Byterover server.

If the direct Byterover server is unavailable, route through mcp-router for indirect access. For detailed setup, refer to the Integration with IDEs and Setup section in byterover-rules.md.

---

## Operational Playbooks (merged from WARP MCP Policy)

## Build and Tests

- Use Gradle MCP for quality, build, publish (e.g., `detekt`, `ktlintCheck`, `assembleDebug`).
- Do not install as part of assemble.

## Android Device Operations

- Use Android MCP for adb/logs/package management.
- Use Mobile-MCP to relaunch, tap flows, and capture focused logs.
- Manual ADB only as last resort with explicit approval.

## Code Indexing and Search

- Use Claude Context for indexing/search; Repomix as needed for packing.

## Documentation and Research

- DeepWiki, Context7/DocFork for official docs/examples.
- Tavily/Brave for current info; Exa + Zen for deep research and consensus.

## iOS (when applicable)

- Use SwiftLint MCP for Swift code quality.

## Dependency and Build-Logic Policy

- All modules use the version catalog; no hardcoded versions.
- Root `settings.gradle.kts` acts only as a container (composite builds).
- Prefer OkHttp BOM via `platform(libs.okhttp.bom)`.
- Use `version.ref` for plugin versions (e.g., ktlint).
- Minimal convention plugins in build-logic; all modules apply them.

## Architecture and Navigation

- Feature-based modular design; Clean Architecture.
- Prefer MVI for complex screens.
- Navigation 3 only; validate with latest docs via DeepWiki/Context7.

## Local Run with MCP Tools (examples)

- Build plugins (composite): Gradle MCP `build` in `plugins/`.
- Quality per module: `detekt`, `ktlintCheck` via Gradle MCP.
- Assemble app: `assembleDebug` (via Gradle MCP) — do not install.
- Install + validate on emulator: Android MCP install APK; Mobile-MCP launch and validate UX.

## Notes

- If AGP config fails due to deprecated properties (e.g., `android.enableBuildCache`), remove/fix them first.
- Generated sources (KSP, `build/generated`) are excluded from Detekt.

## Enforcement

- Use MCP servers for ALL supported tasks.
- Only fall back to manual commands if the server is unavailable AND with explicit approval.
