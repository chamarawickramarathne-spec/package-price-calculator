package lk.hiresmcq.packcalc.util

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import lk.hiresmcq.packcalc.domain.PackageQuote
import java.math.BigDecimal

/** Draws the studio copy: itemised equipment lines and the full price breakdown. */
internal class BreakdownSheet(document: PdfDocument) : PdfPageWriter(document) {

    private val title = PdfTheme.paint(16f, PdfTheme.RED, bold = true)
    private val fieldLabel = PdfTheme.paint(10f, PdfTheme.INK_STRONG)
    private val body = PdfTheme.paint(11f, PdfTheme.INK)
    private val bodyBold = PdfTheme.paint(11f, PdfTheme.INK, bold = true)
    private val muted = PdfTheme.paint(9f, PdfTheme.MUTED)
    private val tableHead = PdfTheme.paint(10f, PdfTheme.INK_STRONG)
    private val strong = PdfTheme.paint(12f, PdfTheme.GREEN, bold = true)
    private val footer = PdfTheme.paint(9f, PdfTheme.FOOTER)

    private lateinit var quote: PackageQuote

    fun render(quote: PackageQuote) {
        this.quote = quote
        startPage(first = false)
        drawHeading()
        drawItems()
        drawTotals()
        drawFooter()
        finishPage()
    }

    private fun drawHeading() {
        top("Price Breakdown", PdfTheme.MARGIN, title)
        advance(PdfTheme.lineHeight(title) + 10f)
        val label = buildString {
            append(quote.packageName)
            quote.invoiceNumber?.let { append("  \u00B7  $it") }
        }
        top(PdfTheme.ellipsize(body, label, PdfTheme.CONTENT_WIDTH * 0.6f), PdfTheme.MARGIN, body)
        quote.clientName?.takeIf { it.isNotBlank() }?.let { client ->
            rightTop(PdfTheme.ellipsize(fieldLabel, client, PdfTheme.CONTENT_WIDTH * 0.35f), PdfTheme.CONTENT_RIGHT, fieldLabel)
        }
        advance(PdfTheme.lineHeight(body) + 18f)
    }

    private fun drawItems() {
        needs(60f)
        val qtyX = PdfTheme.MARGIN + PdfTheme.CONTENT_WIDTH * 0.62f
        val unitX = PdfTheme.MARGIN + PdfTheme.CONTENT_WIDTH * 0.84f
        top("ITEM", PdfTheme.MARGIN, tableHead)
        rightTop("QTY", qtyX, tableHead)
        rightTop("UNIT (LKR)", unitX, tableHead)
        rightTop("TOTAL (LKR)", PdfTheme.CONTENT_RIGHT, tableHead)
        advance(PdfTheme.lineHeight(tableHead) + 6f)
        rule()
        advance(14f)
        if (quote.lines.isEmpty()) {
            top("No equipment items.", PdfTheme.MARGIN, muted)
            advance(PdfTheme.lineHeight(muted))
        } else {
            quote.lines.forEach { line ->
                needs(18f)
                top(PdfTheme.ellipsize(body, line.name, qtyX - PdfTheme.MARGIN - 24f), PdfTheme.MARGIN, body)
                rightTop(line.quantity.toString(), qtyX, body)
                rightTop(CurrencyFormat.formatPlain(line.unitValue), unitX, body)
                rightTop(CurrencyFormat.formatPlain(line.lineTotal), PdfTheme.CONTENT_RIGHT, body)
                advance(18f)
            }
        }
        advance(12f)
    }

    private fun drawTotals() {
        needs(190f)
        val labelX = PdfTheme.MARGIN + PdfTheme.CONTENT_WIDTH * 0.58f
        totalRow("Equipment", CurrencyFormat.formatPlain(quote.breakdown.equipmentTotal), labelX, body)
        totalRow(
            "Event hours (${NumberInput.toDisplay(quote.laborHours)} hrs \u00D7 " +
                "${CurrencyFormat.formatPlain(quote.hourlyRate)})",
            CurrencyFormat.formatPlain(quote.breakdown.laborTotal),
            labelX,
            body,
        )
        val extras = quote.extraCosts
        totalRowIfPositive("Extra gear", extras.extraGear, labelX)
        totalRowIfPositive("Transportation", extras.transportation, labelX)
        totalRowIfPositive("Assistant pay", extras.assistantPay, labelX)
        totalRowIfPositive("Editing / retouching", extras.editing, labelX)
        totalRowIfPositive("Additional", extras.additional, labelX)
        advance(4f)
        rule()
        advance(12f)
        totalRow("Subtotal", CurrencyFormat.formatPlain(quote.breakdown.subtotal), labelX, bodyBold)
        totalRow(
            "Profit (${NumberInput.toDisplay(quote.profitPercentage)}%)",
            CurrencyFormat.formatPlain(quote.breakdown.profitAmount),
            labelX,
            body,
        )
        advance(6f)
        rule()
        advance(16f)
        totalRow("Final Total (LKR)", CurrencyFormat.formatPlain(quote.breakdown.finalTotal), labelX, strong)
    }

    private fun drawFooter() {
        needs(40f)
        advance(25f)
        centered("Studio copy - itemised prices and profit included. Not for the client.", y, footer)
    }

    private fun totalRow(label: String, value: String, labelX: Float, paint: Paint) {
        val target = canvas ?: return
        val maxLabel = PdfTheme.CONTENT_RIGHT - labelX - paint.measureText(value) - 14f
        target.drawText(PdfTheme.ellipsize(paint, label, maxLabel), labelX, PdfTheme.baselineOf(paint, y), paint)
        target.drawText(
            value, PdfTheme.CONTENT_RIGHT - paint.measureText(value), PdfTheme.baselineOf(paint, y), paint,
        )
        advance(PdfTheme.lineHeight(paint) + 5f)
    }

    private fun totalRowIfPositive(label: String, value: BigDecimal, labelX: Float) {
        if (value.signum() > 0) {
            totalRow(label, CurrencyFormat.formatPlain(value), labelX, body)
        }
    }
}
