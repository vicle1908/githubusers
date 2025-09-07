#!/bin/bash

# Test script to verify Git hook functionality

echo "🧪 Testing Git hook sync functionality..."
echo

# Create a test change in a canonical doc
TEST_FILE="docs/assistants/test-hook.md"
echo "# Test Hook Document" > "$TEST_FILE"
echo "This is a test document to verify hook functionality" >> "$TEST_FILE"

# Stage the test file
git add "$TEST_FILE"

echo "📝 Created and staged test file: $TEST_FILE"
echo "🎣 Triggering pre-commit hook..."
echo

# Run the pre-commit hook directly
if [[ -f ".git/hooks/pre-commit" ]]; then
    ./.git/hooks/pre-commit
    HOOK_RESULT=$?
    
    if [[ $HOOK_RESULT -eq 0 ]]; then
        echo "✅ Hook executed successfully"
    else
        echo "❌ Hook execution failed"
    fi
else
    echo "❌ Pre-commit hook not found"
fi

# Clean up test file
rm -f "$TEST_FILE"
git reset HEAD "$TEST_FILE" 2>/dev/null

echo
echo "🧹 Cleaned up test file"
echo "✅ Hook test complete!"
