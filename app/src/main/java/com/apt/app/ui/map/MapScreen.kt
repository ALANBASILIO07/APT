package com.apt.app.ui.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apt.app.data.AptViewModel
import com.apt.app.data.Place
import com.apt.app.data.PlaceStatus
import com.apt.app.ui.components.AptCard
import com.apt.app.ui.components.CardShape
import com.apt.app.ui.components.ChipRow
import com.apt.app.ui.components.Dot
import com.apt.app.ui.components.FilledPill
import com.apt.app.ui.components.LabeledField
import com.apt.app.ui.components.OutlinePill
import com.apt.app.ui.components.ScreenTitle
import com.apt.app.ui.components.SectionLabel
import com.apt.app.ui.theme.AptText
import com.apt.app.ui.theme.Arcilla
import com.apt.app.ui.theme.Ciruela
import com.apt.app.ui.theme.Crema
import com.apt.app.ui.theme.CremaProfunda
import com.apt.app.ui.theme.Linea
import com.apt.app.ui.theme.OroRosa
import com.apt.app.ui.theme.Tinta
import com.apt.app.ui.theme.TintaSuave

private val filters = listOf("Todos", "Por visitar", "Visitados")

@Composable
fun MapScreen(vm: AptViewModel) {
    var filter by rememberSaveable { mutableIntStateOf(0) }
    var selectedId by rememberSaveable { mutableStateOf<Long?>(null) }
    var addingAt by remember { mutableStateOf<Offset?>(null) }
    var placingMode by rememberSaveable { mutableStateOf(false) }

    val visible = vm.places.filter {
        when (filter) {
            1 -> it.status == PlaceStatus.PorVisitar
            2 -> it.status == PlaceStatus.Visitado
            else -> true
        }
    }
    val selected = vm.places.firstOrNull { it.id == selectedId }

    LazyColumn(Modifier.fillMaxSize(), contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)) {
        item {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ScreenTitle("Nuestro mapa", Modifier.weight(1f))
                Box(
                    Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(1.2.dp, Ciruela, CircleShape)
                        .clickable(role = Role.Button) { placingMode = !placingMode; selectedId = null }
                        .semantics { contentDescription = "Agregar lugar" },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null, tint = Ciruela)
                }
            }
            ChipRow(
                filters, filter, { filter = it },
                Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(14.dp))
            if (placingMode) {
                Text(
                    "Toca el mapa donde quieres marcar el lugar.",
                    style = AptText.pequeno,
                    color = Arcilla,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                )
            }
            IllustratedMap(
                places = visible,
                selectedId = selectedId,
                placing = placingMode,
                onPin = { selectedId = if (selectedId == it) null else it },
                onPlace = { addingAt = it; placingMode = false },
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(16.dp))
            if (selected != null) {
                PlaceDetail(
                    selected,
                    onToggle = { vm.toggleVisited(selected.id) },
                    onRemove = { vm.removePlace(selected.id); selectedId = null },
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
                Spacer(Modifier.height(16.dp))
            }
            SectionLabel("Lugares", Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(6.dp))
        }
        items(visible, key = { it.id }) { p ->
            PlaceRow(p, p.id == selectedId) { selectedId = if (selectedId == p.id) null else p.id }
        }
    }

    addingAt?.let { pos ->
        AddPlaceDialog(
            onDismiss = { addingAt = null },
            onSave = { name, region, km, note ->
                vm.addPlace(name, region, km, pos.x, pos.y, note)
                addingAt = null
            },
        )
    }
}

@Composable
private fun IllustratedMap(
    places: List<Place>,
    selectedId: Long?,
    placing: Boolean,
    onPin: (Long) -> Unit,
    onPlace: (Offset) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(CardShape)
            .background(CremaProfunda)
            .border(1.dp, if (placing) Arcilla else Linea, CardShape)
            .pointerInput(placing) {
                if (placing) detectTapGestures { o -> onPlace(Offset(o.x / size.width, o.y / size.height)) }
            }
            .semantics { contentDescription = "Mapa ilustrado con ${places.size} lugares" }
    ) {
        val w = maxWidth
        val h = maxHeight
        Canvas(Modifier.fillMaxSize()) {
            // Retícula suave
            val step = 28.dp.toPx()
            var x = 0f
            while (x < size.width) { drawLine(Linea.copy(alpha = 0.6f), Offset(x, 0f), Offset(x, size.height), 1f); x += step }
            var y = 0f
            while (y < size.height) { drawLine(Linea.copy(alpha = 0.6f), Offset(0f, y), Offset(size.width, y), 1f); y += step }
            // Tierra: silueta simplificada
            val land = Path().apply {
                moveTo(size.width * 0.02f, size.height * 0.05f)
                cubicTo(size.width * 0.25f, size.height * 0.10f, size.width * 0.30f, size.height * 0.35f, size.width * 0.42f, size.height * 0.55f)
                cubicTo(size.width * 0.50f, size.height * 0.72f, size.width * 0.62f, size.height * 0.80f, size.width * 0.70f, size.height * 0.70f)
                cubicTo(size.width * 0.74f, size.height * 0.55f, size.width * 0.80f, size.height * 0.40f, size.width * 0.92f, size.height * 0.36f)
                cubicTo(size.width * 0.96f, size.height * 0.50f, size.width * 0.90f, size.height * 0.68f, size.width * 0.80f, size.height * 0.75f)
                cubicTo(size.width * 0.66f, size.height * 0.92f, size.width * 0.45f, size.height * 0.95f, size.width * 0.30f, size.height * 0.80f)
                cubicTo(size.width * 0.18f, size.height * 0.62f, size.width * 0.10f, size.height * 0.40f, size.width * 0.02f, size.height * 0.05f)
                close()
            }
            drawPath(land, Crema)
            drawPath(land, OroRosa, style = Stroke(1.5f))
            // Ruta punteada entre los lugares visitados
            val visited = places.filter { it.status == PlaceStatus.Visitado }
            if (visited.size > 1) {
                val route = Path().apply {
                    visited.forEachIndexed { i, p ->
                        val o = Offset(p.x * size.width, p.y * size.height)
                        if (i == 0) moveTo(o.x, o.y) else lineTo(o.x, o.y)
                    }
                }
                drawPath(route, Arcilla, style = Stroke(2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))))
            }
        }
        places.forEach { p ->
            val isSel = p.id == selectedId
            val estado = if (p.status == PlaceStatus.Visitado) "visitado" else "por visitar"
            // Área táctil de 44 dp alrededor de un pin visualmente pequeño
            Box(
                Modifier
                    .offset(x = w * p.x - 22.dp, y = h * p.y - 22.dp)
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable(enabled = !placing, role = Role.Button) { onPin(p.id) }
                    .semantics { contentDescription = "${p.name}, $estado" },
                contentAlignment = Alignment.Center,
            ) {
                val visited = p.status == PlaceStatus.Visitado
                Box(
                    Modifier
                        .size(if (isSel) 22.dp else 16.dp)
                        .clip(CircleShape)
                        .background(if (visited) Ciruela else Crema)
                        .border(2.dp, if (visited) Crema else Ciruela, CircleShape)
                )
            }
            if (isSel) {
                Text(
                    p.name,
                    style = AptText.pequeno.copy(fontSize = 12.sp),
                    color = Crema,
                    modifier = Modifier
                        .offset(x = w * p.x - 40.dp, y = h * p.y - 44.dp)
                        .clip(CircleShape)
                        .background(Ciruela)
                        .padding(horizontal = 10.dp, vertical = 3.dp),
                )
            }
        }
    }
}

@Composable
private fun PlaceDetail(p: Place, onToggle: () -> Unit, onRemove: () -> Unit, modifier: Modifier = Modifier) {
    AptCard(modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(p.name, style = AptText.titulo.copy(letterSpacing = 1.sp), color = Tinta)
                    Text(p.region, style = AptText.pequeno, color = TintaSuave)
                }
                Text("${p.km} KM", style = AptText.mono, color = Arcilla)
            }
            if (p.note.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Text(p.note, style = AptText.scriptPequena, color = Ciruela)
            }
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (p.status == PlaceStatus.Visitado) {
                    OutlinePill("Por visitar", onToggle)
                } else {
                    FilledPill("¡Fuimos!", onToggle)
                }
                TextButton(onClick = onRemove) { Text("Quitar", style = AptText.pequeno, color = TintaSuave) }
            }
        }
    }
}

@Composable
private fun PlaceRow(p: Place, selected: Boolean, onClick: () -> Unit) {
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .background(if (selected) CremaProfunda else Crema)
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Dot(if (p.status == PlaceStatus.Visitado) Ciruela else OroRosa)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(p.name, style = AptText.cuerpo, color = Tinta)
                Text(
                    if (p.status == PlaceStatus.Visitado) "Visitado · ${p.region}" else "Por visitar · ${p.region}",
                    style = AptText.pequeno, color = TintaSuave,
                )
            }
            Text("${p.km} KM", style = AptText.mono, color = TintaSuave)
        }
        HorizontalDivider(color = Linea, modifier = Modifier.padding(horizontal = 20.dp))
    }
}

@Composable
private fun AddPlaceDialog(onDismiss: () -> Unit, onSave: (String, String, Int, String) -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var region by rememberSaveable { mutableStateOf("") }
    var km by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Crema,
        title = { Text("NUEVO LUGAR", style = AptText.titulo, color = Tinta) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LabeledField("Nombre", name, { name = it }, placeholder = "Bacalar")
                LabeledField("Estado o país", region, { region = it })
                LabeledField(
                    "Distancia en km", km, { km = it.filter(Char::isDigit).take(5) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                LabeledField("¿Por qué ir?", note, { note = it })
            }
        },
        confirmButton = {
            FilledPill("Guardar", { onSave(name.trim(), region.trim(), km.toIntOrNull() ?: 0, note.trim()) }, enabled = name.isNotBlank())
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = TintaSuave) } },
    )
}
