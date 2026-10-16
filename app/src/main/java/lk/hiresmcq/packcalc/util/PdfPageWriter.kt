package lk.hiresmcq.packcalc.util

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument

/** Shared A4 page flow and text placement for the PDF sheets. */
internal abstract class PdfPageWriter(private val document: PdfDocument) {

    protected var canvas: Canvas? = null
        private set

    protected var y = 0f
        private set

    private var page: PdfDocument.Page? = null

    protected fun startPage(first: Boolean) {
        val number = document.pages.size + 1
        val info = PdfDocument.PageInfo.Builder(PdfTheme.PAGE_WIDTH, PdfTheme.PAGE_HEIGHT, number).create()
        page = document.startPage(info)
        canvas = page!!.canvas
        y = PdfTheme.MARGIN
        if (!first) drawContinuationHeader()
    }

    protected fun finishPage() {
        page?.let { document.finishPage(it) }
        page = null
        canvas = null
    }

    protected fun needs(height: Float) {
        if (y + height > PdfTheme.PAGE_HEIGHT - PdfTheme.MARGIN) {
            finishPage()
            startPage(first = false)
        }
    }

    protected fun advance(delta: Float) {
        y += delta
    }

    protected fun top(text: String, x: Float, paint: Paint) {
        canvas?.drawText(text, x, PdfTheme.baselineOf(paint, y), paint)
    }

    protected fun topAt(text: String, x: Float, topY: Float, paint: Paint) {
        canvas?.drawText(text, x, PdfTheme.baselineOf(paint, topY), paint)
    }

    protected fun rightTop(text: String, right: Float, paint: Paint) {
        val target = canvas ?: return
        target.drawText(text, right - paint.measureText(text), PdfTheme.baselineOf(paint, y), paint)
    }

    protected fun centered(text: String, topY: Float, paint: Paint) {
        val target = canvas ?: return
        val shown = PdfTheme.ellipsize(paint, text, PdfTheme.CONTENT_WIDTH)
        val x = (PdfTheme.PAGE_WIDTH - paint.measureText(shown)) / 2f
        target.drawText(shown, x, PdfTheme.baselineOf(paint, topY), paint)
    }

    protected fun rule() {
        canvas?.drawLine(
            PdfTheme.MARGIN, y, PdfTheme.CONTENT_RIGHT, y, PdfTheme.stroke(PdfTheme.RULE, 1f),
        )
    }

    private fun drawContinuationHeader() {
        val small = PdfTheme.paint(12f, PdfTheme.ORANGE, bold = true)
        top(PdfTheme.APP_NAME, PdfTheme.MARGIN, small)
        y += PdfTheme.lineHeight(small) + 6f
        rule()
        y += 16f
    }
}
