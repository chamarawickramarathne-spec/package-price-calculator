package lk.hiresmcq.packcalc.ui.equipment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import lk.hiresmcq.packcalc.data.model.EquipmentDto
import lk.hiresmcq.packcalc.ui.LoadState
import lk.hiresmcq.packcalc.ui.components.EmptyBox
import lk.hiresmcq.packcalc.ui.components.ErrorBox
import lk.hiresmcq.packcalc.ui.components.LoadingBox
import lk.hiresmcq.packcalc.ui.components.SearchField
import lk.hiresmcq.packcalc.util.CurrencyFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentScreen(
    viewModel: EquipmentListViewModel,
    onAddEquipment: () -> Unit,
    onOpenTaxonomy: () -> Unit,
    onMenuClick: (() -> Unit)? = null,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Equipment") },
                navigationIcon = {
                    if (onMenuClick != null) {
                        IconButton(onClick = onMenuClick) {
                            Icon(Icons.Filled.Menu, contentDescription = "Open menu")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onOpenTaxonomy) {
                        Icon(Icons.Filled.Tune, contentDescription = "Manage categories and types")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddEquipment) {
                Icon(Icons.Filled.Add, contentDescription = "Add equipment")
            }
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val current = state) {
                is LoadState.Loading -> LoadingBox()
                is LoadState.Error -> ErrorBox(current.message, onRetry = viewModel::load)
                is LoadState.Ready -> EquipmentListContent(data = current.data)
            }
        }
    }
}

@Composable
private fun EquipmentListContent(
    data: EquipmentListData,
) {
    var query by remember { mutableStateOf("") }
    var categoryFilter by remember { mutableStateOf<Int?>(null) }

    val typeNames = remember(data.types) { data.types.associate { it.id to it.type } }
    val categoryNames = remember(data.categories) { data.categories.associate { it.id to it.name } }

    val filtered = remember(data.equipment, query, categoryFilter) {
        data.equipment.filter { item ->
            val matchesQuery = query.isBlank() ||
                item.name.contains(query, ignoreCase = true) ||
                item.model.contains(query, ignoreCase = true)
            val matchesCategory = categoryFilter == null || item.categoryId == categoryFilter
            matchesQuery && matchesCategory
        }
    }

    val grouped = remember(filtered, data.categories) {
        data.categories
            .map { cat -> cat to filtered.filter { it.categoryId == cat.id } }
            .filter { it.second.isNotEmpty() }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchField(
            value = query,
            onValueChange = { query = it },
            placeholder = "Search equipment",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = categoryFilter == null,
                onClick = { categoryFilter = null },
                label = { Text("All") },
            )
            data.categories.forEach { category ->
                FilterChip(
                    selected = categoryFilter == category.id,
                    onClick = { categoryFilter = category.id },
                    label = { Text(category.name) },
                )
            }
        }

        if (grouped.isEmpty()) {
            EmptyBox(
                title = "No equipment found",
                subtitle = "Try a different search, or add new equipment with the + button.",
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 96.dp),
            ) {
                grouped.forEach { (category, items) ->
                    item(key = "header_${category.id}") {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = 16.dp,
                                bottom = 4.dp,
                            ),
                        )
                    }
                    items(items, key = { it.id ?: 0 }) { item ->
                        EquipmentRow(
                            item = item,
                            typeName = typeNames[item.type].orEmpty(),
                            categoryName = categoryNames[item.categoryId].orEmpty(),
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun EquipmentRow(
    item: EquipmentDto,
    typeName: String,
    categoryName: String,
) {
    val inactive = (item.isActive ?: 1) == 0
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 10.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = listOf(item.model, typeName, categoryName)
                    .filter { it.isNotBlank() }
                    .joinToString("  ·  "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = CurrencyFormat.format(item.value) + if (inactive) "   (inactive)" else "",
                style = MaterialTheme.typography.bodyMedium,
                color = if (inactive) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                },
            )
        }
    }
}
