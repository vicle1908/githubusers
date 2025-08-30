# Detekt Usage Guide

## Overview

This document provides guidance on using detekt for static code analysis in the GitHub Users project.

## Configuration

- **Version**: 1.23.8
- **Configuration**: Root `detekt.yml` file
- **Baseline**: Module-specific baseline files in `config/detekt/`
- **Formatting**: Ktlint ruleset integration for consistent formatting

## Usage

### Running Detekt

```bash
# Run detekt on all modules
./gradlew detekt

# Run detekt on specific module
./gradlew :app:detekt

# Generate baseline for specific module
./gradlew :app:detektBaseline
```

### Configuration Structure

- **Root config**: `detekt.yml` - Main configuration and rule sets
- **Module baselines**: `config/detekt/baseline.xml` - Suppressed issues per module
- **Convention plugin**: Automatically applies detekt to all modules

## Rule Sets

### Active Rule Sets

- **Naming**: Class, function, and variable naming conventions
- **Complexity**: Cyclomatic complexity and method length limits
- **Style**: Code style and formatting rules
- **Ktlint**: Formatting rules through ktlint integration

### Compose-Specific Rules

- **FunctionNaming**: Allows PascalCase for `@Composable` functions
- **TopLevelPropertyNaming**: PascalCase for constants
- **TooManyFunctions**: Increased threshold for Compose modules
- **LongParameterList**: Optimized for Compose composables

## Integration

### Ktlint Integration

- **Formatting**: Handled by ktlint ruleset wrapper
- **Alignment**: Perfect alignment between detekt and ktlint
- **Performance**: Single formatting engine for better performance

### Build Integration

- **Convention plugin**: Automatically applied to all modules
- **Baseline management**: Module-specific issue suppression
- **CI/CD**: Integrated into build verification process

## Best Practices

### Rule Configuration

- Use `ignoreAnnotated` for Compose-specific annotations
- Configure thresholds appropriate for module type
- Suppress false positives through baseline files

### Baseline Management

- Generate baselines for new modules
- Review and clean up baselines regularly
- Use baselines only for legitimate false positives

### Performance Optimization

- Use ktlint ruleset for formatting
- Configure appropriate thresholds
- Exclude test directories where appropriate

