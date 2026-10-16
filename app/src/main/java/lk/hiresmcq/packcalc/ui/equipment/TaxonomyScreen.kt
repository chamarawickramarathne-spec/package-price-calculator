package lk.hiresmcq.packcalc.ui.equipment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import lk.hiresmcq.packcalc.ui.LoadState
import lk.hiresmcq.packcalc.ui.components.ConfirmDialog
import lk.hiresmcq.packcalc.ui.components.ErrorBox
import lk.hiresmcq.packcalc.ui.components.LabeledTextField
import lk.hiresmcq.packcalc.ui.components.LoadingBox

private data class EditTarget(val id: Int?, val name: String, val description: String = "")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxonomyScreen(
    viewModel: TaxonomyViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbar.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var tabIndex by remember { mutableIntStateOf(0) }
    var editorTarget by remember { mutableStateOf<EditTarget?>(null) }
    var deleteTarget by remember { mutableStateOf<EditTarget?>(null) }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeSnackbar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categories & Types") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { editorTarget = EditTarget(null, "") }) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            TabRow(selectedTabIndex = tabIndex) {
                Tab(
                    selected = tabIndex == 0,
                    onClick = { tabIndex = 0 },
                    text = { Text("Categories") },
                )
                Tab(
                    selected = tabIndex == 1,
                    onClick = { tabIndex = 1 },
                    text = { Text("Types") },
                )
            }
            when (val current = state) {
                is LoadState.Loading -> LoadingBox()
                is LoadState.Error -> ErrorBox(current.message, onRetry = viewModel::load)
                is LoadState.Ready -> if (tabIndex == 0) {
                    LazyColumn(contentPadding = PaddingValues(bottom = 96.dp)) {
                        items(current.data.categories, key = { it.id ?: 0 }) { category ->
                            TaxonomyRow(
                                title = category.name,
                                subtitle = category.description.orEmpty(),
                                onEdit = {
                                    editorTarget = EditTarget(
                                        category.id,
                                        category.name,
                                        category.description.orEmpty(),
                                    )
                                },
                                onDelete = {
                                    deleteTarget = EditTarget(category.id, category.name)
                                },
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        }
                    }
                } else {
                    LazyColumn(contentPadding = PaddingValues(bottom = 96.dp)) {
                        items(current.data.types, key = { it.id ?: 0 }) { type ->
                            TaxonomyRow(
                                title = type.type,
                                subtitle = "",
                                onEdit = { editorTarget = EditTarget(type.id, type.type) },
                                onDelete = { deleteTarget = EditTarget(type.id, type.type) },
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        }
                    }
                }
            }
        }
    }

    editorTarget?.let { target ->
        TaxonomyEditDialog(
            title = when {
                tabIndex == 1 -> if (target.id == null) "New type" else "Edit type"
                else -> if (target.id == null) "New category" else "Edit category"
            },
            showDescription = tabIndex == 0,
            initialName = target.name,
            initialDescription = target.description,
            onDismiss = { editorTarget = null },
            onSave = { name, description ->
                if (tabIndex == 0) {
                    viewModel.saveCategory(target.id, name, description)
                } else {
                    viewModel.saveType(target.id, name)
                }
                editorTarget = null
            },
        )
    }

    deleteTarget?.let { target ->
        ConfirmDialog(
            title = "Delete?",
            message = "Deleting \"${target.name}\" also removes every equipment item that belongs to it. This cannot be undone.",
            confirmLabel = "Delete",
            destructive = true,
            onConfirm = {
                val id = target.id
                if (id != null) {
                    if (tabIndex == 0) viewModel.deleteCategory(id) else viewModel.deleteType(id)
                }
                deleteTarget = null
            },
            onDismiss = { deleteTarget = null },
        )
    }
}

@Composable
private fun TaxonomyRow(
    title: String,
    subtitle: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        IconButton(onClick = onEdit) {
            Icon(Icons.Filled.Edit, contentDescription = "Edit $title")
        }
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Filled.Delete,
                contentDescription = "Delete $title",
                tint = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
private fun TaxonomyEditDialog(
    title: String,
    showDescription: Boolean,
    initialName: String,
    initialDescription: String,
    onDismiss: () -> Unit,
    onSave: (name: String, description: String) -> Unit,
) {
    var name by remember { mutableStateOf(initialName) }
    var description by remember { mutableStateOf(initialDescription) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                LabeledTextField(value = name, onValueChange = { name = it }, label = "Name")
                if (showDescription) {
                    Box(modifier = Modifier.padding(top = 8.dp)) {
                        LabeledTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = "Description",
                            singleLine = false,
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onSave(name, description) },
                enabled = name.isNotBlank(),
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
