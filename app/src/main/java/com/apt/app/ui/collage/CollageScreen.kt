package com.apt.app.ui.collage

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apt.app.data.AptViewModel
import com.apt.app.data.MemoryKind
import com.apt.app.data.SampleData
import com.apt.app.ui.PreviewPhone
import com.apt.app.ui.components.AptPhoto
import com.apt.app.ui.components.CardShape
import com.apt.app.ui.components.PillButton
import com.apt.app.ui.components.PillField
import com.apt.app.ui.components.ScreenHeader
import com.apt.app.ui.main.MainScreen
import com.apt.app.ui.main.Tab
import com.apt.app.ui.theme.AptText
import com.apt.app.ui.theme.AptTheme
import com.apt.app.ui.theme.Ciruela
import com.apt.app.ui.theme.Crema
import com.apt.app.ui.theme.Linea
import com.apt.app.ui.theme.Superficie
import com.apt.app.ui.theme.TintaSuave

/** Diseños de página: dos columnas, una grande arriba con dos abajo, o cuadrícula de cuatro. */
private enum class Layout(val slots: Int, val label: String) { Dos(2, "Dos fotos"), Tres(3, "Tres fotos"), Cuatro(4, "Cuatro fotos") }

@Composable
fun CollageScreen(vm: AptViewModel) {
    // Fotos disponibles: las de sus recuerdos; si aún no hay, las de ejemplo
    val pool = vm.memories.filter { it.kind == MemoryKind.Foto && it.uri != null }.mapNotNull { it.uri }.distinct()
        .ifEmpty { SampleData.ROLL }
    var layout by rememberSaveable { mutableStateOf(Layout.Tres) }
    var slots by rememberSaveable { mutableStateOf(listOf(5, 9, 6, 1)) }
    var caption by rememberSaveable { mutableStateOf("nuestro año") }
    var saved by rememberSaveable { mutableStateOf(false) }

    fun cycle(k: Int) {
        slots = slots.toMutableList().also { it[k] = ((if (it[k] < 0) -1 else it[k]) + 1) % pool.size }
        saved = false
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenHeader("Crear collage", "Arma una página de nuestro álbum")
        Row(Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Layout.entries.forEach { l -> LayoutButton(l, l == layout) { layout = l } }
        }

        Box(
            Modifier
                .padding(start = 20.dp, end = 20.dp, top = 16.dp)
                .fillMaxWidth()
                .aspectRatio(4f / 5f)
                .clip(CardShape)
                .background(Superficie)
                .border(1.dp, Linea, CardShape)
                .padding(8.dp)
        ) {
            val slot: @Composable (Int, Modifier) -> Unit = { k, m ->
                val img = slots[k].takeIf { it >= 0 }?.let { pool[it % pool.size] }
                PhotoSlot(img, m) { cycle(k) }
            }
            Grid(layout, slot)
            Text(
                caption,
                style = AptText.script.copy(
                    fontSize = 40.sp, lineHeight = 44.sp, color = Color.White,
                    shadow = Shadow(Color(0x73000000), Offset(0f, 2f), 28f),
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(bottom = 14.dp),
            )
        }
        Text(
            "Toca cada espacio para cambiar la foto.",
            style = AptText.pequeno.copy(fontSize = 12.sp),
            color = TintaSuave,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 10.dp),
        )
        Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            PillField(caption, { caption = it; saved = false }, "Dedicatoria del collage", Modifier.fillMaxWidth(), placeholder = "Escribe una dedicatoria")
            PillButton(
                if (saved) "Guardado en recuerdos ✓" else "Guardar en recuerdos",
                { saved = true; vm.showToast("Collage guardado") },
                Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun Grid(layout: Layout, slot: @Composable (Int, Modifier) -> Unit) {
    val gap = Arrangement.spacedBy(8.dp)
    @Composable
    fun RowScope.cell(k: Int) = slot(k, Modifier.weight(1f).fillMaxHeight())
    when (layout) {
        Layout.Dos -> Row(Modifier.fillMaxSize(), horizontalArrangement = gap) { cell(0); cell(1) }
        Layout.Tres -> Column(Modifier.fillMaxSize(), verticalArrangement = gap) {
            Row(Modifier.weight(1.3f).fillMaxWidth()) { cell(0) }
            Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = gap) { cell(1); cell(2) }
        }
        Layout.Cuatro -> Column(Modifier.fillMaxSize(), verticalArrangement = gap) {
            Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = gap) { cell(0); cell(1) }
            Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = gap) { cell(2); cell(3) }
        }
    }
}

@Composable
private fun PhotoSlot(img: String?, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier
            .background(Crema)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = "Cambiar foto de este espacio" },
        contentAlignment = Alignment.Center,
    ) {
        if (img != null) {
            AptPhoto(img, "", Modifier.fillMaxSize())
        } else {
            Canvas(Modifier.fillMaxSize()) {
                drawRect(Linea, style = Stroke(1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))))
            }
            Text("+ foto", style = AptText.mono.copy(fontSize = 11.sp), color = TintaSuave)
        }
    }
}

/** Miniatura del diseño de página. */
@Composable
private fun LayoutButton(layout: Layout, selected: Boolean, onClick: () -> Unit) {
    val color = if (selected) Ciruela else Linea
    val shape = RoundedCornerShape(10.dp)
    val cell: @Composable (Modifier) -> Unit = { m -> Box(m.clip(RoundedCornerShape(2.dp)).background(color)) }
    Box(
        Modifier
            .size(52.dp, 62.dp)
            .clip(shape)
            .border(1.5.dp, color, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = layout.label; this.selected = selected }
            .padding(5.dp)
    ) {
        val gap = Arrangement.spacedBy(3.dp)
        when (layout) {
            Layout.Dos -> Row(Modifier.fillMaxSize(), horizontalArrangement = gap) {
                cell(Modifier.weight(1f).fillMaxHeight()); cell(Modifier.weight(1f).fillMaxHeight())
            }
            Layout.Tres -> Column(Modifier.fillMaxSize(), verticalArrangement = gap) {
                cell(Modifier.weight(1.3f).fillMaxWidth())
                Row(Modifier.weight(1f), horizontalArrangement = gap) {
                    cell(Modifier.weight(1f).fillMaxHeight()); cell(Modifier.weight(1f).fillMaxHeight())
                }
            }
            Layout.Cuatro -> Column(Modifier.fillMaxSize(), verticalArrangement = gap) {
                repeat(2) {
                    Row(Modifier.weight(1f), horizontalArrangement = gap) {
                        cell(Modifier.weight(1f).fillMaxHeight()); cell(Modifier.weight(1f).fillMaxHeight())
                    }
                }
            }
        }
    }
}

// Vista previa para el panel Split/Design (datos de ejemplo; las fotos de internet no cargan).
@Preview(name = "Collage", device = PreviewPhone, showSystemUi = true)
@Composable
private fun CollageScreenPreview() = AptTheme { MainScreen(AptViewModel(linked = true), Tab.Collage) }
