---
trigger: always_on
title: "Project Development Standards"
description: "Comprehensive guide for AI assistants working with this Android project"
tags: ["android", "kotlin", "mcp", "development", "standards"]
version: "1.0"
schema: "1.0"
---

# Project Development Standards

This document consolidates all the key rules and standards for working with this Android project.

## Core Principles

1. **MCP-First with CLI Preference**: While the general policy is to prefer MCP servers, we have a specific preference for using CLI tools (gh, git, gradle) directly through Desktop Commander to reduce context usage.

2. **Clean Architecture**: Follow feature-based modular design with clear separation of concerns.

3. **Consistency**: Maintain consistent coding, testing, and documentation practices across all modules.

## Tool Usage Policy

### CLI Tools Preference

We prefer using CLI tools directly through Desktop Commander instead of their MCP counterparts:

1. **GitHub CLI (gh)** - Use direct `gh` commands instead of `github-mcp-server`
2. **Git CLI** - Use direct `git` commands instead of `git-mcp-server` 
3. **Gradle CLI** - Use direct `gradle`/`./gradlew` commands instead of `gradle-mcp-server`

Implementation:
```
mcp_mcp-router_start_process
```

Benefits:
- Reduced context usage compared to MCP tools
- More direct control over command execution
- Faster execution in some cases
- Consistent interface through Desktop Commander
