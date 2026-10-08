package pe.edu.upeu.pharmamobil.presentation.producto

import kotlin.math.roundToLong
import pe.edu.upeu.pharmamobil.domain.model.Producto

<<<<<<< Updated upstream
=======
import pe.edu.upeu.pharmamobil.platform.formatearSoles
>>>>>>> Stashed changes

data class ProductoUi(
    val id: Long,
    val nombre: String,
    val precio: String,
    val stock: String,
    val requiereReposicion: Boolean
)

fun Producto.aUi(): ProductoUi = ProductoUi(
    id = id,
    nombre = nombre,
    precio = formatearSoles(precio),
    stock = "$stock u.",
    requiereReposicion = requiereReposicion
)
<<<<<<< Updated upstream

/** Kotlin comun no trae String.format, asi que armamos los dos decimales a mano. */
private fun Double.enSoles(): String {

    val centavos = (this * 100).roundToLong()

    val enteros = centavos / 100
    val decimales = (centavos % 100).toString().padStart(2, '0')

    return "S/ $enteros.$decimales"
}
=======
>>>>>>> Stashed changes
