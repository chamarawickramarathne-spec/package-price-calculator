package lk.hiresmcq.packcalc.ui.update

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lk.hiresmcq.packcalc.BuildConfig
import lk.hiresmcq.packcalc.data.remote.ApkDownloader
import lk.hiresmcq.packcalc.data.repo.UpdateInfo
import lk.hiresmcq.packcalc.data.repo.UpdateRepository
import lk.hiresmcq.packcalc.domain.Version
import lk.hiresmcq.packcalc.util.ApkInstaller
import java.io.File

sealed interface UpdateUiState {
    data object Idle : UpdateUiState
    data object Checking : UpdateUiState
    data object UpToDate : UpdateUiState
    data object CheckFailed : UpdateUiState
    data class Available(val info: UpdateInfo) : UpdateUiState
    data class Downloading(val info: UpdateInfo, val bytesRead: Long, val total: Long) : UpdateUiState
    data class ReadyToInstall(val info: UpdateInfo, val apk: File) : UpdateUiState
    data class Failed(val message: String) : UpdateUiState
}

class UpdateViewModel(
    private val repository: UpdateRepository,
    private val downloader: ApkDownloader,
    private val appContext: Context,
) : ViewModel() {

    private val _state = MutableStateFlow<UpdateUiState>(UpdateUiState.Idle)
    val state: StateFlow<UpdateUiState> = _state.asStateFlow()

    init {
        checkForUpdate()
    }

    fun checkForUpdate() {
        val current = _state.value
        if (current is UpdateUiState.Checking || current is UpdateUiState.Downloading) return

        _state.value = UpdateUiState.Checking
        viewModelScope.launch {
            val installed = Version.parse(BuildConfig.VERSION_NAME)
            if (installed == null) {
                _state.value = UpdateUiState.CheckFailed
                return@launch
            }
            _state.value = repository.latestUpdate(installed).fold(
                onSuccess = { info ->
                    if (info == null) UpdateUiState.UpToDate else UpdateUiState.Available(info)
                },
                onFailure = { UpdateUiState.CheckFailed },
            )
        }
    }

    fun download(info: UpdateInfo) {
        _state.value = UpdateUiState.Downloading(info, 0L, info.sizeBytes)
        viewModelScope.launch {
            try {
                val dest = File(appContext.cacheDir, "updates/${UpdateRepository.APK_ASSET_NAME}")
                dest.parentFile?.mkdirs()
                if (dest.exists()) dest.delete()

                downloader.download(info.downloadUrl, dest) { read, total ->
                    _state.value = UpdateUiState.Downloading(
                        info = info,
                        bytesRead = read,
                        total = if (total > 0) total else info.sizeBytes,
                    )
                }
                _state.value = UpdateUiState.ReadyToInstall(info, dest)
            } catch (e: Exception) {
                _state.value = UpdateUiState.Failed(e.message ?: "The update could not be downloaded.")
            }
        }
    }

    fun install() {
        val ready = _state.value as? UpdateUiState.ReadyToInstall ?: return
        if (ApkInstaller.canInstall(appContext)) {
            ApkInstaller.install(appContext, ready.apk)
        } else {
            ApkInstaller.openInstallPermissionSettings(appContext)
        }
    }

    fun dismiss() {
        if (_state.value is UpdateUiState.Downloading) return
        _state.value = UpdateUiState.Idle
    }
}
