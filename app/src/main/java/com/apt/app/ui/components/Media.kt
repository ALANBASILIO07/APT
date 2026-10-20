package com.apt.app.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.core.content.ContextCompat
import com.apt.app.ui.theme.Ciruela
import com.apt.app.ui.theme.Crema
import com.apt.app.ui.theme.TintaSuave
import kotlinx.coroutines.delay
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

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

/** Grabación con permiso de micrófono y contador de segundos. */
class Recording internal constructor(
    private val start: () -> Unit,
    private val stop: () -> String?,
) {
    var active by mutableStateOf(false)
        internal set
    var secs by mutableIntStateOf(0)
        internal set

    fun begin() = start()

    /** Termina y devuelve la ruta del audio (null en vistas previas o si falló). */
    fun end(): String? = stop()
}

@Composable
fun rememberRecording(): Recording {
    val context = LocalContext.current
    val preview = LocalInspectionMode.current
    val recorder = remember { VoiceRecorder(context) }
    lateinit var recording: Recording

    fun startNow() {
        if (preview) { recording.active = true; recording.secs = 0; return }
        runCatching { recorder.start(); recording.secs = 0; recording.active = true }
    }

    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) startNow()
    }

    recording = remember {
        Recording(
            start = {
                if (preview || ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) startNow()
                else permission.launch(Manifest.permission.RECORD_AUDIO)
            },
            stop = {
                recording.active = false
                if (preview) null else recorder.stop()
            },
        )
    }

    LaunchedEffect(recording.active) {
        while (recording.active) {
            delay(1000)
            recording.secs++
        }
    }
    DisposableEffect(Unit) { onDispose { recorder.stop() } }
    return recording
}

/** Segundos transcurridos mientras running sea verdadero; reinicia con resetKey. */
@Composable
fun rememberElapsed(running: Boolean, limit: Int, resetKey: Any?, onEnd: () -> Unit = {}): Int {
    var el by remember(resetKey) { mutableIntStateOf(0) }
    LaunchedEffect(running, resetKey) {
        if (running && el >= limit) el = 0
        while (running) {
            delay(1000)
            if (el + 1 >= limit) { el = limit; onEnd(); break }
            el++
        }
    }
    return el
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
