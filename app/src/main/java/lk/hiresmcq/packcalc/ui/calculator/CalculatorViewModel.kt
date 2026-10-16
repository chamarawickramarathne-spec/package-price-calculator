package lk.hiresmcq.packcalc.ui.calculator

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lk.hiresmcq.packcalc.data.model.EquipmentDto
import lk.hiresmcq.packcalc.data.repo.CatalogRepository
import lk.hiresmcq.packcalc.data.repo.LocalPackageRepository
import lk.hiresmcq.packcalc.data.repo.TemplateRepository
import lk.hiresmcq.packcalc.domain.EquipmentLine
import lk.hiresmcq.packcalc.domain.PackageDraft
import lk.hiresmcq.packcalc.domain.PricingBreakdown
import lk.hiresmcq.packcalc.domain.PricingCalculator
import lk.hiresmcq.packcalc.ui.LoadState
import lk.hiresmcq.packcalc.util.NumberInput
import lk.hiresmcq.packcalc.util.PdfInvoiceGenerator
import java.math.BigDecimal

class CalculatorViewModel(
    private val catalogRepository: CatalogRepository,
    private val templateRepository: TemplateRepository,
    private val packageRepository: LocalPackageRepository,
    private val appContext: Context,
    private val mode: CalculatorMode,
) : ViewModel() {

    private val _state = MutableStateFlow<LoadState<CalculatorData>>(LoadState.Loading)
    val state: StateFlow<LoadState<CalculatorData>> = _state.asStateFlow()

    private val _form = MutableStateFlow(CalculatorForm())
    val form: StateFlow<CalculatorForm> = _form.asStateFlow()

    private val _lines = MutableStateFlow<List<EquipmentLine>>(emptyList())
    val lines: StateFlow<List<EquipmentLine>> = _lines.asStateFlow()

    private val _tab = MutableStateFlow(0)
    val tab: StateFlow<Int> = _tab.asStateFlow()

    private val _saving = MutableStateFlow(false)
    val saving: StateFlow<Boolean> = _saving.asStateFlow()

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved.asStateFlow()

    private val _pdf = MutableStateFlow<PdfResult?>(null)
    val pdf: StateFlow<PdfResult?> = _pdf.asStateFlow()

    private val _snackbar = MutableStateFlow<String?>(null)
    val snackbar: StateFlow<String?> = _snackbar.asStateFlow()

    val breakdown: StateFlow<PricingBreakdown> =
        combine(_form, _lines) { form, lines -> compute(form, lines) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, PricingBreakdown.ZERO)

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

            val equipmentById = equipmentList.mapNotNull { item -> item.id?.let { it to item } }.toMap()
            _state.value = LoadState.Ready(
                CalculatorData(
                    categories = categoryList,
                    activeEquipment = equipmentList.filter { (it.isActive ?: 1) == 1 },
                ),
            )

            when (mode) {
                is CalculatorMode.New -> Unit
                is CalculatorMode.Edit -> loadExistingPackage(mode.packageId)
                is CalculatorMode.FromTemplate -> loadFromTemplate(mode.templateId, equipmentById)
            }
        }
    }

    private suspend fun loadExistingPackage(packageId: Long) {
        val quote = packageRepository.getQuote(packageId)
        if (quote == null) {
            _snackbar.value = "Package not found"
            return
        }
        _form.value = CalculatorForm(
            packageName = quote.packageName,
            clientName = quote.clientName.orEmpty(),
            projectDate = quote.projectDate.orEmpty(),
            laborHoursText = NumberInput.toDisplay(quote.laborHours),
            hourlyRateText = NumberInput.toDisplay(quote.hourlyRate),
            profitText = NumberInput.toDisplay(quote.profitPercentage),
            extraGearText = NumberInput.toDisplay(quote.extraCosts.extraGear),
            transportationText = NumberInput.toDisplay(quote.extraCosts.transportation),
            assistantPayText = NumberInput.toDisplay(quote.extraCosts.assistantPay),
            editingText = NumberInput.toDisplay(quote.extraCosts.editing),
            additionalText = NumberInput.toDisplay(quote.extraCosts.additional),
            notes = quote.notes.orEmpty(),
            sourceTemplateId = quote.sourceTemplateId,
            editingPackageId = quote.id,
        )
        _lines.value = quote.lines
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
            laborHoursText = NumberInput.toDisplay(template.laborHours),
            hourlyRateText = NumberInput.toDisplay(template.hourlyRate),
            profitText = NumberInput.toDisplay(template.marginPercentage),
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

    fun onPackageNameChange(value: String) = _form.update { it.copy(packageName = value, showNameError = false) }
    fun onClientNameChange(value: String) = _form.update { it.copy(clientName = value) }
    fun onProjectDateChange(value: String) = _form.update { it.copy(projectDate = value) }
    fun onLaborHoursChange(value: String) = _form.update { it.copy(laborHoursText = value) }
    fun onHourlyRateChange(value: String) = _form.update { it.copy(hourlyRateText = value) }
    fun onProfitChange(value: String) = _form.update { it.copy(profitText = value) }
    fun onExtraGearChange(value: String) = _form.update { it.copy(extraGearText = value) }
    fun onTransportationChange(value: String) = _form.update { it.copy(transportationText = value) }
    fun onAssistantPayChange(value: String) = _form.update { it.copy(assistantPayText = value) }
    fun onEditingChange(value: String) = _form.update { it.copy(editingText = value) }
    fun onAdditionalChange(value: String) = _form.update { it.copy(additionalText = value) }
    fun onNotesChange(value: String) = _form.update { it.copy(notes = value) }

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
        _lines.value = if (quantity <= 0) {
            _lines.value.filterNot { it.equipmentId == equipmentId }
        } else {
            _lines.value.map { if (it.equipmentId == equipmentId) it.copy(quantity = quantity) else it }
        }
    }

    fun selectTab(index: Int) {
        if (index > 0 && !validateName()) return
        _tab.value = index.coerceIn(0, LAST_TAB)
    }

    fun nextTab() = selectTab(_tab.value + 1)

    fun previousTab() = selectTab(_tab.value - 1)

    fun save() {
        if (!validateName()) return
        _saving.value = true
        viewModelScope.launch {
            persist()
                .onSuccess { id ->
                    _form.update { it.copy(editingPackageId = id) }
                    _saving.value = false
                    _saved.value = true
                }
                .onFailure {
                    _saving.value = false
                    _snackbar.value = it.message ?: "Could not save package"
                }
        }
    }

    fun requestPdf(pdfMode: PdfMode) {
        if (!validateName()) return
        _saving.value = true
        viewModelScope.launch {
            val id = persist().getOrElse {
                _saving.value = false
                _snackbar.value = it.message ?: "Could not save package"
                return@launch
            }
            _form.update { it.copy(editingPackageId = id) }
            val quote = packageRepository.getQuote(id)
            if (quote == null) {
                _saving.value = false
                _snackbar.value = "Could not load package"
                return@launch
            }
            val file = withContext(Dispatchers.IO) {
                runCatching { PdfInvoiceGenerator.generate(appContext, quote) }
            }.getOrElse {
                _saving.value = false
                _snackbar.value = it.message ?: "Could not create the invoice PDF"
                return@launch
            }
            _saving.value = false
            _pdf.value = PdfResult(file, pdfMode)
        }
    }

    fun consumePdf() {
        _pdf.value = null
    }

    fun consumeSnackbar() {
        _snackbar.value = null
    }

    /** Saves the current form locally and returns the package id. */
    private suspend fun persist(): Result<Long> = runCatching {
        val form = _form.value
        val draft = PackageDraft(
            id = form.editingPackageId,
            packageName = form.packageName.trim(),
            clientName = form.clientName.trim().ifBlank { null },
            projectDate = form.projectDate.trim().ifBlank { null },
            lines = _lines.value,
            laborHours = NumberInput.toBigDecimal(form.laborHoursText),
            hourlyRate = NumberInput.toBigDecimal(form.hourlyRateText),
            profitPercentage = NumberInput.toBigDecimal(form.profitText),
            extraCosts = form.extraCosts(),
            notes = form.notes.trim().ifBlank { null },
            sourceTemplateId = form.sourceTemplateId,
        )
        packageRepository.save(draft)
    }

    private fun validateName(): Boolean {
        if (_form.value.packageName.isBlank()) {
            _form.update { it.copy(showNameError = true) }
            _tab.value = 0
            _snackbar.value = "Please enter a package name."
            return false
        }
        return true
    }

    private fun compute(form: CalculatorForm, lines: List<EquipmentLine>): PricingBreakdown =
        PricingCalculator.calculate(
            lines = lines,
            laborHours = NumberInput.toBigDecimal(form.laborHoursText),
            hourlyRate = NumberInput.toBigDecimal(form.hourlyRateText),
            profitPercentage = NumberInput.toBigDecimal(form.profitText),
            extraCosts = form.extraCosts(),
        )
}
