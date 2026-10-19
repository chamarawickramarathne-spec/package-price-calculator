package lk.hiresmcq.packcalc.util

import android.graphics.pdf.PdfDocument
import lk.hiresmcq.packcalc.domain.BusinessProfile
import lk.hiresmcq.packcalc.domain.PackageQuote

/** Draws the client copy (page 1): the Details.pdf house-style quote summary. */
internal class InvoiceSheet(document: PdfDocument) : PdfPageWriter(document) {

    private val wordmark = PdfTheme.paint(30f, PdfTheme.ORANGE, bold = true)
    private val studio = PdfTheme.paint(13f, PdfTheme.INK_STRONG)
    private val title = PdfTheme.paint(16f, PdfTheme.RED, bold = true)
    private val fieldLabel = PdfTheme.paint(10f, PdfTheme.INK_STRONG)
    private val fieldValue = PdfTheme.paint(11f, PdfTheme.INK)
    private val packageName = PdfTheme.paint(24f, PdfTheme.WHITE, bold = true)
    private val tagline = PdfTheme.paint(10f, PdfTheme.WHITE, italic = true)
    private val heading = PdfTheme.paint(14f, PdfTheme.INK, bold = true)
    private val body = PdfTheme.paint(11f, PdfTheme.INK)
    private val muted = PdfTheme.paint(9f, PdfTheme.MUTED)
    private val badgeCaption = PdfTheme.paint(13f, PdfTheme.INK_SUB)
    private val badgeCurrency = PdfTheme.paint(20f, PdfTheme.GREEN, bold = true)
    private val badgeAmount = PdfTheme.paint(32f, PdfTheme.GREEN, bold = true)
    private val noteTitle = PdfTheme.paint(11f, PdfTheme.NOTE_TITLE, bold = true)
    private val noteBody = PdfTheme.paint(10f, PdfTheme.NOTE_TEXT)
    private val footer = PdfTheme.paint(9f, PdfTheme.FOOTER)

    private lateinit var quote: PackageQuote
    private lateinit var business: BusinessProfile

    fun render(quote: PackageQuote, business: BusinessProfile) {
        this.quote = quote
        this.business = business
        startPage(first = true)
        drawMasthead()
        drawPackageDetails()
        drawMeta()
        drawBanner()
        drawChecklist()
        drawPriceBadge()
        drawNoteBand()
        drawFooter()
        finishPage()
    }

    private fun drawMasthead() {
        advance(PdfTheme.MARGIN + 26f - y)
        top(PdfTheme.APP_NAME, PdfTheme.MARGIN, PdfTheme.fit(wordmark, PdfTheme.APP_NAME, PdfTheme.CONTENT_WIDTH))
        advance(PdfTheme.lineHeight(wordmark) + 18f)
        val name = business.displayName
        top(
            PdfTheme.ellipsize(PdfTheme.fit(studio, name, PdfTheme.CONTENT_WIDTH), name, PdfTheme.CONTENT_WIDTH),
            PdfTheme.MARGIN,
            studio,
        )
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
            PdfTheme.ellipsize(tagline, business.displayTagline, maxWidth),
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
        val items = quote.lines.size
        if (items > 0) rows += "$items Item${if (items == 1) "" else "s"} of Professional Equipment"
        return rows
    }

    private fun checklistNote(): String? {
        val extras = quote.extraCosts
        val included = ArrayList<String>()
        if (extras.extraGear.signum() > 0) included += "extra gear"
        if (extras.transportation.signum() > 0) included += "transportation"
        if (extras.assistantPay.signum() > 0) included += "assistant pay"
        if (extras.editing.signum() > 0) included += "editing and retouching"
        if (extras.additional.signum() > 0) included += "additional costs"
        return when (included.size) {
            0 -> null
            1 -> "The package price includes ${included[0]}."
            2 -> "The package price includes ${included[0]} and ${included[1]}."
            else -> "The package price includes " +
                included.dropLast(1).joinToString(", ") + " and " + included.last() + "."
        }
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
        val contactLines = business.contactLine
            ?.let { PdfTheme.wrap(it, footer, PdfTheme.CONTENT_WIDTH, maxLines = 2) }
            .orEmpty()
        needs(50f + contactLines.size * 16f)
        rule()
        advance(12f)
        val copyright =
            "\u00A9 ${PdfDates.year(quote.createdAt)} ${business.displayName}. All rights reserved."
        centered(copyright, y, footer)
        centered("Thank you for choosing ${PdfTheme.APP_NAME} for your photography pricing needs.", y + 16f, footer)
        var lineTop = y + 32f
        contactLines.forEach { line ->
            centered(line, lineTop, footer)
            lineTop += 16f
        }
    }
}
