# Detekt 1.23.8 Migration Guide

## Overview

This document outlines the migration from previous detekt versions to 1.23.8, including configuration changes and new features.

## Key Changes

### Configuration Updates

- **Formatting**: Now handled by ktlint ruleset integration
- **Ktlint**: Added ktlint ruleset wrapper for perfect alignment
- **Compose**: Enhanced Compose-specific rule configurations

### Deprecated Features

- **detekt-formatting**: Replaced with ktlint ruleset
- **Formatting rules**: All detekt formatting rules now disabled
- **Legacy config**: Some deprecated keys removed

## Migration Steps

### Update Dependencies

```toml
```text
# OLD: detekt-formatting
# detekt-formatting = { module = "io.gitlab.arturbosch.detekt:detekt-formatting", version.ref = "detekt-formatting" }

# NEW: ktlint ruleset
detekt-ktlint-rules = { module = "dev.detekt:detekt-rules-ktlint-wrapper", version.ref = "detekt" }
```

### Configuration Changes

```yaml
```text
# OLD: detekt-formatting
# detekt-formatting = { module = "io.gitlab.arturbosch.detekt:detekt-formatting", version.ref = "detekt-formatting" }

# NEW: ktlint ruleset
detekt-ktlint-rules = { module = "dev.detekt:detekt-rules-ktlint-wrapper", version.ref = "detekt" }
# OLD: Formatting rules enabled
formatting:
  active: true
  # ... many formatting rules

# NEW: Formatting handled by ktlint
formatting:
  active: false

# NEW: Ktlint integration
ktlint:
  active: true
  code_style: 'android_studio'
  autoCorrect: true
```

### Plugin Updates

```kotlin
```gradle
# OLD: detekt-formatting
# detekt-formatting = { module = "io.gitlab.arturbosch.detekt:detekt-formatting", version.ref = "detekt-formatting" }

# NEW: ktlint ruleset
detekt-ktlint-rules = { module = "dev.detekt:detekt-rules-ktlint-wrapper", version.ref = "detekt" }
# OLD: Formatting rules enabled
formatting:
  active: true
  # ... many formatting rules

# NEW: Formatting handled by ktlint
formatting:
  active: false

# NEW: Ktlint integration
ktlint:
  active: true
  code_style: 'android_studio'
  autoCorrect: true
// OLD: detekt-formatting
dependencies {
    add("detektPlugins", libs.findLibrary("detekt-formatting").get())
}

// NEW: ktlint ruleset
dependencies {
    add("detektPlugins", libs.findLibrary("detekt-ktlint-rules").get())
}
```

## Benefits

### Improved Performance

- Single formatting engine reduces build time
- Eliminates duplicate rule processing
- Better integration with existing ktlint setup

### Enhanced Compose Support

- Optimized thresholds for Compose modules
- Better handling of Compose-specific patterns
- Improved annotation support

### Perfect Alignment

- No more conflicts between detekt and ktlint
- Consistent formatting across the project
- Android Studio code style enforcement

## Breaking Changes

### Formatting Rules

- All detekt formatting rules are disabled
- Ktlint handles all formatting through ruleset wrapper
- Existing formatting configurations are ignored

### Dependencies

- `detekt-formatting` dependency removed
- `detekt-ktlint-rules` dependency added
- Version catalog updated accordingly

## Verification

### Check Configuration

```bash
```gradle
# OLD: detekt-formatting
# detekt-formatting = { module = "io.gitlab.arturbosch.detekt:detekt-formatting", version.ref = "detekt-formatting" }

# NEW: ktlint ruleset
detekt-ktlint-rules = { module = "dev.detekt:detekt-rules-ktlint-wrapper", version.ref = "detekt" }
# OLD: Formatting rules enabled
formatting:
  active: true
  # ... many formatting rules

# NEW: Formatting handled by ktlint
formatting:
  active: false

# NEW: Ktlint integration
ktlint:
  active: true
  code_style: 'android_studio'
  autoCorrect: true
// OLD: detekt-formatting
dependencies {
    add("detektPlugins", libs.findLibrary("detekt-formatting").get())
}

// NEW: ktlint ruleset
dependencies {
    add("detektPlugins", libs.findLibrary("detekt-ktlint-rules").get())
}
# Verify detekt configuration
./gradlew detekt --console=plain

# Check ktlint integration
./gradlew ktlintCheck
```

### Review Baselines

- Existing baselines may need updates
- New ktlint rules may introduce new issues
- Review and regenerate baselines as needed

## Troubleshooting

### Common Issues

- **Missing ktlint ruleset**: Ensure dependency is added to version catalog
- **Formatting conflicts**: Verify detekt formatting rules are disabled
- **Baseline issues**: Regenerate baselines for updated rule sets

### Support

- Check [Detekt Ktlint Ruleset Documentation](https://detekt.dev/docs/next/rules/ktlint/)
- Review [Ktlint Official Rules](https://pinterest.github.io/ktlint/rules/standard/)
- Consult project documentation for specific configurations

