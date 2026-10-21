# Google Play Console — Submission Checklist (v1.6.4)

Everything you need to paste into Play Console, in console order.
Built version: `versionCode 18` / `versionName 1.6.4` / `targetSdk 36` / `minSdk 26`.

---

## 0. Verify the artifact on a phone FIRST (do not skip)

The Play build was reported closing instantly on launch, so test the exact
Play artifact before uploading it:

```
& ".\gradlew.bat" assemblePlayRelease "-Dorg.gradle.java.home=C:\Users\Nwick\.jdks\jdk-17.0.20+8"
```

Output: `app\build\outputs\apk\play\release\app-play-release.apk` — the same
code the AAB ships. Sideload it (`adb install -r <file>`) and confirm the app
opens and shows the sign-in gate.

If it still closes on launch, **open it once more**: v1.6.4 records the crash
and shows the report on screen (app version, channel, thread, stack trace and
the last startup steps) with a **Copy details** button. Send that text in —
it is the missing evidence.

---

## 1. Build the upload artifact (done — rebuild any time)

```
& ".\gradlew.bat" bundlePlayRelease "-Dorg.gradle.java.home=C:\Users\Nwick\.jdks\jdk-17.0.20+8"
```

Output: `app\build\outputs\bundle\playRelease\app-play-release.aab`
(signed with `keystore\packcalc-release.jks` — this becomes your **upload key**).

The GitHub-channel APK is `assembleGithubRelease` and is NOT uploaded to Play.

---

## 2. Create the app

- **App name:** `Package Price Calculator`
- **Default language:** English (United States)
- **App or game:** App
- **Free or paid:** Free

## 3. App content (left menu under "App content")

### Privacy policy (required)
```
https://chamarawickramarathne-spec.github.io/package-price-calculator/privacy-policy.html
```
(Served from `docs/privacy-policy.html` via GitHub Pages.)

### Contact details
- **Contact email:** enter a real inbox you control (Play requires one; it is
  not shown publicly unless you choose).
- **Website (optional):** the GitHub repo URL.

### Store listing (Main store listing)

| Field | Value | Limit |
|---|---|---|
| App name | `Package Price Calculator` | 24 / 30 ✓ |
| Short description | see below | ~78 / 80 ✓ |
| Full description | see below | ~1,900 / 4,000 ✓ |

**Short description (paste):**

```
Quote like a studio: gear, hours and profit in one wizard - branded PDF quotes.
```

**Full description (paste):**

```
Price a photo or video shoot in minutes - then send a professional PDF quote
under your own studio name.

Package Price Calculator is a guided quoting tool for photographers,
videographers, event crews and studios. Four simple steps replace the
spreadsheet: details, equipment, event hours and profit. The app does every
calculation and hands you a two-page PDF quote that is ready for WhatsApp.

HOW IT WORKS
1. Details - client name, package name, project date and notes.
2. Equipment - pick gear from your own catalog at your own rates.
3. Labor & Profit - event hours, hourly rate, extra costs (transportation,
   assistant pay, editing, extra gear) and your profit margin.
4. Pricing - watch the live breakdown and the final total in LKR, then
   export the PDF.

FEATURES
- 4-step pricing wizard with the final total always visible
- Equipment catalog with categories, types and your own values
- Reusable templates - start any quote from your usual package in one tap
- Extra cost fields real shoots have: travel, assistants, editing, extra gear
- Two-page PDF quote: a clean client copy plus an itemised studio copy with
  the full breakdown and profit
- Your business name, phone and email printed on every quote
- Automatic invoice numbers (INV-0001, INV-0002, ...)
- Saved packages history - open, edit and re-issue any job
- Live price breakdown in LKR: equipment, hours, extras, subtotal, profit
- Works offline - quotes are stored on your device
- One-time email sign-in, no password to remember

WHO IT IS FOR
Photography and videography studios, wedding and event crews, freelancers and
any service business that quotes packages from equipment, hours and costs.

WHY PEOPLE LIKE IT
- No spreadsheets and no mental maths - prices stay consistent
- Quotes look branded and professional, not handwritten
- Real costs are never forgotten - travel, assistants and editing are in the price
- Quote before you leave the venue, invoice the same day

Free, no ads, no in-app purchases.
```

### Graphics (upload these files)

| Asset | File | Verified |
|---|---|---|
| Feature graphic | `media/promo/feature-graphic-1024x500.png` | 1024x500, 24-bit ✓ |
| Hi-res icon | `media/icons/icon-512x512.png` | 512x512, 32-bit w/ alpha ✓ (spec allows alpha) |
| Phone screenshot 1 | `media/images/play/login.png` | 1080x1920, 24-bit, 1.78:1 ✓ |
| Phone screenshot 2 | `media/images/play/details.png` | 1080x1920, 24-bit, 1.78:1 ✓ |
| Phone screenshot 3 | `media/images/play/equipment.png` | 1080x1920, 24-bit, 1.78:1 ✓ |
| Phone screenshot 4 | `media/images/play/pricing.png` | 1080x1920, 24-bit, 1.78:1 ✓ |

(The old `media/images/screens/*.png` are 1080x2400 = 2.22:1 — **too tall for
Play** (max 2:1); they are still used for GitHub/Facebook assets.)

Play screenshot rules met: ≥2 shots, sides 320–3840 px, ratio ≤ 2:1, 24-bit PNG
(no alpha), ≤ 8 MB.

## 4. Categorization

- **App category:** Business (alternative: Productivity)
- **Tags (up to 5):** `invoice`, `quoting`, `photography`, `pricing`, `calculator`

## 5. Content declarations (App content page)

| Question | Answer |
|---|---|
| Target audience | Select **18–24, 25–34, 35–44, 45–54, 55–64, 65+**; do NOT select under 13 |
| Is the app designed for children under 13? | **No** (not part of Families program) |
| Contains ads? | **No** |
| In-app purchases? | **No** |
| Social features / user interaction? | **No** |
| User-generated content stored off-device? | **No** |
| News / government / COVID app? | **No** |
| Login / account creation? | App asks for a **one-time email, no password, no verification** → answer per console wording "users enter an email address"; then for account/data deletion: point to privacy policy section 7 (web-based deletion instructions): the privacy policy URL above |
| Content rating questionnaire | All content areas: **None** (no violence, sexual content, language, substances, gambling, user interaction) → expect a rating like **Everyone / 3+** |

### Data deletion ("Delete data URL") — paste into Play Console

```
https://chamarawickramarathne-spec.github.io/package-price-calculator/data-deletion.html
```

(Served from `docs/data-deletion.html`. It names the app/developer, lists
numbered request steps with a prefilled GitHub Issue link, and states what is
deleted (email + usage records, no retention) vs what stays on-device only —
all three Play requirements. Cross-linked from privacy policy section 7.)

When asked "Do you provide a way for users to request that some or all of their
data is deleted…?" → **Yes** (this URL).

### Reviewer note (demo access)
Play requires a way to test the app. **Any syntactically valid email opens the
gate** (e.g. `reviewer@example.com`) — no password or email verification exists.
Mention this in the "App review information / instructions" free-text box if
asked for sign-in instructions.

## 6. Data safety form (paste-ready answers)

Pre-conditions: collected data is **encrypted in transit (TLS/HTTPS)**; stored
server-side by Supabase; **not sold, not shared** with third parties for other
apps' purposes.

| Data type | Collected | Shared | Sold | Why (purposes) | Deletion |
|---|---|---|---|---|---|
| **Email addresses** (sign-in gate) | Yes | No | No | App functionality; Developer communications (studio may email users about its apps) | Yes — see the data deletion page URL below |
| **Device identifiers** (salted SHA-256 hash of Android ID; not the raw ID) | Yes | No | No | Analytics (usage events: app open, PDF export); App functionality | Yes — see the data deletion page URL below |
| App activity / package & pricing data | **No** — stays on device (Room/SQLite) | — | — | — | Uninstall / clear storage |

Also declare when asked:
- Data is **encrypted in transit** = Yes.
- Data at rest: stored in Google Cloud (Supabase project) with project-level
  access controls; public app key restricted by RLS policies.
- Retention: kept until the user requests deletion (privacy policy section 6/7).
- No data is collected for advertising; no advertising SDKs exist in the app.

## 7. Upload & signing (Production setup)

1. **Testing → Internal testing (or Closed testing) → Create new release →
   Upload** `app-play-release.aab`.
2. Release name: `1.6.4` — Release notes: paste the GitHub release notes.
3. When prompted for **Play App Signing**: enroll (required for new apps).
   - Upload key = your existing `packcalc-release.jks` (the AAB is already
     signed with it).
   - Google generates and holds the **app signing key**.
   - Consequence: users who installed the GitHub APK cannot switch to Play
     without uninstall/reinstall (different signer). Accepted trade-off.
4. First release must start on a **testing track**, not straight to production.

## 8. Testing track before production

- **Organization account:** internal/closed testing → production is
  straightforward.
- **Personal account:** Google requires a **closed test with ≥12 testers
  continuously opted in for 14 days** before applying for production access.
  Create a Google Group, add 12+ real testers, share the opt-in link, wait
  14 days with no gaps, then apply for production access.

## 9. Final review before hitting "Submit"

- [ ] targetSdk = 36 (checked in `app/build.gradle.kts`; verified in merged manifest)
- [ ] Play merged manifest has **no** `REQUEST_INSTALL_PACKAGES` (verified for playRelease)
- [ ] versionCode 18 > 0 and unique on Play
- [ ] The **Play APK was sideloaded and opened** at least once (section 0)
- [ ] Privacy policy URL loads over HTTPS (verified after Pages enable)
- [ ] Data safety matches this checklist
- [ ] Feature graphic + 4 screenshots + icon uploaded
- [ ] Content rating completed
- [ ] No "You need to fix" items left in Play Console dashboard
- [ ] Account verification / payment profile done (one-time $25 already paid
      if account exists)
