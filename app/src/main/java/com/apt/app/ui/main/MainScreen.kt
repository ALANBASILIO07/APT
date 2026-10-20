package com.apt.app.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apt.app.data.AptViewModel
import com.apt.app.ui.collage.CollageScreen
import com.apt.app.ui.components.Dot
import com.apt.app.ui.components.Monogram
import com.apt.app.ui.components.SectionLabel
import com.apt.app.ui.components.formatDays
import com.apt.app.ui.corner.CornerScreen
import com.apt.app.ui.map.MapScreen
import com.apt.app.ui.memories.MemoriesScreen
import com.apt.app.ui.theme.AptText
import com.apt.app.ui.theme.AvisoSinLeer
import com.apt.app.ui.theme.Ciruela
import com.apt.app.ui.theme.Crema
import com.apt.app.ui.theme.Linea
import com.apt.app.ui.theme.SystemBarIcons
import com.apt.app.ui.theme.Tinta
import com.apt.app.ui.theme.TintaSuave
import com.apt.app.ui.us.UsScreen

enum class Tab(val label: String) { Mapa("Mapa"), Recuerdos("Recuerdos"), Collage("Collage"), Rincon("Rincón"), Nosotros("Nosotros") }

@Composable
fun MainScreen(vm: AptViewModel, initialTab: Tab = Tab.Mapa) {
    SystemBarIcons(darkIcons = true)
    var tab by rememberSaveable { mutableStateOf(initialTab) }
    var showNotices by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        containerColor = Crema,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { TopBar(vm, onBell = { showNotices = true }) },
        bottomBar = { BottomTabs(tab) { tab = it } },
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (tab) {
                Tab.Mapa -> MapScreen(vm)
                Tab.Recuerdos -> MemoriesScreen(vm)
                Tab.Collage -> CollageScreen(vm)
                Tab.Rincon -> CornerScreen(vm)
                Tab.Nosotros -> UsScreen(vm)
            }
        }
    }

    if (showNotices) {
        NoticesSheet(vm, onDismiss = { showNotices = false; vm.markNoticesRead() })
    }
}

@Composable
private fun TopBar(vm: AptViewModel, onBell: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Crema)
            .statusBarsPadding()
            .padding(start = 20.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Monogram(Ciruela, size = 22.dp)
        Spacer(Modifier.width(10.dp))
        Text("APT", style = AptText.marca, color = Tinta)
        Spacer(Modifier.weight(1f))
        Text("${formatDays(vm.daysTogether)} DÍAS JUNTOS", style = AptText.mono.copy(fontSize = 11.sp), color = TintaSuave)
        val unread = vm.unreadNotices
        IconButton(
            onClick = onBell,
            modifier = Modifier.semantics {
                contentDescription = if (unread > 0) "Avisos, $unread sin leer" else "Avisos"
            },
        ) {
            Box {
                Icon(Icons.Outlined.Notifications, contentDescription = null, tint = Tinta)
                if (unread > 0) {
                    Box(
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(start = 10.dp)
                            .size(15.dp)
                            .clip(CircleShape)
                            .background(AvisoSinLeer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("$unread", style = AptText.mono.copy(fontSize = 9.sp, letterSpacing = 0.sp), color = Crema)
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomTabs(selected: Tab, onSelect: (Tab) -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(Crema)
            .navigationBarsPadding()
    ) {
        HorizontalDivider(color = Linea)
        Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Tab.entries.forEach { t ->
                val active = t == selected
                Column(
                    Modifier
                        .weight(1f)
                        .selectable(selected = active, role = Role.Tab) { onSelect(t) }
                        .padding(top = 8.dp, bottom = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        Modifier
                            .width(16.dp)
                            .height(2.dp)
                            .background(if (active) Ciruela else Crema)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        t.label.uppercase(),
                        style = AptText.etiqueta.copy(fontSize = 10.sp, letterSpacing = 1.sp),
                        color = if (active) Ciruela else TintaSuave,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoticesSheet(vm: AptViewModel, onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Crema,
        tonalElevation = 0.dp,
        modifier = Modifier.semantics { paneTitle = "Avisos" },
    ) {
        Column(Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
            SectionLabel("Avisos")
            Spacer(Modifier.height(12.dp))
            if (vm.notices.isEmpty()) {
                Text("Nada nuevo por ahora.", style = AptText.cuerpo, color = TintaSuave)
            }
            LazyColumn {
                items(vm.notices, key = { it.id }) { n ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { }
                            .padding(vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Dot(if (n.read) Linea else AvisoSinLeer)
                        Spacer(Modifier.width(12.dp))
                        Text(n.text, style = AptText.cuerpo, color = Tinta, modifier = Modifier.weight(1f))
                        Text(n.time.uppercase(), style = AptText.mono.copy(fontSize = 10.sp), color = TintaSuave)
                    }
                    HorizontalDivider(color = Linea)
                }
            }
        }
    }
}
