package pe.edu.upeu.pharmamobile.presentation.producto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upeu.pharmamobile.domain.repository.ProductoRepository
import pe.edu.upeu.pharmamobile.domain.usecase.RegistrarProductoUseCase

class ProductoViewModel(
    private val registrarProductoUseCase: RegistrarProductoUseCase,
    private val productoRepository: ProductoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductoUiState())
    val uiState: StateFlow<ProductoUiState> = _uiState.asStateFlow()

    init {
        cargarProductos()
    }

    fun cargarProductos() {
        viewModelScope.launch {
            _uiState.update { it.copy(fase = ProductoFase.Cargando) }
            try {
                val productos = productoRepository.listar()
                _uiState.update {
                    if (productos.isEmpty()) {
                        it.copy(fase = ProductoFase.SinProductos)
                    } else {
                        it.copy(fase = ProductoFase.ConProductos(productos))
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(fase = ProductoFase.Error(e.message ?: "Error al cargar productos"))
                }
            }
        }
    }

    fun onNombreChange(nombre: String) {
        val error = registrarProductoUseCase.validarNombre(nombre)
        _uiState.update {
            it.copy(
                formulario = it.formulario.copy(
                    nombre = nombre,
                    errorNombre = error
                )
            )
        }
    }

    fun onPrecioChange(precio: String) {
        val error = registrarProductoUseCase.validarPrecio(precio)
        _uiState.update {
            it.copy(
                formulario = it.formulario.copy(
                    precio = precio,
                    errorPrecio = error
                )
            )
        }
    }

    fun onStockChange(stock: String) {
        val error = registrarProductoUseCase.validarStock(stock)
        _uiState.update {
            it.copy(
                formulario = it.formulario.copy(
                    stock = stock,
                    errorStock = error
                )
            )
        }
    }

    fun onTabSeleccionada(index: Int) {
        _uiState.update { it.copy(tabSeleccionada = index) }
    }

    fun registrarProducto() {
        val form = _uiState.value.formulario

        val errorNombre = registrarProductoUseCase.validarNombre(form.nombre)
        val errorPrecio = registrarProductoUseCase.validarPrecio(form.precio)
        val errorStock = registrarProductoUseCase.validarStock(form.stock)

        if (errorNombre != null || errorPrecio != null || errorStock != null) {
            _uiState.update {
                it.copy(
                    formulario = it.formulario.copy(
                        errorNombre = errorNombre,
                        errorPrecio = errorPrecio,
                        errorStock = errorStock,
                        mensajeResultado = "",
                        esExito = false
                    )
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(formulario = it.formulario.copy(estaEnviando = true))
            }

            val resultado = registrarProductoUseCase(
                nombre = form.nombre,
                precioTexto = form.precio,
                stockTexto = form.stock
            )

            resultado.fold(
                onSuccess = { productoRegistrado ->
                    val productosActualizados = productoRepository.listar()
                    _uiState.update {
                        it.copy(
                            fase = if (productosActualizados.isEmpty()) {
                                ProductoFase.SinProductos
                            } else {
                                ProductoFase.ConProductos(productosActualizados)
                            },
                            formulario = FormularioProductoState(
                                mensajeResultado = "✅ Producto '${productoRegistrado.nombre}' agregado con éxito.",
                                esExito = true
                            )
                        )
                    }
                },
                onFailure = { excepcion ->
                    _uiState.update {
                        it.copy(
                            formulario = it.formulario.copy(
                                estaEnviando = false,
                                mensajeResultado = "❌ Error: ${excepcion.message}",
                                esExito = false
                            )
                        )
                    }
                }
            )
        }
    }
}
