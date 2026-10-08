package pe.edu.upeu.pharmamobil.presentation.producto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upeu.pharmamobil.domain.usecase.ListarProductosUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.ProductoInvalidoException
import pe.edu.upeu.pharmamobil.domain.usecase.RegistrarProductoUseCase
<<<<<<< Updated upstream


class ProductoViewModel(
    private val registrarProducto: RegistrarProductoUseCase,
    private val listarProductos: ListarProductosUseCase
=======
import pe.edu.upeu.pharmamobil.presentation.producto.ProductoUiState.Fase
import pe.edu.upeu.pharmamobil.presentation.producto.ProductoUiState.Operacion
import pe.edu.upeu.pharmamobil.domain.platform.Compartidor
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.domain.usecase.comoTextoParaCompartir

class ProductoViewModel(
    private val registrarProducto: RegistrarProductoUseCase,
    private val listarProductos: ListarProductosUseCase,
    private val actualizarProducto: ActualizarProductoUseCase,
    private val eliminarProducto: EliminarProductoUseCase,
    private val compartidor: Compartidor
>>>>>>> Stashed changes
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductoUiState())
    val uiState: StateFlow<ProductoUiState> = _uiState.asStateFlow()

    init {
        cargarProductos()
    }

    fun cargarProductos() {

        viewModelScope.launch {

            _uiState.update {
                it.copy(fase = ProductoUiState.Fase.Cargando)
            }

            val fase = listarProductos().fold(
                onSuccess = { productos ->
                    if (productos.isEmpty()) {
                        ProductoUiState.Fase.SinProductos
                    } else {
                        ProductoUiState.Fase.ConProductos(productos.map { it.aUi() })
                    }
                },
                onFailure = { fallo ->
                    ProductoUiState.Fase.Error(
                        fallo.message ?: "No se pudo cargar el inventario"
                    )
                }
            )

            _uiState.update {
                it.copy(fase = fase)
            }
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

    fun registrar() {

        if (_uiState.value.registrando) return

        viewModelScope.launch {

<<<<<<< Updated upstream
            _uiState.update {
                it.copy(registrando = true, mensajeExito = null)
=======
    fun registrar() = guardar()

    fun compartir(productoUi: ProductoUi) {
        val producto = Producto(
            id = productoUi.id,
            nombre = productoUi.nombre,
            precio = productoUi.precioRaw,
            stock = productoUi.stockRaw
        )
        compartidor.compartir(producto.comoTextoParaCompartir())
    }

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
>>>>>>> Stashed changes
            }

            val formulario = _uiState.value.formulario

            registrarProducto(
                nombre = formulario.nombre,
                precio = formulario.precio,
                stock = formulario.stock
            ).fold(
                onSuccess = { producto ->
                    _uiState.update {
                        it.copy(
                            registrando = false,
                            formulario = FormularioProducto(),
                            mensajeExito = "Producto \"${producto.nombre}\" registrado correctamente"
                        )
                    }
                    cargarProductos()
                },
                onFailure = { fallo ->
                    when (fallo) {

                        is ProductoInvalidoException -> _uiState.update {
                            it.copy(
                                registrando = false,
                                formulario = it.formulario.copy(
                                    nombreError = fallo.errores.nombre,
                                    precioError = fallo.errores.precio,
                                    stockError = fallo.errores.stock
                                )
                            )
                        }

                        else -> _uiState.update {
                            it.copy(
                                registrando = false,
                                fase = ProductoUiState.Fase.Error(
                                    fallo.message ?: "No se pudo registrar el producto"
                                )
                            )
                        }
                    }
                }
            )
        }
    }
}
