package lk.hiresmcq.packcalc.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * One append-only row for `public.user_emails` - the email typed on the
 * app's sign-in gate, recorded so the studio can promote its other
 * applications.
 *
 * The app only ever INSERTs (anon has INSERT-only rights there), so this DTO
 * carries no id and no `created_at` - Postgres fills both. No name, no
 * package data and no password accompany it; only the email plus the same
 * pseudonymous device/app identity the usage log uses. The email is always
 * normalized (trim + lowercase) by the caller before this DTO is built,
 * because `email` is UNIQUE server-side.
 */
@Serializable
data class UserEmailDto(
    val email: String,
    @SerialName("device_id") val deviceId: String? = null,
    @SerialName("app_version") val appVersion: String? = null,
    val platform: String? = null,
)
