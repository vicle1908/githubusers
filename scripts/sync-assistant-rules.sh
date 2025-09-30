#!/bin/bash

# Assistant Rules Sync Script
# This script syncs canonical documentation to all AI assistants

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
CANONICAL_DIR="$PROJECT_ROOT/docs/assistants"

echo "🚀 Starting AI Assistant Rules Sync..."

# Create remaining canonical files
echo "📝 Creating remaining canonical documentation..."

# Create dev-environment.md
cat > "$CANONICAL_DIR/dev-environment.md" << 'EOF'
# Development Environment Setup

## Required Environment Variables

### MCP Server API Keys

Set these environment variables in your shell profile:

```bash
# Core MCP Services
export OPENAI_API_KEY="your-openai-key"
export GEMINI_API_KEY="your-gemini-key"
export XAI_API_KEY="your-xai-key"
export ANTHROPIC_API_KEY="your-anthropic-key"

# Search and Documentation
export TAVILY_API_KEY="your-tavily-key"
export BRAVE_API_KEY="your-brave-key"
export EXA_API_KEY="your-exa-key"
export CONTEXT7_API_KEY="your-context7-key"

# Knowledge and Memory
export MILVUS_TOKEN="your-milvus-token"
export OPENMEMORY_API_KEY="your-openmemory-key"

# GitHub Integration
export GITHUB_PERSONAL_ACCESS_TOKEN="your-github-token"

# Android Development
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$PATH:$ANDROID_HOME/emulator:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools"
```

## MCP Server Configuration

### Required MCP Servers

1. **Gradle MCP Server** - Build operations (MANDATORY)
2. **Android MCP Server** - Device operations
3. **Mobile-MCP Server** - UI automation
4. **Claude Context** - Code search
5. **ByteRover MCP** - Knowledge management
6. **OpenMemory** - Persistent storage
7. **DeepWiki** - Repository documentation
8. **Context7/DocFork** - Library documentation
9. **Tavily/Brave** - Web search
10. **Zen MCP** - Multi-AI consultation

## Local Setup

1. Export all required environment variables
2. Install MCP servers via npm/pip as needed
3. Configure assistant settings to use environment variables
4. Never commit actual keys to version control

## CI/CD Setup

Add secrets to your CI/CD platform:
- GitHub Actions: Repository Settings → Secrets
- GitLab CI: Project Settings → CI/CD → Variables
- Jenkins: Credentials → Global credentials

## Security Best Practices

1. Use environment variables, never hardcode secrets
2. Rotate keys regularly
3. Use different keys for development and production
4. Add `.env` files to `.gitignore`
5. Use secret management tools in production
EOF

# Create claude-guide.md
cat > "$CANONICAL_DIR/claude-guide.md" << 'EOF'
# Claude Assistant Guide

## Overview

This guide provides Claude-specific configuration while referencing canonical documentation for shared policies.

## Core Policies

All core policies are defined in canonical documentation:

- **MCP-First Workflow**: See [mcp-guide.md](./mcp-guide.md)
- **MCP Server Usage**: See [mcp-guide.md](./mcp-guide.md)
- **Research Strategy**: See [enhanced-research-strategy.md](./enhanced-research-strategy.md)
- **Android Standards**: See [android-standards.md](./android-standards.md)
- **Kotlin Style**: See [kotlin-style.md](./kotlin-style.md)

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
4. **OpenMemory** for knowledge management
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
mcp_openmemory_search-memories("query")
mcp_openmemory_add-memory("key insight")
```

## Remember

- ALWAYS follow the CRITICAL PLAN PERSISTENCE RULE
- NEVER skip the 10-step research workflow
- ALWAYS use MCP servers for supported tasks
EOF

# Create README.md for docs/assistants
cat > "$CANONICAL_DIR/README.md" << 'EOF'
# AI Assistant Rules Documentation

## Overview

This directory contains the canonical documentation for all AI assistants working on this project. All assistants (Claude, Cursor, Augment, KiloCode, Gemini, Trae) must follow these rules.

## Canonical Documents

1. **[mcp-guide.md](./mcp-guide.md)** - MCP-first policy, server selection and usage
3. **[enhanced-research-strategy.md](./enhanced-research-strategy.md)** - 10-step research workflow
4. **[multi-ai-consultation.md](./multi-ai-consultation.md)** - Multi-AI consensus rules
5. **[android-standards.md](./android-standards.md)** - Android development standards
6. **[kotlin-style.md](./kotlin-style.md)** - Kotlin code style guide
7. **[android-debugging.md](./android-debugging.md)** - MCP-based debugging
9. **[dev-environment.md](./dev-environment.md)** - Environment setup
10. **[claude-guide.md](./claude-guide.md)** - Claude-specific notes

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
2. Run sync script: `./scripts/sync-assistant-rules.sh`
3. Verify with: `./scripts/verify-assistant-sync.sh`
4. Commit all changes together

### Adding New Rules

1. Create new canonical doc in `docs/assistants/`
2. Update this README
3. Add to sync script mapping
4. Run sync and verify

### Verification

Run verification to ensure all assistants have matching content:
```bash
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
EOF

echo "✅ Canonical documentation created"

# Now sync to all assistants
echo "🔄 Syncing to assistant directories..."

# Sync to Cursor (.mdc files)
echo "  📋 Syncing to Cursor..."
# Preserve existing MDC frontmatter if present, then append canonical body
for file in android-standards kotlin-style mcp-guide enhanced-research-strategy multi-ai-consultation android-debugging; do
  case $file in
    android-standards)
      src="$CANONICAL_DIR/android-standards.md"
      dest="$PROJECT_ROOT/.cursor/rules/android.mdc"
      ;;
    kotlin-style)
      src="$CANONICAL_DIR/kotlin-style.md"
      dest="$PROJECT_ROOT/.cursor/rules/kotlin.mdc"
      ;;
    *)
      src="$CANONICAL_DIR/$file.md"
      dest="$PROJECT_ROOT/.cursor/rules/$file.mdc"
      ;;
  esac

  if [ -f "$dest" ]; then
    # Extract frontmatter block if present (first YAML block delimited by ---)
    awk 'NR==1 && $0=="---"{in_fm=1; print; next} in_fm{print; if($0=="---"){in_fm=0; exit}}' "$dest" > /tmp/cursor_mdc_frontmatter.txt || true
    if [ -s /tmp/cursor_mdc_frontmatter.txt ] && [ "$(grep -c '^---$' /tmp/cursor_mdc_frontmatter.txt)" -eq 2 ] && [ "$(wc -l < /tmp/cursor_mdc_frontmatter.txt)" -ge 3 ]; then
      cat /tmp/cursor_mdc_frontmatter.txt > "$dest.tmp"
      # Append canonical body, stripping ONLY top-of-file frontmatter if present (keep in-body horizontal rules)
      perl -0777 -pe 's/^\x{FEFF}?---\s*\n.*?\n---\s*\n//s' "$src" >> "$dest.tmp"
      mv "$dest.tmp" "$dest"
    else
      # No meaningful frontmatter: write canonical body directly (strip only top-of-file frontmatter if present)
      perl -0777 -pe 's/^\x{FEFF}?---\s*\n.*?\n---\s*\n//s' "$src" > "$dest"
    fi
    rm -f /tmp/cursor_mdc_frontmatter.txt
  else
    # Destination missing: write canonical body (strip only top-of-file frontmatter if present)
    mkdir -p "$(dirname "$dest")"
    perl -0777 -pe 's/^\x{FEFF}?---\s*\n.*?\n---\s*\n//s' "$src" > "$dest"
  fi

done

# Sync to Augment (preserve frontmatter)
echo "  📋 Syncing to Augment..."
for file in android-standards kotlin-style mcp-guide enhanced-research-strategy multi-ai-consultation; do
    # Map file names correctly
    case $file in
        android-standards) src="$CANONICAL_DIR/android-standards.md" ;;
        kotlin-style) src="$CANONICAL_DIR/kotlin-style.md" ;;
        *) src="$CANONICAL_DIR/$file.md" ;;
    esac
    dest="$PROJECT_ROOT/.augment/rules/$file.md"
    if [ -f "$dest" ]; then
        # Extract frontmatter
        awk '/^---$/,/^---$/' "$dest" > /tmp/frontmatter.txt
        # If frontmatter is non-empty (contains more than just the markers), preserve it
        if [ -s /tmp/frontmatter.txt ] && [ $(grep -c '^---$' /tmp/frontmatter.txt) -eq 2 ] && [ $(wc -l < /tmp/frontmatter.txt) -ge 3 ]; then
            cat /tmp/frontmatter.txt > "$dest.tmp"
            echo "" >> "$dest.tmp"
            cat "$src" >> "$dest.tmp"
            mv "$dest.tmp" "$dest"
        else
            # No meaningful frontmatter: write canonical content directly
            cat "$src" > "$dest"
        fi
    else
        cat "$src" > "$dest"
    fi
done

# Sync to KiloCode (preserve globs)
echo "  📋 Syncing to KiloCode..."
for file in android-standards kotlin-style mcp-guide enhanced-research-strategy; do
    # Map file names correctly
    case $file in
        android-standards) src="$CANONICAL_DIR/android-standards.md" ;;
        kotlin-style) src="$CANONICAL_DIR/kotlin-style.md" ;;
        *) src="$CANONICAL_DIR/$file.md" ;;
    esac
    dest="$PROJECT_ROOT/.kilocode/rules/$file.md"
    if [ -f "$dest" ]; then
        # Extract frontmatter with globs
        awk '/^---$/,/^---$/' "$dest" > /tmp/frontmatter.txt
        # If frontmatter contains globs or non-empty YAML, preserve it
        if [ -s /tmp/frontmatter.txt ] && [ $(grep -c '^---$' /tmp/frontmatter.txt) -eq 2 ] && [ $(wc -l < /tmp/frontmatter.txt) -ge 3 ]; then
            cat /tmp/frontmatter.txt > "$dest.tmp"
            echo "" >> "$dest.tmp"
            cat "$src" >> "$dest.tmp"
            mv "$dest.tmp" "$dest"
        else
            # No meaningful frontmatter: write canonical content directly
            cat "$src" > "$dest"
        fi
    else
        cat "$src" > "$dest"
    fi
done

# Update Trae project rules
echo "  📋 Updating Trae rules..."
cat > "$PROJECT_ROOT/.trae/rules/project_rules.md" << 'EOF'
# Project Assistant Rules

# Memory
OpenMemory is the approved persistent store. Use `mcp_router__search-memories` before major work and `mcp_router__add-memory` only when persistence is required by the user.

# Planning
Use the built-in planning tool (`update_plan`) for multi-step tasks.
EOF

# Create root pointer files
echo "📍 Creating root pointer files..."

cat > "$PROJECT_ROOT/WARP.md" << 'EOF'
# WARP Development Workflow

This file has been moved to maintain consistency.

Please see: [docs/assistants/mcp-guide.md](docs/assistants/mcp-guide.md)

All assistant rules are now centralized in `docs/assistants/`
EOF

cat > "$PROJECT_ROOT/CLAUDE.md" << 'EOF'
# Claude Assistant Guide

This file has been moved to maintain consistency.

Please see: [docs/assistants/claude-guide.md](docs/assistants/claude-guide.md)

All assistant rules are now centralized in `docs/assistants/`
EOF

echo "✅ Sync complete!"
echo ""
echo "📊 Summary:"
echo "  - Created canonical docs in docs/assistants/"
echo "  - Synced to .cursor/rules/ (7 files)"
echo "  - Synced to .augment/rules/ (6 files)"
echo "  - Synced to .kilocode/rules/ (5 files)"
echo "  - Updated .trae/rules/project_rules.md"
echo "  - Created root pointer files"
echo ""
echo "Next steps:"
echo "  1. Review .gemini/settings.json for hardcoded secrets"
echo "  2. Run verification: ./scripts/verify-assistant-sync.sh"
echo "  3. Commit changes with message: 'chore: unify assistant policies MCP-first'"

chmod +x "$PROJECT_ROOT/scripts/sync-assistant-rules.sh"
