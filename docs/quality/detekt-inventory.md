# Detekt Configuration Inventory

## Overview

This document provides an inventory of detekt configuration across the GitHub Users project.

## Configuration Files

### Root Configuration

- **File**: `detekt.yml` (root directory)
- **Purpose**: Main detekt configuration for all modules
- **Features**: Rule sets, thresholds, and ktlint integration

### Module Baselines

- **Core modules**: `config/detekt/baseline.xml` in each core module
- **Feature modules**: `config/detekt/baseline.xml` in each feature module
- **Purpose**: Suppress false positives and known issues per module

## Rule Sets

### Active Rule Sets

- **Naming**: Class, function, and variable naming conventions
- **Complexity**: Cyclomatic complexity and method length limits
- **Style**: Code style and formatting rules
- **Empty blocks**: Empty function and class body detection
- **Exceptions**: Exception handling best practices
- **Ktlint**: Formatting rules through ktlint integration

### Compose-Specific Rules

- **FunctionNaming**: Allows PascalCase for `@Composable` functions
- **TopLevelPropertyNaming**: PascalCase for constants
- **TooManyFunctions**: Increased threshold (25) for Compose modules
- **LongParameterList**: Optimized for Compose composables
- **UnusedPrivateMember**: Ignores `@Preview` functions

## Integration

### Build System

- **Convention plugin**: `DetektConventionPlugin` applied to all modules
- **Dependencies**: Ktlint ruleset wrapper for formatting
- **Configuration**: Shared configuration from root `detekt.yml`

### Ktlint Integration

- **Ruleset**: `detekt-rules-ktlint-wrapper` dependency
- **Formatting**: All formatting handled by ktlint
- **Alignment**: Perfect alignment between detekt and ktlint

## Module Coverage

### Core Modules

- **core-common**: Basic detekt configuration
- **core-data**: Database and network specific rules
- **core-mvi**: State management specific rules
- **core-ui**: UI component specific rules

### Feature Modules

- **feature-users**: User management specific rules
- **feature-users-list**: List component specific rules
- **feature-users-detail**: Detail view specific rules
- **feature-search**: Search functionality specific rules

## Configuration Management

### Centralized Configuration

- **Single source**: Root `detekt.yml` for all modules
- **Consistent rules**: Same rule sets across all modules
- **Shared thresholds**: Common complexity and naming thresholds

### Baseline Management

- **Module-specific**: Each module has its own baseline
- **False positives**: Suppress legitimate rule violations
- **Regular review**: Clean up baselines periodically

## Performance Considerations

### Rule Optimization

- **Exclusions**: Test directories excluded from analysis
- **Thresholds**: Appropriate limits for different module types
- **Ktlint**: Single formatting engine for better performance

### Build Integration

- **Convention plugin**: Automatic application to all modules
- **Baseline caching**: Module-specific issue suppression
- **Parallel execution**: Independent analysis per module
