package lk.hiresmcq.packcalc.ui.equipment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lk.hiresmcq.packcalc.data.model.CategoryDto
import lk.hiresmcq.packcalc.data.model.EquipmentTypeDto
import lk.hiresmcq.packcalc.data.repo.CatalogRepository
import lk.hiresmcq.packcalc.ui.LoadState

data class TaxonomyData(
    val categories: List<CategoryDto>,
    val types: List<EquipmentTypeDto>,
)

class TaxonomyViewModel(
    private val repository: CatalogRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<LoadState<TaxonomyData>>(LoadState.Loading)
    val state: StateFlow<LoadState<TaxonomyData>> = _state.asStateFlow()

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
            val categoryList = categories.getOrNull()
            val typeList = types.getOrNull()
            when {
                categoryList == null -> _state.value =
                    LoadState.Error(categories.exceptionOrNull()?.message ?: "Failed to load categories")
                typeList == null -> _state.value =
                    LoadState.Error(types.exceptionOrNull()?.message ?: "Failed to load types")
                else -> _state.value = LoadState.Ready(TaxonomyData(categoryList, typeList))
            }
        }
    }

    fun saveCategory(id: Int?, name: String, description: String) {
        viewModelScope.launch {
            repository.saveCategory(
                CategoryDto(
                    id = id,
                    name = name.trim(),
                    description = description.trim().ifBlank { null },
                    displayOrder = null,
                    isActive = 1,
                ),
            )
                .onSuccess {
                    _snackbar.value = "Category saved"
                    load()
                }
                .onFailure { _snackbar.value = it.message }
        }
    }

    fun deleteCategory(id: Int) {
        viewModelScope.launch {
            repository.deleteCategory(id)
                .onSuccess {
                    _snackbar.value = "Category deleted"
                    load()
                }
                .onFailure { _snackbar.value = it.message }
        }
    }

    fun saveType(id: Int?, type: String) {
        viewModelScope.launch {
            repository.saveType(
                EquipmentTypeDto(id = id, type = type.trim(), isActive = 1),
            )
                .onSuccess {
                    _snackbar.value = "Type saved"
                    load()
                }
                .onFailure { _snackbar.value = it.message }
        }
    }

    fun deleteType(id: Int) {
        viewModelScope.launch {
            repository.deleteType(id)
                .onSuccess {
                    _snackbar.value = "Type deleted"
                    load()
                }
                .onFailure { _snackbar.value = it.message }
        }
    }

    fun consumeSnackbar() {
        _snackbar.value = null
    }
}
