package com.apt.app.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.roundToInt

private val esMX = Locale("es", "MX")

/** Sin store (vistas previas) todo vive en memoria con los datos de ejemplo. */
class AptViewModel(private val store: AptStore? = null, linked: Boolean = false) : ViewModel() {

    var profile by mutableStateOf(store?.loadProfile() ?: Profile(linked = linked, myCode = "AP-2814", partnerCode = "AP-0525"))
        private set

    val places = mutableStateListOf<Place>().apply { addAll(store?.loadPlaces() ?: SampleData.places) }
    val memories = mutableStateListOf<Memory>().apply { addAll(store?.loadMemories() ?: SampleData.memories) }
    val poems = mutableStateListOf<Poem>().apply { addAll(store?.loadPoems() ?: SampleData.poems) }
    val ideas = mutableStateListOf<Idea>().apply { addAll(store?.loadIdeas() ?: SampleData.ideas) }
    val notices = mutableStateListOf<Notice>().apply { addAll(store?.loadNotices() ?: SampleData.notices) }
    val songs = SampleData.songs

    /** Mensaje breve que aparece arriba de la pantalla y se va solo. */
    data class Toast(val id: Long, val msg: String)

    var toast by mutableStateOf<Toast?>(null)
        private set

    fun showToast(msg: String) {
        toast = Toast(System.nanoTime(), msg)
    }

    fun clearToast(t: Toast) {
        if (toast == t) toast = null
    }

    private fun newId() = System.currentTimeMillis()

    fun updateProfile(transform: (Profile) -> Profile) {
        profile = transform(profile)
        store?.saveProfile(profile)
    }

    // Fechas

    val daysTogether: Long
        get() = ChronoUnit.DAYS.between(profile.since, LocalDate.now()).coerceAtLeast(0)

    val daysLabel: String
        get() = NumberFormat.getIntegerInstance(esMX).format(daysTogether)

    /** "Cumplen 32 meses juntos en 12 días" o "Su aniversario de 3 años es en 12 días". */
    val anniversaryText: String
        get() {
            val since = profile.since
            val today = LocalDate.now()
            fun onDay(d: LocalDate) = d.withDayOfMonth(minOf(since.dayOfMonth, d.lengthOfMonth()))
            var next = onDay(today)
            if (!next.isAfter(today)) next = onDay(today.plusMonths(1))
            val toNext = ChronoUnit.DAYS.between(today, next)
            val months = (next.year - since.year) * 12 + next.monthValue - since.monthValue
            return if (months > 0 && months % 12 == 0) "Su aniversario de ${months / 12} años es en $toNext días"
            else "Cumplen $months meses juntos en $toNext días"
        }

    var showAlert by mutableStateOf(true)

    val unreadNotices: Int
        get() = notices.count { it.unread }

    init {
        if (profile.myCode.isEmpty()) {
            val code = "AP-" + (1000..9999).random()
            updateProfile { it.copy(myCode = code) }
        }
    }

    // Vinculación

    fun link(partnerCode: String, since: LocalDate) = updateProfile {
        it.copy(linked = true, partnerCode = partnerCode.uppercase(), since = since)
    }

    fun unlink() = updateProfile { it.copy(linked = false, partnerCode = "") }

    // Mapa

    val visited: List<Place> get() = places.filter { it.visited }
    val wishes: List<Place> get() = places.filter { !it.visited }

    val nextPlace: Place?
        get() = places.firstOrNull { it.id == profile.nextPlaceId && !it.visited } ?: wishes.firstOrNull()

    /** Distancia aproximada desde el último lugar visitado, como en el prototipo. */
    val nextKmLabel: String
        get() {
            val last = visited.lastOrNull() ?: return ""
            val next = nextPlace ?: return ""
            val km = (hypot((next.lon - last.lon) * cos((next.lat + last.lat) / 2 * PI / 180), next.lat - last.lat) * 111).roundToInt()
            return "≈ ${NumberFormat.getIntegerInstance(Locale("es")).format(km)} KM"
        }

    fun setNext(id: String) = updateProfile { it.copy(nextPlaceId = id) }

    fun addPlace(p: Place) {
        if (places.any { it.id == p.id }) return
        places.add(p)
        store?.savePlaces(places)
        showToast("${p.name} agregado al mapa")
    }

    fun markVisited(id: String) {
        val p = places.firstOrNull { it.id == id } ?: return
        val month = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM yyyy", esMX)).uppercase(esMX).replace(".", "")
        places.remove(p)
        places.add(p.copy(visited = true, date = month))
        store?.savePlaces(places)
        if (profile.nextPlaceId == id) updateProfile { it.copy(nextPlaceId = wishes.firstOrNull()?.id) }
        showToast("¡Visitaron ${p.name}!")
    }

    // Recuerdos

    fun memoriesOf(placeId: String) = memories.filter { it.placeId == placeId }

    fun addMemories(items: List<Memory>) {
        memories.addAll(items)
        store?.saveMemories(memories)
        val place = places.firstOrNull { it.id == items.firstOrNull()?.placeId }?.name.orEmpty()
        showToast(if (items.size > 1) "${items.size} recuerdos en $place" else "Recuerdo en $place")
    }

    fun newMemoryId() = newId()

    // Rincón

    fun addPoem(title: String, text: String) {
        poems.add(Poem(newId(), title, text, profile.me, LocalDate.now()))
        store?.savePoems(poems)
        showToast("Poema guardado")
    }

    fun addRecitation(poemId: Long, secs: Int, uri: String?) {
        val i = poems.indexOfFirst { it.id == poemId }.takeIf { it >= 0 } ?: return
        poems[i] = poems[i].copy(audios = poems[i].audios + Recitation(profile.me, secs.coerceAtLeast(1), uri))
        store?.savePoems(poems)
        showToast("Tu voz quedó guardada")
    }

    fun toggleIdea(id: Long) {
        val i = ideas.indexOfFirst { it.id == id }.takeIf { it >= 0 } ?: return
        ideas[i] = ideas[i].copy(done = !ideas[i].done)
        store?.saveIdeas(ideas)
    }

    fun addIdea(text: String) {
        if (text.isBlank()) return
        ideas.add(Idea(newId(), text.trim(), profile.me.uppercase(esMX)))
        store?.saveIdeas(ideas)
    }

    // Avisos

    fun readNotice(id: Long) {
        val i = notices.indexOfFirst { it.id == id }.takeIf { it >= 0 } ?: return
        notices[i] = notices[i].copy(unread = false)
        store?.saveNotices(notices)
    }

    fun readAllNotices() {
        for (i in notices.indices) notices[i] = notices[i].copy(unread = false)
        store?.saveNotices(notices)
    }
}
