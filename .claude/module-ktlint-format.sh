#!/bin/bash

# Claude Code Post-Edit Hook: File-specific ktlint formatting per module
# Runs after Edit/MultiEdit/Write to format only the affected Kotlin file(s)

declare -a file_paths=()
declare -a kotlin_files=()

# Function to log messages
log() {
  echo "[$(date '+%Y-%m-%d %H:%M:%S')] $*" >&2
}

set -euo pipefail

# Read hook input from stdin (JSON)
input=$(cat)

# Parse tool information from JSON input
tool_name=$(echo "$input" | jq -r '.tool_name // empty')

# Extract file paths: support single file_path or array from .tool_input.files[] or .tool_input.edits[].file_path
if jq -e '.tool_input.file_path' <<< "$input" > /dev/null 2>&1; then
  single_file=$(jq -r '.tool_input.file_path // empty' <<< "$input")
  if [ -n "$single_file" ]; then
    file_paths+=("$single_file")
  fi
elif jq -e '.tool_input.files' <<< "$input" > /dev/null 2>&1; then
  jq -r '.tool_input.files[] // empty' <<< "$input" | while IFS= read -r fp; do
    if [ -n "$fp" ]; then
      file_paths+=("$fp")
    fi
  done
elif jq -e '.tool_input.edits' <<< "$input" > /dev/null 2>&1; then
  jq -r '.tool_input.edits[].file_path // empty' <<< "$input" | while IFS= read -r fp; do
    if [ -n "$fp" ]; then
      file_paths+=("$fp")
    fi
  done
else
  single_file=$(jq -r '.tool_input.file_path // empty' <<< "$input")
  if [ -n "$single_file" ]; then
    file_paths+=("$single_file")
  fi
fi

log "Hook triggered by tool: $tool_name"
log "Files to process: ${file_paths[*]:-none}"

# Filter to Kotlin files only
if [ ${#file_paths[@]} -gt 0 ]; then
  for fp in "${file_paths[@]}"; do
    if [[ "$fp" =~ \.(kt|kts)$ ]]; then
      kotlin_files+=("$fp")
    fi
  done
fi

if [ ${#kotlin_files[@]} -eq 0 ]; then
  log "No Kotlin files to process. Skipping."
  exit 0
fi

log "Processing ${#kotlin_files[@]} Kotlin file(s)..."

# Change to project root
cd "$(dirname "$0")/.."

# Prepare module-file pairs array (Bash 3 compatible)
module_pairs=()
for fp in "${kotlin_files[@]}"; do
  # Extract module and relative path
  module=$(echo "$fp" | sed -n 's|.*/\([a-zA-Z0-9-]*\)/src/.*|\1|p')
  relative_path="src/${fp#*/src/}"

  if [ -z "$module" ] || [ -z "$relative_path" ]; then
    log "Warning: Could not parse module/path for $fp. Skipping."
    continue
  fi

  module_pairs+=("$module $relative_path")
  log "Added $relative_path to module $module"
done

if [ ${#module_pairs[@]} -eq 0 ]; then
  log "No valid module-file pairs. Skipping."
  exit 0
fi

# Sort module_pairs by module to group them
IFS=$'\n' sorted_pairs=($(sort <<<"${module_pairs[*]}"))
unset IFS

# Run ktlint and detekt per module group
current_module=""
files_str=""
i=0
while [ $i -lt ${#sorted_pairs[@]} ]; do
  pair="${sorted_pairs[$i]}"
  module=$(echo "$pair" | cut -d' ' -f1)
  rel_path=$(echo "$pair" | cut -d' ' -f2-)

  if [ "$module" != "$current_module" ]; then
    # Process previous group if exists
    if [ -n "$current_module" ] && [ -n "$files_str" ]; then
      # Run ktlint
      ktlint_task=":$current_module:ktlintFormat"
      log "Running $ktlint_task on: $files_str"
      if ./gradlew "$ktlint_task" $files_str > /dev/null 2>&1; then
        num_files=$(echo "$files_str" | wc -w)
        log "✅ ktlint formatting completed for module $current_module ($num_files files)"
      else
        num_files=$(echo "$files_str" | wc -w)
        log "⚠️ ktlint format issues for module $current_module ($num_files files)"
      fi

      # Run detekt autocorrect
      detekt_task=":$current_module:detekt"
      log "Running $detekt_task --auto-correct on: $files_str"
      if ./gradlew "$detekt_task" --auto-correct $files_str > /dev/null 2>&1; then
        log "✅ detekt autocorrect completed for module $current_module"
      else
        log "⚠️ detekt autocorrect issues for module $current_module (non-critical)"
      fi
    fi
    current_module="$module"
    files_str="$rel_path"
  else
    files_str="$files_str $rel_path"
  fi
  i=$((i + 1))
done

# Process the last group
if [ -n "$current_module" ] && [ -n "$files_str" ]; then
  # Run ktlint
  ktlint_task=":$current_module:ktlintFormat"
  log "Running $ktlint_task on: $files_str"
  if ./gradlew "$ktlint_task" $files_str > /dev/null 2>&1; then
    num_files=$(echo "$files_str" | wc -w)
    log "✅ ktlint formatting completed for module $current_module ($num_files files)"
  else
    num_files=$(echo "$files_str" | wc -w)
    log "⚠️ ktlint format issues for module $current_module ($num_files files)"
  fi

  # Run detekt autocorrect
  detekt_task=":$current_module:detekt"
  log "Running $detekt_task --auto-correct on: $files_str"
  if ./gradlew "$detekt_task" --auto-correct $files_str > /dev/null 2>&1; then
    log "✅ detekt autocorrect completed for module $current_module"
  else
    log "⚠️ detekt autocorrect issues for module $current_module (non-critical)"
  fi
fi

log "Multi-file ktlint and detekt formatting completed."
exit 0