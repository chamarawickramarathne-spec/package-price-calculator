package lk.hiresmcq.packcalc.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TemplateDto(
    val id: Int? = null,
    val name: String,
    val description: String? = null,
    @SerialName("labor_hours") val laborHours: Double? = null,
    @SerialName("hourly_rate") val hourlyRate: Double? = null,
    @SerialName("margin_percentage") val marginPercentage: Double? = null,
    @SerialName("is_active") val isActive: Int? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
)

@Serializable
data class TemplateEquipmentDto(
    val id: Int? = null,
    @SerialName("template_id") val templateId: Int,
    @SerialName("equipment_id") val equipmentId: Int,
    val quantity: Int? = null,
    @SerialName("created_at") val createdAt: String? = null,
)
