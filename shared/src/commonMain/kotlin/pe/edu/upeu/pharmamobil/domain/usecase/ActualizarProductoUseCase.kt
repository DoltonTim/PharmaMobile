package pe.edu.upeu.pharmamobil.domain.usecase

import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository

class ActualizarProductoUseCase(
    private val productoRepository: ProductoRepository
) {
    suspend operator fun invoke(producto: Producto): Result<Producto> = resultadoDe {
        productoRepository.actualizar(producto)
    }

    suspend operator fun invoke(
        id: Long,
        nombre: String,
        precio: String,
        stock: String
    ): Result<Producto> {
        val errores = ErroresDeProducto(
            nombre = if (nombre.isBlank()) "El nombre es obligatorio" else null,
            precio = when {
                precio.isBlank() -> "El precio es obligatorio"
                precio.toDoubleOrNull() == null -> "El precio debe ser un número válido"
                precio.toDouble() <= 0 -> "El precio debe ser mayor a 0"
                else -> null
            },
            stock = when {
                stock.isBlank() -> "El stock es obligatorio"
                stock.toIntOrNull() == null -> "El stock debe ser un número entero"
                stock.toInt() < 0 -> "El stock no puede ser negativo"
                else -> null
            }
        )

        if (errores.hayErrores) {
            return Result.failure(ProductoInvalidoException(errores))
        }

        return resultadoDe {
            productoRepository.actualizar(
                Producto(
                    id = id,
                    nombre = nombre.trim(),
                    precio = precio.toDouble(),
                    stock = stock.toInt()
                )
            )
        }
    }
}
