package lk.hiresmcq.packcalc.ui.templates

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import lk.hiresmcq.packcalc.domain.EquipmentLine
import lk.hiresmcq.packcalc.domain.PricingCalculator
import lk.hiresmcq.packcalc.ui.LoadState
import lk.hiresmcq.packcalc.ui.components.DecimalField
import lk.hiresmcq.packcalc.ui.components.ErrorBox
import lk.hiresmcq.packcalc.ui.components.EquipmentPickerSheet
import lk.hiresmcq.packcalc.ui.components.LabeledTextField
import lk.hiresmcq.packcalc.ui.components.LoadingBox
import lk.hiresmcq.packcalc.ui.components.QuantityStepper
import lk.hiresmcq.packcalc.ui.components.SectionCard
import lk.hiresmcq.packcalc.util.CurrencyFormat
import lk.hiresmcq.packcalc.util.NumberInput

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateEditorScreen(
    viewModel: TemplateEditorViewModel,
    onBack: () -> Unit,
    onDone: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val form by viewModel.form.collectAsStateWithLifecycle()
    val lines by viewModel.lines.collectAsStateWithLifecycle()
    val saving by viewModel.saving.collectAsStateWithLifecycle()
    val saved by viewModel.saved.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbar.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var showPicker by remember { mutableStateOf(false) }

    LaunchedEffect(saved) {
        if (saved) onDone()
    }
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeSnackbar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Template") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        bottomBar = {
            TemplateBottomBar(
                startingPrice = startingPriceOf(lines, form),
                itemCount = lines.sumOf { it.quantity },
                saving = saving,
                onSave = viewModel::save,
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

            is LoadState.Ready -> Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SectionCard(title = "Template details") {
                    LabeledTextField(
                        value = form.name,
                        onValueChange = viewModel::onNameChange,
                        label = "Template name *",
                        placeholder = "e.g. Wedding Essentials",
                        isError = form.showNameError,
                        supportingText = if (form.showNameError) "Required" else null,
                    )
                    LabeledTextField(
                        value = form.description,
                        onValueChange = viewModel::onDescriptionChange,
                        label = "Description",
                        placeholder = "Optional",
                    )
                }

                SectionCard(title = "Labor & margin") {
                    DecimalField(
                        value = form.laborHoursText,
                        onValueChange = viewModel::onLaborHoursChange,
                        label = "Event hours",
                        prefix = "hrs",
                    )
                    DecimalField(
                        value = form.hourlyRateText,
                        onValueChange = viewModel::onHourlyRateChange,
                        label = "Hourly rate",
                        prefix = "Rs.",
                    )
                    DecimalField(
                        value = form.marginText,
                        onValueChange = viewModel::onMarginChange,
                        label = "Margin percentage",
                        prefix = "%",
                    )
                }

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
                            TemplateLineRow(
                                line = line,
                                onQuantityChange = { qty ->
                                    viewModel.setLineQuantity(line.equipmentId, qty)
                                },
                                onRemove = { viewModel.setLineQuantity(line.equipmentId, 0) },
                            )
                        }
                    }
                }
            }
        }
    }

    if (showPicker) {
        val current = state
        if (current is LoadState.Ready) {
            EquipmentPickerSheet(
                categories = current.data.categories,
                equipment = current.data.activeEquipment,
                selectedQuantities = lines.associate { it.equipmentId to it.quantity },
                onSetQuantity = viewModel::setQuantity,
                onDismiss = { showPicker = false },
            )
        }
    }
}

private fun startingPriceOf(
    lines: List<EquipmentLine>,
    form: TemplateForm,
) = PricingCalculator.calculate(
    lines = lines,
    laborHours = NumberInput.toBigDecimal(form.laborHoursText),
    hourlyRate = NumberInput.toBigDecimal(form.hourlyRateText),
    profitPercentage = NumberInput.toBigDecimal(form.marginText),
).finalTotal

@Composable
private fun TemplateLineRow(
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
            IconButton(onClick = onRemove, modifier = Modifier.padding(start = 4.dp)) {
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

@Composable
private fun TemplateBottomBar(
    startingPrice: java.math.BigDecimal,
    itemCount: Int,
    saving: Boolean,
    onSave: () -> Unit,
) {
    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (itemCount == 0) "No items" else "$itemCount items  ·  Starting price",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = CurrencyFormat.format(startingPrice),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Button(onClick = onSave, enabled = !saving) {
                Text(if (saving) "Saving..." else "Save")
            }
        }
    }
}
