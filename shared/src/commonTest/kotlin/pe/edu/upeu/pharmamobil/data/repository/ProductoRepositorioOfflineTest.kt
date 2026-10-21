package pe.edu.upeu.pharmamobil.data.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import pe.edu.upeu.pharmamobil.data.local.ProductoLocal
import pe.edu.upeu.pharmamobil.data.remote.ProductoApi
import pe.edu.upeu.pharmamobil.db.PharmaDb
import pe.edu.upeu.pharmamobil.domain.error.ErrorApi
import pe.edu.upeu.pharmamobil.domain.model.Producto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProductoRepositorioOfflineTest {

    private fun baseEnMemoria(): PharmaDb {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        PharmaDb.Schema.create(driver)
        return PharmaDb(driver)
    }

    private fun repositorioCon(api: ProductoApi): ProductoRepositorioOffline =
        ProductoRepositorioOffline(
            local = ProductoLocal(baseEnMemoria()),
            remoto = api
        )

    private fun apiQueFalla(): ProductoApi = object : ProductoApi(HttpClient()) {
        override suspend fun listar(): List<Producto> {
            throw ErrorApi.SinConexion
        }

        override suspend fun registrar(producto: Producto): Producto {
            throw ErrorApi.SinConexion
        }
    }

    private fun apiEnMemoria(
        iniciales: List<Producto> = emptyList(),
        eventos: MutableList<String> = mutableListOf()
    ): ProductoApi = object : ProductoApi(HttpClient()) {
        private val servidor = iniciales.toMutableList()
        private var siguienteIdServidor = (servidor.maxOfOrNull { it.id } ?: 0L) + 1L

        override suspend fun listar(): List<Producto> {
            eventos.add("listar")
            return servidor.toList()
        }

        override suspend fun registrar(producto: Producto): Producto {
            eventos.add("registrar:${producto.nombre}")
            val creado = producto.copy(id = siguienteIdServidor++)
            servidor.add(creado)
            return creado
        }
    }

    @Test
    fun `sin red el producto queda pendiente y la pantalla lo ve`() =
        runTest {
            val repo = repositorioCon(apiQueFalla())

            repo.registrar(Producto(0, "Ibuprofeno", 4.5, 20))

            val lista = repo.observar().first()
            assertEquals(1, lista.size)
            assertEquals("Ibuprofeno", lista.single().nombre)
            assertTrue(lista.single().id < 0, "El id local sin red debe ser negativo")
        }

    @Test
    fun `al recuperar la red sube lo pendiente antes de bajar y asigna id positivo`() =
        runTest {
            val base = baseEnMemoria()
            val local = ProductoLocal(base)
            val eventos = mutableListOf<String>()

            // 1. Sin red: el usuario registra un producto offline
            val repoOffline = ProductoRepositorioOffline(local, apiQueFalla())
            repoOffline.registrar(Producto(0, "Paracetamol 500mg", 2.5, 30))
            assertEquals(1, local.pendientes().size)

            // 2. Vuelve la red: se sincroniza contra el servidor
            val repoOnline = ProductoRepositorioOffline(
                local = local,
                remoto = apiEnMemoria(
                    iniciales = listOf(Producto(10L, "Amoxicilina", 8.0, 15)),
                    eventos = eventos
                )
            )
            repoOnline.sincronizar()

            // Verifica orden: primero subir (registrar), después bajar (listar)
            assertEquals(listOf("registrar:Paracetamol 500mg", "listar"), eventos)

            // Verifica que ya no quedan pendientes y todos tienen id positivo del servidor
            assertTrue(local.pendientes().isEmpty())
            val finales = repoOnline.observar().first()
            assertEquals(2, finales.size)
            assertTrue(finales.all { it.id > 0 })
        }
}
