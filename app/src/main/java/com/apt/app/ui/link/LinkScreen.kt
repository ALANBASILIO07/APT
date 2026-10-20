package com.apt.app.ui.link

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apt.app.data.AptViewModel
import com.apt.app.ui.PreviewPhone
import com.apt.app.ui.components.AptMark
import com.apt.app.ui.components.Eyebrow
import com.apt.app.ui.components.FloatingHearts
import com.apt.app.ui.components.PillButton
import com.apt.app.ui.components.PillField
import com.apt.app.ui.components.PillValue
import com.apt.app.ui.components.SinceDatePicker
import com.apt.app.ui.components.prettyLower
import com.apt.app.ui.components.rememberReduceMotion
import com.apt.app.ui.theme.AptText
import com.apt.app.ui.theme.AptTheme
import com.apt.app.ui.theme.Ciruela
import com.apt.app.ui.theme.Crema
import com.apt.app.ui.theme.CremaSuave
import com.apt.app.ui.theme.OroRosa
import com.apt.app.ui.theme.SystemBarIcons
import java.text.NumberFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale

/** Bienvenida y vinculación: código de 4+ caracteres y fecha en que se hicieron novios. */
@Composable
fun LinkScreen(vm: AptViewModel, initialStep: Int = 0) {
    SystemBarIcons(darkIcons = false)
    var step by rememberSaveable { mutableIntStateOf(initialStep) }
    var code by rememberSaveable { mutableStateOf("") }
    var since by rememberSaveable { mutableStateOf(vm.profile.since.toString()) }
    BackHandler(enabled = step > 0) { step-- }

    val sinceDate = LocalDate.parse(since)
    val days = NumberFormat.getIntegerInstance(Locale("es", "MX"))
        .format(ChronoUnit.DAYS.between(sinceDate, LocalDate.now()).coerceAtLeast(0))

    Box(Modifier.fillMaxSize().background(Ciruela)) {
        Glow()
        FloatingHearts(OroRosa, Modifier.fillMaxSize(), count = 14)
        BoxWithConstraints(Modifier.fillMaxSize().safeDrawingPadding().imePadding()) {
        // Logo arriba y el paso abajo; si no cabe (teclado abierto), se desplaza.
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight)
                .padding(horizontal = 32.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(36.dp))
                AptMark(OroRosa, Modifier.size(150.dp, 108.dp), description = "Monograma A y P")
                Spacer(Modifier.height(28.dp))
                Text("APT", style = AptText.boton.copy(fontSize = 15.sp, letterSpacing = 7.5.sp), color = Crema, modifier = Modifier.padding(start = 7.5.dp))
                Text("para dos", style = AptText.script.copy(fontSize = 40.sp, lineHeight = 40.sp), color = OroRosa, modifier = Modifier.padding(top = 6.dp))
                Spacer(Modifier.height(40.dp))
            }
            AnimatedContent(
                targetState = step,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "paso",
            ) { s ->
                when (s) {
                    0 -> Intro(onLink = { step = 1 })
                    1 -> CodeStep(vm.profile.myCode, code, { code = it.uppercase() }, onNext = { step = 2 })
                    2 -> DateStep(sinceDate, days, { since = it.toString() }, onNext = { step = 3 })
                    else -> Done(days, sinceDate, onEnter = {
                        vm.link(code, sinceDate)
                        vm.showToast("Cuentas vinculadas")
                    })
                }
            }
        }
        }
    }
}

/** Halo suave de oro rosa detrás del logo. */
@Composable
private fun Glow() {
    val reduceMotion = rememberReduceMotion()
    val t = rememberInfiniteTransition(label = "halo")
    val pulse by t.animateFloat(0f, 1f, infiniteRepeatable(tween(2500), RepeatMode.Reverse), label = "pulso")
    val p = if (reduceMotion) 0.5f else pulse
    Box(Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.TopCenter) {
        Canvas(Modifier.size(320.dp).scale(0.92f + 0.16f * p)) {
            drawCircle(
                Brush.radialGradient(
                    listOf(OroRosa.copy(alpha = 0.18f + 0.14f * p), OroRosa.copy(alpha = 0f)),
                    center = Offset(size.width / 2, size.height / 2),
                    radius = size.minDimension / 2,
                )
            )
        }
    }
}

@Composable
private fun Intro(onLink: () -> Unit) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Un Rincón para Dos", style = AptText.cuerpo.copy(fontSize = 18.sp, lineHeight = 26.sp), color = Crema, modifier = Modifier.padding(bottom = 10.dp))
        PillButton("Vincular con mi pareja", onLink, Modifier.fillMaxWidth(), color = Crema, height = 52.dp, fontSize = 16.sp, uppercase = true)
        Text("Hecho por Alan & Paulina", style = AptText.pequeno, color = CremaSuave)
    }
}

@Composable
private fun StepLabel(text: String) {
    Text(text, style = AptText.mono.copy(fontSize = 11.sp, letterSpacing = 0.sp), color = CremaSuave)
}

@Composable
private fun CodeStep(myCode: String, code: String, onCode: (String) -> Unit, onNext: () -> Unit) {
    val valid = code.trim().length >= 4
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        StepLabel("PASO 1 DE 2")
        Eyebrow("Tu código", color = CremaSuave)
        Text(
            myCode,
            style = AptText.mono.copy(fontSize = 28.sp, letterSpacing = 2.24.sp),
            color = Crema,
            modifier = Modifier.semantics { contentDescription = "Tu código: ${myCode.toList().joinToString(" ")}" },
        )
        Eyebrow("Código de tu pareja", Modifier.padding(top = 12.dp), color = CremaSuave)
        PillField(
            code, onCode, label = "Código de tu pareja",
            modifier = Modifier.fillMaxWidth(),
            placeholder = "AP-0000",
            textStyle = AptText.mono.copy(fontSize = 18.sp, letterSpacing = 0.sp),
            height = 52.dp,
            borderColor = CremaSuave,
            ink = Crema,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
        )
        PillButton("Continuar", onNext, Modifier.fillMaxWidth().padding(top = 8.dp), color = Crema, height = 52.dp, fontSize = 16.sp, enabled = valid)
    }
}

@Composable
private fun DateStep(since: LocalDate, days: String, onSince: (LocalDate) -> Unit, onNext: () -> Unit) {
    var picking by rememberSaveable { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        StepLabel("PASO 2 DE 2")
        Text(
            "¿Desde cuándo son novios?",
            style = AptText.cuerpo.copy(fontSize = 22.sp, lineHeight = 27.sp, fontWeight = FontWeight.Medium),
            color = Crema,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            "Con esta fecha contamos sus días juntos y les avisamos de cada aniversario.",
            style = AptText.cuerpo.copy(fontSize = 14.sp, lineHeight = 20.sp),
            color = CremaSuave,
        )
        PillValue(
            since.toString(), "Fecha en que se hicieron novios", { picking = true },
            textStyle = AptText.mono.copy(fontSize = 17.sp, letterSpacing = 0.sp),
            height = 52.dp, borderColor = CremaSuave, ink = Crema,
        )
        Row(Modifier.padding(top = 4.dp), verticalAlignment = Alignment.Bottom) {
            Text(days, style = AptText.cuerpo.copy(fontSize = 34.sp, lineHeight = 38.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.68).sp), color = Crema)
            Spacer(Modifier.width(8.dp))
            Text("días juntos", style = AptText.script.copy(fontSize = 26.sp), color = OroRosa)
        }
        PillButton("Vincular cuentas", onNext, Modifier.fillMaxWidth().padding(top = 4.dp), color = Crema, height = 52.dp, fontSize = 16.sp)
    }
    if (picking) {
        SinceDatePicker(since, onDismiss = { picking = false }, onPick = { onSince(it); picking = false })
    }
}

@Composable
private fun Done(days: String, since: LocalDate, onEnter: () -> Unit) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.semantics(mergeDescendants = true) {}) {
            Initial("A")
            Canvas(Modifier.width(40.dp).height(2.dp)) {
                drawLine(
                    Crema, Offset(0f, size.height / 2), Offset(size.width, size.height / 2),
                    strokeWidth = 1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)),
                )
            }
            Initial("P")
        }
        Text("Ya están conectados", style = AptText.cuerpo.copy(fontSize = 22.sp, fontWeight = FontWeight.Medium), color = Crema)
        Text(
            "$days días juntos desde el ${since.prettyLower()}.",
            style = AptText.cuerpo,
            color = CremaSuave,
            textAlign = TextAlign.Center,
        )
        PillButton("Entrar", onEnter, Modifier.fillMaxWidth(), color = Crema, height = 52.dp, fontSize = 16.sp)
    }
}

@Composable
private fun Initial(letter: String) {
    Box(Modifier.size(64.dp).border(1.5.dp, Crema, CircleShape), contentAlignment = Alignment.Center) {
        Text(letter, style = AptText.cuerpo.copy(fontSize = 24.sp, fontWeight = FontWeight.Medium), color = Crema)
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

@Preview(name = "4 · Conectados", device = PreviewPhone, showSystemUi = true)
@Composable
private fun DonePreview() = AptTheme { LinkScreen(AptViewModel(), initialStep = 3) }
