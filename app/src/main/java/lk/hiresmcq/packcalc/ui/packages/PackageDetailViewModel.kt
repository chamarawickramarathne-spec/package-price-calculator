package lk.hiresmcq.packcalc.ui.packages

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lk.hiresmcq.packcalc.data.repo.LocalPackageRepository
import lk.hiresmcq.packcalc.data.repo.UsageLogRepository
import lk.hiresmcq.packcalc.domain.PackageQuote
import lk.hiresmcq.packcalc.ui.LoadState
import lk.hiresmcq.packcalc.ui.calculator.PdfMode
import lk.hiresmcq.packcalc.ui.calculator.PdfResult
import lk.hiresmcq.packcalc.ui.calculator.toUsageEvent
import lk.hiresmcq.packcalc.util.PdfInvoiceGenerator

class PackageDetailViewModel(
    private val packageRepository: LocalPackageRepository,
    private val usageLogRepository: UsageLogRepository,
    private val appContext: Context,
    private val packageId: Long,
) : ViewModel() {

    private val _state = MutableStateFlow<LoadState<PackageQuote>>(LoadState.Loading)
    val state: StateFlow<LoadState<PackageQuote>> = _state.asStateFlow()

    private val _pdf = MutableStateFlow<PdfResult?>(null)
    val pdf: StateFlow<PdfResult?> = _pdf.asStateFlow()

    private val _snackbar = MutableStateFlow<String?>(null)
    val snackbar: StateFlow<String?> = _snackbar.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = LoadState.Loading
            val quote = runCatching { packageRepository.getQuote(packageId) }
            val value = quote.getOrNull()
            _state.value = if (value == null) {
                LoadState.Error(quote.exceptionOrNull()?.message ?: "Package not found")
            } else {
                LoadState.Ready(value)
            }
        }
    }

    fun requestPdf(mode: PdfMode) {
        val quote = (_state.value as? LoadState.Ready)?.data ?: return
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) { PdfInvoiceGenerator.generate(appContext, quote) }
            }
                .onSuccess {
                    _pdf.value = PdfResult(it, mode)
                    usageLogRepository.log(mode.toUsageEvent())
                }
                .onFailure { _snackbar.value = it.message ?: "Could not create the invoice PDF" }
        }
    }

    fun consumePdf() {
        _pdf.value = null
    }

    fun consumeSnackbar() {
        _snackbar.value = null
    }
}
