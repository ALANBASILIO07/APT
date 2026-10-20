package com.apt.app.data

import java.time.LocalDate

/** Contenido de ejemplo para el primer arranque, igual que en el prototipo. */
object SampleData {
    private const val U = "https://images.unsplash.com/"
    const val COVER = U + "photo-1494774157365-9e04c6720e47?w=1200&q=80"

    val places = listOf(
        Place(1, "Tulum", "Quintana Roo", 1580, 0.82f, 0.42f, PlaceStatus.Visitado, "Atardecer frente a las ruinas"),
        Place(2, "Oaxaca", "Oaxaca", 460, 0.48f, 0.70f, PlaceStatus.PorVisitar, "Mezcal y Guelaguetza"),
        Place(3, "San Miguel de Allende", "Guanajuato", 270, 0.38f, 0.40f, PlaceStatus.Visitado, "Callejones y globos"),
        Place(4, "Bacalar", "Quintana Roo", 1350, 0.78f, 0.58f, PlaceStatus.PorVisitar, "Laguna de los siete colores"),
        Place(5, "Valle de Guadalupe", "Baja California", 2700, 0.10f, 0.18f, PlaceStatus.PorVisitar, "Ruta del vino"),
    )

    val memories = listOf(
        Memory(1, "Nuestro primer atardecer", LocalDate.of(2023, 3, 4), "Tulum", MemoryKind.Foto,
            U + "photo-1507525428034-b723cf961d3e?w=900&q=80"),
        Memory(2, "Café en la terraza", LocalDate.of(2023, 8, 19), "San Miguel de Allende", MemoryKind.Foto,
            U + "photo-1495474472287-4d71bcdd2085?w=900&q=80"),
        Memory(3, "Lo que me dijiste esa noche", LocalDate.of(2024, 2, 14), "Casa", MemoryKind.Nota,
            text = "Que contigo hasta lo cotidiano se siente como viaje."),
        Memory(4, "Montaña al amanecer", LocalDate.of(2024, 11, 2), "Nevado de Toluca", MemoryKind.Foto,
            U + "photo-1506905925346-21bda4d32df4?w=900&q=80"),
        Memory(5, "Bosque", LocalDate.of(2025, 5, 10), "Valle de Bravo", MemoryKind.Foto,
            U + "photo-1469474968028-56623f02e42e?w=900&q=80"),
        Memory(6, "Playa sin nombre", LocalDate.of(2025, 9, 21), "Mazunte", MemoryKind.Foto,
            U + "photo-1500530855697-b586d89ba3ee?w=900&q=80"),
    )

    val corner = listOf(
        CornerItem(1, CornerKind.Poema, "Paulina",
            "Si el mundo se apaga,\nque sea contigo,\nmirando el mismo cielo.", date = LocalDate.of(2025, 10, 2)),
        CornerItem(2, CornerKind.Nota, "Alan", "Te dejé algo en la bolsa de tu abrigo.", date = LocalDate.of(2025, 10, 12)),
        CornerItem(3, CornerKind.Musica, "Paulina", "La Llorona", "Chavela Vargas", LocalDate.of(2025, 9, 30)),
        CornerItem(4, CornerKind.Musica, "Alan", "Bésame Mucho", "Consuelo Velázquez", LocalDate.of(2025, 9, 1)),
        CornerItem(5, CornerKind.Idea, "Alan", "Picnic en el Desierto de los Leones"),
        CornerItem(6, CornerKind.Idea, "Paulina", "Clase de cerámica juntos", done = true),
        CornerItem(7, CornerKind.Idea, "Paulina", "Ver la lluvia de estrellas en agosto"),
    )

    val notices = listOf(
        Notice(1, "Paulina te dejó un poema en Rincón", "hace 5 min"),
        Notice(2, "Hoy hace un año: Montaña al amanecer", "hoy"),
        Notice(3, "Paulina agregó Bacalar a lugares por visitar", "ayer"),
    )
}
