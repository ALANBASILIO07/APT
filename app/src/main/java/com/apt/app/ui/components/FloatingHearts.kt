package com.apt.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import kotlin.math.sin
import kotlin.random.Random

private data class Heart(val x: Float, val offset: Float, val size: Float, val sway: Float)

/** Corazones que suben despacio sobre la pantalla de bienvenida. */
@Composable
fun FloatingHearts(color: Color, modifier: Modifier = Modifier, count: Int = 12) {
    val reduceMotion = rememberReduceMotion()
    val hearts = remember { List(count) { Heart(Random.nextFloat(), Random.nextFloat(), 6f + Random.nextFloat() * 8f, Random.nextFloat()) } }
    val progress = if (reduceMotion) {
        0.3f
    } else {
        val t = rememberInfiniteTransition(label = "hearts")
        val p by t.animateFloat(0f, 1f, infiniteRepeatable(tween(18000, easing = LinearEasing), RepeatMode.Restart), label = "p")
        p
    }
    Canvas(modifier) {
        hearts.forEach { h ->
            val phase = (h.offset + progress) % 1f
            val y = size.height * (1f - phase)
            val x = size.width * h.x + sin((phase + h.sway) * 6.28f) * 14f
            val alpha = (sin(phase * 3.14f) * 0.45f).coerceAtLeast(0f)
            drawHeart(Offset(x, y), h.size * density / 2f, color.copy(alpha = alpha))
        }
    }
}

fun DrawScope.drawHeart(center: Offset, r: Float, color: Color) {
    val path = Path().apply {
        moveTo(0f, r * 0.9f)
        cubicTo(-r * 1.6f, -r * 0.2f, -r * 0.7f, -r * 1.4f, 0f, -r * 0.5f)
        cubicTo(r * 0.7f, -r * 1.4f, r * 1.6f, -r * 0.2f, 0f, r * 0.9f)
        close()
    }
    translate(center.x, center.y) { drawPath(path, color) }
}
