# Developer Guide

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Hedgehog (2023.1.1) or later
- **JDK**: Version 21
- **Android SDK**: API level 34
- **Gradle**: Version 8.13+

### Initial Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd githubusers
   ```

2. **Open in Android Studio**
   - Open the project in Android Studio
   - Wait for Gradle sync to complete
   - Ensure all dependencies are resolved

3. **Verify setup**
   ```bash
   ./gradlew build
   ```

## 🏗️ Project Architecture

### Feature-Based Modular Design

The project follows a feature-based modular architecture where each feature is completely isolated:

```
githubusers/
├── app/                    # Main application
├── core-*/                 # Core modules
├── feature-*/              # Feature modules
├── navigation-*/           # Navigation system
├── plugins/                # Build plugins
└── catalog/                # Version catalog
```

### Core Principles

1. **Feature Isolation**: Each feature module is self-contained
2. **Deep Link Communication**: Cross-module communication via deep links only
3. **Type Safety**: Compile-time navigation safety
4. **Clean Architecture**: Clear separation of concerns
5. **Navigation 3 Complete**: All Navigation 3 features implemented and verified

## 🛠️ Development Workflow

### 1. Creating a New Feature

Creating a new feature module requires setting it up as a self-contained Gradle project within the main composite build. For the most detailed and authoritative steps, always refer to the **New Module Checklist** in `AGENTS.md`.

#### Step 1: Create Module Directory and Gradle Files
1.  **Create the directory** for your new module (e.g., `feature-your-feature`).
2.  **Copy Gradle files** from an existing module (like `feature-users`) into your new directory. This includes:
    *   `gradlew` and `gradlew.bat`
    *   `gradle.properties`
    *   `settings.gradle.kts`
3.  **Update the module's `settings.gradle.kts`** to set the `rootProject.name` to your new module's name.

#### Step 2: Add to Root Composite Build
In the **root** `settings.gradle.kts` file for the entire project, add your new module to the composite build:
```kotlin
includeBuild("feature-your-feature")
```

#### Step 3: Configure `build.gradle.kts`
Create a `build.gradle.kts` file in your new module and apply the necessary plugins, using `feature-users` as a template. A standard feature module applies a combination of standard plugins from the version catalog and custom convention plugins.

```kotlin
plugins {
    // Standard Android and Kotlin plugins from the version catalog
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)

    // Custom Convention Plugins for this project
    id("githubusers.quality.detekt")
    id("githubusers.quality.ktlint")
    id("githubusers.test.convention")
    id("githubusers.dependency.update")
    id("githubusers.feature.module") // Generic feature module plugin
}

dependencies {
    implementation(project(":core-common"))
    implementation(project(":core-ui"))
    implementation(project(":navigation-api"))
    // ... other dependencies from libs
}
```

### 2. Adding Navigation

Follow the `FEATURE_DESTINATION_TEMPLATE.md` to define destinations, deep link handlers, and a feature API for your new module. This ensures your feature is correctly integrated into the app's navigation graph.

### 3. Implementing UI and Logic

With the module structure in place, you can implement your UI with Jetpack Compose and your business logic using ViewModels, following the project's MVI architecture pattern.

## 🧪 Testing

All tests should be executed via the **Gradle MCP**, not by calling `./gradlew` directly. This ensures that tests run with the correct configuration and environment.

### Running Tests with Gradle MCP

*   **Run all tests for a specific module:**
    *   `:feature-your-feature:test`
    *   `:feature-your-feature:connectedAndroidTest` (for UI tests)

*   **Run all tests in the project:**
    *   `test` (for all unit tests)
    *   `connectedAndroidTest` (for all UI tests)

*   **Generate a code coverage report:**
    *   `jacocoTestReport`

## 🔍 Code Quality

Static analysis is a critical part of the development workflow and is enforced through convention plugins. Run these tasks via the **Gradle MCP**.

*   **Run Detekt:**
    *   `detekt` (for the whole project)
    *   `:feature-your-feature:detekt` (for a specific module)

*   **Run KtLint:**
    *   `ktlintCheck` (to check formatting)
    *   `ktlintFormat` (to automatically fix formatting issues)

### Code Standards

Follow these coding standards:

1. **Naming Conventions**
   - Use descriptive names
   - Follow Kotlin naming conventions
   - Use camelCase for variables and functions
   - Use PascalCase for classes and interfaces

2. **Function Design**
   - Keep functions small and focused
   - Use single responsibility principle
   - Prefer pure functions when possible
   - Use meaningful parameter names

3. **Class Design**
   - Follow SOLID principles
   - Use composition over inheritance
   - Keep classes focused and cohesive
   - Use interfaces for abstractions

### Documentation

#### IDE Inspections via MCP

When you need IntelliJ/IDEA inspections, follow JetBrains’ MCP flow: invoke `open_file_in_editor` for the file, then `get_file_problems` to retrieve the IDE inspections. Repeat after fixes to confirm the file is clean. Reference: [JetBrains MCP Supported Tools](https://www.jetbrains.com/help/idea/mcp-server.html#supported-tools).

Document your code with KDoc:

```kotlin
/**
 * Handles user authentication and session management.
 * 
 * @param username The user's username
 * @param password The user's password
 * @return Result containing the authentication token or error
 */
suspend fun authenticate(username: String, password: String): Result<String>
```

## 🚀 Performance

### Build Performance

Optimize build performance:

1. **Use Convention Plugins**: Centralize build logic
2. **Minimize Dependencies**: Only include necessary dependencies
3. **Use Implementation**: Prefer `implementation` over `api`
4. **Parallel Builds**: Enable parallel execution

### Runtime Performance

Optimize app performance:

1. **Lazy Loading**: Use `LazyColumn` for large lists
2. **State Management**: Minimize recomposition
3. **Memory Management**: Proper lifecycle management
4. **Image Loading**: Use efficient image loading libraries

## 🐛 Debugging

### Logging

Use structured logging:

```kotlin
Timber.tag("YourFeatureViewModel").d("Handling event: $event")
    // Handle event
```

### Debugging Tools

1. **Android Studio Debugger**: Set breakpoints and inspect variables
2. **Logcat**: View application logs
3. **Layout Inspector**: Inspect UI hierarchy
4. **Network Inspector**: Monitor network requests

## 📚 Resources

### Documentation
- [Android Developer Guide](https://developer.android.com/guide)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Navigation 3](https://developer.android.com/guide/navigation/navigation3)

## NavigationEvent 1.0.0-alpha09 Alignment

- Bumped `nav3Core` / `navigation3` bundles to `1.0.0-alpha09` / `3.0.0-alpha09` to track the September 24, 2025 release.
- **State handling**: `NavigationEventTransitionState.Idle` is now a singleton object—do not instantiate `Idle()` manually when reacting to predictive back progress.
- **Dispatcher updates**: observe `NavigationEventDispatcher.transitionState` when features need to react to navigation transitions (e.g., pausing heavy work during predictive back). Surface this flow through feature-owned composables rather than ad-hoc mutable state.
- **History access**: leverage `NavigationEventHistoryState` for scrolling or selection history instead of home-grown stacks; it now exposes dispatcher snapshots.
- `NavigationEventInfo` remains an abstract class; keep custom implementations (like `UserNavigationEventInfo`) confined to the owning feature.
- Regression checklist: rerun `./gradlew dependencyUpdatesAll --no-parallel` and manually verify deep links, predictive back gestures, and dialog destinations after bumping.
- [Hilt](https://dagger.dev/hilt/)

### Project-Specific
- [Navigation Architecture](NAVIGATION_ARCHITECTURE.md)
- [Feature Development Guide](FEATURE_BASED_DEVELOPMENT_GUIDE.md)
- [Build System Guide](BUILD_SYSTEM.md)

### Best Practices
- [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [Material Design](https://material.io/design)
- [Android Architecture Guide](https://developer.android.com/topic/architecture)

## 🤝 Contributing

### Pull Request Process

1. **Create Feature Branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Implement Changes**
   - Follow project patterns
   - Add tests
   - Update documentation

3. **Quality Checks**
   ```bash
   ./gradlew build
   ./gradlew test
   ./gradlew detekt
   ./gradlew ktlintCheck
   ```

4. **Submit Pull Request**
   - Clear description
   - Link to issues
   - Include screenshots if UI changes

### Code Review

Review checklist:
- [ ] Code follows project patterns
- [ ] Tests are comprehensive
- [ ] Documentation is updated
- [ ] Build passes
- [ ] No breaking changes
- [ ] Performance impact considered

---

This guide provides the foundation for effective development on the GitHub Users project. Follow these patterns and practices to maintain code quality and consistency across the project.
