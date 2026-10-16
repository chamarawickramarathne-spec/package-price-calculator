package lk.hiresmcq.packcalc.ui.calculator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import lk.hiresmcq.packcalc.ui.LoadState
import lk.hiresmcq.packcalc.ui.calculator.tabs.EquipmentTab
import lk.hiresmcq.packcalc.ui.calculator.tabs.LaborProfitTab
import lk.hiresmcq.packcalc.ui.calculator.tabs.PackageDetailsTab
import lk.hiresmcq.packcalc.ui.calculator.tabs.PricingTab
import lk.hiresmcq.packcalc.ui.components.ErrorBox
import lk.hiresmcq.packcalc.ui.components.InvoicePdfEffect
import lk.hiresmcq.packcalc.ui.components.LoadingBox
import lk.hiresmcq.packcalc.util.CurrencyFormat

private val tabTitles = listOf("Details", "Equipment", "Labor & Profit", "Pricing")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    onBack: () -> Unit,
    onMenuClick: (() -> Unit)?,
    onDone: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val form by viewModel.form.collectAsStateWithLifecycle()
    val lines by viewModel.lines.collectAsStateWithLifecycle()
    val tab by viewModel.tab.collectAsStateWithLifecycle()
    val breakdown by viewModel.breakdown.collectAsStateWithLifecycle()
    val saving by viewModel.saving.collectAsStateWithLifecycle()
    val saved by viewModel.saved.collectAsStateWithLifecycle()
    val pdf by viewModel.pdf.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbar.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(saved) { if (saved) onDone() }
    InvoicePdfEffect(
        pdf = pdf,
        onConsumed = viewModel::consumePdf,
        onMessage = { message -> scope.launch { snackbarHostState.showSnackbar(message) } },
    )
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeSnackbar()
        }
    }

    val title = when {
        form.editingPackageId != null -> "Edit Package"
        form.sourceTemplateId != null -> "From Template"
        else -> "New Package"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (onMenuClick != null) {
                        IconButton(onClick = onMenuClick) {
                            Icon(Icons.Filled.Menu, contentDescription = "Open menu")
                        }
                    } else {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
            )
        },
        bottomBar = {
            CalculatorBottomBar(
                tab = tab,
                finalTotalText = CurrencyFormat.format(breakdown.finalTotal),
                saving = saving,
                onBackTab = viewModel::previousTab,
                onNextTab = viewModel::nextTab,
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
                    .fillMaxSize(),
            ) {
                TabRow(selectedTabIndex = tab) {
                    tabTitles.forEachIndexed { index, label ->
                        Tab(
                            selected = tab == index,
                            onClick = { viewModel.selectTab(index) },
                            text = { Text(label) },
                        )
                    }
                }
                when (tab) {
                    0 -> PackageDetailsTab(viewModel, form)
                    1 -> EquipmentTab(viewModel, current.data, lines)
                    2 -> LaborProfitTab(viewModel, form)
                    else -> PricingTab(
                        viewModel = viewModel,
                        form = form,
                        lines = lines,
                        breakdown = breakdown,
                        onSharePdf = { viewModel.requestPdf(PdfMode.Share) },
                        onSavePdf = { viewModel.requestPdf(PdfMode.Save) },
                    )
                }
            }
        }
    }
}
