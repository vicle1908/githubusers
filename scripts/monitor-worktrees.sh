#!/bin/bash

# Git Worktree Monitoring Script for Multi-AI Development
# Monitors worktree health, disk usage, and provides status information
# Usage: ./scripts/monitor-worktrees.sh [--health] [--disk] [--status] [--json]

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
CYAN='\033[0;36m'
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

print_header() {
    echo -e "${CYAN}$1${NC}"
}

# Function to show usage
show_usage() {
    cat << EOF
Usage: $0 [options]

Monitors Git worktrees for AI assistant parallel development.

Options:
  --health       Check worktree health and status
  --disk         Show disk usage information
  --status       Show detailed status of all worktrees
  --json         Output in JSON format
  --cleanup      Suggest cleanup actions
  --all          Show all information (default)
  --help         Show this help message

Examples:
  $0                    # Show all information
  $0 --health          # Check worktree health
  $0 --disk            # Show disk usage
  $0 --status --json   # JSON output of worktree status

The script provides:
- Worktree health checks
- Disk usage monitoring
- Branch status information
- Uncommitted changes detection
- Cleanup suggestions

EOF
}

# Function to get worktree information
get_worktree_info() {
    local worktree_path="$1"
    local json_output="$2"
    
    if [[ ! -d "$worktree_path" ]]; then
        if [[ "$json_output" == "true" ]]; then
            echo "{\"path\":\"$worktree_path\",\"status\":\"missing\",\"error\":\"Directory does not exist\"}"
        else
            print_error "Worktree directory missing: $worktree_path"
        fi
        return 1
    fi
    
    cd "$worktree_path"
    
    # Get basic information
    local branch_name
    branch_name=$(git branch --show-current 2>/dev/null || echo "unknown")
    
    local last_commit
    last_commit=$(git log -1 --format="%h %s" 2>/dev/null || echo "no commits")
    
    local last_commit_date
    last_commit_date=$(git log -1 --format="%ci" 2>/dev/null || echo "unknown")
    
    # Check for uncommitted changes
    local has_uncommitted="false"
    local uncommitted_files=""
    if ! git diff --quiet || ! git diff --cached --quiet; then
        has_uncommitted="true"
        uncommitted_files=$(git diff --name-only | head -5 | tr '\n' ',' | sed 's/,$//')
    fi
    
    # Check for untracked files
    local has_untracked="false"
    local untracked_files=""
    local untracked_count
    untracked_count=$(git ls-files --others --exclude-standard | wc -l)
    if [[ "$untracked_count" -gt 0 ]]; then
        has_untracked="true"
        untracked_files=$(git ls-files --others --exclude-standard | head -5 | tr '\n' ',' | sed 's/,$//')
    fi
    
    # Get disk usage
    local disk_usage
    disk_usage=$(du -sh "$worktree_path" 2>/dev/null | cut -f1 || echo "unknown")
    
    # Check if branch is up to date with origin
    local is_up_to_date="unknown"
    if git rev-parse --verify "origin/$branch_name" >/dev/null 2>&1; then
        if git diff --quiet "origin/$branch_name"..HEAD; then
            is_up_to_date="true"
        else
            is_up_to_date="false"
        fi
    fi
    
    # Get worktree age
    local worktree_age=""
    if [[ -f "$worktree_path/.ai-context" ]]; then
        local created_date
        created_date=$(grep "Created:" "$worktree_path/.ai-context" | cut -d: -f2- | xargs)
        if [[ -n "$created_date" ]]; then
            worktree_age=$(date -d "$created_date" +%s 2>/dev/null || echo "")
            if [[ -n "$worktree_age" ]]; then
                local current_time
                current_time=$(date +%s)
                local age_days
                age_days=$(( (current_time - worktree_age) / 86400 ))
                worktree_age="${age_days} days"
            fi
        fi
    fi
    
    if [[ "$json_output" == "true" ]]; then
        cat << EOF
{
  "path": "$worktree_path",
  "branch": "$branch_name",
  "last_commit": "$last_commit",
  "last_commit_date": "$last_commit_date",
  "has_uncommitted": $has_uncommitted,
  "uncommitted_files": "$uncommitted_files",
  "has_untracked": $has_untracked,
  "untracked_count": $untracked_count,
  "untracked_files": "$untracked_files",
  "disk_usage": "$disk_usage",
  "is_up_to_date": "$is_up_to_date",
  "age": "$worktree_age"
}
EOF
    else
        echo "  Path: $worktree_path"
        echo "  Branch: $branch_name"
        echo "  Last Commit: $last_commit"
        echo "  Last Commit Date: $last_commit_date"
        echo "  Disk Usage: $disk_usage"
        echo "  Age: $worktree_age"
        echo "  Up to Date: $is_up_to_date"
        
        if [[ "$has_uncommitted" == "true" ]]; then
            print_warning "  Uncommitted Changes: $uncommitted_files"
        fi
        
        if [[ "$has_untracked" == "true" ]]; then
            print_warning "  Untracked Files: $untracked_count files ($untracked_files)"
        fi
        
        echo
    fi
}

# Function to check worktree health
check_worktree_health() {
    local json_output="$1"
    
    print_header "=== Worktree Health Check ==="
    
    if ! git worktree list > /dev/null 2>&1; then
        if [[ "$json_output" == "true" ]]; then
            echo "{\"status\":\"no_worktrees\",\"message\":\"No worktrees found\"}"
        else
            print_warning "No worktrees found"
        fi
        return 0
    fi
    
    local worktrees
    worktrees=$(git worktree list --porcelain | grep "^worktree" | cut -d' ' -f2-)
    
    if [[ -z "$worktrees" ]]; then
        if [[ "$json_output" == "true" ]]; then
            echo "{\"status\":\"no_worktrees\",\"message\":\"No worktrees found\"}"
        else
            print_warning "No worktrees found"
        fi
        return 0
    fi
    
    local healthy_count=0
    local total_count=0
    local issues=()
    
    if [[ "$json_output" == "true" ]]; then
        echo "["
    fi
    
    while IFS= read -r worktree_path; do
        if [[ -d "$worktree_path" ]]; then
            ((total_count++))
            
            if [[ "$json_output" == "true" && "$total_count" -gt 1 ]]; then
                echo ","
            fi
            
            local worktree_info
            worktree_info=$(get_worktree_info "$worktree_path" "$json_output")
            
            if [[ "$json_output" != "true" ]]; then
                echo "Worktree: $(basename "$worktree_path")"
                echo "$worktree_info"
                
                # Check for issues
                cd "$worktree_path"
                if check_uncommitted_changes "$worktree_path"; then
                    issues+=("$worktree_path has uncommitted changes")
                fi
                
                local branch_name
                branch_name=$(git branch --show-current 2>/dev/null || echo "unknown")
                if [[ "$branch_name" != "unknown" && "$branch_name" != "main" && "$branch_name" != "master" ]]; then
                    if ! git rev-parse --verify "origin/$branch_name" >/dev/null 2>&1; then
                        issues+=("$worktree_path branch '$branch_name' not pushed to origin")
                    fi
                fi
                
                ((healthy_count++))
            else
                echo "$worktree_info"
            fi
        fi
    done <<< "$worktrees"
    
    if [[ "$json_output" == "true" ]]; then
        echo "]"
    else
        echo
        print_info "Health Summary: $healthy_count/$total_count worktrees healthy"
        
        if [[ ${#issues[@]} -gt 0 ]]; then
            print_warning "Issues found:"
            for issue in "${issues[@]}"; do
                echo "  - $issue"
            done
        else
            print_success "All worktrees are healthy"
        fi
    fi
}

# Function to check for uncommitted changes
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

# Function to show disk usage
show_disk_usage() {
    local json_output="$1"
    
    print_header "=== Disk Usage Information ==="
    
    if [[ "$json_output" == "true" ]]; then
        echo "{"
    fi
    
    # Main repository size
    local main_size
    main_size=$(du -sh "$PROJECT_ROOT" 2>/dev/null | cut -f1 || echo "unknown")
    
    if [[ "$json_output" == "true" ]]; then
        echo "  \"main_repository\": \"$main_size\","
    else
        echo "Main Repository: $main_size"
    fi
    
    # Worktrees directory size
    if [[ -d "$WORKTREES_DIR" ]]; then
        local worktrees_size
        worktrees_size=$(du -sh "$WORKTREES_DIR" 2>/dev/null | cut -f1 || echo "unknown")
        
        if [[ "$json_output" == "true" ]]; then
            echo "  \"worktrees_directory\": \"$worktrees_size\","
        else
            echo "Worktrees Directory: $worktrees_size"
        fi
        
        # Individual worktree sizes
        if [[ "$json_output" == "true" ]]; then
            echo "  \"individual_worktrees\": ["
        else
            echo
            print_info "Individual Worktree Sizes:"
        fi
        
        local first=true
        for worktree in "$WORKTREES_DIR"/*; do
            if [[ -d "$worktree" ]]; then
                local size
                size=$(du -sh "$worktree" 2>/dev/null | cut -f1 || echo "unknown")
                local name
                name=$(basename "$worktree")
                
                if [[ "$json_output" == "true" ]]; then
                    if [[ "$first" == "true" ]]; then
                        first=false
                    else
                        echo ","
                    fi
                    echo "    {\"name\": \"$name\", \"size\": \"$size\"}"
                else
                    echo "  $name: $size"
                fi
            fi
        done
        
        if [[ "$json_output" == "true" ]]; then
            echo "  ]"
        fi
    else
        if [[ "$json_output" == "true" ]]; then
            echo "  \"worktrees_directory\": \"not_found\""
        else
            echo "Worktrees Directory: Not found"
        fi
    fi
    
    # Git directory size
    local git_size
    git_size=$(du -sh "$PROJECT_ROOT/.git" 2>/dev/null | cut -f1 || echo "unknown")
    
    if [[ "$json_output" == "true" ]]; then
        echo ","
        echo "  \"git_directory\": \"$git_size\""
        echo "}"
    else
        echo "Git Directory: $git_size"
    fi
}

# Function to show detailed status
show_detailed_status() {
    local json_output="$1"
    
    print_header "=== Detailed Worktree Status ==="
    
    if ! git worktree list > /dev/null 2>&1; then
        if [[ "$json_output" == "true" ]]; then
            echo "{\"status\":\"no_worktrees\",\"message\":\"No worktrees found\"}"
        else
            print_warning "No worktrees found"
        fi
        return 0
    fi
    
    local worktrees
    worktrees=$(git worktree list --porcelain | grep "^worktree" | cut -d' ' -f2-)
    
    if [[ -z "$worktrees" ]]; then
        if [[ "$json_output" == "true" ]]; then
            echo "{\"status\":\"no_worktrees\",\"message\":\"No worktrees found\"}"
        else
            print_warning "No worktrees found"
        fi
        return 0
    fi
    
    if [[ "$json_output" == "true" ]]; then
        echo "["
    fi
    
    local index=1
    while IFS= read -r worktree_path; do
        if [[ -d "$worktree_path" ]]; then
            if [[ "$json_output" == "true" && "$index" -gt 1 ]]; then
                echo ","
            fi
            
            if [[ "$json_output" == "true" ]]; then
                get_worktree_info "$worktree_path" "true"
            else
                echo "Worktree $index: $(basename "$worktree_path")"
                get_worktree_info "$worktree_path" "false"
            fi
            
            ((index++))
        fi
    done <<< "$worktrees"
    
    if [[ "$json_output" == "true" ]]; then
        echo "]"
    fi
}

# Function to suggest cleanup actions
suggest_cleanup() {
    print_header "=== Cleanup Suggestions ==="
    
    local suggestions=()
    
    # Check for old worktrees
    if [[ -d "$WORKTREES_DIR" ]]; then
        for worktree in "$WORKTREES_DIR"/*; do
            if [[ -d "$worktree" && -f "$worktree/.ai-context" ]]; then
                local created_date
                created_date=$(grep "Created:" "$worktree/.ai-context" | cut -d: -f2- | xargs)
                if [[ -n "$created_date" ]]; then
                    local created_timestamp
                    created_timestamp=$(date -d "$created_date" +%s 2>/dev/null || echo "")
                    if [[ -n "$created_timestamp" ]]; then
                        local current_time
                        current_time=$(date +%s)
                        local age_days
                        age_days=$(( (current_time - created_timestamp) / 86400 ))
                        
                        if [[ "$age_days" -gt 7 ]]; then
                            suggestions+=("Worktree $(basename "$worktree") is $age_days days old - consider cleanup")
                        fi
                    fi
                fi
            fi
        done
    fi
    
    # Check for worktrees with no recent activity
    if git worktree list > /dev/null 2>&1; then
        local worktrees
        worktrees=$(git worktree list --porcelain | grep "^worktree" | cut -d' ' -f2-)
        
        while IFS= read -r worktree_path; do
            if [[ -d "$worktree_path" ]]; then
                cd "$worktree_path"
                local last_commit_date
                last_commit_date=$(git log -1 --format="%ci" 2>/dev/null || echo "")
                if [[ -n "$last_commit_date" ]]; then
                    local last_commit_timestamp
                    last_commit_timestamp=$(date -d "$last_commit_date" +%s 2>/dev/null || echo "")
                    if [[ -n "$last_commit_timestamp" ]]; then
                        local current_time
                        current_time=$(date +%s)
                        local days_since_commit
                        days_since_commit=$(( (current_time - last_commit_timestamp) / 86400 ))
                        
                        if [[ "$days_since_commit" -gt 3 ]]; then
                            suggestions+=("Worktree $(basename "$worktree_path") has no commits for $days_since_commit days")
                        fi
                    fi
                fi
            fi
        done <<< "$worktrees"
    fi
    
    if [[ ${#suggestions[@]} -eq 0 ]]; then
        print_success "No cleanup suggestions - all worktrees are in good condition"
    else
        print_warning "Cleanup suggestions:"
        for suggestion in "${suggestions[@]}"; do
            echo "  - $suggestion"
        done
        
        echo
        print_info "Use './scripts/cleanup-worktrees.sh' to remove worktrees"
        print_info "Use 'git worktree prune' to clean up stale references"
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
    local show_health="false"
    local show_disk="false"
    local show_status="false"
    local show_cleanup="false"
    local json_output="false"
    local show_all="true"
    
    while [[ $# -gt 0 ]]; do
        case $1 in
            --health)
                show_health="true"
                show_all="false"
                shift
                ;;
            --disk)
                show_disk="true"
                show_all="false"
                shift
                ;;
            --status)
                show_status="true"
                show_all="false"
                shift
                ;;
            --cleanup)
                show_cleanup="true"
                show_all="false"
                shift
                ;;
            --json)
                json_output="true"
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
            *)
                print_error "Unknown option: $1"
                show_usage
                exit 1
                ;;
        esac
    done
    
    # Change to project root
    cd "$PROJECT_ROOT"
    
    # Show information based on options
    if [[ "$show_all" == "true" ]]; then
        check_worktree_health "$json_output"
        echo
        show_disk_usage "$json_output"
        echo
        show_detailed_status "$json_output"
        echo
        suggest_cleanup
    else
        if [[ "$show_health" == "true" ]]; then
            check_worktree_health "$json_output"
        fi
        
        if [[ "$show_disk" == "true" ]]; then
            show_disk_usage "$json_output"
        fi
        
        if [[ "$show_status" == "true" ]]; then
            show_detailed_status "$json_output"
        fi
        
        if [[ "$show_cleanup" == "true" ]]; then
            suggest_cleanup
        fi
    fi
}

# Run main function with all arguments
main "$@"
