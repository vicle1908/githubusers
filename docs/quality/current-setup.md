# Current quality setup (post-implementation)

Quality conventions

- Aggregator plugin: githubusers.quality (applies githubusers.quality.ktlint + githubusers.quality.detekt conventions)
- KtLint convention:
  - Configures when org.jlleitschuh.gradle.ktlint is present
  - Sets ext.android=true and wires ktlintCheck into check
- Detekt convention:
  - Configures when io.gitlab.arturbosch.detekt is present
  - Uses config at plugins/src/main/resources/detekt/detekt.yml
  - Adds detekt-formatting ruleset
  - Wires detekt into check
  - ignoreFailures defaults to false; override with -Pdetekt.ignoreFailures=true

Module coverage

- All Android and JVM modules declare plugin aliases:
  - alias(libs.plugins.detekt)
  - alias(libs.plugins.ktlint)
- Android/JVM convention plugins apply quality conventions automatically, so check invokes both tools.
- App additionally applies the aggregator plugin id("githubusers.quality").

Configuration

- Detekt configuration centralized at plugins/src/main/resources/detekt/detekt.yml
- Module-level duplicate detekt.yml moved to docs/quality/legacy/
- .editorconfig at repo root enforces single imports (no wildcard) via ktlint_official style

CI & pre-commit

- .github/workflows/ci.yml builds plugins, runs detekt/ktlintCheck and assembleDebug for app, and uploads reports
- tools/git-hooks/pre-commit formats staged Kotlin files (ktlintFormat) and runs detekt; docs/quality/precommit.md has installation steps

Notes

- Running AGP tasks (e.g., assembleDebug or Android module detekt) requires removing deprecated global gradle.properties options like android.enableBuildCache from the user Gradle home if present.
- Non-Android modules (e.g., core-common) validate quality tasks successfully.
