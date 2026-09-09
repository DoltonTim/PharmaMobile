package pe.edu.upeu.pharmamobile.domain

import kotlinx.coroutines.runBlocking
import pe.edu.upeu.pharmamobile.data.repository.ProductoRepositorioEnMemoria
import pe.edu.upeu.pharmamobile.domain.model.DetallePedido
import pe.edu.upeu.pharmamobile.domain.model.Producto
import pe.edu.upeu.pharmamobile.domain.usecase.RegistrarProductoUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProductoDomainTest {

    @Test
    fun productoCalculaRequiereReposicionCorrectamente() {
        val prodConBajoStock = Producto(id = 1L, nombre = "Ibuprofeno", precio = 5.0, stock = 3)
        val prodConStockSuficiente = Producto(id = 2L, nombre = "Paracetamol", precio = 4.0, stock = 10)
        val prodConStockLimite = Producto(id = 3L, nombre = "Amoxicilina", precio = 12.0, stock = 5)

        assertTrue(prodConBajoStock.requiereReposicion)
        assertTrue(prodConStockLimite.requiereReposicion)
        assertFalse(prodConStockSuficiente.requiereReposicion)
    }

    @Test
    fun detallePedidoFallaSiCantidadEsCeroOMenor() {
        val producto = Producto(id = 1L, nombre = "Paracetamol", precio = 5.0, stock = 10)

        assertFailsWith<IllegalArgumentException> {
            DetallePedido(producto = producto, cantidad = 0)
        }

        assertFailsWith<IllegalArgumentException> {
            DetallePedido(producto = producto, cantidad = -2)
        }
    }

    @Test
    fun registrarProductoUseCaseValidaYRegistraCorrectamente() = runBlocking {
        val repo = ProductoRepositorioEnMemoria()
        val useCase = RegistrarProductoUseCase(repo)

        // Caso inválido: nombre vacío
        val resNombreInvalido = useCase(nombre = "", precioTexto = "10.0", stockTexto = "5")
        assertTrue(resNombreInvalido.isFailure)

        // Caso inválido: precio negativo o cero
        val resPrecioInvalido = useCase(nombre = "Aspirina", precioTexto = "0.0", stockTexto = "5")
        assertTrue(resPrecioInvalido.isFailure)

        // Caso inválido: stock negativo
        val resStockInvalido = useCase(nombre = "Aspirina", precioTexto = "10.0", stockTexto = "-1")
        assertTrue(resStockInvalido.isFailure)

        // Caso válido
        val resExito = useCase(nombre = "Aspirina 100mg", precioTexto = "4.50", stockTexto = "20")
        assertTrue(resExito.isSuccess)
        val producto = resExito.getOrThrow()
        assertEquals("Aspirina 100mg", producto.nombre)
        assertEquals(4.50, producto.precio)
        assertEquals(20, producto.stock)
        assertTrue(producto.id > 0)
    }
}
