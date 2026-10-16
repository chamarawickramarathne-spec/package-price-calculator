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
            profitPercentage = BigDecimal("0"),
        )
        assertEquals(BigDecimal("0.00"), result.equipmentTotal)
        assertEquals(BigDecimal("0.00"), result.extraCostsTotal)
        assertEquals(BigDecimal("0.00"), result.finalTotal)
    }

    @Test
    fun `equipment and labor totals are summed correctly`() {
        val result = PricingCalculator.calculate(
            lines = listOf(line("6000.00", 1), line("2000.00", 2)),
            laborHours = BigDecimal("3"),
            hourlyRate = BigDecimal("1000.00"),
            profitPercentage = BigDecimal("10"),
        )
        assertEquals(BigDecimal("10000.00"), result.equipmentTotal)
        assertEquals(BigDecimal("3000.00"), result.laborTotal)
        assertEquals(BigDecimal("13000.00"), result.subtotal)
        assertEquals(BigDecimal("1300.00"), result.profitAmount)
        assertEquals(BigDecimal("14300.00"), result.finalTotal)
    }

    @Test
    fun `extra costs join the subtotal and profit applies to all`() {
        val result = PricingCalculator.calculate(
            lines = listOf(line("1000.00", 1)),
            laborHours = BigDecimal("0"),
            hourlyRate = BigDecimal("0"),
            profitPercentage = BigDecimal("10"),
            extraCosts = ExtraCosts(
                extraGear = BigDecimal("500.00"),
                transportation = BigDecimal("200.00"),
                assistantPay = BigDecimal("150.00"),
                editing = BigDecimal("100.00"),
                additional = BigDecimal("50.00"),
            ),
        )
        assertEquals(BigDecimal("1000.00"), result.equipmentTotal)
        assertEquals(BigDecimal("1000.00"), result.extraCostsTotal)
        assertEquals(BigDecimal("2000.00"), result.subtotal)
        assertEquals(BigDecimal("200.00"), result.profitAmount)
        assertEquals(BigDecimal("2200.00"), result.finalTotal)
    }

    @Test
    fun `zero profit leaves subtotal unchanged`() {
        val result = PricingCalculator.calculate(
            lines = listOf(line("500.00", 1)),
            laborHours = BigDecimal("2"),
            hourlyRate = BigDecimal("500.00"),
            profitPercentage = BigDecimal("0"),
        )
        assertEquals(BigDecimal("0.00"), result.profitAmount)
        assertEquals(result.subtotal, result.finalTotal)
    }

    @Test
    fun `profit rounds half up to two decimals`() {
        val result = PricingCalculator.calculate(
            lines = listOf(line("10.05", 1)),
            laborHours = BigDecimal("0"),
            hourlyRate = BigDecimal("0"),
            profitPercentage = BigDecimal("7.5"),
        )
        // 10.05 * 7.5% = 0.75375 -> 0.75
        assertEquals(BigDecimal("0.75"), result.profitAmount)
        assertEquals(BigDecimal("10.80"), result.finalTotal)
    }

    @Test
    fun `line total multiplies unit value by quantity`() {
        val result = PricingCalculator.calculate(
            lines = listOf(line("1999.99", 3)),
            laborHours = BigDecimal("0"),
            hourlyRate = BigDecimal("0"),
            profitPercentage = BigDecimal("0"),
        )
        assertEquals(BigDecimal("5999.97"), result.equipmentTotal)
    }

    @Test
    fun `extra costs total rounds to two decimals`() {
        val extras = ExtraCosts(
            extraGear = BigDecimal("1.111"),
            transportation = BigDecimal("2.222"),
            assistantPay = BigDecimal("3.333"),
            editing = BigDecimal("4.444"),
            additional = BigDecimal("5.555"),
        )
        assertEquals(BigDecimal("16.67"), extras.total)
    }
}
