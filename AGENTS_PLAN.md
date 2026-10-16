# AGENTS_PLAN.md — Package Price Calculator

Implementation plan and task tracker for the native Android rewrite
(Kotlin + Jetpack Compose) of the Package Price Calculator.

---

## Pre-Flight Checklist (Section 0.1) — Recorded Decisions

| # | Section | Applies? | Status | Notes |
|---|---------|----------|--------|-------|
| 1 | AGENTS.md (app-level) | YES | DONE | Root of repo |
| 2 | AGENTS_PLAN.md | YES | DONE | This file |
| 3 | Code Quality (< 300 lines) | YES | ACTIVE | Files kept under 300 lines |
| 4 | Windows Builds (x86 + x64) | NO | N/A | Windows-only rule; Android single universal APK |
| 5 | Windows Installers | NO | N/A | Android APK on GitHub Release |
| 6 | CLI .bat Launcher | NO | User declined | Mobile app |
| 7 | Desktop Update System | NO | N/A | Mobile app |
| 8 | Git Repository | YES | DONE | At project root |
| 9 | Git Version/Release | YES | DONE | v1.0.0 release with APKs |
| 10 | sql/ Folder | YES | DONE | `sql/` schema + RLS migration |
| 11 | media/ Folder | YES | DONE | logos/icons/images |
| 12 | medial_support.txt | YES | DONE | Regenerated each version |
| 13 | .gitignore | YES | DONE | Secrets, Gradle, keystores, APKs |

---

## History

- **Phase-1 (Web + PWA):** HTML/CSS/JS + PHP/MySQL Lens Calculator. Shipped.
- **Phase-2 (Expo / React Native):** Rewritten as an Expo SDK 57 app, released
  v1.0.0 with x86 + x64 APKs.
- **Phase-3 (Native Android — current):** Rewritten again as a first-class
  native Android app (Kotlin + Jetpack Compose). This plan tracks Phase-3.

---

## Phase-3 (Native Android) — COMPLETED

### Completed
- [x] Gradle project scaffold (wrapper 8.9, version catalog, `app` module).
- [x] Theme (light, `#D84830` accent), launcher icon, manifest, resources.
- [x] Data layer: DTOs, `SupabaseApi` (full CRUD), `NetworkModule`,
      repositories, `AppContainer`, `PackCalcApp`.
- [x] Domain layer: `PricingCalculator`, `CurrencyFormat`, `NumberInput`
      (+ 5 passing unit tests).
- [x] Shared UI components (states, inputs, stepper, picker sheet, cards,
      confirm dialog).
- [x] Calculator screen (New / Edit / From Template) with live breakdown.
- [x] Templates: list + editor.
- [x] Equipment: catalog list + editor, taxonomy manager.
- [x] Packages: history list + detail.
- [x] Navigation: bottom-nav `AppNav` + `MainActivity` wiring.
- [x] `assembleDebug` and `testDebugUnitTest` green.
- [x] `media/` + `sql/` restored; docs regenerated.
- [x] `sql/migrations/001_rls_policies.sql` (interim anon full-CRUD model).
- [x] Release keystore generated; signed release APK built (`assembleRelease`).
- [x] Universal APK staged at `build/Package-Price-Calculator.apk`.
- [x] `medial_support.txt` regenerated for v1.0.0.
- [x] Committed to `main` and pushed.
- [x] GitHub release `v1.0.0` published with the universal APK attached.
- [x] Hotfix 1.0.1: corrected Supabase URL typo (project ref was missing `q`);
      rebuilt + republished the APK.
- [x] Fix 1.0.2: blank "New Package" screen (nested `composable()` in AppNav);
      removed edit/delete options for equipment, templates, categories & types.
- [x] Feature 1.0.3: in-app auto-update (check GitHub Releases, download APK,
      launch installer).

### Pending
- [ ] Apply live DB SQL in Supabase SQL Editor (schema + seed, then RLS) —
      the app connects but tables currently return 0 rows to anon.
- [ ] Supabase Auth for per-user package ownership (hardening).
- [ ] Optional: PDF export / sharing of saved packages.

---

(C) 2026 Hire Artist Studio. AGENTS_PLAN versioned with AGENTS.md.
