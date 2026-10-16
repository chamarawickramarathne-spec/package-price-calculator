package lk.hiresmcq.packcalc.util

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import lk.hiresmcq.packcalc.domain.PackageQuote
import java.math.BigDecimal

/** Draws one [PackageQuote] onto A4 pages using the Details.pdf house style. */
internal class InvoiceSheet(document: PdfDocument) : PdfPageWriter(document) {

    private val wordmark = PdfTheme.paint(30f, PdfTheme.ORANGE, bold = true)
    private val studio = PdfTheme.paint(13f, PdfTheme.INK_STRONG)
    private val title = PdfTheme.paint(16f, PdfTheme.RED, bold = true)
    private val fieldLabel = PdfTheme.paint(10f, PdfTheme.INK_STRONG)
    private val fieldValue = PdfTheme.paint(11f, PdfTheme.INK)
    private val packageName = PdfTheme.paint(24f, PdfTheme.RED, bold = true)
    private val tagline = PdfTheme.paint(10f, PdfTheme.ORANGE, italic = true)
    private val heading = PdfTheme.paint(14f, PdfTheme.INK, bold = true)
    private val body = PdfTheme.paint(11f, PdfTheme.INK)
    private val bodyBold = PdfTheme.paint(11f, PdfTheme.INK, bold = true)
    private val muted = PdfTheme.paint(9f, PdfTheme.MUTED)
    private val tableHead = PdfTheme.paint(10f, PdfTheme.INK_STRONG)
    private val badgeCaption = PdfTheme.paint(13f, PdfTheme.INK_SUB)
    private val badgeCurrency = PdfTheme.paint(20f, PdfTheme.GREEN, bold = true)
    private val badgeAmount = PdfTheme.paint(32f, PdfTheme.GREEN, bold = true)
    private val noteTitle = PdfTheme.paint(11f, PdfTheme.NOTE_TITLE, bold = true)
    private val noteBody = PdfTheme.paint(10f, PdfTheme.NOTE_TEXT)
    private val footer = PdfTheme.paint(9f, PdfTheme.FOOTER)

    private lateinit var quote: PackageQuote

    fun render(quote: PackageQuote) {
        this.quote = quote
        startPage(first = true)
        drawMasthead()
        drawPackageDetails()
        drawMeta()
        drawBanner()
        drawChecklist()
        drawItems()
        drawTotals()
        drawPriceBadge()
        drawNoteBand()
        drawFooter()
        finishPage()
    }

    private fun drawMasthead() {
        advance(PdfTheme.MARGIN + 26f - y)
        top(PdfTheme.APP_NAME, PdfTheme.MARGIN, PdfTheme.fit(wordmark, PdfTheme.APP_NAME, PdfTheme.CONTENT_WIDTH))
        advance(PdfTheme.lineHeight(wordmark) + 18f)
        top(CompanyInfo.NAME, PdfTheme.MARGIN, studio)
        advance(PdfTheme.lineHeight(studio) + 30f)
    }

    private fun drawPackageDetails() {
        top("Package Details", PdfTheme.MARGIN, title)
        advance(PdfTheme.lineHeight(title) + 14f)
    }

    private fun drawMeta() {
        val base = y
        val rightColumn = PdfTheme.MARGIN + 256f
        field("Date:", PdfDates.display(quote.projectDate, quote.createdAt), PdfTheme.MARGIN, base)
        quote.clientName?.takeIf { it.isNotBlank() }?.let {
            field("Client:", it, PdfTheme.MARGIN, base + 45f)
        }
        field("Invoice #:", quote.invoiceNumber ?: "-", rightColumn, base)
        field("Expires On:", PdfDates.displayPlusDays(quote.projectDate, quote.createdAt, 7), rightColumn, base + 45f)
        advance(base + 108f - y)
    }

    private fun field(label: String, value: String, x: Float, topY: Float) {
        topAt(label, x, topY, fieldLabel)
        topAt(PdfTheme.ellipsize(fieldValue, value, 190f), x, topY + 19f, fieldValue)
    }

    private fun drawBanner() {
        val target = canvas ?: return
        val height = 87f
        needs(height + 12f)
        val topY = y
        target.drawRect(PdfTheme.MARGIN, topY, PdfTheme.CONTENT_RIGHT, topY + height, PdfTheme.fill(PdfTheme.BANNER))
        val textX = PdfTheme.MARGIN + 22f
        val maxWidth = PdfTheme.CONTENT_WIDTH - 44f
        val name = PdfTheme.fit(packageName, quote.packageName, maxWidth)
        target.drawText(
            PdfTheme.ellipsize(name, quote.packageName, maxWidth),
            textX, PdfTheme.baselineOf(name, topY + 17f), name,
        )
        target.drawText(
            PdfTheme.ellipsize(tagline, CompanyInfo.TAGLINE, maxWidth),
            textX, PdfTheme.baselineOf(tagline, topY + 54f), tagline,
        )
        advance(topY + height + 24f - y)
    }

    private fun drawChecklist() {
        val target = canvas ?: return
        val rows = checklistRows()
        needs(PdfTheme.lineHeight(heading) + rows.size * 29f + 42f)
        top("Important", PdfTheme.MARGIN, heading)
        advance(PdfTheme.lineHeight(heading) + 12f)
        rows.forEach { row ->
            PdfTheme.tick(target, PdfTheme.MARGIN + 4f, y + 8f, 11f)
            top(PdfTheme.ellipsize(body, row, PdfTheme.CONTENT_WIDTH - 25f), PdfTheme.MARGIN + 25f, body)
            advance(29f)
        }
        checklistNote()?.let { note ->
            advance(2f)
            top(note, PdfTheme.MARGIN + 25f, PdfTheme.fit(muted, note, PdfTheme.CONTENT_WIDTH - 25f))
            advance(PdfTheme.lineHeight(muted))
        }
        advance(8f)
    }

    private fun checklistRows(): List<String> {
        val rows = ArrayList<String>()
        rows += "${NumberInput.toDisplay(quote.laborHours)} Hours of Photography Coverage"
        quote.lines.take(CHECKLIST_LIMIT).forEach { rows += "${it.quantity} \u00D7 ${it.name}" }
        return rows
    }

    private fun checklistNote(): String? {
        val hidden = quote.lines.size - CHECKLIST_LIMIT
        return when {
            hidden > 0 -> "+$hidden more item(s), itemised below."
            quote.extraCosts.total.signum() > 0 -> "Additional costs are itemised below."
            else -> null
        }
    }

    private fun drawItems() {
        val target = canvas ?: return
        needs(48f)
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
        advance(10f)
    }

    private fun drawTotals() {
        needs(200f)
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
        advance(12f)
    }

    private fun drawPriceBadge() {
        val target = canvas ?: return
        val width = 240f
        val height = 91f
        needs(height + 14f)
        val left = PdfTheme.CONTENT_RIGHT - width
        val topY = y
        target.drawRect(left, topY, PdfTheme.CONTENT_RIGHT, topY + height, PdfTheme.fill(PdfTheme.WHITE))
        target.drawRect(left, topY, PdfTheme.CONTENT_RIGHT, topY + height, PdfTheme.stroke(PdfTheme.GREEN, 1.5f))

        val right = PdfTheme.CONTENT_RIGHT - 17f
        val caption = "Total Package Price"
        target.drawText(
            caption, right - badgeCaption.measureText(caption),
            PdfTheme.baselineOf(badgeCaption, topY + 18f), badgeCaption,
        )
        val amount = CurrencyFormat.formatPlain(quote.breakdown.finalTotal)
        val baseline = PdfTheme.centerBaselineOf(badgeAmount, topY + 58f)
        PdfTheme.fit(badgeAmount, amount, width - 34f - badgeCurrency.measureText("LKR") - 8f)
        target.drawText(amount, right - badgeAmount.measureText(amount), baseline, badgeAmount)
        val amountLeft = right - badgeAmount.measureText(amount)
        target.drawText("LKR", amountLeft - 8f - badgeCurrency.measureText("LKR"), baseline, badgeCurrency)
        advance(topY + height + 26f - y)
    }

    private fun drawNoteBand() {
        val target = canvas ?: return
        val notes = quote.notes?.takeIf { it.isNotBlank() } ?: return
        val textX = PdfTheme.MARGIN + 18f
        val lines = PdfTheme.wrap(notes, noteBody, PdfTheme.CONTENT_RIGHT - textX)
        val height = 22f + PdfTheme.lineHeight(noteBody) + lines.size * 14f + 10f
        needs(height + 10f)
        val topY = y
        target.drawRect(PdfTheme.MARGIN, topY, PdfTheme.MARGIN + 3f, topY + height, PdfTheme.fill(PdfTheme.BANNER))
        PdfTheme.warning(target, textX, topY + 24f, 12f)
        target.drawText("Important Note", textX + 20f, PdfTheme.baselineOf(noteTitle, topY + 18f), noteTitle)
        var lineTop = topY + 40f
        lines.forEach { line ->
            target.drawText(line, textX, PdfTheme.baselineOf(noteBody, lineTop), noteBody)
            lineTop += 14f
        }
        advance(topY + height + 12f - y)
    }

    private fun drawFooter() {
        needs(50f)
        rule()
        advance(12f)
        centered("\u00A9 ${PdfDates.year(quote.createdAt)} ${CompanyInfo.NAME}. All rights reserved.", y, footer)
        centered("Thank you for choosing ${PdfTheme.APP_NAME} for your photography pricing needs.", y + 16f, footer)
    }

    private fun totalRow(label: String, value: String, labelX: Float, paint: Paint) {
        val target = canvas ?: return
        val maxLabel = PdfTheme.CONTENT_RIGHT - labelX - paint.measureText(value) - 14f
        target.drawText(PdfTheme.ellipsize(paint, label, maxLabel), labelX, PdfTheme.baselineOf(paint, y), paint)
        target.drawText(
            value, PdfTheme.CONTENT_RIGHT - paint.measureText(value), PdfTheme.baselineOf(paint, y), paint,
        )
        advance(16f)
    }

    private fun totalRowIfPositive(label: String, value: BigDecimal, labelX: Float) {
        if (value.signum() > 0) {
            totalRow(label, CurrencyFormat.formatPlain(value), labelX, body)
        }
    }

    private companion object {
        const val CHECKLIST_LIMIT = 4
    }
}
