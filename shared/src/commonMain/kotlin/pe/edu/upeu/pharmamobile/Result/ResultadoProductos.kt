package pe.edu.upeu.pharmamobile.Result

import pe.edu.upeu.pharmamobile.domain.model.Producto

sealed class ResultadoProductos {
    data object cargando : ResultadoProductos()
    data class Exito(
        val productos : List <Producto>
    ): ResultadoProductos()
    data class Error(
        val mensaje: String
    ): ResultadoProductos()
}