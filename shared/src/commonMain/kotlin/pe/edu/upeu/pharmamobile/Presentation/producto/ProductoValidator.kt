package pe.edu.upeu.pharmamobile.presentation.producto

object ProductoValidator {

    fun validarNombre(nombre: String): String? {
        return if (nombre.isBlank()) "El nombre es obligatorio" else null
    }

    fun validarPrecio(precioTexto: String): String? {
        val precio = precioTexto.toDoubleOrNull()
        return when {
            precioTexto.isBlank() -> "El precio es obligatorio"
            precio == null -> "El precio debe ser un número válido"
            precio <= 0.0 -> "El precio debe ser mayor a 0"
            else -> null
        }
    }

    fun validarStock(stockTexto: String): String? {
        val stock = stockTexto.toIntOrNull()
        return when {
            stockTexto.isBlank() -> "El stock es obligatorio"
            stock == null -> "El stock debe ser un número entero"
            stock < 0 -> "El stock no puede ser negativo"
            else -> null
        }
    }
}
