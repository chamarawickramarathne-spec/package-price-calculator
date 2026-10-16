package lk.hiresmcq.packcalc.ui.equipment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lk.hiresmcq.packcalc.data.model.CategoryDto
import lk.hiresmcq.packcalc.data.model.EquipmentDto
import lk.hiresmcq.packcalc.data.model.EquipmentTypeDto
import lk.hiresmcq.packcalc.data.repo.CatalogRepository
import lk.hiresmcq.packcalc.ui.LoadState
import lk.hiresmcq.packcalc.util.NumberInput
import java.math.BigDecimal

data class EquipmentFormState(
    val name: String = "",
    val model: String = "",
    val valueText: String = "",
    val categoryId: Int? = null,
    val typeId: Int? = null,
    val isActive: Boolean = true,
    val showErrors: Boolean = false,
) {
    val nameValid get() = name.isNotBlank()
    val modelValid get() = model.isNotBlank()
    val categoryValid get() = categoryId != null
    val typeValid get() = typeId != null
}

data class EquipmentEditorData(
    val categories: List<CategoryDto>,
    val types: List<EquipmentTypeDto>,
    val existingId: Int?,
)

class EquipmentEditorViewModel(
    private val repository: CatalogRepository,
    private val equipmentId: Int?,
) : ViewModel() {

    private val _state = MutableStateFlow<LoadState<EquipmentEditorData>>(LoadState.Loading)
    val state: StateFlow<LoadState<EquipmentEditorData>> = _state.asStateFlow()

    private val _form = MutableStateFlow(EquipmentFormState())
    val form: StateFlow<EquipmentFormState> = _form.asStateFlow()

    private val _saving = MutableStateFlow(false)
    val saving: StateFlow<Boolean> = _saving.asStateFlow()

    private val _snackbar = MutableStateFlow<String?>(null)
    val snackbar: StateFlow<String?> = _snackbar.asStateFlow()

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved.asStateFlow()

    val isEditing: Boolean = equipmentId != null

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = LoadState.Loading
            val categories = repository.loadCategories(includeInactive = false)
            val types = repository.loadTypes(includeInactive = false)
            val categoryList = categories.getOrNull()
            val typeList = types.getOrNull()

            if (categoryList == null || typeList == null) {
                val message = categories.exceptionOrNull()?.message
                    ?: types.exceptionOrNull()?.message
                    ?: "Failed to load catalog"
                _state.value = LoadState.Error(message)
                return@launch
            }

            var existing: EquipmentDto? = null
            if (equipmentId != null) {
                val result = repository.loadEquipmentById(equipmentId)
                existing = result.getOrNull()
                if (existing == null) {
                    _state.value = LoadState.Error(
                        result.exceptionOrNull()?.message ?: "Equipment not found",
                    )
                    return@launch
                }
            }

            _state.value = LoadState.Ready(
                EquipmentEditorData(
                    categories = categoryList,
                    types = typeList,
                    existingId = existing?.id,
                ),
            )
            if (existing != null) {
                _form.value = EquipmentFormState(
                    name = existing.name,
                    model = existing.model,
                    valueText = NumberInput.toDisplay(existing.value),
                    categoryId = existing.categoryId,
                    typeId = existing.type,
                    isActive = (existing.isActive ?: 1) == 1,
                )
            }
        }
    }

    fun onNameChange(value: String) = _form.update { it.copy(name = value) }
    fun onModelChange(value: String) = _form.update { it.copy(model = value) }
    fun onValueChange(value: String) = _form.update { it.copy(valueText = value) }
    fun onCategoryChange(id: Int) = _form.update { it.copy(categoryId = id) }
    fun onTypeChange(id: Int) = _form.update { it.copy(typeId = id) }
    fun onActiveChange(active: Boolean) = _form.update { it.copy(isActive = active) }

    fun save() {
        val current = _form.value
        if (!current.nameValid || !current.modelValid || !current.categoryValid || !current.typeValid) {
            _form.update { it.copy(showErrors = true) }
            _snackbar.value = "Please fill in name, model, category and type."
            return
        }
        val dto = EquipmentDto(
            id = equipmentId,
            categoryId = current.categoryId!!,
            type = current.typeId!!,
            model = current.model.trim(),
            name = current.name.trim(),
            value = NumberInput.toBigDecimal(current.valueText).let {
                if (it < BigDecimal.ZERO) BigDecimal.ZERO else it
            }.toDouble(),
            description = null,
            isActive = if (current.isActive) 1 else 0,
        )
        _saving.value = true
        viewModelScope.launch {
            repository.saveEquipment(dto)
                .onSuccess {
                    _saving.value = false
                    _saved.value = true
                }
                .onFailure {
                    _saving.value = false
                    _snackbar.value = it.message
                }
        }
    }

    fun delete() {
        val id = equipmentId ?: return
        _saving.value = true
        viewModelScope.launch {
            repository.deleteEquipment(id)
                .onSuccess {
                    _saving.value = false
                    _saved.value = true
                }
                .onFailure {
                    _saving.value = false
                    _snackbar.value = it.message
                }
        }
    }

    fun consumeSnackbar() {
        _snackbar.value = null
    }
}
