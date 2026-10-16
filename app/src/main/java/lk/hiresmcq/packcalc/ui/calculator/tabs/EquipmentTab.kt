package lk.hiresmcq.packcalc.ui.calculator.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import lk.hiresmcq.packcalc.domain.EquipmentLine
import lk.hiresmcq.packcalc.ui.calculator.CalculatorData
import lk.hiresmcq.packcalc.ui.calculator.CalculatorViewModel
import lk.hiresmcq.packcalc.ui.components.EquipmentPickerSheet
import lk.hiresmcq.packcalc.ui.components.QuantityStepper
import lk.hiresmcq.packcalc.ui.components.SectionCard
import lk.hiresmcq.packcalc.util.CurrencyFormat

@Composable
fun EquipmentTab(
    viewModel: CalculatorViewModel,
    data: CalculatorData,
    lines: List<EquipmentLine>,
) {
    var showPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SectionCard(title = "Equipment") {
            Button(onClick = { showPicker = true }) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Text("Add equipment")
            }
            if (lines.isEmpty()) {
                Text(
                    text = "No equipment added yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                lines.forEach { line ->
                    SelectedLineRow(
                        line = line,
                        onQuantityChange = { qty -> viewModel.setLineQuantity(line.equipmentId, qty) },
                        onRemove = { viewModel.setLineQuantity(line.equipmentId, 0) },
                    )
                }
            }
        }
    }

    if (showPicker) {
        EquipmentPickerSheet(
            categories = data.categories,
            equipment = data.activeEquipment,
            selectedQuantities = viewModel.selectedQuantities,
            onSetQuantity = viewModel::setQuantity,
            onDismiss = { showPicker = false },
        )
    }
}

@Composable
private fun SelectedLineRow(
    line: EquipmentLine,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = line.name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = CurrencyFormat.format(line.unitValue) + " each",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = CurrencyFormat.format(line.lineTotal),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            QuantityStepper(
                quantity = line.quantity,
                onQuantityChange = onQuantityChange,
                min = 0,
            )
            IconButton(onClick = onRemove, modifier = Modifier.size(36.dp)) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Remove ${line.name}",
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}
