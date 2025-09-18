# Unified Agent Operating Handbook (AGENTS.md)

Version: 1.0  •  Status: Stable  •  Audience: All AI Assistants in this workspace

Purpose: A single, shared operating guide for all AI assistants to act consistently and effectively across this repo. It consolidates our working rules, tool usage (MCP routers), memory policy, planning protocol, answer style, sandbox/approval behaviors, and validation guidance.

## TL;DR

- MCP-first: prefer MCP servers for builds, code search, devices, and research. Manual commands only with explicit approval.
- Android/Kotlin: Clean Architecture, feature modules, MVI, Navigation 3; enforce KtLint/Detekt and Version Catalog.
- Builds: use Gradle MCP for quality and assemble; never run `./gradlew`.
- Code search: index with Claude Context and use `search_code`.
- Memory: OpenMemory for project/user context; ByteRover for programming patterns and plan persistence.
- Research: use Tavily/Brave/Exa for current info; cite official docs.

## 1) Core Principles

- Be concise, direct, and friendly: communicate efficiently; avoid unnecessary verbosity.
- Prioritize actionable guidance: always state assumptions, prerequisites, and next steps.
- Be truthful and precise: don’t guess; verify or ask for clarification.
- Ambition vs. precision: be creative for greenfield tasks; be surgical in existing codebases.
- Ownership: keep going until the task is fully resolved or blocked.
- Safety: protect user privacy; avoid dangerous/destructive actions without explicit approval.

## 2) Conversation & Answer Style

- Tone: collaborative, natural, like a coding partner.
- Preambles before tool calls: 1–2 short sentences describing what’s next; group related actions.
- Progress updates: brief status when work spans multiple steps or calls.
- Ask smart questions: confirm ambiguity, environment assumptions, and required constraints.

Final Answer Formatting (for messages presented to the user):

- Use short section headers only when they improve clarity, e.g., `**Summary**`.
- Prefer short bullet lists (4–6 items), one line per bullet when possible.
- Wrap commands, file paths, env vars, and code identifiers in backticks.
- File references must be clickable paths with optional line/column (1-based). Examples:
  - `app/src/main/java/com/example/githubusers/MainActivity.kt`
  - `app/src/main/java/com/example/githubusers/MainActivity.kt:42`
  - `b/app/build.gradle.kts#L10`
  - `navigation-api/src/main/java/com/example/githubusers/navigation/api/DeepLinkHandler.kt:87`
- Do not include URI schemes like `file://` or `vscode://`.
- Avoid heavy formatting when not needed; keep it scannable and minimal.

## 3) Planning Protocol

When to use a plan (with `update_plan`):

- Multi-step, non-trivial tasks with logical phases or dependencies.
- Ambiguous work benefiting from outlining checkpoints.
- When the user asks for TODOs or multiple deliverables.

Rules:

- Keep steps concise (≤ 7 words each); make them meaningful (not “explore codebase”).
- Exactly one step should be `in_progress` until completion.
- Mark completed steps promptly; update if the approach changes (include rationale).

Examples (good):

1. Create feature module scaffold
2. Define destinations for the feature
3. Implement deep link handler
4. Add ViewModel and UI state
5. Add unit/UI tests

## 4) Sandbox & Approvals

- Default sandbox: workspace-write; network: restricted; approvals: on-request.
- Request approval when:
  - A command needs network access (installs, remote calls) or writes beyond the workspace.
  - Running destructive operations (e.g., `rm`, hard resets) not explicitly requested.
  - A critical command fails due to sandbox and needs escalation.
  - Prefer safer alternatives first; justify escalations succinctly.

## 5) Tooling Protocols (General)

- Think before calling tools; group related actions; keep preambles concise.
- Idempotence: design commands/patches to be safe to re-run.
- Shell usage:
  - Prefer `rg`/`rg --files` for searching; if unavailable, use alternatives.
  - Read files in chunks ≤ 250 lines; expect 256-line/10KB output truncation.
- `apply_patch` to edit files (never use other patch commands). Follow its grammar strictly.
- Avoid reading the same file repeatedly without need. Avoid large/duplicate output.

## 5a) MCP-First Enforcement

- Always prefer MCP servers for supported tasks; manual commands only with explicit approval.
- Build and tests: use Gradle MCP; never run `./gradlew` directly. See `docs/assistants/mcp-guide.md`.
- Code search: use Claude Context indexing and `search_code` (Section 8); ensure index exists first.
- Android operations: use Android MCP and Mobile-MCP; manual ADB only as last resort with approval.
- External info: use Tavily/Brave/Exa; official docs via Context7. Validate with multiple sources when critical.
- Memory and knowledge: OpenMemory for user/project memory; ByteRover for programming patterns and plans.
- Research validation: consider Zen consensus for multi-model validation on complex decisions.

apply_patch essentials:

```diff
*** Begin Patch
*** Add File: path/to/file.txt
+Hello
*** Update File: path/to/existing.kt
@@ fun function():
- old
+ new
 *** End Patch
 ```

## 6) Memory Protocol (openmemory)

Tools: `mcp-router__search-memories`, `mcp-router__add-memory`, `mcp-router__delete-all-memories`.

Rules:

- Search memories for every user request to retrieve relevant context.
- Store new user-specific information on explicit user request or when it clearly benefits future turns (preferences, constraints, key decisions). Confirm if sensitive.
- Content to store: preferences, project conventions, environment constraints, non-sensitive patterns. Avoid secrets or regulated data without explicit consent.
- Redaction & consent: if a user provides sensitive data, confirm storage intent; redact unnecessary details.
- Deletion: honor requests to forget; use the delete-all memory tool if asked.

Examples:

- “Remember my preferred commit style.” → add-memory with concise description.
- “What did I tell you about deployments?” → search-memories with query.

## 7) Byterover MCP Protocols

Note: Byterover memory access requires authentication via the Byterover extension. If not authenticated, proceed offline (document key decisions locally) and queue knowledge storage to sync once authenticated. Always prefer retrieving knowledge before implementation and store high-signal programming facts frequently during plans.

Core tools and strong rules:

- List modules first: `byterover-list-modules` before storing/updating to avoid duplicates.
- Naming for modules: `<repo_name>_<module_name>` only.
- Save implementation plan before coding: `byterover-save-implementation-plan` is REQUIRED prior to multi-step implementation.
- Retrieve knowledge frequently: use `byterover-retrieve-knowledge` during tasks to avoid mistakes; REQUIRED frequently for plan tasks.
- Conflict handling: if memory conflicts occur, ALWAYS display the conflict resolution URL to the user.
- Update progress: use `byterover-update-plan-progress` with `by_task_position` preferred.
- Context recovery: use `byterover-retrieve-active-plans` to continue incomplete work reliably.
- Store programming facts: `byterover-store-knowledge` with complete code snippets in triple backticks exactly as written; include concise, high-signal context only; skip trivial/common knowledge.

Useful flows:

- Start of task: list modules → retrieve knowledge → assess context completeness (`byterover-assess-context-completeness`).
- During implementation: save implementation plan → retrieve knowledge frequently → store knowledge facts (with code) → update plan progress.
- Handbook sync: check handbook existence/sync, perform update with preservation strategy if changes diverge.

## 8) Codebase Context & Search

Claude-context tools:

- `index_codebase`: index absolute path before `search_code`. If already indexed and re-index needed, ask to force.
- `get_indexing_status`, `clear_index`: check or reset index state when needed.
- `search_code`: natural language queries; always provide an absolute path. Filter by extension when helpful.

Reference: `docs/assistants/claude-context-indexing.md`

File reference rules in user messages:

- Use clickable paths (no URI schemes); optional `:line` or `#LlineCcolumn`.
- Always provide stand-alone paths, even if repeating the same file in multiple bullets.

## 9) Git Protocols (git-mcp-server)

- Do not commit unless explicitly requested by the user.
- Use `git_set_working_dir` with absolute path before other git actions in a session.
- Prefer Conventional Commits when committing (if asked): `type(scope): subject` with optional body explaining rationale.
- Use `git_wrapup_instructions` to structure finalization (diff review, update docs, commit, optional tag) when asked.
- Use `git log`/`git blame` to understand history/root cause when necessary; do not overuse.
- Avoid destructive operations (`reset --hard`, forced pushes) without explicit approval.

## 10) Web & Docs Research

Network-aware tools must respect sandbox and approvals.

- Prefer official docs for Android/Kotlin:
  - Android Developers (architecture, coroutines, navigation, paging)
  - OkHttp (HTTPS, certificate pinning)
  - Kotlin language and coroutines
- Library docs: use Context7 to resolve and fetch documentation.
- General research: Brave/Tavily for current info; cite sources concisely.
- Real-world patterns: `searchGitHub` (grep-remote) with literal code or `(?s)` regex.

Enhanced workflow: `docs/assistants/enhanced-research-strategy.md`.

## 11) Platform Automation

- Android MCP: `get_packages`, `get_uilayout`, `get_screenshot`, `execute_adb_shell_command`, etc. Use for device tasks; prefer listing and inspecting before tapping/typing. Manual ADB only as last resort with explicit approval.
- Mobile MCP: `mobile_list_elements_on_screen`, tap/long-press by coordinates, open URLs, type keys; always discover UI elements before actions; avoid blind taps when possible.

MCP-first enforcement: prefer MCP servers for supported tasks; manual commands only with explicit approval. See `docs/assistants/mcp-guide.md`.

## 11a) Android Kotlin Development

- Architecture: Clean Architecture, feature-based modules, MVI for complex screens, Navigation component only (aka "Navigation 3" in our docs).
- Dependency policy: use Version Catalog; no hardcoded versions; OkHttp BOM via `platform(libs.okhttp.bom)`; plugin versions via `version.ref`.
- Code quality: KtLint and Detekt enforced; no wildcard imports; KDoc for public APIs; EditorConfig governs formatting.
- Build: Use Gradle MCP for `detekt`, `ktlintCheck`, `assembleDebug`; never run `./gradlew` directly.
- Debugging: Prefer Android MCP for logcat and device info; Mobile-MCP for launching and screenshots. See `docs/assistants/android-debugging.md`.
- Performance: minimize recomposition; use lazy lists; use `remember`/`derivedStateOf`; cache flows in ViewModels with `cachedIn` and proper coroutine scopes.
- Preferences & storage: Use Jetpack DataStore for non-sensitive preferences; use EncryptedSharedPreferences (Jetpack Security) for secrets.
- Security & privacy: Implement OkHttp certificate pinning with a pinset (include backup pins) and rotation strategy; validate inputs and API responses; token storage/rotation; consent and deletion support.
- References: `docs/assistants/android-standards.md`, `docs/assistants/kotlin-style.md`, `.cursor/rules/android.mdc` (pointer), `.cursor/rules/kotlin.mdc` (pointer).

## 11b) Build System & Version Catalog

- Single source of truth: use `catalog/gradle/libs.versions.toml` for ALL versions; never hardcode in module `build.gradle.kts`.
- Convention plugins: apply project plugins instead of duplicating build logic. Typical feature module:

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
  implementation(platform(libs.okhttp.bom))
}
```

- Root as container: this repo uses a composite-build topology where each module (e.g., `app`, `core-*`, `feature-*`, `navigation-*`) is a standalone Gradle build included via `includeBuild("<module>")` in the root `settings.gradle.kts`. Avoid custom logic in the root beyond `includeBuild` and optional dependency substitution.
- Quality tasks: run `detekt` and `ktlintCheck` via Gradle MCP before PRs.
- References: `docs/BUILD_SYSTEM.md`, `docs/BUILD-CONVENTIONS.md`, `docs/quality/detekt-usage.md`.
 - Catalog alias tips: prefer short, stable aliases; group families (e.g., `okhttp`, `okhttp.logging`); use Platforms/BOMs for aligned families.

## 11c) New Module Checklist

1. Create module folder and minimal source structure.
1. Naming: use `core-*`, `feature-*`, `navigation-*` prefixes per conventions.
1. Add to `settings.gradle.kts` and sync.
1. Apply convention plugins (library, hilt, compose as needed). Also apply `githubusers.dependency.update` so `dependencyUpdates` exists in the module.
1. Add dependencies via Version Catalog only; align HTTP stack with OkHttp BOM when used.
1. Navigation ownership: add destinations + deep link handler per template.
1. Register handler with Hilt multibindings if required by navigation API.
1. Add unit tests for deep link parsing and basic navigation flows.
1. Generate Detekt baseline if needed and fix formatting issues.
1. Verify module tasks pass via Gradle MCP: `:module:lint` (or covered by `lintAll`), `:module:detekt`, `:module:ktlintCheck`, `:module:test`, `:module:assemble`, and `:module:dependencyUpdates`.
1. Build and static analysis using Gradle MCP (no direct `./gradlew`).
1. Document module purpose in a short README if substantial.

References:

- Feature template: `docs/FEATURE_DESTINATION_TEMPLATE.md`
- Developer workflow: `docs/DEVELOPER_GUIDE.md`
- Navigation architecture: `docs/NAVIGATION_ARCHITECTURE.md`

## 11d) Gradle Composite Build System

- Structure: root project + composite build for convention plugins under `plugins/` and version catalog under `catalog/`.
- Root role: `settings.gradle.kts` acts only as a container (no custom logic); include modules and `includeBuild("plugins")`.
- Version catalog: wired in `dependencyResolutionManagement` to `catalog/gradle/libs.versions.toml`; all versions come from here.
- Convention plugins: declared in `plugins/` and applied by ID in module `build.gradle.kts` to standardize Android, Compose, Hilt, quality, etc.

Example `settings.gradle.kts` (key parts used in this repo):

```kts
// Root settings.gradle.kts (excerpt)
rootProject.name = "githubusers"

// Version catalog and convention plugins as composite builds
includeBuild("catalog")
includeBuild("plugins")

// Modules included as composite builds (standalone Gradle builds)
includeBuild("app")
includeBuild("core-common")
includeBuild("core-mvi")
includeBuild("core-ui")
includeBuild("core-networking")
includeBuild("core-storage")
includeBuild("navigation-api")
includeBuild("navigation-impl")
includeBuild("feature-users")
includeBuild("feature-search")
includeBuild("feature-settings")
```

Example feature module `build.gradle.kts` (composite + catalog usage):

```kts
plugins {
  id("githubusers.android.library")
  id("githubusers.android.compose")
  id("githubusers.android.hilt")
}

android { /* minimal, standardized by convention plugins */ }

dependencies {
  implementation(project(":core-common"))
  implementation(project(":core-ui"))
  implementation(project(":navigation-api"))

  // Version alignment (align via BOMs as needed)
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.ktor.bom))
  implementation(platform(libs.okhttp.bom))
}
```

Dependency patterns:

- Prefer `implementation`; use `api` only when intentionally exposing transitive APIs.
- Align families via BOMs (e.g., OkHttp) and keep versions in the catalog; no hardcoded versions.

Performance settings (recommended in `gradle.properties`):

- `org.gradle.caching=true`, `org.gradle.parallel=true`, `org.gradle.configuration-cache=true`.
- Use build scans locally for profiling when needed.

Workflow tips:

- Plugin development loop: edits in `plugins/` are picked up immediately via `includeBuild("plugins")`.
- Adding a module: create directory, include in `settings.gradle.kts`, apply convention plugins, add catalog-backed deps, run quality tasks via Gradle MCP.
- Build & quality: run `lint` (`lintAll` when available) before builds to surface errors/warnings, then run `detekt`, `ktlintCheck`, and assemble via Gradle MCP; do not call `./gradlew` directly.

References: `docs/BUILD_SYSTEM.md`, `docs/BUILD-CONVENTIONS.md`.

## 11e) Build Quick Start (Gradle MCP tasks)

- Quality first: run `lintAll` (or the relevant `:module:lint` task) to catch errors and warnings, followed by `detekt` and `ktlintCheck` across modules.
- Assemble app: `:app:assembleDebug` (do not install as part of assemble).
- Tests (if configured): `:app:testDebugUnitTest`, module-specific `:module:test`.
- Plugins (composite): changes in `plugins/` are picked up via `includeBuild("plugins")`.
- Never run `./gradlew` directly; use Gradle MCP to execute these tasks.

## 11f) CI Guidance 2025

- Use the dedicated `dependency-submission.yml` workflow for dependency graph submission; do not duplicate submission inside `ci.yml` to avoid double runs.
- Ensure each module that participates in dependency checks applies `githubusers.dependency.update` so `dependencyUpdates` can run per-module (or wire a root aggregator task that depends on `:module:dependencyUpdates`).
- Prefer matrix strategy for code-quality checks (`detekt`, `ktlint`, `androidLint`), with cache read-only for PR contexts when appropriate.
- Enable Gradle Build Scans (ensure terms are accepted via gradle.properties or action inputs).
- JDK 21: run workflows on Temurin 21; keep wrapper validation enabled; use configuration cache and build cache.
- Artifact hygiene: include run number in artifact names, set retention days, use `if-no-files-found: ignore`, and set a reasonable compression level.
- See `.github/workflows/README-IMPROVEMENTS.md` for details and rationale.

## 16) References

- Android architecture (official): https://developer.android.com/topic/architecture
- Coroutines best practices: https://developer.android.com/kotlin/coroutines/coroutines-best-practices
- Lifecycle-aware coroutines: https://developer.android.com/topic/libraries/architecture/coroutines
- Navigation component (official): https://developer.android.com/guide/navigation
- Navigation with Compose: https://developer.android.com/develop/ui/compose/navigation
- Paging library (API): https://developer.android.com/reference/kotlin/androidx/paging/package-summary
- OkHttp certificate pinning (official): https://square.github.io/okhttp/5.x/okhttp/okhttp3/-certificate-pinner/

Note on security: networking uses Ktor with the OkHttp engine. Implement certificate pinning via OkHttp `CertificatePinner` in the engine configuration. See `docs/KTOR_AUTH_PLUGIN_IMPLEMENTATION.md` for integration guidance.

## 12) Validation & Testing

- If the repo has tests/build: leverage them to validate changes.
- Strategy: test smallest scope first (code you changed) before broader tests.
- Approval-aware behavior:
  - Interactive modes (on-request/untrusted): propose running tests and wait for confirmation.
  - Non-interactive (never/on-failure): proactively run tests to ensure completion.
- Do not fix unrelated failures; call them out succinctly if encountered.

## 12a) Zen Analysis Tools

- Planner: break down complex tasks interactively; useful for multi-phase work.
- Code Review: structured quality/security/performance review when requested or before critical merges.
- Debug: hypothesis-driven root-cause analysis for tricky issues.
- Refactor: identify code smells and decomposition opportunities.
- Precommit: validate repository state and change impact before committing.
- ThinkDeep: multi-stage reasoning for complex architecture decisions.
- Consensus: consult multiple models for high-stakes decisions and synthesize.
- Challenge: prevent reflexive agreement; apply for critical evaluation of assumptions.
- Chat: collaborate and explain reasoning steps and trade-offs.

## 12b) Static Analysis Notes

- Exclude generated sources (e.g., `**/build/**`, `**/build/generated/**`, KSP outputs) from Detekt/KtLint to reduce noise.
- Maintain module Detekt baselines in `config/detekt/baseline.xml` when needed and clean them up regularly.

## 13) Privacy & Safety

- Do not persist secrets or regulated data without explicit user consent.
- Minimize exposure: store only what benefits future turns.
- Be explicit about approvals when performing networked or destructive actions.
- Respect system boundaries and environment constraints.

## 14) Examples & Templates

Preamble examples:

- “I’ve explored the repo; now checking API routes.”
- “Next, I’ll patch config and update tests.”
- “I’m about to scaffold CLI commands and helpers.”

Plan examples (good):

1. Create feature module scaffold
2. Define destinations for the feature
3. Implement deep link handler
4. Add ViewModel and UI state
5. Add unit/UI tests

apply_patch template:

```diff
*** Begin Patch
*** Add File: hello.txt
+Hello, world!
*** Update File: src/main.kt
@@ fun greet():
- println("Hi")
+ println("Hello, world!")
*** End Patch
```

File reference examples for answers:

- `app/src/main/java/com/example/githubusers/MainActivity.kt:42`
- `b/app/build.gradle.kts#L10`
- `navigation-api/src/main/java/com/example/githubusers/navigation/api/DeepLinkHandler.kt:87`

## 15) Change Management

- Update this document when tool rules or workflows change.
- Keep changes minimal and focused; describe rationale in commit body if committing.
- Prefer PR-based updates for peer visibility when collaborating.

---
This handbook unifies how all assistants behave here. When in doubt, ask concise clarifying questions, propose a small plan, and proceed with minimal, high-impact steps.
