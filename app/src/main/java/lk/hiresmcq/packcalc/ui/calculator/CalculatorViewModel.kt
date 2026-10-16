package lk.hiresmcq.packcalc.ui.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lk.hiresmcq.packcalc.data.model.CategoryDto
import lk.hiresmcq.packcalc.data.model.EquipmentDto
import lk.hiresmcq.packcalc.data.model.PackageDto
import lk.hiresmcq.packcalc.data.model.PackageEquipmentDto
import lk.hiresmcq.packcalc.data.repo.CatalogRepository
import lk.hiresmcq.packcalc.data.repo.PackageRepository
import lk.hiresmcq.packcalc.data.repo.TemplateRepository
import lk.hiresmcq.packcalc.domain.EquipmentLine
import lk.hiresmcq.packcalc.domain.PricingCalculator
import lk.hiresmcq.packcalc.ui.LoadState
import lk.hiresmcq.packcalc.util.NumberInput
import java.math.BigDecimal

sealed interface CalculatorMode {
    data object New : CalculatorMode
    data class Edit(val packageId: Int) : CalculatorMode
    data class FromTemplate(val templateId: Int) : CalculatorMode
}

data class CalculatorData(
    val categories: List<CategoryDto>,
    val activeEquipment: List<EquipmentDto>,
    val equipmentById: Map<Int, EquipmentDto>,
)

data class CalculatorForm(
    val packageName: String = "",
    val clientName: String = "",
    val laborHoursText: String = "",
    val hourlyRateText: String = "",
    val marginText: String = "",
    val showNameError: Boolean = false,
    val sourceTemplateId: Int? = null,
    val editingPackageId: Int? = null,
)

class CalculatorViewModel(
    private val catalogRepository: CatalogRepository,
    private val templateRepository: TemplateRepository,
    private val packageRepository: PackageRepository,
    private val mode: CalculatorMode,
) : ViewModel() {

    private val _state = MutableStateFlow<LoadState<CalculatorData>>(LoadState.Loading)
    val state: StateFlow<LoadState<CalculatorData>> = _state.asStateFlow()

    private val _form = MutableStateFlow(CalculatorForm())
    val form: StateFlow<CalculatorForm> = _form.asStateFlow()

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

            val equipmentById = equipmentList.mapNotNull { item ->
                item.id?.let { it to item }
            }.toMap()

            _state.value = LoadState.Ready(
                CalculatorData(
                    categories = categoryList,
                    activeEquipment = equipmentList.filter { (it.isActive ?: 1) == 1 },
                    equipmentById = equipmentById,
                ),
            )

            when (mode) {
                is CalculatorMode.New -> Unit
                is CalculatorMode.Edit -> loadExistingPackage(mode.packageId, equipmentById)
                is CalculatorMode.FromTemplate -> loadFromTemplate(mode.templateId, equipmentById)
            }
        }
    }

    private suspend fun loadExistingPackage(packageId: Int, equipmentById: Map<Int, EquipmentDto>) {
        val result = packageRepository.loadPackage(packageId)
        val pkg = result.getOrNull()
        if (pkg == null) {
            _snackbar.value = result.exceptionOrNull()?.message ?: "Package not found"
            return
        }
        _form.value = CalculatorForm(
            packageName = pkg.packageName,
            clientName = pkg.clientName.orEmpty(),
            laborHoursText = NumberInput.toDisplay(pkg.laborHours),
            hourlyRateText = NumberInput.toDisplay(pkg.hourlyRate),
            marginText = NumberInput.toDisplay(pkg.marginPercentage),
            editingPackageId = pkg.id,
        )
        val linesResult = packageRepository.loadPackageEquipment(packageId)
        val storedLines = linesResult.getOrNull()
        if (storedLines == null) {
            _snackbar.value = linesResult.exceptionOrNull()?.message ?: "Failed to load package items"
            return
        }
        _lines.value = storedLines.map { line ->
            val equipment = equipmentById[line.equipmentId]
            EquipmentLine(
                equipmentId = line.equipmentId,
                name = equipment?.name ?: "Unknown item",
                unitValue = BigDecimal.valueOf(line.unitValue),
                quantity = line.quantity ?: 1,
            )
        }
    }

    private suspend fun loadFromTemplate(templateId: Int, equipmentById: Map<Int, EquipmentDto>) {
        val templateResult = templateRepository.loadTemplate(templateId)
        val template = templateResult.getOrNull()
        if (template == null) {
            _snackbar.value = templateResult.exceptionOrNull()?.message ?: "Template not found"
            return
        }
        _form.value = CalculatorForm(
            packageName = template.name,
            clientName = "",
            laborHoursText = NumberInput.toDisplay(template.laborHours),
            hourlyRateText = NumberInput.toDisplay(template.hourlyRate),
            marginText = NumberInput.toDisplay(template.marginPercentage),
            sourceTemplateId = template.id,
        )
        val linesResult = templateRepository.loadTemplateLines(templateId)
        val templateLines = linesResult.getOrNull()
        if (templateLines == null) {
            _snackbar.value = linesResult.exceptionOrNull()?.message ?: "Failed to load template items"
            return
        }
        _lines.value = templateLines.mapNotNull { line ->
            val equipment = equipmentById[line.equipmentId] ?: return@mapNotNull null
            EquipmentLine(
                equipmentId = line.equipmentId,
                name = equipment.name,
                unitValue = BigDecimal.valueOf(equipment.value ?: 0.0),
                quantity = line.quantity ?: 1,
            )
        }
    }

    fun onPackageNameChange(value: String) {
        _form.update { it.copy(packageName = value, showNameError = false) }
    }

    fun onClientNameChange(value: String) = _form.update { it.copy(clientName = value) }
    fun onLaborHoursChange(value: String) = _form.update { it.copy(laborHoursText = value) }
    fun onHourlyRateChange(value: String) = _form.update { it.copy(hourlyRateText = value) }
    fun onMarginChange(value: String) = _form.update { it.copy(marginText = value) }

    val selectedQuantities: Map<Int, Int>
        get() = _lines.value.associate { it.equipmentId to it.quantity }

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
        if (currentForm.packageName.isBlank()) {
            _form.update { it.copy(showNameError = true) }
            _snackbar.value = "Please enter a package name."
            return
        }
        val breakdown = PricingCalculator.calculate(
            lines = _lines.value,
            laborHours = NumberInput.toBigDecimal(currentForm.laborHoursText),
            hourlyRate = NumberInput.toBigDecimal(currentForm.hourlyRateText),
            marginPercentage = NumberInput.toBigDecimal(currentForm.marginText),
        )
        val dto = PackageDto(
            id = currentForm.editingPackageId,
            packageName = currentForm.packageName.trim(),
            clientName = currentForm.clientName.trim().ifBlank { null },
            templateId = currentForm.sourceTemplateId,
            laborHours = NumberInput.toBigDecimal(currentForm.laborHoursText).toDouble(),
            hourlyRate = NumberInput.toBigDecimal(currentForm.hourlyRateText).toDouble(),
            marginPercentage = NumberInput.toBigDecimal(currentForm.marginText).toDouble(),
            equipmentTotal = breakdown.equipmentTotal.toDouble(),
            laborTotal = breakdown.laborTotal.toDouble(),
            subtotal = breakdown.subtotal.toDouble(),
            marginAmount = breakdown.marginAmount.toDouble(),
            finalTotal = breakdown.finalTotal.toDouble(),
            notes = null,
        )
        val lineDtos = _lines.value.map { line ->
            PackageEquipmentDto(
                packageId = currentForm.editingPackageId ?: 0,
                equipmentId = line.equipmentId,
                quantity = line.quantity,
                unitValue = line.unitValue.toDouble(),
                totalValue = line.lineTotal.toDouble(),
            )
        }
        _saving.value = true
        viewModelScope.launch {
            packageRepository.savePackage(dto, lineDtos)
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
