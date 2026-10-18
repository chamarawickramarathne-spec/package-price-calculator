package lk.hiresmcq.packcalc.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lk.hiresmcq.packcalc.data.repo.UserEmailRepository
import lk.hiresmcq.packcalc.domain.EmailValidation
import lk.hiresmcq.packcalc.util.SessionStore

/** Gate = first launch, blocks the app until submitted. Change = drawer edit. */
enum class LoginMode { Gate, Change }

data class LoginUiState(
    val email: String = "",
    val emailError: String? = null,
    val saving: Boolean = false,
    /** Offline notice; shown as a snackbar, then the screen finishes. */
    val notice: String? = null,
    val done: Boolean = false,
)

/**
 * Drives the email gate (and the drawer's change-email route).
 *
 * Submitting always saves on the device first ([UserEmailRepository]); when
 * the server copy could not be written (offline) the UI shows [notice] and
 * still lets the user in - the copy is retried at the next app open.
 */
class LoginViewModel(
    private val store: SessionStore,
    private val repository: UserEmailRepository,
    private val mode: LoginMode,
) : ViewModel() {

    private val _state = MutableStateFlow(
        LoginUiState(email = if (mode == LoginMode.Change) store.email.orEmpty() else ""),
    )
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onEmailChange(value: String) {
        _state.update { it.copy(email = value, emailError = null) }
    }

    fun submit() {
        val current = _state.value
        if (!EmailValidation.isValid(current.email)) {
            _state.update { it.copy(emailError = INVALID_MESSAGE) }
            return
        }
        val normalized = EmailValidation.normalize(current.email)
        if (mode == LoginMode.Change && normalized == store.email) {
            _state.update { it.copy(done = true) }
            return
        }
        _state.update { it.copy(saving = true, emailError = null) }
        viewModelScope.launch {
            repository.register(normalized)
                .onSuccess { synced ->
                    if (synced) {
                        _state.update { it.copy(saving = false, done = true) }
                    } else {
                        _state.update {
                            it.copy(saving = false, notice = OFFLINE_NOTICE)
                        }
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(saving = false, emailError = error.message ?: GENERIC_ERROR)
                    }
                }
        }
    }

    /** Called once the offline notice has been displayed; finishes the screen. */
    fun consumeNotice() {
        _state.update { it.copy(notice = null, done = true) }
    }

    private companion object {
        const val INVALID_MESSAGE = "Enter a valid email address."
        const val GENERIC_ERROR = "Could not save your email."
        const val OFFLINE_NOTICE =
            "Saved on this device. We'll sync your email when you're back online."
    }
}
