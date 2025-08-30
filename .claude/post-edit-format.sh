#!/bin/bash

# Claude Code Post-Edit Hook: Auto-format Kotlin files with detekt
# Runs after Edit/MultiEdit operations to maintain code style

set -euo pipefail

# Function to log messages
log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $*" >&2
}

# Read hook input from stdin
input=$(cat)

# Parse tool information from JSON input
tool_name=$(echo "$input" | jq -r '.tool_name // empty')
file_path=$(echo "$input" | jq -r '.tool_input.file_path // empty')

log "Hook triggered by tool: $tool_name"
log "File modified: $file_path"

# Only run for Kotlin files (.kt, .kts)
if [[ "$file_path" =~ \.(kt|kts)$ ]]; then
    log "Kotlin file detected, running detekt auto-correct..."
    
    # Change to project root directory
    cd "$(dirname "$0")/.."
    
    # Run detekt auto-correct
    if ./gradlew detekt --auto-correct --quiet > /dev/null 2>&1; then
        log "✅ Code formatting completed successfully"
    else
        log "⚠️  Detekt auto-correct encountered issues (non-critical)"
    fi
else
    log "Skipping formatting for non-Kotlin file"
fi

# Exit successfully to allow the edit to proceed
exit 0