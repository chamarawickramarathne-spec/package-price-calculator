package lk.hiresmcq.packcalc.domain

import lk.hiresmcq.packcalc.util.CompanyInfo

/**
 * The user's own business details, collected on the login gate (v1.6.0) and
 * printed on every PDF quote in place of the built-in studio branding.
 *
 * The sign-in email doubles as the business contact email, so [email] is
 * always the address entered on the gate. Pure data + derived display values -
 * no Android dependencies, so the fallback behaviour is unit-testable.
 * Persistence is `util/BusinessStore`.
 */
data class BusinessProfile(
    val name: String = "",
    val phone: String = "",
    val email: String = "",
) {
    /** Masthead / footer / drawer name; blank falls back to the built-in studio. */
    val displayName: String
        get() = name.trim().ifBlank { CompanyInfo.NAME }

    /** Phone / email joined for the PDF footer; null when both blank. */
    val contactLine: String?
        get() {
            val parts = listOf(phone.trim(), email.trim()).filter { it.isNotBlank() }
            return parts.joinToString(" \u2022 ").takeIf { parts.isNotEmpty() }
        }

    companion object {
        const val MAX_NAME = 60
        const val MAX_PHONE = 30
    }
}
