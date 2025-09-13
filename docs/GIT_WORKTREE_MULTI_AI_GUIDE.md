# Git Worktree Multi-AI Development Guide

## Overview

This guide explains how to use Git Worktree for enabling multiple AI assistants to work in parallel on the same codebase. This approach eliminates context switching, allows isolated development streams, and maximizes productivity through parallel AI development.

## Table of Contents

1. [Introduction](#introduction)
2. [Architecture](#architecture)
3. [Quick Start](#quick-start)
4. [Scripts Reference](#scripts-reference)
5. [Best Practices](#best-practices)
6. [Troubleshooting](#troubleshooting)
7. [Advanced Usage](#advanced-usage)

## Introduction

### What is Git Worktree?

Git Worktree is a Git feature that allows you to have multiple working directories from a single repository. Each worktree can be checked out to a different branch, enabling parallel development without the overhead of multiple repository clones.

### Benefits for Multi-AI Development

- **True Parallel Development**: Multiple AI agents work simultaneously without context switching
- **Isolated Context**: Each worktree maintains independent file states and AI agent context
- **No Stashing Required**: Changes stay isolated in respective trees
- **Independent Testing**: Run tests in one tree while developing in another
- **Space Efficient**: Shared Git history with minimal disk overhead
- **AI Agent Multiplication**: Each AI instance maintains deep context about specific tasks

### When to Use

- Working on multiple features simultaneously
- AI assistants handling different tasks in parallel
- Need to maintain separate development contexts
- Want to avoid constant branch switching and stashing

## Architecture

### Directory Structure

**IMPORTANT**: Worktrees are created **outside** the main repository to avoid nested repositories and maintain clean separation:

```
times/
├── githubusers/                    # Main repository
│   ├── .git/                      # Shared Git metadata
│   ├── scripts/                   # Automation scripts
│   │   ├── create-worktree.sh     # Worktree creation
│   │   ├── cleanup-worktrees.sh   # Automated cleanup
│   │   └── monitor-worktrees.sh   # Health monitoring
│   └── docs/                      # Documentation
│       └── GIT_WORKTREE_MULTI_AI_GUIDE.md
├── githubusers-worktrees/          # Worktrees directory (OUTSIDE main repo)
│   ├── ai-claude-feature-1/       # AI assistant worktrees
│   ├── ai-gemini-bugfix-2/        # Each with isolated context
│   └── ai-copilot-refactor-3/     # Independent development
└── githubusers-module-cleanup/     # Other existing worktrees
```

### Branch Naming Convention

All AI worktree branches follow the pattern:
```
ai/<assistant-name>/<issue-id>/<task-description>
```

Examples:
- `ai/claude/1234/navigation-refactor`
- `ai/gemini/bugfix-001/memory-leak-fix`
- `ai/copilot/feature-002/user-authentication`

### Sparse-Checkout Configuration

Each worktree is configured with sparse-checkout to focus on relevant parts of the codebase:

- **Navigation tasks**: `app/`, `navigation-*/`, `feature-*/`, `core-ui/`
- **UI tasks**: `app/`, `feature-*/`, `core-ui/`, `core-design/`
- **Data tasks**: `core-data/`, `feature-*/`, `app/src/main/java/com/example/githubusers/di/`
- **Testing tasks**: `app/`, `feature-*/`, `core-*/`, `plugins/`, `docs/`
- **Build tasks**: `plugins/`, `build-logic/`, `catalog/`, `app/build.gradle.kts`

## Quick Start

### 1. Create a New Worktree

```bash
# Create a worktree for Claude working on navigation refactor
./scripts/create-worktree.sh claude 1234 navigation-refactor

# Create a worktree for Gemini working on a bug fix
./scripts/create-worktree.sh gemini bugfix-001 memory-leak-fix
```

### 2. Navigate to the Worktree

```bash
# Navigate to the worktree directory
cd worktrees/ai-claude-1234-navigation-refactor

# Start your AI assistant (Claude, Gemini, etc.)
# The worktree is now ready for development
```

### 3. Monitor Worktrees

```bash
# Check health of all worktrees
./scripts/monitor-worktrees.sh --health

# Show disk usage
./scripts/monitor-worktrees.sh --disk

# Get detailed status
./scripts/monitor-worktrees.sh --status
```

### 4. Clean Up When Done

```bash
# Interactive cleanup
./scripts/cleanup-worktrees.sh

# Remove specific worktree
./scripts/cleanup-worktrees.sh worktrees/ai-claude-1234-navigation-refactor

# Remove all worktrees (use with caution)
./scripts/cleanup-worktrees.sh --all --force
```

## Scripts Reference

### create-worktree.sh

Creates a new Git worktree for AI assistant development.

**Usage:**
```bash
./scripts/create-worktree.sh <assistant-name> <issue-id> <task-description>
```

**Arguments:**
- `assistant-name`: Name of the AI assistant (e.g., claude, gemini, copilot)
- `issue-id`: Issue or task identifier (e.g., 1234, bugfix-001)
- `task-description`: Brief description of the task (e.g., navigation-refactor)

**Features:**
- Creates branch with standardized naming convention
- Configures sparse-checkout based on task type
- Sets up per-worktree Git configuration
- Creates AI development environment files
- Provides helpful Git aliases

**Examples:**
```bash
./scripts/create-worktree.sh claude 1234 navigation-refactor
./scripts/create-worktree.sh gemini bugfix-001 memory-leak-fix
./scripts/create-worktree.sh copilot feature-002 user-authentication
```

### cleanup-worktrees.sh

Safely removes worktrees and cleans up associated resources.

**Usage:**
```bash
./scripts/cleanup-worktrees.sh [worktree-path] [options]
```

**Options:**
- `--force`: Force removal without confirmation
- `--all`: Remove all worktrees (use with caution)
- `--dry-run`: Show what would be removed without actually removing
- `--help`: Show help message

**Features:**
- Checks for uncommitted changes
- Offers to commit or stash changes
- Removes worktree directory
- Cleans up Git references
- Runs garbage collection

**Examples:**
```bash
# Interactive selection
./scripts/cleanup-worktrees.sh

# Remove specific worktree
./scripts/cleanup-worktrees.sh worktrees/ai-claude-1234-feature

# Remove all worktrees (dangerous!)
./scripts/cleanup-worktrees.sh --all --force
```

### monitor-worktrees.sh

Monitors worktree health, disk usage, and provides status information.

**Usage:**
```bash
./scripts/monitor-worktrees.sh [options]
```

**Options:**
- `--health`: Check worktree health and status
- `--disk`: Show disk usage information
- `--status`: Show detailed status of all worktrees
- `--json`: Output in JSON format
- `--cleanup`: Suggest cleanup actions
- `--all`: Show all information (default)

**Features:**
- Worktree health checks
- Disk usage monitoring
- Branch status information
- Uncommitted changes detection
- Cleanup suggestions

**Examples:**
```bash
# Show all information
./scripts/monitor-worktrees.sh

# Check health only
./scripts/monitor-worktrees.sh --health

# JSON output
./scripts/monitor-worktrees.sh --status --json
```

## Best Practices

### 1. Branch Management

- **Use descriptive branch names**: Follow the `ai/<assistant>/<issue-id>/<task-description>` convention
- **Keep branches short-lived**: Merge or delete branches when tasks are complete
- **Regular updates**: Rebase worktree branches against main regularly
- **Push frequently**: Push branches to origin to avoid losing work

### 2. Worktree Organization

- **Dedicated directory**: Keep all worktrees in the `worktrees/` directory
- **Clear naming**: Use descriptive worktree directory names
- **Regular cleanup**: Remove worktrees when tasks are complete
- **Monitor disk usage**: Use monitoring scripts to track space usage

### 3. AI Assistant Integration

- **MCP-first approach**: Use MCP servers for all operations
- **Context preservation**: Each AI assistant maintains its own context
- **Knowledge management**: Use ByteRover for storing patterns and insights
- **Documentation**: Update documentation as you work

### 4. Development Workflow

- **Small, focused changes**: Keep changes small and focused
- **Frequent commits**: Commit changes regularly with descriptive messages
- **Testing**: Run tests in worktrees before merging
- **Code review**: Review changes before merging to main

### 5. Conflict Resolution

- **Sparse-checkout**: Use sparse-checkout to reduce conflicts
- **Frequent rebases**: Rebase against main regularly
- **Communication**: Coordinate with other AI assistants when working on related features
- **Merge queues**: Use merge queues for safe merging

## Troubleshooting

### Common Issues

#### 1. Worktree Creation Fails

**Problem**: `git worktree add` fails with "fatal: 'branch' is already checked out"

**Solution**: The branch is already checked out in another worktree. Use a different branch name or remove the existing worktree.

```bash
# Check existing worktrees
git worktree list

# Remove conflicting worktree
./scripts/cleanup-worktrees.sh worktrees/conflicting-worktree
```

#### 2. Sparse-Checkout Not Working

**Problem**: Sparse-checkout doesn't seem to be filtering files

**Solution**: Ensure sparse-checkout is properly initialized and configured.

```bash
cd worktrees/your-worktree
git sparse-checkout init --cone
git sparse-checkout set app/ feature-*/
```

#### 3. Disk Space Issues

**Problem**: Worktrees are consuming too much disk space

**Solution**: Use monitoring and cleanup scripts to manage space.

```bash
# Check disk usage
./scripts/monitor-worktrees.sh --disk

# Clean up old worktrees
./scripts/cleanup-worktrees.sh --all --dry-run
```

#### 4. Git Configuration Issues

**Problem**: Git configuration is not working as expected

**Solution**: Check per-worktree configuration.

```bash
cd worktrees/your-worktree
git config --list --show-origin
git config extensions.worktreeConfig
```

### Performance Issues

#### 1. Slow Operations

- **Large repositories**: Use sparse-checkout to limit checked-out files
- **Many worktrees**: Regularly clean up unused worktrees
- **Disk I/O**: Use SSD storage for better performance

#### 2. Memory Usage

- **Multiple AI instances**: Monitor system resources when running multiple AI assistants
- **Large files**: Avoid checking out large binary files in worktrees
- **Git objects**: Run garbage collection regularly

## Advanced Usage

### 1. Custom Sparse-Checkout Patterns

You can customize sparse-checkout patterns for specific tasks:

```bash
cd worktrees/your-worktree
git sparse-checkout set --no-cone
git sparse-checkout set 'app/src/main/java/com/example/githubusers/'
git sparse-checkout set 'feature-users/'
git sparse-checkout set 'core-ui/'
```

### 2. Per-Worktree Configuration

Set up worktree-specific Git configuration:

```bash
cd worktrees/your-worktree
git config extensions.worktreeConfig true
git config user.name "AI Assistant (claude)"
git config user.email "ai-claude@githubusers.local"
git config core.editor "code --wait"
```

### 3. Integration with CI/CD

Configure CI/CD to handle worktree branches:

```yaml
# .github/workflows/worktree-ci.yml
name: Worktree CI
on:
  push:
    branches: ['ai/**']
  pull_request:
    branches: ['ai/**']

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
        with:
          fetch-depth: 0
      - name: Run tests
        run: ./gradlew test
```

### 4. Automated Worktree Management

Create automated scripts for common workflows:

```bash
#!/bin/bash
# scripts/ai-workflow.sh

# Create worktree for AI task
./scripts/create-worktree.sh "$1" "$2" "$3"

# Navigate to worktree
cd "worktrees/ai-$1-$2-$3"

# Start AI assistant with context
echo "Starting AI assistant for task: $3"
echo "Worktree: $(pwd)"
echo "Branch: $(git branch --show-current)"
```

### 5. Monitoring and Alerting

Set up monitoring for worktree health:

```bash
#!/bin/bash
# scripts/worktree-monitor.sh

# Check for stale worktrees
./scripts/monitor-worktrees.sh --cleanup

# Alert if disk usage is high
DISK_USAGE=$(df -h . | awk 'NR==2 {print $5}' | sed 's/%//')
if [ "$DISK_USAGE" -gt 80 ]; then
    echo "Warning: Disk usage is ${DISK_USAGE}%"
fi
```

## Integration with Project Standards

### MCP-First Approach

All operations should use MCP servers when available:

- **Build operations**: Use Gradle MCP Server
- **Android operations**: Use Android MCP and Mobile-MCP
- **Code search**: Use Claude Context MCP
- **Knowledge management**: Use ByteRover MCP

### Android Development Standards

Follow the project's Android development standards:

- **Clean Architecture**: Maintain separation of concerns
- **MVI Pattern**: Use unidirectional data flow
- **Feature-based modularization**: Respect module boundaries
- **Navigation 3**: Use only Navigation 3 components
- **Material 3 Design**: Follow design system guidelines

### Code Quality

Maintain high code quality standards:

- **Kotlin Style Guide**: Follow project style guidelines
- **Static Analysis**: Use Detekt and KtLint
- **Testing**: Write comprehensive tests
- **Documentation**: Update documentation as needed

## Conclusion

Git Worktree provides a powerful foundation for multi-AI parallel development. By following the practices outlined in this guide, you can:

- Enable multiple AI assistants to work simultaneously
- Maintain isolated development contexts
- Reduce context switching overhead
- Maximize development productivity
- Ensure code quality and consistency

The provided scripts automate the common workflows and help maintain a clean, organized development environment. Regular monitoring and cleanup ensure optimal performance and resource usage.

For questions or issues, refer to the troubleshooting section or consult the project's development standards documentation.
