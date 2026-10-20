package com.apt.app.ui.memories

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apt.app.data.AptViewModel
import com.apt.app.data.Memory
import com.apt.app.data.MemoryKind
import com.apt.app.ui.PreviewPhone
import com.apt.app.ui.components.AptPhoto
import com.apt.app.ui.components.CardShape
import com.apt.app.ui.components.Chip
import com.apt.app.ui.components.CircleButton
import com.apt.app.ui.components.Eyebrow
import com.apt.app.ui.components.GhostButton
import com.apt.app.ui.components.PillButton
import com.apt.app.ui.components.PillShape
import com.apt.app.ui.components.ScreenHeader
import com.apt.app.ui.components.Segmented
import com.apt.app.ui.components.VoicePlayer
import com.apt.app.ui.components.Wave
import com.apt.app.ui.components.formatTime
import com.apt.app.ui.components.pretty
import com.apt.app.ui.components.rememberElapsed
import com.apt.app.ui.components.rememberRecording
import com.apt.app.ui.components.stripes
import com.apt.app.ui.main.MainScreen
import com.apt.app.ui.main.Tab
import com.apt.app.ui.theme.AptText
import com.apt.app.ui.theme.AptTheme
import com.apt.app.ui.theme.Ciruela
import com.apt.app.ui.theme.Crema
import com.apt.app.ui.theme.CremaSuave
import com.apt.app.ui.theme.Grabando
import com.apt.app.ui.theme.Linea
import com.apt.app.ui.theme.Superficie
import com.apt.app.ui.theme.Tinta
import com.apt.app.ui.theme.TintaSuave
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MemoriesScreen(vm: AptViewModel, placeId: String, onPlace: (String) -> Unit, onCollage: () -> Unit) {
    val place = vm.visited.firstOrNull { it.id == placeId } ?: vm.visited.lastOrNull()
    val mems = place?.let { vm.memoriesOf(it.id) }.orEmpty()
    var slide by rememberSaveable(place?.id) { mutableIntStateOf(0) }
    var sheet by rememberSaveable { mutableStateOf(false) }
    val i = slide.coerceIn(0, (mems.size - 1).coerceAtLeast(0))
    val m = mems.getOrNull(i)

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenHeader("Galería de recuerdos", place?.name ?: "Aún no hay lugares visitados")
        FlowRow(
            Modifier.padding(start = 20.dp, end = 20.dp, top = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            vm.visited.forEach { p -> Chip(p.name, p.id == place?.id, { onPlace(p.id) }) }
        }

        if (m != null) {
            MemoryViewer(m, onPrev = { slide = (i - 1 + mems.size) % mems.size }, onNext = { slide = (i + 1) % mems.size })
            Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)) {
                mems.indices.forEach { k ->
                    Box(
                        Modifier
                            .width(if (k == i) 18.dp else 6.dp)
                            .height(6.dp)
                            .clip(PillShape)
                            .background(if (k == i) Tinta else Linea)
                    )
                }
            }
            Column(Modifier.padding(start = 24.dp, end = 24.dp, top = 14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(m.msg, style = AptText.script.copy(fontSize = 30.sp, lineHeight = 34.sp), color = Tinta)
                Text("— ${m.by} · ${m.date.pretty()}", style = AptText.mono, color = TintaSuave)
            }
        } else {
            EmptyMemories()
        }

        Row(Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 28.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PillButton("+ Agregar recuerdo", { sheet = true }, Modifier.weight(1f))
            GhostButton("Collage", onCollage)
        }
    }

    if (sheet && place != null) {
        AddMemorySheet(
            vm,
            initialPlace = place.id,
            onDismiss = { sheet = false },
            onSaved = { pid, firstNew ->
                sheet = false
                onPlace(pid)
                slide = firstNew
            },
        )
    }
}

@Composable
private fun EmptyMemories() {
    Box(
        Modifier
            .padding(start = 20.dp, end = 20.dp, top = 16.dp)
            .fillMaxWidth()
            .aspectRatio(4f / 5f)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawRoundRect(
                Linea, style = Stroke(1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))),
            )
        }
        Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("aún sin recuerdos", style = AptText.script.copy(fontSize = 32.sp), color = Tinta)
            Text("Sube la primera foto de este lugar.", style = AptText.cuerpo.copy(fontSize = 14.sp), color = TintaSuave)
        }
    }
}

@Composable
private fun MemoryViewer(m: Memory, onPrev: () -> Unit, onNext: () -> Unit) {
    val context = LocalContext.current
    val localAudio = m.kind == MemoryKind.Audio && m.uri != null
    var simPlaying by remember(m.id) { mutableStateOf(false) }
    val playing = if (localAudio) VoicePlayer.playingPath == m.uri else simPlaying
    val el = rememberElapsed(playing, m.secs.coerceAtLeast(1), m.id) { simPlaying = false }

    fun togglePlay() {
        when {
            localAudio -> VoicePlayer.toggle(m.uri!!)
            // Videos propios se abren en el reproductor del teléfono
            m.kind == MemoryKind.Video && m.uri?.startsWith("content:") == true -> runCatching {
                context.startActivity(Intent(Intent.ACTION_VIEW).setDataAndType(Uri.parse(m.uri), "video/*").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION))
            }
            else -> simPlaying = !simPlaying
        }
    }

    val zoom by animateFloatAsState(
        if (m.kind == MemoryKind.Video && playing) 1.18f else 1f,
        tween(if (playing) 30_000 else 300, easing = LinearEasing), label = "zoom",
    )

    Box(
        Modifier
            .padding(start = 20.dp, end = 20.dp, top = 16.dp)
            .fillMaxWidth()
            .aspectRatio(4f / 5f)
            .clipToBounds()
            .stripes()
    ) {
        when (m.kind) {
            MemoryKind.Foto, MemoryKind.Video -> AptPhoto(
                m.uri, m.label,
                Modifier.fillMaxSize().graphicsLayer { scaleX = zoom; scaleY = zoom },
            )
            MemoryKind.Audio -> Column(
                Modifier.fillMaxSize().background(Ciruela).padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterVertically),
            ) {
                Eyebrow("Nota de voz", color = CremaSuave)
                Wave(playing, Crema, 48.dp, progress = el.toFloat() / m.secs.coerceAtLeast(1))
                CircleButton(if (playing) "❚❚" else "▶", if (playing) "Pausar" else "Reproducir", ::togglePlay, size = 60.dp, color = Crema, fontSize = 16.sp)
                Text("${formatTime(el)} / ${formatTime(m.secs)}", style = AptText.mono, color = CremaSuave)
            }
            MemoryKind.Nota -> Box(Modifier.fillMaxSize().background(Superficie).padding(40.dp), contentAlignment = Alignment.Center) {
                Text(m.label, style = AptText.script.copy(fontSize = 34.sp, lineHeight = 44.sp), color = Tinta, textAlign = TextAlign.Center)
            }
        }

        if (m.kind == MemoryKind.Video) {
            Box(
                Modifier
                    .align(Alignment.Center)
                    .size(64.dp)
                    .graphicsLayer { alpha = if (playing) 0.35f else 1f }
                    .clip(CircleShape)
                    .background(Color(0x80141018))
                    .clickable(role = Role.Button, onClick = ::togglePlay)
                    .semantics { contentDescription = if (playing) "Pausar" else "Reproducir" },
                contentAlignment = Alignment.Center,
            ) { Text(if (playing) "❚❚" else "▶", color = Color.White, fontSize = 18.sp) }
            Row(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(36.dp)
                    .clip(PillShape)
                    .background(Color(0xB8FFFFFF))
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(formatTime(el), style = AptText.mono, color = Color(0xFF1C1922))
                Box(Modifier.weight(1f).height(3.dp).clip(PillShape).background(Color(0x26000000))) {
                    Box(Modifier.fillMaxHeight().fillMaxWidth(el.toFloat() / m.secs.coerceAtLeast(1)).background(Color(0xFF1C1922)))
                }
                Text(formatTime(m.secs), style = AptText.mono, color = Color(0xFF1C1922))
            }
        }

        Box(
            Modifier
                .padding(14.dp)
                .height(26.dp)
                .clip(PillShape)
                .background(Color(0x73141018))
                .padding(horizontal = 10.dp),
            contentAlignment = Alignment.Center,
        ) { Text(m.kind.label, style = AptText.boton.copy(fontSize = 11.sp, letterSpacing = 0.66.sp), color = Color.White) }

        CircleButton("←", "Recuerdo anterior", onPrev, Modifier.align(Alignment.CenterStart).padding(start = 12.dp), size = 40.dp, background = Crema, fontSize = 16.sp)
        CircleButton("→", "Recuerdo siguiente", onNext, Modifier.align(Alignment.CenterEnd).padding(end = 12.dp), size = 40.dp, background = Crema, fontSize = 16.sp)
    }
}

private enum class UpType(val label: String) { Foto("Fotos"), Video("Video"), Audio("Audio"), Nota("Nota") }

private fun keepAccess(context: Context, uri: Uri) {
    runCatching { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
}

private fun videoSecs(context: Context, uri: Uri): Int {
    val r = MediaMetadataRetriever()
    return try {
        r.setDataSource(context, uri)
        ((r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L) / 1000).toInt()
    } catch (e: Exception) {
        0
    } finally {
        r.release()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AddMemorySheet(vm: AptViewModel, initialPlace: String, onDismiss: () -> Unit, onSaved: (String, Int) -> Unit) {
    val context = LocalContext.current
    var type by rememberSaveable { mutableStateOf(UpType.Foto) }
    var place by rememberSaveable { mutableStateOf(initialPlace) }
    var msg by rememberSaveable { mutableStateOf("") }
    val picked = remember { mutableStateListOf<String>() }
    var audioPath by remember { mutableStateOf<String?>(null) }
    var audioSecs by remember { mutableIntStateOf(0) }
    val rec = rememberRecording()

    val pickPhotos = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(12)) { uris ->
        uris.forEach { keepAccess(context, it); picked.add(it.toString()) }
    }
    val pickVideo = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) { keepAccess(context, uri); picked.clear(); picked.add(uri.toString()) }
    }

    val isMedia = type == UpType.Foto || type == UpType.Video
    val ready = when (type) {
        UpType.Foto, UpType.Video -> picked.isNotEmpty()
        UpType.Audio -> audioSecs > 0 && !rec.active
        UpType.Nota -> msg.isNotBlank()
    }
    val label = when (type) {
        UpType.Foto -> if (picked.isEmpty()) "Elige qué subir" else "Subir ${picked.size} ${if (picked.size > 1) "fotos" else "foto"}"
        UpType.Video -> if (picked.isEmpty()) "Elige qué subir" else "Subir 1 video"
        UpType.Audio -> "Subir nota de voz"
        UpType.Nota -> "Guardar nota"
    }

    fun submit() {
        if (!ready) return
        val text = msg.trim().ifEmpty { "Un momento más para guardar." }
        val today = LocalDate.now()
        val me = vm.profile.me
        var id = vm.newMemoryId()
        val items = when (type) {
            UpType.Foto -> picked.map { Memory(id++, place, MemoryKind.Foto, "nueva foto", text, me, today, it) }
            UpType.Video -> listOf(Memory(id, place, MemoryKind.Video, "nuevo video", text, me, today, picked[0], videoSecs(context, Uri.parse(picked[0]))))
            UpType.Audio -> listOf(Memory(id, place, MemoryKind.Audio, "voz", text, me, today, audioPath, audioSecs))
            UpType.Nota -> listOf(Memory(id, place, MemoryKind.Nota, msg.trim(), "Nota escrita hoy.", me, today))
        }
        val before = vm.memoriesOf(place).size
        vm.addMemories(items)
        onSaved(place, before)
    }

    ModalBottomSheet(
        onDismissRequest = { if (rec.active) rec.end(); onDismiss() },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Crema,
        tonalElevation = 0.dp,
        shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
        dragHandle = { Box(Modifier.padding(top = 10.dp, bottom = 14.dp).size(40.dp, 5.dp).clip(PillShape).background(Linea)) },
        modifier = Modifier.semantics { paneTitle = "Agregar recuerdo" },
    ) {
        Column(Modifier.padding(start = 20.dp, end = 20.dp, bottom = 34.dp).verticalScroll(rememberScrollState())) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text("Agregar recuerdo", style = AptText.cuerpo.copy(fontSize = 20.sp, fontWeight = FontWeight.SemiBold), color = Tinta, modifier = Modifier.weight(1f))
                Text(
                    "Cancelar",
                    style = AptText.boton,
                    color = TintaSuave,
                    modifier = Modifier.clickable(role = Role.Button) { if (rec.active) rec.end(); onDismiss() }.padding(8.dp),
                )
            }
            Segmented(
                UpType.entries.map { it.label }, type.ordinal,
                { type = UpType.entries[it]; picked.clear(); if (rec.active) rec.end() },
                Modifier.padding(top = 14.dp), fontSize = 13.sp,
            )

            if (isMedia) {
                Row(Modifier.padding(top = 16.dp).fillMaxWidth()) {
                    Text("De tu galería", style = AptText.pequeno, color = TintaSuave, modifier = Modifier.weight(1f))
                    Text("${picked.size} SELECCIONADAS", style = AptText.mono.copy(fontSize = 13.sp), color = TintaSuave)
                }
                // Cuadrícula de 4: el primer mosaico abre la galería, los demás son lo elegido (tocar quita)
                val cells: List<String?> = listOf(null) + picked
                Column(Modifier.padding(top = 8.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    cells.chunked(4).forEachIndexed { r, row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            row.forEachIndexed { c, uri ->
                                val k = r * 4 + c - 1
                                val tile = Modifier.weight(1f).aspectRatio(1f).clip(RoundedCornerShape(6.dp))
                                if (uri == null) {
                                    Box(
                                        tile
                                            .background(Superficie)
                                            .clickable(role = Role.Button) {
                                                val req = if (type == UpType.Video) ActivityResultContracts.PickVisualMedia.VideoOnly else ActivityResultContracts.PickVisualMedia.ImageOnly
                                                if (type == UpType.Video) pickVideo.launch(PickVisualMediaRequest(req)) else pickPhotos.launch(PickVisualMediaRequest(req))
                                            },
                                        contentAlignment = Alignment.Center,
                                    ) { Text("+ elegir", style = AptText.mono.copy(fontSize = 11.sp), color = TintaSuave) }
                                } else {
                                    Box(tile.clickable(role = Role.Button) { picked.removeAt(k) }.semantics { contentDescription = "Elegida ${k + 1}, toca para quitarla" }) {
                                        AptPhoto(uri, "", Modifier.fillMaxSize().graphicsLayer { alpha = 0.75f })
                                        Box(
                                            Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(5.dp)
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .background(Ciruela)
                                                .border(1.5.dp, Color.White, CircleShape),
                                            contentAlignment = Alignment.Center,
                                        ) { Text("${k + 1}", style = AptText.boton.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold), color = Color.White) }
                                    }
                                }
                            }
                            // Relleno para que la última fila no estire sus mosaicos
                            repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                }
            }

            if (type == UpType.Audio) {
                val secsShown = if (rec.active) rec.secs else audioSecs
                Column(
                    Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(Ciruela)
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Wave(rec.active, Crema, 40.dp)
                    Text(formatTime(secsShown), style = AptText.mono.copy(fontSize = 22.sp), color = Crema)
                    val inner by animateFloatAsState(if (rec.active) 24f else 52f, label = "rec")
                    Box(
                        Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .border(2.dp, Crema, CircleShape)
                            .clickable(role = Role.Button) {
                                if (rec.active) {
                                    val s = rec.secs
                                    audioPath = rec.end()
                                    audioSecs = s.coerceAtLeast(1)
                                } else {
                                    audioSecs = 0
                                    rec.begin()
                                }
                            }
                            .semantics { contentDescription = if (rec.active) "Detener grabación" else "Grabar nota de voz" },
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(Modifier.size(inner.dp).clip(RoundedCornerShape(if (rec.active) 6.dp else 26.dp)).background(Grabando))
                    }
                    Text(
                        when {
                            rec.active -> "Toca para detener"
                            audioSecs > 0 -> "Listo. Toca para grabar de nuevo"
                            else -> "Toca para grabar una nota de voz"
                        },
                        style = AptText.pequeno,
                        color = CremaSuave,
                    )
                }
            }

            Text("Lugar", style = AptText.pequeno, color = TintaSuave, modifier = Modifier.padding(top = 16.dp))
            FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                vm.visited.forEach { p -> Chip(p.name, p.id == place, { place = p.id }) }
            }

            val msgLabel = if (type == UpType.Nota) "Tu nota" else "Mensaje (opcional)"
            Text(msgLabel, style = AptText.pequeno, color = TintaSuave, modifier = Modifier.padding(top = 16.dp))
            BasicTextField(
                value = msg,
                onValueChange = { msg = it },
                textStyle = AptText.script.copy(fontSize = 22.sp, lineHeight = 28.sp, color = Tinta),
                cursorBrush = SolidColor(Tinta),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .height(80.dp)
                    .semantics { contentDescription = msgLabel },
                decorationBox = { inner ->
                    Box(Modifier.fillMaxSize().border(1.dp, Linea, CardShape).padding(horizontal = 16.dp, vertical = 12.dp)) {
                        if (msg.isEmpty()) Text("Escribe algo para recordar este momento", style = AptText.script.copy(fontSize = 22.sp, lineHeight = 28.sp), color = Tinta.copy(alpha = 0.4f))
                        inner()
                    }
                },
            )

            PillButton(label, ::submit, Modifier.padding(top = 16.dp).fillMaxWidth(), height = 50.dp, enabled = ready)
        }
    }
}

// Vista previa para el panel Split/Design (datos de ejemplo; las fotos de internet no cargan).
@Preview(name = "Recuerdos", device = PreviewPhone, showSystemUi = true)
@Composable
private fun MemoriesScreenPreview() = AptTheme { MainScreen(AptViewModel(linked = true), Tab.Recuerdos) }
