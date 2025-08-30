# GitHub Users Project - Build System Architecture

## Overview

This document describes the modern, convention-based build system architecture for the GitHub Users Android project. The system uses Gradle convention plugins to centralize build logic, eliminate duplication, and ensure consistency across all modules.

## Architecture Principles

### 1. **Convention Over Configuration**
- All common build logic is centralized in convention plugins
- Individual modules use minimal, declarative configuration
- Build behavior is standardized across similar module types

### 2. **Version Centralization**
- All versions are managed through the version catalog (`catalog/gradle/libs.versions.toml`)
- No hardcoded versions in individual module build files
- Single source of truth for dependency versions

### 3. **Dependency Optimization**
- Use `implementation` dependencies by default
- Use `api` dependencies only when modules need to expose dependencies to consumers
- Platform BOMs for version alignment

## Project Structure

```
githubusers/
├── app/                           # Main application module
├── core-*/                        # Core functionality modules
├── feature-*/                     # Feature modules
├── navigation-*/                  # Navigation modules
├── plugins/                       # Convention plugins
├── catalog/                       # Version catalog
├── internal-platform/             # Internal BOM module
└── test-module/                  # Testing utilities
```

## Convention Plugins

### Base Plugins

#### `githubusers.base.module`
- Applied to all modules
- Configures common project properties
- Sets up quality tools (Detekt, KtLint)

#### `githubusers.common.version`
- Centralizes common version properties
- Sets `group`, `version`, `compileSdk`, `minSdk`, `targetSdk`
- Configures JVM target and Kotlin compiler extension version

### Android Module Plugins

#### `githubusers.android.library`
- Configures Android library modules
- Sets up namespace generation (prevents BuildConfig duplication)
- Configures common Android settings

#### `githubusers.android.application`
- Configures Android application modules
- Handles complex application configuration (flavors, build types, NDK)
- Manages application-specific settings via extensions

#### `githubusers.android.library.compose`
- Adds Compose-specific configuration
- Configures Compose compiler and UI dependencies

#### `githubusers.android.hilt`
- Configures Hilt dependency injection
- Sets up KSP for annotation processing

#### `githubusers.android.room`
- Configures Room database components
- Sets up KSP for Room annotation processing

### Publishing Plugins

#### `githubusers.android.publishing`
- Configures Maven publishing for all modules
- Sets up publication artifacts and POM information

#### `githubusers.android.library.publishing`
- Specialized publishing for Android library modules
- Handles release component publication

### Quality Plugins

#### `githubusers.quality.detekt`
- Configures Detekt static code analysis
- Applies project-wide Detekt rules

#### `githubusers.quality.ktlint`
- Configures KtLint code formatting
- Applies project-wide KtLint rules

### Specialized Module Plugins

#### `githubusers.feature.module`
- Configures feature modules
- Sets up feature-specific dependencies and configuration

#### `githubusers.core.module`
- Configures core modules
- Sets up core-specific dependencies and configuration

#### `githubusers.navigation.module`
- Configures navigation modules
- Sets up navigation-specific dependencies and configuration

#### `githubusers.jvm.library`
- Configures JVM-only library modules
- Sets up Kotlin JVM compilation

## Version Catalog

### Structure
The version catalog (`catalog/gradle/libs.versions.toml`) is organized into three main sections:

#### `[versions]`
- Library versions (e.g., `kotlin = "2.2.10"`)
- SDK versions (e.g., `sdk-compile = "36"`)
- Plugin versions (e.g., `android-gradle-plugin = "8.12.2"`)

#### `[plugins]`
- Gradle plugin declarations
- Convention plugin references
- External plugin aliases

#### `[libraries]`
- Library dependencies
- Platform BOMs
- Testing dependencies

### Key Versions
```toml
[versions]
kotlin = "2.2.10"
android-gradle-plugin = "8.12.2"
compose-compiler = "1.6.0"
hilt = "2.57.1"
room = "2.7.2"
```

## Module Configuration Examples

### Application Module
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("githubusers.android.application")
    // ... other plugins
}

// Configure via convention plugin extensions
extensions.configure<ApplicationConfigExtension>("appConfig") {
    applicationId = libs.versions.application.id.get()
    versionCode = libs.versions.app.version.code.get().toInt()
    versionName = libs.versions.app.version.name.get()
}
```

### Feature Module
```kotlin
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("githubusers.android.library")
    id("githubusers.feature.module")
    // ... other plugins
}

dependencies {
    // Use implementation for internal dependencies
    implementation(libs.local.core.domain)
    implementation(libs.local.navigation.api)
    
    // Use api only when exposing to consumers
    api(libs.local.feature.users)
}
```

### JVM Library Module
```kotlin
plugins {
    alias(libs.plugins.kotlin.jvm)
    id("githubusers.jvm.library")
    id("githubusers.common.version")
}

dependencies {
    // Use api for core functionality that should be exposed
    api(libs.kotlinx.coroutines.core)
    api(libs.androidx.paging.common)
}
```

## Dependency Management

### Dependency Types

#### `implementation`
- **Use by default** for most dependencies
- Dependencies are not exposed to consumers
- Reduces transitive dependency exposure

#### `api`
- **Use sparingly** only when necessary
- Dependencies are exposed to consumers
- Required for core modules and aggregator modules

#### `compileOnly`
- Used for annotation processors and build-time dependencies
- Not included in runtime classpath

#### `ksp`
- Used for KSP annotation processors
- Generates code at build time

### Platform BOMs
```kotlin
// Use platform BOMs for version alignment
implementation(platform(libs.internal.platform))
implementation(platform(libs.androidx.compose.bom))
implementation(platform(libs.ktor.bom))
```

## Build Configuration

### Android Configuration
- **Namespace**: Automatically generated to prevent BuildConfig duplication
- **SDK Versions**: Centralized in convention plugins
- **Build Types**: Standardized across all modules
- **Product Flavors**: Configured via convention plugin extensions

### Kotlin Configuration
- **JVM Target**: Centralized in convention plugins
- **Compiler Options**: Standardized across all modules
- **Serialization**: Applied when needed via plugin

### Quality Tools
- **Detekt**: Static code analysis with project-wide rules
- **KtLint**: Code formatting with project-wide rules
- **Configuration**: Centralized in convention plugins

## Migration Guide

### From Legacy Build Files

#### 1. Remove Custom Configuration
```kotlin
// ❌ Remove these blocks
android { ... }
kotlin { ... }
publishing { ... }
```

#### 2. Apply Convention Plugins
```kotlin
// ✅ Apply appropriate convention plugins
id("githubusers.android.library")
id("githubusers.feature.module")
```

#### 3. Use Version Catalog
```kotlin
// ❌ Don't hardcode versions
implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.10")

// ✅ Use version catalog
implementation(libs.kotlin.stdlib)
```

#### 4. Optimize Dependencies
```kotlin
// ❌ Don't expose unnecessary dependencies
api(libs.androidx.core.ktx)

// ✅ Use implementation when possible
implementation(libs.androidx.core.ktx)
```

## Best Practices

### 1. **Plugin Application Order**
```kotlin
plugins {
    // 1. Apply base Android/Kotlin plugins first
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    
    // 2. Apply convention plugins
    id("githubusers.android.library")
    id("githubusers.feature.module")
    
    // 3. Apply quality plugins last
    id("githubusers.quality.detekt")
}
```

### 2. **Dependency Declaration**
```kotlin
dependencies {
    // Group dependencies by type
    // Core dependencies
    implementation(libs.local.core.domain)
    
    // UI dependencies
    implementation(libs.androidx.compose.ui)
    
    // Testing dependencies
    testImplementation(libs.junit)
}
```

### 3. **Version Management**
- Always use version catalog for versions
- Never hardcode versions in build files
- Use platform BOMs for version alignment
- Keep versions in sync across related libraries

### 4. **Module Organization**
- Group related functionality in modules
- Use clear, descriptive module names
- Minimize cross-module dependencies
- Use aggregator modules for feature groups

## Troubleshooting

### Common Issues

#### 1. **Plugin Not Found**
```bash
# Error: Plugin with id 'githubusers.android.library' not found
# Solution: Ensure plugins module is built and included
./gradlew :plugins:build
```

#### 2. **Namespace Conflicts**
```bash
# Error: Type com.example.githubusers.BuildConfig is defined multiple times
# Solution: Convention plugins automatically generate unique namespaces
```

#### 3. **Version Conflicts**
```bash
# Error: Multiple versions of the same library
# Solution: Use platform BOMs and version catalog
```

#### 4. **Build Cache Issues**
```bash
# Error: Configuration cache problems
# Solution: Clear build cache
./gradlew clean
```

### Debug Commands
```bash
# List all available tasks
./gradlew tasks

# Build specific module
./gradlew :feature-users:assembleDebug

# Clean build
./gradlew clean

# Build with debug info
./gradlew build --info
```

## Future Enhancements

### Planned Improvements
1. **Enhanced Convention Plugin System**
   - More specialized module types
   - Automatic dependency management
   - Build variant optimization

2. **Advanced Version Management**
   - Automatic version updates
   - Dependency conflict resolution
   - Security vulnerability scanning

3. **Build Performance**
   - Parallel build optimization
   - Incremental compilation
   - Build cache optimization

### Contributing
When adding new modules or dependencies:
1. Follow the established patterns
2. Use convention plugins when possible
3. Add versions to the version catalog
4. Update this documentation

## Conclusion

The convention-based build system provides:
- **Consistency**: All modules follow the same patterns
- **Maintainability**: Centralized configuration reduces duplication
- **Scalability**: Easy to add new modules and dependencies
- **Quality**: Standardized quality tools across all modules

This architecture ensures that the project can grow while maintaining build consistency and reducing maintenance overhead.
