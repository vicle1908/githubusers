#!/bin/bash

# ANSI color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}   Assistant Rules Sync Verification   ${NC}"
echo -e "${BLUE}========================================${NC}"
echo

# Function to extract content without frontmatter
extract_content() {
    local file="$1"
    if [[ ! -f "$file" ]]; then
        echo ""
        return
    fi
    
    # Skip frontmatter if it exists
    awk '
    BEGIN { in_frontmatter = 0; skip_next_blank = 0 }
    /^---$/ {
        if (NR == 1) {
            in_frontmatter = 1
            next
        } else if (in_frontmatter) {
            in_frontmatter = 0
            skip_next_blank = 1
            next
        }
    }
    in_frontmatter { next }
    skip_next_blank && /^$/ { skip_next_blank = 0; next }
    { print }
    ' "$file"
}

# Function to compare files
compare_files() {
    local canonical="$1"
    local target="$2"
    local description="$3"
    
    if [[ ! -f "$canonical" ]]; then
        echo -e "${RED}✗ Canonical file missing: $canonical${NC}"
        return 1
    fi
    
    if [[ ! -f "$target" ]]; then
        echo -e "${RED}✗ Target file missing: $target${NC}"
        return 1
    fi
    
    # Extract content without frontmatter
    canonical_content=$(extract_content "$canonical")
    target_content=$(extract_content "$target")
    
    # Create temp files for comparison
    temp_canonical=$(mktemp)
    temp_target=$(mktemp)
    echo "$canonical_content" > "$temp_canonical"
    echo "$target_content" > "$temp_target"
    
    # Compare content
    if diff -q "$temp_canonical" "$temp_target" > /dev/null 2>&1; then
        echo -e "${GREEN}✓ $description${NC}"
        rm "$temp_canonical" "$temp_target"
        return 0
    else
        echo -e "${RED}✗ $description - Content differs${NC}"
        echo -e "${YELLOW}  Differences (first 5 lines):${NC}"
        diff -u "$temp_canonical" "$temp_target" | head -n 5
        rm "$temp_canonical" "$temp_target"
        return 1
    fi
}

# Check canonical docs exist
echo -e "${BLUE}Checking canonical docs...${NC}"
canonical_docs=(
    "warp-mcp-policy.md"
    "mcp-guide.md"
    "enhanced-research-strategy.md"
    "multi-ai-consultation.md"
    "android-standards.md"
    "kotlin-style.md"
    "android-debugging.md"
    "dev-environment.md"
    "claude-guide.md"
)

all_canonical_exist=true
for doc in "${canonical_docs[@]}"; do
    if [[ -f "docs/assistants/$doc" ]]; then
        echo -e "${GREEN}✓ docs/assistants/$doc exists${NC}"
    else
        echo -e "${RED}✗ docs/assistants/$doc missing${NC}"
        all_canonical_exist=false
    fi
done
echo

# Verify Cursor rules (.mdc extension)
echo -e "${BLUE}Verifying Cursor rules (.cursor/rules/)...${NC}"
cursor_mappings=(
    "warp-mcp-policy.md:warp-mcp-policy.mdc"
    "mcp-guide.md:mcp-guide.mdc"
    "enhanced-research-strategy.md:enhanced-research-strategy.mdc"
    "multi-ai-consultation.md:multi-ai-consultation.mdc"
    "android-standards.md:android.mdc"
    "kotlin-style.md:kotlin.mdc"
    "android-debugging.md:android-debugging.mdc"
)

for mapping in "${cursor_mappings[@]}"; do
    IFS=':' read -r canonical target <<< "$mapping"
    compare_files "docs/assistants/$canonical" ".cursor/rules/$target" "Cursor: $target"
done
echo

# Verify Augment rules
echo -e "${BLUE}Verifying Augment rules (.augment/rules/)...${NC}"
augment_files=(
    "warp-mcp-policy.md"
    "mcp-guide.md"
    "enhanced-research-strategy.md"
    "multi-ai-consultation.md"
    "android-standards.md"
    "kotlin-style.md"
    "android-debugging.md"
)

for file in "${augment_files[@]}"; do
    compare_files "docs/assistants/$file" ".augment/rules/$file" "Augment: $file"
done
echo

# Verify KiloCode rules
echo -e "${BLUE}Verifying KiloCode rules (.kilocode/rules/)...${NC}"
kilocode_files=(
    "warp-mcp-policy.md"
    "mcp-guide.md"
    "enhanced-research-strategy.md"
    "multi-ai-consultation.md"
    "android-standards.md"
    "kotlin-style.md"
    "android-debugging.md"
)

for file in "${kilocode_files[@]}"; do
    compare_files "docs/assistants/$file" ".kilocode/rules/$file" "KiloCode: $file"
done
echo

# Verify Trae project rules (special handling)
echo -e "${BLUE}Verifying Trae rules (.trae/rules/project_rules.md)...${NC}"
if [[ -f ".trae/rules/project_rules.md" ]]; then
    # Check if all key sections are present
    if grep -q "OpenMemory" .trae/rules/project_rules.md && \
       grep -q "planning tool" .trae/rules/project_rules.md; then
        echo -e "${GREEN}✓ Trae project_rules.md contains memory and planning guidance${NC}"
    else
        echo -e "${RED}✗ Trae project_rules.md missing critical sections${NC}"
    fi
else
    echo -e "${RED}✗ Trae project_rules.md missing${NC}"
fi
echo

# Check root pointer files
echo -e "${BLUE}Checking root pointer files...${NC}"
if grep -q "docs/assistants/warp-mcp-policy.md" WARP.md 2>/dev/null; then
    echo -e "${GREEN}✓ WARP.md points to canonical docs${NC}"
else
    echo -e "${RED}✗ WARP.md not properly configured${NC}"
fi

if grep -q "docs/assistants/claude-guide.md" CLAUDE.md 2>/dev/null; then
    echo -e "${GREEN}✓ CLAUDE.md points to canonical docs${NC}"
else
    echo -e "${RED}✗ CLAUDE.md not properly configured${NC}"
fi
echo

# Check Gemini configuration
echo -e "${BLUE}Checking Gemini configuration...${NC}"
if [[ -f ".gemini/settings.json" ]]; then
    # Check for environment variable placeholders
    if grep -q '\${' .gemini/settings.json; then
        echo -e "${GREEN}✓ Gemini settings.json uses environment variables${NC}"
    else
        if grep -q 'sk-' .gemini/settings.json || grep -q 'api_key' .gemini/settings.json; then
            echo -e "${YELLOW}⚠ Gemini settings.json may contain hardcoded secrets${NC}"
        else
            echo -e "${GREEN}✓ Gemini settings.json appears safe${NC}"
        fi
    fi
else
    echo -e "${YELLOW}⚠ Gemini settings.json not found${NC}"
fi
echo

# Summary
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}            Sync Summary                ${NC}"
echo -e "${BLUE}========================================${NC}"

# Count synced files
cursor_count=$(ls -1 .cursor/rules/*.mdc 2>/dev/null | wc -l)
augment_count=$(ls -1 .augment/rules/*.md 2>/dev/null | wc -l)
kilocode_count=$(ls -1 .kilocode/rules/*.md 2>/dev/null | wc -l)

echo -e "Canonical docs: ${GREEN}${#canonical_docs[@]}${NC} files"
echo -e "Cursor rules: ${GREEN}$cursor_count${NC} files"
echo -e "Augment rules: ${GREEN}$augment_count${NC} files"
echo -e "KiloCode rules: ${GREEN}$kilocode_count${NC} files"
echo -e "Trae rules: ${GREEN}1${NC} combined file"
echo

# Check if sync script exists
if [[ -f "scripts/sync-assistant-rules.sh" ]]; then
    echo -e "${GREEN}✓ Sync script exists at scripts/sync-assistant-rules.sh${NC}"
else
    echo -e "${RED}✗ Sync script missing${NC}"
fi

echo
echo -e "${BLUE}Verification complete!${NC}"
