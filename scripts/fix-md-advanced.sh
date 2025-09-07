#!/bin/bash

# Advanced Markdown structural fixes targeting:
# - MD022: Headings should be surrounded by blank lines
# - MD031: Fenced code blocks should be surrounded by blank lines
# - MD032: Lists should be surrounded by blank lines (top-level lists)
# - MD029: Ordered list item prefix (normalize to 1.)

set -euo pipefail

BLUE='\033[0;34m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

process_file() {
  local file="$1"
  # Skip generated or irrelevant directories
  if [[ "$file" == *"/node_modules/"* ]] || [[ "$file" == *"/.git/"* ]] || [[ "$file" == *"/build/"* ]]; then
    return
  fi

  echo -e "${BLUE}Processing: $file${NC}"
  local tmp
  tmp=$(mktemp)

  # Use awk to load all lines, then process structurally
  awk '
    function is_blank(idx) { return (idx<1 || idx>n) ? 1 : (lines[idx] ~ /^[[:space:]]*$/) }
    function starts_code_fence(s) { return (s ~ /^```/) }
    function is_heading(s) { return (s ~ /^[#]{1,6}[[:space:]]+/) }
    function is_list_item(s) {
      # any indent then list marker
      return (s ~ /^[[:space:]]*([-+*]|[0-9]+[.)])[[:space:]]+/)
    }
    function is_top_level_list_item(s) {
      return (s ~ /^([-+*]|[0-9]+[.)])[[:space:]]+/)
    }
    # normalize ordered list numbers handled post-processing via sed due to awk portability
    BEGIN { in_code=0; fm_start=0; fm_end=0 }
    { lines[++n] = $0 }
    END {
      # detect frontmatter region (optional)
      if (n>=1 && lines[1] == "---") {
        fm_start=1
        for (i=2;i<=n;i++) { if (lines[i]=="---") { fm_end=i; break } }
      }

      for (i=1; i<=n; i++) {
        s = lines[i]

        # toggle code fence
        if (starts_code_fence(s)) {
          if (in_code==0) {
            # opening fence: MD031 ensure blank line before (unless at BOF or after frontmatter)
            if (!is_blank(i-1) && !(fm_end>0 && i-1==fm_end)) {
              out[++m] = ""
            }
            in_code=1
          } else {
            in_code=0
          }
          out[++m] = s
          # after code fence line, if it is closing fence (in_code became 0), ensure blank line after (MD031)
          if (in_code==0) {
            if (!is_blank(i+1)) { out[++m] = "" }
          }
          continue
        }

        if (in_code==1) { out[++m] = s; continue }

        # MD022: Headings surrounded by blank lines (ignore inside frontmatter)
        if (is_heading(s)) {
          # before
          if (!is_blank(i-1) && !(fm_end>0 && i-1==fm_end)) { out[++m] = "" }
          out[++m] = s
          # after
          if (!is_blank(i+1)) { out[++m] = "" }
          continue
        }

        # MD032/MD029: Lists surrounded by blank lines (top-level) and normalize ordered numbers
        if (is_top_level_list_item(s)) {
          # ensure blank line before list block start
          if (!is_blank(i-1)) { out[++m] = "" }

          # process contiguous list block
          j=i
          while (j<=n) {
            t = lines[j]
            if (t ~ /^[[:space:]]*$/) { break }
            if (!is_list_item(t)) { break }
            # keep item as-is; will normalize ordered numbers after awk pass
            block[++bcount] = t
            j++
          }
          # emit block
          for (k=1;k<=bcount;k++) out[++m] = block[k]
          delete block; bcount=0
          # ensure blank line after list block
          if (!is_blank(j)) { out[++m] = "" }
          i = j-1
          continue
        }

        # Not heading/code/list start, just pass through
        out[++m] = s
      }

      # collapse 3+ blank lines to max 2, later our other fixer will reduce to 1 as configured
      prev_blank=0
      for (i=1;i<=m;i++) {
        if (out[i] ~ /^[[:space:]]*$/) {
          if (prev_blank>=2) { next }
          prev_blank++
        } else { prev_blank=0 }
        print out[i]
      }
    }
  ' "$file" > "$tmp"

  # Post-process for MD029: normalize ordered lists to "1." style
  # This uses BSD sed (-i '') for macOS compatibility
  sed -E -i '' 's/^([[:space:]]*)[0-9]+[.)][[:space:]]+/\1 1. /' "$tmp"

  # Replace original if changed
  if ! cmp -s "$file" "$tmp"; then
    mv "$tmp" "$file"
    echo -e "  ${YELLOW}✓ Structural fixes applied${NC}"
  else
    rm -f "$tmp"
    echo -e "  ${GREEN}✓ No structural changes${NC}"
  fi
}

# Gather markdown files via while/read for macOS compatibility
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}   Advanced Markdown Structural Fixes   ${NC}"
echo -e "${BLUE}========================================${NC}"

while IFS= read -r f; do
  process_file "$f"
done < <(find . -name "*.md" -type f | grep -v "/node_modules/" | grep -v "/.git/" | sort)

echo -e "${GREEN}✓ Advanced structural fixes complete${NC}"

