package lk.hiresmcq.packcalc.ui.templates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lk.hiresmcq.packcalc.data.model.CategoryDto
import lk.hiresmcq.packcalc.data.model.EquipmentDto
import lk.hiresmcq.packcalc.data.model.TemplateDto
import lk.hiresmcq.packcalc.data.model.TemplateEquipmentDto
import lk.hiresmcq.packcalc.data.repo.CatalogRepository
import lk.hiresmcq.packcalc.data.repo.TemplateRepository
import lk.hiresmcq.packcalc.domain.EquipmentLine
import lk.hiresmcq.packcalc.ui.LoadState
import lk.hiresmcq.packcalc.util.NumberInput
import java.math.BigDecimal

data class TemplateEditorData(
    val categories: List<CategoryDto>,
    val activeEquipment: List<EquipmentDto>,
    val existingId: Int?,
)

data class TemplateForm(
    val name: String = "",
    val description: String = "",
    val laborHoursText: String = "",
    val hourlyRateText: String = "",
    val marginText: String = "",
    val showNameError: Boolean = false,
)

class TemplateEditorViewModel(
    private val templateRepository: TemplateRepository,
    private val catalogRepository: CatalogRepository,
    private val templateId: Int?,
) : ViewModel() {

    val isEditing: Boolean = templateId != null

    private val _state = MutableStateFlow<LoadState<TemplateEditorData>>(LoadState.Loading)
    val state: StateFlow<LoadState<TemplateEditorData>> = _state.asStateFlow()

    private val _form = MutableStateFlow(TemplateForm())
    val form: StateFlow<TemplateForm> = _form.asStateFlow()

    private val _lines = MutableStateFlow<List<EquipmentLine>>(emptyList())
    val lines: StateFlow<List<EquipmentLine>> = _lines.asStateFlow()

    private val _saving = MutableStateFlow(false)
    val saving: StateFlow<Boolean> = _saving.asStateFlow()

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved.asStateFlow()

    private val _snackbar = MutableStateFlow<String?>(null)
    val snackbar: StateFlow<String?> = _snackbar.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = LoadState.Loading
            val categories = catalogRepository.loadCategories(includeInactive = false)
            val equipment = catalogRepository.loadEquipment(includeInactive = true)
            val categoryList = categories.getOrNull()
            val equipmentList = equipment.getOrNull()
            if (categoryList == null || equipmentList == null) {
                _state.value = LoadState.Error(
                    categories.exceptionOrNull()?.message
                        ?: equipment.exceptionOrNull()?.message
                        ?: "Failed to load catalog",
                )
                return@launch
            }
            val activeEquipment = equipmentList.filter { (it.isActive ?: 1) == 1 }
            _state.value = LoadState.Ready(
                TemplateEditorData(
                    categories = categoryList,
                    activeEquipment = activeEquipment,
                    existingId = templateId,
                ),
            )
            if (templateId != null) {
                loadExisting(templateId, equipmentList.mapNotNull { it.id?.let { id -> id to it } }.toMap())
            }
        }
    }

    private suspend fun loadExisting(id: Int, equipmentById: Map<Int, EquipmentDto>) {
        val result = templateRepository.loadTemplate(id)
        val template = result.getOrNull()
        if (template == null) {
            _snackbar.value = result.exceptionOrNull()?.message ?: "Template not found"
            return
        }
        _form.value = TemplateForm(
            name = template.name,
            description = template.description.orEmpty(),
            laborHoursText = NumberInput.toDisplay(template.laborHours),
            hourlyRateText = NumberInput.toDisplay(template.hourlyRate),
            marginText = NumberInput.toDisplay(template.marginPercentage),
        )
        val linesResult = templateRepository.loadTemplateLines(id)
        val stored = linesResult.getOrNull()
        if (stored == null) {
            _snackbar.value = linesResult.exceptionOrNull()?.message ?: "Failed to load template items"
            return
        }
        _lines.value = stored.map { line ->
            val equipment = equipmentById[line.equipmentId]
            EquipmentLine(
                equipmentId = line.equipmentId,
                name = equipment?.name ?: "Unknown item",
                unitValue = BigDecimal.valueOf(equipment?.value ?: 0.0),
                quantity = line.quantity ?: 1,
            )
        }
    }

    fun onNameChange(value: String) = _form.update { it.copy(name = value, showNameError = false) }
    fun onDescriptionChange(value: String) = _form.update { it.copy(description = value) }
    fun onLaborHoursChange(value: String) = _form.update { it.copy(laborHoursText = value) }
    fun onHourlyRateChange(value: String) = _form.update { it.copy(hourlyRateText = value) }
    fun onMarginChange(value: String) = _form.update { it.copy(marginText = value) }

    fun setQuantity(equipment: EquipmentDto, quantity: Int) {
        val id = equipment.id ?: return
        val current = _lines.value
        if (quantity <= 0) {
            _lines.value = current.filterNot { it.equipmentId == id }
            return
        }
        val existing = current.firstOrNull { it.equipmentId == id }
        _lines.value = if (existing == null) {
            current + EquipmentLine(
                equipmentId = id,
                name = equipment.name,
                unitValue = BigDecimal.valueOf(equipment.value ?: 0.0),
                quantity = quantity,
            )
        } else {
            current.map { if (it.equipmentId == id) it.copy(quantity = quantity) else it }
        }
    }

    fun setLineQuantity(equipmentId: Int, quantity: Int) {
        if (quantity <= 0) {
            _lines.value = _lines.value.filterNot { it.equipmentId == equipmentId }
        } else {
            _lines.value = _lines.value.map {
                if (it.equipmentId == equipmentId) it.copy(quantity = quantity) else it
            }
        }
    }

    fun save() {
        val currentForm = _form.value
        if (currentForm.name.isBlank()) {
            _form.update { it.copy(showNameError = true) }
            _snackbar.value = "Please enter a template name."
            return
        }
        val dto = TemplateDto(
            id = templateId,
            name = currentForm.name.trim(),
            description = currentForm.description.trim().ifBlank { null },
            laborHours = NumberInput.toBigDecimal(currentForm.laborHoursText).toDouble(),
            hourlyRate = NumberInput.toBigDecimal(currentForm.hourlyRateText).toDouble(),
            marginPercentage = NumberInput.toBigDecimal(currentForm.marginText).toDouble(),
            isActive = 1,
        )
        val lineDtos = _lines.value.map { line ->
            TemplateEquipmentDto(
                templateId = templateId ?: 0,
                equipmentId = line.equipmentId,
                quantity = line.quantity,
            )
        }
        _saving.value = true
        viewModelScope.launch {
            templateRepository.saveTemplate(dto, lineDtos)
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
