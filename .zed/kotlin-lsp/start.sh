#!/bin/bash
# Enhanced Kotlin Language Server startup script
# Addresses timeout and memory issues

# JVM options optimized for large Android projects
JVM_OPTS=(
    "-Xmx6g"                          # Increase heap to 6GB (adjust based on available RAM)
    "-Xms2g"                          # Set initial heap to 2GB
    "-XX:+UseG1GC"                    # Use G1 garbage collector for better performance
    "-XX:MaxGCPauseMillis=200"        # Limit GC pause times
    "-XX:+UnlockExperimentalVMOptions"
    "-XX:+UseJVMCICompiler"           # Use modern compiler if available
    "-Dlogback.configurationFile=/dev/null"  # Suppress SLF4J warnings
    "-Dorg.slf4j.simpleLogger.defaultLogLevel=WARN" # Reduce logging noise
)

# Check if kotlin-language-server is available
if ! command -v kotlin-language-server >/dev/null 2>&1; then
    echo "Error: kotlin-language-server not found in PATH" >&2
    exit 1
fi

# Launch with optimized JVM settings
exec kotlin-language-server "${JVM_OPTS[@]}" "$@"