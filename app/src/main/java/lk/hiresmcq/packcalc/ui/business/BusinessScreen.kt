package lk.hiresmcq.packcalc.ui.business

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import lk.hiresmcq.packcalc.ui.components.LabeledTextField
import lk.hiresmcq.packcalc.util.CompanyInfo

/**
 * Business Details screen (v1.5.0): the studio's name, tagline and contact
 * details, printed on every PDF quote in place of the built-in branding.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessScreen(
    viewModel: BusinessViewModel,
    onBack: () -> Unit,
    onDone: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.saved) {
        if (state.saved) onDone()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Business Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "These details appear on every PDF quote in place of " +
                    "\"${CompanyInfo.NAME}\" - the studio name on the quote, " +
                    "the sub-title under the package, and the contact line in the footer.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            Field(
                state = state,
                field = BusinessField.NAME,
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = "Business name",
                placeholder = CompanyInfo.NAME,
                supportingIfBlank = "Leave blank to keep \"${CompanyInfo.NAME}\".",
            )
            Field(
                state = state,
                field = BusinessField.TAGLINE,
                value = state.tagline,
                onValueChange = viewModel::onTaglineChange,
                label = "Tagline",
                placeholder = CompanyInfo.TAGLINE,
                supportingIfBlank = "Leave blank to keep the default tagline.",
            )
            Field(
                state = state,
                field = BusinessField.PHONE,
                value = state.phone,
                onValueChange = viewModel::onPhoneChange,
                label = "Phone",
                placeholder = "+94 77 123 4567",
            )
            Field(
                state = state,
                field = BusinessField.EMAIL,
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                label = "Email",
                placeholder = "studio@example.com",
            )
            Field(
                state = state,
                field = BusinessField.ADDRESS,
                value = state.address,
                onValueChange = viewModel::onAddressChange,
                label = "Address",
                placeholder = "Colombo, Sri Lanka",
            )
            Button(
                onClick = viewModel::save,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            ) {
                Icon(Icons.Filled.Save, contentDescription = null)
                Text("Save details", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
private fun Field(
    state: BusinessUiState,
    field: BusinessField,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    supportingIfBlank: String? = null,
) {
    val error = state.errors[field]
    LabeledTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = placeholder,
        isError = error != null,
        supportingText = error ?: supportingIfBlank.takeIf { value.isBlank() },
        modifier = Modifier.padding(bottom = 12.dp),
    )
}
