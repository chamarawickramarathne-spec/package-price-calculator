# AGENTS_PLAN.md — Package Price Calculator

Implementation plan and task tracker for the Package Price Calculator
(Expo SDK 57 / React Native 0.86 / TypeScript Android app).

---

## Pre-Flight Checklist (Section 0.1) — Recorded Decisions

| # | Section | Applies? | Status | Notes |
|---|---------|----------|--------|-------|
| 1 | AGENTS.md (app-level) | YES | DONE | Root of repo (this dir) |
| 2 | AGENTS_PLAN.md | YES | DONE | This file |
| 3 | Code Quality (< 300 lines) | YES | ACTIVE | Files kept under 300 lines |
| 4 | Windows Builds (x86 + x64) | YES | IN PROGRESS | EAS builds: apk-x86, apk-x64 |
| 5 | Windows Installers | YES | IN PROGRESS | Both ABI APKs -> GitHub Release |
| 6 | CLI .bat Launcher | NO | User declined | Mobile app, not CLI |
| 7 | Desktop Update System | YES | IN PROGRESS | EAS cloud build = deployment path |
| 8 | Git Repository | YES | DONE | At project root |
| 9 | Git Version/Release | YES | IN PROGRESS | gh authenticated; release pending |
| 10 | sql/ Folder | NO | User declined | Supabase-hosted schema, not local SQL |
| 11 | media/ Folder | YES | PENDING | Logos/icons/images inside media/ |
| 12 | medial_support.txt | YES | PENDING | Regenerated each version |
| 13 | .gitignore | YES | DONE | Covers .env, node_modules, builds |

---

## Phase-1 (Web+PWA) — COMPLETED

- Full Lens Calculator web app: HTML/CSS/JS + PHP/MySQL backend (Hire Artist Studio).
- 8 categories, 50+ equipment items, 5 templates, PDF generation.
- See PROJECT_OVERVIEW.md and CHANGELOG.md (v1.0.0).

---

## Phase-2 (React Native Rewrite) — COMPLETED

- SDK 57 typecheck green (	sc --noEmit exits 0, 0 errors).
- 14 TypeScript errors fixed across screens/state/lib.
- app.json restored (was corrupted on disk with React TSX).
- expo-build-properties + eas-cli installed; eas.json created (apk-x86, apk-x64).

---

## Phase-3 (Build & Release) — COMPLETED

### Completed
- [x] Phase-2 typecheck green (tsc exit 0)
- [x] app.json valid (android.package wired, expo-build-properties plugin)
- [x] eas.json with ABI-split profiles (apk-x86 = x86, apk-x64 = x86_64)
- [x] eas-cli + expo-build-properties installed (devDeps)
- [x] AGENTS.md + AGENTS_PLAN.md current (Modification 007)
- [x] EAS project linked (@kogn12/package-price-calculator, projectId 3d0a7203...)
- [x] Lockfile regenerated with npm@10.9.8 (EAS uses npm 10; fixed typescript@5.9.3 sync)
- [x] Icon assets generated (icon/adaptive/splash, 1024px) under app/assets/
- [x] expo-splash-screen added (SDK-57 splash schema: dropped newArchEnabled + top-level splash)
- [x] EAS cloud build x86 SUCCEEDED -> build/Package-Price-Calculator-x86.apk
- [x] EAS cloud build x64 SUCCEEDED -> build/Package-Price-Calculator-x64.apk
- [x] ABI verified (x86 APK = lib/x86 only, x64 APK = lib/x86_64 only)
- [x] media/ created (logos, icons, images, backgrounds)
- [x] medial_support.txt generated for v1.0.0
- [x] GitHub release v1.0.0 with both installer artifacts
- [x] AGENTS.md / AGENTS_PLAN.md updated; committed; tagged v1.0.0

---

## Pending (Phase-4+ ideas)
- Theme presets / advanced pricing models
- Supabase auth + user-specific packages
- More templates + equipment seed data
- medials can display package breakdown in PDF

(C) 2026 Hire Artist Studio. AGENTS_PLAN versionated with AGENTS.