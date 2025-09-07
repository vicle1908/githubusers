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

 1. **Get Crash Logs (Android MCP)**:


```bash
```text

mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")

```

 1. **Get Detailed Stack Trace**:
```bash


```text
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
```

### Phase 2: Investigation

 1. **Search Code Patterns**:


```bash
```text

mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_claude-context_search_code("date formatting Instant LocalDate conversion")

```

 1. **Component-Specific Logs**:
```bash


```text
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_claude-context_search_code("date formatting Instant LocalDate conversion")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|MainActivity)' | tail -15")
```

### Phase 3: Fix Application

 1. **Apply Code Fixes** using appropriate editing tools
 1. **Search for Similar Issues**:


```bash
```text

mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_claude-context_search_code("date formatting Instant LocalDate conversion")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|MainActivity)' | tail -15")
mcp_claude-context_search_code("similar pattern that needs fixing")

```

### Phase 4: Build Verification

**MANDATORY - Use Gradle MCP**:
```bash


```text
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_claude-context_search_code("date formatting Instant LocalDate conversion")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|MainActivity)' | tail -15")
mcp_claude-context_search_code("similar pattern that needs fixing")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
```

**NEVER** use `./gradlew` directly

### Phase 5: Installation and Testing

 1. **Install APK (Android MCP)**:


```bash
```text

mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_claude-context_search_code("date formatting Instant LocalDate conversion")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|MainActivity)' | tail -15")
mcp_claude-context_search_code("similar pattern that needs fixing")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")

```

Or use the install_apk helper if available

 1. **Launch App (Mobile-MCP)**:
```bash


```text
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_claude-context_search_code("date formatting Instant LocalDate conversion")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|MainActivity)' | tail -15")
mcp_claude-context_search_code("similar pattern that needs fixing")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers.debug")
```

 1. **Capture Screenshot**:


```bash
```text

mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_claude-context_search_code("date formatting Instant LocalDate conversion")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|MainActivity)' | tail -15")
mcp_claude-context_search_code("similar pattern that needs fixing")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers.debug")
mcp_mobile-mcp_mobile_take_screenshot()

```

 1. **Verify Success**:
```bash


```text
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_claude-context_search_code("date formatting Instant LocalDate conversion")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|MainActivity)' | tail -15")
mcp_claude-context_search_code("similar pattern that needs fixing")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers.debug")
mcp_mobile-mcp_mobile_take_screenshot()
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(Success|UserDetail)' | tail -10")
```

## Common Crash Patterns

### Date/Time Formatting

**Problem**:

```kotlin
```text

mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_claude-context_search_code("date formatting Instant LocalDate conversion")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|MainActivity)' | tail -15")
mcp_claude-context_search_code("similar pattern that needs fixing")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers.debug")
mcp_mobile-mcp_mobile_take_screenshot()
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(Success|UserDetail)' | tail -10")
DateTimeFormatter.ofPattern("MMMM yyyy").format(instant)  // Crashes

```

**Solution**:
```kotlin


```text
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_claude-context_search_code("date formatting Instant LocalDate conversion")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|MainActivity)' | tail -15")
mcp_claude-context_search_code("similar pattern that needs fixing")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers.debug")
mcp_mobile-mcp_mobile_take_screenshot()
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(Success|UserDetail)' | tail -10")
DateTimeFormatter.ofPattern("MMMM yyyy").format(instant)  // Crashes
DateTimeFormatter.ofPattern("MMMM yyyy")
    .format(instant.atZone(ZoneId.systemDefault()).toLocalDate())
```

### Null Pointer Exceptions

**Problem**:

```kotlin
```kotlin

mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_claude-context_search_code("date formatting Instant LocalDate conversion")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|MainActivity)' | tail -15")
mcp_claude-context_search_code("similar pattern that needs fixing")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers.debug")
mcp_mobile-mcp_mobile_take_screenshot()
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(Success|UserDetail)' | tail -10")
DateTimeFormatter.ofPattern("MMMM yyyy").format(instant)  // Crashes
DateTimeFormatter.ofPattern("MMMM yyyy")
    .format(instant.atZone(ZoneId.systemDefault()).toLocalDate())
val username = savedStateHandle["username"]  // Can be null

```

**Solution**:
```kotlin


```kotlin
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_claude-context_search_code("date formatting Instant LocalDate conversion")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|MainActivity)' | tail -15")
mcp_claude-context_search_code("similar pattern that needs fixing")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers.debug")
mcp_mobile-mcp_mobile_take_screenshot()
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(Success|UserDetail)' | tail -10")
DateTimeFormatter.ofPattern("MMMM yyyy").format(instant)  // Crashes
DateTimeFormatter.ofPattern("MMMM yyyy")
    .format(instant.atZone(ZoneId.systemDefault()).toLocalDate())
val username = savedStateHandle["username"]  // Can be null
val username = savedStateHandle["username"] ?: ""
```

### Navigation Issues

**Problem**:

```kotlin
```kotlin

mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_claude-context_search_code("date formatting Instant LocalDate conversion")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|MainActivity)' | tail -15")
mcp_claude-context_search_code("similar pattern that needs fixing")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers.debug")
mcp_mobile-mcp_mobile_take_screenshot()
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(Success|UserDetail)' | tail -10")
DateTimeFormatter.ofPattern("MMMM yyyy").format(instant)  // Crashes
DateTimeFormatter.ofPattern("MMMM yyyy")
    .format(instant.atZone(ZoneId.systemDefault()).toLocalDate())
val username = savedStateHandle["username"]  // Can be null
val username = savedStateHandle["username"] ?: ""
"app://users/$username"  // Incorrect pattern

```

**Solution**:
```kotlin


```kotlin
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_claude-context_search_code("date formatting Instant LocalDate conversion")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|MainActivity)' | tail -15")
mcp_claude-context_search_code("similar pattern that needs fixing")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers.debug")
mcp_mobile-mcp_mobile_take_screenshot()
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(Success|UserDetail)' | tail -10")
DateTimeFormatter.ofPattern("MMMM yyyy").format(instant)  // Crashes
DateTimeFormatter.ofPattern("MMMM yyyy")
    .format(instant.atZone(ZoneId.systemDefault()).toLocalDate())
val username = savedStateHandle["username"]  // Can be null
val username = savedStateHandle["username"] ?: ""
"app://users/$username"  // Incorrect pattern
"app://users/user/$username"  // Correct deep link
```

## Logcat Filter Patterns (via Android MCP)

### Crash Detection


```bash
```kotlin

mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_claude-context_search_code("date formatting Instant LocalDate conversion")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|MainActivity)' | tail -15")
mcp_claude-context_search_code("similar pattern that needs fixing")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers.debug")
mcp_mobile-mcp_mobile_take_screenshot()
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(Success|UserDetail)' | tail -10")
DateTimeFormatter.ofPattern("MMMM yyyy").format(instant)  // Crashes
DateTimeFormatter.ofPattern("MMMM yyyy")
    .format(instant.atZone(ZoneId.systemDefault()).toLocalDate())
val username = savedStateHandle["username"]  // Can be null
val username = savedStateHandle["username"] ?: ""
"app://users/$username"  // Incorrect pattern
"app://users/user/$username"  // Correct deep link
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception|Error|Crash)' | tail -20")

```

### Component Debugging
```bash


```kotlin
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_claude-context_search_code("date formatting Instant LocalDate conversion")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|MainActivity)' | tail -15")
mcp_claude-context_search_code("similar pattern that needs fixing")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers.debug")
mcp_mobile-mcp_mobile_take_screenshot()
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(Success|UserDetail)' | tail -10")
DateTimeFormatter.ofPattern("MMMM yyyy").format(instant)  // Crashes
DateTimeFormatter.ofPattern("MMMM yyyy")
    .format(instant.atZone(ZoneId.systemDefault()).toLocalDate())
val username = savedStateHandle["username"]  // Can be null
val username = savedStateHandle["username"] ?: ""
"app://users/$username"  // Incorrect pattern
"app://users/user/$username"  // Correct deep link
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception|Error|Crash)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|ViewModel|Repository)' | tail -15")
```

### Stack Trace Analysis


```bash
```kotlin

mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_claude-context_search_code("date formatting Instant LocalDate conversion")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|MainActivity)' | tail -15")
mcp_claude-context_search_code("similar pattern that needs fixing")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers.debug")
mcp_mobile-mcp_mobile_take_screenshot()
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(Success|UserDetail)' | tail -10")
DateTimeFormatter.ofPattern("MMMM yyyy").format(instant)  // Crashes
DateTimeFormatter.ofPattern("MMMM yyyy")
    .format(instant.atZone(ZoneId.systemDefault()).toLocalDate())
val username = savedStateHandle["username"]  // Can be null
val username = savedStateHandle["username"] ?: ""
"app://users/$username"  // Incorrect pattern
"app://users/user/$username"  // Correct deep link
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception|Error|Crash)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|ViewModel|Repository)' | tail -15")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")

```

### Real-time Monitoring
```bash


```kotlin
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_claude-context_search_code("date formatting Instant LocalDate conversion")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|MainActivity)' | tail -15")
mcp_claude-context_search_code("similar pattern that needs fixing")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers.debug")
mcp_mobile-mcp_mobile_take_screenshot()
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(Success|UserDetail)' | tail -10")
DateTimeFormatter.ofPattern("MMMM yyyy").format(instant)  // Crashes
DateTimeFormatter.ofPattern("MMMM yyyy")
    .format(instant.atZone(ZoneId.systemDefault()).toLocalDate())
val username = savedStateHandle["username"]  // Can be null
val username = savedStateHandle["username"] ?: ""
"app://users/$username"  // Incorrect pattern
"app://users/user/$username"  // Correct deep link
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception|Error|Crash)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|ViewModel|Repository)' | tail -15")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_android_execute_adb_shell_command("logcat -c && logcat | grep -E '(YourTag)' | head -10")
```

## Build and Test Commands (MCP-Only)

### Clean Build


```bash
```kotlin

mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_claude-context_search_code("date formatting Instant LocalDate conversion")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|MainActivity)' | tail -15")
mcp_claude-context_search_code("similar pattern that needs fixing")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers.debug")
mcp_mobile-mcp_mobile_take_screenshot()
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(Success|UserDetail)' | tail -10")
DateTimeFormatter.ofPattern("MMMM yyyy").format(instant)  // Crashes
DateTimeFormatter.ofPattern("MMMM yyyy")
    .format(instant.atZone(ZoneId.systemDefault()).toLocalDate())
val username = savedStateHandle["username"]  // Can be null
val username = savedStateHandle["username"] ?: ""
"app://users/$username"  // Incorrect pattern
"app://users/user/$username"  // Correct deep link
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception|Error|Crash)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|ViewModel|Repository)' | tail -15")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_android_execute_adb_shell_command("logcat -c && logcat | grep -E '(YourTag)' | head -10")
mcp_gradle-mcp-server_execute_gradle_task(":app:clean")

```

### Debug Build
```bash


```kotlin
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_claude-context_search_code("date formatting Instant LocalDate conversion")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|MainActivity)' | tail -15")
mcp_claude-context_search_code("similar pattern that needs fixing")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers.debug")
mcp_mobile-mcp_mobile_take_screenshot()
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(Success|UserDetail)' | tail -10")
DateTimeFormatter.ofPattern("MMMM yyyy").format(instant)  // Crashes
DateTimeFormatter.ofPattern("MMMM yyyy")
    .format(instant.atZone(ZoneId.systemDefault()).toLocalDate())
val username = savedStateHandle["username"]  // Can be null
val username = savedStateHandle["username"] ?: ""
"app://users/$username"  // Incorrect pattern
"app://users/user/$username"  // Correct deep link
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception|Error|Crash)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|ViewModel|Repository)' | tail -15")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_android_execute_adb_shell_command("logcat -c && logcat | grep -E '(YourTag)' | head -10")
mcp_gradle-mcp-server_execute_gradle_task(":app:clean")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
```

### Install APK


```bash
```kotlin

mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_claude-context_search_code("date formatting Instant LocalDate conversion")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|MainActivity)' | tail -15")
mcp_claude-context_search_code("similar pattern that needs fixing")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers.debug")
mcp_mobile-mcp_mobile_take_screenshot()
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(Success|UserDetail)' | tail -10")
DateTimeFormatter.ofPattern("MMMM yyyy").format(instant)  // Crashes
DateTimeFormatter.ofPattern("MMMM yyyy")
    .format(instant.atZone(ZoneId.systemDefault()).toLocalDate())
val username = savedStateHandle["username"]  // Can be null
val username = savedStateHandle["username"] ?: ""
"app://users/$username"  // Incorrect pattern
"app://users/user/$username"  // Correct deep link
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception|Error|Crash)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|ViewModel|Repository)' | tail -15")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_android_execute_adb_shell_command("logcat -c && logcat | grep -E '(YourTag)' | head -10")
mcp_gradle-mcp-server_execute_gradle_task(":app:clean")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")

# Via Android MCP

mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")

```

### Verify Installation
```bash


```kotlin
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_claude-context_search_code("date formatting Instant LocalDate conversion")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|MainActivity)' | tail -15")
mcp_claude-context_search_code("similar pattern that needs fixing")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers.debug")
mcp_mobile-mcp_mobile_take_screenshot()
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(Success|UserDetail)' | tail -10")
DateTimeFormatter.ofPattern("MMMM yyyy").format(instant)  // Crashes
DateTimeFormatter.ofPattern("MMMM yyyy")
    .format(instant.atZone(ZoneId.systemDefault()).toLocalDate())
val username = savedStateHandle["username"]  // Can be null
val username = savedStateHandle["username"] ?: ""
"app://users/$username"  // Incorrect pattern
"app://users/user/$username"  // Correct deep link
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception|Error|Crash)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|ViewModel|Repository)' | tail -15")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_android_execute_adb_shell_command("logcat -c && logcat | grep -E '(YourTag)' | head -10")
mcp_gradle-mcp-server_execute_gradle_task(":app:clean")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
# Via Android MCP
mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")
mcp_android_execute_adb_shell_command("pm list packages | grep githubusers")
```

### Launch App


```bash
```kotlin

mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_claude-context_search_code("date formatting Instant LocalDate conversion")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|MainActivity)' | tail -15")
mcp_claude-context_search_code("similar pattern that needs fixing")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers.debug")
mcp_mobile-mcp_mobile_take_screenshot()
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(Success|UserDetail)' | tail -10")
DateTimeFormatter.ofPattern("MMMM yyyy").format(instant)  // Crashes
DateTimeFormatter.ofPattern("MMMM yyyy")
    .format(instant.atZone(ZoneId.systemDefault()).toLocalDate())
val username = savedStateHandle["username"]  // Can be null
val username = savedStateHandle["username"] ?: ""
"app://users/$username"  // Incorrect pattern
"app://users/user/$username"  // Correct deep link
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception|Error|Crash)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|ViewModel|Repository)' | tail -15")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_android_execute_adb_shell_command("logcat -c && logcat | grep -E '(YourTag)' | head -10")
mcp_gradle-mcp-server_execute_gradle_task(":app:clean")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")

# Via Android MCP

mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")
mcp_android_execute_adb_shell_command("pm list packages | grep githubusers")
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers.debug")

```

### Take Screenshot
```bash


```kotlin
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_claude-context_search_code("date formatting Instant LocalDate conversion")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|MainActivity)' | tail -15")
mcp_claude-context_search_code("similar pattern that needs fixing")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers.debug")
mcp_mobile-mcp_mobile_take_screenshot()
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(Success|UserDetail)' | tail -10")
DateTimeFormatter.ofPattern("MMMM yyyy").format(instant)  // Crashes
DateTimeFormatter.ofPattern("MMMM yyyy")
    .format(instant.atZone(ZoneId.systemDefault()).toLocalDate())
val username = savedStateHandle["username"]  // Can be null
val username = savedStateHandle["username"] ?: ""
"app://users/$username"  // Incorrect pattern
"app://users/user/$username"  // Correct deep link
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception|Error|Crash)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|ViewModel|Repository)' | tail -15")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_android_execute_adb_shell_command("logcat -c && logcat | grep -E '(YourTag)' | head -10")
mcp_gradle-mcp-server_execute_gradle_task(":app:clean")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
# Via Android MCP
mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")
mcp_android_execute_adb_shell_command("pm list packages | grep githubusers")
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers.debug")
mcp_mobile-mcp_mobile_take_screenshot()
```

## Manual ADB Usage (LAST RESORT ONLY)

**⚠️ WARNING**: Manual ADB commands are **ONLY** allowed when:

 1. MCP servers are unavailable
 1. You have **EXPLICIT** user approval
 1. No other option exists

If manual ADB is approved:

```bash
```kotlin

mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_claude-context_search_code("date formatting Instant LocalDate conversion")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|MainActivity)' | tail -15")
mcp_claude-context_search_code("similar pattern that needs fixing")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")
mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers.debug")
mcp_mobile-mcp_mobile_take_screenshot()
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(Success|UserDetail)' | tail -10")
DateTimeFormatter.ofPattern("MMMM yyyy").format(instant)  // Crashes
DateTimeFormatter.ofPattern("MMMM yyyy")
    .format(instant.atZone(ZoneId.systemDefault()).toLocalDate())
val username = savedStateHandle["username"]  // Can be null
val username = savedStateHandle["username"] ?: ""
"app://users/$username"  // Incorrect pattern
"app://users/user/$username"  // Correct deep link
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(FATAL|AndroidRuntime|Exception|Error|Crash)' | tail -20")
mcp_android_execute_adb_shell_command("logcat -d | grep -E '(UserDetail|ViewModel|Repository)' | tail -15")
mcp_android_execute_adb_shell_command("logcat -d | grep -A 30 -B 5 'FATAL EXCEPTION' | tail -40")
mcp_android_execute_adb_shell_command("logcat -c && logcat | grep -E '(YourTag)' | head -10")
mcp_gradle-mcp-server_execute_gradle_task(":app:clean")
mcp_gradle-mcp-server_execute_gradle_task(":app:assembleDebug")

# Via Android MCP

mcp_android_execute_adb_shell_command("pm install -r /path/to/app-debug.apk")
mcp_android_execute_adb_shell_command("pm list packages | grep githubusers")
mcp_mobile-mcp_mobile_launch_app("com.example.githubusers.debug")
mcp_mobile-mcp_mobile_take_screenshot()

# Only with explicit approval

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
- [ ] Installed with Android MCP
- [ ] Launched with Mobile-MCP
- [ ] Screenshot taken with Mobile-MCP
- [ ] Logs verified with Android MCP
- [ ] No new crashes detected

## Best Practices

### DO
- Use MCP tools for ALL debugging operations
- Follow systematic approach: Detect → Analyze → Fix → Verify
- Apply consistent fixes across similar issues
- Test thoroughly using Mobile-MCP
- Document solutions in ByteRover

### DON'T
- Use manual ADB without explicit approval
- Skip MCP tools for convenience
- Apply quick fixes without understanding
- Ignore similar issues in codebase
- Skip verification steps

## Integration with Other Tools

- Use `mcp_byterover-mcp_byterover-store-knowledge` to save debugging patterns
- Use `mcp_openmemory_add-memory` to store debugging decisions
- Use `mcp_zen_consensus` for complex debugging scenarios

## Enforcement

 1. **ALWAYS** use Android MCP for logcat and device operations
 1. **ALWAYS** use Mobile-MCP for app launch and UI interaction
 1. **ALWAYS** use Gradle MCP for builds
 1. **NEVER** use manual ADB without explicit approval
 1. **NEVER** use `./gradlew` directly
