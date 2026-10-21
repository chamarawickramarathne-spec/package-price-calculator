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
- [x] **Release 1.3.0 — usage logging (Mod 011):** append-only
      `public.access_logs` events (`app_open`, `pdf_export`) with a pseudonymous
      device hash; INSERT-only anon grant, no SELECT policy; `usage_reports.sql`
      for the studio; `UsageEventTest` + `DeviceHashTest` (19 tests total).
      (Released and published before the docs were updated - backfilled in
      Mod 012's log entry.)
- [x] **Release 1.4.0 — email sign-in gate (Mod 012):**
      - Mandatory one-time email screen before the main UI (no password);
        remembered on the device (SharedPreferences) and skipped afterwards.
      - Email recorded in new append-only `public.user_emails` table (anon
        INSERT-only, UNIQUE email, length CHECKs) for promoting other apps.
      - Offline-first: local save wins, server copy retried at each app open
        until it lands; drawer shows the email with a tap-to-change route.
      - `EmailValidationTest` added; unit tests now 27. versionCode 11.
      - Also removed the build log files accidentally committed in v1.3.0 and
        added `*.out` to `.gitignore`.
- [x] **Fix 1.4.1 — user_emails sync never landed (Mod 013):**
      - Root cause: `?on_conflict=email` (PostgREST ignore-duplicates) makes
        PostgreSQL require **SELECT** on the table, which anon does not have
        (the lead list is INSERT-only by design) → every insert died with
        HTTP 401 / `42501`.
      - `insertUserEmail` is now a plain INSERT (`Prefer: return=minimal`);
        duplicates surface as HTTP 409, which the repository already maps to
        "synced". No schema change; SELECT still denied (verified live).
      - Live probes: pre-fix POST → 401, fixed POST → 201, repeat → 409.
      - versionCode 12; 27/27 unit tests.

### Completed (1.4.1 follow-up)
- [x] ~~Run `sql/migrations/003_user_emails.sql`~~ — **verified applied** on
      the live DB (probe returns 401 `permission denied`, not 404), and the
      catalog seed loads (`category_types` → 200 with rows). No SQL run needed.
- [x] Publish/verify the **v1.4.1 GitHub release** - tag pushed, release
      published with `Package-Price-Calculator.apk`; anonymous download
      verified (HTTP 200, 1,864,253 bytes) so the in-app updater can fetch it
      and installed devices sync their pending emails on first launch.

### Completed — Feature 1.5.0 (Mod 014): business details on the PDF

- [x] `domain/BusinessProfile.kt` — pure model (name, tagline, phone, email,
      address) with `displayName` / `displayTagline` falling back to
      `CompanyInfo` and `contactLine` joining the non-blank contact fields
      (" • " separator, null when all blank).
- [x] `util/BusinessStore.kt` — SharedPreferences persistence (same pattern as
      `SessionStore`); exposed from `AppContainer`.
- [x] `ui/business/{BusinessScreen,BusinessViewModel}.kt` — drawer destination
      with the 5 fields + validation (email format via `EmailValidation`,
      max lengths); saved locally on Save.
- [x] PDF (`InvoiceSheet`): masthead name, banner tagline, footer © line use
      `BusinessProfile.displayName` / `displayTagline`; new contact line under
      the footer (wrapped, max 2 lines) when any contact field is filled.
- [x] Drawer header shows the user's business name instead of the hardcoded
      studio name; new "Business Details" drawer entry.
- [x] `BusinessProfileTest`; unit tests 32; versionCode 13, versionName 1.5.0.
- [x] Docs: AGENTS.md Mod 014, `medial_support.txt` regenerated.
- [x] Commit `b8edd6b` + tag `v1.5.0` pushed; GitHub release published with
      `Package-Price-Calculator.apk` (signed with the publisher key, SHA-256
      `F5:C6:C7:3F…93:EC:87`); anonymous download verified (HTTP 200,
      `application/vnd.android.package-archive`, 1,864,253 bytes) so in-app
      updates to 1.5.0 work.

### Completed — Feature 1.6.0 (Mod 015): business details on the login gate

- [x] Login gate now collects **email + business name (required) + phone
      (optional)**; tagline and address dropped.
- [x] `domain/BusinessProfile` reduced to `name` / `phone` / `email`; the
      sign-in email is the PDF contact email (`contactLine` = phone • email).
- [x] `LoginViewModel` writes the profile to `BusinessStore` before the network
      call (offline-safe); a drawer email change keeps the profile email in
      sync and preserves name/phone.
- [x] PDF package banner always prints `CompanyInfo.TAGLINE` (tagline field
      removed).
- [x] `InvoiceSheet.drawMeta`: Date / Invoice # / Expires On (/ Client) render
      **label + value on one line**, each clamped to its own column so a long
      value cannot run under the adjacent column.
- [x] Removed `ui/business/`, `Dest.BUSINESS`, the drawer entry and the
      `AppNav` route/imports.
- [x] One-time local backfill (`AppContainer.backfillBusinessEmail`): installs
      that passed the gate before v1.6.0 copy their remembered email into the
      profile so quotes keep their footer contact line.
- [x] `BusinessProfileTest` updated (4 tests); **31/31** unit tests green;
      `assembleDebug` + `assembleRelease` green.
- [x] versionCode 14, versionName 1.6.0; signed APK staged at
      `build/Package-Price-Calculator.apk` (publisher key, SHA-256
      `F5:C6:C7:3F…93:EC:87`).
- [x] Docs: AGENTS.md Mod 015, `medial_support.txt` regenerated.

### Completed (1.6.0 release)
- [x] **Release 1.6.0**: commit `9d00537`, annotated tag `v1.6.0` pushed,
      GitHub release published with the stable asset
      `Package-Price-Calculator.apk`; anonymous download verified (HTTP 200,
      `application/vnd.android.package-archive`, 1,864,253 bytes) and the
      Releases API reports `v1.6.0` as latest, so in-app updates work.

### Completed — Fix 1.6.1 (Mod 016): business details editable from the drawer

- [x] The drawer's edit route now shows **email + business name + phone**
      (pre-filled), so an install that already passed the gate can change its
      details; both modes validate and save the same way.
- [x] Details-only edits save locally without touching the server; a changed
      email is still pushed (`register`) with the offline retry.
- [x] Drawer hint → "Tap to edit details"; Change-mode headings/button updated.
- [x] 31/31 unit tests green; `assembleDebug` + `assembleRelease` green;
      versionCode 15, versionName 1.6.1.

### Completed (1.6.1 release)
- [x] **Release 1.6.1**: commit `a32cb2d`, annotated tag `v1.6.1` pushed,
      GitHub release published with the stable asset
      `Package-Price-Calculator.apk`; anonymous download verified (HTTP 200,
      `application/vnd.android.package-archive`, 1,864,253 bytes) and the
      Releases API reports `v1.6.1` as latest, so in-app updates work.

### Completed — Icon fix 1.6.2 (Mod 017): app icon matches `media/` again

- [x] The launcher icon (red square + white Material camera) was replaced by
      the blue/teal art that already lives in `media/icons/icon-512x512.png`:
      `drawable-nodpi/ic_launcher_art.png` added and referenced as the
      adaptive-icon **foreground** in both `ic_launcher.xml` and
      `ic_launcher_round.xml` (full-bleed).
- [x] `drawable/ic_launcher_foreground.xml` (white camera vector) deleted.
- [x] `ic_launcher_background` `#D84830` → `#0E4F6B` (sampled from the art's
      edge). UI accent stays warm red-orange by decision.
- [x] `LoginScreen.LoginLogo()` now draws `ic_launcher_art` (96dp, circle
      clipped) instead of a red circle + camera.
- [x] versionCode 16, versionName 1.6.2; 31/31 unit tests green;
      `assembleDebug` + `assembleRelease` green.
- [x] APK verified with `aapt2 dump`: background `#ff0e4f6b`, art nodpi 512x512,
      both adaptive XMLs wired to it, old vector gone; signed by the publisher
      key (SHA-256 `F5:C6:C7:3F…93:EC:87`).
- [x] Signed APK staged at `build/Package-Price-Calculator.apk`
      (1,907,429 bytes); `medial_support.txt` updated for 1.6.2.

### Completed (1.6.2 release)
- [x] **Release 1.6.2**: commit `ae0169a`, annotated tag `v1.6.2` pushed,
      GitHub release published with the stable asset
      `Package-Price-Calculator.apk`; anonymous download verified (HTTP 200,
      `application/vnd.android.package-archive`, 1,907,429 bytes) and the
      Releases API reports `v1.6.2` as latest, so in-app updates work.

### Completed — Promo assets (Mod 018): Facebook banners + app screens

- [x] Attempted a real emulator capture first; the Android Emulator hypervisor
      driver is not installed on this machine (admin + reboot required), so the
      screens were produced as faithful HTML/CSS recreations of the Compose UI
      (exact colours from `Color.kt`, exact labels from the screens) and
      captured with headless Edge at 3x scale - no packages downloaded.
- [x] `media/images/screens/screen-{login,details,equipment,pricing}.png`
      (1080x2400 each).
- [x] `media/promo/facebook-banner-1200x630.png` (link/share),
      `media/promo/facebook-post-1080x1080.png` (feed),
      `media/promo/facebook-story-1080x1920.png` (story) - blue v1.6.2 icon,
      screen shots, mini PDF-quote card, warm red/cream/amber/green palette.
- [x] Sources committed in `media/promo/src/` (relative asset paths) so both the
      screens and the banners can be re-rendered from the repo.
- [x] `medial_support.txt` section 4 lists the ready-made images.
- [x] No app code changed - no rebuild/re-release (v1.6.2 stays current).

### Completed — Google Play preparation 1.6.3 (Mod 019)

- [x] **Flavors:** `distribution` dimension — `github` (self-updater,
      `REQUEST_INSTALL_PACKAGES` via `app/src/github/AndroidManifest.xml`) and
      `play` (no permission, `BuildConfig.SELF_UPDATE=false`; `AppNav` never
      constructs `UpdateViewModel`, so no GitHub API calls either).
- [x] **targetSdk 35 → 36** (Play requires API 36 for new apps since
      2026-08-31); `android.suppressUnsupportedCompileSdk=36` added (AGP 8.7.3
      warning only). All variants build; merged manifests verified.
- [x] **Privacy policy** `docs/privacy-policy.html` (+ `privacy-icon.png`) —
      email + hashed device id disclosures, no-ads/no-trackers statement,
      web-based data deletion via the repo Issues tab.
- [x] **Play-compliant screenshots** `media/images/play/*.png`
      (1080x1920, 24-bit, ratio 1.78) because Play rejects >2:1 (the old
      1080x2400 screens); sources `media/promo/src/play-*.html`.
- [x] **Feature graphic** `media/promo/feature-graphic-1024x500.png` (24-bit).
- [x] **Store listing text + Data Safety answers** in
      `docs/play-console-checklist.md` (title 24/30, short 79/80, full
      1992/4000 — all verified programmatically).
- [x] 31/31 unit tests green on all six test variants.
- [x] versionCode 17, versionName 1.6.3; github APK (1,907,437 bytes) staged
      at `build/Package-Price-Calculator.apk`; Play AAB
      `app-play-release.aab` (4,240,614 bytes); both signed with the
      publisher key (SHA-256 `F5:C6:C7:3F…93:EC:87`).

### Completed — Play data-deletion page (Mod 020)

- [x] `docs/data-deletion.html` (new) — Play "Delete data URL" target: names
      app + developer, numbered request steps with a prefilled GitHub Issue
      link, deleted-vs-kept table (email + usage records deleted, no retention;
      app content is device-local), 30-day response, email alternative,
      cross-links the privacy policy.
- [x] `docs/privacy-policy.html` — section 7 cross-links the deletion page;
      section 1c fixed (dropped tagline/address removed in v1.6.0).
- [x] `docs/play-console-checklist.md` — paste-ready Delete data URL, the
      "Yes" deletion answer, Data Safety deletion column updated.
- [x] Docs-only change — **no rebuild, no release**; v1.6.3 stays current.

### Completed — Crash diagnostics 1.6.4 (Mod 021)

- [x] **Reported symptom:** the Google Play build closes instantly on launch
      while the GitHub APK works. No root cause could be proven from the build
      side and there is no Play Console access and no attached device here, so
      the fix is to make the crash **visible on the phone**.
- [x] `domain/CrashReport.kt` (new, pure) — a captured crash as data plus
      `summary` / `asReportText(appVersion, channel, startupTrail)`.
      `CrashReportTest` (5 tests) covers the format.
- [x] `util/CrashReporter.kt` (new) — global
      `Thread.setDefaultUncaughtExceptionHandler`, armed as the first
      statement of `Application.onCreate`; writes the report synchronously to
      `filesDir/last_crash.txt` and then hands the throwable back to the
      platform handler (the crash is never swallowed).
- [x] `util/StartupLog.kt` (new) — trimmed, timestamped breadcrumb trail at
      `filesDir/startup.log` (app.onCreate → crash handler armed → container
      ready → activity.onCreate → startup complete).
- [x] `ui/components/CrashSurface.kt` (new) — the report on screen with
      "Copy details" and "Carry on"; the carry-on button is optional so a
      failed start-up shows no way forward.
- [x] `PackCalcApp` — crash handler armed first, `AppContainer` built inside
      `runCatching` (failure kept in `startupFailure`), every startup step
      individually guarded.
- [x] `MainActivity` — shows the stored report (or the start-up failure)
      instead of opening the app.
- [x] 36/36 unit tests green; `assembleGithubDebug`, `assemblePlayDebug`,
      `assembleGithubRelease`, `assemblePlayRelease`, `bundlePlayRelease` all
      green; every source file under 300 lines.
- [x] versionCode 18, versionName 1.6.4. GitHub APK (1,907,429 bytes) signed
      with the publisher key (SHA-256 `F5:C6:C7:3F…93:EC:87`) and staged at
      `build/Package-Price-Calculator.apk`; Play AAB
      `app-play-release.aab` (4,252,449 bytes) + sideloadable
      `app-play-release.apk`. Verified in the AAB dex: `last_crash.txt`,
      `startup.log`, "crash handler armed", "startup complete" all present
      after R8; the AAB manifest has no `REQUEST_INSTALL_PACKAGES`.
- [x] **Release 1.6.4**: commit `96a593c`, annotated tag `v1.6.4` pushed,
      GitHub release published with the stable asset
      `Package-Price-Calculator.apk`; anonymous download verified (HTTP 200,
      `application/vnd.android.package-archive`, 1,907,429 bytes) and the
      Releases API reports `v1.6.4` as latest, so in-app updates work.

### Pending — Play Console (user-side, follow docs/play-console-checklist.md)

- [ ] Create the app in Play Console + paste store listing text/assets.
- [ ] File the Data Safety form + content declarations.
- [ ] Upload `app-play-release.aab` to a testing track; enroll in Play App
      Signing (upload key = packcalc-release.jks).
- [x] Privacy policy URL live: GitHub Pages confirmed enabled on the repo
      (`main` / `/docs`) and both
      `…/privacy-policy.html` and `…/data-deletion.html` verified HTTP 200
      after the Mod 020 push.
- [ ] Personal account only: closed test with 12 testers x 14 days, then
      production access.

### Later
- [ ] **Sideload the exact Play artifact once before uploading it** (new in
      v1.6.4): `app/build/outputs/apk/play/release/app-play-release.apk` ships
      the same code as the AAB. If it still closes on launch, the next launch
      shows the crash report on screen (version, channel, stack trace, startup
      trail) — copy it and send it in.
- [ ] **Eyeball the new app icon on a device** (v1.6.2): launcher icon and the
      login-screen logo come from `media/icons/icon-512x512.png`; no
      device/emulator was attached to render it. Some launchers cache the old
      icon until the launcher restarts.
- [ ] **Eyeball the login gate + PDF on a device** (no device or emulator
      attached to this machine): gate collects name/phone → drawer shows the
      name, PDF footer shows phone • email; the sign-in row lands in
      `user_emails` (offline sign-in lands at the next launch); the two-page
      PDF and the inline Date / Invoice # / Expires On rows render as designed.
- [ ] Supabase Auth for real per-user accounts (hardening; the email gate is a
      soft identity, not authentication).

---

(C) 2026 Hire Artist Studio. AGENTS_PLAN versioned with AGENTS.md.
