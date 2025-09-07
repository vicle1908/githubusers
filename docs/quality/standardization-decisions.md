# Standardization decisions for quality plugin enforcement

Scope:

- Apply KtLint and Detekt via convention plugins across all modules
- Wire ktlintCheck and detekt into the Gradle check lifecycle
- Align Detekt formatting rules via detekt-ktlint wrapper and disable Detekt formatting rules
- Centralize Detekt configuration under config/detekt/detekt.yml

Decisions:

1. Aggregator vs. per-tool conventions

- Decision: Keep per-tool conventions (githubusers.quality.ktlint, githubusers.quality.detekt) and add aggregator githubusers.quality for convenience.

1. Underlying plugin application

- Decision: Convention plugins apply their underlying IDs (org.jlleitschuh.gradle.ktlint, io.gitlab.arturbosch.detekt) to guarantee presence.

1. Task wiring

- Decision: Always wire ktlintCheck and detekt into 'check' to enforce during verification.

1. Strictness

- Decision: Detekt ignoreFailures is strict by default; allow override via -Pdetekt.ignoreFailures=true for transitional stages.

1. Ruleset alignment

- Decision: Replace detekt-formatting with detekt-ktlint wrapper for perfect alignment with KtLint. Keep formatting: active: false in detekt.yml.

1. Centralized configuration

- Decision: Move root detekt.yml to config/detekt/detekt.yml; keep module baselines at config/detekt/baseline.xml.

1. Generated code exclusions

- Decision: Exclude build/**, generated/**, build/generated/**, build/ksp/** from Detekt tasks to avoid false positives.

1. Docs and MCP-first

- Decision: Update docs/WARP.md to prefer gradle-mcp-server; add pre-commit guidance; ensure all docs stay under docs/.
