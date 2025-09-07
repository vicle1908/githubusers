#!/bin/bash

# Fix markdownlint issues in .cursor/rules/*.mdc
# - Surround headings/code fences/lists with blank lines (MD022/MD031/MD032)
# - Normalize ordered lists to "1." (MD029)
# - Add language to code fences if missing (MD040)
# - Trim trailing spaces, collapse blank lines, ensure final newline

set -euo pipefail

BLUE='\033[0;34m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

fix_file() {
  local file="$1"
  echo -e "${BLUE}Fixing: $file${NC}"

  local tmp=$(mktemp)
  local changed=false

  # First pass: add language to unlabeled code fences (portable bash)
  tmp_lang=$(mktemp)
  in_code=false
  buf=""
  while IFS= read -r line || [ -n "$line" ]; do
    if [ "$line" = "```" ]; then
      if [ "$in_code" = false ]; then
        in_code=true
        buf=""
      else
        # closing fence
        in_code=false
        # detect language
        if echo "$buf" | grep -qE "@Composable|(^| )fun |(^| )class |(^| )interface |(^| )val |(^| )var |package com\\."; then
          lang="kotlin"
        elif echo "$buf" | grep -qE "dependencies \{|apply plugin:|implementation |testImplementation|android \{"; then
          lang="gradle"
        elif echo "$buf" | grep -qE "^<|xmlns:|android:"; then
          lang="xml"
        elif echo "$buf" | grep -qE "^#!/|(^| )echo( |$)|(^| )git( |$)|(^| )npm( |$)|(^| )yarn( |$)"; then
          lang="bash"
        elif echo "$buf" | grep -qE "^\{|^\["; then
          lang="json"
        elif echo "$buf" | grep -qE "^[A-Za-z_-]+:|^  - |^- " ; then
          lang="yaml"
        elif echo "$buf" | grep -qE "^# |^## |^- "; then
          lang="markdown"
        else
          lang="text"
        fi
        printf "%s\n" "\`\`\`$lang" >> "$tmp_lang"
        printf "%s\n" "$buf" >> "$tmp_lang"
        printf "%s\n" "\`\`\`" >> "$tmp_lang"
      fi
    else
      if [ "$in_code" = true ]; then
        if [ -z "$buf" ]; then buf="$line"; else buf="$buf
$line"; fi
      else
        printf '%s\n' "$line" >> "$tmp_lang"
      fi
    fi
  done < "$file"
  # if file ended while in code block, close it
  if [ "$in_code" = true ]; then
    printf "%s\n%s\n" "\`\`\`" "$buf" >> "$tmp_lang"
  fi
  mv "$tmp_lang" "$tmp"

  if ! cmp -s "$file" "$tmp"; then changed=true; fi

  # Second pass: surround headings/code fences/lists with blank lines (skip YAML frontmatter)
  local tmp2=$(mktemp)
  awk '
    function is_blank(s){ return s ~ /^[[:space:]]*$/ }
    BEGIN { in_code=0; in_fm=0 }
    {
      a[NR]=$0
    }
    END {
      n=NR
      # detect frontmatter
      if (n>=1 && a[1]=="---") {
        in_fm=1
        for (i=2;i<=n;i++){ if (a[i]=="---") { fm_end=i; break } }
      }
      for (i=1;i<=n;i++) {
        s=a[i]
        # code fences
        if (s ~ /^```/) {
          if (in_code==0) {
            if (!is_blank(a[i-1]) && !(in_fm && i-1==fm_end)) print ""
            in_code=1
            print s
            next
          } else {
            in_code=0
            print s
            if (!is_blank(a[i+1])) print ""
            next
          }
        }
        if (in_code==1) { print s; continue }
        # headings
        if (s ~ /^#{1,6}[[:space:]]+/) {
          if (!is_blank(a[i-1]) && !(in_fm && i-1==fm_end)) print ""
          print s
          if (!is_blank(a[i+1])) print ""
          continue
        }
        # top-level list items
        if (s ~ /^([-*+]|[0-9]+[.)])[[:space:]]+/) {
          if (!is_blank(a[i-1])) print ""
          # emit contiguous list block
          j=i
          while (j<=n) {
            t=a[j]
            if (is_blank(t)) break
            if (t !~ /^[[:space:]]*([-*+]|[0-9]+[.)])[[:space:]]+/) break
            print t
            j++
          }
          if (!is_blank(a[j])) print ""
          i=j-1
          continue
        }
        print s
      }
    }
  ' "$tmp" > "$tmp2"

  if ! cmp -s "$tmp" "$tmp2"; then changed=true; fi

  # Third pass: normalize ordered list numbering to "1." (preserve indent)
  sed -E -i '' 's/^([[:space:]]*)[0-9]+\.[[:space:]]+/\1 1. /' "$tmp2"

  # Trim trailing spaces
  sed -i '' 's/[ \t]*$//' "$tmp2"

  # Collapse multiple blank lines to one
  awk '/^$/{if (++n >= 2) next} {n=0} 1' "$tmp2" > "$tmp"

  # Ensure file ends with newline
  printf '%s\n' "$(cat "$tmp")" > "$tmp2"

  if ! cmp -s "$file" "$tmp2"; then
    mv "$tmp2" "$file"
    changed=true
  else
    rm -f "$tmp2"
  fi
  rm -f "$tmp"

  if [ "$changed" = true ]; then
    echo -e "  ${YELLOW}✓ Fixed${NC}"
  else
    echo -e "  ${GREEN}✓ Already clean${NC}"
  fi
}

main() {
  shopt -s nullglob
  local files=(.cursor/rules/*.mdc)
  if [ ${#files[@]} -eq 0 ]; then
    echo "No .mdc files found in .cursor/rules"
    exit 0
  fi
  for f in "${files[@]}"; do
    fix_file "$f"
  done
  echo -e "${GREEN}Done fixing .cursor/rules/.mdc files${NC}"
}

main

