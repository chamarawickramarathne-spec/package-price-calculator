package lk.hiresmcq.packcalc.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * One append-only usage event for `public.access_logs`.
 *
 * The app only ever INSERTs these rows (anon has INSERT-only rights there), so
 * this DTO carries no id and no `accessed_at` - Postgres fills both. Only
 * non-personal facts are sent: which event, which route, the app identity and a
 * pseudonymous device hash. No client names, package names or prices are sent.
 */
@Serializable
data class AccessLogDto(
    @SerialName("event_type") val eventType: String,
    @SerialName("request_uri") val requestUri: String,
    @SerialName("user_agent") val userAgent: String,
    @SerialName("device_id") val deviceId: String,
    @SerialName("app_version") val appVersion: String,
    val platform: String,
)
