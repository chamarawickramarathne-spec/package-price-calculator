package lk.hiresmcq.packcalc.ui.calculator.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import lk.hiresmcq.packcalc.domain.EquipmentLine
import lk.hiresmcq.packcalc.domain.PricingBreakdown
import lk.hiresmcq.packcalc.ui.calculator.CalculatorForm
import lk.hiresmcq.packcalc.ui.calculator.CalculatorViewModel
import lk.hiresmcq.packcalc.ui.components.LabeledTextField
import lk.hiresmcq.packcalc.ui.components.PriceBreakdownCard
import lk.hiresmcq.packcalc.ui.components.SectionCard
import lk.hiresmcq.packcalc.util.CurrencyFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PricingTab(
    viewModel: CalculatorViewModel,
    form: CalculatorForm,
    lines: List<EquipmentLine>,
    breakdown: PricingBreakdown,
    onSharePdf: () -> Unit,
    onSavePdf: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PriceBreakdownCard(breakdown = breakdown, profitLabel = "Profit")

        SectionCard(title = "Equipment lines") {
            if (lines.isEmpty()) {
                Text(
                    text = "No equipment added.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                lines.forEach { line ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "${line.quantity} x ${line.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = CurrencyFormat.format(line.lineTotal),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }

        SectionCard(title = "Project notes") {
            LabeledTextField(
                value = form.notes,
                onValueChange = viewModel::onNotesChange,
                label = "Notes",
                placeholder = "Anything to remember for this shoot",
                singleLine = false,
            )
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
            Text(
                text = "Saving or sharing first stores the package so it gets an invoice number.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
