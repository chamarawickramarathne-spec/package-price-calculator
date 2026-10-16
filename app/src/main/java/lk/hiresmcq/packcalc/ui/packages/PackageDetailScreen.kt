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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import lk.hiresmcq.packcalc.domain.PricingCalculator
import lk.hiresmcq.packcalc.ui.LoadState
import lk.hiresmcq.packcalc.ui.components.ErrorBox
import lk.hiresmcq.packcalc.ui.components.LoadingBox
import lk.hiresmcq.packcalc.ui.components.PriceBreakdownCard
import lk.hiresmcq.packcalc.ui.components.SectionCard
import lk.hiresmcq.packcalc.util.CurrencyFormat
import lk.hiresmcq.packcalc.util.NumberInput
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageDetailScreen(
    viewModel: PackageDetailViewModel,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbar.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeSnackbar()
        }
    }

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
                        onClick = { (state as? LoadState.Ready)?.data?.pkg?.id?.let(onEdit) },
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

            is LoadState.Ready -> {
                val data = current.data
                val pkg = data.pkg
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SectionCard(title = "Package details") {
                        DetailRow("Package", pkg.packageName)
                        pkg.clientName?.let {
                            if (it.isNotBlank()) DetailRow("Client", it)
                        }
                        DetailRow(
                            label = "Labor",
                            value = "${NumberInput.toDisplay(pkg.laborHours)} hrs  ×  " +
                                CurrencyFormat.format(BigDecimal.valueOf(pkg.hourlyRate ?: 0.0)),
                        )
                        DetailRow(
                            label = "Margin",
                            value = "${NumberInput.toDisplay(pkg.marginPercentage)}%",
                        )
                    }

                    if (data.lines.isNotEmpty()) {
                        SectionCard(title = "Equipment") {
                            data.lines.forEach { line ->
                                EquipmentRow(line)
                            }
                        }
                    }

                    PriceBreakdownCard(
                        breakdown = PricingCalculator.calculate(
                            lines = data.lines,
                            laborHours = BigDecimal.valueOf(pkg.laborHours ?: 0.0),
                            hourlyRate = BigDecimal.valueOf(pkg.hourlyRate ?: 0.0),
                            marginPercentage = BigDecimal.valueOf(pkg.marginPercentage ?: 0.0),
                        ),
                        marginLabel = "Margin",
                    )
                }
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
                Text(
                    text = line.name,
                    style = MaterialTheme.typography.bodyLarge,
                )
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
