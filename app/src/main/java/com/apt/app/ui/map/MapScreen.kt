package com.apt.app.ui.map

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apt.app.R
import com.apt.app.data.AptViewModel
import com.apt.app.data.MemoryKind
import com.apt.app.data.Place
import com.apt.app.data.SampleData
import com.apt.app.ui.PreviewPhone
import com.apt.app.ui.components.AptPhoto
import com.apt.app.ui.components.CardShape
import com.apt.app.ui.components.Eyebrow
import com.apt.app.ui.components.GhostButton
import com.apt.app.ui.components.HeartIcon
import com.apt.app.ui.components.PillButton
import com.apt.app.ui.components.PillShape
import com.apt.app.ui.components.ScreenHeader
import com.apt.app.ui.main.MainScreen
import com.apt.app.ui.main.Tab
import com.apt.app.ui.theme.AptText
import com.apt.app.ui.theme.AptTheme
import com.apt.app.ui.theme.Arcilla
import com.apt.app.ui.theme.Ciruela
import com.apt.app.ui.theme.Crema
import com.apt.app.ui.theme.CremaSuave
import com.apt.app.ui.theme.Linea
import com.apt.app.ui.theme.OroRosa
import com.apt.app.ui.theme.Superficie
import com.apt.app.ui.theme.Tierra
import com.apt.app.ui.theme.Tinta
import com.apt.app.ui.theme.TintaSuave
import kotlin.math.hypot

// El mapa se dibuja en un lienzo de 1000 x 389 (proyección del prototipo) y se muestra a 848 x 330 dp.
private const val VW = 1000f
private const val VH = 389f
private val MAP_W = 848.dp
private val MAP_H = 330.dp

private fun px(lon: Double) = ((lon + 180) / 360).toFloat()
private fun py(lat: Double) = ((80 - lat) / 140).toFloat()

@Composable
fun MapScreen(vm: AptViewModel, onOpenMemories: (String) -> Unit) {
    var sel by rememberSaveable { mutableStateOf<String?>(null) }
    val next = vm.nextPlace

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        if (vm.showAlert) AnniversaryAlert(vm.anniversaryText, onClose = { vm.showAlert = false })

        Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 6.dp)) {
            Eyebrow("Nuestro mapa")
            Spacer(Modifier.height(6.dp))
            Text(
                "${vm.visited.size} lugares vividos, ${vm.wishes.size} por conocer".uppercase(),
                style = AptText.titulo,
                color = Tinta,
            )
        }

        WorldMap(vm.places, next?.id, sel, onSelect = { sel = it }, modifier = Modifier.padding(top = 10.dp))

        Row(
            Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LegendDot(filled = true, "Visitado")
            LegendDot(filled = false, "Queremos ir")
            Spacer(Modifier.weight(1f))
            Text("Desliza el mapa →", style = AptText.pequeno.copy(fontSize = 12.sp), color = TintaSuave)
        }

        vm.places.firstOrNull { it.id == sel }?.let { p ->
            SelectedPlace(
                vm, p, isNext = p.id == next?.id,
                onOpenMemories = { onOpenMemories(p.id) },
                onMakeNext = { vm.setNext(p.id); vm.showToast("${p.name} es su próximo destino") },
                onVisited = { vm.markVisited(p.id) },
                onClose = { sel = null },
            )
        }

        NextDestination(vm)

        Suggestions(vm, onAdded = { sel = it })
    }
}

@Composable
private fun AnniversaryAlert(text: String, onClose: () -> Unit) {
    Row(
        Modifier
            .padding(start = 20.dp, end = 20.dp, top = 14.dp)
            .fillMaxWidth()
            .clip(PillShape)
            .background(Superficie)
            .border(1.dp, Linea, PillShape)
            .padding(start = 16.dp, end = 8.dp, top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HeartIcon(Arcilla, 14.dp)
        Spacer(Modifier.width(10.dp))
        Text(text, style = AptText.pequeno, color = Tinta, modifier = Modifier.weight(1f))
        Box(
            Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable(role = Role.Button, onClick = onClose)
                .semantics { contentDescription = "Cerrar aviso" },
            contentAlignment = Alignment.Center,
        ) { Text("✕", color = TintaSuave, fontSize = 14.sp) }
    }
}

@Composable
private fun rememberLandPath(): Path? {
    val context = LocalContext.current
    return remember {
        runCatching {
            val d = context.resources.openRawResource(R.raw.world_land).bufferedReader().use { it.readText() }
            PathParser().parsePathString(d).toPath()
        }.getOrNull()
    }
}

@Composable
private fun WorldMap(places: List<Place>, nextId: String?, sel: String?, onSelect: (String) -> Unit, modifier: Modifier = Modifier) {
    val land = rememberLandPath()
    val scroll = rememberScrollState()
    val density = LocalDensity.current
    LaunchedEffect(Unit) { scroll.scrollTo(with(density) { 140.dp.roundToPx() }) }

    val visited = places.filter { it.visited }
    val next = places.firstOrNull { it.id == nextId }

    Box(modifier.fillMaxWidth().background(Crema).horizontalScroll(scroll)) {
        // clipToBounds: la Antártida y las copias del otro lado del mapa quedan fuera del lienzo
        Box(Modifier.size(MAP_W, MAP_H).clipToBounds()) {
            Canvas(Modifier.fillMaxSize().semantics { contentDescription = "Mapa del mundo con los lugares de ambos" }) {
                val sx = size.width / VW
                val sy = size.height / VH
                if (land != null) {
                    withTransform({ scale(sx, sy, pivot = Offset.Zero) }) { drawPath(land, Tierra) }
                }
                fun pt(p: Place) = Offset(px(p.lon) * size.width, py(p.lat) * size.height)
                // Curvas entre lugares, arqueadas hacia arriba como en el prototipo
                fun Path.curveTo(a: Place, b: Place) {
                    val pa = pt(a)
                    val pb = pt(b)
                    val mid = Offset((pa.x + pb.x) / 2, (pa.y + pb.y) / 2 - hypot(pb.x - pa.x, pb.y - pa.y) * 0.3f)
                    quadraticTo(mid.x, mid.y, pb.x, pb.y)
                }
                val stroke = 2.4f * sx
                if (visited.isNotEmpty()) {
                    val route = Path().apply {
                        moveTo(pt(visited[0]).x, pt(visited[0]).y)
                        for (i in 1 until visited.size) curveTo(visited[i - 1], visited[i])
                    }
                    drawPath(route, Ciruela, style = Stroke(stroke))
                    if (next != null) {
                        val last = visited.last()
                        val dashed = Path().apply { moveTo(pt(last).x, pt(last).y); curveTo(last, next) }
                        drawPath(
                            dashed, Ciruela,
                            style = Stroke(stroke, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * sx, 7 * sx))),
                        )
                    }
                }
            }
            places.forEach { p -> Pin(p, isNext = p.id == nextId, isSel = p.id == sel, onClick = { onSelect(p.id) }) }
        }
    }
}

@Composable
private fun Pin(p: Place, isNext: Boolean, isSel: Boolean, onClick: () -> Unit) {
    val x = MAP_W * px(p.lon)
    val y = MAP_H * py(p.lat)
    val size by animateDpAsState(if (isSel || isNext) 20.dp else 14.dp, label = "pin")
    val bg = when {
        p.visited -> Ciruela
        isNext -> Arcilla
        else -> Crema
    }
    val border = if (p.visited) Crema else Arcilla
    val state = when {
        p.visited -> "visitado"
        isNext -> "próximo destino"
        else -> "por visitar"
    }
    Box(
        Modifier
            .offset(x - 22.dp, y - 22.dp)
            .size(44.dp)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = "${p.name}, $state" },
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.size(size).clip(CircleShape).background(bg).border(2.dp, border, CircleShape))
    }
    if (isSel || isNext || !p.visited) {
        Text(
            p.name,
            style = AptText.pequeno.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold, lineHeight = 14.sp),
            color = Tinta,
            maxLines = 1,
            modifier = Modifier
                .offset(x + 12.dp, y - 10.dp)
                .clip(PillShape)
                .background(Crema)
                .border(1.dp, Linea, PillShape)
                .padding(horizontal = 7.dp, vertical = 2.dp),
        )
    }
}

@Composable
private fun LegendDot(filled: Boolean, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(9.dp)
                .clip(CircleShape)
                .then(if (filled) Modifier.background(Ciruela) else Modifier.border(2.dp, Arcilla, CircleShape))
        )
        Spacer(Modifier.width(6.dp))
        Text(label, style = AptText.pequeno.copy(fontSize = 12.sp), color = TintaSuave)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SelectedPlace(
    vm: AptViewModel,
    p: Place,
    isNext: Boolean,
    onOpenMemories: () -> Unit,
    onMakeNext: () -> Unit,
    onVisited: () -> Unit,
    onClose: () -> Unit,
) {
    val mems = vm.memoriesOf(p.id)
    val img = mems.firstOrNull { it.kind == MemoryKind.Foto || it.kind == MemoryKind.Video }?.uri
    val meta = when {
        p.visited -> "${p.date} · ${mems.size} RECUERDOS"
        isNext -> "PRÓXIMO"
        else -> "POR VISITAR"
    }
    Column(
        Modifier
            .padding(start = 20.dp, end = 20.dp, top = 14.dp)
            .fillMaxWidth()
            .clip(CardShape)
            .background(Superficie)
            .border(1.dp, Linea, CardShape)
    ) {
        if (img != null) AptPhoto(img, "", Modifier.fillMaxWidth().height(120.dp))
        Column(Modifier.padding(horizontal = 18.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(p.name, style = AptText.cuerpo.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold), color = Tinta, modifier = Modifier.weight(1f))
                Text(meta, style = AptText.mono, color = TintaSuave)
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (p.visited) {
                    PillButton("Ver recuerdos", onOpenMemories, height = 44.dp, fontSize = 14.sp)
                } else {
                    PillButton("Hacer próximo destino", onMakeNext, height = 44.dp, fontSize = 14.sp)
                    GhostButton("Ya fuimos", onVisited, height = 44.dp, fontSize = 14.sp)
                }
                Box(
                    Modifier
                        .height(44.dp)
                        .clip(PillShape)
                        .clickable(role = Role.Button, onClick = onClose)
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center,
                ) { Text("Cerrar", style = AptText.boton, color = TintaSuave) }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NextDestination(vm: AptViewModel) {
    var changing by rememberSaveable { mutableStateOf(false) }
    val next = vm.nextPlace
    Column(
        Modifier
            .padding(start = 20.dp, end = 20.dp, top = 14.dp)
            .fillMaxWidth()
            .clip(CardShape)
            .background(Ciruela)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Eyebrow("Próximo destino", Modifier.weight(1f), color = CremaSuave)
            Text(vm.nextKmLabel, style = AptText.mono, color = CremaSuave)
        }
        Text("nos vemos en", style = AptText.script.copy(fontSize = 30.sp, lineHeight = 33.sp), color = OroRosa)
        Text(
            next?.name ?: "Elige un destino",
            style = AptText.cuerpo.copy(fontSize = 30.sp, lineHeight = 30.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.3).sp),
            color = Crema,
        )
        PillButton(
            if (changing) "Listo" else "Cambiar destino",
            { changing = !changing },
            Modifier.padding(top = 12.dp),
            color = Crema, height = 44.dp, fontSize = 14.sp,
        )
        if (changing) {
            FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                vm.wishes.forEach { w ->
                    val on = w.id == next?.id
                    Box(
                        Modifier
                            .height(44.dp)
                            .clip(PillShape)
                            .background(if (on) Crema else Color.Transparent)
                            .border(1.dp, CremaSuave, PillShape)
                            .clickable(role = Role.Button) { vm.setNext(w.id) }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(w.name, style = AptText.boton.copy(fontSize = 13.sp), color = if (on) Ciruela else Crema)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Suggestions(vm: AptViewModel, onAdded: (String) -> Unit) {
    val options = SampleData.suggestions.filter { s -> vm.places.none { it.id == s.id } }
    Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 28.dp)) {
        Text("Agregar un lugar que queremos visitar", style = AptText.cuerpo.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold), color = Tinta)
        if (options.isEmpty()) {
            Text("Ya están todos en el mapa.", style = AptText.pequeno, color = TintaSuave, modifier = Modifier.padding(top = 10.dp))
        }
        FlowRow(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { s ->
                GhostButton("+ ${s.name}", { vm.addPlace(s); onAdded(s.id) }, height = 44.dp, fontSize = 13.sp)
            }
        }
    }
}

// Vista previa para el panel Split/Design (datos de ejemplo; las fotos de internet no cargan).
@Preview(name = "Mapa", device = PreviewPhone, showSystemUi = true)
@Composable
private fun MapScreenPreview() = AptTheme { MainScreen(AptViewModel(linked = true), Tab.Mapa) }
