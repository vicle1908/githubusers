#!/bin/bash

# Script to fix code blocks without language specifications

echo "🔍 Fixing code blocks without language specifications..."
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

# Function to detect and fix code blocks in a file
fix_code_blocks() {
    local file="$1"
    local changed=false
    
    echo -e "${BLUE}Processing: $file${NC}"
    
    # Create a temporary file
    temp_file=$(mktemp)
    
    # Process the file line by line
    in_code_block=false
    code_block_content=""
    while IFS= read -r line; do
        if [[ "$line" == '```' ]]; then
            if [ "$in_code_block" = false ]; then
                # Starting a code block without language
                in_code_block=true
                code_block_content=""
                # Don't write the line yet, we'll determine the language first
            else
                # Ending a code block
                in_code_block=false
                # Determine the language based on content
                language=$(detect_language "$code_block_content")
                
                # Write the opening with language
                echo "\`\`\`$language" >> "$temp_file"
                # Write the content
                echo "$code_block_content" >> "$temp_file"
                # Write the closing
                echo '```' >> "$temp_file"
                
                if [ -n "$language" ]; then
                    echo -e "  ${YELLOW}✓ Added language: $language${NC}"
                    changed=true
                fi
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
    
    # Apply changes if any were made
    if [ "$changed" = true ]; then
        mv "$temp_file" "$file"
        ((FIXED_COUNT++))
        echo -e "  ${GREEN}✓ File fixed${NC}"
    else
        rm -f "$temp_file"
        echo -e "  ${GREEN}✓ No changes needed${NC}"
    fi
    
    ((TOTAL_FILES++))
}

# Function to detect language based on code content
detect_language() {
    local content="$1"
    
    # Check for Kotlin
    if echo "$content" | grep -q "fun \|class \|interface \|val \|var \|import com\.\|@Composable\|kotlin"; then
        echo "kotlin"
        return
    fi
    
    # Check for Gradle/Groovy
    if echo "$content" | grep -q "dependencies {\|apply plugin:\|buildscript {\|repositories {\|implementation \|testImplementation"; then
        echo "gradle"
        return
    fi
    
    # Check for XML
    if echo "$content" | grep -q "^<\|xmlns:\|android:\|</\|<?xml"; then
        echo "xml"
        return
    fi
    
    # Check for YAML
    if echo "$content" | grep -q "^  - \|^- \|: |\|^name:\|^on:\|^jobs:"; then
        echo "yaml"
        return
    fi
    
    # Check for JSON
    if echo "$content" | grep -q "^{\|^  \"\|]: {\|],\|^\["; then
        echo "json"
        return
    fi
    
    # Check for Bash/Shell
    if echo "$content" | grep -q "^#!/bin/\|echo \|if \[\|\${\|export \|cd \|npm \|yarn \|git "; then
        echo "bash"
        return
    fi
    
    # Check for Properties
    if echo "$content" | grep -q "^[a-zA-Z0-9.]*="; then
        echo "properties"
        return
    fi
    
    # Check for SQL
    if echo "$content" | grep -q "SELECT \|FROM \|WHERE \|CREATE TABLE\|INSERT INTO"; then
        echo "sql"
        return
    fi
    
    # Check for Markdown tables or structure
    if echo "$content" | grep -q "^|.*|.*|$\|^-*|-*$"; then
        echo "markdown"
        return
    fi
    
    # Default to text if can't determine
    echo "text"
}

# Main execution
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}   Code Block Language Fixing Script   ${NC}"
echo -e "${BLUE}========================================${NC}"
echo

# List of files with code blocks without language (from the report)
files_to_fix=(
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
    "./CURSOR_ANDROID_SETUP.md"
    "./VSCODE_ANDROID_SETUP_GUIDE.md"
    "./docs/BUILD_SYSTEM.md"
    "./docs/FEATURE_BASED_DEVELOPMENT_GUIDE.md"
    "./docs/MARKDOWNLINT-CONVENTIONS.md"
    "./docs/PROJECT_OVERVIEW.md"
    "./docs/assistants/README.md"
    "./docs/assistants/SYNC_SUMMARY.md"
    "./docs/assistants/android-debugging.md"
    "./docs/assistants/android-standards.md"
    "./docs/assistants/claude-guide.md"
    "./docs/assistants/dev-environment.md"
    "./docs/assistants/enhanced-research-strategy.md"
    "./docs/assistants/kotlin-style.md"
    "./docs/navigation3/BYTEROVER_MCP_EXPLORATION_REPORT.md"
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

echo -e "${GREEN}Processing ${#files_to_fix[@]} files with code block issues${NC}"
echo

# Process each file
for file in "${files_to_fix[@]}"; do
    if [ -f "$file" ]; then
        fix_code_blocks "$file"
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
echo -e "${GREEN}✓ Code block fixing complete!${NC}"
