package com.apt.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.apt.app.ui.theme.OroRosa

// Proporción del monograma en el diseño: 100 de ancho por 72 de alto
private const val VW = 100f
private const val VH = 72f

/** Oro rosa con brillo, como el logo del diseño sobre ciruela. */
val OroRosaBrush = Brush.linearGradient(
    0f to Color(0xFFB98472),
    0.45f to OroRosa,
    1f to Color(0xFFF3CFBE),
    start = Offset(0f, Float.POSITIVE_INFINITY),
    end = Offset(Float.POSITIVE_INFINITY, 0f),
)

/**
 * Monograma A&P del sistema Atardecer: la A de ápice redondeado comparte su
 * travesaño curvo con la base de la P, y la P termina en una pierna (por eso se lee "AR").
 * Oro rosa sobre ciruela, ciruela sobre crema.
 */
@Composable
fun Monogram(color: Color, modifier: Modifier = Modifier, size: Dp = 32.dp, brush: Brush? = null) {
    Canvas(
        modifier
            .size(size * (VW / VH), size)
            .semantics { contentDescription = "Monograma A y P" }
    ) {
        val sx = this.size.width / VW
        val sy = this.size.height / VH
        fun x(v: Float) = v * sx
        fun y(v: Float) = v * sy
        val stroke = Stroke(width = 3.8f * sx, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val paint = brush ?: SolidColor(color)

        val path = Path().apply {
            // A: pierna izquierda, ápice y pierna derecha corta
            moveTo(x(3f), y(69f))
            lineTo(x(27f), y(5f))
            lineTo(x(40f), y(37f))
            // Travesaño curvo que nace en la pierna izquierda y baja en el tronco de la P
            moveTo(x(9f), y(55f))
            cubicTo(x(17f), y(46f), x(26f), y(42f), x(37f), y(42f))
            cubicTo(x(44f), y(42f), x(49f), y(46f), x(49f), y(53f))
            lineTo(x(49f), y(69f))
            // Panza de la P
            moveTo(x(45f), y(5f))
            lineTo(x(67f), y(5f))
            arcTo(Rect(x(49f), y(5f), x(85f), y(42f)), -90f, 180f, false)
            lineTo(x(40f), y(42f))
            // Pierna
            moveTo(x(57f), y(51f))
            lineTo(x(67f), y(69f))
        }
        drawPath(path, paint, style = stroke)
    }
}
