# Claude Context Indexing (Android Projects)

## Goals

- Enable fast, reliable semantic code search via Claude Context MCP
- Keep indexes clean by excluding build artifacts and binaries
- Provide a safe remediation path for common vector-store errors

## Quick Start

- Preferred tools (router names):

  - `mcp_router index_codebase` — index a codebase path
  - `mcp_router get_indexing_status` — check progress/completion
  - `mcp_router search_code` — semantic code search
  - `mcp_router clear_index` — clear/reset a path index

- Typical params: `path`, `splitter` (`ast` or `langchain`), `force`, `ignorePatterns`

### Include vs Exclude

Include (source + config)

- `src/**/*.kt`, `src/**/*.java`, `src/**/*.xml`
- `*.gradle`, `*.kts`, `settings.gradle[.kts]`, `gradle.properties`
- Proguard rules, `README.md`, other Markdown docs

Exclude (generated + binaries)

- `**/build/**`, `.gradle/**`, `.idea/**`, `.cxx/**`, `.externalNativeBuild/**`, `.git/**`
- `**/*.apk`, `**/*.aab`, `**/*.so`, `**/*.jar`, `**/*.png`, `**/*.jpg`, `**/*.webp`

### Recommended Flow

1. Small-scope smoke test

   - Index `app/src/main/java` first to verify collection creation

2. Fresh namespace when schema changes

   - Bump namespace when changing embedding model or metric to avoid collisions

3. Lock schema

   - Choose an embedding model and metric; ensure vector dimension matches the collection configuration

4. Expand scope gradually

   - Once smoke test passes, index `app/src/main` then broader modules

## Troubleshooting: “Error validating collection creation”

Common causes

- Schema/state mismatch (dimension/metric) in vector store
- Stale/corrupt local store or insufficient permissions
- Overscoped dataset including build artifacts/binaries

Fix sequence

1. Enable verbose logging; identify backend and namespace/collection
2. Reset state: delete local index directory or drop remote collection; or use a fresh namespace
3. Lock schema: fix one embedding model + metric; ensure dimension matches
4. Apply Android excludes; retry on a small subdirectory
5. Environment hygiene: ensure write permissions; raise file descriptors (e.g., `ulimit -n 8192` on macOS/Linux)

If problems persist

- Try a different backend (local vs remote)
- Temporarily switch to BM25/file-backed index to unblock search

## Approvals & Sandbox (Codex CLI)

- Filesystem: `workspace-write` — safe to edit inside workspace
- Network: `restricted` — web-backed tools may require approval
- Approvals: `on-request` — ask for escalation before network/destructive actions

## Zen Workflows (Optional but Recommended)

- `consensus` to compare remediation paths (incremental vs reset)
- `planner` to track concise steps with status updates
- `precommit` to validate changes before commit

## References

- See `docs/assistants/mcp-guide.md` for MCP-first policy and tool selection
- See `docs/assistants/README.md` for canonical document index
