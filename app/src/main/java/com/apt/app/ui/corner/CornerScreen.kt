package com.apt.app.ui.corner

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.apt.app.data.AptViewModel
import com.apt.app.data.CornerItem
import com.apt.app.data.CornerKind
import com.apt.app.ui.components.AptCard
import com.apt.app.ui.components.ChipRow
import com.apt.app.ui.components.FilledPill
import com.apt.app.ui.components.LabeledField
import com.apt.app.ui.components.OutlinePill
import com.apt.app.ui.components.ScreenTitle
import com.apt.app.ui.components.SectionLabel
import com.apt.app.ui.components.pretty
import com.apt.app.ui.theme.AptText
import com.apt.app.ui.theme.Arcilla
import com.apt.app.ui.theme.Ciruela
import com.apt.app.ui.theme.Crema
import com.apt.app.ui.theme.CremaProfunda
import com.apt.app.ui.theme.Linea
import com.apt.app.ui.theme.Tinta
import com.apt.app.ui.theme.TintaSuave

private val composeLabel = mapOf(
    CornerKind.Nota to "Dejar una nota",
    CornerKind.Poema to "Escribir un poema",
    CornerKind.Musica to "Dedicar una canción",
    CornerKind.Idea to "Anotar una idea",
    CornerKind.Voz to "Grabar mi voz",
)

@Composable
fun CornerScreen(vm: AptViewModel) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var composing by rememberSaveable { mutableStateOf(false) }
    val kind = CornerKind.entries[tab]
    val list = vm.corner.filter { it.kind == kind }

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item {
            Column(Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                ScreenTitle("Rincón")
                Text("Un rincón para dos", style = AptText.scriptPequena, color = Ciruela)
            }
            ChipRow(
                CornerKind.entries.map { it.label }, tab, { tab = it },
                Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(16.dp))
            if (kind == CornerKind.Voz) {
                AptCard(Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                    Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Recita algo para ${vm.profile.partner}", style = AptText.scriptPequena, color = Ciruela)
                        Spacer(Modifier.height(12.dp))
                        VoiceRecorderButton(onRecorded = { path ->
                            vm.addCorner(CornerKind.Voz, "Nota de voz", uri = path)
                        })
                    }
                }
            } else {
                OutlinePill(composeLabel.getValue(kind), { composing = true }, Modifier.padding(horizontal = 20.dp).fillMaxWidth())
            }
            Spacer(Modifier.height(12.dp))
            if (list.isEmpty()) {
                Text(
                    "Todavía no hay nada aquí.",
                    style = AptText.pequeno, color = TintaSuave,
                    modifier = Modifier.padding(20.dp),
                )
            }
        }
        items(list, key = { it.id }) { item ->
            when (kind) {
                CornerKind.Idea -> IdeaRow(item) { vm.toggleIdea(item.id) }
                CornerKind.Musica -> SongRow(item)
                CornerKind.Voz -> VoiceRow(item, onRemove = { vm.removeCorner(item.id) })
                else -> NoteCard(item, Modifier.padding(horizontal = 20.dp, vertical = 6.dp))
            }
        }
    }

    if (composing) {
        ComposeSheet(
            kind = kind,
            partner = vm.profile.partner,
            onDismiss = { composing = false },
            onSend = { text, extra -> vm.addCorner(kind, text, extra); composing = false },
        )
    }
}

@Composable
private fun NoteCard(item: CornerItem, modifier: Modifier = Modifier) {
    AptCard(modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp)) {
            Text(
                item.text,
                style = if (item.kind == CornerKind.Poema) AptText.scriptPequena else AptText.cuerpo,
                color = if (item.kind == CornerKind.Poema) Ciruela else Tinta,
            )
            Spacer(Modifier.height(12.dp))
            Row {
                Text("— ${item.author}", style = AptText.pequeno, color = TintaSuave, modifier = Modifier.weight(1f))
                Text(item.date.pretty(), style = AptText.mono, color = Arcilla)
            }
        }
    }
}

@Composable
private fun IdeaRow(item: CornerItem, onToggle: () -> Unit) {
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .toggleable(value = item.done, role = Role.Checkbox, onValueChange = { onToggle() })
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (item.done) Ciruela else Crema)
                    .border(1.2.dp, Ciruela, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                if (item.done) Icon(Icons.Outlined.Check, null, tint = Crema, modifier = Modifier.size(14.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    item.text,
                    style = AptText.cuerpo.copy(textDecoration = if (item.done) TextDecoration.LineThrough else null),
                    color = if (item.done) TintaSuave else Tinta,
                )
                Text("Idea de ${item.author}", style = AptText.pequeno, color = TintaSuave)
            }
        }
        HorizontalDivider(color = Linea, modifier = Modifier.padding(horizontal = 20.dp))
    }
}

@Composable
private fun SongRow(item: CornerItem) {
    val context = LocalContext.current
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable {
                    // Busca la canción en la app de música que tenga el teléfono
                    val q = Uri.encode("${item.text} ${item.extra}")
                    runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com/search/$q"))) }
                }
                .semantics { contentDescription = "Escuchar ${item.text} de ${item.extra}, dedicada por ${item.author}" }
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(48.dp).background(CremaProfunda),
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Outlined.MusicNote, null, tint = Ciruela) }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(item.text, style = AptText.cuerpo, color = Tinta)
                Text(item.extra, style = AptText.pequeno, color = TintaSuave)
            }
            Text("DE ${item.author.uppercase()}", style = AptText.mono, color = Arcilla)
        }
        HorizontalDivider(color = Linea, modifier = Modifier.padding(horizontal = 20.dp))
    }
}

@Composable
private fun VoiceRow(item: CornerItem, onRemove: () -> Unit) {
    val path = item.uri ?: return
    val playing = VoicePlayer.playingPath == path
    Column {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .border(1.2.dp, Ciruela, CircleShape)
                    .clickable(role = Role.Button) { VoicePlayer.toggle(path) }
                    .semantics { contentDescription = if (playing) "Pausar nota de voz" else "Reproducir nota de voz" },
                contentAlignment = Alignment.Center,
            ) {
                Icon(if (playing) Icons.Outlined.Pause else Icons.Outlined.PlayArrow, null, tint = Ciruela)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                if (playing) AudioWaves() else Text(item.text, style = AptText.cuerpo, color = Tinta)
                Text("${item.author} · ${item.date.pretty()}", style = AptText.pequeno, color = TintaSuave)
            }
            TextButton(onClick = onRemove) { Text("Quitar", color = TintaSuave) }
        }
        HorizontalDivider(color = Linea, modifier = Modifier.padding(horizontal = 20.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComposeSheet(kind: CornerKind, partner: String, onDismiss: () -> Unit, onSend: (String, String) -> Unit) {
    var text by rememberSaveable { mutableStateOf("") }
    var extra by rememberSaveable { mutableStateOf("") }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Crema,
        tonalElevation = 0.dp,
        modifier = Modifier.semantics { paneTitle = composeLabel.getValue(kind) },
    ) {
        Column(
            Modifier.padding(horizontal = 24.dp).padding(bottom = 24.dp).navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            SectionLabel(composeLabel.getValue(kind))
            when (kind) {
                CornerKind.Musica -> {
                    LabeledField("Canción", text, { text = it }, placeholder = "Bésame Mucho")
                    LabeledField("Artista", extra, { extra = it })
                }
                CornerKind.Idea -> LabeledField("Idea", text, { text = it }, placeholder = "Picnic al atardecer")
                CornerKind.Poema -> LabeledField(
                    "Para $partner", text, { text = it },
                    singleLine = false, textStyle = AptText.scriptPequena,
                )
                else -> LabeledField("Para $partner", text, { text = it }, singleLine = false)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onDismiss) { Text("Cancelar", color = TintaSuave) }
                FilledPill("Enviar", { onSend(text.trim(), extra.trim()) }, enabled = text.isNotBlank())
            }
        }
    }
}
