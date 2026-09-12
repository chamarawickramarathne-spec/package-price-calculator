# AGENTS.md — Package Price Calculator (Expo/React Native)

Application-specific development rules, architecture decisions, and modification
log. Kept up to date after every change.

---

## Application Overview

- **Name:** Package Price Calculator (PewPew Chat-style photography pricing tool)
- **Type:** Expo SDK 57 / React Native 0.86 / TypeScript mobile app (Android)
- **Root folder:** `app/` (Expo managed project)
- **Cloud build:** EAS (Expo Application Services) — cloud APK builds, x86 + x64
- **Supabase backend:** tables live in Supabase (URL from `.env`, gitignored)
- **Deployment:** GitHub Releases (x86 + x64 APK installers)

---

## Pre-Flight Checklist (Section 0.1) — Recorded Decisions

| # | Section | Applies? | Status | Notes |
|---|---------|----------|--------|-------|
| 1 | AGENTS.md (app-level) | YES | DONE | This file |
| 2 | AGENTS_PLAN.md | YES | DONE | See AGENTS_PLAN.md |
| 3 | Code Quality (< 300 lines) | YES | ACTIVE | Files kept under 300 lines |
| 4 | Windows Builds (x86 + x64) | YES | DONE | EAS builds: apk-x86, apk-x64 |
| 5 | Windows Installers | YES | DONE | Both ABI APKs → GitHub Release |
| 6 | CLI .bat Launcher | NO | User declined | Mobile app, not CLI |
| 7 | Desktop Update System | YES | DONE | EAS cloud build = deployment path |
| 8 | Git Repository | YES | DONE | At project root (see below) |
| 9 | Git Version/Release | YES | DONE | v1.0.0 released, both APKs attached |
| 10 | sql/ Folder | NO | User declined | Supabase-hosted schema, not local SQL |
| 11 | media/ Folder | YES | DONE | Logos/icons/images inside media/ |
| 12 | medial_support.txt | YES | DONE | Regenerated each version |
| 13 | .gitignore | YES | DONE | Covers .env, node_modules, etc. |

---

## Core Development Rules (Local)

- Run the authoritative typecheck:
  `npx tsc --noEmit` from `app/` — must exit 0.
  (CLI output can garble in this session; trust `$LASTEXITCODE` + count of `error TS`.)
- Write files via `app/`; verify via `[IO.File]::WriteAllText` (UTF-8, no BOM), then re-probe.
- Keep source files under 300 lines.
- No shims, no silent fallbacks, errors handled explicitly.
- SDK-57 best practices for Expo/React Native.

---

## Build & Release Requirements (Windows)

Per AGENTS rules 5.x (both architectures), this app ships **both** ABI-split APKs:

| ABI | EAS profile | `reactNativeArchitectures` |
|-----|-------------|----------------------------|
| `x86` | `apk-x86` | `x86` |
| `x64` | `apk-x64` | `x86_64` |

- `eas.json` holds the two profiles (verified valid JSON).
- `expo-build-properties` (installed, SDK-57-matched) filters the ABI.
- `app.json` restored/valid: Expo SDK 57, `android.package`, plugin wired.
- Installer filenames stay architecture-stable (no embedded version):
  - `Package-Price-Calculator-x86.apk`
  - `Package-Price-Calculator-x64.apk`
- Release tag carries the version; GitHub Releases hosts both artifacts.

**Environment (verified):**
- Node v24.18.1, npm 12.0.2 (local), `gh` 2.97.0 (authenticated), JDK 17 present
  (`JAVA_HOME=C:\Users\Nwick\.local\jdk-17.0.20+8`).
- **No Android SDK / adb / ANDROID_HOME on this machine** → local APK
  compilation not possible; EAS cloud build is the required path.
- `eas-cli` installed as devDependency (`eas.json` + profiles ready).
- **EAS account: linked** — project `@kogn12/package-price-calculator`
  (projectId `3d0a7203-ad3a-429d-ba95-fde036a1f232`), auth via `EXPO_TOKEN`
  from `credentials.txt` (gitignored). Both APK builds SUCCEEDED (x86 + x64).
- **CRITICAL npm note:** EAS builds run `npm ci --include=dev`
  (`npm@10.9.8`). Local `npm@12` regenerates locks that npm 10 rejects
  (`Missing: typescript@5.9.3 from lock file`). If `npm@12` ever rewrites
  `package-lock.json`, re-run `npx npm@10.9.8 install` to keep the lock in
  sync for EAS. `npx npm@10.9.8 ci --dry-run` must exit 0.

---

## Modification Log

### Modification 001

Date: 2026-09-12 (Phase 1)

- Scaffolded Expo SDK 57 + React Native 0.86 + TypeScript project under `app/`.
- Added Supabase client, theme, types, and calculator state (reduce/dispatch).
- Built the 4-step flow: Labor → Gear → Final. Templates + Sample screens.
- Added PDF generation (expo-print) and local storage persistence.

Files: `app/` (full tree), `app/.env` (gitignored), `app/app.json`.

### Modification 002

Date: 2026-09-12 (Phase 1)

- Backend integration: `src/lib/api.ts` (fetchCategories, fetchEquipmentByCategory,
  fetchEquipmentValuesByIds, fetchTemplateEquipment), `src/lib/supabase.ts`.
- Types: `CategoryType`, `ScopedEquipment`, `CartLine`, scoped equipment shape.
- Calculator state: `SET_LINE_QUANTITY`, selection map `Map<number, CartLine>`.

Important note: `CartLine` = `{equipmentId, name, value, quantity}`; gear state stores
`Map<number, CartLine>`; `SET_GEAR` consumes that map.

### Modification 003

Date: 2026-09-12 (Phase 2 — types)

- 14 TypeScript errors fixed; `tsc --noEmit` now exits 0 (0 errors).
- `TemplatesScreen.tsx`: fixed `nullesa` typo→`null`; dropped bad `equipment: []`;
  gear now `Map<number, CartLine>` via `fetchEquipmentValuesByIds`; imported
  `Pressable` + `CartLine`.
- `StepIndicator.tsx`: added `templateResolved?: boolean` prop with explicit parens.
- `FinalScreen.tsx` / `LaborScreen.tsx`: `Pressable` import added.
- `GearScreen.tsx`: guarded `rows[0]` if empty.
- `src/lib/pricing.ts`: removed unused `templateToGear`.

### Modification 004

Date: 2026-09-12 (config repair)

- **CRITICAL FIX:** `app\app.json` was corrupted on disk — it contained React/TSX
  source (`import React...`) instead of JSON (a prior GearScreen.tsx write had
  clobbered it). Restored to a valid Expo SDK 57 config.
- Verified: starts with `{`, valid JSON, 960 bytes, `android.package` present.
- This had silently broken `expo start` / `prebuild` / `eas build` config parsing.

### Modification 005

Date: 2026-09-12 (EAS configuration)

- Installed `expo-build-properties` (SDK-57 compatible; exit 0) for ABI filtering.
- Created `eas.json` with `apk-x86` (arch=x86) and `apk-x64` (arch=x86_64) profiles;
  verified valid JSON.
- `app.json` now references the `expo-build-properties` plugin.

### Modification 006

Date: 2026-09-12 (EAS CLI)

- Installed `eas-cli` as a devDependency in `app/` (exit 0; `eas` binary present).
- Confirmed `eas-cli` unavailable before → now `node_modules/.bin/eas` exists.
- EAS account auth NOT yet performed.

### Modification 007

Date: 2026-09-12 (current at the time)

- Confirmed environment: Node v24.18.1, npm 12.0.2, gh 2.97.0 (authenticated,
  remote exists), JDK 17 installed (`JAVA_HOME` set) — no JDK install needed.
- Confirmed **no Android SDK / no local APK path** → EAS cloud build required.
- BLOCKED: EAS auth (`eas whoami` = not logged in, no EXPO_TOKEN).
  Nothing further can be built until the user logs in to Expo or supplies EXPO_TOKEN.

### Modification 008

Date: 2026-09-13 (Build & Release)

- EAS project linked: `@kogn12/package-price-calculator`, projectId
  `3d0a7203-ad3a-429d-ba95-fde036a1f232`, owner `kogn12` written to `app.json`
  (`eas init --force` after removing the stub `00000000-...` projectId).
- **Build blocker 1 fixed — stale lockfile:** EAS runs `npm ci --include=dev`
  on `npm@10.9.8`. The lock (generated by local `npm@12`) was missing
  `typescript@5.9.3` → install phase EUSAGE failure. Regenerated the lock with
  `npx npm@10.9.8 install` (delete `node_modules` + `package-lock.json` first).
  Gate check: `npx npm@10.9.8 ci --dry-run` must exit 0.
- **Build blocker 2 fixed — missing icon assets:** `app/assets/` was empty;
  prebuild died with `ENOENT ./assets/icon.png`. Generated `icon.png`,
  `adaptive-icon.png`, `splash-icon.png` (1024×1024) via a System.Drawing
  PowerShell script (gradient, camera-lens + $ motif).
- **SDK-57 config schema fixes:** removed top-level `newArchEnabled` and
  `splash` (both rejected by the SDK 57 doctor schema). Added the
  `expo-splash-screen` plugin (image = `./assets/splash-icon.png`, bg white).
- `expo-splash-screen` added to dependencies (`npx expo install`).
- Both cloud builds SUCCEEDED: apk-x86 (id `12a134ab-...`, build/x86)
  and apk-x64 (id `d076f022-...`, build/x64).
- Verified ABI integrity: x86 APK contains only `lib/x86/`, x64 APK only
  `lib/x86_64/`. Installers saved as `build/Package-Price-Calculator-x86.apk`
  and `build/Package-Price-Calculator-x64.apk` (gitignored).
- `media/` created (logos/, icons/, images/, backgrounds/) with 1024/512/114 px
  logo/icon assets.
- `medial_support.txt` generated for v1.0.0.
- Note: `eas-cli` stays a devDependency; `expo doctor` flags it (advisory,
  non-blocking on EAS).

### Files / Components (current tree)

```
app/
├── app.json            # valid SDK 57 config (projectId, owner, plugins)
├── eas.json            # apk-x86 / apk-x64 ABI profiles
├── package.json        # expo-splash-screen added (SDK-57 pinned)
├── assets/             # icon.png, adaptive-icon.png, splash-icon.png (1024px)
├── src/
│   ├── components/StepIndicator.tsx
│   ├── lib/{api,pricing,supabase}.ts
│   ├── screens/{FinalScreen,GearScreen,LaborScreen,TemplatesScreen}.tsx
│   ├── state/calculator.tsx
│   ├── config.ts, theme.ts, types.ts
├── App.tsx, index.ts
└── .env                # EXPO_PUBLIC_SUPABASE_* (gitignored)

media/
├── logos/logo-1024x1024.png
├── icons/icon-512x512.png, icon-114x114.png
├── images/logo.png
└── backgrounds/.gitkeep

build/                  # gitignored release installers
├── Package-Price-Calculator-x86.apk
└── Package-Price-Calculator-x64.apk
```

---

## Remaining Work (Tracked in AGENTS_PLAN.md)

- None blocking. Phase-3 (build & release) is complete; v1.0.0 shipped with
  both ABI installers. See AGENTS_PLAN.md for Phase-4+ ideas.
