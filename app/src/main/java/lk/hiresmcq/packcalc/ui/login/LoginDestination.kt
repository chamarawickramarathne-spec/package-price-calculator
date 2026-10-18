package lk.hiresmcq.packcalc.ui.login

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import lk.hiresmcq.packcalc.ui.SimpleVmFactory
import lk.hiresmcq.packcalc.ui.appContainer

/**
 * NavHost wiring for the email gate / change-email routes: builds the
 * [LoginViewModel] from the app container and lets the caller decide what
 * "done" means (enter the app after the gate, pop back after a change).
 */
@Composable
fun LoginDestination(
    mode: LoginMode,
    onDone: () -> Unit,
    onBack: (() -> Unit)? = null,
) {
    val container = appContainer()
    val vm: LoginViewModel = viewModel(
        factory = SimpleVmFactory(container) {
            LoginViewModel(it.sessionStore, it.userEmailRepository, mode)
        },
    )
    LoginScreen(
        viewModel = vm,
        mode = mode,
        onDone = onDone,
        onBack = onBack,
    )
}
