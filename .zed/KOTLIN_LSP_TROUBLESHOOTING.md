# Kotlin Language Server Troubleshooting Guide

## Issues Addressed

### 1. Initialization Timeout
**Error**: `Request timed out` - Language server fails to initialize within default timeout

### 2. SLF4J Warning  
**Error**: 
```
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
```

## Applied Fixes

### 1. Configuration Optimizations

**Increased debounce time** to 2000ms (from default 250ms):
```json
"diagnostics": {
  "enabled": true,
  "debounceTime": 2000
}
```

### 2. Memory Management Scripts

Created helper scripts for better JVM memory management:

**`.zed/kotlin-lsp/start.sh`** - Optimized startup script with:
- `-Xmx6g` - Increased heap size to 6GB
- `-XX:+UseG1GC` - Better garbage collector
- `-Dlogback.configurationFile=/dev/null` - Suppresses SLF4J warnings

**`kls-classpath.sh`** - Project classpath helper for dependency resolution

### 3. Alternative Solutions

If timeouts persist, try these approaches:

#### Option A: Increase System JVM Memory
Set environment variable:
```bash
export JAVA_OPTS="-Xmx8g -XX:+UseG1GC"
```

#### Option B: Use Kotlin LSP (Official)
Switch to official Kotlin LSP instead of kotlin-language-server:
```json
"languages": {
  "Kotlin": {
    "language_servers": ["kotlin-lsp"]
  }
}
```

#### Option C: Exclude Large Modules
For very large projects, temporarily exclude heavy modules during development.

## Troubleshooting Steps

1. **Check Available RAM**: Ensure system has 8GB+ available
2. **Restart Zed**: After configuration changes
3. **Check logs**: Use Zed's LSP debug panel
4. **Gradual loading**: Open smaller files first, then larger ones
5. **Clear caches**: Delete `.gradle/caches` if necessary

## Performance Tips

- **Use module-specific development**: Focus on one feature module at a time
- **Exclude test sources**: If not actively testing
- **Optimize Gradle**: Ensure Gradle daemon is running and optimized
- **SSD storage**: Projects on SSD perform significantly better

## Alternative IDEs

If Kotlin Language Server continues to timeout:
- **Android Studio**: Native Kotlin support
- **IntelliJ IDEA**: Excellent Kotlin integration  
- **VSCode**: With Kotlin extension
- **Neovim**: With nvim-lspconfig optimization