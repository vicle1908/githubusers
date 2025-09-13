#!/bin/bash

# Git Branch Policies for Multi-AI Development
# Enforces branch naming conventions and policies for AI worktrees
# Usage: ./scripts/branch-policies.sh [validate|create|list|cleanup] [options]

set -euo pipefail

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

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

# Branch naming patterns
AI_BRANCH_PATTERN="^ai/[a-zA-Z0-9_-]+/[a-zA-Z0-9_-]+/[a-zA-Z0-9_-]+$"
FEATURE_BRANCH_PATTERN="^feature/[a-zA-Z0-9_-]+$"
BUGFIX_BRANCH_PATTERN="^bugfix/[a-zA-Z0-9_-]+$"
HOTFIX_BRANCH_PATTERN="^hotfix/[a-zA-Z0-9_-]+$"
RELEASE_BRANCH_PATTERN="^release/[0-9]+\.[0-9]+\.[0-9]+$"

# Function to show usage
show_usage() {
    cat << EOF
Usage: $0 <command> [options]

Git Branch Policies for Multi-AI Development

Commands:
  validate [branch]     Validate branch naming convention
  create <type> <name>  Create branch with proper naming
  list [filter]         List branches with policy compliance
  cleanup [options]     Clean up old or invalid branches
  policy               Show branch naming policies

Options:
  --force              Force operations without confirmation
  --dry-run            Show what would be done without executing
  --help               Show this help message

Examples:
  $0 validate ai/claude/1234/navigation-refactor
  $0 create ai gemini bugfix-001 memory-leak-fix
  $0 list ai
  $0 cleanup --dry-run
  $0 policy

Branch Types:
  ai <assistant> <issue-id> <task-description>
  feature <description>
  bugfix <description>
  hotfix <description>
  release <version>

EOF
}

# Function to show branch policies
show_policies() {
    cat << EOF
┌─────────────────────────────────────────────────────────────┐
│                    Branch Naming Policies                   │
├─────────────────────────────────────────────────────────────┤
│ AI Branches:                                                │
│   Pattern: ai/<assistant>/<issue-id>/<task-description>     │
│   Example: ai/claude/1234/navigation-refactor               │
│   Example: ai/gemini/bugfix-001/memory-leak-fix             │
│                                                             │
│ Feature Branches:                                           │
│   Pattern: feature/<description>                            │
│   Example: feature/user-authentication                      │
│   Example: feature/dark-mode-support                        │
│                                                             │
│ Bugfix Branches:                                            │
│   Pattern: bugfix/<description>                             │
│   Example: bugfix/crash-on-startup                          │
│   Example: bugfix/memory-leak-in-cache                      │
│                                                             │
│ Hotfix Branches:                                            │
│   Pattern: hotfix/<description>                             │
│   Example: hotfix/security-patch                            │
│   Example: hotfix/critical-bug-fix                          │
│                                                             │
│ Release Branches:                                           │
│   Pattern: release/<version>                                │
│   Example: release/1.2.3                                    │
│   Example: release/2.0.0                                    │
│                                                             │
│ Protected Branches:                                         │
│   - main                                                    │
│   - master                                                  │
│   - develop                                                 │
│                                                             │
│ Branch Lifecycle:                                           │
│   1. Create with proper naming                              │
│   2. Develop and test                                       │
│   3. Create pull request                                    │
│   4. Merge to main                                          │
│   5. Delete branch after merge                              │
└─────────────────────────────────────────────────────────────┘

EOF
}

# Function to validate branch name
validate_branch_name() {
    local branch_name="$1"
    
    if [[ -z "$branch_name" ]]; then
        print_error "Branch name cannot be empty"
        return 1
    fi
    
    # Check for invalid characters
    if [[ "$branch_name" =~ [^a-zA-Z0-9/._-] ]]; then
        print_error "Branch name contains invalid characters: $branch_name"
        print_info "Allowed characters: a-z, A-Z, 0-9, /, ., _, -"
        return 1
    fi
    
    # Check for consecutive slashes
    if [[ "$branch_name" =~ // ]]; then
        print_error "Branch name cannot contain consecutive slashes: $branch_name"
        return 1
    fi
    
    # Check for leading/trailing slashes
    if [[ "$branch_name" =~ ^/ ]] || [[ "$branch_name" =~ /$ ]]; then
        print_error "Branch name cannot start or end with slash: $branch_name"
        return 1
    fi
    
    # Check for reserved names
    local reserved_names=("main" "master" "develop" "HEAD" "ORIG_HEAD")
    for reserved in "${reserved_names[@]}"; do
        if [[ "$branch_name" == "$reserved" ]]; then
            print_error "Branch name '$branch_name' is reserved"
            return 1
        fi
    done
    
    # Validate against patterns
    if [[ "$branch_name" =~ $AI_BRANCH_PATTERN ]]; then
        print_success "Valid AI branch: $branch_name"
        return 0
    elif [[ "$branch_name" =~ $FEATURE_BRANCH_PATTERN ]]; then
        print_success "Valid feature branch: $branch_name"
        return 0
    elif [[ "$branch_name" =~ $BUGFIX_BRANCH_PATTERN ]]; then
        print_success "Valid bugfix branch: $branch_name"
        return 0
    elif [[ "$branch_name" =~ $HOTFIX_BRANCH_PATTERN ]]; then
        print_success "Valid hotfix branch: $branch_name"
        return 0
    elif [[ "$branch_name" =~ $RELEASE_BRANCH_PATTERN ]]; then
        print_success "Valid release branch: $branch_name"
        return 0
    else
        print_warning "Branch name doesn't match standard patterns: $branch_name"
        print_info "Consider using one of the standard patterns for better organization"
        return 0  # Not an error, just a warning
    fi
}

# Function to create branch with proper naming
create_branch() {
    local branch_type="$1"
    local branch_name="$2"
    local force="$3"
    local dry_run="$4"
    
    case "$branch_type" in
        ai)
            if [[ $# -lt 4 ]]; then
                print_error "AI branch requires: assistant-name issue-id task-description"
                return 1
            fi
            local assistant_name="$2"
            local issue_id="$3"
            local task_description="$4"
            branch_name="ai/$assistant_name/$issue_id/$task_description"
            ;;
        feature)
            branch_name="feature/$branch_name"
            ;;
        bugfix)
            branch_name="bugfix/$branch_name"
            ;;
        hotfix)
            branch_name="hotfix/$branch_name"
            ;;
        release)
            branch_name="release/$branch_name"
            ;;
        *)
            print_error "Unknown branch type: $branch_type"
            print_info "Valid types: ai, feature, bugfix, hotfix, release"
            return 1
            ;;
    esac
    
    # Validate the generated branch name
    if ! validate_branch_name "$branch_name"; then
        return 1
    fi
    
    # Check if branch already exists
    if git show-ref --verify --quiet "refs/heads/$branch_name"; then
        print_error "Branch already exists: $branch_name"
        return 1
    fi
    
    if [[ "$dry_run" == "true" ]]; then
        print_info "[DRY RUN] Would create branch: $branch_name"
        return 0
    fi
    
    # Create the branch
    print_info "Creating branch: $branch_name"
    git checkout -b "$branch_name"
    
    print_success "Branch created: $branch_name"
    print_info "Use './scripts/create-worktree.sh' to create a worktree for this branch"
}

# Function to list branches with policy compliance
list_branches() {
    local filter="$1"
    local show_all="$2"
    
    print_info "Branch Policy Compliance Report"
    echo
    
    local branches
    branches=$(git branch -a --format='%(refname:short)' | grep -v '^origin/HEAD' | sort)
    
    local valid_count=0
    local invalid_count=0
    local ai_count=0
    local feature_count=0
    local bugfix_count=0
    local hotfix_count=0
    local release_count=0
    local other_count=0
    
    while IFS= read -r branch; do
        # Skip remote branches for local listing
        if [[ "$branch" =~ ^origin/ ]]; then
            continue
        fi
        
        # Apply filter if specified
        if [[ -n "$filter" && "$branch" != *"$filter"* ]]; then
            continue
        fi
        
        local status=""
        local type=""
        
        if [[ "$branch" =~ $AI_BRANCH_PATTERN ]]; then
            status="✅ Valid AI"
            type="ai"
            ((ai_count++))
        elif [[ "$branch" =~ $FEATURE_BRANCH_PATTERN ]]; then
            status="✅ Valid Feature"
            type="feature"
            ((feature_count++))
        elif [[ "$branch" =~ $BUGFIX_BRANCH_PATTERN ]]; then
            status="✅ Valid Bugfix"
            type="bugfix"
            ((bugfix_count++))
        elif [[ "$branch" =~ $HOTFIX_BRANCH_PATTERN ]]; then
            status="✅ Valid Hotfix"
            type="hotfix"
            ((hotfix_count++))
        elif [[ "$branch" =~ $RELEASE_BRANCH_PATTERN ]]; then
            status="✅ Valid Release"
            type="release"
            ((release_count++))
        elif [[ "$branch" == "main" || "$branch" == "master" || "$branch" == "develop" ]]; then
            status="🔒 Protected"
            type="protected"
        else
            status="⚠️  Non-standard"
            type="other"
            ((other_count++))
        fi
        
        if [[ "$show_all" == "true" || "$type" != "protected" ]]; then
            printf "%-50s %s\n" "$branch" "$status"
        fi
        
        if [[ "$status" =~ "✅" ]]; then
            ((valid_count++))
        else
            ((invalid_count++))
        fi
    done <<< "$branches"
    
    echo
    print_info "Summary:"
    echo "  Valid branches: $valid_count"
    echo "  Non-standard branches: $invalid_count"
    echo "  AI branches: $ai_count"
    echo "  Feature branches: $feature_count"
    echo "  Bugfix branches: $bugfix_count"
    echo "  Hotfix branches: $hotfix_count"
    echo "  Release branches: $release_count"
    echo "  Other branches: $other_count"
}

# Function to cleanup old or invalid branches
cleanup_branches() {
    local force="$1"
    local dry_run="$2"
    
    print_info "Branch Cleanup Analysis"
    echo
    
    local branches_to_cleanup=()
    local merged_branches=()
    local stale_branches=()
    
    # Get all local branches except main/master
    local branches
    branches=$(git branch --format='%(refname:short)' | grep -v -E '^(main|master|develop)$' | sort)
    
    while IFS= read -r branch; do
        # Check if branch is merged
        if git branch --merged main | grep -q "^[[:space:]]*$branch$"; then
            merged_branches+=("$branch")
        fi
        
        # Check for stale branches (no commits in 30 days)
        local last_commit_date
        last_commit_date=$(git log -1 --format="%ci" "$branch" 2>/dev/null || echo "")
        if [[ -n "$last_commit_date" ]]; then
            local last_commit_timestamp
            last_commit_timestamp=$(date -d "$last_commit_date" +%s 2>/dev/null || echo "")
            if [[ -n "$last_commit_timestamp" ]]; then
                local current_time
                current_time=$(date +%s)
                local days_since_commit
                days_since_commit=$(( (current_time - last_commit_timestamp) / 86400 ))
                
                if [[ "$days_since_commit" -gt 30 ]]; then
                    stale_branches+=("$branch")
                fi
            fi
        fi
        
        # Check for invalid naming
        if [[ ! "$branch" =~ $AI_BRANCH_PATTERN ]] && \
           [[ ! "$branch" =~ $FEATURE_BRANCH_PATTERN ]] && \
           [[ ! "$branch" =~ $BUGFIX_BRANCH_PATTERN ]] && \
           [[ ! "$branch" =~ $HOTFIX_BRANCH_PATTERN ]] && \
           [[ ! "$branch" =~ $RELEASE_BRANCH_PATTERN ]]; then
            branches_to_cleanup+=("$branch")
        fi
    done <<< "$branches"
    
    # Report findings
    if [[ ${#merged_branches[@]} -gt 0 ]]; then
        print_info "Merged branches (safe to delete):"
        for branch in "${merged_branches[@]}"; do
            echo "  - $branch"
        done
        echo
    fi
    
    if [[ ${#stale_branches[@]} -gt 0 ]]; then
        print_warning "Stale branches (no commits in 30+ days):"
        for branch in "${stale_branches[@]}"; do
            echo "  - $branch"
        done
        echo
    fi
    
    if [[ ${#branches_to_cleanup[@]} -gt 0 ]]; then
        print_warning "Branches with non-standard naming:"
        for branch in "${branches_to_cleanup[@]}"; do
            echo "  - $branch"
        done
        echo
    fi
    
    # Perform cleanup if requested
    if [[ "$dry_run" == "true" ]]; then
        print_info "[DRY RUN] Would delete ${#merged_branches[@]} merged branches"
        return 0
    fi
    
    if [[ ${#merged_branches[@]} -gt 0 ]]; then
        if [[ "$force" == "true" ]]; then
            print_info "Deleting merged branches..."
            for branch in "${merged_branches[@]}"; do
                git branch -d "$branch"
                print_success "Deleted merged branch: $branch"
            done
        else
            echo
            read -p "Delete ${#merged_branches[@]} merged branches? (y/N): " confirm
            if [[ "$confirm" =~ ^[Yy]$ ]]; then
                for branch in "${merged_branches[@]}"; do
                    git branch -d "$branch"
                    print_success "Deleted merged branch: $branch"
                done
            fi
        fi
    fi
}

# Main function
main() {
    # Check if we're in a Git repository
    if ! git rev-parse --git-dir > /dev/null 2>&1; then
        print_error "Not in a Git repository. Please run this script from the project root."
        exit 1
    fi
    
    # Parse arguments
    local command=""
    local force="false"
    local dry_run="false"
    local show_all="false"
    
    while [[ $# -gt 0 ]]; do
        case $1 in
            validate|create|list|cleanup|policy)
                command="$1"
                shift
                ;;
            --force)
                force="true"
                shift
                ;;
            --dry-run)
                dry_run="true"
                shift
                ;;
            --all)
                show_all="true"
                shift
                ;;
            --help)
                show_usage
                exit 0
                ;;
            -*)
                print_error "Unknown option: $1"
                show_usage
                exit 1
                ;;
            *)
                break
                ;;
        esac
    done
    
    # Change to project root
    cd "$PROJECT_ROOT"
    
    # Execute command
    case "$command" in
        validate)
            if [[ $# -eq 0 ]]; then
                print_error "Branch name required for validation"
                show_usage
                exit 1
            fi
            validate_branch_name "$1"
            ;;
        create)
            if [[ $# -lt 2 ]]; then
                print_error "Branch type and name required"
                show_usage
                exit 1
            fi
            create_branch "$1" "$2" "$force" "$dry_run"
            ;;
        list)
            list_branches "${1:-}" "$show_all"
            ;;
        cleanup)
            cleanup_branches "$force" "$dry_run"
            ;;
        policy)
            show_policies
            ;;
        "")
            print_error "Command required"
            show_usage
            exit 1
            ;;
        *)
            print_error "Unknown command: $command"
            show_usage
            exit 1
            ;;
    esac
}

# Run main function with all arguments
main "$@"
