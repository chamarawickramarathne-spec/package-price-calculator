package lk.hiresmcq.packcalc.data.repo

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lk.hiresmcq.packcalc.BuildConfig
import lk.hiresmcq.packcalc.data.model.AccessLogDto
import lk.hiresmcq.packcalc.data.remote.SupabaseApi
import lk.hiresmcq.packcalc.domain.UsageEvent
import lk.hiresmcq.packcalc.util.DeviceIdentity

/**
 * Best-effort usage logging.
 *
 * [log] is fire-and-forget: it runs on its own app-scoped IO coroutine so it can
 * never block or crash a screen, and a failure (offline, server down) is only
 * written to Logcat - the event is dropped rather than queued. Logging must never
 * get in the user's way, so nothing here surfaces to the UI.
 */
class UsageLogRepository(
    private val api: SupabaseApi,
    private val appContext: Context,
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun log(event: UsageEvent) {
        scope.launch { logNow(event) }
    }

    suspend fun logNow(event: UsageEvent) = withContext(Dispatchers.IO) {
        val dto = AccessLogDto(
            eventType = event.eventType,
            requestUri = event.requestUri,
            userAgent = DeviceIdentity.userAgent,
            deviceId = DeviceIdentity.deviceId(appContext),
            appVersion = BuildConfig.VERSION_NAME,
            platform = "android",
        )
        safeApiCall { api.insertAccessLog(dto) }
            .onFailure { Log.w(TAG, "Usage log failed (${event.eventType}): ${it.message}") }
    }

    private companion object {
        const val TAG = "UsageLog"
    }
}
