package lk.hiresmcq.packcalc.ui.equipment

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
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import lk.hiresmcq.packcalc.ui.LoadState
import lk.hiresmcq.packcalc.ui.components.DecimalField
import lk.hiresmcq.packcalc.ui.components.ErrorBox
import lk.hiresmcq.packcalc.ui.components.LabeledTextField
import lk.hiresmcq.packcalc.ui.components.LoadingBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentEditorScreen(
    viewModel: EquipmentEditorViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val form by viewModel.form.collectAsStateWithLifecycle()
    val saving by viewModel.saving.collectAsStateWithLifecycle()
    val saved by viewModel.saved.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbar.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(saved) {
        if (saved) onBack()
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
                title = { Text("New equipment") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        when (val current = state) {
            is LoadState.Loading -> androidx.compose.foundation.layout.Box(
                modifier = Modifier.padding(padding).fillMaxSize(),
            ) { LoadingBox() }

            is LoadState.Error -> androidx.compose.foundation.layout.Box(
                modifier = Modifier.padding(padding).fillMaxSize(),
            ) { ErrorBox(current.message, onRetry = viewModel::load) }

            is LoadState.Ready -> Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                LabeledTextField(
                    value = form.name,
                    onValueChange = viewModel::onNameChange,
                    label = "Name *",
                    placeholder = "e.g. Sony A7 IV",
                    isError = form.showErrors && !form.nameValid,
                    supportingText = if (form.showErrors && !form.nameValid) "Required" else null,
                )
                LabeledTextField(
                    value = form.model,
                    onValueChange = viewModel::onModelChange,
                    label = "Model / Brand *",
                    placeholder = "e.g. Sony",
                    isError = form.showErrors && !form.modelValid,
                    supportingText = if (form.showErrors && !form.modelValid) "Required" else null,
                )
                DecimalField(
                    value = form.valueText,
                    onValueChange = viewModel::onValueChange,
                    label = "Value",
                    prefix = "Rs.",
                )
                DropdownField(
                    label = "Category *",
                    options = current.data.categories.mapNotNull { cat ->
                        cat.id?.let { it to cat.name }
                    },
                    selectedId = form.categoryId,
                    onSelected = viewModel::onCategoryChange,
                    isError = form.showErrors && !form.categoryValid,
                )
                DropdownField(
                    label = "Type *",
                    options = current.data.types.mapNotNull { type ->
                        type.id?.let { it to type.type }
                    },
                    selectedId = form.typeId,
                    onSelected = viewModel::onTypeChange,
                    isError = form.showErrors && !form.typeValid,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Active", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "Inactive items are hidden when building packages.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(checked = form.isActive, onCheckedChange = viewModel::onActiveChange)
                }
                Button(
                    onClick = viewModel::save,
                    enabled = !saving,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(if (saving) "Saving..." else "Save equipment")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    label: String,
    options: List<Pair<Int, String>>,
    selectedId: Int?,
    onSelected: (Int) -> Unit,
    isError: Boolean,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.first == selectedId }?.second ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            isError = isError,
            modifier = Modifier
                .menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (id, name) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        onSelected(id)
                        expanded = false
                    },
                )
            }
        }
    }
}
