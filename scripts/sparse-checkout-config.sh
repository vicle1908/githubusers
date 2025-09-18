#!/usr/bin/env bash
set -euo pipefail

# Dynamic sparse-checkout configurator for composite Gradle builds
# - Parses root settings.gradle.kts includeBuild("<module>") entries
# - Falls back to scanning for */settings.gradle.kts files
# - Supports macOS/BSD find syntax (no GNU -printf)
#
# Usage:
#   scripts/sparse-checkout-config.sh --apply [--path <worktree_path>]
#   scripts/sparse-checkout-config.sh --dry-run [--path <worktree_path>]
#
# Notes:
# - Uses non-cone mode to include required root files if needed.
# - Emits a stable, sorted pattern list.

WORKTREE_PATH="${PWD}"
MODE="apply" # or "dry-run"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --path|-p)
      WORKTREE_PATH="$2"; shift 2;;
    --apply)
      MODE="apply"; shift;;
    --dry-run|-n)
      MODE="dry-run"; shift;;
    -h|--help)
      echo "Usage: $0 [--apply|--dry-run] [--path <worktree_path>]"; exit 0;;
    *)
      echo "Unknown arg: $1" >&2; exit 1;;
  esac
done

cd "$WORKTREE_PATH"

if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  echo "Error: $WORKTREE_PATH is not a git worktree" >&2
  exit 1
fi

root_settings="settings.gradle.kts"

list_modules_from_settings() {
  if [[ -f "$root_settings" ]]; then
    # Strip // comments then extract includeBuild("...") targets using portable sed
    sed -E 's#//.*$##' "$root_settings" \
      | sed -nE 's/.*includeBuild\("([^"]+)"\).*/\1/p' \
      | sed -E 's#^\./##; s#^/##; s#/$##'
  fi
}

list_modules_by_scan() {
  # Look for module-local settings.gradle.kts in first two levels (exclude root)
  find . -mindepth 1 -maxdepth 2 -type f -name 'settings.gradle.kts' \
    -not -path './settings.gradle.kts' -exec dirname {} \; \
    | sed 's#^\./##' | sort -u
}

# Collect module directories
modules=$(list_modules_from_settings || true)
if [[ -z "${modules}" ]]; then
  modules=$(list_modules_by_scan || true)
fi

# Ensure app is always present if exists
if [[ -d "app" ]] && ! echo "$modules" | grep -qx "app"; then
  modules="$modules
app"
fi

# Always include key root-level directories commonly required for builds and CI
root_dirs=(
  ".github"
  "scripts"
  "catalog"
  "plugins"
  "testing"
  "gradle"
  ".claude"
  "docs"
)

# Compose sparse patterns (non-cone mode for root files)
patterns=()
# Root files typically needed by Gradle orchestrator
for f in "build.gradle.kts" "settings.gradle.kts" "gradle.properties" \
         ".editorconfig" ".gitignore"; do
  if [[ -f "$f" ]]; then
    patterns+=("/$f")
  fi
done

# Root directories
for d in "${root_dirs[@]}"; do
  if [[ -d "$d" ]]; then
    patterns+=("/$d/**")
  fi
done

# Module directories
while IFS= read -r m; do
  [[ -z "$m" ]] && continue
  [[ ! -d "$m" ]] && continue
  patterns+=("/$m/**")
done <<< "$(echo "$modules" | sort -u)"

# De-duplicate
mapfile -t patterns < <(printf "%s\n" "${patterns[@]}" | awk '!seen[$0]++' | sort)

if [[ "$MODE" == "dry-run" ]]; then
  printf "Would configure sparse-checkout with patterns (count=%d):\n" "${#patterns[@]}"
  printf "%s\n" "${patterns[@]}"
  exit 0
fi

# Apply sparse-checkout using non-cone so we can include root files too
if ! git sparse-checkout init --no-cone >/dev/null 2>&1; then
  # If already initialized, proceed
  :
fi

# Write patterns atomically
SPARSE_FILE="$(git rev-parse --git-path info/sparse-checkout)"
mkdir -p "$(dirname "$SPARSE_FILE")"
{
  for p in "${patterns[@]}"; do
    printf "%s\n" "$p"
  done
} > "$SPARSE_FILE"

echo "Reapplying sparse-checkout (patterns: ${#patterns[@]})..."
git sparse-checkout reapply

echo "Sparse-checkout updated successfully."