package com.apt.app.data

import java.time.LocalDate

data class Profile(
    val linked: Boolean = false,
    val myCode: String = "",
    val partnerCode: String = "",
    val since: LocalDate = LocalDate.of(2023, 2, 14),
    val me: String = "Alan",
    val partner: String = "Paulina",
    val nickForPartner: String = "mi luna",
    val nickForMe: String = "osito",
    val avisoNotas: Boolean = true,
    val avisoRecuerdos: Boolean = true,
    val avisoAniversarios: Boolean = true,
    val coverPhoto: String? = null,
)

enum class PlaceStatus { PorVisitar, Visitado }

/** x e y son posiciones relativas (0..1) dentro del mapa ilustrado. */
data class Place(
    val id: Long,
    val name: String,
    val region: String,
    val km: Int,
    val x: Float,
    val y: Float,
    val status: PlaceStatus,
    val note: String = "",
)

enum class MemoryKind(val label: String) { Foto("Foto"), Video("Video"), Audio("Audio"), Nota("Nota") }

data class Memory(
    val id: Long,
    val title: String,
    val date: LocalDate,
    val place: String,
    val kind: MemoryKind,
    val uri: String? = null,
    val text: String = "",
)

enum class CornerKind(val label: String) {
    Nota("Notas"), Poema("Poemas"), Musica("Música"), Idea("Ideas"), Voz("Voz")
}

data class CornerItem(
    val id: Long,
    val kind: CornerKind,
    val author: String,
    val text: String,
    val extra: String = "",
    val date: LocalDate = LocalDate.now(),
    val done: Boolean = false,
    val uri: String? = null,
)

data class Notice(
    val id: Long,
    val text: String,
    val time: String,
    val read: Boolean = false,
)
