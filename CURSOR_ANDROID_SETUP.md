# Cursor IDE Android Development Setup Guide

## Current Status Analysis

### ✅ What's Working
- **Android SDK**: Properly installed and configured (`/Users/vinhlekhanh/Library/Android/sdk`)
- **Environment Variables**: `ANDROID_HOME` correctly set
- **Gradle Build**: Working perfectly (builds succeed)
- **Extensions Installed**:
  - `fwcd.kotlin` - Kotlin Language Support
  - `esafirm.kotlin-formatter` - Kotlin formatting
  - `naco-siren.gradle-language` - Gradle language support
  - `richardwillis.vscode-gradle-extension-pack` - Gradle extension pack
  - `vscjava.vscode-gradle` - Gradle integration

### ❌ Current Issues
- **Linter False Positives**: Showing unresolved references for Android classes
- **Missing Android-specific extensions**: No dedicated Android extension
- **Incomplete VS Code configuration**: Limited settings for Android development

## Recommended Setup

### 1. Install Essential Extensions

```bash
```text
# Install these extensions in Cursor IDE:
code --install-extension mathiasfrohlich.Kotlin
code --install-extension redhat.java
code --install-extension vscjava.vscode-java-debug
code --install-extension vscjava.vscode-java-dependency
code --install-extension vscjava.vscode-java-test
code --install-extension vscjava.vscode-maven
code --install-extension vscjava.vscode-java-pack
```

### 2. Update VS Code Settings

Create/update `.vscode/settings.json`:

```json
```kotlin
# Install these extensions in Cursor IDE:
code --install-extension mathiasfrohlich.Kotlin
code --install-extension redhat.java
code --install-extension vscjava.vscode-java-debug
code --install-extension vscjava.vscode-java-dependency
code --install-extension vscjava.vscode-java-test
code --install-extension vscjava.vscode-maven
code --install-extension vscjava.vscode-java-pack
{
    "java.configuration.updateBuildConfiguration": "automatic",
    "java.compile.nullAnalysis.mode": "automatic",
    "java.configuration.runtimes": [
        {
            "name": "JavaSE-21",
            "path": "/Library/Java/JavaVirtualMachines/zulu-21.jdk/Contents/Home"
        }
    ],
    "java.home": "/Library/Java/JavaVirtualMachines/zulu-21.jdk/Contents/Home",
    "kotlin.languageServer.enabled": true,
    "kotlin.linting.enabled": true,
    "kotlin.formatting.enabled": true,
    "gradle.nestedProjects": true,
    "gradle.buildServer.enabled": true,
    "files.associations": {
        "*.kt": "kotlin",
        "*.kts": "kotlin"
    },
    "editor.formatOnSave": true,
    "editor.codeActionsOnSave": {
        "source.organizeImports": "explicit"
    }
}
```

### 3. Configure Android SDK Path

Add to your shell profile (`~/.zshrc` or `~/.bashrc`):

```bash
```kotlin
# Install these extensions in Cursor IDE:
code --install-extension mathiasfrohlich.Kotlin
code --install-extension redhat.java
code --install-extension vscjava.vscode-java-debug
code --install-extension vscjava.vscode-java-dependency
code --install-extension vscjava.vscode-java-test
code --install-extension vscjava.vscode-maven
code --install-extension vscjava.vscode-java-pack
{
    "java.configuration.updateBuildConfiguration": "automatic",
    "java.compile.nullAnalysis.mode": "automatic",
    "java.configuration.runtimes": [
        {
            "name": "JavaSE-21",
            "path": "/Library/Java/JavaVirtualMachines/zulu-21.jdk/Contents/Home"
        }
    ],
    "java.home": "/Library/Java/JavaVirtualMachines/zulu-21.jdk/Contents/Home",
    "kotlin.languageServer.enabled": true,
    "kotlin.linting.enabled": true,
    "kotlin.formatting.enabled": true,
    "gradle.nestedProjects": true,
    "gradle.buildServer.enabled": true,
    "files.associations": {
        "*.kt": "kotlin",
        "*.kts": "kotlin"
    },
    "editor.formatOnSave": true,
    "editor.codeActionsOnSave": {
        "source.organizeImports": "explicit"
    }
}
export ANDROID_HOME=/Users/vinhlekhanh/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/tools/bin
```

### 4. Create Kotlin Linting Configuration

Create `.vscode/tasks.json`:

```json
```kotlin
# Install these extensions in Cursor IDE:
code --install-extension mathiasfrohlich.Kotlin
code --install-extension redhat.java
code --install-extension vscjava.vscode-java-debug
code --install-extension vscjava.vscode-java-dependency
code --install-extension vscjava.vscode-java-test
code --install-extension vscjava.vscode-maven
code --install-extension vscjava.vscode-java-pack
{
    "java.configuration.updateBuildConfiguration": "automatic",
    "java.compile.nullAnalysis.mode": "automatic",
    "java.configuration.runtimes": [
        {
            "name": "JavaSE-21",
            "path": "/Library/Java/JavaVirtualMachines/zulu-21.jdk/Contents/Home"
        }
    ],
    "java.home": "/Library/Java/JavaVirtualMachines/zulu-21.jdk/Contents/Home",
    "kotlin.languageServer.enabled": true,
    "kotlin.linting.enabled": true,
    "kotlin.formatting.enabled": true,
    "gradle.nestedProjects": true,
    "gradle.buildServer.enabled": true,
    "files.associations": {
        "*.kt": "kotlin",
        "*.kts": "kotlin"
    },
    "editor.formatOnSave": true,
    "editor.codeActionsOnSave": {
        "source.organizeImports": "explicit"
    }
}
export ANDROID_HOME=/Users/vinhlekhanh/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/tools/bin
{
    "version": "2.0.0",
    "tasks": [
        {
            "label": "Kotlin: ktlintCheck",
            "type": "shell",
            "command": "./gradlew",
            "args": ["ktlintCheck"],
            "group": "build",
            "presentation": {
                "echo": true,
                "reveal": "always",
                "focus": false,
                "panel": "shared"
            },
            "problemMatcher": []
        },
        {
            "label": "Kotlin: ktlintFormat",
            "type": "shell",
            "command": "./gradlew",
            "args": ["ktlintFormat"],
            "group": "build",
            "presentation": {
                "echo": true,
                "reveal": "always",
                "focus": false,
                "panel": "shared"
            }
        },
        {
            "label": "Android: Build Debug",
            "type": "shell",
            "command": "./gradlew",
            "args": ["app:assembleDebug"],
            "group": "build",
            "presentation": {
                "echo": true,
                "reveal": "always",
                "focus": false,
                "panel": "shared"
            }
        }
    ]
}
```

### 5. Configure Linting Rules

Create `.vscode/extensions.json`:

```json
```kotlin
# Install these extensions in Cursor IDE:
code --install-extension mathiasfrohlich.Kotlin
code --install-extension redhat.java
code --install-extension vscjava.vscode-java-debug
code --install-extension vscjava.vscode-java-dependency
code --install-extension vscjava.vscode-java-test
code --install-extension vscjava.vscode-maven
code --install-extension vscjava.vscode-java-pack
{
    "java.configuration.updateBuildConfiguration": "automatic",
    "java.compile.nullAnalysis.mode": "automatic",
    "java.configuration.runtimes": [
        {
            "name": "JavaSE-21",
            "path": "/Library/Java/JavaVirtualMachines/zulu-21.jdk/Contents/Home"
        }
    ],
    "java.home": "/Library/Java/JavaVirtualMachines/zulu-21.jdk/Contents/Home",
    "kotlin.languageServer.enabled": true,
    "kotlin.linting.enabled": true,
    "kotlin.formatting.enabled": true,
    "gradle.nestedProjects": true,
    "gradle.buildServer.enabled": true,
    "files.associations": {
        "*.kt": "kotlin",
        "*.kts": "kotlin"
    },
    "editor.formatOnSave": true,
    "editor.codeActionsOnSave": {
        "source.organizeImports": "explicit"
    }
}
export ANDROID_HOME=/Users/vinhlekhanh/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/tools/bin
{
    "version": "2.0.0",
    "tasks": [
        {
            "label": "Kotlin: ktlintCheck",
            "type": "shell",
            "command": "./gradlew",
            "args": ["ktlintCheck"],
            "group": "build",
            "presentation": {
                "echo": true,
                "reveal": "always",
                "focus": false,
                "panel": "shared"
            },
            "problemMatcher": []
        },
        {
            "label": "Kotlin: ktlintFormat",
            "type": "shell",
            "command": "./gradlew",
            "args": ["ktlintFormat"],
            "group": "build",
            "presentation": {
                "echo": true,
                "reveal": "always",
                "focus": false,
                "panel": "shared"
            }
        },
        {
            "label": "Android: Build Debug",
            "type": "shell",
            "command": "./gradlew",
            "args": ["app:assembleDebug"],
            "group": "build",
            "presentation": {
                "echo": true,
                "reveal": "always",
                "focus": false,
                "panel": "shared"
            }
        }
    ]
}
{
    "recommendations": [
        "fwcd.kotlin",
        "esafirm.kotlin-formatter",
        "naco-siren.gradle-language",
        "richardwillis.vscode-gradle-extension-pack",
        "vscjava.vscode-gradle",
        "redhat.java",
        "vscjava.vscode-java-pack"
    ]
}
```

### 6. Alternative: Use Gradle-based Linting

Since Cursor/VS Code has limitations with Android SDK detection, use Gradle-based linting:

```bash
```kotlin
# Install these extensions in Cursor IDE:
code --install-extension mathiasfrohlich.Kotlin
code --install-extension redhat.java
code --install-extension vscjava.vscode-java-debug
code --install-extension vscjava.vscode-java-dependency
code --install-extension vscjava.vscode-java-test
code --install-extension vscjava.vscode-maven
code --install-extension vscjava.vscode-java-pack
{
    "java.configuration.updateBuildConfiguration": "automatic",
    "java.compile.nullAnalysis.mode": "automatic",
    "java.configuration.runtimes": [
        {
            "name": "JavaSE-21",
            "path": "/Library/Java/JavaVirtualMachines/zulu-21.jdk/Contents/Home"
        }
    ],
    "java.home": "/Library/Java/JavaVirtualMachines/zulu-21.jdk/Contents/Home",
    "kotlin.languageServer.enabled": true,
    "kotlin.linting.enabled": true,
    "kotlin.formatting.enabled": true,
    "gradle.nestedProjects": true,
    "gradle.buildServer.enabled": true,
    "files.associations": {
        "*.kt": "kotlin",
        "*.kts": "kotlin"
    },
    "editor.formatOnSave": true,
    "editor.codeActionsOnSave": {
        "source.organizeImports": "explicit"
    }
}
export ANDROID_HOME=/Users/vinhlekhanh/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/tools/bin
{
    "version": "2.0.0",
    "tasks": [
        {
            "label": "Kotlin: ktlintCheck",
            "type": "shell",
            "command": "./gradlew",
            "args": ["ktlintCheck"],
            "group": "build",
            "presentation": {
                "echo": true,
                "reveal": "always",
                "focus": false,
                "panel": "shared"
            },
            "problemMatcher": []
        },
        {
            "label": "Kotlin: ktlintFormat",
            "type": "shell",
            "command": "./gradlew",
            "args": ["ktlintFormat"],
            "group": "build",
            "presentation": {
                "echo": true,
                "reveal": "always",
                "focus": false,
                "panel": "shared"
            }
        },
        {
            "label": "Android: Build Debug",
            "type": "shell",
            "command": "./gradlew",
            "args": ["app:assembleDebug"],
            "group": "build",
            "presentation": {
                "echo": true,
                "reveal": "always",
                "focus": false,
                "panel": "shared"
            }
        }
    ]
}
{
    "recommendations": [
        "fwcd.kotlin",
        "esafirm.kotlin-formatter",
        "naco-siren.gradle-language",
        "richardwillis.vscode-gradle-extension-pack",
        "vscjava.vscode-gradle",
        "redhat.java",
        "vscjava.vscode-java-pack"
    ]
}
# Check code style
./gradlew ktlintCheck

# Format code
./gradlew ktlintFormat

# Run Android lint
./gradlew app:lintDebug

# Run Detekt static analysis
./gradlew detekt
```

### 7. Cursor-Specific Configuration

For Cursor IDE specifically, add to `.vscode/settings.json`:

```json
```kotlin
# Install these extensions in Cursor IDE:
code --install-extension mathiasfrohlich.Kotlin
code --install-extension redhat.java
code --install-extension vscjava.vscode-java-debug
code --install-extension vscjava.vscode-java-dependency
code --install-extension vscjava.vscode-java-test
code --install-extension vscjava.vscode-maven
code --install-extension vscjava.vscode-java-pack
{
    "java.configuration.updateBuildConfiguration": "automatic",
    "java.compile.nullAnalysis.mode": "automatic",
    "java.configuration.runtimes": [
        {
            "name": "JavaSE-21",
            "path": "/Library/Java/JavaVirtualMachines/zulu-21.jdk/Contents/Home"
        }
    ],
    "java.home": "/Library/Java/JavaVirtualMachines/zulu-21.jdk/Contents/Home",
    "kotlin.languageServer.enabled": true,
    "kotlin.linting.enabled": true,
    "kotlin.formatting.enabled": true,
    "gradle.nestedProjects": true,
    "gradle.buildServer.enabled": true,
    "files.associations": {
        "*.kt": "kotlin",
        "*.kts": "kotlin"
    },
    "editor.formatOnSave": true,
    "editor.codeActionsOnSave": {
        "source.organizeImports": "explicit"
    }
}
export ANDROID_HOME=/Users/vinhlekhanh/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/tools/bin
{
    "version": "2.0.0",
    "tasks": [
        {
            "label": "Kotlin: ktlintCheck",
            "type": "shell",
            "command": "./gradlew",
            "args": ["ktlintCheck"],
            "group": "build",
            "presentation": {
                "echo": true,
                "reveal": "always",
                "focus": false,
                "panel": "shared"
            },
            "problemMatcher": []
        },
        {
            "label": "Kotlin: ktlintFormat",
            "type": "shell",
            "command": "./gradlew",
            "args": ["ktlintFormat"],
            "group": "build",
            "presentation": {
                "echo": true,
                "reveal": "always",
                "focus": false,
                "panel": "shared"
            }
        },
        {
            "label": "Android: Build Debug",
            "type": "shell",
            "command": "./gradlew",
            "args": ["app:assembleDebug"],
            "group": "build",
            "presentation": {
                "echo": true,
                "reveal": "always",
                "focus": false,
                "panel": "shared"
            }
        }
    ]
}
{
    "recommendations": [
        "fwcd.kotlin",
        "esafirm.kotlin-formatter",
        "naco-siren.gradle-language",
        "richardwillis.vscode-gradle-extension-pack",
        "vscjava.vscode-gradle",
        "redhat.java",
        "vscjava.vscode-java-pack"
    ]
}
# Check code style
./gradlew ktlintCheck

# Format code
./gradlew ktlintFormat

# Run Android lint
./gradlew app:lintDebug

# Run Detekt static analysis
./gradlew detekt
{
    "cursor.cpp.disabledLanguages": [],
    "cursor.general.enableCodeActions": true,
    "cursor.general.enableInlineEdit": true,
    "cursor.chat.enableCodebaseContext": true
}
```

## Troubleshooting

### Issue: Unresolved Android References
**Solution**: This is expected in Cursor/VS Code. Use:
1. **Gradle builds** to verify compilation
2. **JetBrains inspections** for accurate analysis
3. **Gradle-based linting** for code quality

### Issue: Kotlin Language Server Not Working
**Solution**:
1. Restart Cursor IDE
2. Reload window (`Cmd+Shift+P` → "Developer: Reload Window")
3. Check Java runtime configuration

### Issue: Gradle Integration Issues
**Solution**:
1. Ensure Gradle wrapper is executable: `chmod +x gradlew`
2. Run `./gradlew --version` to verify Gradle works
3. Use Gradle extension commands in Command Palette

## Best Practices

1. **Use Gradle for builds**: Always use `./gradlew` commands
2. **Trust successful builds**: If Gradle builds succeed, the code is correct
3. **Use JetBrains for analysis**: For accurate linting, use IntelliJ/Android Studio
4. **Leverage Cursor AI**: Use Cursor's AI features for code completion and assistance
5. **Regular sync**: Run `./gradlew build` regularly to catch issues early

## Verification Steps

1. **Test build**: `./gradlew app:assembleDebug`
2. **Test linting**: `./gradlew ktlintCheck`
3. **Test formatting**: `./gradlew ktlintFormat`
4. **Verify extensions**: Check that Kotlin and Gradle extensions are active
5. **Test AI features**: Use Cursor's chat and inline editing features

## Conclusion

Cursor IDE can work well for Android development with proper configuration, but it has limitations compared to Android Studio. The key is to:

- Use Gradle-based tools for builds and linting
- Trust successful builds over linter warnings
- Leverage Cursor's AI features for productivity
- Use JetBrains tools for comprehensive analysis when needed



