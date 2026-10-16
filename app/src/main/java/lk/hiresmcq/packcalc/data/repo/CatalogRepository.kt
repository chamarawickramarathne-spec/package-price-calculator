package lk.hiresmcq.packcalc.data.repo

import lk.hiresmcq.packcalc.data.model.CategoryDto
import lk.hiresmcq.packcalc.data.model.EquipmentDto
import lk.hiresmcq.packcalc.data.model.EquipmentTypeDto
import lk.hiresmcq.packcalc.data.remote.SupabaseApi

class CatalogRepository(private val api: SupabaseApi) {

    suspend fun loadCategories(includeInactive: Boolean): Result<List<CategoryDto>> = safeApiCall {
        api.getCategoryTypes(order = "display_order.asc,id.asc")
            .filter { includeInactive || (it.isActive ?: 1) == 1 }
    }

    suspend fun loadTypes(includeInactive: Boolean): Result<List<EquipmentTypeDto>> = safeApiCall {
        api.getEquipmentTypes(order = "type.asc")
            .filter { includeInactive || (it.isActive ?: 1) == 1 }
    }

    suspend fun loadEquipment(includeInactive: Boolean): Result<List<EquipmentDto>> = safeApiCall {
        api.getEquipment(
            order = "name.asc",
            isActive = if (includeInactive) null else "eq.1",
        )
    }

    suspend fun addEquipment(dto: EquipmentDto): Result<EquipmentDto> = safeApiCall {
        api.insertEquipment(dto).first()
    }

    suspend fun addCategory(dto: CategoryDto): Result<CategoryDto> = safeApiCall {
        api.insertCategory(dto).first()
    }

    suspend fun addType(dto: EquipmentTypeDto): Result<EquipmentTypeDto> = safeApiCall {
        api.insertEquipmentType(dto).first()
    }
}
