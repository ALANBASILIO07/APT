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
    val nextPlaceId: String? = "lis",
    val coverPhoto: String? = null,
    val avisoNuevos: Boolean = true,
    val avisoAniversarios: Boolean = true,
    val avisoVoz: Boolean = true,
    val avisoUnDiaComoHoy: Boolean = false,
)

/** Lugar en el mapa mundial; date es el mes en que lo visitaron, p. ej. "ABR 2024". */
data class Place(
    val id: String,
    val name: String,
    val lon: Double,
    val lat: Double,
    val visited: Boolean = false,
    val date: String = "",
)

enum class MemoryKind(val label: String) { Foto("FOTO"), Video("VIDEO"), Audio("AUDIO"), Nota("NOTA") }

/**
 * Un recuerdo de un lugar. Para fotos y videos uri es la imagen o el archivo;
 * para audio es la grabación; para notas, label es el texto de la nota.
 */
data class Memory(
    val id: Long,
    val placeId: String,
    val kind: MemoryKind,
    val label: String,
    val msg: String,
    val by: String,
    val date: LocalDate,
    val uri: String? = null,
    val secs: Int = 0,
)

/** Una lectura en voz alta de un poema; sin uri es de ejemplo. */
data class Recitation(val by: String, val secs: Int, val uri: String? = null)

data class Poem(
    val id: Long,
    val title: String,
    val text: String,
    val by: String,
    val date: LocalDate,
    val audios: List<Recitation> = emptyList(),
)

data class Song(val title: String, val artist: String, val by: String)

data class Idea(val id: Long, val text: String, val by: String, val done: Boolean = false)

enum class Destino { Mapa, Recuerdos, Collage, Rincon, Nosotros }

data class Notice(
    val id: Long,
    val who: String,
    val text: String,
    val time: String,
    val img: String? = null,
    val go: Destino = Destino.Recuerdos,
    val unread: Boolean = true,
)
