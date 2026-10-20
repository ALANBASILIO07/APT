package com.apt.app.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apt.app.data.AptViewModel
import com.apt.app.data.Destino
import com.apt.app.data.Notice
import com.apt.app.ui.collage.CollageScreen
import com.apt.app.ui.components.AptMark
import com.apt.app.ui.components.AptPhoto
import com.apt.app.ui.components.CardShape
import com.apt.app.ui.components.Divider
import com.apt.app.ui.components.HeartIcon
import com.apt.app.ui.components.PillShape
import com.apt.app.ui.corner.CornerScreen
import com.apt.app.ui.map.MapScreen
import com.apt.app.ui.memories.MemoriesScreen
import com.apt.app.ui.theme.AptText
import com.apt.app.ui.theme.Arcilla
import com.apt.app.ui.theme.Ciruela
import com.apt.app.ui.theme.Crema
import com.apt.app.ui.theme.Insignia
import com.apt.app.ui.theme.Linea
import com.apt.app.ui.theme.OroRosa
import com.apt.app.ui.theme.Superficie
import com.apt.app.ui.theme.SystemBarIcons
import com.apt.app.ui.theme.Tinta
import com.apt.app.ui.theme.TintaSuave
import com.apt.app.ui.us.UsScreen
import kotlinx.coroutines.delay

enum class Tab(val label: String) { Mapa("Mapa"), Recuerdos("Recuerdos"), Collage("Collage"), Rincon("Rincón"), Nosotros("Nosotros") }

private fun Destino.tab() = when (this) {
    Destino.Mapa -> Tab.Mapa
    Destino.Recuerdos -> Tab.Recuerdos
    Destino.Collage -> Tab.Collage
    Destino.Rincon -> Tab.Rincon
    Destino.Nosotros -> Tab.Nosotros
}

@Composable
fun MainScreen(vm: AptViewModel, initialTab: Tab = Tab.Mapa) {
    SystemBarIcons(darkIcons = true)
    var tab by rememberSaveable { mutableStateOf(initialTab) }
    var memPlace by rememberSaveable { mutableStateOf(vm.visited.lastOrNull()?.id ?: "") }
    var notifOpen by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = notifOpen) { notifOpen = false }

    Box(Modifier.fillMaxSize().background(Crema)) {
        Column(Modifier.fillMaxSize()) {
            TopBar(vm, notifOpen, onBell = { notifOpen = !notifOpen })
            Box(Modifier.weight(1f)) {
                when (tab) {
                    Tab.Mapa -> MapScreen(vm, onOpenMemories = { memPlace = it; tab = Tab.Recuerdos })
                    Tab.Recuerdos -> MemoriesScreen(vm, memPlace, onPlace = { memPlace = it }, onCollage = { tab = Tab.Collage })
                    Tab.Collage -> CollageScreen(vm)
                    Tab.Rincon -> CornerScreen(vm)
                    Tab.Nosotros -> UsScreen(vm)
                }
            }
            BottomTabs(tab) { tab = it; notifOpen = false }
        }

        if (notifOpen) {
            NoticesPanel(
                vm,
                onDismiss = { notifOpen = false },
                onOpen = { n ->
                    vm.readNotice(n.id)
                    notifOpen = false
                    tab = n.go.tab()
                },
            )
        }

        Toast(vm, Modifier.align(Alignment.TopCenter))
    }
}

@Composable
private fun TopBar(vm: AptViewModel, open: Boolean, onBell: () -> Unit) {
    Column(Modifier.background(Crema).statusBarsPadding()) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(start = 20.dp, end = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AptMark(Ciruela, Modifier.size(30.dp, 22.dp))
            Spacer(Modifier.width(10.dp))
            Text("APT", style = AptText.boton.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 3.9.sp), color = Tinta)
            Spacer(Modifier.weight(1f))
            Text("${vm.daysLabel} DÍAS JUNTOS", style = AptText.mono, color = TintaSuave)
            val unread = vm.unreadNotices
            Box(
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = onBell)
                    .semantics {
                        contentDescription = if (unread > 0) "Notificaciones, $unread sin leer" else "Notificaciones"
                        if (open) paneTitle = "Notificaciones abiertas"
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Notifications, contentDescription = null, tint = Tinta, modifier = Modifier.size(22.dp))
                if (unread > 0) {
                    Box(
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 5.dp, end = 5.dp)
                            .size(16.dp)
                            .clip(PillShape)
                            .background(Insignia),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("$unread", style = AptText.boton.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold), color = Color.White)
                    }
                }
            }
        }
        Divider()
    }
}

@Composable
private fun BottomTabs(selected: Tab, onSelect: (Tab) -> Unit) {
    Column(Modifier.fillMaxWidth().background(Crema).navigationBarsPadding()) {
        Divider()
        Row(Modifier.fillMaxWidth().height(58.dp).padding(horizontal = 6.dp)) {
            Tab.entries.forEach { t ->
                val active = t == selected
                Column(
                    Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .selectable(selected = active, role = Role.Tab) { onSelect(t) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Box(
                        Modifier
                            .width(if (active) 16.dp else 0.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Ciruela)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        t.label.uppercase(),
                        style = AptText.boton.copy(fontSize = 11.sp, letterSpacing = 1.1.sp),
                        color = if (active) Ciruela else TintaSuave,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
private fun NoticesPanel(vm: AptViewModel, onDismiss: () -> Unit, onOpen: (Notice) -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0x47140C16))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onDismiss)
    )
    Column(
        Modifier
            .statusBarsPadding()
            .padding(top = 57.dp, start = 12.dp, end = 12.dp)
            .fillMaxWidth()
            .clip(CardShape)
            .background(Crema)
            .border(1.dp, Linea, CardShape)
            .semantics { paneTitle = "Notificaciones" }
            .verticalScroll(rememberScrollState()),
    ) {
        Row(Modifier.padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Notificaciones", style = AptText.cuerpo.copy(fontSize = 16.sp, fontWeight = FontWeight.SemiBold), color = Tinta)
            Spacer(Modifier.weight(1f))
            Text(
                "Marcar como leídas",
                style = AptText.boton.copy(fontSize = 13.sp),
                color = Ciruela,
                modifier = Modifier.clickable(role = Role.Button) { vm.readAllNotices() }.padding(vertical = 8.dp),
            )
        }
        vm.notices.forEach { n ->
            Divider()
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(if (n.unread) Superficie else Color.Transparent)
                    .clickable { onOpen(n) }
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                if (n.img != null) {
                    AptPhoto(n.img, "", Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)))
                } else {
                    Box(
                        Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Ciruela),
                        contentAlignment = Alignment.Center,
                    ) { HeartIcon(OroRosa, 16.dp) }
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append(n.who) }
                            append(" ")
                            append(n.text)
                        },
                        style = AptText.cuerpo.copy(fontSize = 14.sp, lineHeight = 19.6.sp),
                        color = Tinta,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(n.time, style = AptText.mono.copy(fontSize = 11.sp), color = TintaSuave)
                }
                Spacer(Modifier.width(12.dp))
                Box(
                    Modifier
                        .padding(top = 6.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (n.unread) Arcilla else Color.Transparent)
                )
            }
        }
    }
}

@Composable
private fun Toast(vm: AptViewModel, modifier: Modifier = Modifier) {
    val t = vm.toast
    LaunchedEffect(t) {
        if (t != null) {
            delay(2600)
            vm.clearToast(t)
        }
    }
    AnimatedVisibility(t != null, modifier.statusBarsPadding().padding(top = 63.dp), enter = fadeIn(), exit = fadeOut()) {
        var last by remember { mutableStateOf("") }
        if (t != null) last = t.msg
        Row(
            Modifier
                .widthIn(max = 320.dp)
                .clip(PillShape)
                .background(Tinta)
                .padding(horizontal = 18.dp, vertical = 11.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HeartIcon(OroRosa, 14.dp)
            Spacer(Modifier.width(8.dp))
            Text(last, style = AptText.cuerpo.copy(fontSize = 14.sp), color = Crema, maxLines = 1)
        }
    }
}
