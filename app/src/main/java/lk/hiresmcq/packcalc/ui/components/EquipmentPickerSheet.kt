package lk.hiresmcq.packcalc.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import lk.hiresmcq.packcalc.data.model.CategoryDto
import lk.hiresmcq.packcalc.data.model.EquipmentDto
import lk.hiresmcq.packcalc.util.CurrencyFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentPickerSheet(
    categories: List<CategoryDto>,
    equipment: List<EquipmentDto>,
    selectedQuantities: Map<Int, Int>,
    onSetQuantity: (EquipmentDto, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var query by remember { mutableStateOf("") }
    var categoryFilter by remember { mutableStateOf<Int?>(null) }

    val categoryNames = remember(categories) { categories.associate { it.id to it.name } }

    val filtered = remember(equipment, query, categoryFilter) {
        equipment.filter { item ->
            val matchesQuery = query.isBlank() ||
                item.name.contains(query, ignoreCase = true) ||
                item.model.contains(query, ignoreCase = true)
            val matchesCategory = categoryFilter == null || item.categoryId == categoryFilter
            matchesQuery && matchesCategory
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Add equipment",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
            )
            SearchField(
                value = query,
                onValueChange = { query = it },
                placeholder = "Search by model or name",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterChip(
                    selected = categoryFilter == null,
                    onClick = { categoryFilter = null },
                    label = { Text("All") },
                )
                categories.forEach { category ->
                    FilterChip(
                        selected = categoryFilter == category.id,
                        onClick = { categoryFilter = category.id },
                        label = { Text(category.name) },
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(top = 12.dp))
            if (filtered.isEmpty()) {
                Text(
                    text = "No equipment matches your search.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(24.dp),
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 460.dp),
                ) {
                    items(filtered, key = { it.id ?: 0 }) { item ->
                        EquipmentPickerRow(
                            item = item,
                            categoryName = categoryNames[item.categoryId].orEmpty(),
                            quantity = selectedQuantities[item.id] ?: 0,
                            onSetQuantity = { qty -> onSetQuantity(item, qty) },
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun EquipmentPickerRow(
    item: EquipmentDto,
    categoryName: String,
    quantity: Int,
    onSetQuantity: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (quantity == 0) onSetQuantity(1) }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = listOf(item.model, categoryName)
                    .filter { it.isNotBlank() }
                    .joinToString("  ·  "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = CurrencyFormat.format(item.value),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        if (quantity > 0) {
            QuantityStepper(
                quantity = quantity,
                onQuantityChange = onSetQuantity,
                min = 0,
            )
        } else {
            IconButton(onClick = { onSetQuantity(1) }) {
                Icon(Icons.Filled.Add, contentDescription = "Add ${item.name}")
            }
        }
    }
}
