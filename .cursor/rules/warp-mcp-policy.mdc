# WARP Development Workflow (MCP-First)

## Strict MCP-First Rules (MUST FOLLOW)

- **ALWAYS** use MCP servers instead of manual commands
- Knowledge prep (before any task):
  - Retrieve related context with ByteRover and search OpenMemory for project decisions
- Knowledge capture (after successful tasks):
  - Store critical patterns in ByteRover and persist project context in OpenMemory

## Preferred MCP Usage

### Build and Tests


- **MANDATORY**: Use Gradle MCP Server for all tasks (quality, build, publish)
- **NEVER** run `./gradlew` directly
- Examples:
  - `detekt`, `ktlintCheck` per included build
  - `assembleDebug` for app (do not install)

### Android Device Operations


- Use Android MCP (adb, logs, package management)
- Use Mobile-MCP to relaunch the app, tap Connect, and capture focused logs
- Manual ADB commands are **ONLY** allowed as last resort with explicit user approval

### Code Indexing and Search


- Use Claude Context (semantic indexing) and Repomix when needed

### External Documentation and Research


- DeepWiki, Context7, DocFork for official docs and examples
- Tavily/Brave for current web info
- Exa + Zen MCP multi-AI consensus for deep research

### iOS Quality (when applicable)


- Use SwiftLint MCP for Swift projects

## Quality Enforcement (Android + Backend Kotlin)

- **KtLint** (format and style) via convention plugin `githubusers.quality.ktlint`
- **Detekt** (static analysis) via convention plugin `githubusers.quality.detekt`
- Aggregator plugin `githubusers.quality` applies both conventions
- **No wildcard imports** - Single imports enforced via `.editorconfig` and Detekt rules

## How to Run Locally with MCP Tools

### Build Plugins (Composite Build)


- Run Gradle MCP task `build` in `plugins/`

### Quality per Module


- `app/`: `detekt`, `ktlintCheck` (via Gradle MCP)
- `feature-*/` and `navigation-*/`: `detekt`, `ktlintCheck` (via Gradle MCP)

### Assemble App


- `app/`: `assembleDebug` (via Gradle MCP) — do not install

### Install + Validate on Emulator


- Use Android MCP to install the built APK
- Use Mobile-MCP to launch the app, tap Connect, and capture focused logs

## Dependency and Build-Logic Policy

- All modules **MUST** use the version catalog; no hardcoded versions
- Root `settings.gradle.kts` acts **ONLY** as a container to declare the composite build
- Prefer `platform(libs.okhttp.bom)` for OkHttp
- Use `version.ref` for plugin versions (e.g., ktlint)
- Scaffold minimal convention plugins in build-logic and ensure all modules apply them

## Android Architecture and Navigation Policy

- Feature-based modular design with Clean Architecture
- Prefer MVI for complex screens
- **Navigation 3 ONLY** - add dependencies carefully and validate with latest docs via DeepWiki/Context7

## Notes

- If AGP configuration fails due to deprecated Gradle properties (e.g., `android.enableBuildCache`), remove or fix them before running
- Generated sources (KSP, `build/generated`) are excluded from Detekt

## Enforcement and Fallback

- Use MCP servers for **ALL** supported tasks
- Only fall back to manual commands if the relevant MCP server is unavailable **AND** with explicit approval
