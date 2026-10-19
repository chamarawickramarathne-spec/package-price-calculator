package lk.hiresmcq.packcalc.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lk.hiresmcq.packcalc.data.repo.UserEmailRepository
import lk.hiresmcq.packcalc.domain.BusinessProfile
import lk.hiresmcq.packcalc.domain.EmailValidation
import lk.hiresmcq.packcalc.util.BusinessStore
import lk.hiresmcq.packcalc.util.SessionStore

/** Gate = first launch, blocks the app until submitted. Change = drawer edit. */
enum class LoginMode { Gate, Change }

data class LoginUiState(
    val email: String = "",
    val emailError: String? = null,
    /** Gate only: the business details printed on every PDF quote. */
    val businessName: String = "",
    val businessNameError: String? = null,
    val phone: String = "",
    val phoneError: String? = null,
    val saving: Boolean = false,
    /** Offline notice; shown as a snackbar, then the screen finishes. */
    val notice: String? = null,
    val done: Boolean = false,
)

/**
 * Drives the sign-in / details screen: the first-launch gate and the drawer's
 * edit-details route.
 *
 * Both modes collect the business name and phone (saved to [BusinessStore])
 * together with the sign-in email, which is also the contact email printed on
 * PDFs. Submitting always saves on the device first; when the server copy of a
 * new email could not be written (offline) the UI shows [notice] and still lets
 * the user in - the copy is retried at the next app open.
 */
class LoginViewModel(
    private val store: SessionStore,
    private val repository: UserEmailRepository,
    private val businessStore: BusinessStore,
    private val mode: LoginMode,
) : ViewModel() {

    private val initial = if (mode == LoginMode.Change) {
        val profile = businessStore.load()
        LoginUiState(
            email = store.email.orEmpty(),
            businessName = profile.name,
            phone = profile.phone,
        )
    } else {
        LoginUiState()
    }

    private val _state = MutableStateFlow(initial)
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onEmailChange(value: String) {
        _state.update { it.copy(email = value, emailError = null) }
    }

    fun onBusinessNameChange(value: String) {
        _state.update { it.copy(businessName = value, businessNameError = null) }
    }

    fun onPhoneChange(value: String) {
        _state.update { it.copy(phone = value, phoneError = null) }
    }

    fun submit() {
        val current = _state.value
        val errors = validate(current)
        if (!errors.isValid) {
            _state.update {
                it.copy(
                    emailError = errors.email,
                    businessNameError = errors.businessName,
                    phoneError = errors.phone,
                )
            }
            return
        }
        val normalized = EmailValidation.normalize(current.email)
        // Local save always wins - the profile is written before any network
        // call, so details are never lost, even offline.
        businessStore.save(
            BusinessProfile(
                name = current.businessName.trim(),
                phone = current.phone.trim(),
                email = normalized,
            ),
        )
        // The email is unchanged: nothing to push, the details are already saved.
        if (mode == LoginMode.Change && normalized == store.email) {
            _state.update { it.copy(done = true) }
            return
        }
        _state.update { it.copy(saving = true) }
        viewModelScope.launch { register(normalized) }
    }

    private suspend fun register(normalized: String) {
        repository.register(normalized)
            .onSuccess { synced ->
                if (synced) {
                    _state.update { it.copy(saving = false, done = true) }
                } else {
                    _state.update { it.copy(saving = false, notice = OFFLINE_NOTICE) }
                }
            }
            .onFailure { error ->
                _state.update {
                    it.copy(saving = false, emailError = error.message ?: GENERIC_ERROR)
                }
            }
    }

    /** Called once the offline notice has been displayed; finishes the screen. */
    fun consumeNotice() {
        _state.update { it.copy(notice = null, done = true) }
    }

    private data class Errors(
        val email: String? = null,
        val businessName: String? = null,
        val phone: String? = null,
    ) {
        val isValid: Boolean get() = email == null && businessName == null && phone == null
    }

    private fun validate(state: LoginUiState): Errors {
        var errors = Errors()
        if (!EmailValidation.isValid(state.email)) errors = errors.copy(email = INVALID_MESSAGE)
        if (state.businessName.isBlank()) {
            errors = errors.copy(businessName = REQUIRED_MESSAGE)
        } else if (state.businessName.trim().length > BusinessProfile.MAX_NAME) {
            errors = errors.copy(businessName = "Maximum ${BusinessProfile.MAX_NAME} characters.")
        }
        if (state.phone.trim().length > BusinessProfile.MAX_PHONE) {
            errors = errors.copy(phone = "Maximum ${BusinessProfile.MAX_PHONE} characters.")
        }
        return errors
    }

    private companion object {
        const val INVALID_MESSAGE = "Enter a valid email address."
        const val REQUIRED_MESSAGE = "Enter your business name."
        const val GENERIC_ERROR = "Could not save your email."
        const val OFFLINE_NOTICE =
            "Saved on this device. We'll sync your email when you're back online."
    }
}
