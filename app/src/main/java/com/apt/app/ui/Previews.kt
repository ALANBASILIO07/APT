package com.apt.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apt.app.ui.components.AptMark
import androidx.compose.foundation.layout.size
import com.apt.app.ui.theme.AptText
import com.apt.app.ui.theme.AptTheme
import com.apt.app.ui.theme.Ciruela
import com.apt.app.ui.theme.Crema
import com.apt.app.ui.theme.OroRosa

// Vistas previas para el panel Split/Design de Android Studio.
// Cada pantalla tiene la suya al final de su archivo (MapScreen.kt, CollageScreen.kt, ...),
// así puedes editar el código y ver el diseño lado a lado.

// Teléfono que usan todas las vistas previas.
const val PreviewPhone = "spec:width=393dp,height=852dp,dpi=420"

@Preview(name = "Logo", widthDp = 360, heightDp = 160)
@Composable
private fun LogoPreview() = AptTheme {
    Row(
        Modifier.background(Ciruela).padding(28.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AptMark(OroRosa, Modifier.size(100.dp, 72.dp))
        Spacer(Modifier.width(20.dp))
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("APT", style = AptText.boton.copy(fontSize = 22.sp, letterSpacing = 9.9.sp), color = Crema)
            Text("para dos", style = AptText.script.copy(fontSize = 30.sp), color = OroRosa)
        }
    }
}
