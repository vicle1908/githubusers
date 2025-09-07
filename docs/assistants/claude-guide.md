# Claude Assistant Guide

## Overview

This guide provides Claude-specific configuration while referencing canonical documentation for shared policies.

## Core Policies

All core policies are defined in canonical documentation:

- **MCP-First Workflow**: See [warp-mcp-policy.md](./warp-mcp-policy.md)
- **MCP Server Usage**: See [mcp-guide.md](./mcp-guide.md)
- **Research Strategy**: See [enhanced-research-strategy.md](./enhanced-research-strategy.md)
- **Android Standards**: See [android-standards.md](./android-standards.md)
- **Kotlin Style**: See [kotlin-style.md](./kotlin-style.md)
- **ByteRover Rules**: See [byterover-rules.md](./byterover-rules.md)

## Claude-Specific Configuration

### Project Context

- GitHub Users Android App
- Clean Architecture with MVI
- Navigation 3 with deep links
- Feature-based modularization

### Available MCP Servers

Claude has access to all 14 MCP servers listed in [mcp-guide.md](./mcp-guide.md)

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
