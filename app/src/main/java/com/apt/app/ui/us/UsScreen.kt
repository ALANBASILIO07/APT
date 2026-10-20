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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apt.app.data.AptViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.apt.app.ui.PreviewPhone
import com.apt.app.ui.main.MainScreen
import com.apt.app.ui.main.Tab
import com.apt.app.ui.theme.AptTheme
import com.apt.app.data.SampleData
import com.apt.app.ui.components.AptPhoto
import com.apt.app.ui.components.CardShape
import com.apt.app.ui.components.LabeledField
import com.apt.app.ui.components.OutlinePill
import com.apt.app.ui.components.PillShape
import com.apt.app.ui.components.SectionLabel
import com.apt.app.ui.components.formatDays
import com.apt.app.ui.theme.AptText
import com.apt.app.ui.theme.Ciruela
import com.apt.app.ui.theme.Crema
import com.apt.app.ui.theme.Linea
import com.apt.app.ui.theme.OroRosa
import com.apt.app.ui.theme.Tinta
import com.apt.app.ui.theme.TintaSuave
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun UsScreen(vm: AptViewModel) {
    val context = LocalContext.current
    val p = vm.profile
    var picking by remember { mutableStateOf(false) }

    val pickCover = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            runCatching { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            vm.updateProfile { it.copy(coverPhoto = uri.toString()) }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        // Portada con nombres en cursiva
        Box(
            Modifier
                .fillMaxWidth()
                .height(230.dp)
                .clip(CardShape)
                .clickable { pickCover.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                .semantics { contentDescription = "Foto de portada de ${p.me} y ${p.partner}. Toca para cambiarla" },
        ) {
            AptPhoto(p.coverPhoto ?: SampleData.COVER, "", Modifier.fillMaxSize())
            Box(Modifier.fillMaxSize().background(Tinta.copy(alpha = 0.25f)))
            Column(
                Modifier.align(Alignment.BottomCenter).padding(bottom = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("${p.me} & ${p.partner}", style = AptText.script.copy(fontSize = 36.sp), color = Crema, textAlign = TextAlign.Center)
                Text("${formatDays(vm.daysTogether)} DÍAS JUNTOS", style = AptText.mono, color = Crema)
            }
        }

        Spacer(Modifier.height(24.dp))
        SectionLabel("Juntos desde")
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .clip(PillShape)
                .border(1.dp, Linea, PillShape)
                .background(Color.White.copy(alpha = 0.6f))
                .clickable(role = Role.Button) { picking = true }
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                p.since.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                style = AptText.mono.copy(fontSize = 15.sp),
                color = Tinta,
                modifier = Modifier.weight(1f),
            )
            Icon(Icons.Outlined.CalendarMonth, contentDescription = "Cambiar fecha", tint = Tinta)
        }

        Spacer(Modifier.height(24.dp))
        SectionLabel("Apodos")
        Spacer(Modifier.height(10.dp))
        LabeledField(
            "${p.me} le dice a ${p.partner}", p.nickForPartner,
            { v -> vm.updateProfile { it.copy(nickForPartner = v) } },
            textStyle = AptText.scriptPequena,
        )
        Spacer(Modifier.height(12.dp))
        LabeledField(
            "${p.partner} le dice a ${p.me}", p.nickForMe,
            { v -> vm.updateProfile { it.copy(nickForMe = v) } },
            textStyle = AptText.scriptPequena,
        )

        Spacer(Modifier.height(24.dp))
        SectionLabel("Avisos")
        Spacer(Modifier.height(4.dp))
        NoticeToggle("Notas y poemas nuevos", p.avisoNotas) { v -> vm.updateProfile { it.copy(avisoNotas = v) } }
        NoticeToggle("Recuerdos de hace un año", p.avisoRecuerdos) { v -> vm.updateProfile { it.copy(avisoRecuerdos = v) } }
        NoticeToggle("Aniversarios y fechas", p.avisoAniversarios) { v -> vm.updateProfile { it.copy(avisoAniversarios = v) } }

        Spacer(Modifier.height(24.dp))
        SectionLabel("Vinculación")
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Tu código", style = AptText.pequeno, color = TintaSuave)
                Text(p.myCode, style = AptText.mono.copy(fontSize = 15.sp), color = Tinta)
            }
            Column(Modifier.weight(1f)) {
                Text("Código de ${p.partner}", style = AptText.pequeno, color = TintaSuave)
                Text(p.partnerCode, style = AptText.mono.copy(fontSize = 15.sp), color = Tinta)
            }
        }
        Spacer(Modifier.height(16.dp))
        OutlinePill("Desvincular", { vm.unlink() }, Modifier.fillMaxWidth())
        Spacer(Modifier.height(24.dp))
        Text(
            "Hecho por ${p.me} & ${p.partner}",
            style = AptText.pequeno, color = TintaSuave,
            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
    }

    if (picking) {
        SinceDatePicker(p.since, onDismiss = { picking = false }) { d ->
            vm.updateProfile { it.copy(since = d) }
            picking = false
        }
    }
}

@Composable
private fun NoticeToggle(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .toggleable(value = checked, role = Role.Switch, onValueChange = onChange)
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(label, style = AptText.cuerpo, color = Tinta, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(12.dp))
            Switch(
                checked = checked,
                onCheckedChange = null,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Crema,
                    checkedTrackColor = Ciruela,
                    uncheckedThumbColor = OroRosa,
                    uncheckedTrackColor = Crema,
                    uncheckedBorderColor = Linea,
                ),
            )
        }
        HorizontalDivider(color = Linea)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SinceDatePicker(initial: LocalDate, onDismiss: () -> Unit, onPick: (LocalDate) -> Unit) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = initial.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let { onPick(Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()) }
            }) { Text("Listo", color = Ciruela) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = TintaSuave) } },
        colors = DatePickerDefaults.colors(containerColor = Crema),
    ) {
        DatePicker(
            state = state,
            colors = DatePickerDefaults.colors(
                containerColor = Crema,
                selectedDayContainerColor = Ciruela,
                todayDateBorderColor = Ciruela,
                todayContentColor = Ciruela,
            ),
        )
    }
}

// Vista previa para el panel Split/Design (datos de ejemplo; las fotos de internet no cargan).
@Preview(name = "Nosotros", device = PreviewPhone, showSystemUi = true)
@Composable
private fun UsScreenPreview() = AptTheme { MainScreen(AptViewModel(linked = true), Tab.Nosotros) }
