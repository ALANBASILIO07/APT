package com.apt.app.data

import java.time.LocalDate

/** Contenido de ejemplo para el primer arranque, igual que en el prototipo APT Concepto. */
object SampleData {
    private fun u(id: String) = "https://images.unsplash.com/photo-$id?w=700&q=70&auto=format&fit=crop"

    val SUNSET = u("1519307212971-dd9561667ffb")
    val DANCE = u("1615966650071-855b15f29ad1")
    val CARRY = u("1513744985676-c7e80ee4d05e")
    val HANDS = u("1497414826197-4166f61a8e14")
    val SIT = u("1701401942416-bea590efe2ad")
    val SEA = u("1513521523607-ba30a1159755")
    val WATER = u("1563196638-8c9457546e5f")
    val WALK = u("1575388104683-e076ee9ccaa0")
    val HUG = u("1617376431454-8195cf1fd668")
    val KISS = u("1489094889106-39069373d6ef")
    val SHORE = u("1508978053361-f0df91897005")
    val BEACH = u("1670312770826-4549a18aec92")

    /** Fotos de ejemplo para rellenar el collage. */
    val ROLL = listOf(SUNSET, DANCE, CARRY, HANDS, SIT, SEA, WATER, WALK, HUG, KISS, SHORE, BEACH)

    const val COVER = "https://images.unsplash.com/photo-1497414826197-4166f61a8e14?w=800&q=70&auto=format&fit=crop"

    val places = listOf(
        Place("cdmx", "Ciudad de México", -99.1, 19.4, visited = true, date = "JUN 2023"),
        Place("oax", "Oaxaca", -96.7, 17.1, visited = true, date = "DIC 2023"),
        Place("tulum", "Tulum", -87.5, 20.2, visited = true, date = "ABR 2024"),
        Place("lis", "Lisboa", -9.1, 38.7),
        Place("par", "París", 2.35, 48.86),
        Place("kyo", "Kioto", 135.8, 35.0),
        Place("bue", "Buenos Aires", -58.4, -34.6),
    )

    /** Sugerencias para agregar al mapa. */
    val suggestions = listOf(
        Place("rom", "Roma", 12.5, 41.9),
        Place("seu", "Seúl", 127.0, 37.5),
        Place("cus", "Cusco", -72.0, -13.5),
        Place("mar", "Marrakech", -8.0, 31.6),
    )

    private fun d(y: Int, m: Int, day: Int) = LocalDate.of(y, m, day)

    val memories = listOf(
        Memory(1, "tulum", MemoryKind.Foto, "atardecer en la playa", "Prometimos volver cada año.", "Paulina", d(2024, 4, 12), SIT),
        Memory(2, "tulum", MemoryKind.Video, "en el mar", "El agua más fría y tu risa más fuerte.", "Alan", d(2024, 4, 13), WATER, secs = 42),
        Memory(3, "tulum", MemoryKind.Foto, "caminata en la orilla", "Sin prisa, como siempre contigo.", "Alan", d(2024, 4, 14), WALK),
        Memory(4, "tulum", MemoryKind.Audio, "olas", "Grabé las olas para dormir contigo.", "Paulina", d(2024, 4, 14), secs = 18),
        Memory(5, "oax", MemoryKind.Foto, "bailando", "Bailaste con toda la calle.", "Paulina", d(2023, 12, 27), DANCE),
        Memory(6, "oax", MemoryKind.Video, "atardecer", "El cielo se puso de nuestro color.", "Alan", d(2023, 12, 28), HANDS, secs = 75),
        Memory(7, "cdmx", MemoryKind.Foto, "primer abrazo", "Aquí empezó todo.", "Paulina", d(2023, 6, 14), HUG),
        Memory(8, "cdmx", MemoryKind.Nota, "Helado, lluvia y tú.", "Lo escribí en una servilleta.", "Alan", d(2023, 6, 18)),
        Memory(9, "cdmx", MemoryKind.Foto, "mirando el mar", "El primer viaje del resto.", "Alan", d(2023, 6, 20), SEA),
    )

    val poems = listOf(
        Poem(
            1, "Antes de ti",
            "Antes de ti los mapas eran papel,\nahora cada punto tiene tu voz\ny cada camino termina\nen el mismo lugar: nosotros.",
            "Alan", d(2024, 2, 14), listOf(Recitation("Alan", 34)),
        ),
        Poem(
            2, "Equipaje",
            "Llevo poco:\ntu risa en el bolsillo,\nuna foto que no enseño\ny las ganas de llegar contigo.",
            "Paulina", d(2024, 3, 2), listOf(Recitation("Paulina", 22), Recitation("Alan", 25)),
        ),
        Poem(
            3, "Domingo",
            "No hace falta ir lejos,\nbasta tu mano en la mía\npara que la casa\nse vuelva viaje.",
            "Paulina", d(2024, 6, 9),
        ),
    )

    val songs = listOf(
        Song("La Vie en Rose", "Édith Piaf", "de Pau"),
        Song("Sabor a Mí", "Eydie Gormé y Los Panchos", "de Alan"),
        Song("Coming Home", "Leon Bridges", "de Pau"),
        Song("Quizás, Quizás, Quizás", "Nat King Cole", "de Alan"),
    )

    val ideas = listOf(
        Idea(1, "Cena sorpresa en la azotea", "ALAN"),
        Idea(2, "Aprender a bailar salsa", "PAU"),
        Idea(3, "Ahorrar para Kioto", "PAU", done = true),
        Idea(4, "Maratón de películas de Ghibli", "ALAN"),
    )

    val notices = listOf(
        Notice(1, "Paulina", "subió 3 fotos a Tulum.", "HACE 5 MIN", WALK, Destino.Recuerdos),
        Notice(2, "Alan", "grabó su voz leyendo “Equipaje”.", "HACE 1 H", null, Destino.Rincon),
        Notice(3, "APT", "Hace dos años estaban en Tulum. ¿Lo reviven?", "AYER", SIT, Destino.Recuerdos),
        Notice(4, "Paulina", "agregó Kioto a lugares por visitar.", "LUN", null, Destino.Mapa, unread = false),
    )
}
