# AGENTS.md — Package Price Calculator (Native Android)

Application-specific development rules, architecture decisions, and modification
log. Kept up to date after every change.

---

## Application Overview

- **Name:** Package Price Calculator (Hire Artist Studio / hiresmcq)
- **Type:** Native Android app — Kotlin + Jetpack Compose (Material 3)
- **Root module:** `app/` (Gradle Android application module)
- **Application ID:** `lk.hiresmcq.packcalc` (debug builds use `.debug` suffix)
- **Backend:** Supabase (PostgREST) — URL + anon key injected via `BuildConfig`
- **Currency:** LKR (`Rs. 12,345.00`)
- **Deployment:** GitHub Releases (single universal APK; x86/x64 rule is Windows-only)

---

## Pre-Flight Checklist (Section 0.1) — Recorded Decisions

| # | Section | Applies? | Status | Notes |
|---|---------|----------|--------|-------|
| 1 | AGENTS.md (app-level) | YES | DONE | This file |
| 2 | AGENTS_PLAN.md | YES | DONE | See AGENTS_PLAN.md |
| 3 | Code Quality (< 300 lines) | YES | ACTIVE | All source files kept < 300 lines |
| 4 | Windows Builds (x86 + x64) | NO | N/A | x86/x64 rule is Windows-only; this is an Android app (single universal APK) |
| 5 | Windows Installers | NO | N/A | Android APK attached to GitHub Release |
| 6 | CLI .bat Launcher | NO | User declined | Mobile app, not CLI |
| 7 | Desktop Update System | NO | N/A | Mobile app; updates via APK install |
| 8 | Git Repository | YES | DONE | At project root |
| 9 | Git Version/Release | YES | DONE | Tagged release with APK artifacts |
| 10 | sql/ Folder | YES | DONE | `sql/` holds Postgres schema + RLS |
| 11 | media/ Folder | YES | DONE | Logos/icons/images inside `media/` |
| 12 | medial_support.txt | YES | DONE | Regenerated each version |
| 13 | .gitignore | YES | DONE | Covers secrets, Gradle, keystores, APKs |

---

## Core Development Rules (Local)

- Build command (this machine):
  `& ".\gradlew.bat" assembleDebug "-Dorg.gradle.java.home=C:\Users\Nwick\.jdks\jdk-17.0.20+8" --console=plain`
- Gradle/CLI output can garble in this shell. Redirect to a file
  (`> build.out 2>&1`) and trust `$LASTEXITCODE` plus a clean re-read of the log.
- Keep every source file under 300 lines. No shims, no silent fallbacks;
  handle errors explicitly (`Result` + `LoadState`).
- Packages:
  `ui.<feature>` (screens + ViewModels), `ui.components` (shared UI),
  `ui.navigation` (routes/NavHost), `data.model` (DTOs), `data.remote`
  (Retrofit API + networking), `data.repo` (repositories), `domain`
  (pure logic), `util` (formatting/parsing), `di` (AppContainer).

## Environment (verified)

- JDK 17: `C:\Users\Nwick\.jdks\jdk-17.0.20+8` (passed via `-Dorg.gradle.java.home`)
- Android SDK: `E:\AITools\android-sdk` (pinned in gitignored `local.properties`)
- `GRADLE_USER_HOME=E:\AITools\gradle-home`
- Gradle wrapper 8.9, AGP 8.7.3, Kotlin 2.0.21, Compose BOM 2024.12.01

## Backend / Security Notes

- Supabase project URL + anon key live in gitignored `secrets.properties`
  (template: `secrets.properties.example`).
- The anon key is embedded in the APK (`BuildConfig`). Anyone with the APK can
  read/write tables the anon role is granted. See `sql/migrations/001_rls_policies.sql`
  for the intended Row Level Security model. **Adding Supabase Auth is the
  recommended production hardening step.**
- `com.squareup.retrofit2:converter-kotlinx-serialization` MUST be used for the
  JSON converter (the jakewharton artifact's Kotlin metadata does not resolve).

---

## Modification Log

### Modification 001

Date: 2026-10-16

### Changes
- Rewrote the project from the previous Expo/React Native app into a **native
  Android (Kotlin + Jetpack Compose)** application.
- Scaffolded Gradle project (wrapper 8.9, version catalog, `app` module).
- Added theme (light, warm red-orange `#D84830` accent from logo).
- Built the data layer: DTOs, Retrofit `SupabaseApi` (full CRUD), networking,
  repositories, `AppContainer` DI.
- Built the domain layer: `PricingCalculator` (BigDecimal, HALF_UP 2dp),
  `CurrencyFormat`, `NumberInput` + 5 passing unit tests.
- Built UI: Package Price Calculator (new / edit / from-template), Templates
  list + editor, Equipment catalog list + editor, Taxonomy (categories/types),
  Packages history list + detail, shared components, bottom-nav `AppNav`.
- Restored `media/` assets and `sql/` schema from prior git history.

### Files / Components
- `app/` (full Android module), `gradle/`, `sql/`, `media/`

### Important Notes
- Table names unchanged from prior schema: `category_types`, `equipment_types`,
  `equipment_details`, `templates`, `template_equipment`, `packages`,
  `package_equipment`, `access_logs`.
- Pricing math: `equipment_total + labor_total = subtotal`;
  `margin = subtotal × margin%`; `final_total = subtotal + margin`.
- Release signing: keystore at `keystore/packcalc-release.jks` (alias `packcalc`),
  credentials in gitignored `keystore.properties`. Release APK copied to
  `build/Package-Price-Calculator.apk` (stable filename, no version).
- RLS decision (interim): anon role has full CRUD on app tables; see
  `sql/migrations/001_rls_policies.sql`. Supabase Auth is the recommended
  hardening step.

### Modification 002

Date: 2026-10-16

### Changes
- Changed the GitHub repository visibility from **private to public** so the
  release APK download link resolves for anonymous users.

### Files / Components
- GitHub repo setting: `chamarawickramarathne-spec/package-price-calculator`

### Important Notes
- Private repos return HTTP 404 for release assets to unauthenticated users
  (GitHub hides existence). Public visibility makes the stable link work:
  `https://github.com/chamarawickramarathne-spec/package-price-calculator/releases/download/v1.0.0/Package-Price-Calculator.apk`
- Verified anonymously: HTTP 200, `application/vnd.android.package-archive`.
- Reference: `sql/migrations/001_rls_policies.sql` (interim anon model) + the
  embedded anon key already allow app access regardless of repo visibility.

### Modification 003

Date: 2026-10-16

### Changes
- Fixed a typo in `secrets.properties` `SUPABASE_URL`: the project ref was
  `vpuzpcyrrteadmolycl` (missing `q`) and did not resolve in DNS, so the app
  reported "Could not reach the server. Check your internet connection."
  Corrected to `vpuzpcyrrteadmolqycl`.
- Bumped `versionCode` 1 → 2 and `versionName` 1.0.0 → 1.0.1.
- Rebuilt the signed release APK and republished it.

### Files / Components
- `secrets.properties` (gitignored), `app/build.gradle.kts`

### Important Notes
- The anon key already contained the correct ref (`vpuzpcyrrteadmolqycl`) —
  only the URL was wrong. Verified a corrected query returns HTTP 200.
- Verified the signed APK's `classes.dex` embeds the corrected URL.
- Live Supabase DB state: RLS is ENABLED and blocks the anon role (test INSERT
  → HTTP 401, code `42501`), and every app table returns `Content-Range: */0`
  (0 rows) to anon. To use the app, run in the Supabase SQL Editor, in order:
  1. `sql/hiresmcq_lens_calculator_postgres.sql` (schema + data)
  2. `sql/migrations/001_rls_policies.sql` (interim anon full-CRUD policies)

---

## Remaining Work (Tracked in AGENTS_PLAN.md)

- Supabase Auth for per-user package ownership (hardening).
- Optional: saved-package sharing / PDF export (out of v1 scope).
