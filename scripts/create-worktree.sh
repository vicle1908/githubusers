#!/usr/bin/env bash
set -euo pipefail

# Create a git worktree (full checkout) and verify that all AI assistant
# folders are present if they exist on the source branch. Nothing is excluded
# beyond the repository's own .gitignore rules.
#
# Usage:
#   scripts/create-worktree.sh <source_branch> <worktree_path|auto|-> [<new_branch_name>]
#
# Examples:
#   scripts/create-worktree.sh main /path/to/worktrees/feature-x feature/feature-x
#   scripts/create-worktree.sh main auto ai/security/update-001
#
# Notes:
# - Requires Git 2.25+ for reliable worktree behavior.
# - If worktree_path is "auto" or "-", the path will be derived as:
#   ${WORKTREES_DIR:-"<repo-parent>/<repo-name>-worktrees"}/<sanitized-branch>
#   where <sanitized-branch> replaces '/' and ':' with '-' or '_'.
# - AI assistant folders checked by default: .claude, .github, .cursor, .ai-context, .augment, .trae, .zed, .kilocode, .vscode, .githooks
#   You can extend/override via env var AI_ASSISTANT_DIRS (space-separated).

if [[ $# -lt 2 ]]; then
  echo "Usage: $0 <source_branch> <worktree_path> [<new_branch_name>]" >&2
  exit 1
fi

SRC_BRANCH="$1"
WORKTREE_PATH="$2"
NEW_BRANCH="${3:-}"

# Resolve repo & base worktrees dir for auto path derivation
REPO_ROOT=$(git rev-parse --show-toplevel)
REPO_NAME=$(basename "$REPO_ROOT")
DEFAULT_WORKTREES_DIR="$(dirname "$REPO_ROOT")/${REPO_NAME}-worktrees"
WORKTREES_DIR="${WORKTREES_DIR:-$DEFAULT_WORKTREES_DIR}"

sanitize_branch_name() {
  # Replace path separators and reserved chars with safe ones
  echo "$1" | tr '/:' '-_'
}

# Auto-derive path if requested
if [[ "$WORKTREE_PATH" == "auto" || "$WORKTREE_PATH" == "-" || -z "$WORKTREE_PATH" ]]; then
  base_dir="$WORKTREES_DIR"
  mkdir -p "$base_dir"
  ref_name="$SRC_BRANCH"
  if [[ -n "$NEW_BRANCH" ]]; then
    ref_name="$NEW_BRANCH"
  fi
  WORKTREE_PATH="$base_dir/$(sanitize_branch_name "$ref_name")"
fi

# Default list of AI assistant directories to verify
DEFAULT_AI_DIRS=(
  .claude
  .github
  .cursor
  .ai-context
  .augment
  .trae
  .zed
  .kilocode
  .vscode
  .githooks
)
# Allow override/extension via env var (space-separated)
if [[ -n "${AI_ASSISTANT_DIRS:-}" ]]; then
  # shellcheck disable=SC2206
  AI_DIRS=( ${AI_ASSISTANT_DIRS} )
else
  AI_DIRS=( "${DEFAULT_AI_DIRS[@]}" )
fi

# Ensure parent directory exists for target path
mkdir -p "$(dirname "$WORKTREE_PATH")"

# Preflight: target directory should not be non-empty (empty dir is OK)
if [[ -e "$WORKTREE_PATH" && -d "$WORKTREE_PATH" && -n "$(ls -A "$WORKTREE_PATH" 2>/dev/null)" ]]; then
  echo "ERROR: Target path already exists and is not empty: $WORKTREE_PATH" >&2
  echo "       Choose a different path or remove existing contents." >&2
  exit 2
fi

# Ensure we have the latest refs
git fetch --all --prune

# Preflight: prevent creating a worktree for a branch already checked out elsewhere
if [[ -n "$NEW_BRANCH" ]]; then
  if git worktree list --porcelain | awk '/^branch /{print $2}' | grep -qx "refs/heads/$NEW_BRANCH"; then
    echo "ERROR: Branch '$NEW_BRANCH' is already checked out in another worktree." >&2
    echo "       Run 'git worktree list' to see existing worktrees, or pick a different branch name." >&2
    exit 3
  fi
fi

# Create the worktree from the remote source branch
if [[ -n "$NEW_BRANCH" ]]; then
  echo "Adding worktree at $WORKTREE_PATH for new branch $NEW_BRANCH from $SRC_BRANCH"
  git worktree add -B "$NEW_BRANCH" "$WORKTREE_PATH" "origin/$SRC_BRANCH"
  # Explicitly set upstream for consistency across environments
  git -C "$WORKTREE_PATH" branch --set-upstream-to="origin/$SRC_BRANCH" "$NEW_BRANCH" >/dev/null 2>&1 || true
else
  echo "Adding worktree at $WORKTREE_PATH for branch $SRC_BRANCH"
  git worktree add "$WORKTREE_PATH" "origin/$SRC_BRANCH"
fi

# Verification: ensure AI assistant directories are present if tracked on this branch
missing=()
for d in "${AI_DIRS[@]}"; do
  if [[ ! -e "$WORKTREE_PATH/$d" ]]; then
    # If the directory isn't present, check whether Git tracks anything under it for this branch
    if git -C "$WORKTREE_PATH" ls-tree -r --name-only HEAD -- "$d/" | grep -q .; then
      # Tracked but not materialized (unexpected in full checkout) — attempt to restore
      echo "Restoring tracked AI dir: $d"
      git -C "$WORKTREE_PATH" checkout -- "$d" || true
    fi
  fi
  if [[ ! -e "$WORKTREE_PATH/$d" ]]; then
    missing+=("$d")
  fi
done

if ((${#missing[@]} > 0)); then
  echo "[WARN] Some AI assistant directories are not present in the new worktree: ${missing[*]}" >&2
  echo "       Reasons could include: not tracked on branch 'origin/$SRC_BRANCH' or excluded by project settings." >&2
  echo "       They will appear automatically if/when added to this branch." >&2
else
  echo "Verified AI assistant directories present: ${AI_DIRS[*]}"
fi

echo "Done. Worktree is ready at: $WORKTREE_PATH"
