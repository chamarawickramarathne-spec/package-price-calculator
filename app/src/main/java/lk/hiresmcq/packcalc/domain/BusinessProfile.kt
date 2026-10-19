package lk.hiresmcq.packcalc.domain

import lk.hiresmcq.packcalc.util.CompanyInfo

/**
 * The studio's own business details, entered once on the Business Details
 * screen (v1.5.0) and printed on every PDF quote in place of the built-in
 * studio branding.
 *
 * Pure data + derived display values - no Android dependencies, so the
 * fallback behaviour is unit-testable. Persistence is `util/BusinessStore`.
 */
data class BusinessProfile(
    val name: String = "",
    val tagline: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
) {
    /** Masthead / footer / drawer name; blank falls back to the built-in studio. */
    val displayName: String
        get() = name.trim().ifBlank { CompanyInfo.NAME }

    /** Package-banner sub-title; blank falls back to the built-in tagline. */
    val displayTagline: String
        get() = tagline.trim().ifBlank { CompanyInfo.TAGLINE }

    /** Phone / email / address joined for the PDF footer; null when all blank. */
    val contactLine: String?
        get() {
            val parts = listOf(phone.trim(), email.trim(), address.trim())
                .filter { it.isNotBlank() }
            return parts.joinToString(" \u2022 ").takeIf { parts.isNotEmpty() }
        }

    companion object {
        const val MAX_NAME = 60
        const val MAX_TAGLINE = 80
        const val MAX_PHONE = 30
        const val MAX_ADDRESS = 100
    }
}
