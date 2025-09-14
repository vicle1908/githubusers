---
alwaysApply: true
---
# MCP Guide (Pointer)

Canonical policy lives in `docs/assistants/mcp-guide.md`. This rule is a pointer to avoid duplication.
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
