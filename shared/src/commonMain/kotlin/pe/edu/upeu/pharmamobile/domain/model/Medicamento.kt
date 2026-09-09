package pe.edu.upeu.pharmamobile.domain.model

data class Medicamento(
    val id: Int,
    val nombre: String,
    val principioActivo: String,
    val precio: Double,
    val stock: Int
)
