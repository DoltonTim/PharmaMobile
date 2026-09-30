package pe.edu.upeu.pharmamobil.presentation.producto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upeu.pharmamobil.domain.error.ErrorApi
import pe.edu.upeu.pharmamobil.domain.error.ErrorApiException
import pe.edu.upeu.pharmamobil.domain.usecase.ActualizarProductoUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.EliminarProductoUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.ListarProductosUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.ProductoInvalidoException
import pe.edu.upeu.pharmamobil.domain.usecase.RegistrarProductoUseCase
import pe.edu.upeu.pharmamobil.presentation.producto.ProductoUiState.Fase
import pe.edu.upeu.pharmamobil.presentation.producto.ProductoUiState.Operacion

class ProductoViewModel(
    private val registrarProducto: RegistrarProductoUseCase,
    private val listarProductos: ListarProductosUseCase,
    private val actualizarProducto: ActualizarProductoUseCase,
    private val eliminarProducto: EliminarProductoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductoUiState())
    val uiState: StateFlow<ProductoUiState> = _uiState.asStateFlow()

    init {
        cargarProductos()
    }

    fun cargarProductos() {
        viewModelScope.launch {
            _uiState.update { it.copy(fase = Fase.Cargando) }

            val fase = listarProductos().fold(
                onSuccess = { productos ->
                    if (productos.isEmpty()) {
                        Fase.SinProductos
                    } else {
                        Fase.ConProductos(productos.map { it.aUi() })
                    }
                },
                onFailure = { fallo ->
                    Fase.Error(mensajeDe(fallo))
                }
            )

            _uiState.update { it.copy(fase = fase) }
        }
    }

    fun onNombreChange(nombre: String) {
        _uiState.update {
            it.copy(
                formulario = it.formulario.copy(nombre = nombre, nombreError = null),
                mensajeExito = null
            )
        }
    }

    fun onPrecioChange(precio: String) {
        _uiState.update {
            it.copy(
                formulario = it.formulario.copy(precio = precio, precioError = null),
                mensajeExito = null
            )
        }
    }

    fun onStockChange(stock: String) {
        _uiState.update {
            it.copy(
                formulario = it.formulario.copy(stock = stock, stockError = null),
                mensajeExito = null
            )
        }
    }

    fun iniciarEdicion(producto: ProductoUi) {
        _uiState.update {
            it.copy(
                editandoId = producto.id,
                formulario = FormularioProducto(
                    nombre = producto.nombre,
                    precio = if (producto.precioRaw > 0.0) producto.precioRaw.toString() else "",
                    stock = producto.stockRaw.toString()
                ),
                mensajeExito = null
            )
        }
    }

    fun cancelarEdicion() {
        _uiState.update {
            it.copy(
                editandoId = null,
                formulario = FormularioProducto(),
                mensajeExito = null
            )
        }
    }

    fun guardar() {
        val estadoActual = _uiState.value
        if (estadoActual.operacion is Operacion.EnCurso) return

        val editandoId = estadoActual.editandoId
        val formulario = estadoActual.formulario

        viewModelScope.launch {
            if (editandoId == null) {
                _uiState.update {
                    it.copy(
                        operacion = Operacion.EnCurso(Operacion.Tipo.Crear),
                        mensajeExito = null
                    )
                }
                registrarProducto(
                    nombre = formulario.nombre,
                    precio = formulario.precio,
                    stock = formulario.stock
                ).fold(
                    onSuccess = { producto ->
                        _uiState.update {
                            it.copy(
                                operacion = Operacion.Inactiva,
                                formulario = FormularioProducto(),
                                mensajeExito = "Producto \"${producto.nombre}\" registrado correctamente"
                            )
                        }
                        cargarProductos()
                    },
                    onFailure = { fallo -> manejarFallo(fallo) }
                )
            } else {
                _uiState.update {
                    it.copy(
                        operacion = Operacion.EnCurso(Operacion.Tipo.Actualizar),
                        mensajeExito = null
                    )
                }
                actualizarProducto(
                    id = editandoId,
                    nombre = formulario.nombre,
                    precio = formulario.precio,
                    stock = formulario.stock
                ).fold(
                    onSuccess = { producto ->
                        _uiState.update {
                            it.copy(
                                editandoId = null,
                                operacion = Operacion.Inactiva,
                                formulario = FormularioProducto(),
                                mensajeExito = "Producto \"${producto.nombre}\" actualizado correctamente"
                            )
                        }
                        cargarProductos()
                    },
                    onFailure = { fallo -> manejarFallo(fallo) }
                )
            }
        }
    }

    fun registrar() = guardar()

    fun eliminar(id: Long) = viewModelScope.launch {
        _uiState.update {
            it.copy(operacion = Operacion.EnCurso(Operacion.Tipo.Eliminar))
        }
        eliminarProducto(id)
            .onSuccess {
                cargarProductos()
                _uiState.update {
                    it.copy(
                        operacion = Operacion.Inactiva,
                        mensajeExito = "Producto eliminado"
                    )
                }
            }
            .onFailure { fallo -> manejarFallo(fallo) }
    }

    private fun manejarFallo(fallo: Throwable) {
        when (fallo) {
            is ProductoInvalidoException -> _uiState.update {
                it.copy(
                    operacion = Operacion.Inactiva,
                    formulario = it.formulario.copy(
                        nombreError = fallo.errores.nombre,
                        precioError = fallo.errores.precio,
                        stockError = fallo.errores.stock
                    )
                )
            }
            is ErrorApiException -> when (val error = fallo.error) {
                is ErrorApi.Validacion -> _uiState.update {
                    it.copy(
                        operacion = Operacion.Inactiva,
                        formulario = it.formulario.copy(
                            nombreError = error.porCampo["nombre"],
                            precioError = error.porCampo["precio"],
                            stockError = error.porCampo["stock"]
                        )
                    )
                }
                else -> _uiState.update {
                    it.copy(operacion = Operacion.Fallida(mensajeDeError(error)))
                }
            }
            else -> _uiState.update {
                it.copy(operacion = Operacion.Fallida(fallo.message ?: "Ocurrió un error inesperado"))
            }
        }
    }

    private fun mensajeDe(fallo: Throwable): String {
        return if (fallo is ErrorApiException) {
            mensajeDeError(fallo.error)
        } else {
            fallo.message ?: "No se pudo cargar el inventario"
        }
    }

    private fun mensajeDeError(error: ErrorApi): String = when (error) {
        is ErrorApi.Validacion -> "Error en los datos enviados"
        is ErrorApi.NoEncontrado -> "Recurso no encontrado"
        is ErrorApi.Conflicto -> error.mensaje
        is ErrorApi.Servidor -> "Error interno del servidor"
        is ErrorApi.SinConexion -> "Sin conexión al servidor (verifica el backend)"
        is ErrorApi.TiempoAgotado -> "Tiempo de espera agotado"
    }
}
