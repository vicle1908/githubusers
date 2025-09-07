#!/bin/bash

# Script to scan and fix common markdown linting issues

echo "🔍 Scanning and fixing markdown lint issues..."
echo

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Counter for fixed files
FIXED_COUNT=0
TOTAL_FILES=0

# Function to fix a markdown file
fix_markdown_file() {
    local file="$1"
    local changed=false
    
    # Skip generated files
    if [[ "$file" == *"/build/"* ]] || [[ "$file" == *"/gradle/"* ]]; then
        return
    fi
    
    echo -e "${BLUE}Checking: $file${NC}"
    
    # Create a temporary file
    temp_file=$(mktemp)
    cp "$file" "$temp_file"
    
    # MD009: Remove trailing spaces
    if grep -q " $" "$file"; then
        sed -i '' 's/[ \t]*$//' "$temp_file"
        echo -e "  ${YELLOW}✓ Fixed trailing spaces${NC}"
        changed=true
    fi
    
    # MD012: Remove multiple consecutive blank lines (keep max 1)
    if awk '/^$/{if (++n >= 2) next} {n=0} 1' "$file" | diff -q "$file" - > /dev/null; then
        :
    else
        awk '/^$/{if (++n >= 2) next} {n=0} 1' "$temp_file" > "${temp_file}.tmp" && mv "${temp_file}.tmp" "$temp_file"
        echo -e "  ${YELLOW}✓ Fixed multiple blank lines${NC}"
        changed=true
    fi
    
    # MD047: Files should end with a single newline character
    if [ -s "$file" ] && [ "$(tail -c 1 "$file" | wc -l)" -eq 0 ]; then
        echo "" >> "$temp_file"
        echo -e "  ${YELLOW}✓ Added final newline${NC}"
        changed=true
    fi
    
    # Check if file starts with a heading (MD041)
    first_non_empty_line=$(grep -v '^$' "$file" | grep -v '^---$' | head -n 1)
    if [[ ! "$first_non_empty_line" =~ ^#[[:space:]] ]]; then
        # Skip frontmatter
        if [[ $(head -n 1 "$file") == "---" ]]; then
            :
        else
            echo -e "  ${RED}⚠ Warning: File should start with a top-level heading${NC}"
        fi
    fi
    
    # Check for code blocks without language (MD040)
    if grep -q "^\`\`\`$" "$file"; then
        echo -e "  ${RED}⚠ Warning: Code blocks without language specification found${NC}"
    fi
    
    # Apply changes if any were made
    if [ "$changed" = true ]; then
        cp "$temp_file" "$file"
        ((FIXED_COUNT++))
        echo -e "  ${GREEN}✓ File fixed${NC}"
    else
        echo -e "  ${GREEN}✓ No issues found${NC}"
    fi
    
    rm -f "$temp_file"
    ((TOTAL_FILES++))
}

# Function to add heading if missing
add_heading_if_missing() {
    local file="$1"
    local filename=$(basename "$file")
    
    # Check if file has frontmatter
    if [[ $(head -n 1 "$file") == "---" ]]; then
        return
    fi
    
    # Check if first line is a heading
    first_line=$(head -n 1 "$file")
    if [[ ! "$first_line" =~ ^#[[:space:]] ]]; then
        # Generate heading from filename
        heading=$(echo "$filename" | sed 's/.md$//' | sed 's/-/ /g' | sed 's/\b\(.\)/\u\1/g')
        
        # Create temp file with heading
        temp_file=$(mktemp)
        echo "# $heading" > "$temp_file"
        echo "" >> "$temp_file"
        cat "$file" >> "$temp_file"
        
        mv "$temp_file" "$file"
        echo -e "${YELLOW}Added heading to $file${NC}"
    fi
}

# Main execution
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}   Markdown Lint Fixing Script         ${NC}"
echo -e "${BLUE}========================================${NC}"
echo

# Find all markdown files (excluding node_modules and .git)
md_files=()
while IFS= read -r file; do
    md_files+=("$file")
done < <(find . -name "*.md" -type f | grep -v node_modules | grep -v ".git" | sort)

echo -e "${GREEN}Found ${#md_files[@]} markdown files${NC}"
echo

# Process each file
for file in "${md_files[@]}"; do
    fix_markdown_file "$file"
done

echo
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}           Summary                      ${NC}"
echo -e "${BLUE}========================================${NC}"
echo -e "Total files checked: ${GREEN}$TOTAL_FILES${NC}"
echo -e "Files fixed: ${YELLOW}$FIXED_COUNT${NC}"

# Create a summary report
REPORT_FILE="markdown-lint-report.txt"
echo "Markdown Lint Report - $(date)" > "$REPORT_FILE"
echo "=================================" >> "$REPORT_FILE"
echo "Total files checked: $TOTAL_FILES" >> "$REPORT_FILE"
echo "Files fixed: $FIXED_COUNT" >> "$REPORT_FILE"
echo "" >> "$REPORT_FILE"

# List files with remaining warnings
echo "Files with warnings:" >> "$REPORT_FILE"
for file in "${md_files[@]}"; do
    if [[ "$file" == *"/build/"* ]] || [[ "$file" == *"/gradle/"* ]]; then
        continue
    fi
    
    # Check for code blocks without language
    if grep -q "^\`\`\`$" "$file"; then
        echo "  - $file: Code blocks without language" >> "$REPORT_FILE"
    fi
    
    # Check for missing top-level heading
    if [[ $(head -n 1 "$file") != "---" ]]; then
        first_non_empty_line=$(grep -v '^$' "$file" | head -n 1)
        if [[ ! "$first_non_empty_line" =~ ^#[[:space:]] ]]; then
            echo "  - $file: Missing top-level heading" >> "$REPORT_FILE"
        fi
    fi
done

echo
echo -e "${GREEN}Report saved to: $REPORT_FILE${NC}"
echo
echo -e "${BLUE}Next steps:${NC}"
echo "1. Review warnings in $REPORT_FILE"
echo "2. Manually fix code blocks without language specifications"
echo "3. Add top-level headings to files that need them"
echo "4. Run markdownlint for detailed analysis"
echo
echo -e "${GREEN}✓ Markdown lint fixing complete!${NC}"
