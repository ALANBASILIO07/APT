package com.apt.app.ui.components

import android.provider.Settings
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.apt.app.R
import com.apt.app.ui.theme.AptText
import com.apt.app.ui.theme.Ciruela
import com.apt.app.ui.theme.Crema
import com.apt.app.ui.theme.Linea
import com.apt.app.ui.theme.Raya1
import com.apt.app.ui.theme.Raya2
import com.apt.app.ui.theme.Superficie
import com.apt.app.ui.theme.Tinta
import com.apt.app.ui.theme.TintaSuave
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// Radio 14 en tarjetas, píldora en controles, fotos con esquinas rectas
val CardShape = RoundedCornerShape(14.dp)
val PillShape = RoundedCornerShape(50)

private val esMX = Locale("es", "MX")
private val dateFmt = DateTimeFormatter.ofPattern("dd MMM yyyy", esMX)

/** "12 ABR 2024" */
fun LocalDate.pretty(): String = format(dateFmt).uppercase(esMX).replace(".", "")

/** "14 feb 2023" */
fun LocalDate.prettyLower(): String = "$dayOfMonth ${format(DateTimeFormatter.ofPattern("MMM", esMX)).lowercase(esMX).replace(".", "")} $year"

/** 75 -> "1:15" */
fun formatTime(secs: Int): String = "${secs / 60}:${(secs % 60).toString().padStart(2, '0')}"

/** Respeta "quitar animaciones" del teléfono: corazones y ondas se detienen. */
@Composable
fun rememberReduceMotion(): Boolean {
    val context = LocalContext.current
    return remember {
        runCatching {
            Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
        }.getOrDefault(false)
    }
}

/** Monograma A&P del diseño; solo cambia de color. */
@Composable
fun AptMark(color: Color, modifier: Modifier = Modifier, description: String? = null) {
    Image(
        painterResource(R.drawable.apt_mark),
        contentDescription = description,
        colorFilter = ColorFilter.tint(color),
        contentScale = ContentScale.Fit,
        modifier = modifier,
    )
}

/** Corazón dibujado (el carácter ♥ sale como emoji rojo en Android). */
@Composable
fun HeartIcon(color: Color, size: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        drawHeart(Offset(this.size.width / 2, this.size.height * 0.55f), this.size.minDimension * 0.42f, color)
    }
}

/** Etiqueta de sección: 12 px, mayúsculas, 0.12em. */
@Composable
fun Eyebrow(text: String, modifier: Modifier = Modifier, color: Color = TintaSuave) {
    Text(text.uppercase(esMX), style = AptText.etiqueta, color = color, modifier = modifier.semantics { heading() })
}

/** Encabezado de cada pestaña: etiqueta y título en mayúsculas espaciadas. */
@Composable
fun ScreenHeader(eyebrow: String, title: String, modifier: Modifier = Modifier) {
    Column(modifier.padding(start = 20.dp, end = 20.dp, top = 22.dp)) {
        Eyebrow(eyebrow)
        Spacer(Modifier.height(6.dp))
        Text(title.uppercase(esMX), style = AptText.titulo, color = Tinta, modifier = Modifier.semantics { heading() })
    }
}

/** Botón de contorno en píldora, el principal del sistema. */
@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Ciruela,
    height: Dp = 48.dp,
    fontSize: TextUnit = 15.sp,
    enabled: Boolean = true,
    uppercase: Boolean = false,
) {
    Box(
        modifier
            .defaultMinSize(minHeight = height)
            .clip(PillShape)
            .border(1.5.dp, color.copy(alpha = if (enabled) 1f else 0.5f), PillShape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            if (uppercase) text.uppercase(esMX) else text,
            style = AptText.boton.copy(fontSize = fontSize, letterSpacing = if (uppercase) fontSize * 0.1f else 0.sp),
            color = color.copy(alpha = if (enabled) 1f else 0.5f),
            textAlign = TextAlign.Center,
        )
    }
}

/** Botón secundario: contorno fino de línea, texto en tinta. */
@Composable
fun GhostButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, height: Dp = 48.dp, fontSize: TextUnit = 15.sp) {
    Box(
        modifier
            .defaultMinSize(minHeight = height)
            .clip(PillShape)
            .border(1.dp, Linea, PillShape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = AptText.boton.copy(fontSize = fontSize), color = Tinta)
    }
}

/** Botón redondo con un símbolo (→, ←, ▶, ❚❚). */
@Composable
fun CircleButton(
    symbol: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    color: Color = Ciruela,
    filled: Boolean = false,
    background: Color = Color.Transparent,
    fontSize: TextUnit = 14.sp,
) {
    Box(
        modifier
            .size(size)
            .clip(CircleShape)
            .background(if (filled) color else background)
            .border(1.5.dp, color, CircleShape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Text(symbol, style = AptText.boton.copy(fontSize = fontSize), color = if (filled) Crema else color)
    }
}

/** Píldora de selección: rellena en ciruela cuando está activa. */
@Composable
fun Chip(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .defaultMinSize(minHeight = 44.dp)
            .clip(PillShape)
            .background(if (selected) Ciruela else Color.Transparent)
            .border(1.dp, if (selected) Ciruela else Linea, PillShape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { this.selected = selected }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = AptText.boton.copy(fontSize = 13.sp), color = if (selected) Crema else Tinta)
    }
}

/** Selector en píldora con opciones de igual ancho (Poemas / Música / Ideas). */
@Composable
fun Segmented(options: List<String>, selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier, fontSize: TextUnit = 14.sp) {
    Row(
        modifier
            .fillMaxWidth()
            .border(1.dp, Linea, PillShape)
            .padding(4.dp)
    ) {
        options.forEachIndexed { i, label ->
            val on = i == selected
            Box(
                Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(PillShape)
                    .background(if (on) Ciruela else Color.Transparent)
                    .clickable(role = Role.Tab) { onSelect(i) }
                    .semantics { this.selected = on },
                contentAlignment = Alignment.Center,
            ) {
                Text(label, style = AptText.boton.copy(fontSize = fontSize), color = if (on) Crema else Tinta)
            }
        }
    }
}

/** Campo en píldora con fondo transparente. */
@Composable
fun PillField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    textStyle: TextStyle = AptText.cuerpo,
    height: Dp = 48.dp,
    borderColor: Color = Linea,
    ink: Color = Tinta,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = textStyle.copy(color = ink),
        cursorBrush = SolidColor(ink),
        keyboardOptions = keyboardOptions,
        modifier = modifier
            .height(height)
            .semantics { contentDescription = label },
        decorationBox = { inner ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(height)
                    .border(1.dp, borderColor, PillShape)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isEmpty() && placeholder.isNotEmpty()) {
                    Text(placeholder, style = textStyle, color = ink.copy(alpha = 0.45f))
                }
                inner()
            }
        },
    )
}

/** Campo que abre un selector al tocarlo (por ejemplo, la fecha). */
@Composable
fun PillValue(
    text: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = AptText.mono.copy(fontSize = 16.sp),
    height: Dp = 50.dp,
    borderColor: Color = Linea,
    ink: Color = Tinta,
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(PillShape)
            .border(1.dp, borderColor, PillShape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = "$label: $text" }
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(text, style = textStyle, color = ink)
    }
}

/** Foto con esquinas rectas; mientras carga muestra la superficie crema. */
@Composable
fun AptPhoto(uri: String?, description: String, modifier: Modifier = Modifier) {
    if (uri == null) {
        Box(modifier.stripes())
    } else {
        AsyncImage(
            model = uri,
            contentDescription = description.ifEmpty { null },
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(Superficie),
            error = ColorPainter(Raya1),
            modifier = modifier,
        )
    }
}

/** Rayas diagonales para espacios vacíos de foto. */
fun Modifier.stripes(): Modifier = clipToBounds().drawBehind {
    drawRect(Raya1)
    // Franjas de 10 dp a 45°: separadas 20 dp en perpendicular, 28.3 dp en horizontal
    val w = 10.dp.toPx()
    val step = 20.dp.toPx() * 1.4142f
    var x = -size.height
    while (x < size.width) {
        drawLine(Raya2, Offset(x, size.height), Offset(x + size.height, 0f), strokeWidth = w)
        x += step
    }
}

// Alturas de las barras de audio del prototipo
private val BARS = listOf(.4f, .7f, .5f, .9f, .6f, 1f, .55f, .8f, .45f, .7f, .95f, .5f, .65f, .85f, .4f, .75f, .6f, .9f, .5f, .7f, .35f, .6f, .8f, .45f)

/** Onda de audio: se mueve mientras suena y marca el avance si hay progreso. */
@Composable
fun Wave(playing: Boolean, color: Color, height: Dp, modifier: Modifier = Modifier, bars: Int = 24, progress: Float? = null) {
    val reduceMotion = rememberReduceMotion()
    val t = rememberInfiniteTransition(label = "wave")
    Row(modifier.height(height), horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
        BARS.take(bars).forEachIndexed { i, b ->
            val s by t.animateFloat(0.35f, 1f, infiniteRepeatable(tween(600 + (i % 5) * 120), RepeatMode.Reverse), label = "b$i")
            val scale = if (playing && !reduceMotion) s else 1f
            val dim = progress != null && i.toFloat() / bars > progress
            Box(
                Modifier
                    .width(3.dp)
                    .height(height * b * scale)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color.copy(alpha = if (dim) 0.35f else 1f))
            )
        }
    }
}

/** Interruptor del diseño: pista ciruela encendida, línea apagada. */
@Composable
fun AptSwitch(on: Boolean, modifier: Modifier = Modifier) {
    val knob by animateDpAsState(if (on) 21.dp else 3.dp, label = "knob")
    Box(
        modifier
            .size(44.dp, 26.dp)
            .clip(PillShape)
            .background(if (on) Ciruela else Linea)
    ) {
        Box(
            Modifier
                .offset { IntOffset(knob.roundToPx(), 3.dp.roundToPx()) }
                .size(20.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

/** Fila con texto e interruptor, toda la fila responde al toque. */
@Composable
fun SwitchRow(label: String, on: Boolean, onToggle: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(role = Role.Switch, onClick = onToggle)
            .semantics { stateDescription = if (on) "Activado" else "Desactivado" }
            .padding(vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = AptText.cuerpo, color = Tinta, modifier = Modifier.weight(1f))
        AptSwitch(on)
    }
}

/** Línea divisoria del sistema. */
@Composable
fun Divider(modifier: Modifier = Modifier, color: Color = Linea) {
    Box(modifier.fillMaxWidth().height(1.dp).background(color))
}

val ScreenPadding = PaddingValues(horizontal = 20.dp)
