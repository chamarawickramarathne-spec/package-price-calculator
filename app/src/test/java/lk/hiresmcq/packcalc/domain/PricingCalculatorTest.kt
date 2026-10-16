package lk.hiresmcq.packcalc.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class PricingCalculatorTest {

    private fun line(unit: String, qty: Int) = EquipmentLine(
        equipmentId = 1,
        name = "Test",
        unitValue = BigDecimal(unit),
        quantity = qty,
    )

    @Test
    fun `empty package produces zero breakdown`() {
        val result = PricingCalculator.calculate(
            lines = emptyList(),
            laborHours = BigDecimal("0"),
            hourlyRate = BigDecimal("0"),
            marginPercentage = BigDecimal("0"),
        )
        assertEquals(BigDecimal("0.00"), result.equipmentTotal)
        assertEquals(BigDecimal("0.00"), result.finalTotal)
    }

    @Test
    fun `equipment and labor totals are summed correctly`() {
        val result = PricingCalculator.calculate(
            lines = listOf(line("6000.00", 1), line("2000.00", 2)),
            laborHours = BigDecimal("3"),
            hourlyRate = BigDecimal("1000.00"),
            marginPercentage = BigDecimal("10"),
        )
        assertEquals(BigDecimal("10000.00"), result.equipmentTotal)
        assertEquals(BigDecimal("3000.00"), result.laborTotal)
        assertEquals(BigDecimal("13000.00"), result.subtotal)
        assertEquals(BigDecimal("1300.00"), result.marginAmount)
        assertEquals(BigDecimal("14300.00"), result.finalTotal)
    }

    @Test
    fun `zero margin leaves subtotal unchanged`() {
        val result = PricingCalculator.calculate(
            lines = listOf(line("500.00", 1)),
            laborHours = BigDecimal("2"),
            hourlyRate = BigDecimal("500.00"),
            marginPercentage = BigDecimal("0"),
        )
        assertEquals(BigDecimal("0.00"), result.marginAmount)
        assertEquals(result.subtotal, result.finalTotal)
    }

    @Test
    fun `margin rounds half up to two decimals`() {
        val result = PricingCalculator.calculate(
            lines = listOf(line("10.05", 1)),
            laborHours = BigDecimal("0"),
            hourlyRate = BigDecimal("0"),
            marginPercentage = BigDecimal("7.5"),
        )
        // 10.05 * 7.5% = 0.75375 -> 0.75
        assertEquals(BigDecimal("0.75"), result.marginAmount)
        assertEquals(BigDecimal("10.80"), result.finalTotal)
    }

    @Test
    fun `line total multiplies unit value by quantity`() {
        val result = PricingCalculator.calculate(
            lines = listOf(line("1999.99", 3)),
            laborHours = BigDecimal("0"),
            hourlyRate = BigDecimal("0"),
            marginPercentage = BigDecimal("0"),
        )
        assertEquals(BigDecimal("5999.97"), result.equipmentTotal)
    }
}
