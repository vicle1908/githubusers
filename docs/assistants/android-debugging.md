# Android Debugging Guide (MCP-First)

## MCP Tool Integration for Debugging

### Primary Debugging Tools (USE THESE FIRST)

| Tool | Purpose | When to Use |
|------|---------|-------------|
| `mcp_android_execute_adb_shell_command` | Logcat access and device operations | Crash detection, log analysis |
| `mcp_mobile-mcp_mobile_launch_app` | App testing and automation | Launch app, UI interaction |
| `mcp_mobile-mcp_mobile_take_screenshot` | Visual verification | UI state confirmation |
| `mcp_gradle-mcp-server_execute_gradle_task` | Build verification | After fixes, ensuring compilation |
| `mcp_claude-context_search_code` | Code pattern research | Root cause analysis |

## Debugging Workflow (MCP-First)

### Phase 1: Crash Detection

1. Get crash logs (Android MCP):

```bash
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")
```

1. Get detailed stack trace:

```bash
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
```

### Phase 2: Investigation

1. Search code patterns:

```bash
mcp_claude-context_search_code("date formatting Instant LocalDate conversion")
```

1. Component-specific logs:

```bash
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|MainActivity)' | tail -15")
```

### Phase 3: Fix Application

1. Apply code fixes using appropriate editing tools
2. Search for similar issues:

```bash
mcp_claude-context_search_code("similar pattern that needs fixing")
```

### Phase 4: Build Verification

MANDATORY - Use Gradle MCP:

```bash
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
```

NEVER use `./gradlew` directly.

### Phase 5: Installation and Testing

1. Install APK (Android MCP):

```bash
mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")
```

1. Launch App (Mobile-MCP):

```bash
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers.debug")
```

1. Capture Screenshot:

```bash
mcp_mobile-mcp_mobile_take_screenshot()
```

1. Verify Success:

```bash
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(Success|UserDetail)' | tail -10")
```

## Common Crash Patterns

### Date/Time Formatting

Problem:

```kotlin
DateTimeFormatter.ofPattern("MMMM yyyy").format(instant)  // Crashes
```

Solution:

```kotlin
DateTimeFormatter.ofPattern("MMMM yyyy")
    .format(instant.atZone(ZoneId.systemDefault()).toLocalDate())
```

### Null Pointer Exceptions

Problem:

```kotlin
val username = savedStateHandle["username"]  // Can be null
```

Solution:

```kotlin
val username = savedStateHandle["username"] ?: ""
```

### Navigation Issues

Problem:

```kotlin
"app://users/$username"  // Incorrect pattern
```

Solution:

```kotlin
"app://users/user/$username"  // Correct deep link
```

## Logcat Filter Patterns (via Android MCP)

Crash Detection:

```bash
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception|Error|Crash)' | tail -20")
```

Component Debugging:

```bash
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|ViewModel|Repository)' | tail -15")
```

Stack Trace Analysis:

```bash
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
```

Real-time Monitoring:

```bash
mcp_android_execute_adb_shell_command("logcat -c && logcat | grep -E '(YourTag)' | head -10")
```

## Build and Test Commands (MCP-Only)

Clean Build:

```bash
mcp_gradle-mcp-server_execute_gradle_task(":app:clean")
```

Debug Build:

```bash
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
```

Install APK:

```bash
mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")
```

Verify Installation:

```bash
mcp_android_execute_adb_shell_command("pm list packages | grep githubusers")
```

Launch App:

```bash
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers.debug")
```

Take Screenshot:

```bash
mcp_mobile-mcp_mobile_take_screenshot()
```

## Manual ADB Usage (LAST RESORT ONLY)

WARNING: Manual ADB commands are ONLY allowed when:

1. MCP servers are unavailable
2. You have EXPLICIT user approval
3. No other option exists

If manual ADB is approved:

```bash
adb install -r app-debug.apk
adb logcat -d | grep "FATAL"
```

## Quality Assurance Checklist

### Pre-Fix

- [ ] Identified crash location via Android MCP logcat
- [ ] Analyzed stack trace with Android MCP
- [ ] Researched patterns with Claude Context
- [ ] Understood root cause

### Post-Fix

- [ ] Applied consistent fixes across codebase
- [ ] Build verified with Gradle MCP
- [ ] Verified installation and app startup
- [ ] Validated logs show success markers
