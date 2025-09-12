# App Module

[![Module](https://img.shields.io/badge/Module-App-orange.svg)](https://developer.android.com)
[![Hilt](https://img.shields.io/badge/DI-Hilt-green.svg)](https://dagger.dev/hilt/)

## 📱 Overview

The `app` module is the main application entry point that orchestrates all feature modules and provides the application-level configuration.

## 🎯 Responsibilities

- **Application Initialization**: `UserApplication` class with Hilt setup
- **Main Activity**: Single activity architecture with `MainActivity`
- **Navigation Orchestration**: Coordinates Navigation3 system
- **Deep Link Handling**: Routes deep links to appropriate features
- **Theme Configuration**: Provides app-wide Material 3 theming

## 🏗️ Architecture

### Key Components

| Component | Description |
|-----------|-------------|
| `UserApplication` | Application class with `@HiltAndroidApp` annotation |
| `MainActivity` | Single activity hosting all navigation |
| `DeepLinkEntryActivity` | Handles external deep links |
| `ModuleNavigator` | Cross-module navigation coordinator |
| `GithubUsersTheme` | App-wide Material 3 theme |

### Navigation Setup

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var backStack: Navigation3BackStack
    @Inject lateinit var registry: Navigation3FeatureRegistry
    @Inject lateinit var dispatcher: DeepLinkDispatcher
}
```

## 📦 Dependencies

### Direct Dependencies
- `:core-common` - Common utilities
- `:core-ui` - UI components and theming
- `:navigation-api` - Navigation contracts
- `:navigation-impl` - Navigation implementation
- `:feature-users` - User features
- `:feature-search` - Search functionality
- `:feature-settings` - Settings features

### Key Libraries
- Jetpack Compose
- Hilt Android
- Navigation3 (custom implementation)
- Material 3

## 🔗 Deep Links

The app handles the following deep link patterns:

| Pattern | Description |
|---------|-------------|
| `app://users/list` | User list screen |
| `app://users/user/{username}` | User detail screen |
| `app://search?q={query}` | Search with query |
| `app://settings` | Settings screen |

## 🧪 Testing

### Unit Tests
Located in `src/test/java/`:
- Navigation routing tests
- Deep link parsing tests
- Theme configuration tests

### UI Tests
Located in `src/androidTest/java/`:
- App launch tests
- Navigation flow tests
- Deep link handling tests

### Run Tests
```bash
# Unit tests
./gradlew :app:test

# UI tests
./gradlew :app:connectedAndroidTest
```

## 📋 Manifest Configuration

Key manifest entries:
- **Main Activity**: Exported with launcher intent
- **Deep Link Activity**: Handles external navigation
- **Permissions**: Internet access for API calls
- **Theme**: Material 3 with edge-to-edge support

## 🚀 Build Configuration

### Build Types
- **debug**: Development build with debugging enabled
- **release**: Production build with ProGuard/R8

### Product Flavors
- **dev**: Development environment
- **prod**: Production environment

## 📚 Related Documentation

- [Project Overview](../docs/PROJECT_OVERVIEW.md)
- [Navigation Architecture](../docs/NAVIGATION_ARCHITECTURE.md)
- [Developer Guide](../docs/DEVELOPER_GUIDE.md)
