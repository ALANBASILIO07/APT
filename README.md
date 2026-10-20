# APT · para dos

App Android (Kotlin + Jetpack Compose) del concepto **APT Concepto**, sistema visual **Atardecer**.

## Abrir y correr
1. Android Studio → **Open** → esta carpeta (`APT`).
2. Espera el *Gradle Sync* (descarga Gradle 8.11.1 y las librerías la primera vez).
3. Elige un emulador o teléfono y presiona **Run ▶**.

## Qué hay
| Pantalla | Archivo | Qué hace |
|---|---|---|
| Vincular | `ui/link/LinkScreen.kt` | Bienvenida ciruela, tu código, código de tu pareja (4+ caracteres), fecha en que se hicieron novios |
| Mapa | `ui/map/MapScreen.kt` | Mapa ilustrado con pines (visitado / por visitar), filtros, agregar lugar tocando el mapa |
| Recuerdos | `ui/memories/MemoriesScreen.kt` | Línea de tiempo por año; agrega foto, video, audio o nota |
| Collage | `ui/collage/CollageScreen.kt` | Elige hasta 6 fotos y arma un collage con dedicatoria |
| Rincón | `ui/corner/CornerScreen.kt` | Notas, poemas, canciones dedicadas, ideas por hacer y notas de voz |
| Nosotros | `ui/us/UsScreen.kt` | Portada, días juntos, fecha, apodos, avisos y vinculación |

- Colores y tipografías: `ui/theme/` (Ciruela #5E2750, Crema #F7F1EA, Oro rosa #E3B19C, Arcilla #C47A62, Tinta #2B1626).
- Datos: `data/` — se guardan en el teléfono (SharedPreferences). Contenido de ejemplo en `SampleData.kt`.

## Pendiente
- **Satoshi**: el diseño usa Satoshi (Fontshare). Mientras tanto se usa Manrope. Para cambiarla, reemplaza
  `app/src/main/res/font/apt_sans_regular|medium|semibold|bold.ttf` con los archivos de Satoshi, mismos nombres.
- **Sincronizar con la pareja**: hoy la vinculación y el contenido viven solo en el teléfono. El siguiente paso es un backend
  (por ejemplo Firebase Auth + Firestore + Storage) para que ambos vean lo mismo.
- **Mapa real**: el mapa es ilustrado. Se puede cambiar por Google Maps Compose (requiere API key) u OpenStreetMap.
