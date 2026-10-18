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
  read the catalog and append rows to it — and nothing more: since v1.2.0 the
  anon role has only `SELECT, INSERT` on the five catalog tables (no
  UPDATE/DELETE policy exists). See `sql/install.sql` and
  `sql/migrations/001_rls_policies.sql` for the Row Level Security model.
  **Adding Supabase Auth is the recommended production hardening step.**
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

### Modification 009

Date: 2026-10-17

### Changes
- **Security & code cleanup (was planned as v1.1.2).** Removed the unreachable
  PATCH/DELETE paths and unused `SupabaseApi` endpoints; the catalog, taxonomy
  and templates are now **create-only** (insert-only) at the repository layer.
  - `SupabaseApi.kt`: deleted `updateCategory`, `updateEquipmentType`,
    `updateEquipment`, `updateTemplate`, `deleteTemplateEquipment`, the
    `PATCH`/`DELETE` imports and the unused `id` parameter on `getEquipment`.
  - `CatalogRepository.kt`: `saveEquipment`/`saveCategory`/`saveType` →
    `addEquipment`/`addCategory`/`addType` (insert-only); `loadEquipment` no
    longer takes an `id`.
  - `TemplateRepository.kt`: `saveTemplate` → `createTemplate` (insert template
    + lines; removed the update branch and the no-op `deleteTemplateEquipment`).
  - Call sites: `EquipmentEditorViewModel` → `addEquipment`;
    `TemplateEditorViewModel` → `createTemplate`; `TaxonomyViewModel` →
    `addCategory(name, description)` / `addType(type)`; `TaxonomyScreen`
    `EditTarget` is now `(name, description = "")` and the FAB calls
    `EditTarget("")`.
- **SQL hardening.** `sql/install.sql` and `sql/migrations/001_rls_policies.sql`
  now grant the anon role exactly `SELECT, INSERT` on the five catalog tables
  and `REVOKE ALL` on `packages`, `package_equipment` and `access_logs` (RLS
  enabled, no anon policy ⇒ deny). `FOR ALL` catalog policies replaced with
  explicit `FOR SELECT` / `FOR INSERT` per table, so anon can no longer UPDATE
  or DELETE catalog rows even if a grant is added later.
- **"Labor hours" renamed to "Event hours"** in the calculator
  (`LaborProfitTab`) and the template editor. Code/DB field names
  (`laborHours`, Room column, remote `labor_hours`) are unchanged to avoid a
  schema migration.
- **New defaults on a fresh package:** event hours `3`, hourly rate `1000`,
  profit `10` (`CalculatorForm`).
- **PDF redesigned to match `Details.pdf`** (user-supplied reference) while
  keeping the itemised breakdown. New `util/PdfTheme.kt` (colours taken from the
  reference: orange `#EA580C`, banner amber `#F59E0B`, red `#D22525`, green
  `#10B981`, ink/greys, rule `#E5E7EB`; A4 geometry with a 56pt margin;
  vector tick + warning marks; text fitting/wrapping/ellipsis helpers;
  `PdfDates` for ISO → "October 16, 2026" and "+7 days" expiry) and new
  `util/InvoiceSheet.kt` (the renderer). `PdfInvoiceGenerator.kt` now only
  exposes `generate`/`suggestedName`.
  - Layout: orange wordmark + studio line → red "Package Details" → Date /
    Client / Invoice # / Expires On grid → amber package banner (name + studio
    tagline) → "Important" tick list (event-hours coverage + up to 4 equipment
    items, grey sub-note) → itemised table → totals (equipment, event hours,
    extras, subtotal, profit) → green "Total Package Price / LKR" badge →
    amber-ruled "Important Note" band (notes) → centred footer. Continuation
    pages get a compact orange header.
- Also moved the form→draft mapping to `CalculatorForm.toDraft(lines)` in
  `CalculatorModels.kt`, trimming `CalculatorViewModel.kt` to 285 lines.
- Bumped `versionCode` 6 → 8 and `versionName` 1.1.1 → **1.2.0** (the
  security-only clean-up was never released on its own, so it ships here).

### Files / Components
- `data/remote/SupabaseApi.kt`, `data/repo/{CatalogRepository,TemplateRepository}.kt`
- `ui/equipment/{EquipmentEditorViewModel,TaxonomyViewModel,TaxonomyScreen}.kt`,
  `ui/templates/TemplateEditorViewModel.kt`
- `ui/calculator/{CalculatorModels,CalculatorViewModel}.kt`,
  `ui/calculator/tabs/LaborProfitTab.kt`, `ui/templates/TemplateEditorScreen.kt`
- `util/{PdfTheme,InvoiceSheet,PdfInvoiceGenerator}.kt`
- `sql/install.sql`, `sql/migrations/001_rls_policies.sql`
- `app/build.gradle.kts`, `medial_support.txt`, `AGENTS_PLAN.md`

### Important Notes
- Fixes applied during review before release: item names are clamped to the QTY
  column, totals labels are clamped against their amount, and the green badge
  auto-shrinks the amount so a large total cannot overflow the box.
- `PdfDates` parses ISO dates with `ParsePosition` (no `ParseException` on
  unexpected input) and builds a formatter per call (`SimpleDateFormat` is not
  thread-safe).
- **Not verified on a device:** no Android device/emulator was attached to this
  machine, so the generated PDF was not rendered here. Layout geometry, colours
  and font sizes were transcribed from `Details.pdf` (extracted with PyMuPDF),
  and the module compiles plus passes 13/13 unit tests. On-device PDF rendering
  remains to be eyeballed by the user.
- `Details.pdf` (the reference) is left untracked in the repo root and is not
  committed.

---

### Modification 010

Date: 2026-10-17

### Changes
- **Quote PDF is now two pages with different audiences.**
  - **Page 1 - client copy** (sent on WhatsApp): masthead, "Package Details",
    the date / client / invoice # / expiry grid, the amber package banner, the
    coverage tick list, the green **Total Package Price** badge (final total
    only), the optional amber "Important Note" band and the footer. It carries
    **no** itemised lines and **no** price breakdown.
  - **Page 2 - studio copy**: "Price Breakdown" heading with package name /
    invoice # / client, the itemised table (ITEM / QTY / UNIT (LKR) /
    TOTAL (LKR)) and the full breakdown (equipment, event hours, each non-zero
    extra, subtotal, profit, a green **Final Total (LKR)** row), closed by
    "Studio copy - itemised prices and profit included. Not for the client."
- **Package name and sub-title (tagline) are now white** on the amber banner
  (were red / orange).
- Page 1's coverage list no longer names priced items: `"{hours} Hours of
  Photography Coverage"` plus, when equipment exists, `"{n} Item(s) of
  Professional Equipment"`; the old "itemised below" sub-note was replaced by a
  "The package price includes …" sentence built from the non-zero extras.
- New `util/BreakdownSheet.kt` (studio page); `PdfInvoiceGenerator.generate`
  renders `InvoiceSheet` then `BreakdownSheet` into one `PdfDocument`;
  `PdfPageWriter.rightTop` was restored for the right-aligned amount columns,
  and the `Paint` / `BigDecimal` imports came back with it.
- Bumped `versionCode` 8 → 9 and `versionName` 1.2.0 → **1.2.1**.

### Files / Components
- `util/{InvoiceSheet,BreakdownSheet,PdfPageWriter,PdfInvoiceGenerator}.kt`,
  `app/build.gradle.kts`, `medial_support.txt`, `AGENTS_PLAN.md`.

### Important Notes
- The app's on-screen price breakdown is unchanged; only the PDF is split.
- A package with many equipment lines continues onto a third page (continuation
  header), with page 2's heading staying on its own page.
- **Contrast caveat:** white text on the amber banner (`#F59E0B`) is roughly
  2:1, below the WCAG 4.5:1 minimum. Done as requested; darkening the banner
  (e.g. `#B45309`) would restore readability if that matters for print/screen.
- Not rendered on a device (no device/emulator attached) — still to be
  eyeballed by the user; module compiles and 13/13 unit tests pass.

---

### Modification 011

Date: 2026-10-16

> Retroactively documented in Mod 012: this release was committed and
> published as v1.3.0 without being added to this log (rule 2 slip).

### Changes
- Added **append-only usage logging**. On app open and on PDF share/save the
  app inserts a row into `public.access_logs` (`event_type` `app_open` /
  `pdf_export`, pseudonymous `device_id`, `app_version`, `platform`).
- New components: `domain/UsageEvent.kt`, `data/model/UsageModels.kt`
  (`AccessLogDto`), `data/repo/UsageLogRepository.kt` (fire-and-forget on an
  app-scoped IO scope; failures only go to Logcat), `util/DeviceHash.kt` +
  `util/DeviceIdentity.kt` (salted SHA-256 of ANDROID_ID, truncated to 32 hex).
- SQL: `sql/migrations/002_usage_logs.sql` (makes `access_logs` app-friendly:
  nullable `ip_address`, new columns, length CHECKs, **INSERT-only** anon
  grant with no SELECT/UPDATE/DELETE policy), `sql/usage_reports.sql`,
  `sql/diagnostics.sql`; `install.sql` + `001_rls_policies.sql` aligned.
- Tests: `UsageEventTest` (3) + `DeviceHashTest` (6); total unit tests 19.
- Bumped `versionCode` 9 → 10 and `versionName` 1.2.1 → 1.3.0; GitHub release
  v1.3.0 published with the APK.

### Files / Components
- `domain/UsageEvent.kt`, `data/model/UsageModels.kt`, `data/repo/UsageLogRepository.kt`
- `util/{DeviceHash,DeviceIdentity}.kt`, `di/AppContainer.kt`, `PackCalcApp.kt`
- `ui/calculator/{CalculatorModels,CalculatorViewModel}.kt`,
  `ui/packages/PackageDetailViewModel.kt`, `ui/navigation/AppNav.kt`
- `sql/{migrations/002_usage_logs.sql,usage_reports.sql,diagnostics.sql,install.sql}`
- `app/build.gradle.kts`, `medial_support.txt`

### Important Notes
- Privacy: usage rows contain no client names, package names, prices or emails.
- Two build log files (`build-rel.out`, `test.out`) were committed by mistake;
  they were removed and `*.out` added to `.gitignore` in Mod 012.

---

### Modification 012

Date: 2026-10-18

### Changes
- **Mandatory one-time email sign-in gate before the main page** (v1.4.0).
  On first launch the app opens on a branded login screen; the user enters an
  email once (no password), it is saved on the device, and every later launch
  skips the gate. The drawer shows the saved email under "Hire Artist Studio"
  with a tap-to-change route.
- **The email is also recorded in Supabase** in a new append-only table
  `public.user_emails` (email + device hash + app version + platform +
  timestamp) so the studio can export the list and promote its other apps.
- **Offline-first (per decision):** the local save always wins - the user
  enters the app even without a network, and the server copy is retried at
  every app open until it lands (`emailSynced` flag in `SessionStore`; a
  "we'll sync when you're back online" snackbar covers the gap).
- New components: `domain/EmailValidation.kt` (trim/lowercase + format check),
  `util/SessionStore.kt` (app-private SharedPreferences: `email`,
  `email_synced`), `data/model/UserEmailDto.kt`,
  `data/repo/UserEmailRepository.kt`, `ui/login/{LoginScreen,LoginViewModel,
  LoginDestination}.kt`.
- Navigation: `Dest.LOGIN` + `Dest.EMAIL_CHANGE`; start destination is the
  gate only when no email is remembered. `AppDrawer` extracted from `AppNav`
  into `ui/navigation/AppDrawer.kt` (AppNav was near the 300-line limit).
- `SupabaseApi.insertUserEmail` posts with `on_conflict=email` +
  `Prefer: resolution=ignore-duplicates` (UNIQUE email ⇒ retries/re-installs
  collapse into one row; 409 also treated as synced).
- `PackCalcApp` kicks `userEmailRepository.syncPending()` on every open.
- Added `EmailValidationTest` (5 tests); total unit tests now 27.
- Bumped `versionCode` 10 → 11 and `versionName` 1.3.0 → **1.4.0**.

### Files / Components
- `domain/EmailValidation.kt`, `util/SessionStore.kt`
- `data/model/UserEmailDto.kt`, `data/repo/UserEmailRepository.kt`
- `data/remote/SupabaseApi.kt`, `di/AppContainer.kt`, `PackCalcApp.kt`
- `ui/login/{LoginScreen,LoginViewModel,LoginDestination}.kt`
- `ui/navigation/{AppNav,Dest,AppDrawer}.kt`
- `sql/migrations/003_user_emails.sql` (new), `sql/install.sql`
- `app/build.gradle.kts`, `.gitignore`, `medial_support.txt`,
  `app/src/test/.../domain/EmailValidationTest.kt`

### Important Notes
- **SQL to run:** paste `sql/migrations/003_user_emails.sql` into the Supabase
  SQL Editor once (it is also inside `install.sql` for fresh installs). Until
  it runs, sign-in still works locally; the server insert returns 400 and
  stays pending, syncing at the next launch after the migration.
- Security model mirrors `access_logs`: anon has **INSERT only** on
  `user_emails` (no SELECT policy) - the list is read from the Supabase
  dashboard, never from the app. `email` is UNIQUE + length CHECKed.
- Audit (rule 21): email lives in app-private prefs with `allowBackup=false`;
  travels only over HTTPS; never appears in Logcat, in `access_logs`, or in
  debug HTTP logs (BASIC level, debug builds only). No password/token exists -
  this is a soft identity for lead collection, not an accounts system; Supabase
  Auth remains the recommended hardening step if real accounts are ever needed.
- If the migration is missing, `on_conflict=email` fails with 400 (not 409),
  so `emailSynced` correctly stays false and retries resume after the run.

---

## Remaining Work (Tracked in AGENTS_PLAN.md)

- **Fix the live Supabase DB (RLS/seed)** so the equipment catalog and
  templates load — run `sql/install.sql` in the Supabase SQL Editor.
- **Eyeball the new PDF on a device** and confirm the summary matches
  `Details.pdf`.
- Supabase Auth for per-user ownership (hardening).
