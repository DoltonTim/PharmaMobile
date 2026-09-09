package pe.edu.upeu.pharmamobile.data.repository

import kotlinx.coroutines.delay
import pe.edu.upeu.pharmamobile.domain.model.Producto
import pe.edu.upeu.pharmamobile.domain.repository.ProductoRepository

class ProductoRepositorioEnMemoria : ProductoRepository {

    // Lista simulada de productos en memoria
    private val productos = mutableListOf(
        Producto(id = 1L, nombre = "Paracetamol 500mg", precio = 5.0, stock = 15),
        Producto(id = 2L, nombre = "Amoxicilina 500mg", precio = 12.0, stock = 0),
        Producto(id = 3L, nombre = "Ibuprofeno 400mg", precio = 8.0, stock = 3)
    )

    override suspend fun listar(): List<Producto> {
        delay(500)
        return productos.toList()
    }

    override suspend fun registrar(producto: Producto): Result<Producto> {
        delay(500)
        return try {
            val nuevoId = (productos.maxOfOrNull { it.id } ?: 0L) + 1L
            val productoConId = producto.copy(id = nuevoId)
            productos.add(0, productoConId)
            Result.success(productoConId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun obtenerProductosDisponibles(): List<Producto> {
        return productos.filter { it.stock > 0 }
    }

    fun obtenerNombresDeProductos(): List<String> {
        return productos.map { it.nombre }
    }

    fun buscarProductoPorId(idBuscado: Long): Producto? {
        return productos.find { it.id == idBuscado }
    }
}
