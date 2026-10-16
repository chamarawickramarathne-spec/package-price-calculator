package lk.hiresmcq.packcalc.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import lk.hiresmcq.packcalc.domain.PricingBreakdown
import lk.hiresmcq.packcalc.util.CurrencyFormat

@Composable
fun PriceBreakdownCard(
    breakdown: PricingBreakdown,
    modifier: Modifier = Modifier,
    marginLabel: String? = null,
) {
    SectionCard(title = "Price breakdown", modifier = modifier) {
        BreakdownRow("Equipment", CurrencyFormat.format(breakdown.equipmentTotal))
        BreakdownRow("Labor", CurrencyFormat.format(breakdown.laborTotal))
        BreakdownRow("Subtotal", CurrencyFormat.format(breakdown.subtotal))
        BreakdownRow(
            label = marginLabel ?: "Margin",
            value = CurrencyFormat.format(breakdown.marginAmount),
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
        ) {
            Text(
                text = "Final Total",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = CurrencyFormat.format(breakdown.finalTotal),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun BreakdownRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
