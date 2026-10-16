package lk.hiresmcq.packcalc.ui.equipment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lk.hiresmcq.packcalc.data.model.CategoryDto
import lk.hiresmcq.packcalc.data.model.EquipmentDto
import lk.hiresmcq.packcalc.data.model.EquipmentTypeDto
import lk.hiresmcq.packcalc.data.repo.CatalogRepository
import lk.hiresmcq.packcalc.ui.LoadState

data class EquipmentListData(
    val categories: List<CategoryDto>,
    val types: List<EquipmentTypeDto>,
    val equipment: List<EquipmentDto>,
)

class EquipmentListViewModel(
    private val repository: CatalogRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<LoadState<EquipmentListData>>(LoadState.Loading)
    val state: StateFlow<LoadState<EquipmentListData>> = _state.asStateFlow()

    private val _snackbar = MutableStateFlow<String?>(null)
    val snackbar: StateFlow<String?> = _snackbar.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = LoadState.Loading
            val categories = repository.loadCategories(includeInactive = true)
            val types = repository.loadTypes(includeInactive = true)
            val equipment = repository.loadEquipment(includeInactive = true)

            val categoryList = categories.getOrNull()
            val typeList = types.getOrNull()
            val equipmentList = equipment.getOrNull()
            when {
                categoryList == null -> _state.value =
                    LoadState.Error(categories.exceptionOrNull()?.message ?: "Failed to load categories")
                typeList == null -> _state.value =
                    LoadState.Error(types.exceptionOrNull()?.message ?: "Failed to load types")
                equipmentList == null -> _state.value =
                    LoadState.Error(equipment.exceptionOrNull()?.message ?: "Failed to load equipment")
                else -> _state.value =
                    LoadState.Ready(EquipmentListData(categoryList, typeList, equipmentList))
            }
        }
    }

    fun delete(equipment: EquipmentDto) {
        val id = equipment.id ?: return
        viewModelScope.launch {
            repository.deleteEquipment(id)
                .onSuccess {
                    _snackbar.value = "${equipment.name} deleted"
                    load()
                }
                .onFailure { _snackbar.value = it.message }
        }
    }

    fun consumeSnackbar() {
        _snackbar.value = null
    }
}
