package com.apt.app.ui.link

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apt.app.data.AptViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.apt.app.ui.PreviewPhone
import com.apt.app.ui.theme.AptTheme
import com.apt.app.ui.components.FloatingHearts
import com.apt.app.ui.components.Monogram
import com.apt.app.ui.components.OroRosaBrush
import com.apt.app.ui.components.OutlinePill
import com.apt.app.ui.components.PillShape
import com.apt.app.ui.components.pretty
import com.apt.app.ui.theme.AptText
import com.apt.app.ui.theme.Ciruela
import com.apt.app.ui.theme.Crema
import com.apt.app.ui.theme.OroRosa
import com.apt.app.ui.theme.SystemBarIcons
import com.apt.app.ui.us.SinceDatePicker
import java.time.LocalDate

/** Bienvenida y vinculación con la pareja (código de 4+ caracteres y fecha en que se hicieron novios). */
@Composable
fun LinkScreen(vm: AptViewModel, initialStep: Int = 0) {
    SystemBarIcons(darkIcons = false)
    var step by rememberSaveable { mutableIntStateOf(initialStep) }
    var partnerCode by rememberSaveable { mutableStateOf("") }
    BackHandler(enabled = step > 0) { step-- }

    Box(
        Modifier
            .fillMaxSize()
            .background(Ciruela)
            // Halo suave detrás del logo, como en la pantalla del diseño
            .background(Brush.radialGradient(listOf(Color(0xFF74365F), Ciruela), center = Offset(540f, 620f), radius = 900f))
    ) {
        FloatingHearts(OroRosa, Modifier.fillMaxSize())
        AnimatedContent(targetState = step, label = "link-step") { s ->
            when (s) {
                0 -> Welcome(onLink = { step = 1 })
                1 -> CodeStep(myCode = vm.profile.myCode, onNext = { partnerCode = it; step = 2 })
                else -> DateStep(
                    initialMe = vm.profile.me,
                    initialPartner = vm.profile.partner,
                    initialSince = vm.profile.since,
                    onDone = { since, me, partner -> vm.link(partnerCode, since, me, partner) },
                )
            }
        }
    }
}

@Composable
private fun Welcome(onLink: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(0.6f))
        Monogram(OroRosa, size = 110.dp, brush = OroRosaBrush)
        Spacer(Modifier.height(32.dp))
        Text("APT", style = AptText.marca.copy(fontSize = 20.sp, letterSpacing = 8.sp), color = Crema)
        Text("para dos", style = AptText.script.copy(fontSize = 40.sp), color = OroRosa)
        Spacer(Modifier.weight(1f))
        Text("Un Rincón para Dos", style = AptText.cuerpo.copy(fontSize = 18.sp), color = Crema)
        Spacer(Modifier.height(24.dp))
        OutlinePill("Vincular con mi pareja", onLink, Modifier.fillMaxWidth(), color = Crema)
        Spacer(Modifier.height(20.dp))
        Text("Hecho por Alan & Paulina", style = AptText.pequeno, color = Crema.copy(alpha = 0.75f))
    }
}

@Composable
private fun CodeStep(myCode: String, onNext: (String) -> Unit) {
    var code by rememberSaveable { mutableStateOf("") }
    val valid = code.trim().length >= 4
    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(48.dp))
        Monogram(OroRosa, size = 56.dp, brush = OroRosaBrush)
        Spacer(Modifier.height(32.dp))
        Text("TU CÓDIGO", style = AptText.etiqueta, color = OroRosa)
        Spacer(Modifier.height(10.dp))
        Text(
            myCode.chunked(3).joinToString(" "),
            style = AptText.mono.copy(fontSize = 30.sp, letterSpacing = 6.sp),
            color = Crema,
            modifier = Modifier.semantics { contentDescription = "Tu código: ${myCode.toList().joinToString(" ")}" },
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Compártelo con tu pareja o escribe el suyo abajo.",
            style = AptText.pequeno,
            color = Crema.copy(alpha = 0.75f),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(40.dp))
        Text("CÓDIGO DE TU PAREJA", style = AptText.etiqueta, color = OroRosa)
        Spacer(Modifier.height(10.dp))
        BasicTextField(
            value = code,
            onValueChange = { code = it.filter { c -> c.isLetterOrDigit() }.take(8).uppercase() },
            singleLine = true,
            textStyle = AptText.mono.copy(fontSize = 22.sp, letterSpacing = 4.sp, color = Crema, textAlign = TextAlign.Center),
            cursorBrush = SolidColor(OroRosa),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Código de tu pareja" }
                .clip(PillShape)
                .border(1.2.dp, Crema.copy(alpha = 0.6f), PillShape)
                .padding(horizontal = 20.dp, vertical = 16.dp),
        )
        Spacer(Modifier.height(8.dp))
        Text("Mínimo 4 caracteres", style = AptText.pequeno, color = Crema.copy(alpha = 0.6f))
        Spacer(Modifier.height(40.dp))
        OutlinePill("Continuar", { onNext(code.trim()) }, Modifier.fillMaxWidth(), color = Crema, enabled = valid)
    }
}

@Composable
private fun DateStep(
    initialMe: String,
    initialPartner: String,
    initialSince: LocalDate,
    onDone: (LocalDate, String, String) -> Unit,
) {
    var since by remember { mutableStateOf(initialSince) }
    var me by rememberSaveable { mutableStateOf(initialMe) }
    var partner by rememberSaveable { mutableStateOf(initialPartner) }
    var picking by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(48.dp))
        Text("¿Cuándo empezó todo?", style = AptText.script, color = OroRosa, textAlign = TextAlign.Center)
        Spacer(Modifier.height(32.dp))
        Text("JUNTOS DESDE", style = AptText.etiqueta, color = OroRosa)
        Spacer(Modifier.height(10.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .clip(PillShape)
                .border(1.2.dp, Crema.copy(alpha = 0.6f), PillShape)
                .clickable { picking = true }
                .padding(horizontal = 20.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(since.pretty(), style = AptText.mono.copy(fontSize = 18.sp), color = Crema)
        }
        Spacer(Modifier.height(32.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            NameField("Tú", me, { me = it }, Modifier.weight(1f))
            NameField("Tu pareja", partner, { partner = it }, Modifier.weight(1f))
        }
        Spacer(Modifier.height(48.dp))
        OutlinePill(
            "Entrar",
            { onDone(since, me.trim().ifEmpty { "Yo" }, partner.trim().ifEmpty { "Mi amor" }) },
            Modifier.fillMaxWidth(),
            color = Crema,
        )
        Spacer(Modifier.width(1.dp))
    }

    if (picking) {
        SinceDatePicker(since, onDismiss = { picking = false }, onPick = { since = it; picking = false })
    }
}

@Composable
private fun NameField(label: String, value: String, onChange: (String) -> Unit, modifier: Modifier) {
    Column(modifier) {
        Text(label.uppercase(), style = AptText.etiqueta, color = OroRosa)
        Spacer(Modifier.height(8.dp))
        BasicTextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            textStyle = AptText.cuerpo.copy(color = Crema, textAlign = TextAlign.Center),
            cursorBrush = SolidColor(OroRosa),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = label }
                .clip(PillShape)
                .border(1.2.dp, Crema.copy(alpha = 0.6f), PillShape)
                .background(Color.Transparent)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        )
    }
}

// Vistas previas para el panel Split/Design, una por paso.
@Preview(name = "1 · Bienvenida", device = PreviewPhone, showSystemUi = true)
@Composable
private fun WelcomePreview() = AptTheme { LinkScreen(AptViewModel()) }

@Preview(name = "2 · Código", device = PreviewPhone, showSystemUi = true)
@Composable
private fun CodePreview() = AptTheme { LinkScreen(AptViewModel(), initialStep = 1) }

@Preview(name = "3 · Juntos desde", device = PreviewPhone, showSystemUi = true)
@Composable
private fun SincePreview() = AptTheme { LinkScreen(AptViewModel(), initialStep = 2) }
