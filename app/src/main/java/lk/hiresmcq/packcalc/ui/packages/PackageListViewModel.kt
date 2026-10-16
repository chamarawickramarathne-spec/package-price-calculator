package lk.hiresmcq.packcalc.ui.packages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lk.hiresmcq.packcalc.data.model.PackageDto
import lk.hiresmcq.packcalc.data.repo.PackageRepository
import lk.hiresmcq.packcalc.ui.LoadState
import java.math.BigDecimal

data class PackageDisplay(
    val pkg: PackageDto,
    val finalTotal: BigDecimal,
)

class PackageListViewModel(
    private val packageRepository: PackageRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<LoadState<List<PackageDisplay>>>(LoadState.Loading)
    val state: StateFlow<LoadState<List<PackageDisplay>>> = _state.asStateFlow()

    private val _snackbar = MutableStateFlow<String?>(null)
    val snackbar: StateFlow<String?> = _snackbar.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = LoadState.Loading
            packageRepository.loadPackages()
                .onSuccess { list ->
                    _state.value = LoadState.Ready(list.map(::buildDisplay))
                }
                .onFailure {
                    _state.value = LoadState.Error(it.message ?: "Failed to load packages")
                }
        }
    }

    private fun buildDisplay(pkg: PackageDto): PackageDisplay {
        val total = pkg.finalTotal?.let { BigDecimal.valueOf(it) } ?: BigDecimal.ZERO
        return PackageDisplay(pkg = pkg, finalTotal = total)
    }

    fun delete(pkg: PackageDto) {
        val id = pkg.id ?: return
        viewModelScope.launch {
            packageRepository.deletePackage(id)
                .onSuccess {
                    _snackbar.value = "Package deleted"
                    load()
                }
                .onFailure { _snackbar.value = it.message }
        }
    }

    fun consumeSnackbar() {
        _snackbar.value = null
    }
}
