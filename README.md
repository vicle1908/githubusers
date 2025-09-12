# GitHub Users Android App

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue.svg)](https://kotlinlang.org)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg)](https://android-arsenal.com/api?level=24)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A modern Android application demonstrating best practices in Android development with a feature-based modular architecture, custom Navigation 3 implementation, and clean architecture principles.

## 🚀 Features

- **User Discovery**: Search and browse GitHub users with real-time search
- **User Profiles**: View detailed user information, repositories, and activity
- **Offline Support**: Local caching with Room database for offline access
- **Modern UI**: Material Design 3 with Jetpack Compose
- **Type-Safe Navigation**: Navigation 3 with deep linking support
- **Modular Architecture**: Feature-based modules with complete isolation

## 🏗️ Architecture

### Clean Architecture
- **Presentation Layer**: Jetpack Compose UI, ViewModels, and user interactions
- **Domain Layer**: Business logic, use cases, and entities
- **Data Layer**: Repositories, data sources, and API integration

### Feature-Based Modular Design
```
githubusers/
├── app/                    # Main application entry point
├── core-*/                 # Core functionality modules
│   ├── core-common/        # Common utilities and extensions
│   ├── core-mvi/           # MVI pattern implementation
│   ├── core-networking/    # Network layer
│   ├── core-storage/       # Local storage and caching
│   └── core-ui/            # Shared UI components
├── feature-*/              # Feature-specific modules
│   ├── feature-users/      # User management and profiles
│   ├── feature-search/     # Search functionality
│   └── feature-settings/   # App settings and preferences
├── navigation-*/           # Navigation system
│   ├── navigation-api/     # Navigation contracts
│   └── navigation-impl/    # Navigation implementation
├── plugins/                # Build system convention plugins
└── catalog/                # Dependency version management
```

### Navigation 3 Architecture
- **Type-Safe Navigation**: Compile-time navigation safety with `AppDestination` types
- **Deep Link Support**: Multi-module deep link architecture
- **Feature Isolation**: Each feature owns its navigation destinations
- **Cross-Module Communication**: Via deep links only

## 🛠️ Technology Stack

### Core Technologies
- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern declarative UI toolkit
- **Navigation 3**: Next-generation type-safe navigation
- **Material Design 3**: Modern design system

### Architecture Components
- **Hilt**: Dependency injection framework
- **Room**: Local database and caching
- **Ktor**: HTTP client for API communication
- **Coroutines & Flow**: Asynchronous programming
- **Kotlin Serialization**: Type-safe data serialization

### Quality & Testing
- **Detekt**: Static code analysis
- **KtLint**: Code formatting and style
- **JUnit**: Unit testing framework
- **MockK**: Mocking framework for Kotlin

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 21
- Android SDK 34
- Gradle 8.13+

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd githubusers
   ```

2. **Open in Android Studio**
   - Open the project in Android Studio
   - Wait for Gradle sync to complete

3. **Build and Run**
   ```bash
   # Build the project
   ./gradlew build
   
   # Run on device/emulator
   ./gradlew :app:installDebug
   ```

### Build Commands

```bash
# Clean build (with optimizations)
./gradlew clean --configuration-cache --build-cache

# Build debug variant (optimized)
./gradlew :app:assembleDebug --configuration-cache --build-cache --parallel

# Run all tests (optimized)
./gradlew test --configuration-cache --build-cache --parallel

# Run specific module tests
./gradlew :feature-users:test --configuration-cache --build-cache

# Build all modules (composite build)
./gradlew buildAll --configuration-cache --build-cache --parallel

# Quality checks
./gradlew detekt ktlintCheck --configuration-cache --build-cache --parallel
```

## 📱 App Features

### User Management
- **User List**: Browse GitHub users with pagination
- **User Search**: Real-time search with query suggestions
- **User Details**: Comprehensive user profile information
- **Repository View**: Browse user repositories and contributions

### Settings & Preferences
- **Theme Selection**: Light, dark, and system theme support
- **App Preferences**: Customizable app behavior
- **Data Management**: Clear cache and reset preferences

### Offline Experience
- **Local Caching**: Room database for offline data access
- **Smart Sync**: Automatic data synchronization when online
- **Offline Indicators**: Clear UI feedback for offline state

## 🧪 Testing

### Test Structure
```
src/test/           # Unit tests
src/androidTest/    # Integration and UI tests
```

### Running Tests
```bash
# Run all unit tests
./gradlew test

# Run specific module tests
./gradlew :feature-users:test

# Run UI tests
./gradlew connectedAndroidTest

# Generate test coverage
./gradlew jacocoTestReport
```

## 🔧 Development

### Code Quality
- **Detekt**: Static analysis for code quality
- **KtLint**: Code formatting and style enforcement
- **Pre-commit Hooks**: Automated quality checks

### Build System
- **Convention Plugins**: Standardized build configuration
- **Version Catalog**: Centralized dependency management
- **Composite Builds**: Optimized build performance
- **Configuration Cache**: Gradle 9.0+ with configuration caching enabled
- **Build Cache**: Local build cache with 30-day retention
- **KSP Migration**: Migrated from KAPT to KSP for faster annotation processing

### Development Workflow
1. Create feature branch from `main`
2. Implement changes following project patterns
3. Add tests and documentation
4. Ensure all quality checks pass
5. Submit pull request with clear description

## 📚 Documentation

- **[Architecture Guide](docs/PROJECT_OVERVIEW.md)**: Complete project architecture overview
- **[Navigation Guide](docs/NAVIGATION_ARCHITECTURE.md)**: Navigation 3 implementation details
- **[Feature Development](docs/FEATURE_BASED_DEVELOPMENT_GUIDE.md)**: Feature-based development patterns
- **[Build System](docs/BUILD_SYSTEM.md)**: Build system architecture and conventions

## 🤝 Contributing

### Code Standards
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Write comprehensive documentation
- Include unit tests for new functionality

### Pull Request Process
1. Create feature branch from `main`
2. Implement changes following project patterns
3. Add tests and documentation
4. Ensure build passes and quality checks
5. Submit pull request with clear description

### Review Guidelines
- Code follows established patterns
- Tests are comprehensive and passing
- Documentation is updated
- Build system changes are minimal

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [GitHub API](https://docs.github.com/en/rest) for user data
- [Android Jetpack](https://developer.android.com/jetpack) for modern Android development
- [Material Design](https://material.io/) for design system
- [Kotlin](https://kotlinlang.org/) for the programming language

---

**Built with ❤️ using modern Android development practices**