package lk.hiresmcq.packcalc.data.repo

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lk.hiresmcq.packcalc.BuildConfig
import lk.hiresmcq.packcalc.data.model.UserEmailDto
import lk.hiresmcq.packcalc.data.remote.SupabaseApi
import lk.hiresmcq.packcalc.domain.EmailValidation
import lk.hiresmcq.packcalc.util.DeviceIdentity
import lk.hiresmcq.packcalc.util.SessionStore

/**
 * Sign-in email persistence: always on the device, best-effort on the server.
 *
 * [register] saves locally first and then pushes to `public.user_emails`, so
 * the network is never a gate: the user enters the app either way and the
 * returned [Result] tells the caller whether the server copy landed
 * (`true`) or is still pending (`false`). A pending copy is retried by
 * [syncPending] at every app open until it succeeds; the `emailSynced` flag
 * in [SessionStore] tracks that. Server-side duplicates cannot happen twice
 * (UNIQUE email + PostgREST ignore-duplicates), so retries and re-installs
 * collapse into one row; a 409 therefore counts as synced.
 */
class UserEmailRepository(
    private val api: SupabaseApi,
    private val store: SessionStore,
    private val appContext: Context,
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Saves [rawEmail] locally and pushes it to the server.
     *
     * Success `true` = recorded on the server, success `false` = saved on the
     * device only (offline / server down) and will be retried later. Failure
     * means the input was not a valid email and nothing was saved.
     */
    suspend fun register(rawEmail: String): Result<Boolean> = withContext(Dispatchers.IO) {
        val email = EmailValidation.normalize(rawEmail)
        if (!EmailValidation.isValid(email)) {
            return@withContext Result.failure(IllegalArgumentException(INVALID_MESSAGE))
        }
        store.saveEmail(email)
        Result.success(push())
    }

    /** Fire-and-forget retry for an email that never reached the server. */
    fun syncPending() {
        if (store.email != null && !store.emailSynced) {
            scope.launch { push() }
        }
    }

    /** Never throws; a failure only logs and leaves the email pending. */
    private suspend fun push(): Boolean {
        val email = store.email ?: return false
        val dto = UserEmailDto(
            email = email,
            deviceId = DeviceIdentity.deviceId(appContext),
            appVersion = BuildConfig.VERSION_NAME,
            platform = "android",
        )
        return safeApiCall { api.insertUserEmail(ON_CONFLICT, dto) }.fold(
            onSuccess = {
                store.setEmailSynced(true)
                true
            },
            onFailure = { error ->
                if (error is RemoteException && error.httpCode == HTTP_CONFLICT) {
                    store.setEmailSynced(true)
                    true
                } else {
                    Log.w(TAG, "Email sync failed: ${error.message}")
                    false
                }
            },
        )
    }

    private companion object {
        const val TAG = "UserEmail"
        const val ON_CONFLICT = "email"
        const val HTTP_CONFLICT = 409
        const val INVALID_MESSAGE = "Enter a valid email address."
    }
}
