package lk.hiresmcq.packcalc.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: Int? = null,
    val name: String,
    val description: String? = null,
    @SerialName("display_order") val displayOrder: Int? = null,
    @SerialName("is_active") val isActive: Int? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
)

@Serializable
data class EquipmentTypeDto(
    val id: Int? = null,
    val type: String,
    @SerialName("is_active") val isActive: Int? = null,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class EquipmentDto(
    val id: Int? = null,
    @SerialName("category_id") val categoryId: Int,
    val type: Int,
    val model: String,
    val name: String,
    val value: Double? = null,
    val description: String? = null,
    @SerialName("is_active") val isActive: Int? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
)
