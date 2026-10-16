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

### Modification 004

Date: 2026-10-16

### Changes
- Fixed the blank/white "New Package" screen. Root cause in `ui/navigation/AppNav.kt`:
  the calculator route helper called `composable(...)` **inside** the content
  lambda of an already-registered `composable(...)`. Registering a destination
  during composition renders nothing → empty screen. Replaced the nested
  `NavGraphBuilder.calculatorDestination` with a plain `@Composable
  CalculatorDestination(...)` invoked from the New / Edit / From-Template routes.
- Removed the **edit and delete** options for equipment catalog items, templates,
  and categories / equipment types. These lists are now view + add only
  (`+` still available; templates keep "Use template").
- Made the Equipment and Template editors create-only (removed the edit-load
  path and the editor delete action).
- Removed the now-unused repository methods (`deleteEquipment`, `deleteCategory`,
  `deleteType`, `deleteTemplate`, `loadEquipmentById`) and the corresponding
  `DELETE` endpoints in `SupabaseApi`.
- Bumped `versionCode` 2 → 3 and `versionName` 1.0.1 → 1.0.2.

### Files / Components
- `ui/navigation/AppNav.kt`, `ui/navigation/Dest.kt`
- `ui/equipment/{EquipmentScreen,EquipmentListViewModel,EquipmentEditorScreen,EquipmentEditorViewModel,TaxonomyScreen,TaxonomyViewModel}.kt`
- `ui/templates/{TemplatesScreen,TemplateListViewModel,TemplateEditorScreen,TemplateEditorViewModel}.kt`
- `data/repo/{CatalogRepository,TemplateRepository}.kt`, `data/remote/SupabaseApi.kt`
- `app/build.gradle.kts`

### Important Notes
- Retained by request: deleting/editing **saved packages** (packages history),
  and the in-editor "remove equipment line" buttons used while composing a new
  package or template.
- The `EQUIPMENT_EDIT` and `TEMPLATE_EDIT` navigation routes were removed.

### Modification 005

Date: 2026-10-16

### Changes
- Added an **in-app auto-update** system. On launch the app queries the GitHub
  Releases API for the latest release, compares it with the installed version,
  and — when newer — offers to download and install the APK.
- New components: `domain/Version.kt` (semver parse/compare),
  `data/model/GitHubModels.kt`, `data/remote/GitHubApi.kt`,
  `data/remote/ApkDownloader.kt` (streamed download + progress),
  `data/repo/UpdateRepository.kt`, `util/ApkInstaller.kt` (FileProvider +
  `ACTION_VIEW` + unknown-sources permission), and
  `ui/update/{UpdateViewModel,UpdateDialog}.kt`.
- `AndroidManifest.xml`: added `REQUEST_INSTALL_PACKAGES` and a `FileProvider`
  (`${applicationId}.fileprovider`, paths in `res/xml/file_paths.xml`).
- `NetworkModule`: added a separate GitHub Retrofit client (base
  `https://api.github.com/`, required `User-Agent`/`Accept` headers) and a
  plain `OkHttpClient` factory for downloads.
- `AppNav`: instantiates `UpdateViewModel` (Activity-scoped) and renders
  `UpdateDialog` over the nav host.
- Bumped `versionCode` 3 → 4 and `versionName` 1.0.2 → 1.0.3.
- Added `VersionTest` (6 tests); total unit tests now 11.

### Files / Components
- `domain/Version.kt`, `data/model/GitHubModels.kt`, `data/remote/{GitHubApi,ApkDownloader,NetworkModule}.kt`
- `data/repo/UpdateRepository.kt`, `util/ApkInstaller.kt`, `ui/update/{UpdateViewModel,UpdateDialog}.kt`
- `di/AppContainer.kt`, `ui/navigation/AppNav.kt`, `AndroidManifest.xml`, `res/xml/file_paths.xml`
- `app/build.gradle.kts`, `app/src/test/java/.../domain/VersionTest.kt`

### Important Notes
- Update source: public GitHub repo `chamarawickramarathne-spec/package-price-calculator`,
  asset name kept stable at `Package-Price-Calculator.apk` (no version in the
  filename, per the update rule). Target version parsed from the release tag.
- A failed **update check** is non-fatal: it maps to `CheckFailed` and shows no
  dialog, so a flaky network never blocks the app. Download/install failures
  ARE surfaced (`Failed` dialog).
- Android requires the user to allow "install unknown apps" for this app;
  when not granted, the dialog routes to `ACTION_MANAGE_UNKNOWN_APP_SOURCES`.

---

### Modification 006

Date: 2026-10-17

### Changes
- **Major UX redesign of New Package.** The single scrolling form became a
  **4-tab wizard**: `Details`, `Equipment`, `Labor & Profit`, `Pricing`
  (bottom bar shows the live Final Total plus Back / Next / Save).
- **5 new cost fields** (all flat LKR), added under Labor & Profits:
  Extra Gear Cost, Transportation Cost, Assistant Pay, Editing/Retouching Cost,
  Additional Cost.
- **Renamed Margin → Profit** everywhere in the calculator. New formula:
  `equipmentTotal + laborTotal + extraCosts = subtotal`;
  `profit = subtotal × profit%`; `finalTotal = subtotal + profit`
  (extras join the subtotal and profit applies to all of it).
  `PricingBreakdown` now carries `extraCostsTotal` + `profitAmount`.
- **Packages are now stored on-device with Room (SQLite).** New
  `data/local/{AppDatabase,PackageDao,PackageEntity,PackageItemEntity}.kt`
  and `data/repo/LocalPackageRepository.kt`. The `packages` /
  `package_equipment` remote endpoints were removed from `SupabaseApi`
  and the old `PackageRepository` was deleted.
- **Drawer navigation replaces the bottom bar.** `AppNav` now uses
  `ModalNavigationDrawer` (New Package / Saved Packages / Templates /
  Equipment); the calculator is the start destination. Package-detail and
  calculator-route ids changed from `Int` to `Long`.
- **PDF invoices.** `util/PdfInvoiceGenerator.kt` renders a branded A4 invoice
  (Hire Artist Studio header, placeholder contact, auto `INV-####` number,
  item lines, itemised extra costs, subtotal, profit, final total, notes).
  Available from wizard tab 4 and from the saved-package detail screen, via
  `util/InvoiceSharing.kt` (share) and the SAF document picker (save).
  `ui/components/InvoicePdfEffect.kt` handles both screens.
- Added a **project date** field (with Material3 date picker) to tab 1.
- Bumped `versionCode` 4 → 5 and `versionName` 1.0.3 → 1.1.0.

### Files / Components
- Gradle: `gradle/libs.versions.toml`, `app/build.gradle.kts` (KSP plugin,
  Room runtime/ktx/compiler).
- `data/local/{AppDatabase,PackageDao,PackageEntity,PackageItemEntity}.kt`,
  `data/repo/LocalPackageRepository.kt`, `data/repo/PackageRepository.kt` (deleted).
- `data/remote/SupabaseApi.kt`, `data/model/PackageModels.kt` (package DTOs removed).
- `domain/{ExtraCosts,PricingCalculator,SavedPackage}.kt`.
- `util/{CompanyInfo,PdfInvoiceGenerator,InvoiceSharing}.kt`.
- `ui/calculator/{CalculatorViewModel,CalculatorScreen,CalculatorBottomBar}.kt`
  and `ui/calculator/tabs/{PackageDetailsTab,EquipmentTab,LaborProfitTab,PricingTab}.kt`.
- `ui/packages/{PackageListViewModel,PackageListScreen,PackageDetailViewModel,PackageDetailScreen}.kt`.
- `ui/components/{InvoicePdfEffect,PriceBreakdownCard}.kt`,
  `ui/templates/{TemplatesScreen,TemplateListViewModel,TemplateEditorScreen}.kt`,
  `ui/equipment/EquipmentScreen.kt`, `ui/navigation/{AppNav,Dest}.kt`,
  `di/AppContainer.kt`, `PackCalcApp.kt`, `res/xml/file_paths.xml`,
  `app/proguard-rules.pro`.

### Important Notes
- **Storage split (by decision):** packages live locally in Room (with a
  snapshot of the equipment lines so old packages keep their names/values);
  the **equipment catalog and templates remain in Supabase**.
- Invoice numbers are assigned from the Room row id (`INV-0001`, …) inside a
  single transaction, so they are stable and never reused.
- **Saving or sharing a PDF first stores the package** so it has an invoice
  number; the dialog says so.
- PDF generation runs on `Dispatchers.IO`; the share sheet uses a FileProvider
  path (`cache/invoices`), the save flow uses `ActivityResultContracts.CreateDocument`.
- Added Room consumer keep-rules to `proguard-rules.pro`.
- Unit tests now 13 (7 pricing + 6 version).

---

### Modification 007

Date: 2026-10-17

### Changes
- **Post-release security/robustness audit (rule 21)** of v1.1.0 and five fixes:
  1. `CalculatorViewModel.requestPdf` — PDF generation ran **unwrapped**, so a
     failure (cache write error, OOM) crashed the app. Now wrapped in
     `runCatching`; the failure is reported through the existing snackbar.
  2. `PackageDetailViewModel.requestPdf` — the failure branch of
     `runCatching { … }.onSuccess { … }` was **discarded**, so tapping
     Share/Save produced no feedback at all. Added a `snackbar` channel and
     `consumeSnackbar()`; the screen now shows the message.
  3. `InvoicePdfEffect` — `startActivity(shareIntent)` could throw
     `ActivityNotFoundException` (no receiver for the PDF share) and crash.
     Now guarded, with a user-visible message.
  4. `InvoiceSharing.copyTo` — wrote with the default mode; overwriting an
     existing file could leave the tail of a previously longer PDF. Now opens
     the stream with `"wt"` (truncate).
  5. `app/build.gradle.kts` — release builds **silently fell back to the debug
     signing config** when `keystore.properties` was absent, which would ship an
     APK that cannot install over a release-signed one. Now a
     `gradle.taskGraph.whenReady` guard throws a `GradleException` with
     instructions whenever a Release task runs without the publisher keystore.
     Debug/test tasks are unaffected (verified both paths).
- Bumped `versionCode` 5 → 6 and `versionName` 1.1.0 → 1.1.1.

### Files / Components
- `ui/calculator/CalculatorViewModel.kt`, `ui/packages/PackageDetailViewModel.kt`,
  `ui/packages/PackageDetailScreen.kt`, `ui/components/InvoicePdfEffect.kt`,
  `util/InvoiceSharing.kt`, `app/build.gradle.kts`, `AGENTS_PLAN.md`,
  `medial_support.txt`.

### Important Notes
- Reviewed and found no issue with: HTTP logging (gated behind
  `BuildConfig.DEBUG`, level `BASIC`), manifest exposure (only `MainActivity`
  exported, `allowBackup="false"`, FileProvider `exported="false"`), FileProvider
  paths (cache subdirs only — no external storage), Room schema (FK
  `package_items → packages` `ON DELETE CASCADE` is generated), secrets
  (`secrets.properties`/`keystore.properties` gitignored, absent from the tree).
- Verified the shipped APK is signed by the publisher key
  (`CN=Hire Artist Studio`, SHA-256 `F5:C6:C7:3F…93:EC:87`) — identical to the
  keystore used for v1.0.0–v1.1.0, so in-app updates install cleanly.
- Tests still 13/13; all files remain under 300 lines.

---

### Modification 008

Date: 2026-10-17

### Changes
- Added **`sql/install.sql`** — a single paste-ready, re-runnable database
  installer: base schema mirror + seed data + sequence `setval`s + Row Level
  Security grants and policies. The previous flow needed two files run in a
  specific order, which is the likely cause of the live DB returning 0 rows.
- **Scoped the anon role down to what the app actually uses** (audit, rule 21).
  Verified from the code that the only reachable writes are INSERTs on the five
  catalog tables (all editors pass `id = null` — create-only since Mod 004) and
  a DELETE on `template_equipment`; nothing calls the PATCH paths.
  `public.packages` / `public.package_equipment` are now **revoked** and left
  with RLS enabled and no anon policy (packages are device-local since v1.1.0),
  and `sql/migrations/001_rls_policies.sql` was updated to match so the two SQL
  paths cannot diverge.
- Added a pointer in `sql/hiresmcq_lens_calculator_postgres.sql` recommending
  `sql/install.sql`.

### Files / Components
- `sql/install.sql` (new, 474 lines), `sql/migrations/001_rls_policies.sql`,
  `sql/hiresmcq_lens_calculator_postgres.sql`.

### Important Notes
- Run instructions: Supabase dashboard → SQL Editor → paste `sql/install.sql` →
  Run (single run). Re-running is safe and restores the catalog snapshot; saved
  packages are unaffected because they live in the app's Room database.
- **Not executed here**: no Docker/psql/Postgres is available on this machine, so
  the script could not be run against a real server. It was assembled from the
  previously working schema/seed file plus the known-good policy set, and
  statically checked (ASCII-only, balanced parentheses, even quoting,
  single `BEGIN;`/`COMMIT;`).
- No app code changed, so no rebuild or re-release was required (rule 4/12).
- Deferred hardening (tracked in AGENTS_PLAN.md): remove the now-unreachable
  PATCH/DELETE repository branches and the unused `SupabaseApi` update/delete
  endpoints, then narrow the catalog grant to `SELECT, INSERT` (+ `DELETE` on
  `template_equipment`).

---

## Remaining Work (Tracked in AGENTS_PLAN.md)

- **Fix the live Supabase DB (RLS/seed)** so the equipment catalog and
  templates load — the calculator's Equipment tab is empty until then.
- Supabase Auth for per-user ownership (hardening).
