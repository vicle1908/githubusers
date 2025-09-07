#!/bin/bash

# Helper script to open files in existing Android Studio Preview instance
# This script uses AppleScript to target the running Android Studio process

FILE_PATH="$1"

# Check if Android Studio Preview is running
if ! pgrep -f "Android Studio Preview" > /dev/null; then
    echo "Android Studio Preview is not running"
    exit 1
fi

# Use AppleScript to send keyboard shortcut to open file in existing instance
osascript << EOF
tell application "Android Studio Preview"
    activate
    delay 0.5
    tell application "System Events"
        keystroke "o" using {command down}
        delay 0.5
        keystroke "$FILE_PATH"
        delay 0.2
        key code 36
    end tell
end tell
EOF

echo "Opened $FILE_PATH in existing Android Studio Preview instance"