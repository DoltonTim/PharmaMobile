package pe.edu.upeu.pharmamobil.presentation.producto

data class ProductoUiState(
    val fase: Fase = Fase.Cargando,
    val formulario: FormularioProducto = FormularioProducto(),
    val operacion: Operacion = Operacion.Inactiva,
    val mensajeExito: String? = null,
    val editandoId: Long? = null
) {

    /** Fases excluyentes del inventario: solo una puede estar activa. */
    sealed interface Fase {
        data object Cargando : Fase
        data object SinProductos : Fase
        data class ConProductos(val productos: List<ProductoUi>) : Fase
        data class Error(val mensaje: String) : Fase
    }

    /** Estado de la operación en curso (crear, actualizar, eliminar) para no confundir con cargar lista. */
    sealed interface Operacion {
        data object Inactiva : Operacion
        data class EnCurso(val tipo: Tipo) : Operacion
        data class Fallida(val mensaje: String) : Operacion

        enum class Tipo { Crear, Actualizar, Eliminar }
    }
}

data class FormularioProducto(
    val nombre: String = "",
    val precio: String = "",
    val stock: String = "",
    val nombreError: String? = null,
    val precioError: String? = null,
    val stockError: String? = null
)
