#!/bin/bash

# Enforce MD022 (blank lines around headings) across all .md and .mdc files
# - Skips YAML frontmatter at top of file
# - Ignores headings inside fenced code blocks
# - Adds a blank line before and after every heading as needed
# - Collapses multiple blank lines to one and ensures final newline

set -euo pipefail

BLUE='\033[0;34m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

is_blank() { [[ "$1" =~ ^[[:space:]]*$ ]]; }

fix_md022_file() {
  local file="$1"
  local tmp=$(mktemp)

  local in_code=false
  local in_fm=false
  local fm_end=-1

  # Read all lines into array
  lines=()
  while IFS= read -r line || [ -n "$line" ]; do
    lines+=("$line")
  done < "$file"
  local n=${#lines[@]}

  # Detect frontmatter only if first line is '---'
  if (( n > 0 )) && [[ "${lines[0]}" == "---" ]]; then
    in_fm=true
    for ((i=1;i<n;i++)); do
      if [[ "${lines[$i]}" == "---" ]]; then
        fm_end=$i
        break
      fi
    done
  fi

  # Process lines
  last_written="__BOF__"
  write_blank() {
    if [[ "$last_written" != "__BLANK__" ]]; then
      printf "\n" >> "$tmp"
      last_written="__BLANK__"
    fi
  }
  write_line() {
    printf "%s\n" "$1" >> "$tmp"
    if is_blank "$1"; then last_written="__BLANK__"; else last_written="__TEXT__"; fi
  }

  for ((i=0;i<n;i++)); do
    s="${lines[$i]}"

    # Handle YAML frontmatter passthrough
    if $in_fm && (( i <= fm_end )); then
      write_line "$s"
      if (( i == fm_end )) && (( i+1 < n )) && ! is_blank "${lines[$i+1]}"; then
        write_blank
      fi
      continue
    fi

    # Toggle fenced code blocks
    if [[ "$s" =~ ^\`\`\` ]]; then
      in_code=$([ "$in_code" = true ] && echo false || echo true)
      write_line "$s"
      continue
    fi
    if $in_code; then
      write_line "$s"
      continue
    fi

    # Enforce blank lines around headings
    if [[ "$s" =~ ^#{1,6}[[:space:]]+ ]]; then
      # Blank before heading unless at BOF or immediately after frontmatter end or previous is blank
      if [[ "$last_written" != "__BOF__" && "$last_written" != "__BLANK__" ]]; then
        write_blank
      fi
      write_line "$s"
      # Blank after heading unless next line is already blank or EOF
      if (( i+1 < n )) && ! is_blank "${lines[$i+1]}"; then
        write_blank
      fi
      continue
    fi

    write_line "$s"
  done

  # Collapse multiple blank lines to one
  local tmp2=$(mktemp)
  awk '/^$/{if (++n >= 2) next} {n=0} 1' "$tmp" > "$tmp2"

  # Ensure final newline
  printf '%s\n' "$(cat "$tmp2")" > "$tmp"

  if ! cmp -s "$file" "$tmp"; then
    mv "$tmp" "$file"
    echo -e "  ${YELLOW}✓ Updated: $file${NC}"
  else
    rm -f "$tmp"
    echo -e "  ${GREEN}✓ No change: $file${NC}"
  fi
  rm -f "$tmp2" 2>/dev/null || true
}

main() {
  echo -e "${BLUE}Enforcing MD022 across all .md and .mdc files...${NC}"
  local count=0
  # Find all .md and .mdc files excluding node_modules/.git/build
  while IFS= read -r f; do
    fix_md022_file "$f"
    ((count++))
  done < <(find . \( -name "*.md" -o -name "*.mdc" \) -type f | grep -v "/node_modules/" | grep -v "/.git/" | grep -v "/build/" | sort)
  echo -e "${GREEN}Done. Processed $count files.${NC}"
}

main

