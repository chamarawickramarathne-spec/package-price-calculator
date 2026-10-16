package lk.hiresmcq.packcalc.domain

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Additional flat (LKR) costs added to a package's subtotal before profit.
 */
data class ExtraCosts(
    val extraGear: BigDecimal = BigDecimal.ZERO,
    val transportation: BigDecimal = BigDecimal.ZERO,
    val assistantPay: BigDecimal = BigDecimal.ZERO,
    val editing: BigDecimal = BigDecimal.ZERO,
    val additional: BigDecimal = BigDecimal.ZERO,
) {
    val total: BigDecimal
        get() = extraGear
            .add(transportation)
            .add(assistantPay)
            .add(editing)
            .add(additional)
            .setScale(EquipmentLine.SCALE, RoundingMode.HALF_UP)

    companion object {
        val ZERO = ExtraCosts()
    }
}
