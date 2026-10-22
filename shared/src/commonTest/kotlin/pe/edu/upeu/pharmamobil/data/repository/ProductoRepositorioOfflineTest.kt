package pe.edu.upeu.pharmamobil.data.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import pe.edu.upeu.pharmamobil.data.local.ProductoLocal
import pe.edu.upeu.pharmamobil.data.remote.ProductoApi
import pe.edu.upeu.pharmamobil.db.PharmaDb
import pe.edu.upeu.pharmamobil.domain.model.Producto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProductoRepositorioOfflineTest {

    // Patrón en memoria requerido por la guía
    private fun baseEnMemoria(): PharmaDb {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        PharmaDb.Schema.create(driver)
        return PharmaDb(driver)
    }

    private fun crearMockApi(
        respuestaListar: String = """{"contenido": [], "pagina": 0, "tamanio": 20, "totalElementos": 0, "totalPaginas": 1, "ultima": true}""",
        respuestaRegistrar: String = """{"id": 99, "nombre": "Guardado", "precio": 10.0, "stock": 5, "estado": true, "categoriaId": 1, "categoriaNombre": "General"}""",
        estado: HttpStatusCode = HttpStatusCode.OK
    ): ProductoApi {
        val motor = MockEngine { peticion ->
            if (estado != HttpStatusCode.OK) {
                respond("{}", estado)
            } else if (peticion.method == HttpMethod.Post) {
                respond(
                    content = respuestaRegistrar,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            } else {
                respond(
                    content = respuestaListar,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            }
        }
        val cliente = HttpClient(motor) {
            install(ContentNegotiation) {
                json(Json { 
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
        return ProductoApi(cliente)
    }

    // 1. Sincronización con red
    @Test
    fun prueba1_sincronizacionConRed() = runTest {
        val db = baseEnMemoria()
        val local = ProductoLocal(db)
        val jsonServidor = """
            {
                "contenido": [
                    {"id": 10, "nombre": "Amoxicilina 500mg", "precio": 12.50, "stock": 100, "estado": true, "categoriaId": 1, "categoriaNombre": "Antibioticos"},
                    {"id": 11, "nombre": "Ibuprofeno 400mg", "precio": 5.00, "stock": 50, "estado": true, "categoriaId": 1, "categoriaNombre": "Analgesicos"}
                ],
                "pagina": 0, "tamanio": 20, "totalElementos": 2, "totalPaginas": 1, "ultima": true
            }
        """.trimIndent()

        val api = crearMockApi(respuestaListar = jsonServidor)
        val repo = ProductoRepositorioOffline(local, api)

        repo.sincronizar()

        val lista = repo.observar().first()
        assertEquals(2, lista.size)
        assertEquals("Amoxicilina 500mg", lista[0].nombre)
    }

    // 2. Registro sin red (Prueba clave offline-first)
    @Test
    fun prueba2_registroSinRed() = runTest {
        val db = baseEnMemoria()
        val local = ProductoLocal(db)
        val api = crearMockApi(estado = HttpStatusCode.ServiceUnavailable)
        val repo = ProductoRepositorioOffline(local, api)

        val nuevo = Producto(0, "Paracetamol 500mg", 3.50, 20)
        val guardado = repo.registrar(nuevo)

        // Comprueba que tiene identificador local negativo (marca de pendiente)
        assertTrue(guardado.id < 0)
        assertEquals(1, local.pendientes().size)

        // El Flow lo emite de inmediato
        val lista = repo.observar().first()
        assertEquals(1, lista.size)
        assertEquals("Paracetamol 500mg", lista.first().nombre)
    }

    // 3. Envío al reconectar
    @Test
    fun prueba3_envioAlReconectar() = runTest {
        val db = baseEnMemoria()
        val local = ProductoLocal(db)

        val nuevo = Producto(0, "Omeprazol 20mg", 8.00, 15)
        local.guardarPendiente(nuevo)
        assertEquals(1, local.pendientes().size)

        val jsonExito = """
            {"id": 25, "nombre": "Omeprazol 20mg", "precio": 8.00, "stock": 15, "estado": true, "categoriaId": 1, "categoriaNombre": "General"}
        """.trimIndent()
        val api = crearMockApi(
            respuestaRegistrar = jsonExito,
            respuestaListar = """{"contenido": [{"id": 25, "nombre": "Omeprazol 20mg", "precio": 8.00, "stock": 15, "estado": true, "categoriaId": 1, "categoriaNombre": "General"}], "pagina": 0, "tamanio": 20, "totalElementos": 1, "totalPaginas": 1, "ultima": true}"""
        )
        val repo = ProductoRepositorioOffline(local, api)

        repo.sincronizar()

        // Deja de estar marcado como pendiente
        assertEquals(0, local.pendientes().size)
    }

    // 4. La bajada no borra lo pendiente (Prueba clave offline-first)
    @Test
    fun prueba4_laBajadaNoBorraLoPendiente() = runTest {
        val db = baseEnMemoria()
        val local = ProductoLocal(db)

        // Registramos un pendiente local
        local.guardarPendiente(Producto(0, "Local Pendiente", 2.00, 10))

        val jsonServidor = """
            {
                "contenido": [
                    {"id": 50, "nombre": "Remoto 1", "precio": 15.00, "stock": 30, "estado": true, "categoriaId": 1, "categoriaNombre": "General"}
                ],
                "pagina": 0, "tamanio": 20, "totalElementos": 1, "totalPaginas": 1, "ultima": true
            }
        """.trimIndent()
        val api = crearMockApi(respuestaListar = jsonServidor)
        val repo = ProductoRepositorioOffline(local, api)

        // Al bajar datos del servidor
        local.reemplazarSincronizados(api.listar())

        // El local pendiente sigue existiendo
        val pendientes = local.pendientes()
        assertEquals(1, pendientes.size)
        assertEquals("Local Pendiente", pendientes.first().nombre)
    }

    // 5. Identificadores sin colisión
    @Test
    fun prueba5_identificadoresSinColision() = runTest {
        val db = baseEnMemoria()
        val local = ProductoLocal(db)

        val localPendiente = local.guardarPendiente(Producto(0, "Local", 1.0, 5))
        local.reemplazarSincronizados(listOf(Producto(1, "Servidor Id 1", 10.0, 50)))

        val lista = local.observar().first()
        assertEquals(2, lista.size)
        assertTrue(lista.any { it.id == localPendiente.id })
        assertTrue(lista.any { it.id == 1L })
    }

    // 6. Sincronizar dos veces no duplica
    @Test
    fun prueba6_sincronizarDosVecesNoDuplica() = runTest {
        val db = baseEnMemoria()
        val local = ProductoLocal(db)
        val jsonServidor = """
            {
                "contenido": [
                    {"id": 1, "nombre": "Aspirina", "precio": 4.0, "stock": 20, "estado": true, "categoriaId": 1, "categoriaNombre": "Analgesicos"}
                ],
                "pagina": 0, "tamanio": 20, "totalElementos": 1, "totalPaginas": 1, "ultima": true
            }
        """.trimIndent()
        val api = crearMockApi(respuestaListar = jsonServidor)
        val repo = ProductoRepositorioOffline(local, api)

        repo.sincronizar()
        repo.sincronizar()

        val lista = repo.observar().first()
        assertEquals(1, lista.size)
    }
}
