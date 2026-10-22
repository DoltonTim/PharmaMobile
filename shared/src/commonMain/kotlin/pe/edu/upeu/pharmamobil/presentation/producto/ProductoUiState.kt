package pe.edu.upeu.pharmamobil.presentation.producto

import pe.edu.upeu.pharmamobil.domain.model.Producto

data class ProductoUiState(
    val productos: List<Producto> = emptyList(),
    val fase: Fase = Fase.Cargando,
    val formulario: FormularioProducto = FormularioProducto(),
    val operacion: Operacion = Operacion.Inactiva,
    val sincronizando: Boolean = false,
    val avisoRed: String? = null,
    val mensajeExito: String? = null,
    val editandoId: Long? = null,
    val textoAntiguedadSincronizacion: String = "Nunca sincronizado"
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
