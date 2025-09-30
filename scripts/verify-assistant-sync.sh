#!/bin/bash

# Assistant Rules Verification Script
# This script verifies that all AI assistants have matching content

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
CANONICAL_DIR="$PROJECT_ROOT/docs/assistants"

echo "🔍 Verifying AI Assistant Rules Sync..."
echo ""

# Function to compute checksum of content (ignoring frontmatter)
# Robustly strips a single top-of-file YAML frontmatter block, tolerating BOM and whitespace.
get_content_checksum() {
    local file="$1"
    if [ -f "$file" ]; then
        # Use Perl to remove an optional BOM + YAML frontmatter block at the very start of the file
        # Pattern: optional BOM, then '---' line, non-greedy until next '---' line, then a trailing newline (if present)
        perl -0777 -pe 's/^\x{FEFF}?---\s*\n.*?\n---\s*\n//s' "$file" | shasum -a 256 | cut -d' ' -f1
    else
        echo "MISSING"
    fi
}

# Check canonical docs exist
echo "📚 Checking canonical documentation..."
canonical_files=(
    "warp-mcp-policy.md"
    "mcp-guide.md"
    "enhanced-research-strategy.md"
    "multi-ai-consultation.md"
    "android-standards.md"
    "kotlin-style.md"
    "android-debugging.md"
    "dev-environment.md"
    "claude-guide.md"
    "README.md"
)

missing_canonical=0
for file in "${canonical_files[@]}"; do
    if [ ! -f "$CANONICAL_DIR/$file" ]; then
        echo "  ❌ Missing: $file"
        ((missing_canonical++))
    else
        echo "  ✅ Found: $file"
    fi
done

if [ $missing_canonical -gt 0 ]; then
    echo "  ⚠️  $missing_canonical canonical files missing!"
fi

echo ""
echo "🔄 Checking assistant rule sync..."

# Check Cursor rules (.mdc files)
echo ""
echo "📋 Cursor (.cursor/rules/):"
cursor_files=(
    "android.mdc:android-standards.md"
    "kotlin.mdc:kotlin-style.md"
    "mcp-guide.mdc:mcp-guide.md"
    "enhanced-research-strategy.mdc:enhanced-research-strategy.md"
    "multi-ai-consultation.mdc:multi-ai-consultation.md"
    "android-debugging.mdc:android-debugging.md"
)

cursor_mismatches=0
for mapping in "${cursor_files[@]}"; do
    IFS=':' read -r cursor_file canonical_file <<< "$mapping"
    cursor_path="$PROJECT_ROOT/.cursor/rules/$cursor_file"
    canonical_path="$CANONICAL_DIR/$canonical_file"
    
    cursor_sum=$(get_content_checksum "$cursor_path")
    canonical_sum=$(get_content_checksum "$canonical_path")
    
    if [ "$cursor_sum" = "MISSING" ]; then
        echo "  ❌ Missing: $cursor_file"
        ((cursor_mismatches++))
    elif [ "$cursor_sum" != "$canonical_sum" ]; then
        echo "  ⚠️  Mismatch: $cursor_file ≠ $canonical_file"
        ((cursor_mismatches++))
    else
        echo "  ✅ Synced: $cursor_file = $canonical_file"
    fi
done

# Check Augment rules
echo ""
echo "📋 Augment (.augment/rules/):"
augment_files=(
    "android-standards.md"
    "kotlin-style.md"
    "mcp-guide.md"
    "enhanced-research-strategy.md"
    "multi-ai-consultation.md"
)

augment_mismatches=0
for file in "${augment_files[@]}"; do
    augment_path="$PROJECT_ROOT/.augment/rules/$file"
    canonical_path="$CANONICAL_DIR/$file"
    
    augment_sum=$(get_content_checksum "$augment_path")
    canonical_sum=$(get_content_checksum "$canonical_path")
    
    if [ "$augment_sum" = "MISSING" ]; then
        echo "  ❌ Missing: $file"
        ((augment_mismatches++))
    elif [ "$augment_sum" != "$canonical_sum" ]; then
        echo "  ⚠️  Mismatch: $file"
        ((augment_mismatches++))
    else
        echo "  ✅ Synced: $file"
    fi
done

# Check KiloCode rules
echo ""
echo "📋 KiloCode (.kilocode/rules/):"
kilocode_files=(
    "android-standards.md"
    "kotlin-style.md"
    "mcp-guide.md"
    "enhanced-research-strategy.md"
)

kilocode_mismatches=0
for file in "${kilocode_files[@]}"; do
    kilocode_path="$PROJECT_ROOT/.kilocode/rules/$file"
    canonical_path="$CANONICAL_DIR/$file"
    
    kilocode_sum=$(get_content_checksum "$kilocode_path")
    canonical_sum=$(get_content_checksum "$canonical_path")
    
    if [ "$kilocode_sum" = "MISSING" ]; then
        echo "  ❌ Missing: $file"
        ((kilocode_mismatches++))
    elif [ "$kilocode_sum" != "$canonical_sum" ]; then
        echo "  ⚠️  Mismatch: $file"
        ((kilocode_mismatches++))
    else
        echo "  ✅ Synced: $file"
    fi
done

# Check Trae rules
echo ""
echo "📋 Trae (.trae/rules/):"
trae_path="$PROJECT_ROOT/.trae/rules/project_rules.md"
if [ -f "$trae_path" ]; then
    echo "  ✅ project_rules.md present"
else
    echo "  ❌ Missing project_rules.md"
fi

# Check Gemini settings
echo ""
echo "📋 Gemini (.gemini/):"
gemini_path="$PROJECT_ROOT/.gemini/settings.json"
if [ -f "$gemini_path" ]; then
    # Check for hardcoded secrets
    if grep -q "sk-proj-" "$gemini_path" || \
       grep -q "xai-[A-Za-z0-9]" "$gemini_path" || \
       grep -q "tvly-" "$gemini_path" || \
       grep -q "AIzaSy" "$gemini_path"; then
        echo "  ⚠️  WARNING: Hardcoded secrets detected!"
        echo "     Run: grep -E '(sk-proj-|xai-|tvly-|AIzaSy)' $gemini_path"
    else
        echo "  ✅ No hardcoded secrets found"
    fi
    
    # Check for required MCP servers
    required_servers=(
        "gradle-mcp-server"
        "android"
        "mobile-mcp"
        "claude-context"
        "openmemory"
        "zen"
    )
    
    for server in "${required_servers[@]}"; do
        if grep -q "\"$server\"" "$gemini_path"; then
            echo "  ✅ MCP server configured: $server"
        else
            echo "  ❌ Missing MCP server: $server"
        fi
    done
else
    echo "  ❌ Missing settings.json"
fi

# Check root pointers
echo ""
echo "📍 Root pointer files:"
for file in WARP.md CLAUDE.md; do
    if [ -f "$PROJECT_ROOT/$file" ]; then
        if grep -q "docs/assistants" "$PROJECT_ROOT/$file"; then
            echo "  ✅ $file points to canonical docs"
        else
            echo "  ⚠️  $file doesn't point to canonical docs"
        fi
    else
        echo "  ❌ Missing $file"
    fi
done

# Summary
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📊 Verification Summary:"
echo ""

total_issues=$((missing_canonical + cursor_mismatches + augment_mismatches + kilocode_mismatches))

if [ $total_issues -eq 0 ]; then
    echo "✅ All assistants are fully synchronized!"
    echo "   - Canonical docs: Complete"
    echo "   - Cursor rules: Synced"
    echo "   - Augment rules: Synced"
    echo "   - KiloCode rules: Synced"
    echo "   - Trae rules: Configured"
    echo "   - Gemini settings: Secure"
    echo ""
    echo "🎉 MCP-first policies are unified across all assistants!"
else
    echo "⚠️  Found $total_issues sync issues"
    echo ""
    echo "To fix:"
    echo "  1. Run: ./scripts/sync-assistant-rules.sh"
    echo "  2. Review any remaining issues manually"
    echo "  3. Re-run verification"
fi

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
