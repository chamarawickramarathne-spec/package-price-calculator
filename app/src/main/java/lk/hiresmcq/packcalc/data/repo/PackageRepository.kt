package lk.hiresmcq.packcalc.data.repo

import lk.hiresmcq.packcalc.data.model.PackageDto
import lk.hiresmcq.packcalc.data.model.PackageEquipmentDto
import lk.hiresmcq.packcalc.data.remote.SupabaseApi

class PackageRepository(private val api: SupabaseApi) {

    suspend fun loadPackages(): Result<List<PackageDto>> = safeApiCall {
        api.getPackages(order = "created_at.desc,id.desc")
    }

    suspend fun loadPackage(id: Int): Result<PackageDto> = safeApiCall {
        api.getPackages(order = "id.asc").first { it.id == id }
    }

    suspend fun loadPackageEquipment(packageId: Int): Result<List<PackageEquipmentDto>> = safeApiCall {
        api.getPackageEquipment(order = "id.asc", packageId = "eq.$packageId")
    }

    suspend fun savePackage(
        dto: PackageDto,
        lines: List<PackageEquipmentDto>,
    ): Result<PackageDto> = safeApiCall {
        val saved = if (dto.id == null) {
            api.insertPackage(dto).first()
        } else {
            api.updatePackage("eq.${dto.id}", dto).first()
        }
        val packageId = requireNotNull(saved.id) { "Saved package has no id" }
        api.deletePackageEquipment("eq.$packageId")
        if (lines.isNotEmpty()) {
            val reloaded = lines.map {
                PackageEquipmentDto(
                    packageId = packageId,
                    equipmentId = it.equipmentId,
                    quantity = it.quantity ?: 1,
                    unitValue = it.unitValue,
                    totalValue = it.totalValue,
                )
            }
            api.insertPackageEquipment(reloaded)
        }
        saved
    }

    suspend fun deletePackage(id: Int): Result<Unit> = safeApiCall {
        api.deletePackage("eq.$id")
    }
}
