package lk.hiresmcq.packcalc.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import lk.hiresmcq.packcalc.PackCalcApp
import lk.hiresmcq.packcalc.di.AppContainer

@Composable
fun appContainer(): AppContainer =
    (LocalContext.current.applicationContext as PackCalcApp).container

class SimpleVmFactory(
    private val container: AppContainer,
    private val creator: (AppContainer) -> ViewModel,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = creator(container) as T
}
