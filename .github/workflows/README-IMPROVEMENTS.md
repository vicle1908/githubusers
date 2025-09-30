# GitHub Actions Workflows – September 2025 Snapshot

## Summary

- Balanced coverage across build, test, and security workflows without overloading runners.
- Android toolchain provisioning is centralized via `android-toolchain-versions.yml` and reused by every job.
- OWASP dependency and license aggregation now fails loudly when the underlying tasks exist, instead of being silently ignored.
- Worktree automation uses a single matrix-driven job that adapts to branch conventions (`ai/`, `feature/`, `bugfix/`, `hotfix/`).
- Dependency submission is **not** configured yet; keep referencing the “Outstanding Work” section below before promising it to collaborators.

## Implemented Highlights

- **Enhanced CI (`ci.yml`):** Static analysis (`detektAll`, `ktlintCheckAll`, `:app:lintDebug`) and the native `:core-security:assembleDebug` check run in one Gradle invocation to maximize cache reuse. Unit tests remain isolated, with artifacts, summaries, and toolchain reuse handled consistently.
- **Security & Maintenance (`security-and-maintenance.yml`):** Removes `|| true` fallbacks. Aggregated OWASP and license tasks now short-circuit with a clear log when no included build declares the task, preventing false positives while retaining strict failure semantics when scans are available.
- **Worktree CI (`worktree-ci.yml`):** Consolidated four nearly identical jobs into a single `branch-validation` matrix. Each branch type toggles the expensive tasks it actually needs (e.g., native verification is restricted to `ai/` and `hotfix/` branches, bugfix branches enforce naming rules, release builds run only when useful).
- **Artifact hygiene:** Every job standardises artifact names, compression level (`6`), and `if-no-files-found: ignore` to prevent noisy failures.

## Outstanding Work

1. Wire up a dedicated **dependency submission** workflow using `gradle/actions/dependency-submission`, then document it here.
2. Expand **OWASP/License coverage** by applying the relevant convention plugin to modules that should participate (`core-networking`, `core-security`, etc.). Once the tasks exist, the aggregation job will begin failing on real issues automatically.
3. Evaluate whether code-quality tasks should move to a true matrix (Detekt/Ktlint/Lint split) once runner capacity becomes a bottleneck again.

## Workflow Notes

### `ci.yml`

- Triggered on `push`/`pull_request` for `main` and `develop`, plus manual dispatch.
- `code-quality` job reuses the Android toolchain outputs and runs all static tasks together to warm the configuration cache once.
- `unit-tests` job publishes JUnit reports through `dorny/test-reporter` and stores raw results as artifacts.
- Uploads Detekt, lint, and unit-test reports with run-numbered artifact names for easy triage.

### `worktree-ci.yml`

- `branch-validation` matrix evaluates only for matching branch prefixes; other matrix entries short-circuit automatically.
- Bugfix branches must follow `bugfix/<slug>`; violations fail fast before provisioning gradle.
- Release (`assembleReleaseApp`) builds and native verification are limited to `ai/` and `hotfix/` branches to keep feature branches snappy.
- `ai-merge-queue` only runs for PRs on `ai/` branches and depends on the matrix job finishing cleanly.

### `security-and-maintenance.yml`

- Schedules every Sunday at 02:00 UTC, with manual and main-branch triggers.
- Dependency updates respect plugin cache requirements (`--no-configuration-cache`).
- OWASP & license jobs leverage the new “best-effort but strict” aggregators defined in `build.gradle.kts`.
- Performance and code-metrics jobs write concise summaries to `$GITHUB_STEP_SUMMARY` for quick inspection.

## Permissions & Runtime Defaults

- Jobs request the minimum GitHub permissions required (`contents: read`, `security-events: write` where needed).
- Android SDK versions originate from the shared toolchain workflow; bump them in one place.
- Artifacts default to 7 or 30 days depending on workflow criticality.

## Validation Tips

Run these locally before pushing major workflow edits:

```bash
# Verify Gradle aggregation logic
./gradlew dependencyCheckAnalyzeAll --dry-run
./gradlew generateLicenseReportAll --dry-run

# Smoke-test the CI tasks
./gradlew detektAll ktlintCheckAll :app:lintDebug :core-security:assembleDebug
./gradlew testAll

# Exercise worktree matrix locally (optional)
gh workflow run worktree-ci.yml -f branch_pattern="ai/**"
```

Keep this document in sync whenever workflows gain or lose capabilities so that reviewers and automation can trust it.
