package pe.edu.upeu.pharmamobile.presentation.producto

import pe.edu.upeu.pharmamobile.domain.model.Producto

sealed interface ProductoFase {
    data object Cargando : ProductoFase
    data object SinProductos : ProductoFase
    data class ConProductos(val productos: List<Producto>) : ProductoFase
    data class Error(val mensaje: String) : ProductoFase
}

data class FormularioProductoState(
    val nombre: String = "",
    val precio: String = "",
    val stock: String = "",
    val errorNombre: String? = null,
    val errorPrecio: String? = null,
    val errorStock: String? = null,
    val estaEnviando: Boolean = false,
    val mensajeResultado: String = "",
    val esExito: Boolean = false
)

data class ProductoUiState(
    val fase: ProductoFase = ProductoFase.Cargando,
    val formulario: FormularioProductoState = FormularioProductoState(),
    val tabSeleccionada: Int = 0
)
