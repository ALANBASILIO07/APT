package com.apt.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.apt.app.R

// El diseño usa Satoshi; mientras no esté en res/font usamos Manrope (apt_sans_*).
// Para cambiarla basta con reemplazar los cuatro archivos apt_sans_*.ttf.
val Sans = FontFamily(
    Font(R.font.apt_sans_regular, FontWeight.Normal),
    Font(R.font.apt_sans_medium, FontWeight.Medium),
    Font(R.font.apt_sans_semibold, FontWeight.SemiBold),
    Font(R.font.apt_sans_bold, FontWeight.Bold),
)

// Cursiva para lo que se siente: poemas, dedicatorias, apodos
val Script = FontFamily(Font(R.font.pinyon_script, FontWeight.Normal))

// Monoespaciada para fechas, km y códigos
val Mono = FontFamily(
    Font(R.font.jetbrains_mono_regular, FontWeight.Normal),
    Font(R.font.jetbrains_mono_medium, FontWeight.Medium),
)

object AptText {
    val marca = TextStyle(fontFamily = Sans, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, letterSpacing = 4.sp)
    val titulo = TextStyle(fontFamily = Sans, fontWeight = FontWeight.Medium, fontSize = 17.sp, letterSpacing = 2.5.sp)
    val etiqueta = TextStyle(fontFamily = Sans, fontWeight = FontWeight.Medium, fontSize = 12.sp, letterSpacing = 1.6.sp)
    val cuerpo = TextStyle(fontFamily = Sans, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 22.sp)
    val pequeno = TextStyle(fontFamily = Sans, fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 18.sp)
    val boton = TextStyle(fontFamily = Sans, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, letterSpacing = 2.sp)
    val mono = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Medium, fontSize = 12.sp, letterSpacing = 1.sp)
    val script = TextStyle(fontFamily = Script, fontSize = 30.sp, lineHeight = 38.sp)
    val scriptPequena = TextStyle(fontFamily = Script, fontSize = 22.sp, lineHeight = 30.sp)
}

private val base = Typography()

val AptTypography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = Sans),
    displayMedium = base.displayMedium.copy(fontFamily = Sans),
    displaySmall = base.displaySmall.copy(fontFamily = Sans),
    headlineLarge = base.headlineLarge.copy(fontFamily = Sans),
    headlineMedium = base.headlineMedium.copy(fontFamily = Sans),
    headlineSmall = base.headlineSmall.copy(fontFamily = Sans),
    titleLarge = base.titleLarge.copy(fontFamily = Sans),
    titleMedium = base.titleMedium.copy(fontFamily = Sans),
    titleSmall = base.titleSmall.copy(fontFamily = Sans),
    bodyLarge = base.bodyLarge.copy(fontFamily = Sans),
    bodyMedium = base.bodyMedium.copy(fontFamily = Sans),
    bodySmall = base.bodySmall.copy(fontFamily = Sans),
    labelLarge = base.labelLarge.copy(fontFamily = Sans),
    labelMedium = base.labelMedium.copy(fontFamily = Sans),
    labelSmall = base.labelSmall.copy(fontFamily = Sans),
)
