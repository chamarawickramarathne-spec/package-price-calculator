package lk.hiresmcq.packcalc.util

import android.content.Context
import lk.hiresmcq.packcalc.domain.BusinessProfile

/**
 * Device-local copy of the business details entered on the login gate
 * (v1.6.0: business name + phone; the sign-in email is the contact email).
 * Stored in app-private SharedPreferences - the manifest sets
 * allowBackup="false", so it never leaves the device via Android backup.
 * The PDF generator and the drawer read the same store, so what the user
 * enters once is what every later quote prints.
 */
class BusinessStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(): BusinessProfile = BusinessProfile(
        name = prefs.getString(KEY_NAME, null).orEmpty(),
        phone = prefs.getString(KEY_PHONE, null).orEmpty(),
        email = prefs.getString(KEY_EMAIL, null).orEmpty(),
    )

    fun save(profile: BusinessProfile) {
        prefs.edit()
            .putString(KEY_NAME, profile.name.trim())
            .putString(KEY_PHONE, profile.phone.trim())
            .putString(KEY_EMAIL, profile.email.trim())
            .apply()
    }

    private companion object {
        const val PREFS_NAME = "business"
        const val KEY_NAME = "name"
        const val KEY_PHONE = "phone"
        const val KEY_EMAIL = "email"
    }
}
