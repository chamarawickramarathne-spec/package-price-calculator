package lk.hiresmcq.packcalc.domain

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A captured crash, in a form that can be shown on screen and pasted into a
 * bug report.
 *
 * Pure data + formatting only (no Android types), so the report format is
 * unit-testable. The capture itself lives in `util/CrashReporter`.
 */
data class CrashReport(
    val timestampMillis: Long,
    val threadName: String,
    val exceptionClass: String,
    val message: String?,
    val stackTrace: String,
) {

    /** One-line description, e.g. `java.lang.IllegalStateException: boom`. */
    val summary: String
        get() = if (message.isNullOrBlank()) exceptionClass else "$exceptionClass: $message"

    /**
     * The full report: a header, the stack trace, and the last startup steps.
     *
     * The startup trail matters because a crash that happens before the first
     * frame leaves no stack trace on screen - the trail is the only clue
     * about *where* the app died.
     */
    fun asReportText(appVersion: String, channel: String, startupTrail: String): String =
        buildString {
        appendLine("Package Price Calculator - crash report")
        appendLine("App version: $appVersion")
        appendLine("Channel: $channel")
        appendLine("Time: ${formatTime(timestampMillis)}")
        appendLine("Thread: $threadName")
        appendLine("Exception: $summary")
        appendLine()
        appendLine("--- stack trace ---")
        appendLine(stackTrace.trimEnd())
        val trail = startupTrail.trim()
        if (trail.isNotEmpty()) {
            appendLine()
            appendLine("--- last startup steps ---")
            appendLine(trail)
        }
    }

    companion object {
        fun from(
            throwable: Throwable,
            threadName: String,
            timestampMillis: Long,
        ): CrashReport = CrashReport(
            timestampMillis = timestampMillis,
            threadName = threadName,
            exceptionClass = throwable.javaClass.name,
            message = throwable.message,
            stackTrace = throwable.stackTraceToString(),
        )

        // SimpleDateFormat is not thread safe, so a formatter is built per call.
        private fun formatTime(millis: Long): String =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(millis))
    }
}
