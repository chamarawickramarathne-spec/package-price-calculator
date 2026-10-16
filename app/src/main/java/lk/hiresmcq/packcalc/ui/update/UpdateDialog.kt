package lk.hiresmcq.packcalc.ui.update

import android.text.format.Formatter
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import lk.hiresmcq.packcalc.BuildConfig

@Composable
fun UpdateDialog(viewModel: UpdateViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    when (val current = state) {
        is UpdateUiState.Available -> AlertDialog(
            onDismissRequest = { viewModel.dismiss() },
            title = { Text("Update available") },
            text = {
                Text(
                    "Version ${current.info.version} is available (you have " +
                        "${BuildConfig.VERSION_NAME}).",
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.download(current.info) }) { Text("Update") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismiss() }) { Text("Later") }
            },
        )

        is UpdateUiState.Downloading -> AlertDialog(
            onDismissRequest = { },
            title = { Text("Downloading update") },
            text = {
                Column {
                    val fraction = if (current.total > 0) {
                        (current.bytesRead.toFloat() / current.total).coerceIn(0f, 1f)
                    } else {
                        0f
                    }
                    LinearProgressIndicator(
                        progress = { fraction },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "${Formatter.formatShortFileSize(context, current.bytesRead)} / " +
                            Formatter.formatShortFileSize(context, current.total),
                    )
                }
            },
            confirmButton = {},
        )

        is UpdateUiState.ReadyToInstall -> AlertDialog(
            onDismissRequest = { viewModel.dismiss() },
            title = { Text("Ready to install") },
            text = {
                Text(
                    "Version ${current.info.version} has been downloaded. Tap Install, then " +
                        "confirm in the Android installer. If prompted, allow " +
                        "installations from this app.",
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.install() }) { Text("Install") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismiss() }) { Text("Later") }
            },
        )

        is UpdateUiState.Failed -> AlertDialog(
            onDismissRequest = { viewModel.dismiss() },
            title = { Text("Update failed") },
            text = { Text(current.message) },
            confirmButton = {
                TextButton(onClick = { viewModel.dismiss() }) { Text("OK") }
            },
        )

        UpdateUiState.Checking,
        UpdateUiState.UpToDate,
        UpdateUiState.CheckFailed,
        UpdateUiState.Idle,
        -> Unit
    }
}
