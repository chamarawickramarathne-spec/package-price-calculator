package lk.hiresmcq.packcalc

import android.app.Application
import android.util.Log
import lk.hiresmcq.packcalc.di.AppContainer
import lk.hiresmcq.packcalc.domain.UsageEvent
import lk.hiresmcq.packcalc.util.CrashReporter
import lk.hiresmcq.packcalc.util.StartupLog

class PackCalcApp : Application() {

    lateinit var container: AppContainer
        private set

    /**
     * The throwable that stopped the app from starting, when there is one.
     * The UI shows it instead of opening, so a broken start is visible
     * rather than silent.
     */
    var startupFailure: Throwable? = null
        private set

    override fun onCreate() {
        super.onCreate()
        // Armed first, so nothing can crash before the report is captured.
        CrashReporter(this).install()
        StartupLog.mark(this, "app.onCreate")
        StartupLog.mark(this, "crash handler armed")

        runCatching { container = AppContainer(this) }
            .onSuccess { StartupLog.mark(this, "container ready") }
            .onFailure {
                startupFailure = it
                Log.e(TAG, "AppContainer could not be built", it)
                StartupLog.mark(this, "container failed: ${it.javaClass.simpleName}")
            }
        if (startupFailure != null) return

        runCatching { container.usageLogRepository.log(UsageEvent.AppOpen) }
            .onFailure { Log.w(TAG, "Usage log failed", it) }
        // Retry an email that never reached user_emails (saved while offline).
        runCatching { container.userEmailRepository.syncPending() }
            .onFailure { Log.w(TAG, "Email sync failed", it) }
        // Backfill installs that signed in before business details moved to the
        // login gate (v1.6.0): the remembered email is the PDF contact email.
        runCatching { container.backfillBusinessEmail() }
            .onFailure { Log.w(TAG, "Business backfill failed", it) }
        StartupLog.mark(this, "startup steps done")
    }

    private companion object {
        const val TAG = "PackCalcApp"
    }
}
