package lk.hiresmcq.packcalc.util

import android.content.Context
import android.graphics.pdf.PdfDocument
import lk.hiresmcq.packcalc.domain.PackageQuote
import java.io.File

/**
 * Renders a package quote to a branded A4 PDF (Details.pdf house style):
 * page 1 is the client summary, page 2 the studio copy with itemised prices.
 */
object PdfInvoiceGenerator {

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
        try {
            InvoiceSheet(document).render(quote)
            BreakdownSheet(document).render(quote)
            file.outputStream().use { document.writeTo(it) }
        } finally {
            document.close()
        }
        return file
    }
}
