package com.apt.app.ui.components

import android.provider.Settings
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.apt.app.ui.theme.AptText
import com.apt.app.ui.theme.Ciruela
import com.apt.app.ui.theme.Crema
import com.apt.app.ui.theme.CremaProfunda
import com.apt.app.ui.theme.Linea
import com.apt.app.ui.theme.OroRosa
import com.apt.app.ui.theme.Tinta
import com.apt.app.ui.theme.TintaSuave
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

val CardShape = RoundedCornerShape(14.dp)
val PillShape = RoundedCornerShape(50)

fun formatDays(days: Long): String = NumberFormat.getIntegerInstance(Locale.US).format(days)

private val dateFmt = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es", "MX"))
fun LocalDate.pretty(): String = format(dateFmt).uppercase(Locale("es", "MX")).replace(".", "")

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

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier, color: Color = TintaSuave) {
    Text(
        text.uppercase(),
        style = AptText.etiqueta,
        color = color,
        modifier = modifier.semantics { heading() },
    )
}

@Composable
fun ScreenTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text.uppercase(),
        style = AptText.titulo,
        color = Tinta,
        modifier = modifier.semantics { heading() },
    )
}

@Composable
fun AptCard(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, content: @Composable () -> Unit) {
    val base = modifier
        .clip(CardShape)
        .background(Color.White.copy(alpha = 0.55f))
        .border(1.dp, Linea, CardShape)
    Box(if (onClick != null) base.clickable(onClick = onClick) else base) { content() }
}

/** Botón de contorno en píldora, el principal del sistema. */
@Composable
fun OutlinePill(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Ciruela,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = PillShape,
        border = BorderStroke(1.2.dp, if (enabled) color else color.copy(alpha = 0.35f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color, disabledContentColor = color.copy(alpha = 0.4f)),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
        modifier = modifier.defaultMinSize(minHeight = 48.dp),
    ) {
        Text(text.uppercase(), style = AptText.boton)
    }
}

@Composable
fun FilledPill(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = PillShape,
        colors = ButtonDefaults.buttonColors(containerColor = Ciruela, contentColor = Crema),
        elevation = null,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
        modifier = modifier.defaultMinSize(minHeight = 48.dp),
    ) {
        Text(text.uppercase(), style = AptText.boton)
    }
}

/** Píldora de selección: rellena en ciruela cuando está activa. */
@Composable
fun Chip(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .defaultMinSize(minHeight = 40.dp)
            .clip(PillShape)
            .background(if (selected) Ciruela else Color.Transparent)
            .border(1.dp, if (selected) Ciruela else Linea, PillShape)
            .clickable(role = Role.Tab, onClick = onClick)
            .semantics { this.selected = selected }
            .padding(horizontal = 16.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = AptText.pequeno, color = if (selected) Crema else Tinta)
    }
}

@Composable
fun ChipRow(options: List<String>, selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEachIndexed { i, label -> Chip(label, i == selected, { onSelect(i) }) }
    }
}

/** Campo con etiqueta visible arriba y contorno en píldora. */
@Composable
fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    textStyle: TextStyle = AptText.cuerpo,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    shapeRadiusPill: Boolean = true,
) {
    val shape = if (shapeRadiusPill && singleLine) PillShape else CardShape
    Column(modifier) {
        Text(label, style = AptText.pequeno, color = TintaSuave)
        Spacer(Modifier.height(6.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            minLines = if (singleLine) 1 else 4,
            textStyle = textStyle.copy(color = Tinta),
            cursorBrush = SolidColor(Ciruela),
            keyboardOptions = keyboardOptions,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { this.contentDescription = label }
                .clip(shape)
                .border(1.dp, Linea, shape)
                .background(Color.White.copy(alpha = 0.6f))
                .padding(horizontal = 20.dp, vertical = 14.dp),
            decorationBox = { inner ->
                Box {
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(placeholder, style = textStyle, color = TintaSuave.copy(alpha = 0.6f))
                    }
                    inner()
                }
            },
        )
    }
}

/** Foto con esquinas rectas; si no carga muestra un bloque crema con ícono. */
@Composable
fun AptPhoto(uri: String?, description: String, modifier: Modifier = Modifier) {
    if (uri == null) {
        Box(modifier.background(CremaProfunda), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.Image, contentDescription = description, tint = OroRosa, modifier = Modifier.size(28.dp))
        }
    } else {
        AsyncImage(
            model = uri,
            contentDescription = description,
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(CremaProfunda),
            error = ColorPainter(OroRosa.copy(alpha = 0.5f)),
            modifier = modifier,
        )
    }
}

@Composable
fun Dot(color: Color, modifier: Modifier = Modifier) {
    Box(modifier.size(8.dp).clip(CircleShape).background(color))
}
