---
type: "agent_requested"
description: "Example description"
---

# Kotlin Development Rule

## Scope


- Language-only guidance for Kotlin source. Do not include Android/Compose/Hilt/Navigation/Room/DataStore specifics here (see .augment/rules/android.md for platform topics).

## Style and naming


- Follow the official Kotlin coding conventions.
  - Packages/directories: lowercase, no underscores (Android resource filenames are separate and use snake_case; that is outside this file).
  - Kotlin files: If a single top-level type, name the file after that type (PascalCase). For extension collections, use DescriptiveExtensions.kt.
  - Types/objects: PascalCase. Functions/properties/locals: camelCase. Constants: UPPER_SNAKE_CASE.
- Formatting
  - Prefer expression-bodied functions for trivial logic; keep functions small and single-purpose.
  - Enable trailing commas for multiline lists/parameters to improve diffs.
  - Max line length 120 (project default); keep imports ordered consistently.

## Imports


- Single imports only; no wildcard/star imports.
  - Use explicit imports; allow aliases to disambiguate collisions.
  - Keep import ordering consistent with KtLint/IDE settings.

## Types and data modeling


- Favor immutability (val-first); expose read-only views of collections.
- Use data classes for immutable aggregates; prefer value (inline) classes for type safety where helpful.
- Avoid primitive obsession; model domain concepts explicitly.
- Public API and module boundary members must have explicit types. Locals and private members may rely on type inference when obvious.

## Functions and APIs


- Keep functions small; prefer early returns over deep nesting.
- Use default parameter values and named arguments to improve call-site clarity (avoid excessive overloads).
- Use extension functions judiciously for domain readability; avoid surprising shadowing.
- Document public APIs with concise KDoc that links to related types; avoid @param/@return when not necessary.

## Null-safety and errors


- Avoid !!; use safe calls (?.) and Elvis (?:) for fallbacks.
- Validate inputs with require/check/checkNotNull for preconditions.
- Prefer sealed error hierarchies or Kotlin Result for domain errors; propagate exceptions only for unrecoverable failures.

## Collections and sequences


- Prefer immutable interfaces (List/Set/Map) at boundaries. Use mutable copies internally when needed.
- For large transformation chains, consider Sequence to avoid intermediate allocations; measure before optimizing.
- Use mapNotNull/filterNotNull appropriately; be mindful of allocation cost in hot paths.

## Language idioms (scope functions)


- let: for null-chains or single-expression transformations that return a different type.
- apply: for builder-style configuration of a receiver; keep mutation localized and return the receiver.
- run: for computing and returning a result with a receiver when you need a different return type.
- also: for side-effects (logging, validation) without altering the value being passed through.
- with: rarely, for grouping multiple calls on an external receiver; prefer run/apply on the object itself.
- Avoid deep nesting or overuse that harms readability; prefer clear, small functions.

## Generics and advanced features


- Variance: out for producers, in for consumers.
- Reified type parameters with inline functions where reflection would otherwise be needed.
- Operator overloading only when it preserves clarity and conventional meaning.
- Consider typealias for complex function types to improve readability.

## Coroutines and Flow (language-level)


- Structured concurrency: scope ownership must be explicit; do not use GlobalScope.
- Dispatchers: Default for CPU-bound, IO for blocking I/O. Inject dispatchers for testability when needed.
- Cancellation-aware code; use withContext(NonCancellable) only for critical cleanup.
- Expose read-only StateFlow/SharedFlow; prefer stateIn/shareIn to publish cold flows.
- Testing: kotlinx-coroutines-test runTest; assert flows with Turbine.

## Testing


- Unit tests for pure logic and transformations; keep tests deterministic.
- Use JUnit (4/5) or Kotest, MockK for doubles, kotlinx-coroutines-test for coroutines, and Turbine for Flow assertions.

## Lint and quality


- KtLint + Detekt are required:
  - Enforce no wildcard imports and import ordering.
  - Keep code formatted per Kotlin conventions; add KDoc to public APIs.
  - Prefer explicit return types for public APIs and library modules.

## Sources


- Kotlin coding conventions: https://kotlinlang.org/docs/coding-conventions
- Kotlin coroutines/Flow (general): https://kotlinlang.org/docs/coroutines-overview
- Detekt import rules (no wildcard imports): https://detekt.dev/docs/rules/style , https://detekt.dev/docs/rules/formatting
- KtLint rules (no-wildcard-imports, import ordering): https://pinterest.github.io/ktlint/rules/standard/

