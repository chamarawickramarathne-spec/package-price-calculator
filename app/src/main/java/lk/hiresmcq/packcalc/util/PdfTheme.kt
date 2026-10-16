package lk.hiresmcq.packcalc.util

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** Colours, geometry and drawing primitives for the Details.pdf house style. */
internal object PdfTheme {

    const val APP_NAME = "Package Price Calculator"
    const val PAGE_WIDTH = 595
    const val PAGE_HEIGHT = 842
    const val MARGIN = 56f
    const val CONTENT_RIGHT = 539f
    const val CONTENT_WIDTH = CONTENT_RIGHT - MARGIN

    const val ORANGE = 0xFFEA580C.toInt()
    const val BANNER = 0xFFF59E0B.toInt()
    const val RED = 0xFFD22525.toInt()
    const val GREEN = 0xFF10B981.toInt()
    const val INK = 0xFF1F2937.toInt()
    const val INK_STRONG = 0xFF222429.toInt()
    const val INK_SUB = 0xFF374151.toInt()
    const val NOTE_TITLE = 0xFF92400E.toInt()
    const val NOTE_TEXT = 0xFF78350F.toInt()
    const val FOOTER = 0xFF18191B.toInt()
    const val MUTED = 0xFF6B7280.toInt()
    const val RULE = 0xFFE5E7EB.toInt()
    const val WHITE = Color.WHITE

    fun paint(size: Float, color: Int, bold: Boolean = false, italic: Boolean = false): Paint {
        val face = when {
            bold && italic -> Typeface.BOLD_ITALIC
            bold -> Typeface.BOLD
            italic -> Typeface.ITALIC
            else -> Typeface.NORMAL
        }
        return Paint().apply {
            this.color = color
            textSize = size
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, face)
        }
    }

    fun fill(color: Int): Paint = Paint().apply {
        this.color = color
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    fun stroke(color: Int, width: Float): Paint = Paint().apply {
        this.color = color
        style = Paint.Style.STROKE
        strokeWidth = width
        isAntiAlias = true
    }

    fun baselineOf(paint: Paint, top: Float): Float = top - paint.fontMetrics.ascent

    fun centerBaselineOf(paint: Paint, centerY: Float): Float =
        centerY - (paint.fontMetrics.ascent + paint.fontMetrics.descent) / 2f

    fun lineHeight(paint: Paint): Float = paint.fontMetrics.descent - paint.fontMetrics.ascent

    /** Shrinks [paint] in place until [text] fits [maxWidth]. */
    fun fit(paint: Paint, text: String, maxWidth: Float): Paint {
        while (paint.measureText(text) > maxWidth && paint.textSize > 9f) {
            paint.textSize -= 1f
        }
        return paint
    }

    fun ellipsize(paint: Paint, text: String, maxWidth: Float): String {
        if (paint.measureText(text) <= maxWidth) return text
        var end = text.length
        while (end > 1) {
            val candidate = text.take(end).trimEnd() + "\u2026"
            if (paint.measureText(candidate) <= maxWidth) return candidate
            end--
        }
        return "\u2026"
    }

    fun wrap(text: String, paint: Paint, maxWidth: Float, maxLines: Int = 12): List<String> {
        val out = ArrayList<String>()
        text.split('\n').forEach { paragraph ->
            var current = ""
            paragraph.split(' ').forEach { word ->
                val candidate = if (current.isEmpty()) word else "$current $word"
                if (current.isEmpty() || paint.measureText(candidate) <= maxWidth) {
                    current = candidate
                } else {
                    out += current
                    current = word
                }
            }
            out += current
        }
        if (out.size <= maxLines) return out
        val kept = out.take(maxLines - 1).toMutableList()
        kept += ellipsize(paint, out.drop(maxLines - 1).joinToString(" "), maxWidth)
        return kept
    }

    /** Green tick drawn inside a [size] box; left edge at [x], vertical centre [centerY]. */
    fun tick(canvas: Canvas, x: Float, centerY: Float, size: Float) {
        val stroke = stroke(GREEN, size * 0.18f).apply {
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
        val path = Path().apply {
            moveTo(x, centerY)
            lineTo(x + size * 0.36f, centerY + size * 0.38f)
            lineTo(x + size, centerY - size * 0.42f)
        }
        canvas.drawPath(path, stroke)
    }

    /** Amber warning triangle with an exclamation mark. */
    fun warning(canvas: Canvas, x: Float, centerY: Float, size: Float) {
        val top = centerY - size / 2f
        val line = stroke(NOTE_TITLE, size * 0.12f).apply { strokeJoin = Paint.Join.ROUND }
        val path = Path().apply {
            moveTo(x + size / 2f, top)
            lineTo(x + size, top + size * 0.92f)
            lineTo(x, top + size * 0.92f)
            close()
        }
        canvas.drawPath(path, line)
        val bar = stroke(NOTE_TITLE, size * 0.13f).apply { strokeCap = Paint.Cap.ROUND }
        canvas.drawLine(x + size / 2f, top + size * 0.34f, x + size / 2f, top + size * 0.62f, bar)
        canvas.drawCircle(x + size / 2f, top + size * 0.78f, size * 0.07f, fill(NOTE_TITLE))
    }
}

/** Date helpers for the invoice (project dates are stored as ISO yyyy-MM-dd). */
internal object PdfDates {

    fun display(value: String?, fallbackMillis: Long): String =
        formatter("MMMM d, yyyy").format(base(value, fallbackMillis))

    fun displayPlusDays(value: String?, fallbackMillis: Long, days: Int): String =
        formatter("MMMM d, yyyy").format(addDays(base(value, fallbackMillis), days))

    fun year(fallbackMillis: Long): String = formatter("yyyy").format(Date(millis(fallbackMillis)))

    private fun base(value: String?, fallbackMillis: Long): Date {
        val trimmed = value?.trim()
        if (!trimmed.isNullOrEmpty()) {
            val position = ParsePosition(0)
            formatter("yyyy-MM-dd").parse(trimmed, position)?.let {
                if (position.index == trimmed.length) return it
            }
        }
        return Date(millis(fallbackMillis))
    }

    private fun addDays(date: Date, days: Int): Date =
        Calendar.getInstance().apply {
            time = date
            add(Calendar.DAY_OF_YEAR, days)
        }.time

    private fun millis(value: Long): Long = if (value > 0) value else System.currentTimeMillis()

    private fun formatter(pattern: String): SimpleDateFormat =
        SimpleDateFormat(pattern, Locale.US)
}
