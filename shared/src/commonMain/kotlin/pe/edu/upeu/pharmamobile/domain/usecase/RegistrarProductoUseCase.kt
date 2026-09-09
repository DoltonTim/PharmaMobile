package pe.edu.upeu.pharmamobile.domain.usecase

import pe.edu.upeu.pharmamobile.domain.model.Producto
import pe.edu.upeu.pharmamobile.domain.repository.ProductoRepository

class RegistrarProductoUseCase(
    private val repository: ProductoRepository
) {
    suspend operator fun invoke(
        nombre: String,
        precioTexto: String,
        stockTexto: String
    ): Result<Producto> {
        val errorNombre = validarNombre(nombre)
        if (errorNombre != null) {
            return Result.failure(IllegalArgumentException(errorNombre))
        }

        val errorPrecio = validarPrecio(precioTexto)
        if (errorPrecio != null) {
            return Result.failure(IllegalArgumentException(errorPrecio))
        }

        val errorStock = validarStock(stockTexto)
        if (errorStock != null) {
            return Result.failure(IllegalArgumentException(errorStock))
        }

        val precio = precioTexto.toDouble()
        val stock = stockTexto.toInt()

        val producto = Producto(
            id = 0L,
            nombre = nombre.trim(),
            precio = precio,
            stock = stock
        )

        return repository.registrar(producto)
    }

    fun validarNombre(nombre: String): String? {
        return if (nombre.isBlank()) "El nombre es obligatorio" else null
    }

    fun validarPrecio(precioTexto: String): String? {
        val precio = precioTexto.toDoubleOrNull()
        return when {
            precioTexto.isBlank() -> "El precio es obligatorio"
            precio == null || !precio.isFinite() -> "El precio debe ser un número válido"
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
