package lk.hiresmcq.packcalc.util

import android.content.Context

/**
 * Device-local sign-in state: the email the user entered on the gate screen
 * and whether the server copy (public.user_emails) has landed yet.
 *
 * Stored in app-private SharedPreferences. The manifest sets
 * allowBackup="false", so it never leaves the device via Android backup.
 * This is a soft identity - there is no password or token, it only remembers
 * who signed in on this install so the gate is skipped on later launches.
 */
class SessionStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** The remembered email, or null when the gate has never been completed. */
    val email: String?
        get() = prefs.getString(KEY_EMAIL, null)?.takeIf { it.isNotBlank() }

    /** True once the email exists in public.user_emails (retries stop here). */
    val emailSynced: Boolean
        get() = prefs.getBoolean(KEY_EMAIL_SYNCED, false)

    /** Saves the email and marks it as not yet synced (a new email must be pushed). */
    fun saveEmail(value: String) {
        prefs.edit()
            .putString(KEY_EMAIL, value)
            .putBoolean(KEY_EMAIL_SYNCED, false)
            .apply()
    }

    fun setEmailSynced(synced: Boolean) {
        prefs.edit().putBoolean(KEY_EMAIL_SYNCED, synced).apply()
    }

    private companion object {
        const val PREFS_NAME = "session"
        const val KEY_EMAIL = "email"
        const val KEY_EMAIL_SYNCED = "email_synced"
    }
}
