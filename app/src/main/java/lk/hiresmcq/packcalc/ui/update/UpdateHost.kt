package lk.hiresmcq.packcalc.ui.update

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import lk.hiresmcq.packcalc.BuildConfig
import lk.hiresmcq.packcalc.di.AppContainer
import lk.hiresmcq.packcalc.ui.SimpleVmFactory

/**
 * Hosts the in-app updater.
 *
 * The updater only exists in the github distribution channel. The play flavor
 * wires no updater (see SelfUpdateWiring), so this renders nothing there and
 * the app makes no GitHub API calls.
 */
@Composable
fun UpdateHost(container: AppContainer, appContext: Context) {
    if (!BuildConfig.SELF_UPDATE) return
    val repository = container.updateRepository ?: return
    val downloader = container.apkDownloader ?: return
    val viewModel: UpdateViewModel = viewModel(
        factory = SimpleVmFactory(container) {
            UpdateViewModel(repository, downloader, appContext)
        },
    )
    UpdateDialog(viewModel)
}
