#!/bin/bash

# Sparse-Checkout Configuration for Multi-AI Development
# Manages sparse-checkout patterns to reduce conflicts and improve performance
# Usage: ./scripts/sparse-checkout-config.sh [setup|update|list|reset] [options]

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

# Sparse-checkout patterns for different task types
declare -A SPARSE_PATTERNS=(
    ["navigation"]="app navigation-api navigation-impl feature-users feature-search feature-settings core-ui plugins catalog testing .cursor .claude .augment .ai-context"
    ["ui"]="app feature-users feature-search feature-settings core-ui core-design plugins catalog navigation-api navigation-impl testing .cursor .claude .augment .ai-context"
    ["data"]="core-data feature-users feature-search feature-settings app/src/main/java/com/example/githubusers/di plugins catalog navigation-api navigation-impl testing .cursor .claude .augment .ai-context"
    ["testing"]="app feature-users feature-search feature-settings core-common core-mvi core-networking core-storage core-ui plugins docs catalog testing navigation-api navigation-impl .cursor .claude .augment .ai-context"
    ["build"]="plugins catalog app/build.gradle.kts testing .cursor .claude .augment .ai-context"
    ["default"]="app feature-users feature-search feature-settings core-common core-mvi core-networking core-storage core-ui plugins docs scripts catalog testing navigation-api navigation-impl .cursor .claude .augment .ai-context"
)

# Function to show usage
show_usage() {
    cat << EOF
Usage: $0 <command> [options]

Sparse-Checkout Configuration for Multi-AI Development

Commands:
  setup <type>          Set up sparse-checkout for specific task type
  update <type>         Update sparse-checkout pattern for current worktree
  list                  List available sparse-checkout patterns
  reset                 Reset to full checkout (disable sparse-checkout)
  status                Show current sparse-checkout status
  optimize              Optimize sparse-checkout for current worktree

Options:
  --worktree <path>     Specify worktree path (default: current directory)
  --force               Force operations without confirmation
  --dry-run             Show what would be done without executing
  --help                Show this help message

Task Types:
  navigation            Navigation-related development
  ui                    UI/UX development
  data                  Data layer development
  testing               Testing and quality assurance
  build                 Build system and plugins
  default               Default development setup

Examples:
  $0 setup navigation
  $0 update ui --worktree worktrees/ai-claude-1234-feature
  $0 list
  $0 reset
  $0 status
  $0 optimize

EOF
}

# Function to list available patterns
list_patterns() {
    print_info "Available Sparse-Checkout Patterns:"
    echo
    
    for pattern_type in "${!SPARSE_PATTERNS[@]}"; do
        echo "📁 $pattern_type:"
        echo "   ${SPARSE_PATTERNS[$pattern_type]}"
        echo
    done
    
    print_info "Usage: $0 setup <pattern-type>"
}

# Function to get current worktree path
get_worktree_path() {
    local specified_path="$1"
    
    if [[ -n "$specified_path" ]]; then
        if [[ ! "$specified_path" = /* ]]; then
            specified_path="$PROJECT_ROOT/$specified_path"
        fi
        echo "$specified_path"
    else
        pwd
    fi
}

# Function to check if directory is a worktree
is_worktree() {
    local path="$1"
    
    if [[ -d "$path/.git" ]]; then
        return 0
    else
        return 1
    fi
}

# Function to setup sparse-checkout
setup_sparse_checkout() {
    local pattern_type="$1"
    local worktree_path="$2"
    local force="$3"
    local dry_run="$4"
    
    # Validate pattern type
    if [[ -z "${SPARSE_PATTERNS[$pattern_type]:-}" ]]; then
        print_error "Unknown pattern type: $pattern_type"
        print_info "Available types: ${!SPARSE_PATTERNS[*]}"
        return 1
    fi
    
    # Check if directory is a worktree
    if ! is_worktree "$worktree_path"; then
        print_error "Not a Git worktree: $worktree_path"
        return 1
    fi
    
    local patterns="${SPARSE_PATTERNS[$pattern_type]}"
    
    if [[ "$dry_run" == "true" ]]; then
        print_info "[DRY RUN] Would set up sparse-checkout for $pattern_type:"
        echo "  Patterns: $patterns"
        return 0
    fi
    
    cd "$worktree_path"
    
    # Initialize sparse-checkout if not already done
    if ! git config core.sparseCheckout > /dev/null 2>&1; then
        print_info "Initializing sparse-checkout..."
        git sparse-checkout init --cone
    fi
    
    # Set the patterns
    print_info "Setting sparse-checkout patterns for $pattern_type..."
    git sparse-checkout set $patterns
    
    print_success "Sparse-checkout configured for $pattern_type"
    print_info "Patterns: $patterns"
    
    # Show current status
    show_sparse_status "$worktree_path"
}

# Function to update sparse-checkout
update_sparse_checkout() {
    local pattern_type="$1"
    local worktree_path="$2"
    local force="$3"
    local dry_run="$4"
    
    # Check if sparse-checkout is already configured
    if ! git config core.sparseCheckout > /dev/null 2>&1; then
        print_warning "Sparse-checkout not initialized. Setting up..."
        setup_sparse_checkout "$pattern_type" "$worktree_path" "$force" "$dry_run"
        return $?
    fi
    
    # Update with new patterns
    setup_sparse_checkout "$pattern_type" "$worktree_path" "$force" "$dry_run"
}

# Function to reset sparse-checkout
reset_sparse_checkout() {
    local worktree_path="$1"
    local force="$2"
    local dry_run="$3"
    
    if ! is_worktree "$worktree_path"; then
        print_error "Not a Git worktree: $worktree_path"
        return 1
    fi
    
    if [[ "$dry_run" == "true" ]]; then
        print_info "[DRY RUN] Would reset sparse-checkout to full checkout"
        return 0
    fi
    
    cd "$worktree_path"
    
    # Check if sparse-checkout is configured
    if ! git config core.sparseCheckout > /dev/null 2>&1; then
        print_info "Sparse-checkout not configured. Nothing to reset."
        return 0
    fi
    
    if [[ "$force" != "true" ]]; then
        echo
        read -p "Reset sparse-checkout to full checkout? (y/N): " confirm
        if [[ ! "$confirm" =~ ^[Yy]$ ]]; then
            print_info "Operation cancelled"
            return 0
        fi
    fi
    
    print_info "Resetting sparse-checkout..."
    git sparse-checkout disable
    
    print_success "Sparse-checkout reset. Full checkout restored."
}

# Function to show sparse-checkout status
show_sparse_status() {
    local worktree_path="$1"
    
    if ! is_worktree "$worktree_path"; then
        print_error "Not a Git worktree: $worktree_path"
        return 1
    fi
    
    cd "$worktree_path"
    
    print_info "Sparse-Checkout Status for: $(basename "$worktree_path")"
    echo
    
    # Check if sparse-checkout is enabled
    if git config core.sparseCheckout > /dev/null 2>&1; then
        local sparse_enabled
        sparse_enabled=$(git config core.sparseCheckout)
        
        if [[ "$sparse_enabled" == "true" ]]; then
            print_success "Sparse-checkout: ENABLED"
            
            # Show current patterns
            echo
            print_info "Current patterns:"
            git sparse-checkout list | while read -r pattern; do
                echo "  - $pattern"
            done
            
            # Show excluded files count
            local excluded_count
            excluded_count=$(git ls-files --others --exclude-standard | wc -l)
            if [[ "$excluded_count" -gt 0 ]]; then
                echo
                print_info "Excluded files: $excluded_count"
            fi
            
        else
            print_info "Sparse-checkout: DISABLED"
        fi
    else
        print_info "Sparse-checkout: NOT CONFIGURED"
    fi
    
    # Show disk usage
    local disk_usage
    disk_usage=$(du -sh "$worktree_path" 2>/dev/null | cut -f1 || echo "unknown")
    echo
    print_info "Disk usage: $disk_usage"
}

# Function to optimize sparse-checkout
optimize_sparse_checkout() {
    local worktree_path="$1"
    local force="$2"
    local dry_run="$3"
    
    if ! is_worktree "$worktree_path"; then
        print_error "Not a Git worktree: $worktree_path"
        return 1
    fi
    
    cd "$worktree_path"
    
    # Check if sparse-checkout is enabled
    if ! git config core.sparseCheckout > /dev/null 2>&1 || [[ "$(git config core.sparseCheckout)" != "true" ]]; then
        print_warning "Sparse-checkout not enabled. Nothing to optimize."
        return 0
    fi
    
    if [[ "$dry_run" == "true" ]]; then
        print_info "[DRY RUN] Would optimize sparse-checkout"
        return 0
    fi
    
    print_info "Optimizing sparse-checkout..."
    
    # Reapply sparse-checkout to ensure consistency
    git sparse-checkout reapply
    
    # Clean up any untracked files that shouldn't be there
    local untracked_files
    untracked_files=$(git ls-files --others --exclude-standard)
    
    if [[ -n "$untracked_files" ]]; then
        print_info "Found untracked files outside sparse-checkout patterns:"
        echo "$untracked_files" | head -10
        if [[ $(echo "$untracked_files" | wc -l) -gt 10 ]]; then
            echo "... and $(( $(echo "$untracked_files" | wc -l) - 10 )) more files"
        fi
        
        if [[ "$force" == "true" ]]; then
            print_info "Removing untracked files outside sparse-checkout patterns..."
            git clean -fd
        else
            echo
            read -p "Remove untracked files outside sparse-checkout patterns? (y/N): " confirm
            if [[ "$confirm" =~ ^[Yy]$ ]]; then
                git clean -fd
                print_success "Untracked files removed"
            fi
        fi
    fi
    
    print_success "Sparse-checkout optimized"
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
    local worktree_path=""
    local force="false"
    local dry_run="false"
    
    while [[ $# -gt 0 ]]; do
        case $1 in
            setup|update|list|reset|status|optimize)
                command="$1"
                shift
                ;;
            --worktree)
                worktree_path="$2"
                shift 2
                ;;
            --force)
                force="true"
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
                break
                ;;
        esac
    done
    
    # Get worktree path
    worktree_path=$(get_worktree_path "$worktree_path")
    
    # Execute command
    case "$command" in
        setup)
            if [[ $# -eq 0 ]]; then
                print_error "Pattern type required for setup"
                show_usage
                exit 1
            fi
            setup_sparse_checkout "$1" "$worktree_path" "$force" "$dry_run"
            ;;
        update)
            if [[ $# -eq 0 ]]; then
                print_error "Pattern type required for update"
                show_usage
                exit 1
            fi
            update_sparse_checkout "$1" "$worktree_path" "$force" "$dry_run"
            ;;
        list)
            list_patterns
            ;;
        reset)
            reset_sparse_checkout "$worktree_path" "$force" "$dry_run"
            ;;
        status)
            show_sparse_status "$worktree_path"
            ;;
        optimize)
            optimize_sparse_checkout "$worktree_path" "$force" "$dry_run"
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
