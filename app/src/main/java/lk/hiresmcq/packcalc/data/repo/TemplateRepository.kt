package lk.hiresmcq.packcalc.data.repo

import lk.hiresmcq.packcalc.data.model.TemplateDto
import lk.hiresmcq.packcalc.data.model.TemplateEquipmentDto
import lk.hiresmcq.packcalc.data.remote.SupabaseApi

class TemplateRepository(private val api: SupabaseApi) {

    suspend fun loadTemplates(includeInactive: Boolean): Result<List<TemplateDto>> = safeApiCall {
        api.getTemplates(
            order = "name.asc",
            isActive = if (includeInactive) null else "eq.1",
        )
    }

    suspend fun loadTemplateEquipment(): Result<List<TemplateEquipmentDto>> = safeApiCall {
        api.getTemplateEquipment(order = "id.asc", templateId = null)
    }

    suspend fun loadTemplate(id: Int): Result<TemplateDto> = safeApiCall {
        api.getTemplates(order = "id.asc", isActive = null).first { it.id == id }
    }

    suspend fun loadTemplateLines(templateId: Int): Result<List<TemplateEquipmentDto>> = safeApiCall {
        api.getTemplateEquipment(order = "id.asc", templateId = "eq.$templateId")
    }

    suspend fun saveTemplate(
        dto: TemplateDto,
        lines: List<TemplateEquipmentDto>,
    ): Result<TemplateDto> = safeApiCall {
        val saved = if (dto.id == null) {
            api.insertTemplate(dto).first()
        } else {
            api.updateTemplate("eq.${dto.id}", dto).first()
        }
        val templateId = requireNotNull(saved.id) { "Saved template has no id" }
        api.deleteTemplateEquipment("eq.$templateId")
        if (lines.isNotEmpty()) {
            val reloaded = lines.map {
                TemplateEquipmentDto(
                    templateId = templateId,
                    equipmentId = it.equipmentId,
                    quantity = it.quantity ?: 1,
                )
            }
            api.insertTemplateEquipment(reloaded)
        }
        saved
    }

    suspend fun deleteTemplate(id: Int): Result<Unit> = safeApiCall {
        api.deleteTemplate("eq.$id")
    }
}
