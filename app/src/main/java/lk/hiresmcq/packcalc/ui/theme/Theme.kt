package lk.hiresmcq.packcalc.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = BrandRed,
    onPrimary = Color.White,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = BrandOrange,
    onSecondary = Color.White,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    background = WarmBackground,
    onBackground = OnWarmBackground,
    surface = WarmBackground,
    onSurface = OnWarmBackground,
    surfaceVariant = SurfaceVariantWarm,
    onSurfaceVariant = OnSurfaceVariantWarm,
    outline = OutlineWarm,
    outlineVariant = OutlineVariantWarm,
    error = ErrorRed,
    onError = OnErrorRed,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
)

@Composable
fun PackCalcTheme(content: @Composable () -> Unit) {
    // Light theme only per product decision.
    MaterialTheme(
        colorScheme = LightColors,
        typography = PackCalcTypography,
        content = content,
    )
}
