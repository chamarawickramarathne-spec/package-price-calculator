package lk.hiresmcq.packcalc.ui.calculator

import lk.hiresmcq.packcalc.data.model.CategoryDto
import lk.hiresmcq.packcalc.data.model.EquipmentDto
import lk.hiresmcq.packcalc.domain.EquipmentLine
import lk.hiresmcq.packcalc.domain.ExtraCosts
import lk.hiresmcq.packcalc.domain.PackageDraft
import lk.hiresmcq.packcalc.util.NumberInput
import java.io.File

const val LAST_TAB = 3

sealed interface CalculatorMode {
    data object New : CalculatorMode
    data class Edit(val packageId: Long) : CalculatorMode
    data class FromTemplate(val templateId: Int) : CalculatorMode
}

enum class PdfMode { Share, Save }

data class PdfResult(val file: File, val mode: PdfMode)

data class CalculatorData(
    val categories: List<CategoryDto>,
    val activeEquipment: List<EquipmentDto>,
)

data class CalculatorForm(
    val packageName: String = "",
    val clientName: String = "",
    val projectDate: String = "",
    val laborHoursText: String = "3",
    val hourlyRateText: String = "1000",
    val profitText: String = "10",
    val extraGearText: String = "",
    val transportationText: String = "",
    val assistantPayText: String = "",
    val editingText: String = "",
    val additionalText: String = "",
    val notes: String = "",
    val showNameError: Boolean = false,
    val sourceTemplateId: Int? = null,
    val editingPackageId: Long? = null,
) {
    fun extraCosts(): ExtraCosts = ExtraCosts(
        extraGear = NumberInput.toBigDecimal(extraGearText),
        transportation = NumberInput.toBigDecimal(transportationText),
        assistantPay = NumberInput.toBigDecimal(assistantPayText),
        editing = NumberInput.toBigDecimal(editingText),
        additional = NumberInput.toBigDecimal(additionalText),
    )

    fun toDraft(lines: List<EquipmentLine>): PackageDraft = PackageDraft(
        id = editingPackageId,
        packageName = packageName.trim(),
        clientName = clientName.trim().ifBlank { null },
        projectDate = projectDate.trim().ifBlank { null },
        lines = lines,
        laborHours = NumberInput.toBigDecimal(laborHoursText),
        hourlyRate = NumberInput.toBigDecimal(hourlyRateText),
        profitPercentage = NumberInput.toBigDecimal(profitText),
        extraCosts = extraCosts(),
        notes = notes.trim().ifBlank { null },
        sourceTemplateId = sourceTemplateId,
    )
}
