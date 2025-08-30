# Build Conventions

## Overview

This document outlines the build conventions used in the GitHub Users project.

## Gradle Configuration

### Version Catalog

- **Location**: `catalog/gradle/libs.versions.toml`
- **Purpose**: Centralized dependency management
- **Usage**: All modules reference versions from catalog

### Convention Plugins

- **Location**: `plugins/src/main/kotlin/`
- **Purpose**: Consistent build configuration across modules
- **Application**: Applied automatically to all modules

## Module Structure

### Core Modules

- **core-common**: Shared utilities and common code
- **core-data**: Data layer and network components
- **core-mvi**: State management and architecture
- **core-ui**: UI components and theming

### Feature Modules

- **feature-users**: User management functionality
- **feature-users-list**: User list display
- **feature-users-detail**: User detail views
- **feature-search**: Search functionality

### Navigation Modules

- **navigation-api**: Navigation interface definitions
- **navigation-impl**: Navigation implementation
- **navigation-annotations**: Navigation annotations
- **navigation-ksp**: Navigation code generation

## Build Variants

### Product Flavors

- **dev**: Development environment
- **prod**: Production environment

### Build Types

- **debug**: Debug builds with development tools
- **release**: Release builds for distribution
- **staging**: Staging environment builds

## Quality Tools

### Static Analysis

- **Detekt**: Code quality analysis with ktlint integration
- **Ktlint**: Code formatting and style enforcement
- **Baseline**: Module-specific issue suppression

### Testing

- **Unit tests**: Module-specific test suites
- **Integration tests**: Cross-module functionality tests
- **UI tests**: Compose UI testing

## Dependencies

### Core Dependencies

- **Android**: Latest stable versions
- **Kotlin**: Latest stable version
- **Compose**: Latest stable version
- **Hilt**: Dependency injection

### Feature Dependencies

- **Ktor**: Network client
- **Room**: Database persistence
- **Paging**: Pagination support
- **Navigation**: Compose navigation

## Build Performance

### Optimization

- **Parallel execution**: Independent module builds
- **Incremental builds**: Only changed modules rebuilt
- **Caching**: Gradle build cache enabled
- **Configuration cache**: Plugin configuration cached

### Monitoring

- **Build scans**: Performance analysis
- **Timing reports**: Build time breakdown
- **Memory usage**: JVM memory optimization
