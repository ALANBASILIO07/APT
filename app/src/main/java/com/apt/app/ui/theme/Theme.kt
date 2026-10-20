package com.apt.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val AtardecerColors = lightColorScheme(
    primary = Ciruela,
    onPrimary = Crema,
    primaryContainer = OroRosa,
    onPrimaryContainer = Tinta,
    secondary = Arcilla,
    onSecondary = Crema,
    tertiary = OroRosa,
    onTertiary = Tinta,
    background = Crema,
    onBackground = Tinta,
    surface = Crema,
    onSurface = Tinta,
    surfaceVariant = CremaProfunda,
    onSurfaceVariant = TintaSuave,
    surfaceContainerLowest = Crema,
    surfaceContainerLow = Crema,
    surfaceContainer = Crema,
    surfaceContainerHigh = Crema,
    surfaceContainerHighest = CremaProfunda,
    outline = Linea,
    outlineVariant = Linea,
    error = AvisoSinLeer,
)

// Radio 14 en tarjetas, píldora en controles, sin sombras
private val AtardecerShapes = Shapes(
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(14.dp),
    extraLarge = RoundedCornerShape(20.dp),
)

@Composable
fun AptTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AtardecerColors,
        typography = AptTypography,
        shapes = AtardecerShapes,
        content = content,
    )
}

/** Íconos claros sobre fondo ciruela, oscuros sobre crema. */
@Composable
fun SystemBarIcons(darkIcons: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return
    SideEffect {
        val window = (view.context as? Activity)?.window ?: return@SideEffect
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = darkIcons
            isAppearanceLightNavigationBars = darkIcons
        }
    }
}
