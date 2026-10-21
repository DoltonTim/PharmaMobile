package pe.edu.upeu.pharmamobil.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository

/**
 * Doble del inventario para las pruebas: sin delay y capaz de fallar a
 * voluntad. Sin el, probar el camino de error del caso de uso o del ViewModel
 * era imposible, porque el repositorio en memoria nunca falla.
 */
class FakeProductoRepository(
    iniciales: MutableList<Producto> = mutableListOf()
) : ProductoRepository {

    private val productos = iniciales.toMutableList()
    private val flujo = MutableStateFlow(productos.toList())

    var fallaAlRegistrar: Throwable? = null
    var fallaAlListar: Throwable? = null

    private var siguienteId = 1L

    override fun observar(): Flow<List<Producto>> = flujo

    override suspend fun sincronizar() {
        fallaAlListar?.let { throw it }
        flujo.value = productos.toList()
    }

    override suspend fun registrar(producto: Producto): Producto {
        fallaAlRegistrar?.let { throw it }

        val guardado = producto.copy(id = siguienteId++)
        productos.add(guardado)
        flujo.value = productos.toList()
        return guardado
    }

    override suspend fun listar(): List<Producto> {
        fallaAlListar?.let { throw it }
        return productos.toList()
    }

    override suspend fun obtener(id: Long): Producto {
        return productos.first { it.id == id }
    }

    override suspend fun actualizar(producto: Producto): Producto {
        val index = productos.indexOfFirst { it.id == producto.id }
        if (index != -1) {
            productos[index] = producto
            flujo.value = productos.toList()
        }
        return producto
    }

    override suspend fun eliminar(id: Long) {
        productos.removeAll { it.id == id }
        flujo.value = productos.toList()
    }
}
