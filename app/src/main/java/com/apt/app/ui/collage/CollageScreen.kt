package com.apt.app.ui.collage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apt.app.data.AptViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.apt.app.ui.PreviewPhone
import com.apt.app.ui.main.MainScreen
import com.apt.app.ui.main.Tab
import com.apt.app.ui.theme.AptTheme
import com.apt.app.data.Memory
import com.apt.app.data.MemoryKind
import com.apt.app.ui.components.AptCard
import com.apt.app.ui.components.AptPhoto
import com.apt.app.ui.components.FilledPill
import com.apt.app.ui.components.LabeledField
import com.apt.app.ui.components.OutlinePill
import com.apt.app.ui.components.ScreenTitle
import com.apt.app.ui.theme.AptText
import com.apt.app.ui.theme.Ciruela
import com.apt.app.ui.theme.Crema
import com.apt.app.ui.theme.TintaSuave

private const val MAX = 6

@Composable
fun CollageScreen(vm: AptViewModel) {
    val photos = vm.memories.filter { it.kind == MemoryKind.Foto }
    var selected by rememberSaveable { mutableStateOf(listOf<Long>()) }
    var composing by rememberSaveable { mutableStateOf(false) }
    var caption by rememberSaveable { mutableStateOf("nuestro año") }

    if (composing) {
        CollagePreview(
            photos = selected.mapNotNull { id -> photos.firstOrNull { it.id == id } },
            caption = caption,
            onCaption = { caption = it },
            onBack = { composing = false },
        )
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(Modifier.padding(vertical = 12.dp)) {
                ScreenTitle("Collage")
                Spacer(Modifier.height(4.dp))
                Text(
                    if (selected.isEmpty()) "Elige hasta $MAX fotos para armar un collage."
                    else "${selected.size} de $MAX elegidas",
                    style = AptText.pequeno, color = TintaSuave,
                )
                if (selected.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        FilledPill("Crear collage", { composing = true })
                        TextButton(onClick = { selected = emptyList() }) { Text("Limpiar", color = TintaSuave) }
                    }
                }
            }
        }
        items(photos, key = { it.id }) { m ->
            val index = selected.indexOf(m.id)
            val isOn = index >= 0
            Box(
                Modifier
                    .aspectRatio(1f)
                    .toggleable(value = isOn, role = Role.Checkbox) { on ->
                        selected = if (on && selected.size < MAX) selected + m.id else selected - m.id
                    }
            ) {
                AptPhoto(m.uri, m.title, Modifier.fillMaxSize())
                if (isOn) {
                    Box(Modifier.fillMaxSize().border(3.dp, Ciruela))
                    Box(
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Ciruela),
                        contentAlignment = Alignment.Center,
                    ) { Text("${index + 1}", style = AptText.mono.copy(fontSize = 11.sp), color = Crema) }
                }
            }
        }
    }
}

@Composable
private fun CollagePreview(photos: List<Memory>, caption: String, onCaption: (String) -> Unit, onBack: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ScreenTitle("Tu collage")
        AptCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Primera foto grande y el resto en filas de dos o tres
                photos.firstOrNull()?.let { AptPhoto(it.uri, it.title, Modifier.fillMaxWidth().aspectRatio(16f / 10f)) }
                photos.drop(1).chunked(if (photos.size - 1 == 4) 2 else 3).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        row.forEach { AptPhoto(it.uri, it.title, Modifier.weight(1f).aspectRatio(1f)) }
                    }
                }
                Text(
                    caption,
                    style = AptText.script,
                    color = Ciruela,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        }
        LabeledField("Dedicatoria", caption, onCaption)
        OutlinePill("Volver a elegir", onBack)
    }
}

// Vista previa para el panel Split/Design (datos de ejemplo; las fotos de internet no cargan).
@Preview(name = "Collage", device = PreviewPhone, showSystemUi = true)
@Composable
private fun CollageScreenPreview() = AptTheme { MainScreen(AptViewModel(linked = true), Tab.Collage) }
