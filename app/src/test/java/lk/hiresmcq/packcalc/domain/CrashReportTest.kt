package lk.hiresmcq.packcalc.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CrashReportTest {

    @Test
    fun summaryIncludesMessageWhenPresent() {
        val report = CrashReport.from(
            throwable = IllegalStateException("boom"),
            threadName = "main",
            timestampMillis = 0L,
        )
        assertEquals("java.lang.IllegalStateException: boom", report.summary)
    }

    @Test
    fun summaryFallsBackToClassNameWhenMessageIsBlank() {
        val report = CrashReport.from(
            throwable = IllegalStateException("   "),
            threadName = "main",
            timestampMillis = 0L,
        )
        assertEquals("java.lang.IllegalStateException", report.summary)
    }

    @Test
    fun reportTextCarriesVersionChannelAndStackTrace() {
        val report = CrashReport.from(
            throwable = IllegalStateException("boom"),
            threadName = "main",
            timestampMillis = 0L,
        )
        val text = report.asReportText(
            appVersion = "1.6.4 (18)",
            channel = "play",
            startupTrail = "",
        )
        assertTrue(text.contains("App version: 1.6.4 (18)"))
        assertTrue(text.contains("Channel: play"))
        assertTrue(text.contains("Thread: main"))
        assertTrue(text.contains("java.lang.IllegalStateException: boom"))
        assertTrue(text.contains("--- stack trace ---"))
    }

    @Test
    fun reportTextAppendsStartupTrailOnlyWhenPresent() {
        val report = CrashReport.from(
            throwable = RuntimeException("x"),
            threadName = "main",
            timestampMillis = 0L,
        )
        val withoutTrail = report.asReportText("1.6.4 (18)", "play", "")
        assertTrue(!withoutTrail.contains("--- last startup steps ---"))

        val withTrail = report.asReportText(
            appVersion = "1.6.4 (18)",
            channel = "play",
            startupTrail = "10:00:00  app.onCreate",
        )
        assertTrue(withTrail.contains("--- last startup steps ---"))
        assertTrue(withTrail.contains("10:00:00  app.onCreate"))
    }

    @Test
    fun stackTraceKeepsTheCauseChain() {
        val cause = IllegalArgumentException("root cause")
        val report = CrashReport.from(
            throwable = RuntimeException("wrapper", cause),
            threadName = "main",
            timestampMillis = 0L,
        )
        assertTrue(report.stackTrace.contains("root cause"))
        assertTrue(report.stackTrace.contains("Caused by"))
    }
}
