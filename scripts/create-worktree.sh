#!/bin/bash

# Git Worktree Creation Script for Multi-AI Development
# Creates isolated worktrees for AI assistants with proper configuration
# Usage: ./scripts/create-worktree.sh <assistant-name> <issue-id> <task-description>

set -euo pipefail

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
# Create worktrees outside the main repository to avoid nested repos
WORKTREES_DIR="$(dirname "$PROJECT_ROOT")/githubusers-worktrees"
MAIN_BRANCH="main"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to show usage
show_usage() {
    cat << EOF
Usage: $0 <assistant-name> <issue-id> <task-description>

Creates a new Git worktree for AI assistant parallel development. Worktrees now always include plugins, catalog, and testing module for comprehensive support.

Arguments:
  assistant-name    Name of the AI assistant (e.g., claude, gemini, copilot)
  issue-id         Issue or task identifier (e.g., 1234, bugfix-001)
  task-description Brief description of the task (e.g., navigation-refactor)

Examples:
  $0 claude 1234 navigation-refactor
  $0 gemini bugfix-001 memory-leak-fix
  $0 copilot feature-002 user-authentication

The script will:
1. Create a new branch: ai/<assistant-name>/<issue-id>/<task-description>
2. Set up a worktree outside the main repo: ../githubusers-worktrees/ai-<assistant-name>-<issue-id>-<task-description>/
3. Configure sparse-checkout for the worktree
4. Set up per-worktree Git configuration
5. Initialize the worktree for AI development

EOF
}

# Function to validate inputs
validate_inputs() {
    local assistant_name="$1"
    local issue_id="$2"
    local task_description="$3"
    
    # Validate assistant name
    if [[ ! "$assistant_name" =~ ^[a-zA-Z0-9_-]+$ ]]; then
        print_error "Assistant name must contain only alphanumeric characters, hyphens, and underscores"
        exit 1
    fi
    
    # Validate issue ID
    if [[ ! "$issue_id" =~ ^[a-zA-Z0-9_-]+$ ]]; then
        print_error "Issue ID must contain only alphanumeric characters, hyphens, and underscores"
        exit 1
    fi
    
    # Validate task description
    if [[ ! "$task_description" =~ ^[a-zA-Z0-9_-]+$ ]]; then
        print_error "Task description must contain only alphanumeric characters, hyphens, and underscores"
        exit 1
    fi
}

# Function to check if worktree already exists
check_existing_worktree() {
    local worktree_path="$1"
    
    if [[ -d "$worktree_path" ]]; then
        print_error "Worktree already exists at: $worktree_path"
        print_info "Use 'git worktree list' to see existing worktrees"
        exit 1
    fi
}

# Function to create branch name
create_branch_name() {
    local assistant_name="$1"
    local issue_id="$2"
    local task_description="$3"
    
    echo "ai/$assistant_name/$issue_id/$task_description"
}

# Function to create worktree path
create_worktree_path() {
    local assistant_name="$1"
    local issue_id="$2"
    local task_description="$3"
    
    echo "$WORKTREES_DIR/ai-$assistant_name-$issue_id-$task_description"
}

# Function to configure sparse-checkout
configure_sparse_checkout() {
    local worktree_path="$1"
    local task_description="$2"
    
    print_info "Configuring sparse-checkout for focused development..."
    
    cd "$worktree_path"
    
    # Initialize sparse-checkout
    git sparse-checkout init --cone
    
    # Always include plugins and catalog for build/dependency support
    # Configure based on task type (this can be customized per project)
    case "$task_description" in
        *navigation*|*nav*)
            git sparse-checkout set app navigation-api navigation-impl feature-users feature-search feature-settings core-ui plugins catalog
            print_info "Configured sparse-checkout for navigation-related tasks"
            ;;
        *ui*|*compose*|*screen*)
            git sparse-checkout set app feature-users feature-search feature-settings core-ui core-design plugins catalog
            print_info "Configured sparse-checkout for UI-related tasks"
            ;;
        *data*|*repository*|*api*)
            git sparse-checkout set core-data feature-users feature-search feature-settings app/src/main/java/com/example/githubusers/di plugins catalog
            print_info "Configured sparse-checkout for data-related tasks"
            ;;
        *test*|*testing*)
            git sparse-checkout set app feature-users feature-search feature-settings core-common core-mvi core-networking core-storage core-ui plugins docs catalog testing
            print_info "Configured sparse-checkout for testing-related tasks"
            ;;
        *plugin*|*build*|*gradle*)
            git sparse-checkout set plugins catalog app/build.gradle.kts
            print_info "Configured sparse-checkout for build-related tasks"
            ;;
        *)
            # Always include plugins and catalog for build/dependency support
            # Include testing module for comprehensive testing coverage
            # Default: include most modules but exclude large directories
            git sparse-checkout set app feature-users feature-search feature-settings core-common core-mvi core-networking core-storage core-ui plugins docs scripts catalog testing
            print_info "Configured sparse-checkout with default settings"
            ;;
    esac
}

# Function to set up per-worktree configuration
setup_worktree_config() {
    local worktree_path="$1"
    local assistant_name="$2"
    local issue_id="$3"
    local task_description="$4"
    
    print_info "Setting up per-worktree Git configuration..."
    
    cd "$worktree_path"
    
    # Enable worktree-specific configuration
    git config extensions.worktreeConfig true
    
    # Set worktree-specific user configuration
    git config user.name "AI Assistant ($assistant_name)"
    git config user.email "ai-$assistant_name@githubusers.local"
    
    # Set worktree-specific commit template
    cat > .gitmessage << EOF
# AI Assistant: $assistant_name
# Task: $issue_id - $task_description
# 
# Changes made by AI assistant for parallel development
# 
# Type: [feat|fix|docs|style|refactor|test|chore]
# Scope: [module or component affected]
# 
# Description:
# 
# 
# Testing:
# - [ ] Unit tests pass
# - [ ] Integration tests pass
# - [ ] Manual testing completed
# 
# Related: #$issue_id
EOF
    
    git config commit.template .gitmessage
    
    # Set up useful aliases for this worktree
    git config alias.ai-status "status --porcelain"
    git config alias.ai-log "log --oneline --graph --decorate -10"
    git config alias.ai-diff "diff --name-only"
    
    print_success "Per-worktree configuration completed"
}

# Function to create AI development setup
setup_ai_development() {
    local worktree_path="$1"
    local assistant_name="$2"
    local issue_id="$3"
    local task_description="$4"
    
    print_info "Setting up AI development environment..."
    
    # Create AI-specific files
    cat > "$worktree_path/.ai-context" << EOF
# AI Assistant Context File
# Generated: $(date)
# Assistant: $assistant_name
# Task: $issue_id - $task_description
# Worktree: $(basename "$worktree_path")

## Task Context
- Assistant: $assistant_name
- Issue ID: $issue_id
- Description: $task_description
- Created: $(date)

## Development Guidelines
- Use MCP-first approach for all operations
- Follow Android development standards
- Maintain feature ownership principles
- Use ByteRover for knowledge management
- Follow Kotlin style guide

## Quick Commands
- Check status: git ai-status
- View recent commits: git ai-log
- See changed files: git ai-diff
- Update from main: git fetch origin && git rebase origin/main
EOF
    
    # Create a simple README for the worktree
    cat > "$worktree_path/README-AI.md" << EOF
# AI Worktree: $assistant_name - $issue_id

**Task**: $task_description  
**Created**: $(date)  
**Assistant**: $assistant_name

## Purpose
This worktree is dedicated to AI assistant development for the specified task.

## Usage
1. Navigate to this directory: \`cd $(basename "$worktree_path")\`
2. Start your AI assistant (Claude, Gemini, etc.)
3. Work on the task following the project's development standards
4. Use the configured Git aliases for quick operations

## Important Notes
- This worktree shares Git history with the main repository
- Changes are isolated to this branch until merged
- Use \`git fetch origin && git rebase origin/main\` to stay updated
- Follow the commit template for consistent commit messages

## Cleanup
When done, remove this worktree with:
\`git worktree remove $(basename "$worktree_path")\`
EOF
    
    print_success "AI development environment setup completed"
}

# Function to display worktree information
display_worktree_info() {
    local branch_name="$1"
    local worktree_path="$2"
    local assistant_name="$3"
    local issue_id="$4"
    local task_description="$5"
    
    print_success "Worktree created successfully!"
    echo
    echo "┌─────────────────────────────────────────────────────────────┐"
    echo "│                    AI Worktree Created                      │"
    echo "├─────────────────────────────────────────────────────────────┤"
    echo "│ Assistant:     $assistant_name"
    echo "│ Issue ID:      $issue_id"
    echo "│ Task:          $task_description"
    echo "│ Branch:        $branch_name"
    echo "│ Worktree Path: $worktree_path"
    echo "├─────────────────────────────────────────────────────────────┤"
    echo "│ Next Steps:                                                │"
    echo "│ 1. cd $(basename "$worktree_path")"
    echo "│ 2. Start your AI assistant"
    echo "│ 3. Begin development following project standards"
    echo "│ 4. Use 'git ai-status' for quick status checks"
    echo "└─────────────────────────────────────────────────────────────┘"
    echo
    print_info "Use 'git worktree list' to see all worktrees"
    print_info "Use './scripts/cleanup-worktrees.sh' to remove when done"
}

# Main function
main() {
    # Check if we're in a Git repository
    if ! git rev-parse --git-dir > /dev/null 2>&1; then
        print_error "Not in a Git repository. Please run this script from the project root."
        exit 1
    fi
    
    # Check arguments
    if [[ $# -ne 3 ]]; then
        print_error "Invalid number of arguments"
        echo
        show_usage
        exit 1
    fi
    
    local assistant_name="$1"
    local issue_id="$2"
    local task_description="$3"
    
    # Validate inputs
    validate_inputs "$assistant_name" "$issue_id" "$task_description"
    
    # Create branch and worktree names
    local branch_name
    branch_name=$(create_branch_name "$assistant_name" "$issue_id" "$task_description")
    
    local worktree_path
    worktree_path=$(create_worktree_path "$assistant_name" "$issue_id" "$task_description")
    
    print_info "Creating worktree for AI assistant: $assistant_name"
    print_info "Branch: $branch_name"
    print_info "Path: $worktree_path"
    
    # Check if worktree already exists
    check_existing_worktree "$worktree_path"
    
    # Ensure worktrees directory exists
    if [[ ! -d "$WORKTREES_DIR" ]]; then
        print_info "Creating worktrees directory: $WORKTREES_DIR"
        mkdir -p "$WORKTREES_DIR"
    fi
    
    # Ensure we're on main branch in the main repository
    cd "$PROJECT_ROOT"
    if [[ $(git branch --show-current) != "$MAIN_BRANCH" ]]; then
        print_warning "Not on $MAIN_BRANCH branch. Switching to $MAIN_BRANCH..."
        git checkout "$MAIN_BRANCH"
    fi
    
    # Fetch latest changes
    print_info "Fetching latest changes from origin..."
    git fetch origin
    
    # Create the worktree
    print_info "Creating worktree..."
    git worktree add "$worktree_path" -b "$branch_name"
    
    # Configure sparse-checkout
    configure_sparse_checkout "$worktree_path" "$task_description"
    
    # Set up per-worktree configuration
    setup_worktree_config "$worktree_path" "$assistant_name" "$issue_id" "$task_description"
    
    # Set up AI development environment
    setup_ai_development "$worktree_path" "$assistant_name" "$issue_id" "$task_description"
    
    # Display information
    display_worktree_info "$branch_name" "$worktree_path" "$assistant_name" "$issue_id" "$task_description"
}

# Run main function with all arguments
main "$@"
