# AI Assistant Rules Documentation

## Overview

This directory contains the canonical documentation for all AI assistants working on this project. All assistants (Claude, Cursor, Augment, KiloCode, Gemini, Trae) must follow these rules.

## Canonical Documents

1. **[warp-mcp-policy.md](./warp-mcp-policy.md)** - MCP-first development workflow
1. **[mcp-guide.md](./mcp-guide.md)** - MCP server selection and usage
1. **[enhanced-research-strategy.md](./enhanced-research-strategy.md)** - 10-step research workflow
1. **[multi-ai-consultation.md](./multi-ai-consultation.md)** - Multi-AI consensus rules
1. **[android-standards.md](./android-standards.md)** - Android development standards
1. **[kotlin-style.md](./kotlin-style.md)** - Kotlin code style guide
1. **[android-debugging.md](./android-debugging.md)** - MCP-based debugging
1. **[byterover-rules.md](./byterover-rules.md)** - ByteRover workflows and persistence
1. **[dev-environment.md](./dev-environment.md)** - Environment setup
1. **[claude-guide.md](./claude-guide.md)** - Claude-specific notes

## Sync Locations

| Assistant | Location | Files |
|-----------|----------|-------|
| Cursor | `.cursor/rules/` | `*.mdc` files |
| Augment | `.augment/rules/` | `*.md` files with YAML frontmatter |
| KiloCode | `.kilocode/rules/` | `*.md` files with globs |
| Gemini | `.gemini/` | `settings.json` |
| Trae | `.trae/rules/` | `project_rules.md` |
| Claude | Root | `CLAUDE.md` (pointer to docs) |
| WARP | Root | `WARP.md` (pointer to docs) |

## Maintenance

### Updating Rules

1. **ALWAYS** update canonical docs in `docs/assistants/` first
1. Run sync script: `./scripts/sync-assistant-rules.sh`
1. Verify with: `./scripts/verify-assistant-sync.sh`
1. Commit all changes together

### Adding New Rules

1. Create new canonical doc in `docs/assistants/`
1. Update this README
1. Add to sync script mapping
1. Run sync and verify

### Verification

Run verification to ensure all assistants have matching content:

```bash
```text

```text
```text

./scripts/verify-assistant-sync.sh

```

## Critical Policies

### MCP-First (MANDATORY)


- **ALWAYS** use Gradle MCP for builds
- **NEVER** run `./gradlew` directly
- Android MCP for device operations
- Mobile-MCP for UI automation
- Manual commands ONLY with explicit approval

### Code Quality (MANDATORY)


- **NO wildcard imports** - Single imports only
- KtLint and Detekt enforcement
- Version catalog only
- OkHttp BOM via `platform(libs.okhttp.bom)`
- Plugin versions via `version.ref`

### Architecture (MANDATORY)


- Clean Architecture with MVI
- **Navigation 3 ONLY**
- Feature-based modularization
- `settings.gradle.kts` as container only

### Knowledge Management (MANDATORY)


- ByteRover for programming patterns
- OpenMemory for project context
- **CRITICAL PLAN PERSISTENCE RULE** must be followed

## Support

For questions or updates, consult the canonical documentation first, then check assistant-specific configurations.
