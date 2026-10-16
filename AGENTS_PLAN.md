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
- [x] Navigation: bottom-nav `AppNav` + `MainActivity` wiring (bottom bar was
      replaced by a navigation drawer in v1.1.0).
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
- [x] **Feature 1.1.0 — UX redesign (this phase):**
      - New Package became a 4-tab wizard (Details / Equipment / Labor & Profit /
        Pricing) with Back / Next / Save.
      - 5 new flat LKR cost fields (extra gear, transportation, assistant pay,
        editing/retouching, additional); extras join the subtotal and profit
        applies to all of it.
      - Margin renamed to Profit throughout the calculator.
      - Packages stored **locally with Room (SQLite)**; catalog + templates stay
        in Supabase.
      - Navigation drawer replaces the bottom bar (New Package is the start
        destination).
      - **PDF invoices** (branded, auto `INV-####`) shared via WhatsApp/email or
        saved with the system file picker — from the wizard and from the saved
        package detail.
      - Added a project date field with a Material3 date picker.
      - Added Room + KSP; unit tests now 13 (7 pricing + 6 version).
- [x] **Fix 1.1.1 — post-release audit (rule 21):**
      - PDF failures are now surfaced instead of crashing (wizard) or being
        silently swallowed (saved-package detail).
      - Share-sheet launch failures show a message instead of crashing.
      - Saved invoices now overwrite the target file (truncate) so an existing
        longer PDF can never leave trailing bytes.
      - Release builds now fail loudly when the published signing key is
        missing, instead of silently falling back to a debug signature.
- [x] **SQL installer (Mod 008):** `sql/install.sql` — one paste-ready,
      re-runnable script (schema + seed + sequences + RLS) so the live DB can be
      fixed in a single run.
- [x] **Release 1.2.0 — security cleanup + UX/PDF refresh (Mod 009):**
      - Catalog/taxonomy/templates made strictly create-only (removed the
        unreachable PATCH/DELETE repository branches and unused `SupabaseApi`
        endpoints).
      - SQL narrowed to `SELECT, INSERT` on the five catalog tables; `REVOKE ALL`
        on `packages`, `package_equipment`, `access_logs`; explicit per-command
        policies (no `FOR ALL`).
      - "Labor hours" → "Event hours" (label only; DB/Room field names kept).
      - New-package defaults: 3 event hours, LKR 1,000/hour, 10% profit.
      - PDF redesigned in the `Details.pdf` house style (orange wordmark, red
        "Package Details", amber package banner, green price badge, tick list,
        amber note band) while keeping the itemised breakdown; split into
        `util/PdfTheme.kt` + `util/InvoiceSheet.kt`.
      - 13/13 unit tests; all files < 300 lines.
- [x] **Release 1.2.1 — two-page PDF (Mod 010):** page 1 is the client summary
      (no itemised lines, no breakdown); page 2 is the studio copy
      (`util/BreakdownSheet.kt`) with the itemised table and the full price
      breakdown ending in a green Final Total row. Package name + sub-title are
      now white on the amber banner; page 1's coverage list shows hours +
      equipment count instead of priced item names.

### Pending
- [ ] **Run `sql/install.sql`** in the Supabase SQL Editor (single paste, single
      run) so the equipment catalog and templates load — until then the
      Equipment picker and Templates list stay empty.
- [ ] **Eyeball the redesigned PDF on a device** and confirm the summary matches
      `Details.pdf` (not rendered on this machine — no device/emulator attached).
- [ ] Supabase Auth for per-user ownership (hardening).

---

(C) 2026 Hire Artist Studio. AGENTS_PLAN versioned with AGENTS.md.
