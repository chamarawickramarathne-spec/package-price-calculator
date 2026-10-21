package lk.hiresmcq.packcalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import lk.hiresmcq.packcalc.ui.components.CrashSurface
import lk.hiresmcq.packcalc.ui.navigation.AppNav
import lk.hiresmcq.packcalc.ui.theme.PackCalcTheme
import lk.hiresmcq.packcalc.util.CrashReporter
import lk.hiresmcq.packcalc.util.StartupLog

class MainActivity : ComponentActivity() {

    private val crashReporter by lazy { CrashReporter(applicationContext) }

    /** The crash report to show, when the app did not start cleanly. */
    private var report by mutableStateOf<String?>(null)

    /** True when the app itself failed to start, so carrying on is pointless. */
    private var blocked by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StartupLog.mark(applicationContext, "activity.onCreate")

        val startupFailure = (application as PackCalcApp).startupFailure
        if (startupFailure != null) {
            // The app could not even build its dependencies: show why and
            // offer no way forward, because there is nothing to carry on to.
            blocked = true
            report = crashReporter.capture(startupFailure, threadName = "startup")
        } else {
            report = crashReporter.pendingReportText()
        }

        setContent {
            PackCalcTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val current = report
                    if (current != null) {
                        CrashSurface(
                            reportText = current,
                            onContinue = if (blocked) {
                                null
                            } else {
                                {
                                    crashReporter.clearPendingReport()
                                    report = null
                                }
                            },
                        )
                    } else {
                        AppNav()
                    }
                }
            }
        }
        StartupLog.mark(applicationContext, "content set")
    }
}
