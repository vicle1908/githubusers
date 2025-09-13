#!/bin/bash

# Worktree Monitoring and Cleanup Automation
# Comprehensive monitoring system for Git Worktree health and performance
# Usage: ./scripts/worktree-monitoring.sh [monitor|cleanup|report|alert] [options]

set -euo pipefail

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
# Worktrees are now created outside the main repository
WORKTREES_DIR="$(dirname "$PROJECT_ROOT")/githubusers-worktrees"
LOG_DIR="$PROJECT_ROOT/logs"
MONITORING_CONFIG="$PROJECT_ROOT/.worktree-monitoring.conf"

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

# Default monitoring configuration
DEFAULT_CONFIG="
# Worktree Monitoring Configuration
STALE_THRESHOLD_DAYS=7
DISK_USAGE_THRESHOLD_GB=5
MAX_WORKTREES=10
CLEANUP_DRY_RUN=true
ALERT_EMAIL=
LOG_RETENTION_DAYS=30
HEALTH_CHECK_INTERVAL_MINUTES=60
"

# Function to initialize monitoring configuration
init_monitoring_config() {
    if [[ ! -f "$MONITORING_CONFIG" ]]; then
        print_info "Creating monitoring configuration..."
        echo "$DEFAULT_CONFIG" > "$MONITORING_CONFIG"
        print_success "Configuration created: $MONITORING_CONFIG"
    fi
}

# Function to load monitoring configuration
load_config() {
    if [[ -f "$MONITORING_CONFIG" ]]; then
        source "$MONITORING_CONFIG"
    else
        # Use defaults
        STALE_THRESHOLD_DAYS=7
        DISK_USAGE_THRESHOLD_GB=5
        MAX_WORKTREES=10
        CLEANUP_DRY_RUN=true
        ALERT_EMAIL=""
        LOG_RETENTION_DAYS=30
        HEALTH_CHECK_INTERVAL_MINUTES=60
    fi
}

# Function to show usage
show_usage() {
    cat << EOF
Usage: $0 <command> [options]

Worktree Monitoring and Cleanup Automation

Commands:
  monitor              Run comprehensive monitoring check
  cleanup              Perform automated cleanup
  report               Generate monitoring report
  alert                Send alerts for issues
  config               Show/edit monitoring configuration
  health               Quick health check
  metrics              Show performance metrics

Options:
  --force              Force operations without confirmation
  --dry-run            Show what would be done without executing
  --verbose            Show detailed output
  --config <file>      Use custom configuration file
  --help               Show this help message

Examples:
  $0 monitor
  $0 cleanup --dry-run
  $0 report --verbose
  $0 alert
  $0 config
  $0 health

EOF
}

# Function to create log directory
ensure_log_dir() {
    if [[ ! -d "$LOG_DIR" ]]; then
        mkdir -p "$LOG_DIR"
    fi
}

# Function to log message
log_message() {
    local level="$1"
    local message="$2"
    local timestamp
    timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    
    ensure_log_dir
    
    echo "[$timestamp] [$level] $message" >> "$LOG_DIR/worktree-monitoring.log"
    
    case "$level" in
        ERROR)
            print_error "$message"
            ;;
        WARNING)
            print_warning "$message"
            ;;
        SUCCESS)
            print_success "$message"
            ;;
        *)
            print_info "$message"
            ;;
    esac
}

# Function to get worktree metrics
get_worktree_metrics() {
    local worktree_path="$1"
    
    if [[ ! -d "$worktree_path" ]]; then
        return 1
    fi
    
    cd "$worktree_path"
    
    # Get basic metrics
    local branch_name
    branch_name=$(git branch --show-current 2>/dev/null || echo "unknown")
    
    local last_commit_date
    last_commit_date=$(git log -1 --format="%ci" 2>/dev/null || echo "")
    
    local disk_usage
    disk_usage=$(du -sm "$worktree_path" 2>/dev/null | cut -f1 || echo "0")
    
    local file_count
    file_count=$(find "$worktree_path" -type f | wc -l)
    
    local uncommitted_changes
    uncommitted_changes=$(git status --porcelain | wc -l)
    
    # Calculate age
    local age_days=""
    if [[ -n "$last_commit_date" ]]; then
        local last_commit_timestamp
        last_commit_timestamp=$(date -d "$last_commit_date" +%s 2>/dev/null || echo "")
        if [[ -n "$last_commit_timestamp" ]]; then
            local current_time
            current_time=$(date +%s)
            age_days=$(( (current_time - last_commit_timestamp) / 86400 ))
        fi
    fi
    
    # Check if branch is up to date
    local is_up_to_date="unknown"
    if git rev-parse --verify "origin/$branch_name" >/dev/null 2>&1; then
        if git diff --quiet "origin/$branch_name"..HEAD; then
            is_up_to_date="true"
        else
            is_up_to_date="false"
        fi
    fi
    
    # Output metrics as JSON-like format
    cat << EOF
{
  "path": "$worktree_path",
  "branch": "$branch_name",
  "last_commit_date": "$last_commit_date",
  "age_days": "$age_days",
  "disk_usage_mb": "$disk_usage",
  "file_count": "$file_count",
  "uncommitted_changes": "$uncommitted_changes",
  "is_up_to_date": "$is_up_to_date"
}
EOF
}

# Function to run comprehensive monitoring
run_monitoring() {
    local verbose="$1"
    
    print_header "=== Worktree Monitoring Report ==="
    log_message "INFO" "Starting comprehensive monitoring check"
    
    local total_worktrees=0
    local healthy_worktrees=0
    local stale_worktrees=0
    local large_worktrees=0
    local issues=()
    
    # Get all worktrees
    if ! git worktree list > /dev/null 2>&1; then
        log_message "WARNING" "No worktrees found"
        return 0
    fi
    
    local worktrees
    worktrees=$(git worktree list --porcelain | grep "^worktree" | cut -d' ' -f2-)
    
    if [[ -z "$worktrees" ]]; then
        log_message "WARNING" "No worktrees found"
        return 0
    fi
    
    while IFS= read -r worktree_path; do
        if [[ -d "$worktree_path" ]]; then
            ((total_worktrees++))
            
            local metrics
            metrics=$(get_worktree_metrics "$worktree_path")
            
            local branch_name
            branch_name=$(echo "$metrics" | grep '"branch"' | cut -d'"' -f4)
            
            local age_days
            age_days=$(echo "$metrics" | grep '"age_days"' | cut -d'"' -f4)
            
            local disk_usage_mb
            disk_usage_mb=$(echo "$metrics" | grep '"disk_usage_mb"' | cut -d'"' -f4)
            
            local uncommitted_changes
            uncommitted_changes=$(echo "$metrics" | grep '"uncommitted_changes"' | cut -d'"' -f4)
            
            local is_up_to_date
            is_up_to_date=$(echo "$metrics" | grep '"is_up_to_date"' | cut -d'"' -f4)
            
            if [[ "$verbose" == "true" ]]; then
                echo "Worktree: $(basename "$worktree_path")"
                echo "  Branch: $branch_name"
                echo "  Age: ${age_days} days"
                echo "  Disk Usage: ${disk_usage_mb} MB"
                echo "  Uncommitted Changes: $uncommitted_changes"
                echo "  Up to Date: $is_up_to_date"
                echo
            fi
            
            # Check for issues
            local worktree_issues=()
            
            # Check for stale worktrees
            if [[ -n "$age_days" && "$age_days" -gt "$STALE_THRESHOLD_DAYS" ]]; then
                worktree_issues+=("stale (${age_days} days old)")
                ((stale_worktrees++))
            fi
            
            # Check for large worktrees
            local disk_usage_gb=$((disk_usage_mb / 1024))
            if [[ "$disk_usage_gb" -gt "$DISK_USAGE_THRESHOLD_GB" ]]; then
                worktree_issues+=("large (${disk_usage_gb} GB)")
                ((large_worktrees++))
            fi
            
            # Check for uncommitted changes
            if [[ "$uncommitted_changes" -gt 0 ]]; then
                worktree_issues+=("uncommitted changes ($uncommitted_changes files)")
            fi
            
            # Check if up to date
            if [[ "$is_up_to_date" == "false" ]]; then
                worktree_issues+=("not up to date with origin")
            fi
            
            if [[ ${#worktree_issues[@]} -eq 0 ]]; then
                ((healthy_worktrees++))
            else
                issues+=("$(basename "$worktree_path"): ${worktree_issues[*]}")
            fi
        fi
    done <<< "$worktrees"
    
    # Generate summary
    echo
    print_header "=== Monitoring Summary ==="
    echo "Total Worktrees: $total_worktrees"
    echo "Healthy Worktrees: $healthy_worktrees"
    echo "Stale Worktrees: $stale_worktrees"
    echo "Large Worktrees: $large_worktrees"
    echo "Issues Found: ${#issues[@]}"
    
    if [[ ${#issues[@]} -gt 0 ]]; then
        echo
        print_warning "Issues Found:"
        for issue in "${issues[@]}"; do
            echo "  - $issue"
        done
    fi
    
    # Log summary
    log_message "INFO" "Monitoring completed: $healthy_worktrees/$total_worktrees healthy worktrees"
    
    if [[ ${#issues[@]} -gt 0 ]]; then
        log_message "WARNING" "Found ${#issues[@]} issues requiring attention"
    fi
}

# Function to perform automated cleanup
run_cleanup() {
    local force="$1"
    local dry_run="$2"
    local verbose="$3"
    
    print_header "=== Automated Worktree Cleanup ==="
    log_message "INFO" "Starting automated cleanup (dry_run=$dry_run, force=$force)"
    
    local cleaned_count=0
    local skipped_count=0
    
    # Get all worktrees
    if ! git worktree list > /dev/null 2>&1; then
        log_message "WARNING" "No worktrees found for cleanup"
        return 0
    fi
    
    local worktrees
    worktrees=$(git worktree list --porcelain | grep "^worktree" | cut -d' ' -f2-)
    
    if [[ -z "$worktrees" ]]; then
        log_message "WARNING" "No worktrees found for cleanup"
        return 0
    fi
    
    while IFS= read -r worktree_path; do
        if [[ -d "$worktree_path" ]]; then
            local metrics
            metrics=$(get_worktree_metrics "$worktree_path")
            
            local branch_name
            branch_name=$(echo "$metrics" | grep '"branch"' | cut -d'"' -f4)
            
            local age_days
            age_days=$(echo "$metrics" | grep '"age_days"' | cut -d'"' -f4)
            
            local uncommitted_changes
            uncommitted_changes=$(echo "$metrics" | grep '"uncommitted_changes"' | cut -d'"' -f4)
            
            # Check if worktree should be cleaned up
            local should_cleanup=false
            local cleanup_reason=""
            
            # Check for stale worktrees
            if [[ -n "$age_days" && "$age_days" -gt "$STALE_THRESHOLD_DAYS" ]]; then
                should_cleanup=true
                cleanup_reason="stale (${age_days} days old)"
            fi
            
            # Check for merged branches
            if [[ "$branch_name" != "main" && "$branch_name" != "master" && "$branch_name" != "develop" ]]; then
                if git merge-base --is-ancestor "$worktree_path" origin/main 2>/dev/null; then
                    should_cleanup=true
                    cleanup_reason="merged to main"
                fi
            fi
            
            if [[ "$should_cleanup" == "true" ]]; then
                if [[ "$verbose" == "true" ]]; then
                    echo "Worktree: $(basename "$worktree_path")"
                    echo "  Branch: $branch_name"
                    echo "  Reason: $cleanup_reason"
                    echo "  Uncommitted Changes: $uncommitted_changes"
                fi
                
                if [[ "$dry_run" == "true" ]]; then
                    echo "  [DRY RUN] Would cleanup: $cleanup_reason"
                    ((skipped_count++))
                else
                    if [[ "$uncommitted_changes" -gt 0 && "$force" != "true" ]]; then
                        echo "  [SKIP] Has uncommitted changes (use --force to override)"
                        ((skipped_count++))
                    else
                        echo "  [CLEANUP] Removing worktree: $cleanup_reason"
                        if ./scripts/cleanup-worktrees.sh "$worktree_path" --force; then
                            ((cleaned_count++))
                            log_message "SUCCESS" "Cleaned up worktree: $(basename "$worktree_path") ($cleanup_reason)"
                        else
                            log_message "ERROR" "Failed to cleanup worktree: $(basename "$worktree_path")"
                        fi
                    fi
                fi
                echo
            else
                ((skipped_count++))
            fi
        fi
    done <<< "$worktrees"
    
    # Generate cleanup summary
    echo
    print_header "=== Cleanup Summary ==="
    echo "Worktrees Cleaned: $cleaned_count"
    echo "Worktrees Skipped: $skipped_count"
    
    log_message "INFO" "Cleanup completed: $cleaned_count cleaned, $skipped_count skipped"
}

# Function to generate monitoring report
generate_report() {
    local verbose="$1"
    
    print_header "=== Worktree Monitoring Report ==="
    
    local report_file="$LOG_DIR/worktree-report-$(date +%Y%m%d-%H%M%S).md"
    ensure_log_dir
    
    cat > "$report_file" << EOF
# Worktree Monitoring Report

**Generated**: $(date)
**Project**: $(basename "$PROJECT_ROOT")

## Summary

EOF
    
    # Run monitoring and capture output
    local monitoring_output
    monitoring_output=$(run_monitoring "$verbose" 2>&1)
    
    echo "$monitoring_output" >> "$report_file"
    
    # Add detailed metrics
    cat >> "$report_file" << EOF

## Detailed Metrics

EOF
    
    # Get all worktrees and their metrics
    if git worktree list > /dev/null 2>&1; then
        local worktrees
        worktrees=$(git worktree list --porcelain | grep "^worktree" | cut -d' ' -f2-)
        
        while IFS= read -r worktree_path; do
            if [[ -d "$worktree_path" ]]; then
                local metrics
                metrics=$(get_worktree_metrics "$worktree_path")
                
                cat >> "$report_file" << EOF

### $(basename "$worktree_path")

\`\`\`json
$metrics
\`\`\`

EOF
            fi
        done <<< "$worktrees"
    fi
    
    # Add recommendations
    cat >> "$report_file" << EOF

## Recommendations

1. **Regular Cleanup**: Run automated cleanup weekly
2. **Monitor Disk Usage**: Keep worktrees under $DISK_USAGE_THRESHOLD_GB GB
3. **Stale Worktrees**: Clean up worktrees older than $STALE_THRESHOLD_DAYS days
4. **Branch Management**: Delete merged branches promptly
5. **Health Monitoring**: Run health checks daily

## Next Steps

- Review stale worktrees and clean up if appropriate
- Monitor disk usage and optimize sparse-checkout patterns
- Update branch policies if needed
- Consider archiving old AI worktrees

EOF
    
    print_success "Report generated: $report_file"
    log_message "SUCCESS" "Generated monitoring report: $report_file"
}

# Function to send alerts
send_alerts() {
    print_header "=== Worktree Alerts ==="
    
    local alert_count=0
    local alerts=()
    
    # Check for critical issues
    if git worktree list > /dev/null 2>&1; then
        local worktrees
        worktrees=$(git worktree list --porcelain | grep "^worktree" | cut -d' ' -f2-)
        
        while IFS= read -r worktree_path; do
            if [[ -d "$worktree_path" ]]; then
                local metrics
                metrics=$(get_worktree_metrics "$worktree_path")
                
                local branch_name
                branch_name=$(echo "$metrics" | grep '"branch"' | cut -d'"' -f4)
                
                local age_days
                age_days=$(echo "$metrics" | grep '"age_days"' | cut -d'"' -f4)
                
                local disk_usage_mb
                disk_usage_mb=$(echo "$metrics" | grep '"disk_usage_mb"' | cut -d'"' -f4)
                
                # Check for critical issues
                if [[ -n "$age_days" && "$age_days" -gt 14 ]]; then
                    alerts+=("CRITICAL: Worktree $(basename "$worktree_path") is ${age_days} days old")
                    ((alert_count++))
                fi
                
                local disk_usage_gb=$((disk_usage_mb / 1024))
                if [[ "$disk_usage_gb" -gt 10 ]]; then
                    alerts+=("WARNING: Worktree $(basename "$worktree_path") is ${disk_usage_gb} GB")
                    ((alert_count++))
                fi
            fi
        done <<< "$worktrees"
    fi
    
    if [[ $alert_count -gt 0 ]]; then
        print_warning "Found $alert_count alerts:"
        for alert in "${alerts[@]}"; do
            echo "  - $alert"
            log_message "WARNING" "$alert"
        done
        
        # Send email alert if configured
        if [[ -n "${ALERT_EMAIL:-}" ]]; then
            echo "Sending email alert to: $ALERT_EMAIL"
            # Email sending logic would go here
        fi
    else
        print_success "No alerts found"
        log_message "SUCCESS" "No alerts found"
    fi
}

# Function to show configuration
show_config() {
    print_header "=== Monitoring Configuration ==="
    
    if [[ -f "$MONITORING_CONFIG" ]]; then
        cat "$MONITORING_CONFIG"
    else
        print_warning "Configuration file not found: $MONITORING_CONFIG"
        print_info "Run with --init to create default configuration"
    fi
}

# Function to run quick health check
run_health_check() {
    print_header "=== Quick Health Check ==="
    
    local issues=0
    
    # Check if we're in a Git repository
    if ! git rev-parse --git-dir > /dev/null 2>&1; then
        print_error "Not in a Git repository"
        ((issues++))
    else
        print_success "Git repository: OK"
    fi
    
    # Check worktree directory
    if [[ -d "$WORKTREES_DIR" ]]; then
        print_success "Worktrees directory: OK"
    else
        print_warning "Worktrees directory not found: $WORKTREES_DIR"
    fi
    
    # Check scripts
    local scripts=("create-worktree.sh" "cleanup-worktrees.sh" "monitor-worktrees.sh")
    for script in "${scripts[@]}"; do
        if [[ -x "$SCRIPT_DIR/$script" ]]; then
            print_success "Script $script: OK"
        else
            print_error "Script $script: Missing or not executable"
            ((issues++))
        fi
    done
    
    # Check configuration
    if [[ -f "$MONITORING_CONFIG" ]]; then
        print_success "Monitoring configuration: OK"
    else
        print_warning "Monitoring configuration: Missing"
    fi
    
    # Check log directory
    if [[ -d "$LOG_DIR" ]]; then
        print_success "Log directory: OK"
    else
        print_warning "Log directory: Missing"
    fi
    
    echo
    if [[ $issues -eq 0 ]]; then
        print_success "Health check passed: No issues found"
    else
        print_error "Health check failed: $issues issues found"
    fi
}

# Function to show performance metrics
show_metrics() {
    print_header "=== Performance Metrics ==="
    
    # System metrics
    echo "System Information:"
    echo "  OS: $(uname -s)"
    echo "  Architecture: $(uname -m)"
    echo "  Disk Usage: $(df -h . | awk 'NR==2 {print $5}')"
    echo "  Memory Usage: $(free -h | awk 'NR==2 {print $3 "/" $2}')"
    echo
    
    # Git metrics
    echo "Git Repository:"
    echo "  Repository Size: $(du -sh .git | cut -f1)"
    echo "  Total Commits: $(git rev-list --count HEAD)"
    echo "  Active Branches: $(git branch -r | wc -l)"
    echo
    
    # Worktree metrics
    if git worktree list > /dev/null 2>&1; then
        local worktree_count
        worktree_count=$(git worktree list | wc -l)
        echo "Worktrees:"
        echo "  Total Worktrees: $worktree_count"
        echo "  Worktrees Directory: $WORKTREES_DIR"
        
        if [[ -d "$WORKTREES_DIR" ]]; then
            local worktrees_size
            worktrees_size=$(du -sh "$WORKTREES_DIR" 2>/dev/null | cut -f1 || echo "0")
            echo "  Total Size: $worktrees_size"
        fi
    else
        echo "Worktrees: None found"
    fi
}

# Main function
main() {
    # Initialize monitoring
    init_monitoring_config
    load_config
    
    # Parse arguments
    local command=""
    local force="false"
    local dry_run="false"
    local verbose="false"
    local config_file=""
    
    while [[ $# -gt 0 ]]; do
        case $1 in
            monitor|cleanup|report|alert|config|health|metrics)
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
            --verbose)
                verbose="true"
                shift
                ;;
            --config)
                config_file="$2"
                shift 2
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
    
    # Use custom config if specified
    if [[ -n "$config_file" ]]; then
        MONITORING_CONFIG="$config_file"
        load_config
    fi
    
    # Change to project root
    cd "$PROJECT_ROOT"
    
    # Execute command
    case "$command" in
        monitor)
            run_monitoring "$verbose"
            ;;
        cleanup)
            run_cleanup "$force" "$dry_run" "$verbose"
            ;;
        report)
            generate_report "$verbose"
            ;;
        alert)
            send_alerts
            ;;
        config)
            show_config
            ;;
        health)
            run_health_check
            ;;
        metrics)
            show_metrics
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
