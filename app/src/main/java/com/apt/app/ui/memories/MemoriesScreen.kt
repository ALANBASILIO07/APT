package com.apt.app.ui.memories

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.apt.app.data.AptViewModel
import com.apt.app.data.Memory
import com.apt.app.data.MemoryKind
import com.apt.app.ui.components.AptCard
import com.apt.app.ui.components.AptPhoto
import com.apt.app.ui.components.ChipRow
import com.apt.app.ui.components.FilledPill
import com.apt.app.ui.components.LabeledField
import com.apt.app.ui.components.ScreenTitle
import com.apt.app.ui.components.SectionLabel
import com.apt.app.ui.components.pretty
import com.apt.app.ui.corner.VoicePlayer
import com.apt.app.ui.corner.VoiceRecorderButton
import com.apt.app.ui.theme.AptText
import com.apt.app.ui.theme.Arcilla
import com.apt.app.ui.theme.Ciruela
import com.apt.app.ui.theme.Crema
import com.apt.app.ui.theme.CremaProfunda
import com.apt.app.ui.theme.Linea
import com.apt.app.ui.theme.Tinta
import com.apt.app.ui.theme.TintaSuave
import java.time.LocalDate

@Composable
fun MemoriesScreen(vm: AptViewModel) {
    var adding by rememberSaveable { mutableStateOf(false) }
    val years = vm.memories.map { it.date.year }.distinct().sortedDescending()

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ScreenTitle("Recuerdos", Modifier.weight(1f))
                Box(
                    Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(1.2.dp, Ciruela, CircleShape)
                        .clickable(role = Role.Button) { adding = true }
                        .semantics { contentDescription = "Agregar recuerdo" },
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Outlined.Add, contentDescription = null, tint = Ciruela) }
            }
            Text(
                "${vm.memories.size} momentos guardados",
                style = AptText.pequeno, color = TintaSuave,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }
        years.forEach { year ->
            item(key = "y$year") {
                SectionLabel("$year", Modifier.padding(start = 20.dp, top = 24.dp, bottom = 10.dp), color = Arcilla)
            }
            items(vm.memories.filter { it.date.year == year }, key = { it.id }) { m ->
                MemoryCard(m, Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
            }
        }
    }

    if (adding) AddMemorySheet(vm, onDismiss = { adding = false })
}

@Composable
private fun MemoryCard(m: Memory, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    AptCard(modifier.fillMaxWidth()) {
        Column {
            when (m.kind) {
                MemoryKind.Foto -> AptPhoto(m.uri, m.title, Modifier.fillMaxWidth().aspectRatio(4f / 3f))
                MemoryKind.Video, MemoryKind.Audio -> Box(
                    Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(if (m.kind == MemoryKind.Video) Tinta else CremaProfunda)
                        .clickable(enabled = m.uri != null) {
                            val u = m.uri ?: return@clickable
                            if (m.kind == MemoryKind.Audio) {
                                // Audio grabado en la app: ruta local
                                VoicePlayer.toggle(u)
                            } else {
                                runCatching {
                                    context.startActivity(
                                        Intent(Intent.ACTION_VIEW).setDataAndType(Uri.parse(u), "video/*")
                                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    )
                                }
                            }
                        }
                        .semantics { contentDescription = "Reproducir ${m.kind.label.lowercase()}: ${m.title}" },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        if (m.kind == MemoryKind.Video) Icons.Outlined.PlayArrow else Icons.Outlined.Mic,
                        contentDescription = null,
                        tint = if (m.kind == MemoryKind.Video) Crema else Ciruela,
                        modifier = Modifier.size(36.dp),
                    )
                }
                MemoryKind.Nota -> Box(
                    Modifier.fillMaxWidth().background(CremaProfunda).padding(24.dp),
                ) {
                    Text("“${m.text}”", style = AptText.scriptPequena, color = Ciruela)
                }
            }
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(m.title, style = AptText.cuerpo, color = Tinta)
                    if (m.place.isNotBlank()) Text(m.place, style = AptText.pequeno, color = TintaSuave)
                    if (m.kind != MemoryKind.Nota && m.text.isNotBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Text(m.text, style = AptText.pequeno, color = Tinta)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Text(m.date.pretty(), style = AptText.mono, color = Arcilla)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMemorySheet(vm: AptViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var kind by rememberSaveable { mutableStateOf(MemoryKind.Foto) }
    var title by rememberSaveable { mutableStateOf("") }
    var place by rememberSaveable { mutableStateOf("") }
    var text by rememberSaveable { mutableStateOf("") }
    var uri by rememberSaveable { mutableStateOf<String?>(null) }
    val date = remember { LocalDate.now() }

    fun keep(u: Uri?) {
        if (u == null) return
        runCatching { context.contentResolver.takePersistableUriPermission(u, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
        uri = u.toString()
    }

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { keep(it) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Crema,
        tonalElevation = 0.dp,
        modifier = Modifier.semantics { paneTitle = "Nuevo recuerdo" },
    ) {
        Column(
            Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            SectionLabel("Nuevo recuerdo")
            ChipRow(
                MemoryKind.entries.map { it.label }, kind.ordinal,
                { kind = MemoryKind.entries[it]; uri = null },
                Modifier.horizontalScroll(rememberScrollState()),
            )
            when (kind) {
                MemoryKind.Foto, MemoryKind.Video -> {
                    val isVideo = kind == MemoryKind.Video
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .border(1.dp, Linea)
                            .clickable {
                                pickMedia.launch(
                                    PickVisualMediaRequest(
                                        if (isVideo) ActivityResultContracts.PickVisualMedia.VideoOnly
                                        else ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }
                            .semantics { contentDescription = if (isVideo) "Elegir video" else "Elegir foto" },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (uri != null && !isVideo) {
                            AptPhoto(uri, "Foto elegida", Modifier.fillMaxSize())
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(if (isVideo) Icons.Outlined.Videocam else Icons.Outlined.Add, null, tint = Ciruela)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    when {
                                        uri != null -> "Video elegido"
                                        isVideo -> "Elegir video"
                                        else -> "Elegir foto"
                                    },
                                    style = AptText.cuerpo, color = Ciruela,
                                )
                            }
                        }
                    }
                }
                MemoryKind.Audio -> VoiceRecorderButton(
                    onRecorded = { path -> uri = path },
                    modifier = Modifier.fillMaxWidth(),
                )
                MemoryKind.Nota -> LabeledField(
                    "Lo que quieres recordar", text, { text = it },
                    singleLine = false, textStyle = AptText.scriptPequena,
                )
            }
            LabeledField("Título", title, { title = it }, placeholder = "Nuestro primer atardecer")
            LabeledField("Lugar", place, { place = it })
            if (kind != MemoryKind.Nota) LabeledField("Nota (opcional)", text, { text = it })
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(date.pretty(), style = AptText.mono, color = Arcilla, modifier = Modifier.weight(1f))
                TextButton(onClick = onDismiss) { Text("Cancelar", color = TintaSuave) }
                FilledPill(
                    "Guardar",
                    onClick = {
                        vm.addMemory(title.trim().ifEmpty { "Sin título" }, place.trim(), date, kind, uri, text.trim())
                        onDismiss()
                    },
                    enabled = when (kind) {
                        MemoryKind.Nota -> text.isNotBlank()
                        else -> uri != null
                    },
                )
            }
        }
    }
}

