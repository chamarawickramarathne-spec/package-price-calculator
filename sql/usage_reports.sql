-- =========================================================================
-- Package Price Calculator - usage reports (READ ONLY)
--
-- Run these in the Supabase SQL Editor to see how the app is being used.
-- Nothing here writes or changes data - safe to run any time.
--
-- WHAT THE COLUMNS MEAN
--   event_type  'app_open'   -> the app was launched (one row per launch)
--               'pdf_export' -> a quote PDF was produced
--               NULL         -> a legacy row from the old web app
--   request_uri '/app/open', '/pdf/share', '/pdf/save', or the old
--               '/api/log_access.php' for legacy rows
--   device_id   SHA-256 of ANDROID_ID truncated to 32 chars. Same phone =
--               same id, so COUNT(DISTINCT device_id) is your user count.
--   app_version e.g. '1.3.0'
--   platform    'android' (NULL for legacy web rows)
-- =========================================================================

-- 1. Headline numbers: opens, PDF exports, and how many distinct devices.
SELECT
    count(*) FILTER (WHERE event_type = 'app_open')                    AS app_opens,
    count(*) FILTER (WHERE event_type = 'pdf_export')                  AS pdf_exports,
    count(DISTINCT device_id) FILTER (WHERE device_id IS NOT NULL)     AS unique_devices,
    min(accessed_at) FILTER (WHERE event_type = 'app_open')            AS first_app_open,
    max(accessed_at) FILTER (WHERE event_type = 'app_open')            AS last_app_open
FROM public.access_logs;

-- 2. Activity per day for the last 30 days (opens and PDF exports).
SELECT
    date_trunc('day', accessed_at)::date                     AS day,
    count(*) FILTER (WHERE event_type = 'app_open')           AS app_opens,
    count(*) FILTER (WHERE event_type = 'pdf_export')         AS pdf_exports,
    count(DISTINCT device_id)                                 AS devices
FROM public.access_logs
WHERE accessed_at >= now() - interval '30 days'
  AND event_type IS NOT NULL
GROUP BY 1
ORDER BY 1 DESC;

-- 3. Distinct devices (your "how many people") with first/last seen.
SELECT
    device_id,
    min(accessed_at)                                    AS first_seen,
    max(accessed_at)                                    AS last_seen,
    count(*) FILTER (WHERE event_type = 'app_open')      AS opens,
    count(*) FILTER (WHERE event_type = 'pdf_export')    AS pdf_exports,
    max(app_version)                                     AS latest_version
FROM public.access_logs
WHERE device_id IS NOT NULL
GROUP BY device_id
ORDER BY last_seen DESC;

-- 4. Install bases by app version.
SELECT
    app_version,
    count(DISTINCT device_id) AS devices,
    count(*)                  AS events
FROM public.access_logs
WHERE event_type IS NOT NULL
GROUP BY app_version
ORDER BY devices DESC NULLS LAST;

-- 5. PDF export paths (share vs save).
SELECT
    request_uri,
    count(*)                  AS exports,
    count(DISTINCT device_id) AS devices
FROM public.access_logs
WHERE event_type = 'pdf_export'
GROUP BY request_uri
ORDER BY exports DESC;

-- 6. Everything recent (newest first). Handy for a quick sanity check that the
--    app is reporting in after you install it.
SELECT id, accessed_at, event_type, request_uri, device_id, app_version, platform
FROM public.access_logs
ORDER BY id DESC
LIMIT 50;
