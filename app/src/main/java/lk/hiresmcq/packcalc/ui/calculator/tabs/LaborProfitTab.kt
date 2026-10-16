package lk.hiresmcq.packcalc.ui.calculator.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lk.hiresmcq.packcalc.ui.calculator.CalculatorForm
import lk.hiresmcq.packcalc.ui.calculator.CalculatorViewModel
import lk.hiresmcq.packcalc.ui.components.DecimalField
import lk.hiresmcq.packcalc.ui.components.SectionCard

@Composable
fun LaborProfitTab(viewModel: CalculatorViewModel, form: CalculatorForm) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SectionCard(title = "Labor & profit") {
            DecimalField(
                value = form.laborHoursText,
                onValueChange = viewModel::onLaborHoursChange,
                label = "Labor hours",
                prefix = "hrs",
            )
            DecimalField(
                value = form.hourlyRateText,
                onValueChange = viewModel::onHourlyRateChange,
                label = "Hourly rate",
                prefix = "Rs.",
            )
            DecimalField(
                value = form.profitText,
                onValueChange = viewModel::onProfitChange,
                label = "Profit percentage",
                prefix = "%",
            )
        }

        SectionCard(title = "Additional costs") {
            DecimalField(
                value = form.extraGearText,
                onValueChange = viewModel::onExtraGearChange,
                label = "Extra gear cost",
                prefix = "Rs.",
            )
            DecimalField(
                value = form.transportationText,
                onValueChange = viewModel::onTransportationChange,
                label = "Transportation cost",
                prefix = "Rs.",
            )
            DecimalField(
                value = form.assistantPayText,
                onValueChange = viewModel::onAssistantPayChange,
                label = "Assistant pay",
                prefix = "Rs.",
            )
            DecimalField(
                value = form.editingText,
                onValueChange = viewModel::onEditingChange,
                label = "Editing / retouching cost",
                prefix = "Rs.",
            )
            DecimalField(
                value = form.additionalText,
                onValueChange = viewModel::onAdditionalChange,
                label = "Additional cost",
                prefix = "Rs.",
            )
        }
    }
}
