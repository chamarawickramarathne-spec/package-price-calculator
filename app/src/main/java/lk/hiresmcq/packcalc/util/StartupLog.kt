package lk.hiresmcq.packcalc.util

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A short trail of the steps the app went through while starting up, kept in
 * the app's private files.
 *
 * It answers the question a stack trace cannot: *where* did the app die?
 * Each step is appended with a timestamp and the file is trimmed, so it
 * stays small enough to read on screen next to the crash report.
 */
object StartupLog {

    private const val FILE_NAME = "startup.log"
    private const val MAX_LINES = 80

    /**
     * Records a startup step. Never throws - logging must not break the app.
     */
    fun mark(context: Context, step: String) {
        runCatching {
            val file = File(context.filesDir, FILE_NAME)
            val existing: List<String> = if (file.exists()) file.readLines() else emptyList()
            val merged = (existing + "${time()}  $step").takeLast(MAX_LINES)
            file.writeText(merged.joinToString("\n"))
        }
    }

    /** The last [lines] recorded steps, oldest first. */
    fun tail(context: Context, lines: Int): String = runCatching {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return@runCatching ""
        file.readLines().takeLast(lines).joinToString("\n")
    }.getOrDefault("")

    // SimpleDateFormat is not thread safe, so a formatter is built per call.
    private fun time(): String = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
}
