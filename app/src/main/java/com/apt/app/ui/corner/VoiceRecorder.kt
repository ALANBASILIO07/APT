package com.apt.app.ui.corner

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.apt.app.ui.components.rememberReduceMotion
import com.apt.app.ui.theme.AptText
import com.apt.app.ui.theme.Arcilla
import com.apt.app.ui.theme.Ciruela
import com.apt.app.ui.theme.Crema
import com.apt.app.ui.theme.TintaSuave
import java.io.File

/** Graba la voz en un archivo .m4a dentro de la app. */
class VoiceRecorder(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var file: File? = null

    fun start() {
        val f = File(context.filesDir, "voz_${System.currentTimeMillis()}.m4a")
        @Suppress("DEPRECATION")
        val r = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) MediaRecorder(context) else MediaRecorder()
        r.setAudioSource(MediaRecorder.AudioSource.MIC)
        r.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        r.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        r.setAudioEncodingBitRate(96_000)
        r.setAudioSamplingRate(44_100)
        r.setOutputFile(f.absolutePath)
        r.prepare()
        r.start()
        recorder = r
        file = f
    }

    /** Devuelve la ruta del archivo grabado, o null si algo falló. */
    fun stop(): String? {
        val r = recorder ?: return null
        val path = runCatching { r.stop(); file?.absolutePath }.getOrNull()
        r.release()
        recorder = null
        return path
    }
}

/** Reproduce un archivo local; un solo reproductor a la vez. */
object VoicePlayer {
    private var player: MediaPlayer? = null
    var playingPath by mutableStateOf<String?>(null)
        private set

    fun toggle(path: String) {
        if (playingPath == path) { stop(); return }
        stop()
        runCatching {
            player = MediaPlayer().apply {
                setDataSource(path)
                setOnCompletionListener { VoicePlayer.stop() }
                prepare()
                start()
            }
            playingPath = path
        }
    }

    fun stop() {
        player?.release()
        player = null
        playingPath = null
    }
}

@Composable
fun VoiceRecorderButton(onRecorded: (String) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val recorder = remember { VoiceRecorder(context) }
    var recording by remember { mutableStateOf(false) }
    var lastPath by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) { onDispose { recorder.stop() } }

    fun begin() {
        runCatching { recorder.start(); recording = true }
    }

    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) begin()
    }

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(if (recording) Ciruela else Crema)
                .border(1.5.dp, Ciruela, CircleShape)
                .clickable(role = Role.Button) {
                    if (recording) {
                        recording = false
                        recorder.stop()?.let { lastPath = it; onRecorded(it) }
                    } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        begin()
                    } else {
                        permission.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
                .semantics { contentDescription = if (recording) "Detener grabación" else "Grabar voz" },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                if (recording) Icons.Outlined.Stop else Icons.Outlined.Mic,
                contentDescription = null,
                tint = if (recording) Crema else Ciruela,
                modifier = Modifier.size(30.dp),
            )
        }
        Spacer(Modifier.height(10.dp))
        if (recording) {
            AudioWaves()
        } else {
            Text(
                if (lastPath != null) "Grabación lista" else "Toca para grabar",
                style = AptText.pequeno,
                color = TintaSuave,
            )
        }
    }
}

/** Ondas de audio mientras graba; quietas si el teléfono reduce animaciones. */
@Composable
fun AudioWaves(modifier: Modifier = Modifier) {
    val reduceMotion = rememberReduceMotion()
    val t = rememberInfiniteTransition(label = "waves")
    Row(modifier.height(20.dp), horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(9) { i ->
            val h by t.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(420 + i * 70), RepeatMode.Reverse),
                label = "w$i",
            )
            Box(
                Modifier
                    .width(3.dp)
                    .fillMaxHeight(if (reduceMotion) 0.6f else h)
                    .clip(CircleShape)
                    .background(Arcilla)
            )
        }
    }
}
