package lk.hiresmcq.packcalc.ui.packages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import lk.hiresmcq.packcalc.domain.PackageQuote
import lk.hiresmcq.packcalc.ui.LoadState
import lk.hiresmcq.packcalc.ui.calculator.PdfMode
import lk.hiresmcq.packcalc.ui.components.ErrorBox
import lk.hiresmcq.packcalc.ui.components.InvoicePdfEffect
import lk.hiresmcq.packcalc.ui.components.LoadingBox
import lk.hiresmcq.packcalc.ui.components.PriceBreakdownCard
import lk.hiresmcq.packcalc.ui.components.SectionCard
import lk.hiresmcq.packcalc.util.CurrencyFormat
import lk.hiresmcq.packcalc.util.NumberInput

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageDetailScreen(
    viewModel: PackageDetailViewModel,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pdf by viewModel.pdf.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    InvoicePdfEffect(
        pdf = pdf,
        onConsumed = viewModel::consumePdf,
        onMessage = { message -> scope.launch { snackbarHostState.showSnackbar(message) } },
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Package") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { (state as? LoadState.Ready)?.data?.let { onEdit(it.id) } },
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit package")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        when (val current = state) {
            is LoadState.Loading -> LoadingBox(modifier = Modifier.padding(padding))
            is LoadState.Error -> ErrorBox(
                message = current.message,
                onRetry = viewModel::load,
                modifier = Modifier.padding(padding),
            )

            is LoadState.Ready -> PackageDetailContent(
                quote = current.data,
                modifier = Modifier.padding(padding),
                onSavePdf = { viewModel.requestPdf(PdfMode.Save) },
                onSharePdf = { viewModel.requestPdf(PdfMode.Share) },
            )
        }
    }
}

@Composable
private fun PackageDetailContent(
    quote: PackageQuote,
    modifier: Modifier,
    onSavePdf: () -> Unit,
    onSharePdf: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SectionCard(title = "Package details") {
            quote.invoiceNumber?.let { DetailRow("Invoice No.", it) }
            DetailRow("Package", quote.packageName)
            quote.clientName?.takeIf { it.isNotBlank() }?.let { DetailRow("Client", it) }
            quote.projectDate?.takeIf { it.isNotBlank() }?.let { DetailRow("Project date", it) }
            DetailRow(
                label = "Labor",
                value = "${NumberInput.toDisplay(quote.laborHours)} hrs  ×  " +
                    CurrencyFormat.format(quote.hourlyRate),
            )
            DetailRow("Profit", "${NumberInput.toDisplay(quote.profitPercentage)}%")
        }

        if (quote.lines.isNotEmpty()) {
            SectionCard(title = "Equipment") {
                quote.lines.forEach { line -> EquipmentRow(line) }
            }
        }

        val extras = quote.extraCosts
        if (extras.total.signum() > 0) {
            SectionCard(title = "Additional costs") {
                if (extras.extraGear.signum() > 0) DetailRow("Extra gear", CurrencyFormat.format(extras.extraGear))
                if (extras.transportation.signum() > 0) DetailRow("Transportation", CurrencyFormat.format(extras.transportation))
                if (extras.assistantPay.signum() > 0) DetailRow("Assistant pay", CurrencyFormat.format(extras.assistantPay))
                if (extras.editing.signum() > 0) DetailRow("Editing / retouching", CurrencyFormat.format(extras.editing))
                if (extras.additional.signum() > 0) DetailRow("Additional", CurrencyFormat.format(extras.additional))
            }
        }

        PriceBreakdownCard(breakdown = quote.breakdown, profitLabel = "Profit")

        quote.notes?.takeIf { it.isNotBlank() }?.let { notes ->
            SectionCard(title = "Project notes") {
                Text(text = notes, style = MaterialTheme.typography.bodyMedium)
            }
        }

        SectionCard(title = "Invoice") {
            FilledTonalButton(onClick = onSavePdf, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.PictureAsPdf, contentDescription = null)
                Text("Save PDF invoice")
            }
            OutlinedButton(onClick = onSharePdf, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Share, contentDescription = null)
                Text("Share PDF invoice")
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun EquipmentRow(line: lk.hiresmcq.packcalc.domain.EquipmentLine) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = line.name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "${line.quantity} × ${CurrencyFormat.format(line.unitValue)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = CurrencyFormat.format(line.lineTotal),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}
