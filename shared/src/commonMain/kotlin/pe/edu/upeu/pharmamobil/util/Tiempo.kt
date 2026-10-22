package pe.edu.upeu.pharmamobil.util

expect fun tiempoActualMillis(): Long

fun formatearAntiguedad(timestampMillis: Long?, ahora: Long = tiempoActualMillis()): String {
    if (timestampMillis == null || timestampMillis <= 0L) {
        return "Nunca sincronizado"
    }
    val diferenciaMillis = ahora - timestampMillis
    val minutos = diferenciaMillis / (60 * 1000)
    val horas = minutos / 60
    val dias = horas / 24

    return when {
        dias > 0 -> if (dias == 1L) "Actualizado hace 1 día" else "Actualizado hace $dias días"
        horas > 0 -> if (horas == 1L) "Actualizado hace 1 hora" else "Actualizado hace $horas horas"
        minutos > 0 -> if (minutos == 1L) "Actualizado hace 1 minuto" else "Actualizado hace $minutos minutos"
        else -> "Actualizado hace un momento"
    }
}
