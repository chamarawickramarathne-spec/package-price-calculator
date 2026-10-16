package lk.hiresmcq.packcalc.domain

/**
 * The usage events the app records in `public.access_logs`.
 *
 * [eventType] is the coarse bucket used by the reports in `sql/usage_reports.sql`
 * ("app_open" vs "pdf_export"); [requestUri] keeps the old web-log convention and
 * distinguishes the PDF paths (share vs save).
 */
enum class UsageEvent(val eventType: String, val requestUri: String) {
    AppOpen(eventType = "app_open", requestUri = "/app/open"),
    PdfShare(eventType = "pdf_export", requestUri = "/pdf/share"),
    PdfSave(eventType = "pdf_export", requestUri = "/pdf/save"),
}
