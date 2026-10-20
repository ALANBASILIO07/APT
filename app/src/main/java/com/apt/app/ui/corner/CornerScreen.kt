package com.apt.app.ui.corner

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apt.app.data.AptViewModel
import com.apt.app.data.Poem
import com.apt.app.data.Recitation
import com.apt.app.data.Song
import com.apt.app.ui.PreviewPhone
import com.apt.app.ui.components.CardShape
import com.apt.app.ui.components.CircleButton
import com.apt.app.ui.components.Divider
import com.apt.app.ui.components.Eyebrow
import com.apt.app.ui.components.GhostButton
import com.apt.app.ui.components.PillButton
import com.apt.app.ui.components.PillField
import com.apt.app.ui.components.PillShape
import com.apt.app.ui.components.ScreenHeader
import com.apt.app.ui.components.Segmented
import com.apt.app.ui.components.VoicePlayer
import com.apt.app.ui.components.Wave
import com.apt.app.ui.components.formatTime
import com.apt.app.ui.components.pretty
import com.apt.app.ui.components.rememberElapsed
import com.apt.app.ui.components.rememberRecording
import com.apt.app.ui.components.rememberReduceMotion
import com.apt.app.ui.main.MainScreen
import com.apt.app.ui.main.Tab
import com.apt.app.ui.theme.AptText
import com.apt.app.ui.theme.AptTheme
import com.apt.app.ui.theme.Ciruela
import com.apt.app.ui.theme.Crema
import com.apt.app.ui.theme.Grabando
import com.apt.app.ui.theme.Linea
import com.apt.app.ui.theme.Superficie
import com.apt.app.ui.theme.Tinta
import com.apt.app.ui.theme.TintaSuave

@Composable
fun CornerScreen(vm: AptViewModel) {
    var section by rememberSaveable { mutableIntStateOf(0) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ScreenHeader("Nuestro rincón", "Poemas, voz, canciones e ideas")
        Segmented(listOf("Poemas", "Música", "Ideas"), section, { section = it }, Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp))
        when (section) {
            0 -> Poems(vm)
            1 -> Songs(vm.songs)
            else -> Ideas(vm)
        }
    }
}

@Composable
private fun Poems(vm: AptViewModel) {
    var index by rememberSaveable { mutableIntStateOf(0) }
    var writing by rememberSaveable { mutableStateOf(false) }
    if (vm.poems.isEmpty()) return
    val i = index.coerceIn(0, vm.poems.size - 1)
    val poem = vm.poems[i]
    val rec = rememberRecording()

    Column(
        Modifier
            .padding(start = 20.dp, end = 20.dp, top = 16.dp)
            .fillMaxWidth()
            .clip(CardShape)
            .background(Superficie)
            .border(1.dp, Linea, CardShape)
            .padding(start = 24.dp, end = 24.dp, top = 26.dp, bottom = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Eyebrow(poem.title, Modifier.weight(1f))
            CircleButton("←", "Poema anterior", { if (rec.active) rec.end(); index = (i - 1 + vm.poems.size) % vm.poems.size })
            Spacer(Modifier.width(8.dp))
            CircleButton("→", "Poema siguiente", { if (rec.active) rec.end(); index = (i + 1) % vm.poems.size })
        }
        Text(poem.text, style = AptText.script.copy(fontSize = 29.sp, lineHeight = 38.sp), color = Tinta, modifier = Modifier.padding(top = 14.dp))
        Text("— ${poem.by} · ${poem.date.pretty()}", style = AptText.mono, color = TintaSuave, modifier = Modifier.padding(top = 14.dp))

        Divider(Modifier.padding(top = 18.dp))
        Column(Modifier.padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Eyebrow("Recitado")
            poem.audios.forEachIndexed { k, a -> RecitationRow(poem, k, a) }
            if (poem.audios.isEmpty() && !rec.active) {
                Text("Nadie lo ha leído en voz alta todavía.", style = AptText.pequeno, color = TintaSuave)
            }
            if (rec.active) RecordingRow(rec.secs)
            PillButton(
                if (rec.active) "Detener y guardar" else "Grabar mi voz recitándolo",
                {
                    if (rec.active) {
                        val secs = rec.secs.coerceAtLeast(3)
                        val path = rec.end()
                        vm.addRecitation(poem.id, secs, path)
                    } else {
                        VoicePlayer.stop()
                        rec.begin()
                    }
                },
                Modifier.fillMaxWidth(),
                height = 44.dp, fontSize = 14.sp,
            )
        }
    }
    GhostButton("Escribir un poema", { writing = true }, Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 28.dp).fillMaxWidth())

    if (writing) {
        WritePoemSheet(onDismiss = { writing = false }, onSave = { t, body ->
            vm.addPoem(t, body)
            index = vm.poems.size - 1
            writing = false
        })
    }
}

/** Una voz grabada; las de ejemplo (sin archivo) avanzan con un contador. */
@Composable
private fun RecitationRow(poem: Poem, k: Int, a: Recitation) {
    var simOn by remember(poem.id, k) { mutableStateOf(false) }
    val on = if (a.uri != null) VoicePlayer.playingPath == a.uri else simOn
    val el = rememberElapsed(on, a.secs, "${poem.id}-$k") { simOn = false }
    Row(verticalAlignment = Alignment.CenterVertically) {
        CircleButton(
            if (on) "❚❚" else "▶",
            "${if (on) "Pausar" else "Escuchar"} voz de ${a.by}",
            { if (a.uri != null) VoicePlayer.toggle(a.uri) else simOn = !simOn },
            size = 40.dp, filled = on, fontSize = 12.sp,
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Wave(on, Ciruela, 22.dp, progress = if (on) el.toFloat() / a.secs.coerceAtLeast(1) else null)
            Row {
                Text("Voz de ${a.by} · ", style = AptText.pequeno.copy(fontSize = 12.sp), color = TintaSuave)
                Text(
                    if (on) "${formatTime(el)} / ${formatTime(a.secs)}" else formatTime(a.secs),
                    style = AptText.mono,
                    color = TintaSuave,
                )
            }
        }
    }
}

@Composable
private fun RecordingRow(secs: Int) {
    val reduceMotion = rememberReduceMotion()
    val t = rememberInfiniteTransition(label = "grabando")
    val pulse by t.animateFloat(1f, 0.25f, infiniteRepeatable(tween(500), RepeatMode.Reverse), label = "pulso")
    Row(
        Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(PillShape)
            .background(Crema)
            .border(1.dp, Linea, PillShape)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(10.dp).alpha(if (reduceMotion) 1f else pulse).clip(CircleShape).background(Grabando))
        Spacer(Modifier.width(10.dp))
        Text("Grabando tu voz", style = AptText.cuerpo.copy(fontSize = 14.sp), color = Tinta, modifier = Modifier.weight(1f))
        Text(formatTime(secs), style = AptText.mono.copy(fontSize = 13.sp), color = Tinta)
    }
}

@Composable
private fun Songs(songs: List<Song>) {
    val context = LocalContext.current
    Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 28.dp)) {
        songs.forEach { s ->
            Row(Modifier.fillMaxWidth().padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                CircleButton(
                    "▶", "Escuchar ${s.title} en Spotify",
                    {
                        val q = Uri.encode("${s.title} ${s.artist}")
                        runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com/search/$q"))) }
                    },
                    fontSize = 13.sp,
                )
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(s.title, style = AptText.cuerpo.copy(fontWeight = FontWeight.SemiBold), color = Tinta)
                    Text(s.artist, style = AptText.pequeno, color = TintaSuave)
                }
                Text(s.by, style = AptText.script.copy(fontSize = 20.sp), color = TintaSuave, maxLines = 1)
            }
            Divider()
        }
    }
}

@Composable
private fun Ideas(vm: AptViewModel) {
    var idea by rememberSaveable { mutableStateOf("") }
    Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp)) {
        vm.ideas.forEach { d ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable(role = Role.Checkbox) { vm.toggleIdea(d.id) }
                    .semantics { stateDescription = if (d.done) "Hecha" else "Pendiente" }
                    .padding(vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(if (d.done) Ciruela else Color.Transparent)
                        .border(1.5.dp, Ciruela, CircleShape),
                    contentAlignment = Alignment.Center,
                ) { if (d.done) Text("✓", color = Crema, fontSize = 12.sp) }
                Spacer(Modifier.width(14.dp))
                Text(
                    d.text,
                    style = AptText.cuerpo.copy(textDecoration = if (d.done) TextDecoration.LineThrough else null),
                    color = if (d.done) TintaSuave else Tinta,
                    modifier = Modifier.weight(1f),
                )
                Text(d.by, style = AptText.mono.copy(fontSize = 11.sp), color = TintaSuave)
            }
            Divider()
        }
    }
    Row(Modifier.padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 28.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PillField(idea, { idea = it }, "Nueva idea", Modifier.weight(1f), placeholder = "Nueva idea para hacer juntos", height = 46.dp)
        PillButton("Añadir", { vm.addIdea(idea); idea = "" }, height = 46.dp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WritePoemSheet(onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var title by rememberSaveable { mutableStateOf("") }
    var body by rememberSaveable { mutableStateOf("") }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Crema,
        tonalElevation = 0.dp,
        shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
        dragHandle = { Box(Modifier.padding(top = 10.dp, bottom = 14.dp).size(40.dp, 5.dp).clip(PillShape).background(Linea)) },
        modifier = Modifier.semantics { paneTitle = "Escribir un poema" },
    ) {
        Column(Modifier.padding(start = 20.dp, end = 20.dp, bottom = 34.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text("Escribir un poema", style = AptText.cuerpo.copy(fontSize = 20.sp, fontWeight = FontWeight.SemiBold), color = Tinta, modifier = Modifier.weight(1f))
                Text("Cancelar", style = AptText.boton, color = TintaSuave, modifier = Modifier.clickable(role = Role.Button, onClick = onDismiss).padding(8.dp))
            }
            PillField(title, { title = it }, "Título", Modifier.fillMaxWidth(), placeholder = "Título")
            BasicTextField(
                value = body,
                onValueChange = { body = it },
                textStyle = AptText.script.copy(fontSize = 26.sp, lineHeight = 34.sp, color = Tinta),
                cursorBrush = SolidColor(Tinta),
                modifier = Modifier.fillMaxWidth().height(200.dp).semantics { contentDescription = "Poema" },
                decorationBox = { inner ->
                    Box(Modifier.fillMaxSize().border(1.dp, Linea, CardShape).padding(16.dp)) {
                        if (body.isEmpty()) Text("Escribe aquí, un verso por línea", style = AptText.script.copy(fontSize = 26.sp), color = Tinta.copy(alpha = 0.4f))
                        inner()
                    }
                },
            )
            PillButton(
                "Guardar poema",
                { onSave(title.trim().ifEmpty { "Sin título" }, body.trim()) },
                Modifier.fillMaxWidth(),
                height = 50.dp,
                enabled = body.isNotBlank(),
            )
        }
    }
}

// Vista previa para el panel Split/Design (datos de ejemplo; las fotos de internet no cargan).
@Preview(name = "Rincón", device = PreviewPhone, showSystemUi = true)
@Composable
private fun CornerScreenPreview() = AptTheme { MainScreen(AptViewModel(linked = true), Tab.Rincon) }
