# Navigation 3 Enhancements Plan (Typed Destinations, Deep Links, Persistence)

Status: Draft saved locally
Source: Multi-model review (gpt-5 + o3)
Timestamp: 2025-08-28T10:12:31Z

Overview
- Stack: Navigation 3-only, typed destinations, deep-link-driven Navigation3Controller/Host, centralized router, persistence, kill switch, telemetry placeholders. No androidx.navigation.compose usage.
- Goal: Validate and deliver a two-sprint roadmap to harden deep links, saved state, predictive back, multi-window/exported activities, kill switch + telemetry, and documentation.

Constraints
- Use androidx.navigation 2.8/2.9 semantics only.
- Typed destinations (Serializable payloads) are the single source of truth.
- Back stack persistence must survive cold start and process death with restore parity.
- Predictive back interception only at top-of-stack.
- Support multi-window and exported activities for deep links.

Two-sprint ordering (proposed)
- Sprint 1 (P0 foundation)
  - DL-OWN-KSP: per-feature deep link ownership via KSP
  - DL-APP-LINKS: app links verification and normalization
  - SS-PERSIST: entry-level SavedStateHandle persistence
  - SS-RESTORE: restore parity tests
  - PB-TOP: Predictive Back (top-of-stack only)
  - KS-TEL (minimal): read-only kill switch + telemetry baseline
  - DOC-RUN (skeleton): initial docs/runbooks structure

- Sprint 2 (P1 hardening and expansion)
  - DL-TESTS: expanded device matrix and adb flows
  - MW-EXPORT: multi-window + exported deep link activities
  - KS-TEL (expansion): richer metrics/dashboards
  - DOC-RUN (finalization)

Task list (IDs)
1) DL-OWN-KSP — Define @OwnsDeepLinks, codegen registry, param metadata.
2) DL-APP-LINKS — Digital Asset Links + normalization (slash/case/encoding/UTM).
3) DL-TESTS — Unit/Instr/device validation, adb flows across API levels.
4) SS-PERSIST — Serialize typed back stack; lifecycle-aware persistence; process-death restore.
5) SS-RESTORE — Restore parity tests; versioned payload migrations.
6) PB-TOP — Top-of-stack back handling only; predictive back preserved.
7) MW-EXPORT — Exported entry activity, autoVerify, resizeableActivity, per-window stacks.
8) KS-TEL — Read-only mode gating navigate/pop; telemetry events.
9) DOC-RUN — Module template, ownership ledger, KSP usage, test playbooks, app links steps, predictive back guidance.

Acceptance & testing (high level)
- Deep links: Ownership collision detection; app links verified on device; canonicalization; fallback routing without crash.
- Persistence: Parity snapshot before/after kill across core flows; migrations for payload version bumps.
- Predictive Back: Only top screens intercept; gesture preview preserved.
- Multi-window: Separate back stacks per window; no bleed; deep links route correctly.
- Telemetry/Kill switch: Read-only prevents navigation while logging; key nav/restore/deeplink events emitted.

Observability
- Key metrics: deeplink.resolve.success_rate, deeplink.normalize.rewrite.count, restore.success/failure, restore.parity.pass_rate, back.intercepted.count, nav.events.navigate/pop.
- Dashboards for restore success trend, deep link verification status, and navigation event volumes.

Risks & mitigations
- Score/format drift: Enforce structured JSON schemas and validation for generated artifacts and review outputs.
- Predictive back overreach: Scope interception to top-of-stack; add UI tests for gesture behavior.
- App links flakiness: Include device verification and adb start flows; log failures with URI + reason.
- Restore parity regressions: Treat parity test suite as gating; add diff logging for mismatches.

Next steps
- Confirm remote memory target (Zen MCP, Git remote, etc.).
- Optionally move a subset of DL-TESTS baseline to Sprint 1; keep matrix expansion in Sprint 2.
- Decide on rollout gating thresholds (e.g., back stack restore pass rate ≥ 99% before enabling >10% external links).

