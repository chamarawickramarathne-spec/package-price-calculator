package lk.hiresmcq.packcalc.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import lk.hiresmcq.packcalc.BuildConfig

/**
 * Identity strings sent with each usage event.
 *
 * [deviceId] is a salted hash of ANDROID_ID, so it is stable for one install
 * (and across re-installs) but cannot be traced back to the raw system id. On
 * the rare device that reports no ANDROID_ID the value falls back to a
 * constant, which simply means those devices share one bucket.
 */
object DeviceIdentity {

    @SuppressLint("HardwareIds")
    fun deviceId(context: Context): String {
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID,
        )
        val seed = if (androidId.isNullOrBlank()) "unknown-device" else androidId
        return DeviceHash.of("packcalc:$seed")
    }

    val userAgent: String =
        "PackagePriceCalculator/${BuildConfig.VERSION_NAME} Android/${Build.VERSION.SDK_INT}"
}
