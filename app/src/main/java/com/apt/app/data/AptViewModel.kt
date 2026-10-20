package com.apt.app.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** Sin store (vistas previas) todo vive en memoria con los datos de ejemplo. */
class AptViewModel(private val store: AptStore? = null, linked: Boolean = false) : ViewModel() {

    var profile by mutableStateOf(store?.loadProfile() ?: Profile(linked = linked, myCode = "K7P2QX"))
        private set

    val places = mutableStateListOf<Place>().apply { addAll(store?.loadPlaces() ?: SampleData.places) }
    val memories = mutableStateListOf<Memory>().apply { addAll(store?.loadMemories() ?: SampleData.memories) }
    val corner = mutableStateListOf<CornerItem>().apply { addAll(store?.loadCorner() ?: SampleData.corner) }
    val notices = mutableStateListOf<Notice>().apply { addAll(store?.loadNotices() ?: SampleData.notices) }

    val daysTogether: Long
        get() = ChronoUnit.DAYS.between(profile.since, LocalDate.now()).coerceAtLeast(0)

    val unreadNotices: Int
        get() = notices.count { !it.read }

    private fun newId() = System.currentTimeMillis()

    fun updateProfile(transform: (Profile) -> Profile) {
        profile = transform(profile)
        store?.saveProfile(profile)
    }

    init {
        if (profile.myCode.isEmpty()) {
            val alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
            val code = (1..6).map { alphabet.random() }.joinToString("")
            updateProfile { it.copy(myCode = code) }
        }
    }

    fun link(partnerCode: String, since: LocalDate, me: String, partner: String) = updateProfile {
        it.copy(linked = true, partnerCode = partnerCode.uppercase(), since = since, me = me, partner = partner)
    }

    fun unlink() = updateProfile { it.copy(linked = false, partnerCode = "") }

    // Mapa
    fun addPlace(name: String, region: String, km: Int, x: Float, y: Float, note: String) {
        places.add(Place(newId(), name, region, km, x, y, PlaceStatus.PorVisitar, note))
        store?.savePlaces(places)
    }

    fun toggleVisited(id: Long) {
        val i = places.indexOfFirst { it.id == id }.takeIf { it >= 0 } ?: return
        val p = places[i]
        places[i] = p.copy(status = if (p.status == PlaceStatus.Visitado) PlaceStatus.PorVisitar else PlaceStatus.Visitado)
        store?.savePlaces(places)
    }

    fun removePlace(id: Long) {
        places.removeAll { it.id == id }
        store?.savePlaces(places)
    }

    // Recuerdos
    fun addMemory(title: String, place: String, date: LocalDate, kind: MemoryKind, uri: String?, text: String) {
        memories.add(0, Memory(newId(), title, date, place, kind, uri, text))
        store?.saveMemories(memories)
        notify("Guardaste un recuerdo: $title")
    }

    fun removeMemory(id: Long) {
        memories.removeAll { it.id == id }
        store?.saveMemories(memories)
    }

    // Rincón
    fun addCorner(kind: CornerKind, text: String, extra: String = "", uri: String? = null) {
        corner.add(0, CornerItem(newId(), kind, profile.me, text, extra, LocalDate.now(), uri = uri))
        store?.saveCorner(corner)
    }

    fun toggleIdea(id: Long) {
        val i = corner.indexOfFirst { it.id == id }.takeIf { it >= 0 } ?: return
        corner[i] = corner[i].copy(done = !corner[i].done)
        store?.saveCorner(corner)
    }

    fun removeCorner(id: Long) {
        corner.removeAll { it.id == id }
        store?.saveCorner(corner)
    }

    // Avisos
    fun markNoticesRead() {
        for (i in notices.indices) notices[i] = notices[i].copy(read = true)
        store?.saveNotices(notices)
    }

    private fun notify(text: String) {
        notices.add(0, Notice(newId(), text, "ahora"))
        store?.saveNotices(notices)
    }
}
