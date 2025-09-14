# Pre-commit setup

Install the hook

Option A (recommended)

1. From repository root, set hooksPath once:

```text
./scripts/install-hooks.sh
```

This uses `.githooks/` for all hooks, including `pre-commit`.

Option B (legacy)

1. From repository root:

```text
ln -sf ../../tools/git-hooks/pre-commit .git/hooks/pre-commit
chmod +x tools/git-hooks/pre-commit
```

What it does

- Formats staged Kotlin files via ktlintFormat fast path
- Runs Detekt (fails on violations unless -Pdetekt.ignoreFailures=true is set)
- Re-adds formatted files to the index

Notes

- If Android Gradle Plugin configuration fails due to deprecated gradle.properties, fix those properties first.
- Generated code is excluded in Detekt.
