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
            id = null,
        )
    }

    suspend fun loadEquipmentById(id: Int): Result<EquipmentDto> = safeApiCall {
        api.getEquipment(order = "id.asc", isActive = null, id = "eq.$id").first()
    }

    suspend fun saveEquipment(dto: EquipmentDto): Result<EquipmentDto> = safeApiCall {
        val id = dto.id
        if (id == null) {
            api.insertEquipment(dto).first()
        } else {
            api.updateEquipment("eq.$id", dto).first()
        }
    }

    suspend fun deleteEquipment(id: Int): Result<Unit> = safeApiCall {
        api.deleteEquipment("eq.$id")
    }

    suspend fun saveCategory(dto: CategoryDto): Result<CategoryDto> = safeApiCall {
        val id = dto.id
        if (id == null) {
            api.insertCategory(dto).first()
        } else {
            api.updateCategory("eq.$id", dto).first()
        }
    }

    suspend fun deleteCategory(id: Int): Result<Unit> = safeApiCall {
        api.deleteCategory("eq.$id")
    }

    suspend fun saveType(dto: EquipmentTypeDto): Result<EquipmentTypeDto> = safeApiCall {
        val id = dto.id
        if (id == null) {
            api.insertEquipmentType(dto).first()
        } else {
            api.updateEquipmentType("eq.$id", dto).first()
        }
    }

    suspend fun deleteType(id: Int): Result<Unit> = safeApiCall {
        api.deleteEquipmentType("eq.$id")
    }
}
