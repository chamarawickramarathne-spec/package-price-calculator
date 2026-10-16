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

@Serializable
data class PackageDto(
    val id: Int? = null,
    @SerialName("package_name") val packageName: String,
    @SerialName("client_name") val clientName: String? = null,
    @SerialName("template_id") val templateId: Int? = null,
    @SerialName("labor_hours") val laborHours: Double? = null,
    @SerialName("hourly_rate") val hourlyRate: Double? = null,
    @SerialName("margin_percentage") val marginPercentage: Double? = null,
    @SerialName("equipment_total") val equipmentTotal: Double? = null,
    @SerialName("labor_total") val laborTotal: Double? = null,
    val subtotal: Double? = null,
    @SerialName("margin_amount") val marginAmount: Double? = null,
    @SerialName("final_total") val finalTotal: Double? = null,
    val notes: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
)

@Serializable
data class PackageEquipmentDto(
    val id: Int? = null,
    @SerialName("package_id") val packageId: Int,
    @SerialName("equipment_id") val equipmentId: Int,
    val quantity: Int? = null,
    @SerialName("unit_value") val unitValue: Double,
    @SerialName("total_value") val totalValue: Double,
    @SerialName("created_at") val createdAt: String? = null,
)
