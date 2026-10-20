package com.apt.app.ui.us

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apt.app.data.AptViewModel
import com.apt.app.data.SampleData
import com.apt.app.ui.PreviewPhone
import com.apt.app.ui.components.AptPhoto
import com.apt.app.ui.components.CardShape
import com.apt.app.ui.components.Divider
import com.apt.app.ui.components.Eyebrow
import com.apt.app.ui.components.GhostButton
import com.apt.app.ui.components.PillField
import com.apt.app.ui.components.PillValue
import com.apt.app.ui.components.SinceDatePicker
import com.apt.app.ui.components.SwitchRow
import com.apt.app.ui.main.MainScreen
import com.apt.app.ui.main.Tab
import com.apt.app.ui.theme.AptText
import com.apt.app.ui.theme.AptTheme
import com.apt.app.ui.theme.Ciruela
import com.apt.app.ui.theme.Linea
import com.apt.app.ui.theme.Tinta
import com.apt.app.ui.theme.TintaSuave
import java.util.Locale

@Composable
fun UsScreen(vm: AptViewModel) {
    val context = LocalContext.current
    val p = vm.profile
    var picking by rememberSaveable { mutableStateOf(false) }

    val pickCover = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            runCatching { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            vm.updateProfile { it.copy(coverPhoto = uri.toString()) }
        }
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Box(
            Modifier
                .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                .fillMaxWidth()
                .heightIn(min = 260.dp)
                .clip(CardShape)
                .clickable(role = Role.Button) { pickCover.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                .semantics { contentDescription = "Foto de ${p.me} y ${p.partner}. Toca para cambiarla" },
            contentAlignment = Alignment.BottomCenter,
        ) {
            AptPhoto(p.coverPhoto ?: SampleData.COVER, "", Modifier.matchParentSize())
            Box(
                Modifier.matchParentSize().background(
                    Brush.verticalGradient(0.3f to Color(0x00140C16), 1f to Color(0xC7140C16))
                )
            )
            Column(Modifier.padding(horizontal = 20.dp, vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${p.me} & ${p.partner}", style = AptText.script.copy(fontSize = 38.sp, lineHeight = 42.sp), color = Color.White)
                Text(
                    "${vm.daysLabel} DÍAS JUNTOS",
                    style = AptText.mono,
                    color = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }

        Section("Juntos desde") {
            PillValue(p.since.toString(), "Fecha en que se hicieron novios", { picking = true }, Modifier.padding(top = 10.dp))
        }

        Section("Apodos") {
            Column(Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NickField("${p.me} le dice a ${p.partner}", p.nickForPartner) { v -> vm.updateProfile { it.copy(nickForPartner = v) } }
                NickField("${p.partner} le dice a ${p.me}", p.nickForMe) { v -> vm.updateProfile { it.copy(nickForMe = v) } }
            }
        }

        Section("Avisos") {
            Column(Modifier.padding(top = 6.dp)) {
                SwitchRow("Nuevos recuerdos de mi pareja", p.avisoNuevos) { vm.updateProfile { it.copy(avisoNuevos = !it.avisoNuevos) } }
                Divider()
                SwitchRow("Aniversarios y mesiversarios", p.avisoAniversarios) { vm.updateProfile { it.copy(avisoAniversarios = !it.avisoAniversarios) } }
                Divider()
                SwitchRow("Notas de voz y poemas", p.avisoVoz) { vm.updateProfile { it.copy(avisoVoz = !it.avisoVoz) } }
                Divider()
                SwitchRow("“Un día como hoy”", p.avisoUnDiaComoHoy) { vm.updateProfile { it.copy(avisoUnDiaComoHoy = !it.avisoUnDiaComoHoy) } }
                Divider()
            }
        }

        Section("Cuenta vinculada", bottom = 28.dp) {
            Row(
                Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
                    .border(1.dp, Linea, CardShape)
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(10.dp).clip(CircleShape).background(Ciruela))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("${p.partner.lowercase(Locale("es"))}@apt", style = AptText.cuerpo.copy(fontWeight = FontWeight.SemiBold), color = Tinta)
                    Text("${p.myCode} ↔ ${p.partnerCode.ifEmpty { "—" }}", style = AptText.mono, color = TintaSuave)
                }
                GhostButton("Desvincular", { vm.unlink() }, height = 44.dp, fontSize = 13.sp)
            }
        }
    }

    if (picking) {
        SinceDatePicker(p.since, onDismiss = { picking = false }, onPick = { d ->
            vm.updateProfile { it.copy(since = d) }
            picking = false
        })
    }
}

@Composable
private fun Section(title: String, bottom: androidx.compose.ui.unit.Dp = 0.dp, content: @Composable () -> Unit) {
    Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = bottom)) {
        Eyebrow(title)
        content()
    }
}

@Composable
private fun NickField(label: String, value: String, onChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = AptText.pequeno, color = TintaSuave)
        PillField(value, onChange, label, Modifier.fillMaxWidth(), textStyle = AptText.script.copy(fontSize = 26.sp, lineHeight = 30.sp), height = 50.dp)
    }
}

// Vista previa para el panel Split/Design (datos de ejemplo; las fotos de internet no cargan).
@Preview(name = "Nosotros", device = PreviewPhone, showSystemUi = true)
@Composable
private fun UsScreenPreview() = AptTheme { MainScreen(AptViewModel(linked = true), Tab.Nosotros) }
