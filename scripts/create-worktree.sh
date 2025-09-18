#!/usr/bin/env bash
set -euo pipefail

# Create a git worktree and configure sparse-checkout dynamically for all modules.
#
# Usage:
#   scripts/create-worktree.sh <source_branch> <worktree_path> [<new_branch_name>]
#
# Examples:
#   scripts/create-worktree.sh main /path/to/worktrees/feature-x feature/feature-x
#   scripts/create-worktree.sh main /path/to/worktrees/build-green ai/build-green
#
# Notes:
# - Requires git 2.25+ for sparse-checkout convenience commands.
# - After creating the worktree, we invoke scripts/sparse-checkout-config.sh to
#   dynamically include all modules based on root settings.gradle.kts.

if [[ $# -lt 2 ]]; then
  echo "Usage: $0 <source_branch> <worktree_path> [<new_branch_name>]" >&2
  exit 1
fi

SRC_BRANCH="$1"
WORKTREE_PATH="$2"
NEW_BRANCH="${3:-}"

REPO_ROOT="$(git rev-parse --show-toplevel)"

# Ensure we have the latest refs
git fetch --all --prune

if [[ -n "$NEW_BRANCH" ]]; then
  echo "Adding worktree at $WORKTREE_PATH for new branch $NEW_BRANCH from $SRC_BRANCH"
  git worktree add -B "$NEW_BRANCH" "$WORKTREE_PATH" "origin/$SRC_BRANCH"
else
  echo "Adding worktree at $WORKTREE_PATH for branch $SRC_BRANCH"
  git worktree add "$WORKTREE_PATH" "origin/$SRC_BRANCH"
fi

echo "Configuring dynamic sparse-checkout for modules..."
chmod +x "$REPO_ROOT/scripts/sparse-checkout-config.sh" || true
"$REPO_ROOT/scripts/sparse-checkout-config.sh" --apply --path "$WORKTREE_PATH"

echo "Done. Worktree is ready at: $WORKTREE_PATH"