package lk.hiresmcq.packcalc.util

import android.content.Context
import android.os.Process
import lk.hiresmcq.packcalc.BuildConfig
import lk.hiresmcq.packcalc.domain.CrashReport
import java.io.File

/**
 * Captures any uncaught exception before it kills the process.
 *
 * Why this exists: a crash that only happens on one distribution channel
 * (the Google Play build) used to close the app with no explanation at all,
 * and without a Play Console account there was no way to read the stack
 * trace. Now the report is written to the app's private files *before* the
 * process dies and is displayed the next time the app starts (see
 * `ui/components/CrashSurface`).
 *
 * The handler never swallows the crash: the throwable is handed back to the
 * previous (platform) handler so the process still dies the way Android
 * expects.
 */
class CrashReporter(private val context: Context) {

    private val file: File get() = File(context.filesDir, FILE_NAME)

    /**
     * Registers the global handler. Call this as the first statement of
     * `Application.onCreate` so nothing can crash before it is armed.
     */
    fun install() {
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            runCatching { capture(throwable, thread.name) }
            if (previous != null) {
                previous.uncaughtException(thread, throwable)
            } else {
                Process.killProcess(Process.myPid())
            }
        }
    }

    /** The report stored by the last crash, or null when the app has not crashed. */
    fun pendingReportText(): String? =
        runCatching { if (file.exists()) file.readText() else null }
            .getOrNull()
            ?.takeIf { it.isNotBlank() }

    /** Forgets the stored report (the user chose to carry on). */
    fun clearPendingReport() {
        runCatching { file.delete() }
    }

    /**
     * Builds a report for [throwable], stores it and returns its text.
     * Used for crashes that are caught in-app as well as by the global
     * handler.
     */
    fun capture(throwable: Throwable, threadName: String = Thread.currentThread().name): String {
        val report = CrashReport.from(throwable, threadName, System.currentTimeMillis())
        val text = report.asReportText(
            appVersion = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
            channel = BuildConfig.FLAVOR,
            startupTrail = StartupLog.tail(context, TRAIL_LINES),
        )
        runCatching { file.writeText(text) }
        return text
    }

    private companion object {
        const val FILE_NAME = "last_crash.txt"
        const val TRAIL_LINES = 15
    }
}
