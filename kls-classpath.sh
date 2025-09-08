#!/bin/bash
# Kotlin Language Server classpath script for Android project
# This helps the language server find project dependencies

PROJECT_ROOT="$(pwd)"

# Try to get classpath from Gradle
if [ -f "./gradlew" ]; then
    echo "Getting classpath from Gradle..." >&2
    
    # Get Android dependencies 
    CLASSPATH=""
    
    # Add Android SDK jars if available
    if [ -n "$ANDROID_HOME" ]; then
        ANDROID_JAR="$ANDROID_HOME/platforms/android-34/android.jar"
        if [ -f "$ANDROID_JAR" ]; then
            CLASSPATH="$ANDROID_JAR"
        fi
    fi
    
    # Try to get project dependencies from Gradle
    if command -v ./gradlew >/dev/null 2>&1; then
        GRADLE_DEPS=$(./gradlew :app:dependencies --configuration=implementation 2>/dev/null | grep -E '\.jar$|\.aar$' | head -20 | tr '\n' ':' 2>/dev/null || true)
        if [ -n "$GRADLE_DEPS" ]; then
            CLASSPATH="$CLASSPATH:$GRADLE_DEPS"
        fi
    fi
    
    echo "$CLASSPATH"
else
    echo "No Gradle wrapper found" >&2
    exit 1
fi