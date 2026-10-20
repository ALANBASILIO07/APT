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
import com.apt.app.data.AptViewModel
import com.apt.app.ui.components.Monogram
import com.apt.app.ui.components.OroRosaBrush
import com.apt.app.ui.link.LinkScreen
import com.apt.app.ui.main.MainScreen
import com.apt.app.ui.main.Tab
import com.apt.app.ui.theme.AptText
import com.apt.app.ui.theme.AptTheme
import com.apt.app.ui.theme.Ciruela
import com.apt.app.ui.theme.Crema
import com.apt.app.ui.theme.OroRosa

// Vistas previas para el panel Split/Design de Android Studio.
// Usan datos de ejemplo en memoria; las fotos de internet no cargan en la vista previa.

private const val PHONE = "spec:width=393dp,height=852dp,dpi=420"

@Preview(name = "Logo", widthDp = 360, heightDp = 160)
@Composable
private fun LogoPreview() = AptTheme {
    Row(
        Modifier.background(Ciruela).padding(28.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Monogram(OroRosa, size = 72.dp, brush = OroRosaBrush)
        Spacer(Modifier.width(20.dp))
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("APT", style = AptText.marca.copy(fontSize = 20.sp, letterSpacing = 8.sp), color = Crema)
            Text("para dos", style = AptText.script.copy(fontSize = 30.sp), color = OroRosa)
        }
    }
}

@Preview(name = "1 · Bienvenida", device = PHONE, showSystemUi = true)
@Composable
private fun WelcomePreview() = AptTheme { LinkScreen(AptViewModel()) }

@Preview(name = "2 · Código", device = PHONE, showSystemUi = true)
@Composable
private fun CodePreview() = AptTheme { LinkScreen(AptViewModel(), initialStep = 1) }

@Preview(name = "3 · Juntos desde", device = PHONE, showSystemUi = true)
@Composable
private fun SincePreview() = AptTheme { LinkScreen(AptViewModel(), initialStep = 2) }

@Preview(name = "Mapa", device = PHONE, showSystemUi = true)
@Composable
private fun MapPreview() = AptTheme { MainScreen(AptViewModel(linked = true), Tab.Mapa) }

@Preview(name = "Recuerdos", device = PHONE, showSystemUi = true)
@Composable
private fun MemoriesPreview() = AptTheme { MainScreen(AptViewModel(linked = true), Tab.Recuerdos) }

@Preview(name = "Collage", device = PHONE, showSystemUi = true)
@Composable
private fun CollagePreview() = AptTheme { MainScreen(AptViewModel(linked = true), Tab.Collage) }

@Preview(name = "Rincón", device = PHONE, showSystemUi = true)
@Composable
private fun CornerPreview() = AptTheme { MainScreen(AptViewModel(linked = true), Tab.Rincon) }

@Preview(name = "Nosotros", device = PHONE, showSystemUi = true)
@Composable
private fun UsPreview() = AptTheme { MainScreen(AptViewModel(linked = true), Tab.Nosotros) }
