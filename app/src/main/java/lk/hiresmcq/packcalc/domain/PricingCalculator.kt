package lk.hiresmcq.packcalc.domain

import java.math.BigDecimal
import java.math.RoundingMode

data class EquipmentLine(
    val equipmentId: Int,
    val name: String,
    val unitValue: BigDecimal,
    val quantity: Int,
) {
    val lineTotal: BigDecimal
        get() = unitValue.multiply(BigDecimal(quantity)).setScale(SCALE, RoundingMode.HALF_UP)

    companion object {
        const val SCALE = 2
    }
}

data class PricingBreakdown(
    val equipmentTotal: BigDecimal,
    val laborTotal: BigDecimal,
    val subtotal: BigDecimal,
    val marginAmount: BigDecimal,
    val finalTotal: BigDecimal,
) {
    companion object {
        val ZERO = PricingBreakdown(
            equipmentTotal = BigDecimal.ZERO.setScale(2),
            laborTotal = BigDecimal.ZERO.setScale(2),
            subtotal = BigDecimal.ZERO.setScale(2),
            marginAmount = BigDecimal.ZERO.setScale(2),
            finalTotal = BigDecimal.ZERO.setScale(2),
        )
    }
}

object PricingCalculator {

    private val HUNDRED = BigDecimal("100")

    fun calculate(
        lines: List<EquipmentLine>,
        laborHours: BigDecimal,
        hourlyRate: BigDecimal,
        marginPercentage: BigDecimal,
    ): PricingBreakdown {
        val equipmentTotal = lines
            .fold(BigDecimal.ZERO) { acc, line -> acc.add(line.lineTotal) }
            .money()

        val laborTotal = laborHours.multiply(hourlyRate).money()
        val subtotal = equipmentTotal.add(laborTotal).money()
        val marginAmount = subtotal
            .multiply(marginPercentage)
            .divide(HUNDRED, EquipmentLine.SCALE, RoundingMode.HALF_UP)
            .money()
        val finalTotal = subtotal.add(marginAmount).money()

        return PricingBreakdown(
            equipmentTotal = equipmentTotal,
            laborTotal = laborTotal,
            subtotal = subtotal,
            marginAmount = marginAmount,
            finalTotal = finalTotal,
        )
    }

    private fun BigDecimal.money(): BigDecimal =
        setScale(EquipmentLine.SCALE, RoundingMode.HALF_UP)
}
