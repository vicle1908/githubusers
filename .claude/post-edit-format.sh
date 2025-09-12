#!/bin/bash

# Claude Code Post-Edit Hook: Module-specific detekt auto-correct
# Runs after Edit/MultiEdit on Kotlin files to maintain code style

set -euo pipefail

# Function to log messages
log() {
  echo "[$(date '+%Y-%m-%d %H:%M:%S')] $*" >&2
}

# Read input: arg or stdin JSON
log "Input mode: args ($#) or stdin?"
if [ $# -gt 0 ]; then
  file_path="$1"
  tool_name="getDiagnostics"  # Default for pre-diagnostics
  log "Using arg input: $file_path"
elif [ -p /dev/stdin ]; then
  input=$(cat)
  tool_name=$(echo "$input" | jq -r '.tool_name // empty')
  file_path=$(echo "$input" | jq -r '.tool_input.file_path // empty')
  log "Using stdin input: $file_path"
else
  log "No input provided. Skipping."
  exit 0
fi

if [ -z "$file_path" ]; then
  log "No valid file_path. Skipping."
  exit 0
fi
log "Hook triggered by tool: $tool_name"
log "File modified: $file_path"

# Only run for Kotlin files (.kt, .kts)
if [[ "$file_path" =~ \.(kt|kts)$ ]]; then
  log "Kotlin file detected, running module-specific detekt auto-correct..."
  
  # Change to project root directory
  cd "$(dirname "$0")/.."
  
  # Extract module from file_path (e.g., feature-users from /.../feature-users/src/...)
  module=$(echo "$file_path" | sed -n 's|.*/\([a-zA-Z0-9-]*\)/src/.*|\1|p')
  if [ -z "$module" ]; then
    log "Warning: Could not parse module from $file_path. Falling back to full project detekt."
    if ./gradlew detekt --auto-correct --quiet > /dev/null 2>&1; then
      log "✅ Detekt auto-correct completed for full project"
    else
      log "⚠️  Detekt auto-correct encountered issues (non-critical)"
    fi
  else
    # Run detekt auto-correct for the specific module
    module_task=":$module:detekt"
    if ./gradlew "$module_task" --auto-correct --quiet > /dev/null 2>&1; then
      log "✅ Detekt auto-correct completed for module $module"
    else
      log "⚠️  Detekt auto-correct encountered issues for module $module (non-critical)"
    fi
  fi
else
  log "Skipping formatting for non-Kotlin file"
fi

# Exit successfully to allow the edit to proceed
exit 0
