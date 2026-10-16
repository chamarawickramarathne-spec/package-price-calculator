package lk.hiresmcq.packcalc.ui.packages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import lk.hiresmcq.packcalc.data.repo.LocalPackageRepository
import lk.hiresmcq.packcalc.domain.SavedPackageSummary
import lk.hiresmcq.packcalc.ui.LoadState

class PackageListViewModel(
    private val packageRepository: LocalPackageRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<LoadState<List<SavedPackageSummary>>>(LoadState.Loading)
    val state: StateFlow<LoadState<List<SavedPackageSummary>>> = _state.asStateFlow()

    private val _snackbar = MutableStateFlow<String?>(null)
    val snackbar: StateFlow<String?> = _snackbar.asStateFlow()

    init {
        observe()
    }

    fun load() = observe()

    private fun observe() {
        viewModelScope.launch {
            _state.value = LoadState.Loading
            packageRepository.observeSummaries()
                .catch { error ->
                    _state.value = LoadState.Error(error.message ?: "Failed to load packages")
                }
                .collect { list -> _state.value = LoadState.Ready(list) }
        }
    }

    fun delete(summary: SavedPackageSummary) {
        viewModelScope.launch {
            runCatching { packageRepository.delete(summary.id) }
                .onSuccess { _snackbar.value = "Package deleted" }
                .onFailure { _snackbar.value = it.message ?: "Could not delete package" }
        }
    }

    fun consumeSnackbar() {
        _snackbar.value = null
    }
}
