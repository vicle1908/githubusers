# KSP Deep Link Ownership

## Overview

The Navigation3 framework uses KSP (Kotlin Symbol Processing) to generate deep link ownership providers at compile time. This allows features to declare their deep link patterns without manual registry maintenance.

## How It Works

### 1. Feature Declaration

Annotate your deep link owning class with `@OwnsDeepLinks`:

```kotlin
```kotlin

```kotlin
@OwnsDeepLinks(moduleId = "users")
class UsersDeepLinkOwner : DeepLinkOwner {
    @DeepLinkSpec(patterns = [
        "app://users/list",
        "app://users/{userId}",
        "https://example.com/users/{userId}"
    ])
    fun ownsPattern() {}
}
```

### 2. Code Generation

The `navigation-ksp` processor generates:

- Module-specific provider classes implementing `DeepLinkOwnersProvider` (e.g., `GeneratedDeepLinkOwners_Users_abc123`) in your module’s generated sources
- ServiceLoader configuration in `META-INF/services/` registering each provider
- No central registry class; all ownership is discovered at runtime via ServiceLoader

### 3. Multi-Module Integration

#### Generated Provider Structure


```kotlin
```kotlin

```kotlin
@OwnsDeepLinks(moduleId = "users")
class UsersDeepLinkOwner : DeepLinkOwner {
    @DeepLinkSpec(patterns = [
        "app://users/list",
        "app://users/{userId}",
        "https://example.com/users/{userId}"
    ])
    fun ownsPattern() {}
}
@OwnsDeepLinks(moduleId = "users")
class UsersDeepLinkOwner : DeepLinkOwner {
    @DeepLinkSpec(patterns = [
        "app://users/list",
        "app://users/{userId}",
        "https://example.com/users/{userId}"
    ])
    fun ownsPattern() {}
}
// Generated in: build/generated/ksp/main/kotlin/.../GeneratedDeepLinkOwners_Users_abc123.k
class GeneratedDeepLinkOwners_Users_abc123 : DeepLinkOwnersProvider {
    override fun getOwners(): Map<String, Set<String>> {
        return mapOf(
            "users" to setOf(
                "app://users/list",
                "app://users/{userId}",
                "https://example.com/users/{userId}"
            )
        )
    }
}
```

#### ServiceLoader Registration


```text
```kotlin

@OwnsDeepLinks(moduleId = "users")
class UsersDeepLinkOwner : DeepLinkOwner {
    @DeepLinkSpec(patterns = [
        "app://users/list",
        "app://users/{userId}",
        "https://example.com/users/{userId}"
    ])
    fun ownsPattern() {}
}
@OwnsDeepLinks(moduleId = "users")
class UsersDeepLinkOwner : DeepLinkOwner {
    @DeepLinkSpec(patterns = [
        "app://users/list",
        "app://users/{userId}",
        "https://example.com/users/{userId}"
    ])
    fun ownsPattern() {}
}
// Generated in: build/generated/ksp/main/kotlin/.../GeneratedDeepLinkOwners_Users_abc123.k
class GeneratedDeepLinkOwners_Users_abc123 : DeepLinkOwnersProvider {
    override fun getOwners(): Map<String, Set<String>> {
        return mapOf(
            "users" to setOf(
                "app://users/list",
                "app://users/{userId}",
                "https://example.com/users/{userId}"
            )
        )
    }
}

# Generated in: build/generated/ksp/main/resources/META-INF/services/

# File: com.example.navigation3.api.DeepLinkOwnersProvider

com.example.githubusers.navigation.generated.GeneratedDeepLinkOwners_Users_abc123

```

### 4. Runtime Discovery

At runtime, navigation-impl discovers all providers via ServiceLoader and aggregates ownership, no DI bindings required:

```kotlin

```kotlin
```kotlin

@OwnsDeepLinks(moduleId = "users")
class UsersDeepLinkOwner : DeepLinkOwner {
    @DeepLinkSpec(patterns = [
        "app://users/list",
        "app://users/{userId}",
        "https://example.com/users/{userId}"
    ])
    fun ownsPattern() {}
}
@OwnsDeepLinks(moduleId = "users")
class UsersDeepLinkOwner : DeepLinkOwner {
    @DeepLinkSpec(patterns = [
        "app://users/list",
        "app://users/{userId}",
        "https://example.com/users/{userId}"
    ])
    fun ownsPattern() {}
}
// Generated in: build/generated/ksp/main/kotlin/.../GeneratedDeepLinkOwners_Users_abc123.k
class GeneratedDeepLinkOwners_Users_abc123 : DeepLinkOwnersProvider {
    override fun getOwners(): Map<String, Set<String>> {
        return mapOf(
            "users" to setOf(
                "app://users/list",
                "app://users/{userId}",
                "https://example.com/users/{userId}"
            )
        )
    }
}

# Generated in: build/generated/ksp/main/resources/META-INF/services/

# File: com.example.navigation3.api.DeepLinkOwnersProvider

com.example.githubusers.navigation.generated.GeneratedDeepLinkOwners_Users_abc123

# Generated in: build/generated/ksp/main/resources/META-INF/services/

# File: com.example.navigation3.api.DeepLinkOwnersProvider

com.example.githubusers.navigation.generated.GeneratedDeepLinkOwners_Users_abc123
val providers = ServiceLoader.load(DeepLinkOwnersProvider::class.java)
val ownership: Map<String, Set<String>> = providers
    .flatMap { it.getOwners().entries }
    .groupBy({ it.key }, { it.value })
    .mapValues { (_, sets) -> sets.flatten().toSet() }

```

## Configuration Requirements

### 1. Module Build Configuration

In your feature module's `build.gradle.kts`:

```kotlin

```kotlin
```kotlin

@OwnsDeepLinks(moduleId = "users")
class UsersDeepLinkOwner : DeepLinkOwner {
    @DeepLinkSpec(patterns = [
        "app://users/list",
        "app://users/{userId}",
        "https://example.com/users/{userId}"
    ])
    fun ownsPattern() {}
}
@OwnsDeepLinks(moduleId = "users")
class UsersDeepLinkOwner : DeepLinkOwner {
    @DeepLinkSpec(patterns = [
        "app://users/list",
        "app://users/{userId}",
        "https://example.com/users/{userId}"
    ])
    fun ownsPattern() {}
}
// Generated in: build/generated/ksp/main/kotlin/.../GeneratedDeepLinkOwners_Users_abc123.k
class GeneratedDeepLinkOwners_Users_abc123 : DeepLinkOwnersProvider {
    override fun getOwners(): Map<String, Set<String>> {
        return mapOf(
            "users" to setOf(
                "app://users/list",
                "app://users/{userId}",
                "https://example.com/users/{userId}"
            )
        )
    }
}

# Generated in: build/generated/ksp/main/resources/META-INF/services/

# File: com.example.navigation3.api.DeepLinkOwnersProvider

com.example.githubusers.navigation.generated.GeneratedDeepLinkOwners_Users_abc123

# Generated in: build/generated/ksp/main/resources/META-INF/services/

# File: com.example.navigation3.api.DeepLinkOwnersProvider

com.example.githubusers.navigation.generated.GeneratedDeepLinkOwners_Users_abc123
val providers = ServiceLoader.load(DeepLinkOwnersProvider::class.java)
val ownership: Map<String, Set<String>> = providers
    .flatMap { it.getOwners().entries }
    .groupBy({ it.key }, { it.value })
    .mapValues { (_, sets) -> sets.flatten().toSet() }

# Generated in: build/generated/ksp/main/resources/META-INF/services/

# File: com.example.navigation3.api.DeepLinkOwnersProvider

com.example.githubusers.navigation.generated.GeneratedDeepLinkOwners_Users_abc123
val providers = ServiceLoader.load(DeepLinkOwnersProvider::class.java)
val ownership: Map<String, Set<String>> = providers
    .flatMap { it.getOwners().entries }
    .groupBy({ it.key }, { it.value })
    .mapValues { (_, sets) -> sets.flatten().toSet() }
plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    implementation(project(":navigation3-api"))
    ksp(project(":navigation3-ksp"))
}

// IMPORTANT: Configure KSP to generate resources
ksp {
    arg("generate.service.files", "true")
}

// Ensure resources are included in the JAR
tasks.withType<Jar> {
    from(layout.buildDirectory.dir("generated/ksp/main/resources"))
}

```

### 2. Processor Module Setup

The `navigation-ksp` module must be configured as a JVM library (not Android):

```kotlin

```kotlin
```kotlin

@OwnsDeepLinks(moduleId = "users")
class UsersDeepLinkOwner : DeepLinkOwner {
    @DeepLinkSpec(patterns = [
        "app://users/list",
        "app://users/{userId}",
        "https://example.com/users/{userId}"
    ])
    fun ownsPattern() {}
}
@OwnsDeepLinks(moduleId = "users")
class UsersDeepLinkOwner : DeepLinkOwner {
    @DeepLinkSpec(patterns = [
        "app://users/list",
        "app://users/{userId}",
        "https://example.com/users/{userId}"
    ])
    fun ownsPattern() {}
}
// Generated in: build/generated/ksp/main/kotlin/.../GeneratedDeepLinkOwners_Users_abc123.k
class GeneratedDeepLinkOwners_Users_abc123 : DeepLinkOwnersProvider {
    override fun getOwners(): Map<String, Set<String>> {
        return mapOf(
            "users" to setOf(
                "app://users/list",
                "app://users/{userId}",
                "https://example.com/users/{userId}"
            )
        )
    }
}

# Generated in: build/generated/ksp/main/resources/META-INF/services/

# File: com.example.navigation3.api.DeepLinkOwnersProvider

com.example.githubusers.navigation.generated.GeneratedDeepLinkOwners_Users_abc123

# Generated in: build/generated/ksp/main/resources/META-INF/services/

# File: com.example.navigation3.api.DeepLinkOwnersProvider

com.example.githubusers.navigation.generated.GeneratedDeepLinkOwners_Users_abc123
val providers = ServiceLoader.load(DeepLinkOwnersProvider::class.java)
val ownership: Map<String, Set<String>> = providers
    .flatMap { it.getOwners().entries }
    .groupBy({ it.key }, { it.value })
    .mapValues { (_, sets) -> sets.flatten().toSet() }

# Generated in: build/generated/ksp/main/resources/META-INF/services/

# File: com.example.navigation3.api.DeepLinkOwnersProvider

com.example.githubusers.navigation.generated.GeneratedDeepLinkOwners_Users_abc123
val providers = ServiceLoader.load(DeepLinkOwnersProvider::class.java)
val ownership: Map<String, Set<String>> = providers
    .flatMap { it.getOwners().entries }
    .groupBy({ it.key }, { it.value })
    .mapValues { (_, sets) -> sets.flatten().toSet() }
plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    implementation(project(":navigation3-api"))
    ksp(project(":navigation3-ksp"))
}

// IMPORTANT: Configure KSP to generate resources
ksp {
    arg("generate.service.files", "true")
}

// Ensure resources are included in the JAR
tasks.withType<Jar> {
    from(layout.buildDirectory.dir("generated/ksp/main/resources"))
}

# Generated in: build/generated/ksp/main/resources/META-INF/services/

# File: com.example.navigation3.api.DeepLinkOwnersProvider

com.example.githubusers.navigation.generated.GeneratedDeepLinkOwners_Users_abc123
val providers = ServiceLoader.load(DeepLinkOwnersProvider::class.java)
val ownership: Map<String, Set<String>> = providers
    .flatMap { it.getOwners().entries }
    .groupBy({ it.key }, { it.value })
    .mapValues { (_, sets) -> sets.flatten().toSet() }
plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    implementation(project(":navigation3-api"))
    ksp(project(":navigation3-ksp"))
}

// IMPORTANT: Configure KSP to generate resources
ksp {
    arg("generate.service.files", "true")
}

// Ensure resources are included in the JAR
tasks.withType<Jar> {
    from(layout.buildDirectory.dir("generated/ksp/main/resources"))
}
plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
    implementation("com.squareup:kotlinpoet:$kotlinPoetVersion")
    implementation("com.squareup:kotlinpoet-ksp:$kotlinPoetKspVersion")
}

```

## Troubleshooting

### ServiceLoader Not Finding Providers

1. **Check resource generation**: Verify `META-INF/services/` files exist in build outpu
2. **Verify JAR packaging**: Use `jar tf build/libs/your-module.jar | grep META-INF`
3. **Module dependencies**: Ensure runtime classpath includes all feature modules

### KSP Not Generating Code

1. **Check processor registration**: Verify `navigation-ksp` is properly registered
2. **Annotation visibility**: Ensure `@OwnsDeepLinks` is `RUNTIME` retention
3. **Build cache**: Try `./gradlew clean` if changes aren't reflected

### Multi-Module Conflicts

1. **Duplicate patterns**: Framework will log warnings for duplicate patterns
2. **Module isolation**: Each module should have unique `moduleId` values
3. **Dependency order**: ServiceLoader discovery order is non-deterministic

## Best Practices

1. **One owner per module**: Each feature module should have a single `@OwnsDeepLinks` class
2. **Unique module IDs**: Use reverse-domain style IDs (e.g., `com.example.users`)
3. **Pattern consistency**: Use normalized patterns (lowercase schemes, no trailing slashes)
4. **Testing**: Write tests for your DeepLinkOwner classes to verify pattern matching
5. **Documentation**: Document your deep link patterns in module README files

## Migration from Manual Registration

If migrating from manual deep link registration:

1. Remove manual registry classes
2. Add `@OwnsDeepLinks` to existing owner classes
3. Remove Hilt multibindings or central registries for ownership; discovery is done at runtime via ServiceLoader
4. Remove hardcoded pattern lists from DI configuration
5. Test deep link resolution after migration

## Example: Complete Feature Setup

```kotlin

```kotlin
```kotlin

@OwnsDeepLinks(moduleId = "users")
class UsersDeepLinkOwner : DeepLinkOwner {
    @DeepLinkSpec(patterns = [
        "app://users/list",
        "app://users/{userId}",
        "https://example.com/users/{userId}"
    ])
    fun ownsPattern() {}
}
@OwnsDeepLinks(moduleId = "users")
class UsersDeepLinkOwner : DeepLinkOwner {
    @DeepLinkSpec(patterns = [
        "app://users/list",
        "app://users/{userId}",
        "https://example.com/users/{userId}"
    ])
    fun ownsPattern() {}
}
// Generated in: build/generated/ksp/main/kotlin/.../GeneratedDeepLinkOwners_Users_abc123.k
class GeneratedDeepLinkOwners_Users_abc123 : DeepLinkOwnersProvider {
    override fun getOwners(): Map<String, Set<String>> {
        return mapOf(
            "users" to setOf(
                "app://users/list",
                "app://users/{userId}",
                "https://example.com/users/{userId}"
            )
        )
    }
}

# Generated in: build/generated/ksp/main/resources/META-INF/services/

# File: com.example.navigation3.api.DeepLinkOwnersProvider

com.example.githubusers.navigation.generated.GeneratedDeepLinkOwners_Users_abc123

# Generated in: build/generated/ksp/main/resources/META-INF/services/

# File: com.example.navigation3.api.DeepLinkOwnersProvider

com.example.githubusers.navigation.generated.GeneratedDeepLinkOwners_Users_abc123
val providers = ServiceLoader.load(DeepLinkOwnersProvider::class.java)
val ownership: Map<String, Set<String>> = providers
    .flatMap { it.getOwners().entries }
    .groupBy({ it.key }, { it.value })
    .mapValues { (_, sets) -> sets.flatten().toSet() }

# Generated in: build/generated/ksp/main/resources/META-INF/services/

# File: com.example.navigation3.api.DeepLinkOwnersProvider

com.example.githubusers.navigation.generated.GeneratedDeepLinkOwners_Users_abc123
val providers = ServiceLoader.load(DeepLinkOwnersProvider::class.java)
val ownership: Map<String, Set<String>> = providers
    .flatMap { it.getOwners().entries }
    .groupBy({ it.key }, { it.value })
    .mapValues { (_, sets) -> sets.flatten().toSet() }
plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    implementation(project(":navigation3-api"))
    ksp(project(":navigation3-ksp"))
}

// IMPORTANT: Configure KSP to generate resources
ksp {
    arg("generate.service.files", "true")
}

// Ensure resources are included in the JAR
tasks.withType<Jar> {
    from(layout.buildDirectory.dir("generated/ksp/main/resources"))
}

# Generated in: build/generated/ksp/main/resources/META-INF/services/

# File: com.example.navigation3.api.DeepLinkOwnersProvider

com.example.githubusers.navigation.generated.GeneratedDeepLinkOwners_Users_abc123
val providers = ServiceLoader.load(DeepLinkOwnersProvider::class.java)
val ownership: Map<String, Set<String>> = providers
    .flatMap { it.getOwners().entries }
    .groupBy({ it.key }, { it.value })
    .mapValues { (_, sets) -> sets.flatten().toSet() }
plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    implementation(project(":navigation3-api"))
    ksp(project(":navigation3-ksp"))
}

// IMPORTANT: Configure KSP to generate resources
ksp {
    arg("generate.service.files", "true")
}

// Ensure resources are included in the JAR
tasks.withType<Jar> {
    from(layout.buildDirectory.dir("generated/ksp/main/resources"))
}
plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
    implementation("com.squareup:kotlinpoet:$kotlinPoetVersion")
    implementation("com.squareup:kotlinpoet-ksp:$kotlinPoetKspVersion")
}

# Generated in: build/generated/ksp/main/resources/META-INF/services/

# File: com.example.navigation3.api.DeepLinkOwnersProvider

com.example.githubusers.navigation.generated.GeneratedDeepLinkOwners_Users_abc123
val providers = ServiceLoader.load(DeepLinkOwnersProvider::class.java)
val ownership: Map<String, Set<String>> = providers
    .flatMap { it.getOwners().entries }
    .groupBy({ it.key }, { it.value })
    .mapValues { (_, sets) -> sets.flatten().toSet() }
plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    implementation(project(":navigation3-api"))
    ksp(project(":navigation3-ksp"))
}

// IMPORTANT: Configure KSP to generate resources
ksp {
    arg("generate.service.files", "true")
}

// Ensure resources are included in the JAR
tasks.withType<Jar> {
    from(layout.buildDirectory.dir("generated/ksp/main/resources"))
}
plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
    implementation("com.squareup:kotlinpoet:$kotlinPoetVersion")
    implementation("com.squareup:kotlinpoet-ksp:$kotlinPoetKspVersion")
}
// users/src/main/kotlin/com/example/users/navigation/UsersNavigation.k
package com.example.users.navigation

import com.example.navigation3.api.OwnsDeepLinks
import com.example.navigation3.api.DeepLinkSpec
import com.example.navigation3.api.DeepLinkOwner

@OwnsDeepLinks(moduleId = "users")
class UsersNavigation : DeepLinkOwner {

    @DeepLinkSpec(patterns = [
        "app://users/list",
        "app://users/{userId}",
        "https://example.com/users/{userId}"
    ])
    fun userPatterns() {}

    override fun handleDeepLink(uri: Uri): Boolean {
        return when {
            uri.path == "/users/list" -> navigateToUserList()
            uri.path?.startsWith("/users/") == true -> {
                val userId = uri.lastPathSegmen
                navigateToUserDetail(userId)
            }
            else -> false
        }
    }
}

```

This approach ensures compile-time safety, reduces boilerplate, and enables true modular deep link ownership across feature modules.

