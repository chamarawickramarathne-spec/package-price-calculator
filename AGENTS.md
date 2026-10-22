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

- Build commands (this machine):
  - All debug variants (both flavors): `& ".\gradlew.bat" assembleDebug "-Dorg.gradle.java.home=C:\Users\Nwick\.jdks\jdk-17.0.20+8" --console=plain`
  - GitHub channel APK: `assembleGithubRelease` → staged as `build/Package-Price-Calculator.apk`
  - Google Play bundle: `bundlePlayRelease` → `app/build/outputs/bundle/playRelease/app-play-release.aab`
- **Two flavors (Mod 019):** `github` (self-updater + `REQUEST_INSTALL_PACKAGES`)
  and `play` (no updater, no install permission, `BuildConfig.SELF_UPDATE=false`).
  Never move the install permission back into `app/src/main/AndroidManifest.xml`
  — it would make the Play build policy-noncompliant.
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

### Modification 013

Date: 2026-10-19

### Changes
- **Fixed: sign-in emails never reached `public.user_emails` (v1.4.1).**
  Root cause was a PostgreSQL privilege rule, not the schema or network:
  `SupabaseApi.insertUserEmail` sent `?on_conflict=email` +
  `Prefer: resolution=ignore-duplicates`, i.e. `INSERT ... ON CONFLICT (email)
  DO NOTHING`. PostgreSQL requires **SELECT** privilege on the relation (and a
  matching SELECT RLS policy) for any `ON CONFLICT` clause with an arbiter
  - but `user_emails` is deliberately INSERT-only (the lead list must stay
  unreadable through the public anon key), so **every insert failed with
  HTTP 401 / `42501 permission denied`** and the row was never written.
  `access_logs` was unaffected because it uses a plain INSERT.
- **Fix:** `insertUserEmail` is now a plain `POST user_emails` with
  `Prefer: return=minimal` (no `on_conflict`, no `resolution=*`). A repeat
  email returns **HTTP 409** (`23505` unique violation), which
  `UserEmailRepository.push()` already treated as "already synced";
  `UNIQUE(email)` remains the dedupe mechanism. No schema change, no SELECT
  grant - the security model is unchanged.
- `push()` now logs the HTTP code on failure
  (`Email sync failed (401): ...`) for easier diagnosis, and its `onFailure`
  branch resolves `httpCode` via `(error as? RemoteException)?.httpCode`.
- SQL comments in `sql/migrations/003_user_emails.sql` updated: dedupe is
  UNIQUE + HTTP 409, and the file now explicitly documents why `on_conflict`
  must never be sent.
- Bumped `versionCode` 11 → 12 and `versionName` 1.4.0 → **1.4.1**.

### Files / Components
- `data/remote/SupabaseApi.kt`, `data/repo/UserEmailRepository.kt`
- `sql/migrations/003_user_emails.sql` (comments only)
- `app/build.gradle.kts`, `medial_support.txt`, `AGENTS.md`, `AGENTS_PLAN.md`

### Important Notes
- **Verified live against the Supabase project (both directions):**
  - Pre-fix `POST /rest/v1/user_emails?on_conflict=email` → HTTP 401
    `42501 permission denied for table user_emails` (hint asks for
    `GRANT SELECT` - exactly the arbiter requirement). Nothing written.
  - Fixed `POST /rest/v1/user_emails` (plain) → **HTTP 201**, and the same
    body again → **HTTP 409** `duplicate key value violates unique
    constraint "user_emails_email_key"`.
  - One test row (`dev-fix-check@example.com`) was created by this
    verification and can be deleted in the Supabase table editor.
  - `GET /rest/v1/user_emails` still returns 401 for anon (no SELECT leak);
    `GET /rest/v1/category_types` returns 200 (app path healthy).
- **Migration 003 is already applied** on the live DB (the 401 - not a 404 -
  proves the table exists). The Mod 012 note about "run migration 003 /
  insert returns 400" is superseded by this entry.
- **Existing installs need the 1.4.1 APK.** Devices that typed an email on
  1.4.0 still hold it locally with `emailSynced=false`, so it syncs
  automatically at the first launch after the in-app update - no data lost.
- Rejected alternative: `GRANT SELECT` to `anon` (with or without a SELECT
  policy) would make `on_conflict` work but expose the whole lead list from
  the APK; a SECURITY DEFINER RPC would not help already-shipped builds.
- Unit tests 27/27 green; all source files remain under 300 lines.

---

### Modification 014

Date: 2026-10-19

### Changes
- **Business details on the PDF (v1.5.0).** New drawer destination
  "Business Details" where the user enters their own **business name,
  tagline, phone, email and address** once; every PDF quote then prints
  those details instead of the built-in "Hire Artist Studio" branding, and
  the drawer header shows the user's business name.
- **New components:**
  - `domain/BusinessProfile.kt` — pure model with `displayName` /
    `displayTagline` (blank fields fall back to `CompanyInfo`) and
    `contactLine` (non-blank phone/email/address joined with " • ", null
    when all blank); max-length constants for validation.
  - `util/BusinessStore.kt` — app-private SharedPreferences persistence
    (same pattern as `SessionStore`; `allowBackup=false` so it stays on
    the device).
  - `ui/business/{BusinessScreen,BusinessViewModel}.kt` — form with the 5
    fields, per-field validation (email via `EmailValidation`, length caps)
    and Save; saving pops back to the drawer.
- **PDF (`InvoiceSheet`):** `render(quote, business)` — the masthead studio
  name, the banner sub-title and the footer © line all use the profile's
  display values (auto-shrunk/ellipsized as before); a new centred
  **contact line** (up to 2 wrapped lines, `needs()` grows the footer
  reservation) appears under the thank-you line whenever any contact field
  is filled. `PdfInvoiceGenerator.generate` loads the profile via
  `BusinessStore`.
- **Navigation:** `Dest.BUSINESS` (added to `topLevelRoutes`); `AppDrawer`
  takes a `businessName` parameter (hardcoded studio name removed) and
  gained a "Business Details" entry (`Icons.Filled.Store`);
  `AppNav` re-reads `businessStore.load().displayName` per route change so
  the drawer shows the latest saved name after returning from the screen.
- **Cleanup:** removed the dead `CompanyInfo.PHONE` / `EMAIL` / `ADDRESS`
  constants (never referenced; superseded by user-entered details).
- Bumped `versionCode` 12 → 13 and `versionName` 1.4.1 → **1.5.0**.
- Added `BusinessProfileTest` (5 tests); total unit tests now 32.

### Files / Components
- `domain/BusinessProfile.kt` (new), `util/BusinessStore.kt` (new),
  `util/CompanyInfo.kt` (trimmed to NAME + TAGLINE)
- `util/{InvoiceSheet,PdfInvoiceGenerator}.kt`
- `ui/business/{BusinessScreen,BusinessViewModel}.kt` (new)
- `ui/navigation/{Dest,AppNav,AppDrawer}.kt`, `di/AppContainer.kt`
- `app/build.gradle.kts`, `app/src/test/.../domain/BusinessProfileTest.kt`
- `AGENTS.md`, `AGENTS_PLAN.md`, `medial_support.txt`

### Important Notes
- **Storage is device-local only** (decision): the profile is not sent to
  Supabase; it lives in app-private SharedPreferences and only leaves the
  device inside PDFs the user explicitly shares/saves.
- Blank name/tagline keep the built-in "Hire Artist Studio" branding, so
  existing behaviour is unchanged until the user opts in.
- Contact line layout: `PdfTheme.wrap(..., maxLines = 2)` and a
  `needs(50 + lines*16)` footer reservation; `centered()` ellipsizes as a
  final guard, so long values can never overflow the page.
- Audit (rule 21): no secrets, no network writes, no new permissions; input
  length-capped (name 60, tagline 80, phone 30, email 320 via
  `EmailValidation`, address 100); all files under 300 lines; 32/32 unit
  tests green; `assembleDebug` green.
- **Released as v1.5.0**: commit `b8edd6b`, tag `v1.5.0` pushed, GitHub release
  published with the stable asset name `Package-Price-Calculator.apk`
  (signed by the publisher key `CN=Hire Artist Studio`, SHA-256
  `F5:C6:C7:3F…93:EC:87`). Anonymous download verified: HTTP 200,
  `application/vnd.android.package-archive`, 1,864,253 bytes. `versionCode` 13.

---

### Modification 015

Date: 2026-10-20

### Changes
- **Business details now collected on the login gate (v1.6.0).** The separate
  "Business Details" drawer screen is gone: the gate asks for the **email**
  (existing, no password), the **business name** (required, max 60) and an
  optional **phone** (max 30). Tagline and address are dropped entirely.
- **The sign-in email is the business contact email** (decision): one email
  field signs the user in and is printed in the PDF footer contact line.
  `LoginViewModel` writes the profile to `BusinessStore` *before* the network
  call, so details are never lost offline.
- `domain/BusinessProfile` reduced to `name`, `phone`, `email`
  (`displayName` still falls back to `CompanyInfo.NAME`; `contactLine` now
  joins phone • email). `displayTagline`, `MAX_TAGLINE`, `MAX_ADDRESS` removed;
  the PDF package banner always prints `CompanyInfo.TAGLINE`.
- **New-package PDF meta block** (`InvoiceSheet.drawMeta`) renders the
  label and value **on one line** for Date, Invoice #, Expires On (and
  Client): each field is clamped to its own column (`rightColumn - 14f` /
  `CONTENT_RIGHT`) so a long value can never overlap the right column.
- `LoginScreen`: gate mode shows the two extra fields with focus
  Next/Next/Done handling and updated copy; change-email mode is unchanged.
  A drawer email change keeps the profile email in sync (name/phone preserved).
- **One-time local backfill** (`AppContainer.backfillBusinessEmail`, called from
  `PackCalcApp`): installs that completed the gate before v1.6.0 copy their
  remembered email into the profile so quotes keep their contact line.
- Removed `ui/business/` (`BusinessScreen`, `BusinessViewModel`),
  `Dest.BUSINESS` (and from `topLevelRoutes`), the drawer entry and its
  `Icons.Filled.Store` import, and the `AppNav` business route/imports.
- `BusinessProfileTest` updated to the new shape (4 tests); unit tests 31/31.
- Bumped `versionCode` 13 → 14 and `versionName` 1.5.0 → **1.6.0**.

### Files / Components
- `domain/BusinessProfile.kt`, `util/BusinessStore.kt`, `util/CompanyInfo.kt`
- `ui/login/{LoginScreen,LoginViewModel,LoginDestination}.kt`
- `ui/navigation/{Dest,AppNav,AppDrawer}.kt`, `ui/business/` (deleted)
- `util/InvoiceSheet.kt` (inline meta + default tagline), `di/AppContainer.kt`,
  `PackCalcApp.kt`
- `app/build.gradle.kts`, `app/src/test/.../domain/BusinessProfileTest.kt`
- `AGENTS.md`, `AGENTS_PLAN.md`, `medial_support.txt`

### Important Notes
- **Storage is device-local only** (unchanged): the profile never goes to
  Supabase; it lives in app-private SharedPreferences and leaves the device
  only inside PDFs the user shares/saves. Business name/phone share the same
  prefs file as before, so a v1.5.0 install keeps its saved name/phone.
- Audit (rule 21): no new permissions, no new network calls, no secrets;
  login input stays length-capped and the email is validated
  (`EmailValidation`); the business name is required so the gate cannot be
  cleared with an empty quote header; all source files remain under 300 lines;
  `assembleDebug` + `testDebugUnitTest` (31/31) + `assembleRelease` green.
- Signed release APK staged at `build/Package-Price-Calculator.apk`
  (versionCode 14, versionName 1.6.0, publisher key `CN=Hire Artist Studio`,
  SHA-256 `F5:C6:C7:3F…93:EC:87`).
- **Released as v1.6.0**: commit `9d00537`, annotated tag `v1.6.0` pushed,
  GitHub release published with the stable asset name
  `Package-Price-Calculator.apk`. Anonymous download verified: HTTP 200,
  `application/vnd.android.package-archive`, 1,864,253 bytes; the GitHub
  Releases API reports `v1.6.0` as latest, so in-app updates resolve.
- **Not rendered on a device** (no device/emulator attached): the inline
  meta rows are geometry-verified only; still to be eyeballed by the user.

---

### Modification 016

Date: 2026-10-20

### Changes
- **Business details are editable again (v1.6.1).** v1.6.0 hid the business
  name/phone behind the one-time gate, so an install that had already signed in
  had no way to change them. The drawer route (email row → "Tap to edit
  details") now shows the same three fields as the gate.
- `LoginViewModel` pre-fills email / business name / phone from `SessionStore` +
  `BusinessStore` in Change mode, validates them with the same rules as the
  gate (name required ≤ 60, phone ≤ 30, valid email) and always writes the
  profile locally before any network call. The email is only pushed when it is
  actually new, so a details-only edit never hits the server.
- `LoginScreen`: email / business name / phone render in **both** modes (the
  email field is `ImeAction.Next`, phone is `Done` → submit); Change mode
  heading "Your details", top bar "Business details", button "Save details".
- Drawer hint changed from "Tap to change email" to "Tap to edit details".
- Bumped `versionCode` 14 → 15 and `versionName` 1.6.0 → **1.6.1**.

### Files / Components
- `ui/login/{LoginScreen,LoginViewModel}.kt`, `ui/navigation/AppDrawer.kt`
- `app/build.gradle.kts`, `AGENTS.md`, `AGENTS_PLAN.md`, `medial_support.txt`

### Important Notes
- Audit (rule 21): no new permissions, no new network calls, no secrets; input
  validation is unchanged and now enforced in both modes, so the profile can
  never be cleared to an empty business name; the "email unchanged" shortcut
  means editing name/phone works fully offline.
- 31/31 unit tests green; `assembleDebug` + `assembleRelease` green; all source
  files remain under 300 lines.
- Signed release APK staged at `build/Package-Price-Calculator.apk`
  (versionCode 15, versionName 1.6.1, publisher key `CN=Hire Artist Studio`,
  SHA-256 `F5:C6:C7:3F…93:EC:87`).
- **Released as v1.6.1**: commit `a32cb2d`, annotated tag `v1.6.1` pushed,
  GitHub release published with the stable asset name
  `Package-Price-Calculator.apk`. Anonymous download verified: HTTP 200,
  `application/vnd.android.package-archive`, 1,864,253 bytes; the GitHub
  Releases API reports `v1.6.1` as latest, so in-app updates resolve.

---

### Modification 017

Date: 2026-10-20

### Changes
- **App icon now matches the `media/` icon (v1.6.2).** The launcher icon was a
  red `#D84830` square with a white Material camera glyph, while
  `media/icons/icon-512x512.png` (inherited from the old Expo app) was a
  completely different blue/teal tile. Per decision the **blue media artwork is
  now the app's icon**:
  - Added `app/src/main/res/drawable-nodpi/ic_launcher_art.png` (byte-for-byte
    copy of `media/icons/icon-512x512.png`, 512×512) as the single art source.
  - `mipmap-anydpi-v26/ic_launcher.xml` + `ic_launcher_round.xml`:
    `android:foreground` → `@drawable/ic_launcher_art` (full-bleed, so it
    renders correctly whether the platform bounds the layer to the 108dp canvas
    or to the intrinsic size; the launcher mask crops a clean gradient circle
    with the white mark inside the safe zone).
  - Deleted the now-unused `drawable/ic_launcher_foreground.xml` (white camera
    vector).
  - `values/colors.xml`: `ic_launcher_background` `#D84830` → **`#0E4F6B`**
    (sampled from the art's own edge colour, so it only shows under mask/parallax
    edges). `brand_red` and the whole UI accent stay warm red-orange by decision.
- **Login logo follows the icon.** `LoginScreen.LoginLogo()` no longer draws a
  red circle + white camera; it renders `ic_launcher_art` at 96dp clipped to a
  circle. Removed the `Box` / `background` / `requiredSize` imports it used.
- Bumped `versionCode` 15 → 16 and `versionName` 1.6.1 → **1.6.2**.

### Files / Components
- `app/src/main/res/drawable-nodpi/ic_launcher_art.png` (new)
- `app/src/main/res/mipmap-anydpi-v26/{ic_launcher,ic_launcher_round}.xml`
- `app/src/main/res/drawable/ic_launcher_foreground.xml` (deleted)
- `app/src/main/res/values/colors.xml`
- `app/src/main/java/lk/hiresmcq/packcalc/ui/login/LoginScreen.kt`
- `app/build.gradle.kts`, `medial_support.txt`, `AGENTS_PLAN.md`

### Important Notes
- `media/` was **not** modified - it is the source of truth and already carries
  the required 512x512 + 114x114 sizes.
- Verified in the built APKs (`aapt2 dump`): `color/ic_launcher_background` =
  `#ff0e4f6b`, `drawable/ic_launcher_art` = nodpi 512x512 PNG, both adaptive
  icon XMLs point at background `0x7f020005` / foreground `0x7f040006`, no
  `ic_launcher_foreground` entry remains, `versionCode='16' versionName='1.6.2'`.
- Release APK signed by the publisher key (`CN=Hire Artist Studio`, SHA-256
  `F5:C6:C7:3F…93:EC:87`) - same key as v1.0.0-v1.6.1, so in-app updates
  install cleanly.
- Unit tests 31/31 green; `assembleDebug` + `assembleRelease` green; all source
  files remain under 300 lines.
- **Not rendered on a device** (no device/emulator attached): the icon geometry
  was verified analytically and from the APK's compiled resources only - still
  to be eyeballed by the user. Some launchers cache the old icon until the
  launcher restarts after the update.
- No `<monochrome>` layer added (Android 13 themed icons): the art is opaque, so
  a themed icon would render as a solid blob.
- **Released as v1.6.2**: commit `ae0169a`, annotated tag `v1.6.2` pushed,
  GitHub release published with the stable asset name
  `Package-Price-Calculator.apk`. Anonymous download verified: HTTP 200,
  `application/vnd.android.package-archive`, 1,907,429 bytes; the GitHub
  Releases API reports `v1.6.2` as latest, so in-app updates resolve.

---

### Modification 018

Date: 2026-10-20

### Changes
- **Facebook promotional banner images (marketing assets, v1.6.2).** Three
  ready-to-post images added under `media/promo/`:
  - `facebook-banner-1200x630.png` - link / share image
  - `facebook-post-1080x1080.png` - feed post image
  - `facebook-story-1080x1920.png` - story / reel cover
  All three use the current blue/teal app icon (v1.6.2) plus app screenshots and
  a mini PDF-quote card, in the app's warm red / cream / amber / green palette.
- **Four app screen PNGs (1080x2400)** added under `media/images/screens/`
  (`screen-login`, `screen-details`, `screen-equipment`, `screen-pricing`).
- **How they were made (decision):** an emulator run was attempted first, but the
  Android Emulator hypervisor driver is not installed on this machine (installing
  it needs admin rights and a reboot). The screens were therefore produced as
  **faithful HTML/CSS recreations of the Compose UI** - exact colours from
  `ui/theme/Color.kt`, exact labels and copy from `LoginScreen`,
  `CalculatorScreen` (+ tabs) and `CalculatorBottomBar` - captured with headless
  Edge at 3x device scale. The banners are HTML too, screenshotted at the exact
  Facebook sizes with headless Edge (system Edge, **no npm packages or downloads**).
- Editable sources committed under `media/promo/src/` (screen HTML + banner HTML +
  shared CSS, relative asset paths) so the images can be regenerated:
  `msedge --headless=new --screenshot=out.png --window-size=WxH [--force-device-scale-factor=3] file:///.../media/promo/src/<name>.html`
  (screens are captured at `--window-size=360,800 --force-device-scale-factor=3`).
- `medial_support.txt`: section 4 now lists the ready-made promo images.

### Files / Components
- `media/promo/facebook-banner-1200x630.png`, `media/promo/facebook-post-1080x1080.png`,
  `media/promo/facebook-story-1080x1920.png`
- `media/promo/src/{login,details,equipment,pricing,banner-1200x630,banner-1080x1080,banner-1080x1920}.html`
  + `base.css` + `banner-base.css`
- `media/images/screens/screen-{login,details,equipment,pricing}.png`
- `medial_support.txt`

### Important Notes
- **No app code changed** - marketing assets only, so no rebuild or re-release
  was required (rule 4 not triggered); v1.6.2 remains the current release.
- The screen PNGs are recreations, not on-device captures: labels, colours and
  layout mirror the real UI, but the font is Segoe UI (Roboto is not installed
  on this machine) and system chrome (status-bar icons) is approximated. For true
  device captures later: install the Android Emulator hypervisor driver, boot the
  existing `VortexTest` AVD (Android 34, 1080x2400 @420dpi), then
  `adb install` the APK and `adb exec-out screencap -p > screen.png`.
- Sample identities in the images (Lens Lab Studio, lenslab.lk@gmail.com,
  +94 77 123 4567, Amila & Nuwan) are fictional placeholders.
- Audit (rule 21): assets only - no code, permissions, network calls or secrets;
  nothing personal or sensitive embedded.

---

### Modification 019

Date: 2026-10-20

### Changes
- **Google Play preparation (v1.6.3).** Two blockers found against current Play
  policy and fixed, plus all submission assets created:
  1. **`REQUEST_INSTALL_PACKAGES` removed from the Play build.** Play reserves
     that permission for apps whose core purpose is installing APKs, so a
     Play build with the self-updater would be rejected. Added a
     `distribution` flavor dimension: **`github`** (self-updater +
     permission, current behavior) and **`play`** (no updater, no permission,
     `BuildConfig.SELF_UPDATE=false`). The permission moved to
     `app/src/github/AndroidManifest.xml`; `AppNav` only constructs
     `UpdateViewModel` / renders `UpdateDialog` when `SELF_UPDATE` is true,
     so the Play build never even calls the GitHub API.
  2. **targetSdk 35 → 36.** Since 2026-08-31 Google Play requires new apps to
     target Android 16 (API 36); `compileSdk`/`targetSdk` bumped to 36
     (platform android-36 + build-tools 36.0.0 were already in the SDK).
     AGP 8.7.3 builds against 36 with a warning-only notice, silenced with
     `android.suppressUnsupportedCompileSdk=36` in `gradle.properties`.
     All variants build; 31/31 unit tests green; merged manifest verified
     (`playRelease`: targetSdk 36, NO install permission; `githubRelease`:
     keeps it).
  3. **Play-compliant screenshots.** Play caps screenshot aspect ratio at
     2:1; the Mod 018 screens are 1080x2400 (2.22:1) and would be rejected.
     New presentation captures at **1080x1920 (16:9, 24-bit, no alpha)**:
     `media/images/play/{login,details,equipment,pricing}.png` — caption +
     the full screen scaled to fit (nothing cropped). Sources:
     `media/promo/src/play-*.html`. The 2400-tall screens stay for
     GitHub/Facebook use (Play's 2:1 rule applies only to Play uploads).
  4. **Feature graphic 1024x500**: `media/promo/feature-graphic-1024x500.png`
     (+ `src/feature-graphic-1024x500.html`), 24-bit.
  5. **Privacy policy** at `docs/privacy-policy.html` (+ `privacy-icon.png`)
     for GitHub Pages: collects only email + hashed device id, no ads/trackers,
     deletion requests via the repo's Issues tab.
  6. **`docs/play-console-checklist.md`** — paste-ready Play Console
     submission guide: store listing text (title 24/30, short 79/80, full
     1992/4000 chars, verified), Data Safety form answers, content-rating and
     target-audience guidance, signing/closed-testing steps, reviewer note
     (any valid email opens the gate — no password exists).
  7. Bumped `versionCode` 16 → 17, `versionName` 1.6.2 → **1.6.3**.

### Files / Components
- `app/build.gradle.kts` (flavors, SDK 36, version), `gradle.properties`
- `app/src/main/AndroidManifest.xml` (permission removed),
  `app/src/github/AndroidManifest.xml` (new, permission added)
- `ui/navigation/AppNav.kt` (updater gated on `BuildConfig.SELF_UPDATE`)
- `docs/privacy-policy.html`, `docs/privacy-icon.png`, `docs/play-console-checklist.md`
- `media/promo/feature-graphic-1024x500.png`, `media/promo/src/feature-graphic-1024x500.html`
- `media/promo/src/play-{login,details,equipment,pricing}.html`
- `media/images/play/{login,details,equipment,pricing}.png`
- `media/promo/facebook-*.png` (re-rendered versions from Mod 018 session, valid)
- `AGENTS.md`, `AGENTS_PLAN.md`, `medial_support.txt`

### Important Notes
- Artifacts verified: github APK = `lk.hiresmcq.packcalc` v1.6.3 / code 17 /
  targetSdk 36, signed by the publisher key (`CN=Hire Artist Studio`, SHA-256
  `F5:C6:C7:3F…93:EC:87`), staged at `build/Package-Price-Calculator.apk`
  (1,907,437 bytes). Play AAB `app-play-release.aab` (4,240,614 bytes) signed
  with the same key (becomes the upload key in Play App Signing).
- The 512x512 store icon keeps its alpha channel — Play's spec explicitly
  wants a 32-bit PNG **with alpha**; no flattening needed.
- targetSdk 36 runtime impact is nil here: the app never locked orientation,
  Compose handles predictive back, and edge-to-edge was already in effect at
  target 35.
- Audit (rule 21): the play flavor makes no GitHub network calls and declares
  no install permission (verified in the merged manifest); no new secrets,
  no new permissions, no third-party SDKs; store listing texts and Data
  Safety answers match what the code actually collects (email, hashed device
  id); all source files remain under 300 lines; 31/31 unit tests green.
- **Not done here (console-side):** Play Console account steps, Data Safety
  form entry, AAB upload, Play App Signing enrollment, closed testing —
  follow `docs/play-console-checklist.md`.

---

### Modification 020

Date: 2026-10-20

### Changes
- **Play "Delete data URL" page (docs-only, no app code, no rebuild).**
  Google Play requires a dedicated, prominent URL where users can request
  data deletion. Created `docs/data-deletion.html` (served by GitHub Pages
  from `docs/`) which satisfies all three Play requirements: it names the
  app + developer, gives numbered request steps with a **prefilled GitHub
  Issue link** (`issues/new?title=Privacy data request — delete my data`),
  and spells out what is deleted (sign-in email + usage records; **no
  retention** after the 30-day window) versus what is device-local only
  (quotes/catalog/business details → Settings → Clear storage or uninstall).
  A public-Issues privacy caveat points users to the email channel for
  private requests.
- `docs/privacy-policy.html`: section 7 now cross-links the deletion page;
  section 1c dropped the stale "tagline… address" wording (those fields
  were removed from the app in Mod 015 / v1.6.0).
- `docs/play-console-checklist.md`: new "Data deletion" block with the
  paste-ready URL
  (`https://chamarawickramarathne-spec.github.io/package-price-calculator/data-deletion.html`),
  the "Yes" answer for the deletion-functionality question, and the Data
  Safety table's deletion column now points at the deletion page.

### Files / Components
- `docs/data-deletion.html` (new)
- `docs/privacy-policy.html`, `docs/play-console-checklist.md`

### Important Notes
- No application code changed → **no rebuild, no version bump, no new
  release** (v1.6.3 / versionCode 17 remains current; same rule-4 exemption
  as Mod 018).
- Both pages rely on GitHub Pages on the repo (`main` branch, `/docs` folder);
  Pages was confirmed enabled during Mod 020 and both URLs verified live
  (HTTP 200): the privacy policy and
  `…/package-price-calculator/data-deletion.html`.

---

### Modification 021

Date: 2026-10-22

### Changes
- **On-device crash reporting (v1.6.4)** — the Google Play build was reported
  to close instantly on launch while the GitHub APK worked. No root cause
  could be proven from the build side (see the notes below), and there is no
  Play Console access and no attached device on this machine, so the fix is
  to make the crash **visible and reportable**:
  - `domain/CrashReport.kt` (new, pure) — a captured crash as data
    (`timestamp`, `threadName`, `exceptionClass`, `message`, `stackTrace`) plus
    `summary` and `asReportText(appVersion, channel, startupTrail)`. The
    report carries the **app version, the distribution channel
    (`BuildConfig.FLAVOR`), the thread, the full stack trace and the last
    startup steps**.
  - `util/CrashReporter.kt` (new) — installs a global
    `Thread.setDefaultUncaughtExceptionHandler` as the **first** statement of
    `Application.onCreate`. It writes the report to
    `filesDir/last_crash.txt` *synchronously* and then hands the throwable
    back to the previous (platform) handler, so the process still dies the
    way Android expects — the crash is never swallowed. Also exposes
    `pendingReportText()` / `clearPendingReport()` / `capture(throwable)`.
  - `util/StartupLog.kt` (new) — a trimmed, timestamped breadcrumb trail at
    `filesDir/startup.log` ("app.onCreate", "crash handler armed", "container
    ready", "activity.onCreate", "startup complete", …). It answers *where*
    the app died, which a stack trace cannot.
  - `ui/components/CrashSurface.kt` (new) — the report on screen: title,
    "Copy details" (clipboard) and "Carry on". `onContinue` is nullable so a
    start-up failure can be shown without offering a way forward.
  - `PackCalcApp` — arms the crash handler first, then builds `AppContainer`
    inside `runCatching`; every startup step (usage log, email sync, business
    backfill) is individually guarded so a failing background step can never
    stop the app. A container failure is kept in `startupFailure` instead of
    being swallowed.
  - `MainActivity` — on start it shows the stored report (or the
    `startupFailure`) on `CrashSurface` instead of opening the app; when the
    container itself failed, no "Carry on" is offered (there is nothing to
    carry on to).
  - Bumped `versionCode` 17 → 18 and `versionName` 1.6.3 → **1.6.4**.

### Files / Components
- `domain/CrashReport.kt` (new), `util/CrashReporter.kt` (new),
  `util/StartupLog.kt` (new), `ui/components/CrashSurface.kt` (new)
- `PackCalcApp.kt`, `MainActivity.kt`
- `app/src/test/java/lk/hiresmcq/packcalc/domain/CrashReportTest.kt` (new)
- `app/build.gradle.kts`, `medial_support.txt`, `AGENTS.md`, `AGENTS_PLAN.md`,
  `docs/play-console-checklist.md`

### Important Notes
- **Why not a "degraded container"?** The plan originally allowed a fallback
  container when `AppContainer` cannot be built. It was dropped on purpose:
  a half-working app hides the real error, whereas `startupFailure` +
  `CrashSurface` shows the exact exception. Nothing is silently swallowed.
- **A crash dialog inside the handler is not possible** — the main looper is
  not running while the uncaught handler executes, so a dialog shown there
  would never be drawn. The report is therefore written to disk and displayed
  on the **next** launch, which is the standard approach and the only
  reliable one.
- **What was ruled out (build side):** the `play` and `github` release builds
  are functionally identical — the merged manifests differ only by
  `REQUEST_INSTALL_PACKAGES` (github-only, intentional); the R8 class sets
  differ only by the self-update classes that the play flavor does not use;
  resources are identical; `minSdk 26` / `targetSdk 36` /
  `versionCode 17` in both. The 16 KB page-size requirement is met (every
  `PT_LOAD` segment of `libandroidx.graphics.path.so` has `p_align = 0x4000`)
  and that native library is not loaded at start-up (emoji2 1.3.0 never
  references `androidx.graphics.path`).
- **Audit (rule 21):** the report is written to **app-private** storage only
  (`filesDir`, `allowBackup=false`), contains no client data, no prices and
  no personal data beyond the app version and the crash text; it is never
  uploaded anywhere. No new permission, no new network call, no secret.
  `StartupLog` holds step labels only.
- 36/36 unit tests green (5 new `CrashReportTest`); `assembleGithubDebug`,
  `assemblePlayDebug`, `assembleGithubRelease`, `assemblePlayRelease` and
  `bundlePlayRelease` all green; every source file is still under 300 lines.
- Verified in the built artifacts: `app-play-release.apk` = versionCode 18 /
  1.6.4, **no** `REQUEST_INSTALL_PACKAGES`; the AAB's `base/dex/classes.dex`
  contains the new string literals (`last_crash.txt`, `startup.log`,
  "crash handler armed", "startup complete"), so the crash layer really is
  in the Play bundle even after R8; the AAB manifest has no
  `REQUEST_INSTALL_PACKAGES`. The GitHub APK is signed with the publisher
  key (`CN=Hire Artist Studio`, SHA-256 `F5:C6:C7:3F…93:EC:87`), the same
  key as v1.0.0–v1.6.3, so in-app updates install cleanly.
- **Released as v1.6.4**: commit `96a593c`, annotated tag `v1.6.4` pushed,
  GitHub release published with the stable asset name
  `Package-Price-Calculator.apk`. Anonymous download verified: HTTP 200,
  `application/vnd.android.package-archive`, 1,907,429 bytes; the GitHub
  Releases API reports `v1.6.4` as latest, so in-app updates resolve.
  The Play AAB (`app/build/outputs/bundle/playRelease/app-play-release.aab`,
  4,252,449 bytes) is ready for the console upload.

---

### Modification 022

Date: 2026-10-22

### Changes
- **Play upload artifact staged and verified (no app code change).** The
  Play Console AAB was rebuilt from scratch and copied to a stable location
  next to the GitHub APK so a Gradle clean can never wipe the upload file:
  - Rebuilt with `bundlePlayRelease` → **BUILD SUCCESSFUL** (exit 0), artifact
    byte-identical to the Mod 021 build (4,252,449 bytes).
  - Staged copy: **`build/Package-Price-Calculator.aab`** (SHA-256
    `794446BBEFD112C6B0E9864C6A7F21AC96003B799EBD020622565B942DA46863`,
    verified identical to the Gradle output).
- **Verification performed (read-only) on the AAB:**
  - `jarsigner -verify` → `jar verified.`; signer `CN=Hire Artist Studio,
    OU=PackCalc, O=Hire Artist Studio, L=Colombo, ST=Western, C=LK`
    (self-signed, valid 2026-10-16 → 2054-03-03) — this is the **upload key**
    for Play App Signing enrollment.
  - AAB structure valid: `BundleConfig.pb`, `base/manifest/AndroidManifest.xml`
    (proto), `base/dex/classes.dex`, `base/resources.pb`,
    `BUNDLE-METADATA/*`.
  - Merged manifest of the matching `playRelease` APK: `lk.hiresmcq.packcalc`,
    versionCode **18**, versionName **1.6.4**, minSdk 26, targetSdk 36,
    compileSdk 36.
  - Permissions in the AAB manifest: `android.permission.INTERNET` only (plus
    androidx's auto-generated `DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`);
    **no** `REQUEST_INSTALL_PACKAGES`, no storage/`QUERY_ALL_PACKAGES`/
    `MANAGE_EXTERNAL_STORAGE`/`POST_NOTIFICATIONS`.
- **`docs/play-console-checklist.md`:** section 1 now documents the staged
  upload path plus a verification table (size, SHA-256, package/version, SDK
  levels, permissions, signature); section 7 points the upload step at
  `build\Package-Price-Calculator.aab`.
- Git working tree was clean before the change and the only commits after
  v1.6.4 are documentation ones, so the AAB matches `HEAD` exactly.

### Files / Components
- `build/Package-Price-Calculator.aab` (new staged artifact; gitignored via
  `build/` and `*.aab`)
- `docs/play-console-checklist.md`, `AGENTS.md`, `AGENTS_PLAN.md`

### Important Notes
- **No app code changed** → no version bump, no rebuild of the GitHub channel,
  no new release (rule 4 exemption, same as Mod 018 / Mod 020).
- `medial_support.txt` was **not** regenerated: no feature, description, tag
  or prompt changed in this modification (the file already describes v1.6.4).
- Audit (rule 21): build/staging only — no source edit, no new dependency, no
  permission, no network call, no secret. The staged file is a byte copy of an
  already-verified artifact and is ignored by git (`build/`, `*.aab`).
- Play App Signing note (unchanged): the upload key is the publisher key
  `packcalc-release.jks`. Google then holds the app-signing key, so a device
  that installed the GitHub APK must uninstall before installing from Play
  (different signer) — accepted trade-off.
- **Still user-side:** the Play Console upload itself
  (Testing → Internal/Closed testing → Create new release → Upload), the Data
  Safety form, content rating, and — on a personal account — a closed test
  with 12 testers for 14 days before production access.

---

### Modification 023

Date: 2026-10-22

### Changes
- **Fixed the Google Play launch crash (v1.6.5).** The v1.6.4 Play build threw
  `java.lang.ClassCastException` inside `Application.onCreate` and closed
  instantly. The root cause was **found and proven** (not guessed):
  `NetworkModule.createGitHubApi` called `retrofit.create(GitHubApi::class.java)`,
  and in the Play flavor that result was **unused** (R8 removed the
  `githubApi` / `updateRepository` / `apkDownloader` fields because
  `BuildConfig.SELF_UPDATE=false`). R8 full mode sees **no subtypes** of a
  Retrofit service interface (the service is a dynamic proxy), so it replaced
  the Kotlin `check-cast` with an unconditional
  `new ClassCastException(); throw` — every Play launch died inside
  `AppContainer.<init>`.
  - **Fix:** the updater's network layer now lives only in the `github`
    flavor source set, so the Play build never creates a GitHub service at
    all. No R8 keep-rule, no `-dontwarn` trick, no behaviour change for the
    GitHub channel.
  - `app/src/github/java/lk/hiresmcq/packcalc/data/remote/UpdateNetwork.kt`
    (new) — `createGitHubApi` / `createUpdateClient` (moved out of the shared
    `NetworkModule`).
  - `app/src/github/java/lk/hiresmcq/packcalc/di/SelfUpdateWiring.kt` (new) —
    builds the GitHub service and keeps it in a typed field (which is what
    stops R8 from folding the cast away).
  - `app/src/play/java/lk/hiresmcq/packcalc/di/SelfUpdateWiring.kt` (new) —
    no service, no updater: `repository` / `downloader` are `null`.
  - `data/remote/NetworkModule.kt` — GitHub code removed; documents why the
    Supabase service result must stay in a typed field.
  - `di/AppContainer.kt` — `updateRepository` / `apkDownloader` are now
    nullable and sourced from `SelfUpdateWiring`.
  - `ui/update/UpdateHost.kt` (new) — hosts the updater; renders nothing when
    it is absent (play flavor).
  - `ui/navigation/AppNav.kt` — the `BuildConfig.SELF_UPDATE` block replaced by
    `UpdateHost(container, appContext)` (273 lines).
  - Bumped `versionCode` 18 → 19 and `versionName` 1.6.4 → **1.6.5**.

### Files / Components
- `app/src/github/java/lk/hiresmcq/packcalc/data/remote/UpdateNetwork.kt` (new)
- `app/src/github/java/lk/hiresmcq/packcalc/di/SelfUpdateWiring.kt` (new)
- `app/src/play/java/lk/hiresmcq/packcalc/di/SelfUpdateWiring.kt` (new)
- `app/src/main/java/lk/hiresmcq/packcalc/ui/update/UpdateHost.kt` (new)
- `app/src/main/java/lk/hiresmcq/packcalc/{data/remote/NetworkModule,di/AppContainer,ui/navigation/AppNav}.kt`
- `app/build.gradle.kts`, `AGENTS_PLAN.md`, `medial_support.txt`,
  `docs/play-console-checklist.md`

### Important Notes
- **Proof in bytecode** (`dexdump` on the shipped APKs, not inference):
  - v1.6.4 **play** (broken): `const-class v2, Lt2/a;` (GitHubApi) → create →
    `new-instance ClassCastException` → `throw v1` at 0x03a3.
  - v1.6.4 **github** (worked): `const-class v2, Lt2/b;` → create → **real**
    `check-cast v1, Lt2/b;` → stored in the typed field.
  - v1.6.5 **play** (fixed): `GitHubApi` is **absent from the entire build**
    (not in the R8 mapping); `AppContainer.<init>` keeps a **real**
    `check-cast v4, Lt2/c;` (SupabaseApi) that stores into the typed field
    `Lv2/a;.a`; no unconditional cast-failure throw remains.
  - v1.6.5 **github**: `SelfUpdateWiring.<clinit>` keeps a **real**
    `check-cast v0, Lt2/b;` → the in-app updater still works.
  - The two `ClassCastException` instructions still present inside
    `AppContainer.<init>` are byte-identical to the ones in the known-good
    GitHub build → pre-existing and benign.
- **Audit (rule 21):** the change adds no permission, no network call, no
  secret and no new dependency. The Play build now makes **no** GitHub API
  call at all and still declares only `INTERNET` (verified in the merged
  manifest of `app-play-release.apk`); the GitHub build keeps
  `REQUEST_INSTALL_PACKAGES`. The crash-reporting layer from Mod 021 is
  untouched and stays in both flavors, so any future start-up failure is still
  visible on screen on the next launch.
- 36/36 unit tests green on both flavors; `assemblePlayDebug`,
  `assembleGithubDebug`, `assemblePlayRelease`, `assembleGithubRelease` and
  `bundlePlayRelease` all green; every source file is still under 300 lines.
- Artifacts: GitHub APK (1,907,429 bytes) signed with the publisher key
  (`CN=Hire Artist Studio`, SHA-256 `F5:C6:C7:3F…93:EC:87` — the same key as
  v1.0.0–v1.6.4, so in-app updates install cleanly), staged at
  `build/Package-Price-Calculator.apk`; Play AAB (4,250,380 bytes, SHA-256
  `DAC8896DEF09E9DA67C151FE3AF6FA1641C45243803F48BD34FC5C020719F566`) staged
  at `build/Package-Price-Calculator.aab`.
- **Still user-side:** sideload `app-play-release.apk` once as a smoke test,
  then upload the AAB to Play Console (see `docs/play-console-checklist.md`).

---

## Remaining Work (Tracked in AGENTS_PLAN.md)

- **Google Play submission (user-side):** follow
  `docs/play-console-checklist.md` — create app, paste listing text, upload
  `build/Package-Price-Calculator.aab`, Data Safety, content rating, Play App
  Signing, closed testing (12 testers x 14 days on a personal account),
  production.
- **Install-test the exact Play artifact before uploading it** (new in
  v1.6.4): `app/build/outputs/apk/play/release/app-play-release.apk` is the
  same code the AAB ships — sideload it once and confirm it opens. If the
  Play build still closes on launch, the next launch now shows the crash
  report on screen (version, channel, stack trace, startup trail) — copy it
  and send it in.
- **Eyeball the new app icon on a device** (v1.6.2): launcher icon and the
  login-screen logo now come from `media/icons/icon-512x512.png` (blue/teal
  art); no device/emulator was attached to render it.
- **Eyeball the login gate + PDF on a device** (no device or emulator attached
  to this machine): gate collects name/phone → drawer shows the name, PDF
  footer shows phone • email; sign-in row lands in `user_emails` (airplane-mode
  sign-in lands at the next launch); the two-page PDF and the inline
  Date / Invoice # / Expires On rows render as designed.
- Supabase Auth for per-user ownership (hardening).
