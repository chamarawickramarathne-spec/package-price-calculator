package lk.hiresmcq.packcalc.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import lk.hiresmcq.packcalc.domain.PackageQuote
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Renders a package quote to a simple, branded A4 PDF invoice. */
object PdfInvoiceGenerator {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 40f
    private const val CONTENT_RIGHT = PAGE_WIDTH - MARGIN
    private const val CONTENT_WIDTH = CONTENT_RIGHT - MARGIN

    fun suggestedName(quote: PackageQuote): String {
        val number = quote.invoiceNumber ?: "INV"
        val safeName = quote.packageName
            .replace(Regex("[^A-Za-z0-9 _-]"), "")
            .trim()
            .replace(Regex("\\s+"), "_")
            .take(40)
            .ifBlank { "Package" }
        return "${number}_$safeName.pdf"
    }

    fun generate(context: Context, quote: PackageQuote): File {
        val dir = File(context.cacheDir, "invoices").apply { mkdirs() }
        val file = File(dir, suggestedName(quote))
        val document = PdfDocument()
        val sheet = Sheet(document)
        try {
            sheet.render(quote)
            file.outputStream().use { document.writeTo(it) }
        } finally {
            document.close()
        }
        return file
    }

    private class Sheet(private val document: PdfDocument) {
        private val title = paint(22f, Color.BLACK, bold = true)
        private val heading = paint(13f, Color.BLACK, bold = true)
        private val label = paint(10f, Color.DKGRAY)
        private val body = paint(11f, Color.BLACK)
        private val bodyBold = paint(11f, Color.BLACK, bold = true)
        private val small = paint(9f, Color.GRAY)
        private val rule = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
        }

        private var page: PdfDocument.Page? = null
        private var canvas: Canvas? = null
        private var y = 0f

        fun render(quote: PackageQuote) {
            startPage()
            drawHeader(quote)
            drawParties(quote)
            drawItems(quote)
            drawTotals(quote)
            drawNotes(quote)
            finishPage()
        }

        private fun startPage() {
            val info = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, document.pages.size + 1).create()
            page = document.startPage(info)
            canvas = page!!.canvas
            y = MARGIN
        }

        private fun finishPage() {
            page?.let { document.finishPage(it) }
            page = null
            canvas = null
        }

        private fun newPageIfNeeded(needed: Float) {
            if (y + needed > PAGE_HEIGHT - MARGIN) {
                finishPage()
                startPage()
            }
        }

        private fun line(color: Int, width: Float) {
            val c = canvas ?: return
            val paint = Paint().apply { this.color = color; strokeWidth = width }
            c.drawLine(MARGIN, y, CONTENT_RIGHT, y, paint)
            y += 8f
        }

        private fun drawHeader(quote: PackageQuote) {
            val c = canvas ?: return
            c.drawText(CompanyInfo.NAME, MARGIN, y + 18f, title)
            c.drawText(CompanyInfo.TAGLINE, MARGIN, y + 34f, small)
            c.drawText(CompanyInfo.PHONE, MARGIN, y + 48f, small)
            c.drawText(CompanyInfo.EMAIL, MARGIN, y + 61f, small)
            c.drawText(CompanyInfo.ADDRESS, MARGIN, y + 74f, small)

            val right = CONTENT_RIGHT
            drawRight("INVOICE", right, y + 18f, title)
            drawRight("No: ${quote.invoiceNumber ?: "-"}", right, y + 40f, body)
            drawRight("Date: ${formatDate(quote.createdAt)}", right, y + 56f, small)
            quote.projectDate?.takeIf { it.isNotBlank() }?.let {
                drawRight("Project: $it", right, y + 70f, small)
            }
            y += 88f
            line(Color.LTGRAY, 1f)
        }

        private fun drawParties(quote: PackageQuote) {
            y += 6f
            canvas?.drawText("Package: ${quote.packageName}", MARGIN, y, bodyBold)
            y += 16f
            quote.clientName?.takeIf { it.isNotBlank() }?.let {
                canvas?.drawText("Bill to: $it", MARGIN, y, body)
                y += 16f
            }
            y += 4f
        }

        private fun drawItems(quote: PackageQuote) {
            val c = canvas ?: return
            newPageIfNeeded(30f)
            c.drawText("ITEM", MARGIN, y, label)
            drawRight("QTY", MARGIN + CONTENT_WIDTH * 0.62f, y, label)
            drawRight("UNIT (LKR)", MARGIN + CONTENT_WIDTH * 0.83f, y, label)
            drawRight("TOTAL (LKR)", CONTENT_RIGHT, y, label)
            y += 6f
            line(Color.LTGRAY, 1f)

            if (quote.lines.isEmpty()) {
                c.drawText("No equipment items.", MARGIN, y, small)
                y += 16f
            } else {
                quote.lines.forEach { item ->
                    newPageIfNeeded(18f)
                    c.drawText(ellipsize(item.name, 44), MARGIN, y, body)
                    drawRight(item.quantity.toString(), MARGIN + CONTENT_WIDTH * 0.62f, y, body)
                    drawRight(CurrencyFormat.formatPlain(item.unitValue), MARGIN + CONTENT_WIDTH * 0.83f, y, body)
                    drawRight(CurrencyFormat.formatPlain(item.lineTotal), CONTENT_RIGHT, y, body)
                    y += 18f
                }
            }
            y += 4f
        }

        private fun drawTotals(quote: PackageQuote) {
            val c = canvas ?: return
            newPageIfNeeded(190f)
            val labelX = MARGIN + CONTENT_WIDTH * 0.6f

            totalRow("Equipment", CurrencyFormat.formatPlain(quote.breakdown.equipmentTotal), labelX)
            totalRow(
                "Labor (${NumberInput.toDisplay(quote.laborHours)} hrs x " +
                    CurrencyFormat.formatPlain(quote.hourlyRate) + ")",
                CurrencyFormat.formatPlain(quote.breakdown.laborTotal),
                labelX,
            )
            val extras = quote.extraCosts
            totalRowIfPositive("Extra gear", extras.extraGear, labelX)
            totalRowIfPositive("Transportation", extras.transportation, labelX)
            totalRowIfPositive("Assistant pay", extras.assistantPay, labelX)
            totalRowIfPositive("Editing / retouching", extras.editing, labelX)
            totalRowIfPositive("Additional", extras.additional, labelX)

            y += 2f
            line(Color.LTGRAY, 1f)
            totalRow("Subtotal", CurrencyFormat.formatPlain(quote.breakdown.subtotal), labelX, bold = true)
            totalRow(
                "Profit (${NumberInput.toDisplay(quote.profitPercentage)}%)",
                CurrencyFormat.formatPlain(quote.breakdown.profitAmount),
                labelX,
            )
            y += 4f
            c.drawText("FINAL TOTAL", labelX, y + 12f, heading)
            drawRight(CurrencyFormat.format(quote.breakdown.finalTotal), CONTENT_RIGHT, y + 12f, heading)
            y += 28f
        }

        private fun drawNotes(quote: PackageQuote) {
            val c = canvas ?: return
            val notes = quote.notes?.takeIf { it.isNotBlank() } ?: return
            newPageIfNeeded(60f)
            c.drawText("Notes", MARGIN, y, heading)
            y += 16f
            notes.split("\n").forEach { raw ->
                var remaining = raw
                while (remaining.isNotEmpty()) {
                    newPageIfNeeded(14f)
                    val chunk = ellipsize(remaining, 88)
                    c.drawText(chunk, MARGIN, y, body)
                    remaining = remaining.substring(chunk.length).trimStart()
                    y += 14f
                }
            }
        }

        private fun totalRow(labelText: String, value: String, labelX: Float, bold: Boolean = false) {
            val c = canvas ?: return
            c.drawText(labelText, labelX, y, if (bold) bodyBold else body)
            drawRight(value, CONTENT_RIGHT, y, if (bold) bodyBold else body)
            y += 16f
        }

        private fun totalRowIfPositive(labelText: String, value: java.math.BigDecimal, labelX: Float) {
            if (value.signum() > 0) {
                totalRow(labelText, CurrencyFormat.formatPlain(value), labelX)
            }
        }

        private fun drawRight(text: String, right: Float, baselineY: Float, paint: Paint) {
            val c = canvas ?: return
            c.drawText(text, right - paint.measureText(text), baselineY, paint)
        }

        private fun ellipsize(text: String, max: Int): String =
            if (text.length <= max) text else text.take(max - 1) + "\u2026"

        private fun paint(size: Float, color: Int, bold: Boolean = false) = Paint().apply {
            this.color = color
            textSize = size
            isAntiAlias = true
            typeface = if (bold) Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            else Typeface.DEFAULT
        }
    }

    private fun formatDate(millis: Long): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(if (millis > 0) millis else System.currentTimeMillis()))
}
