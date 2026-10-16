package lk.hiresmcq.packcalc.util

import java.math.BigDecimal

object NumberInput {

    /** Keeps digits and a single decimal point; ignores everything else. */
    fun sanitizeDecimal(raw: String): String {
        val filtered = raw.filter { it.isDigit() || it == '.' }
        val firstDot = filtered.indexOf('.')
        if (firstDot == -1) return filtered
        val head = filtered.substring(0, firstDot + 1)
        val tail = filtered.substring(firstDot + 1).replace(".", "")
        return head + tail
    }

    fun toBigDecimal(text: String): BigDecimal =
        text.trim().toBigDecimalOrNull() ?: BigDecimal.ZERO

    /** Friendly text for editing a stored value, e.g. 3.00 -> "3", 3.50 -> "3.5". */
    fun toDisplay(value: BigDecimal): String {
        val stripped = value.stripTrailingZeros()
        return if (stripped.scale() < 0) stripped.setScale(0).toPlainString() else stripped.toPlainString()
    }

    fun toDisplay(value: Double?): String = toDisplay(BigDecimal.valueOf(value ?: 0.0))
}
