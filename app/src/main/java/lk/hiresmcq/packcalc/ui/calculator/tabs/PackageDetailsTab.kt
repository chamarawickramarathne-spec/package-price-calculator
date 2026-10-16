package lk.hiresmcq.packcalc.ui.calculator.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lk.hiresmcq.packcalc.ui.calculator.CalculatorForm
import lk.hiresmcq.packcalc.ui.calculator.CalculatorViewModel
import lk.hiresmcq.packcalc.ui.components.LabeledTextField
import lk.hiresmcq.packcalc.ui.components.SectionCard
import java.time.Instant
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageDetailsTab(viewModel: CalculatorViewModel, form: CalculatorForm) {
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SectionCard(title = "Package details") {
            LabeledTextField(
                value = form.packageName,
                onValueChange = viewModel::onPackageNameChange,
                label = "Package name *",
                placeholder = "e.g. Wedding Shoot",
                isError = form.showNameError,
                supportingText = if (form.showNameError) "Required" else null,
            )
            LabeledTextField(
                value = form.clientName,
                onValueChange = viewModel::onClientNameChange,
                label = "Client name",
                placeholder = "Optional",
            )
        }

        SectionCard(title = "Project") {
            LabeledTextField(
                value = form.projectDate,
                onValueChange = viewModel::onProjectDateChange,
                label = "Project date",
                placeholder = "YYYY-MM-DD",
            )
            OutlinedButton(onClick = { showDatePicker = true }) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = null)
                Text("Pick a date")
            }
        }
    }

    if (showDatePicker) {
        val pickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let { millis ->
                            viewModel.onProjectDateChange(toIsoDate(millis))
                        }
                        showDatePicker = false
                    },
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }
}

private fun toIsoDate(millis: Long): String =
    Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate().toString()
