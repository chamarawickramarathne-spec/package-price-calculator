package lk.hiresmcq.packcalc.ui.business

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import lk.hiresmcq.packcalc.domain.BusinessProfile
import lk.hiresmcq.packcalc.domain.EmailValidation
import lk.hiresmcq.packcalc.util.BusinessStore

enum class BusinessField { NAME, TAGLINE, PHONE, EMAIL, ADDRESS }

data class BusinessUiState(
    val name: String = "",
    val tagline: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val errors: Map<BusinessField, String> = emptyMap(),
    val saved: Boolean = false,
)

/**
 * Backs the Business Details screen (v1.5.0). Loads the saved profile once,
 * validates on Save and writes it back to [BusinessStore] - the PDF generator
 * and drawer read the same store, so the new details apply immediately.
 */
class BusinessViewModel(private val store: BusinessStore) : ViewModel() {

    private val _state = MutableStateFlow(store.load().toUi())
    val state: StateFlow<BusinessUiState> = _state.asStateFlow()

    fun onNameChange(value: String) = change(BusinessField.NAME) { copy(name = value) }

    fun onTaglineChange(value: String) = change(BusinessField.TAGLINE) { copy(tagline = value) }

    fun onPhoneChange(value: String) = change(BusinessField.PHONE) { copy(phone = value) }

    fun onEmailChange(value: String) = change(BusinessField.EMAIL) { copy(email = value) }

    fun onAddressChange(value: String) = change(BusinessField.ADDRESS) { copy(address = value) }

    fun save() {
        val current = _state.value
        val errors = validate(current)
        if (errors.isNotEmpty()) {
            _state.update { it.copy(errors = errors, saved = false) }
            return
        }
        store.save(
            BusinessProfile(
                name = current.name.trim(),
                tagline = current.tagline.trim(),
                phone = current.phone.trim(),
                email = EmailValidation.normalize(current.email),
                address = current.address.trim(),
            ),
        )
        _state.update { it.copy(errors = emptyMap(), saved = true) }
    }

    private fun change(field: BusinessField, block: BusinessUiState.() -> BusinessUiState) {
        _state.update { current ->
            current.copy(errors = current.errors - field, saved = false).block()
        }
    }

    private fun validate(state: BusinessUiState): Map<BusinessField, String> {
        val errors = mutableMapOf<BusinessField, String>()
        if (state.name.length > BusinessProfile.MAX_NAME) {
            errors[BusinessField.NAME] = "Maximum ${BusinessProfile.MAX_NAME} characters."
        }
        if (state.tagline.length > BusinessProfile.MAX_TAGLINE) {
            errors[BusinessField.TAGLINE] = "Maximum ${BusinessProfile.MAX_TAGLINE} characters."
        }
        if (state.phone.length > BusinessProfile.MAX_PHONE) {
            errors[BusinessField.PHONE] = "Maximum ${BusinessProfile.MAX_PHONE} characters."
        }
        if (state.email.isNotBlank() && !EmailValidation.isValid(state.email)) {
            errors[BusinessField.EMAIL] = "Enter a valid email address, or leave it blank."
        }
        if (state.address.length > BusinessProfile.MAX_ADDRESS) {
            errors[BusinessField.ADDRESS] = "Maximum ${BusinessProfile.MAX_ADDRESS} characters."
        }
        return errors
    }
}

private fun BusinessProfile.toUi() = BusinessUiState(
    name = name,
    tagline = tagline,
    phone = phone,
    email = email,
    address = address,
)
