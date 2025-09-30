#!/bin/bash

# Script to fix markdown files one by one with proper detection and fixes

echo "🔍 Fixing markdown files one by one..."
echo

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Counter
FIXED_COUNT=0
TOTAL_FILES=0

# Function to fix code blocks without language in a file
fix_code_blocks_in_file() {
    local file="$1"
    local temp_file=$(mktemp)
    local in_code_block=false
    local code_block_content=""
    local changed=false
    
    while IFS= read -r line; do
        if [[ "$line" == '```' ]]; then
            if [ "$in_code_block" = false ]; then
                # Starting a code block without language
                in_code_block=true
                code_block_content=""
            else
                # Ending a code block - detect language
                in_code_block=false
                local language=""
                
                # Detect language based on content
                if echo "$code_block_content" | grep -q "fun \|class \|interface \|val \|var \|@Composable\|import com\.\|package com\."; then
                    language="kotlin"
                elif echo "$code_block_content" | grep -q "dependencies {\|apply plugin:\|implementation \|testImplementation\|android {"; then
                    language="gradle"
                elif echo "$code_block_content" | grep -q "^<\|xmlns:\|android:\|</\|<?xml"; then
                    language="xml"
                elif echo "$code_block_content" | grep -q "^#!/bin/\|echo \|\[\[ \|\${\|if \["; then
                    language="bash"
                elif echo "$code_block_content" | grep -q "^{\|\":\|],\|^\["; then
                    language="json"
                elif echo "$code_block_content" | grep -q "^---$\|^[a-zA-Z_-]*:\|^  - \|^- "; then
                    language="yaml"
                elif echo "$code_block_content" | grep -q "^# \|^## \|^### \|- \|\* \|\[.*\](.*)\|^>"; then
                    language="markdown"
                else
                    language="text"
                fi
                
                echo "\`\`\`$language" >> "$temp_file"
                echo "$code_block_content" >> "$temp_file"
                echo '```' >> "$temp_file"
                changed=true
            fi
        elif [[ "$line" =~ ^\`\`\`.+ ]]; then
            # Code block with language already specified
            echo "$line" >> "$temp_file"
            in_code_block=true
        elif [ "$in_code_block" = true ]; then
            # Inside a code block
            if [ -z "$code_block_content" ]; then
                code_block_content="$line"
            else
                code_block_content="$code_block_content
$line"
            fi
        else
            # Regular line
            echo "$line" >> "$temp_file"
        fi
    done < "$file"
    
    if [ "$changed" = true ]; then
        mv "$temp_file" "$file"
        return 0
    else
        rm -f "$temp_file"
        return 1
    fi
}

# Function to add proper blank lines around headings, lists, and code blocks
fix_structure() {
    local file="$1"
    local temp_file=$(mktemp)
    local prev_line=""
    local in_frontmatter=false
    local in_code_block=false
    local line_num=0
    local changed=false
    
    # First pass: read all lines into array
    local lines=()
    while IFS= read -r line; do
        lines+=("$line")
    done < "$file"
    
    # Check for frontmatter
    if [[ "${lines[0]}" == "---" ]]; then
        in_frontmatter=true
    fi
    
    # Second pass: process with context
    for i in "${!lines[@]}"; do
        local line="${lines[$i]}"
        local next_line="${lines[$((i+1))]:-}"
        local prev_line="${lines[$((i-1))]:-}"
        
        # Handle frontmatter
        if [[ "$line" == "---" ]] && [ "$i" -gt 0 ] && [ "$in_frontmatter" = true ]; then
            in_frontmatter=false
            echo "$line" >> "$temp_file"
            # Add blank line after frontmatter if next line is not blank
            if [[ -n "$next_line" ]] && [[ "$next_line" != "" ]]; then
                echo "" >> "$temp_file"
                changed=true
            fi
            continue
        fi
        
        if [ "$in_frontmatter" = true ]; then
            echo "$line" >> "$temp_file"
            continue
        fi
        
        # Toggle code block state
        if [[ "$line" =~ ^\`\`\` ]]; then
            in_code_block=$([ "$in_code_block" = true ] && echo false || echo true)
            
            # MD031: Ensure blank line before code fence (if not at start or after blank)
            if [[ "$in_code_block" = true ]] && [[ -n "$prev_line" ]] && [[ "$prev_line" != "" ]]; then
                echo "" >> "$temp_file"
                changed=true
            fi
            
            echo "$line" >> "$temp_file"
            
            # MD031: Ensure blank line after closing code fence
            if [[ "$in_code_block" = false ]] && [[ -n "$next_line" ]] && [[ "$next_line" != "" ]] && ! [[ "$next_line" =~ ^\`\`\` ]]; then
                echo "" >> "$temp_file"
                changed=true
            fi
            continue
        fi
        
        # Inside code block - pass through
        if [ "$in_code_block" = true ]; then
            echo "$line" >> "$temp_file"
            continue
        fi
        
        # MD022: Headings should be surrounded by blank lines
        if [[ "$line" =~ ^#+ ]]; then
            # Add blank line before heading if needed
            if [[ -n "$prev_line" ]] && [[ "$prev_line" != "" ]]; then
                echo "" >> "$temp_file"
                changed=true
            fi
            echo "$line" >> "$temp_file"
            # Add blank line after heading if needed (even if next is another heading)
            if [[ -n "$next_line" ]] && [[ "$next_line" != "" ]]; then
                echo "" >> "$temp_file"
                changed=true
            fi
            continue
        fi
        
        # MD032: Lists should be surrounded by blank lines
        if [[ "$line" =~ ^[[:space:]]*([-*+]|[0-9]+\.)[[:space:]] ]]; then
            # Check if this is the start of a list block
            if ! [[ "$prev_line" =~ ^[[:space:]]*([-*+]|[0-9]+\.)[[:space:]] ]] && [[ "$prev_line" != "" ]]; then
                echo "" >> "$temp_file"
                changed=true
            fi
            
            # MD029: Normalize ordered list numbers to 1.
            if [[ "$line" =~ ^([[:space:]]*)([0-9]+)(\.)([[:space:]].*)$ ]]; then
                echo "${BASH_REMATCH[1]}1.${BASH_REMATCH[4]}" >> "$temp_file"
                changed=true
            else
                echo "$line" >> "$temp_file"
            fi
            
            # Check if this is the end of a list block
            if ! [[ "$next_line" =~ ^[[:space:]]*([-*+]|[0-9]+\.)[[:space:]] ]] && [[ -n "$next_line" ]] && [[ "$next_line" != "" ]]; then
                echo "" >> "$temp_file"
                changed=true
            fi
            continue
        fi
        
        # Regular line
        echo "$line" >> "$temp_file"
    done
    
    if [ "$changed" = true ]; then
        mv "$temp_file" "$file"
        return 0
    else
        rm -f "$temp_file"
        return 1
    fi
}

# Function to process a single file
process_file() {
    local file="$1"
    
    # Skip build and generated files
    if [[ "$file" == *"/build/"* ]] || [[ "$file" == *"/node_modules/"* ]]; then
        return
    fi
    
    echo -e "${BLUE}Processing: $file${NC}"
    
    local issues_fixed=false
    
    # First fix code blocks without language
    if grep -q "^\`\`\`$" "$file" 2>/dev/null; then
        if fix_code_blocks_in_file "$file"; then
            echo -e "  ${YELLOW}✓ Fixed code blocks without language${NC}"
            issues_fixed=true
        fi
    fi
    
    # Then fix structural issues
    if fix_structure "$file"; then
        echo -e "  ${YELLOW}✓ Fixed structural issues (MD022/MD029/MD031/MD032)${NC}"
        issues_fixed=true
    fi
    
    # Fix trailing spaces (MD009)
    if grep -q " $" "$file" 2>/dev/null; then
        sed -i '' 's/[ \t]*$//' "$file"
        echo -e "  ${YELLOW}✓ Fixed trailing spaces${NC}"
        issues_fixed=true
    fi
    
    # Fix multiple consecutive blank lines (MD012)
    temp_file=$(mktemp)
    awk '/^$/{if (++n >= 2) next} {n=0} 1' "$file" > "$temp_file"
    if ! cmp -s "$file" "$temp_file"; then
        mv "$temp_file" "$file"
        echo -e "  ${YELLOW}✓ Fixed multiple blank lines${NC}"
        issues_fixed=true
    else
        rm -f "$temp_file"
    fi
    
    # Ensure file ends with newline (MD047)
    if [ -s "$file" ] && [ "$(tail -c 1 "$file" | wc -l)" -eq 0 ]; then
        echo "" >> "$file"
        echo -e "  ${YELLOW}✓ Added final newline${NC}"
        issues_fixed=true
    fi
    
    if [ "$issues_fixed" = true ]; then
        ((FIXED_COUNT++))
        echo -e "  ${GREEN}✓ File fixed${NC}"
    else
        echo -e "  ${GREEN}✓ No issues found${NC}"
    fi
    
    ((TOTAL_FILES++))
}

# Main execution
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}   One-by-One Markdown Fixing Script   ${NC}"
echo -e "${BLUE}========================================${NC}"
echo

# Process files with known issues first
files_with_issues=(
    "./.cursor/rules/android-debugging.mdc"
    "./.cursor/rules/android.mdc"
    "./.cursor/rules/enhanced-research-strategy.mdc"
    "./.cursor/rules/kotlin.mdc"
    "./.cursor/rules/mcp-guide.mdc"
    "./.cursor/rules/multi-ai-consultation.mdc"
    "./.cursor/rules/warp-mcp-policy.mdc"
    "./.augment/rules/README.md"
    "./.augment/rules/android-debugging.md"
    "./.augment/rules/android-standards.md"
    "./.augment/rules/android.md"
    "./.augment/rules/enhanced-research-strategy.md"
    "./.augment/rules/kotlin-style.md"
    "./.cursor/rules/README.md"
    "./.kilocode/rules/android-debugging.md"
    "./.kilocode/rules/android-standards.md"
    "./.kilocode/rules/android.md"
    "./.kilocode/rules/enhanced-research-strategy.md"
    "./.kilocode/rules/kotlin-style.md"
    "./.trae/rules/project_rules.md"
    "./CURSOR_ANDROID_SETUP.md"
    "./VSCODE_ANDROID_SETUP_GUIDE.md"
    "./docs/BUILD_SYSTEM.md"
    "./docs/FEATURE_BASED_DEVELOPMENT_GUIDE.md"
    "./docs/MARKDOWNLINT-CONVENTIONS.md"
    "./docs/MARKDOWN_LINTING_SUMMARY.md"
    "./docs/PROJECT_OVERVIEW.md"
    "./docs/assistants/README.md"
    "./docs/assistants/SYNC_SUMMARY.md"
    "./docs/assistants/android-debugging.md"
    "./docs/assistants/android-standards.md"
    "./docs/assistants/claude-guide.md"
    "./docs/assistants/dev-environment.md"
    "./docs/assistants/enhanced-research-strategy.md"
    "./docs/assistants/kotlin-style.md"
    "./docs/navigation3/DISTRIBUTED_DESTINATIONS_GUIDE.md"
    "./docs/navigation3/DISTRIBUTED_DESTINATIONS_MIGRATION.md"
    "./docs/navigation3/MIGRATION_GUIDE.md"
    "./docs/navigation3/MULTI_MODULE_DEEPLINK_ARCHITECTURE.md"
    "./docs/navigation3/NAVIGATION_3_DEEPLINK_ARCHITECTURE.md"
    "./docs/navigation3/NAVIGATION_3_IMPLEMENTATION.md"
    "./docs/navigation3/NAVIGATION_3_INTEGRATION_SUMMARY.md"
    "./docs/navigation3/NAVIGATION_AUDIT_REPORT.md"
    "./docs/navigation3/PERFORMANCE_MONITORING.md"
    "./docs/navigation3/comparison.md"
    "./docs/navigation3/runbooks/KSP_OWNERSHIP.md"
    "./docs/quality/detekt-1.23.8-migration.md"
    "./docs/quality/detekt-ktlint-integration-2025.md"
    "./docs/quality/detekt-usage.md"
    "./docs/quality/precommit.md"
)

echo -e "${GREEN}Processing ${#files_with_issues[@]} files with known issues${NC}"
echo

for file in "${files_with_issues[@]}"; do
    if [ -f "$file" ]; then
        process_file "$file"
    else
        echo -e "${RED}File not found: $file${NC}"
    fi
done

echo
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}           Summary                      ${NC}"
echo -e "${BLUE}========================================${NC}"
echo -e "Total files processed: ${GREEN}$TOTAL_FILES${NC}"
echo -e "Files fixed: ${YELLOW}$FIXED_COUNT${NC}"
echo
echo -e "${GREEN}✓ One-by-one fixing complete!${NC}"
