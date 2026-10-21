package pe.edu.upeu.pharmamobil.data.repository

import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.pharmamobil.data.local.ProductoLocal
import pe.edu.upeu.pharmamobil.data.remote.ProductoApi
import pe.edu.upeu.pharmamobil.domain.error.ErrorApi
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository

class ProductoRepositorioOffline(
    private val local: ProductoLocal,
    private val remoto: ProductoApi
) : ProductoRepository {

    override fun observar(): Flow<List<Producto>> = local.observar()

    override suspend fun registrar(producto: Producto): Producto {
        val guardado = local.guardarPendiente(producto)
        intentar { sincronizar() }
        return guardado
    }

    override suspend fun sincronizar() {
        subirPendientes()
        local.reemplazarSincronizados(remoto.listar())
    }

    private suspend fun subirPendientes() {
        local.pendientes().forEach { pendiente ->
            try {
                remoto.registrar(pendiente)
                local.marcarSincronizado(pendiente)
            } catch (_: ErrorApi.Conflicto) {
                local.marcarSincronizado(pendiente)
            }
        }
    }

    /** Un fallo de red no debe tumbar la operación local. */
    private suspend fun intentar(bloque: suspend () -> Unit) {
        try {
            bloque()
        } catch (sinRed: ErrorApi.SinConexion) {
            // Queda pendiente; se reintentará al sincronizar.
        }
    }
}
