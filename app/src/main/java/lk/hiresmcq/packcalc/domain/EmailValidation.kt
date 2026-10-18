package lk.hiresmcq.packcalc.domain

/**
 * Pure validation + normalisation for the sign-in email (v1.3.0 gate).
 *
 * No Android dependencies, so it is unit-testable. [normalize] is applied
 * before saving and before pushing to the server: the stored row and the
 * `user_emails.email` unique key are always trim + lowercase, so the same
 * person typing "Foo@Bar.com" twice collapses into one row.
 */
object EmailValidation {

    // Practical shape check: local@domain.tld - deliberately conservative but
    // without over-strict RFC rules that reject valid real-world addresses.
    private val PATTERN = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    /** Max length matches `user_emails.email` (varchar(320)). */
    const val MAX_LENGTH = 320

    fun normalize(raw: String): String = raw.trim().lowercase()

    fun isValid(raw: String): Boolean {
        val email = normalize(raw)
        return email.length in 3..MAX_LENGTH && PATTERN.matches(email)
    }
}
