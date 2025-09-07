#!/usr/bin/env bash
set -euo pipefail

# install-hooks.sh: Configure repository to use versioned git hooks under .githooks
# Run this once per clone/worktree.

ROOT="$(git rev-parse --show-toplevel 2>/dev/null || pwd)"
cd "$ROOT"

echo "Configuring core.hooksPath to .githooks"
git config core.hooksPath .githooks

# Ensure hooks are executable
chmod +x .githooks/post-checkout .githooks/post-merge .githooks/post-switch

echo "Verifying hooks..."
if git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  echo "Git hooks installed. On next checkout/merge/switch, rules will auto-sync."
else
  echo "Warning: Not inside a git worktree. Please run from a repository."
fi

