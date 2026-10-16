package lk.hiresmcq.packcalc.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object CurrencyFormat {

    private val symbols = DecimalFormatSymbols(Locale.US)
    private val grouped = DecimalFormat("#,##0.00", symbols)

    fun format(amount: BigDecimal): String = "Rs. ${grouped.format(amount.setScale(2, RoundingMode.HALF_UP))}"

    fun format(value: Double?): String = format(BigDecimal.valueOf(value ?: 0.0))

    fun formatPlain(amount: BigDecimal): String = grouped.format(amount.setScale(2, RoundingMode.HALF_UP))
}
