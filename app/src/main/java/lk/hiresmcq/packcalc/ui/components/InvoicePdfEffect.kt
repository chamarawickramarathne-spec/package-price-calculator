package lk.hiresmcq.packcalc.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import lk.hiresmcq.packcalc.ui.calculator.PdfMode
import lk.hiresmcq.packcalc.ui.calculator.PdfResult
import lk.hiresmcq.packcalc.util.InvoiceSharing

/**
 * Handles a produced invoice PDF: shares it or asks the user where to save it.
 * Call [onConsumed] once done so the producer can clear its state.
 */
@Composable
fun InvoicePdfEffect(
    pdf: PdfResult?,
    onConsumed: () -> Unit,
    onMessage: (String) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var pendingSave by remember { mutableStateOf<PdfResult?>(null) }

    val saveDocument = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf"),
    ) { uri ->
        val result = pendingSave
        pendingSave = null
        if (uri != null && result != null) {
            scope.launch {
                runCatching { InvoiceSharing.copyTo(context, result.file, uri) }
                    .onSuccess { onMessage("Invoice saved") }
                    .onFailure { onMessage(it.message ?: "Could not save invoice") }
            }
        }
    }

    LaunchedEffect(pdf) {
        pdf?.let { result ->
            when (result.mode) {
                PdfMode.Share -> {
                    val intent = InvoiceSharing.shareIntent(context, result.file)
                    runCatching { context.startActivity(intent) }
                        .onFailure { onMessage(it.message ?: "No app available to share the invoice") }
                }
                PdfMode.Save -> {
                    pendingSave = result
                    saveDocument.launch(result.file.name)
                }
            }
            onConsumed()
        }
    }
}
