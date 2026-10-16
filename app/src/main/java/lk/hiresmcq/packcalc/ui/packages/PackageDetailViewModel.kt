package lk.hiresmcq.packcalc.ui.packages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lk.hiresmcq.packcalc.data.model.EquipmentDto
import lk.hiresmcq.packcalc.data.model.PackageDto
import lk.hiresmcq.packcalc.data.repo.CatalogRepository
import lk.hiresmcq.packcalc.data.repo.PackageRepository
import lk.hiresmcq.packcalc.domain.EquipmentLine
import lk.hiresmcq.packcalc.ui.LoadState
import java.math.BigDecimal

data class PackageDetailData(
    val pkg: PackageDto,
    val lines: List<EquipmentLine>,
)

class PackageDetailViewModel(
    private val packageRepository: PackageRepository,
    private val catalogRepository: CatalogRepository,
    private val packageId: Int,
) : ViewModel() {

    private val _state = MutableStateFlow<LoadState<PackageDetailData>>(LoadState.Loading)
    val state: StateFlow<LoadState<PackageDetailData>> = _state.asStateFlow()

    private val _snackbar = MutableStateFlow<String?>(null)
    val snackbar: StateFlow<String?> = _snackbar.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = LoadState.Loading
            val pkgResult = packageRepository.loadPackage(packageId)
            val pkg = pkgResult.getOrNull()
            if (pkg == null) {
                _state.value = LoadState.Error(
                    pkgResult.exceptionOrNull()?.message ?: "Package not found",
                )
                return@launch
            }

            val equipmentResult = catalogRepository.loadEquipment(includeInactive = true)
            val equipmentById = equipmentResult.getOrNull()
                ?.mapNotNull { item -> item.id?.let { it to item } }
                ?.toMap()

            val linesResult = packageRepository.loadPackageEquipment(packageId)
            val storedLines = linesResult.getOrNull()
            if (storedLines == null) {
                _state.value = LoadState.Error(
                    linesResult.exceptionOrNull()?.message ?: "Failed to load package items",
                )
                return@launch
            }

            val lines = storedLines.map { line ->
                val equipment = equipmentById?.get(line.equipmentId)
                EquipmentLine(
                    equipmentId = line.equipmentId,
                    name = equipment?.name ?: equipmentName(line.equipmentId, equipmentById),
                    unitValue = BigDecimal.valueOf(line.unitValue),
                    quantity = line.quantity ?: 1,
                )
            }
            _state.value = LoadState.Ready(PackageDetailData(pkg = pkg, lines = lines))
        }
    }

    private fun equipmentName(id: Int, byId: Map<Int, EquipmentDto>?): String =
        byId?.get(id)?.name ?: "Item #$id"

    fun consumeSnackbar() {
        _snackbar.value = null
    }
}
