# Firebase Performance Setup & Practice

## Purpose

Document the end-to-end steps for enabling Firebase Performance Monitoring in the GitHub Users Android app, authenticating the Firebase CLI/MCP tooling, and validating that traces are flowing.

## Prerequisites

- Android project checked out at the workspace root
- `google-services.json` already placed under `app/`
- Access to the Firebase project `githubusers-74dd8`
- Firebase CLI installed (bundled with the Firebase MCP server in this repo)

## Build Integration

1. Ensure the convention plugin is applied in `app/build.gradle.kts`:
   ```kotlin
   plugins {
       id("githubusers.firebase.performance")
   }
   ```
2. The plugin delegates to `plugins/src/main/kotlin/com/example/githubusers/plugins/FirebasePerformanceConventionPlugin.kt` to:
   - Apply `com.google.gms.google-services` and `com.google.firebase.firebase-perf`
   - Inject `BuildConfig.FIREBASE_PERF_ENABLED`
   - Add the Firebase BOM and `firebase-perf` dependency
3. Verify that the generated `BuildConfig` contains `FIREBASE_PERF_ENABLED = true` for debug builds.

## Runtime Wiring

- `app/src/main/java/com/example/githubusers/UserApplication.kt` initialises Firebase via `FirebaseApp.initializeApp(this)`.
- Performance collection defaults to `BuildConfig.FIREBASE_PERF_ENABLED` and logs status through Timber.
- The custom `PerformanceMonitor` in `core-ui` remains enabled only in debug.

## CLI & MCP Configuration

1. Log in to the Firebase CLI using the MCP tool:
   ```text
   firebase_login
   ```
   Paste the browser provided auth code when prompted.
2. Project metadata is stored via the repo-level configs:
   - `firebase.json`
   - `.firebaserc`
   Both map the default alias to `githubusers-74dd8`.
3. Confirm the environment:
   ```text
   firebase_get_environment
   ```
4. List projects as a smoke test:
   ```text
   firebase_list_projects
   ```

## Validation Workflow

1. Build the debug APK:
   ```bash
   ./gradlew :app:assembleDebug
   ```
2. Install/run on an emulator or device; exercise key paging and networking paths.
3. Watch logcat for `Firebase Performance Monitoring initialised` to confirm SDK startup.
4. Open the Firebase console → Performance dashboard → confirm traces appear (first data can take several minutes).
5. Optional: re-run `firebase_consult_assistant` with targeted prompts once traces exist to surface summaries.

## BigQuery Export (Optional)

1. In Firebase console → Integrations → BigQuery → enable export for Performance Monitoring.
2. Choose or create a BigQuery project/dataset; confirm IAM allows writes.
3. Query the exported tables with BigQuery SQL or BI tooling for deeper analysis.

## Troubleshooting

- **No project detected:** ensure `firebase.json` and `.firebaserc` exist in the repo root and include `githubusers-74dd8`.
- **CLI not logged in:** re-run `firebase_login` and verify `firebase_get_environment` shows an authenticated user.
- **No traces in console:** confirm the app was launched with network access, wait up to 12 hours on first run, and ensure `BuildConfig.FIREBASE_PERF_ENABLED` is true for the build variant.
- **Gradle build misses dependency:** rerun `./gradlew :app:dependencies --configuration debugRuntimeClasspath | grep firebase` to check BOM alignment.

## Firebase CLI & MCP Capabilities

The workspace exposes the Firebase CLI through `npx firebase` and wraps common flows via MCP tools. Below is a quick reference of what each layer supports.

### Firebase CLI (run with `npx firebase <command>`)

- `firebase projects:list` — enumerate accessible projects (use `--json` for automation).
- `firebase apps:list --platform ANDROID` — review registered app IDs; `apps:sdkconfig` downloads config files.
- `firebase apps:android:sha:*` — manage SHA fingerprints (create/list/delete).
- `firebase deploy` / `firebase hosting:channel:*` — standard Hosting deploy/promote flows (unused today but available).
- `firebase emulators:start` — launch local emulators when the repo supplies `firebase.json` configs.
- `firebase functions:*`, `firebase firestore:*`, `firebase database:*`, `firebase storage:*` — lifecycle and data admin for each product if enabled in the project.
- `firebase perf:enable|disable` — toggle Performance Monitoring collection for the project.
- `firebase crashlytics:*`, `firebase auth:*`, `firebase extensions:*` — specialist tooling for the respective products.

Use `npx firebase --help` or `npx firebase <command> --help` for exhaustive flags.

### Firebase MCP Tools (invoked inside the Codex workspace)

- `firebase_login` / `firebase_logout` — manage CLI authentication without leaving the session.
- `firebase_get_environment` — report current project directory, config path, authenticated user, and parsed `firebase.json` aliases.
- `firebase_update_environment` — point the MCP server at a different repo directory (expects a `firebase.json`).
- `firebase_list_projects` / `firebase_list_apps` — thin wrappers around the CLI for quick listing.
- `firebase_consult_assistant` — conversational assistant for Firebase docs/best practices (no live metrics today).
- `firebase_get_sdk_config` / `firebase_create_android_sha` / `firebase_create_app` — programmatic SDK and app management helpers.
- `firebase_apphosting_*`, `firebase_remoteconfig_*`, `firebase_dataconnect_*`, `firebase_messaging_send_message` — delegated product-specific operations.

> ℹ️ Performance Monitoring metrics are not yet exposed through MCP or CLI APIs—use the Firebase console or BigQuery export for raw trace inspection.

### Keeping Both Layers

- The MCP server calls into the CLI for many operations while keeping auth state and responses inside this AI session.
- The CLI remains the superset: use it for scripting, CI, emulator orchestration, or any command MCP does not yet surface.
- Preferred workflow: use MCP for quick status checks and doc lookups; drop down to `npx firebase …` when you need the full command set or automation hooks.
