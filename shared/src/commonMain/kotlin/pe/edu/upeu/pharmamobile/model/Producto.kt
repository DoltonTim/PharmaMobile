package pe.edu.upeu.pharmamobile.model

data class Producto (
    val id: Long,
    val nombre: String,
    val precio: Double,
    val stock: Int
    ){
    init {
        require(value = nombre.isNotBlank()){
            "El monto no puede estar vacio"
        }
        require(value = precio>0)
        {
            "El precio debde ser mayor que 0"
        }
        require(value = stock>=0){
            "E no puede ser negativo"
        }
    }
    fun verificarStock(cantidad: Int): Boolean{
        return stock >= cantidad
    }
    fun estadoDisponible(): Double{
        return precio * stock
    }
    fun disnuirStock(cantidad: Int): Producto{
        require( value = cantidad > 0){
            "la cantidad debe ser mayor que cero "
        }
        require ( value = verificarStock(cantidad)){
            "stock insuficiente"
        }
        return copy(
            stock = stock - cantidad
        )
    }
}