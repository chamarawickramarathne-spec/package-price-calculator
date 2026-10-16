package lk.hiresmcq.packcalc.ui.templates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lk.hiresmcq.packcalc.data.model.EquipmentDto
import lk.hiresmcq.packcalc.data.model.TemplateDto
import lk.hiresmcq.packcalc.data.model.TemplateEquipmentDto
import lk.hiresmcq.packcalc.data.repo.CatalogRepository
import lk.hiresmcq.packcalc.data.repo.TemplateRepository
import lk.hiresmcq.packcalc.domain.EquipmentLine
import lk.hiresmcq.packcalc.domain.PricingCalculator
import lk.hiresmcq.packcalc.ui.LoadState
import java.math.BigDecimal

data class TemplateDisplay(
    val template: TemplateDto,
    val itemCount: Int,
    val startingPrice: BigDecimal,
)

class TemplateListViewModel(
    private val templateRepository: TemplateRepository,
    private val catalogRepository: CatalogRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<LoadState<List<TemplateDisplay>>>(LoadState.Loading)
    val state: StateFlow<LoadState<List<TemplateDisplay>>> = _state.asStateFlow()

    private val _snackbar = MutableStateFlow<String?>(null)
    val snackbar: StateFlow<String?> = _snackbar.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = LoadState.Loading
            val templates = templateRepository.loadTemplates(includeInactive = false)
            val linesResult = templateRepository.loadTemplateEquipment()
            val equipmentResult = catalogRepository.loadEquipment(includeInactive = true)

            val templateList = templates.getOrNull()
            val lineList = linesResult.getOrNull()
            val equipmentById = equipmentResult.getOrNull()
                ?.mapNotNull { item -> item.id?.let { it to item } }
                ?.toMap()

            when {
                templateList == null -> _state.value =
                    LoadState.Error(templates.exceptionOrNull()?.message ?: "Failed to load templates")
                lineList == null -> _state.value =
                    LoadState.Error(linesResult.exceptionOrNull()?.message ?: "Failed to load template items")
                equipmentById == null -> _state.value =
                    LoadState.Error(equipmentResult.exceptionOrNull()?.message ?: "Failed to load equipment")
                else -> {
                    val displays = templateList.map { template ->
                        buildDisplay(template, lineList, equipmentById)
                    }
                    _state.value = LoadState.Ready(displays)
                }
            }
        }
    }

    private fun buildDisplay(
        template: TemplateDto,
        lines: List<TemplateEquipmentDto>,
        equipmentById: Map<Int, EquipmentDto>,
    ): TemplateDisplay {
        val templateLines = lines.filter { it.templateId == template.id }
        val equipmentLines = templateLines.mapNotNull { line ->
            val equipment = equipmentById[line.equipmentId] ?: return@mapNotNull null
            EquipmentLine(
                equipmentId = line.equipmentId,
                name = equipment.name,
                unitValue = BigDecimal.valueOf(equipment.value ?: 0.0),
                quantity = line.quantity ?: 1,
            )
        }
        val breakdown = PricingCalculator.calculate(
            lines = equipmentLines,
            laborHours = BigDecimal.valueOf(template.laborHours ?: 0.0),
            hourlyRate = BigDecimal.valueOf(template.hourlyRate ?: 0.0),
            marginPercentage = BigDecimal.valueOf(template.marginPercentage ?: 0.0),
        )
        return TemplateDisplay(
            template = template,
            itemCount = templateLines.sumOf { it.quantity ?: 1 },
            startingPrice = breakdown.finalTotal,
        )
    }

    fun delete(template: TemplateDto) {
        val id = template.id ?: return
        viewModelScope.launch {
            templateRepository.deleteTemplate(id)
                .onSuccess {
                    _snackbar.value = "Template deleted"
                    load()
                }
                .onFailure { _snackbar.value = it.message }
        }
    }

    fun consumeSnackbar() {
        _snackbar.value = null
    }
}
