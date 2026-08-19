package pe.edu.upeu.pharmamobile.model

data class Pedido (
    val id:Long,
    val cliente:Cliente,
    val detalles: List<DetallePedido>,
    val estado: EstadoPedido
)
