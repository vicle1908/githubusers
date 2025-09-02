# WARP development workflow (MCP-first)

Strict MCP-first rules (must follow)
- Always use MCP servers instead of manual commands.
- Knowledge prep (before any task):
  - Retrieve related context with ByteRover and search OpenMemory for project decisions.
- Knowledge capture (after successful tasks):
  - Store critical patterns in ByteRover and persist project context in OpenMemory.

Preferred MCP usage
- Build and tests: use Gradle MCP Server for all tasks (quality, build, publish). Do not run ./gradlew directly.
  - Examples:
    - detekt, ktlintCheck per included build
    - assembleDebug for app (do not install)
- Android device ops: use Android MCP (adb, logs, package management).
- Mobile automation: use Mobile-MCP to relaunch the app, tap Connect, and capture focused logs.
- Code indexing and search: use Claude Context (semantic indexing) and Repomix when needed.
- External documentation and research:
  - DeepWiki, Context7, DocFork for official docs and examples.
  - Tavily/Brave for current web info; Exa + multi-AI consensus for deep research.
- iOS quality: use SwiftLint MCP for Swift projects.

Quality enforcement (Android + backend Kotlin)
- KtLint (format and style) via convention plugin githubusers.quality.ktlint
- Detekt (static analysis) via convention plugin githubusers.quality.detekt, aligned with ktlint wrapper
- Aggregator plugin githubusers.quality applies both conventions
- No wildcard imports; single imports enforced via .editorconfig and Detekt rules

How to run locally with MCP tools
- Build plugins (composite build): run Gradle MCP task build in plugins/
- Quality per module:
  - app/: detekt, ktlintCheck (via Gradle MCP)
  - feature-*/ and navigation-*/: detekt, ktlintCheck (via Gradle MCP)
- Assemble app: app/: assembleDebug (via Gradle MCP) — do not install
- Install + validate on emulator:
  - Use Android MCP to install the built APK
  - Use Mobile-MCP to launch the app, tap Connect, and capture focused logs

Dependency and build-logic policy
- All modules must use the version catalog; no hardcoded versions.
- Root settings.gradle.kts acts only as a container to declare the composite build.
- Prefer platform(libs.okhttp.bom) for OkHttp and use version.ref for plugin versions (e.g., ktlint).
- Scaffold minimal convention plugins in build-logic and ensure all modules apply them.

Android architecture and navigation policy
- Feature-based modular design with Clean Architecture; prefer MVI for complex screens.
- Navigation 3 only; add dependencies carefully and validate with latest docs via DeepWiki/Context7.

Notes
- If AGP configuration fails due to deprecated Gradle properties (e.g., android.enableBuildCache), remove or fix them before running.
- Generated sources (KSP, build/generated) are excluded from Detekt.

Enforcement and fallback
- Use MCP servers for all supported tasks. Only fall back to manual commands if the relevant MCP server is unavailable and with explicit approval.

