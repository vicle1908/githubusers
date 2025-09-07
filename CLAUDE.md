# Claude Assistant Guide

## Overview

This guide provides Claude-specific configuration for the GitHub Users Android project.

## Core Policies

All core policies are defined in canonical documentation:

- **MCP-First Workflow**: See [docs/assistants/warp-mcp-policy.md](docs/assistants/warp-mcp-policy.md)
- **MCP Server Usage**: See [docs/assistants/mcp-guide.md](docs/assistants/mcp-guide.md)
- **Research Strategy**: See [docs/assistants/enhanced-research-strategy.md](docs/assistants/enhanced-research-strategy.md)
- **Android Standards**: See [docs/assistants/android-standards.md](docs/assistants/android-standards.md)
- **Kotlin Style**: See [docs/assistants/kotlin-style.md](docs/assistants/kotlin-style.md)
- **ByteRover Rules**: See [docs/assistants/byterover-rules.md](docs/assistants/byterover-rules.md)

## Claude-Specific Configuration

### Project Context

- GitHub Users Android App
- Clean Architecture with MVI
- Navigation 3 with deep links
- Feature-based modularization

### Available MCP Servers

Claude has access to all 14 MCP servers listed in [docs/assistants/mcp-guide.md](docs/assistants/mcp-guide.md)

### Enforcement Priorities

1. **Gradle MCP** for ALL builds - NEVER use ./gradlew
2. **Android MCP** for device operations
3. **Mobile-MCP** for UI automation
4. **ByteRover** for knowledge management
5. Manual commands ONLY with explicit approval

## Quick Reference

### Build Tasks
```bash
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
```

### Android Operations
```bash
mcp_android_execute_adb_shell_command("logcat -d | tail -20")
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers.debug")
```

### Knowledge Management
```bash
mcp_byterover-mcp_byterover-retrieve-knowledge("query")
mcp_byterover-mcp_byterover-save-implementation-plan(plan)
```

## Remember

- ALWAYS follow the CRITICAL PLAN PERSISTENCE RULE
- NEVER skip the 10-step research workflow
- ALWAYS use MCP servers for supported tasks

## See Also

For detailed documentation, see [docs/assistants/](docs/assistants/) directory.
