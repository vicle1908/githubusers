#!/bin/bash

# Git Worktree Cleanup Script for Multi-AI Development
# Safely removes worktrees and cleans up associated resources
# Usage: ./scripts/cleanup-worktrees.sh [worktree-path] [--force] [--all]

set -euo pipefail

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
# Worktrees are now created outside the main repository
WORKTREES_DIR="$(dirname "$PROJECT_ROOT")/githubusers-worktrees"

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
Usage: $0 [worktree-path] [options]

Cleans up Git worktrees for AI assistant parallel development.

Arguments:
  worktree-path    Path to specific worktree to remove (optional)
                   If not provided, will show interactive selection

Options:
  --force         Force removal without confirmation
  --all           Remove all worktrees (use with caution)
  --dry-run       Show what would be removed without actually removing
  --help          Show this help message

Examples:
  $0                                    # Interactive selection
  $0 worktrees/ai-claude-1234-feature   # Remove specific worktree
  $0 --all --dry-run                    # Show all worktrees that would be removed
  $0 --all --force                      # Remove all worktrees (dangerous!)

The script will:
1. Check for uncommitted changes
2. Offer to commit or stash changes
3. Remove the worktree directory
4. Clean up Git references
5. Run garbage collection if needed

EOF
}

# Function to check if worktree has uncommitted changes
check_uncommitted_changes() {
    local worktree_path="$1"
    
    cd "$worktree_path"
    
    # Check for uncommitted changes
    if ! git diff --quiet || ! git diff --cached --quiet; then
        return 0  # Has uncommitted changes
    fi
    
    # Check for untracked files
    if [[ -n "$(git ls-files --others --exclude-standard)" ]]; then
        return 0  # Has untracked files
    fi
    
    return 1  # No uncommitted changes
}

# Function to handle uncommitted changes
handle_uncommitted_changes() {
    local worktree_path="$1"
    local force="$2"
    
    cd "$worktree_path"
    
    print_warning "Worktree has uncommitted changes:"
    git status --short
    
    if [[ "$force" == "true" ]]; then
        print_warning "Force mode: stashing all changes..."
        git stash push -m "Auto-stash before worktree cleanup $(date)"
        return 0
    fi
    
    echo
    print_info "What would you like to do with uncommitted changes?"
    echo "1) Commit changes"
    echo "2) Stash changes"
    echo "3) Discard changes (dangerous!)"
    echo "4) Cancel cleanup"
    
    while true; do
        read -p "Choose option (1-4): " choice
        case $choice in
            1)
                print_info "Opening editor to commit changes..."
                git commit
                return 0
                ;;
            2)
                print_info "Stashing changes..."
                git stash push -m "Stash before worktree cleanup $(date)"
                return 0
                ;;
            3)
                print_warning "Discarding all changes..."
                git reset --hard HEAD
                git clean -fd
                return 0
                ;;
            4)
                print_info "Cleanup cancelled"
                exit 0
                ;;
            *)
                print_error "Invalid option. Please choose 1-4."
                ;;
        esac
    done
}

# Function to remove a single worktree
remove_worktree() {
    local worktree_path="$1"
    local force="$2"
    local dry_run="$3"
    
    # Convert relative path to absolute path if needed
    if [[ ! "$worktree_path" = /* ]]; then
        worktree_path="$PROJECT_ROOT/$worktree_path"
    fi
    
    # Validate worktree path
    if [[ ! -d "$worktree_path" ]]; then
        print_error "Worktree path does not exist: $worktree_path"
        return 1
    fi
    
    # Get worktree info
    local branch_name
    branch_name=$(cd "$worktree_path" && git branch --show-current 2>/dev/null || echo "unknown")
    
    print_info "Removing worktree: $worktree_path"
    print_info "Branch: $branch_name"
    
    if [[ "$dry_run" == "true" ]]; then
        print_info "[DRY RUN] Would remove worktree: $worktree_path"
        return 0
    fi
    
    # Check for uncommitted changes
    if check_uncommitted_changes "$worktree_path"; then
        handle_uncommitted_changes "$worktree_path" "$force"
    fi
    
    # Remove the worktree
    print_info "Removing worktree..."
    if [[ "$force" == "true" ]]; then
        git worktree remove --force "$worktree_path"
    else
        git worktree remove "$worktree_path"
    fi
    
    # Check if branch should be deleted
    if [[ "$branch_name" != "unknown" && "$branch_name" != "main" && "$branch_name" != "master" ]]; then
        if [[ "$force" == "true" ]]; then
            print_info "Deleting branch: $branch_name"
            git branch -D "$branch_name" 2>/dev/null || true
        else
            echo
            read -p "Delete branch '$branch_name'? (y/N): " delete_branch
            if [[ "$delete_branch" =~ ^[Yy]$ ]]; then
                print_info "Deleting branch: $branch_name"
                git branch -D "$branch_name" 2>/dev/null || true
            else
                print_info "Keeping branch: $branch_name"
            fi
        fi
    fi
    
    print_success "Worktree removed: $worktree_path"
}

# Function to list all worktrees
list_worktrees() {
    print_info "Current worktrees:"
    echo
    
    if ! git worktree list > /dev/null 2>&1; then
        print_warning "No worktrees found"
        return 1
    fi
    
    local worktrees
    worktrees=$(git worktree list --porcelain | grep "^worktree" | cut -d' ' -f2-)
    
    if [[ -z "$worktrees" ]]; then
        print_warning "No worktrees found"
        return 1
    fi
    
    local index=1
    while IFS= read -r worktree_path; do
        if [[ -d "$worktree_path" ]]; then
            local branch_name
            branch_name=$(cd "$worktree_path" && git branch --show-current 2>/dev/null || echo "unknown")
            
            local status=""
            if check_uncommitted_changes "$worktree_path"; then
                status=" (has uncommitted changes)"
            fi
            
            echo "$index) $worktree_path"
            echo "   Branch: $branch_name$status"
            echo
        fi
        ((index++))
    done <<< "$worktrees"
    
    return 0
}

# Function to remove all worktrees
remove_all_worktrees() {
    local force="$1"
    local dry_run="$2"
    
    print_warning "Removing ALL worktrees..."
    
    if [[ "$dry_run" == "true" ]]; then
        print_info "[DRY RUN] Would remove all worktrees"
        list_worktrees
        return 0
    fi
    
    if [[ "$force" != "true" ]]; then
        echo
        print_warning "This will remove ALL worktrees. Are you sure?"
        read -p "Type 'yes' to confirm: " confirmation
        if [[ "$confirmation" != "yes" ]]; then
            print_info "Operation cancelled"
            exit 0
        fi
    fi
    
    local worktrees
    worktrees=$(git worktree list --porcelain | grep "^worktree" | cut -d' ' -f2-)
    
    if [[ -z "$worktrees" ]]; then
        print_info "No worktrees to remove"
        return 0
    fi
    
    while IFS= read -r worktree_path; do
        if [[ -d "$worktree_path" ]]; then
            remove_worktree "$worktree_path" "$force" "false"
        fi
    done <<< "$worktrees"
    
    print_success "All worktrees removed"
}

# Function to interactive worktree selection
interactive_selection() {
    local force="$1"
    local dry_run="$2"
    
    if ! list_worktrees; then
        exit 0
    fi
    
    echo
    read -p "Enter worktree number to remove (or 'all' for all worktrees): " selection
    
    if [[ "$selection" == "all" ]]; then
        remove_all_worktrees "$force" "$dry_run"
        return 0
    fi
    
    # Get worktree by index
    local worktrees
    worktrees=$(git worktree list --porcelain | grep "^worktree" | cut -d' ' -f2-)
    
    local index=1
    while IFS= read -r worktree_path; do
        if [[ "$index" == "$selection" ]]; then
            remove_worktree "$worktree_path" "$force" "$dry_run"
            return 0
        fi
        ((index++))
    done <<< "$worktrees"
    
    print_error "Invalid selection: $selection"
    exit 1
}

# Function to run garbage collection
run_garbage_collection() {
    local dry_run="$1"
    
    if [[ "$dry_run" == "true" ]]; then
        print_info "[DRY RUN] Would run garbage collection"
        return 0
    fi
    
    # Ensure we're in the project root
    cd "$PROJECT_ROOT"
    
    print_info "Running garbage collection..."
    git gc --prune=now
    
    print_info "Pruning worktree references..."
    git worktree prune
    
    print_success "Cleanup completed"
}

# Main function
main() {
    # Check if we're in a Git repository
    if ! git rev-parse --git-dir > /dev/null 2>&1; then
        print_error "Not in a Git repository. Please run this script from the project root."
        exit 1
    fi
    
    # Parse arguments
    local worktree_path=""
    local force="false"
    local dry_run="false"
    local remove_all="false"
    
    while [[ $# -gt 0 ]]; do
        case $1 in
            --force)
                force="true"
                shift
                ;;
            --all)
                remove_all="true"
                shift
                ;;
            --dry-run)
                dry_run="true"
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
                if [[ -z "$worktree_path" ]]; then
                    worktree_path="$1"
                else
                    print_error "Multiple worktree paths specified"
                    show_usage
                    exit 1
                fi
                shift
                ;;
        esac
    done
    
    # Change to project root
    cd "$PROJECT_ROOT"
    
    # Handle different modes
    if [[ "$remove_all" == "true" ]]; then
        remove_all_worktrees "$force" "$dry_run"
    elif [[ -n "$worktree_path" ]]; then
        remove_worktree "$worktree_path" "$force" "$dry_run"
    else
        interactive_selection "$force" "$dry_run"
    fi
    
    # Run garbage collection if not dry run
    if [[ "$dry_run" != "true" ]]; then
        run_garbage_collection "$dry_run"
    fi
}

# Run main function with all arguments
main "$@"
